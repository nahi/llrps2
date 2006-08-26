package org.ctor.dev.llrps2.mediator;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.GameMessage;
import org.ctor.dev.llrps2.message.RoundMessage;
import org.ctor.dev.llrps2.model.Move;
import org.ctor.dev.llrps2.session.GameRuleMapper;
import org.ctor.dev.llrps2.session.MoveMapper;
import org.ctor.dev.llrps2.session.Rps;
import org.ctor.dev.llrps2.session.RpsSessionException;

public class MediationRoundHandler extends RoundHandler {
    private static final Log LOG = LogFactory
            .getLog(MediationRoundHandler.class);

    private final SessionHandler left;

    private final SessionHandler right;

    private boolean leftReady = false;

    private boolean rightReady = false;

    static MediationRoundHandler create(
            RoundMediationManager roundMediationManager, RoundMessage round,
            SessionHandler left, SessionHandler right)
            throws RpsSessionException {
        Validate.notNull(roundMediationManager);
        Validate.notNull(round);
        Validate.notNull(left);
        Validate.notNull(right);
        Validate.isTrue(left.isReadyForRoundStart());
        Validate.isTrue(right.isReadyForRoundStart());
        final MediationRoundHandler handler = new MediationRoundHandler(
                roundMediationManager, round, left, right);
        return handler;
    }

    private MediationRoundHandler(RoundMediationManager roundMediationManager,
            RoundMessage round, SessionHandler left, SessionHandler right)
            throws RpsSessionException {
        super(roundMediationManager, round);
        this.left = left;
        this.right = right;
        final String roundId = getRound().getRoundId();
        final String iteration = String.valueOf(getRound().getRule()
                .getGameCount());
        final String ruleId = GameRuleMapper.modelToSession(getRound()
                .getRule().getGameRule());
        getRound().getLeft().setCname(left.getAgentName());
        getRound().getRight().setCname(right.getAgentName());
        left.setRoundHandler(this);
        right.setRoundHandler(this);
        try {
            left.sendRoundReady(roundId, iteration, ruleId);
            right.sendRoundReady(roundId, iteration, ruleId);
        } catch (RpsSessionException rse) {
            left.setRoundHandler(null);
            right.setRoundHandler(null);
            throw rse;
        }
    }

    @Override
    void notifyGameReady(SessionHandler session) throws RpsSessionException {
        if (isLeft(session)) {
            leftReady = true;
        } else {
            rightReady = true;
        }
        if (leftReady && rightReady) {
            newGame();
        }
    }

    @Override
    void notifyMove(SessionHandler session, Rps sessionMove)
            throws RpsSessionException {
        final GameMessage game = getRound().getGames().get(gameNumber - 1);
        final Move move = MoveMapper.sessionToModel(sessionMove);
        if (isLeft(session)) {
            game.setLeftMove(move);
        } else {
            game.setRightMove(move);
        }
        if (game.isCompleted()) {
            completeGame();
        }
    }

    @Override
    void notifySurrender(SessionHandler session) {
        final boolean isLeft = isLeft(session);
        if (gameNumber > 0) {
            final GameMessage game = getRound().getGames().get(gameNumber - 1);
            if (isLeft) {
                game.setLeftMove(Move.Surrender);
                if (game.getRightMove() == null) {
                    game.setRightMove(Move.NotAMove);
                }
            } else {
                game.setRightMove(Move.Surrender);
                if (game.getLeftMove() == null) {
                    game.setLeftMove(Move.NotAMove);
                }
            }
        }
        while (gameNumber < getIteration()) {
            final GameMessage newgame = GameMessage.create(++gameNumber);
            getRound().addGame(newgame);
            if (isLeft) {
                newgame.setLeftMove(Move.Surrender);
                newgame.setRightMove(Move.NotAMove);
            } else {
                newgame.setLeftMove(Move.NotAMove);
                newgame.setRightMove(Move.Surrender);
            }
        }
        notifyRoundResult();
        // close opposite side, too. XXX Too difficult for recovery...
        if (isLeft) {
            getRight().close();
        } else {
            getLeft().close();
        }
    }

    private void newGame() throws RpsSessionException {
        gameNumber += 1;
        final GameMessage game = GameMessage.create(gameNumber);
        getRound().addGame(game);
        left.sendCall();
        right.sendCall();
    }

    private void completeRound() throws RpsSessionException {
        setFinishDateTime();
        left.sendMatch();
        right.sendMatch();
        notifyRoundResult();
    }

    private void completeGame() throws RpsSessionException {
        LOG.info("game complete");
        final GameMessage game = getRound().getGames().get(gameNumber - 1);
        left.sendResultUpdate(MoveMapper.modelToSession(game.getRightMove()));
        right.sendResultUpdate(MoveMapper.modelToSession(game.getLeftMove()));
        if (expired()) {
            LOG.info("round time limit exceeded (dropped the last game)");
            getRound().getGames().remove(getRound().getGames().size() - 1);
            completeRound();
        } else if (gameNumber >= getIteration()) {
            completeRound();
        } else {
            newGame();
        }
    }

    private void notifyRoundResult() {
        setFinishDateTime();
        left.setRoundHandler(null);
        right.setRoundHandler(null);
        getRoundMediationManager().notifyRoundResult(getRound());
    }

    private int getIteration() {
        return getRound().getRule().getGameCount();
    }

    private boolean isLeft(SessionHandler session) {
        return session == left;
    }

    public SessionHandler getLeft() {
        return left;
    }

    public SessionHandler getRight() {
        return right;
    }
}