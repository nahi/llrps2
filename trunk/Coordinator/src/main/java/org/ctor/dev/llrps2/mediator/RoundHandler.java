package org.ctor.dev.llrps2.mediator;

import java.util.GregorianCalendar;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.GameMessage;
import org.ctor.dev.llrps2.message.RoundMessage;
import org.ctor.dev.llrps2.model.DateTimeMapper;
import org.ctor.dev.llrps2.model.Move;
import org.ctor.dev.llrps2.session.GameRuleMapper;
import org.ctor.dev.llrps2.session.MoveMapper;
import org.ctor.dev.llrps2.session.Rps;
import org.ctor.dev.llrps2.session.RpsSessionException;

public class RoundHandler {
    private static final Log LOG = LogFactory.getLog(RoundHandler.class);

    private final RoundMediationManager roundMediationManager;

    private final RoundMessage round;

    private final SessionHandler left;

    private final SessionHandler right;

    private boolean leftReady = false;

    private boolean rightReady = false;

    private int gameNumber = 0;

    static RoundHandler create(RoundMediationManager roundMediationManager,
            RoundMessage round, SessionHandler left, SessionHandler right)
            throws RpsSessionException {
        Validate.notNull(roundMediationManager);
        Validate.notNull(round);
        Validate.notNull(left);
        Validate.notNull(right);
        Validate.isTrue(left.isReadyForRoundStart());
        Validate.isTrue(right.isReadyForRoundStart());
        final RoundHandler handler = new RoundHandler(roundMediationManager,
                round, left, right);
        return handler;
    }

    private RoundHandler(RoundMediationManager roundMediationManager,
            RoundMessage round, SessionHandler left, SessionHandler right)
            throws RpsSessionException {
        this.roundMediationManager = roundMediationManager;
        this.round = round;
        this.left = left;
        this.right = right;
        final String roundId = getRound().getRoundId();
        final String iteration = String.valueOf(getRound().getRule()
                .getGameCount());
        final String ruleId = GameRuleMapper.modelToSession(getRound()
                .getRule().getGameRule());
        getRound().getLeft().setCname(left.getAgentName());
        getRound().getRight().setCname(right.getAgentName());
        getRound().setStartDateTime(DateTimeMapper.modelToMessage(now()));
        left.setRoundHandler(this);
        right.setRoundHandler(this);
        left.sendRoundReady(roundId, iteration, ruleId);
        right.sendRoundReady(roundId, iteration, ruleId);
    }

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

    void notifySurrender(SessionHandler session) {
        final boolean isLeft = isLeft(session);
        if (gameNumber > 0) {
            final GameMessage game = getRound().getGames().get(gameNumber - 1);
            if (isLeft) {
                game.setLeftMove(Move.Surrender);
                if (game.getRightMove() == null) {
                    game.setRightMove(Move.NotAMove);
                }
                try {
                    getRight().recover();
                } catch (RpsSessionException rse) {
                    LOG.info(rse.getMessage(), rse);
                    game.setRightMove(Move.Surrender);
                }
            } else {
                game.setRightMove(Move.Surrender);
                if (game.getLeftMove() == null) {
                    game.setLeftMove(Move.NotAMove);
                }
                try {
                    getLeft().recover();
                } catch (RpsSessionException rse) {
                    LOG.info(rse.getMessage(), rse);
                    game.setLeftMove(Move.Surrender);
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
        getRound().setFinishDateTime(DateTimeMapper.modelToMessage(now()));
        roundMediationManager.notifyRoundResult(getRound());
    }

    private void completeGame() throws RpsSessionException {
        LOG.info("game complete");
        final GameMessage game = getRound().getGames().get(gameNumber - 1);
        left.sendResultUpdate(MoveMapper.modelToSession(game.getRightMove()));
        right.sendResultUpdate(MoveMapper.modelToSession(game.getLeftMove()));
        if (gameNumber >= getIteration()) {
            completeRound();
        } else {
            newGame();
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
        getRound().setFinishDateTime(DateTimeMapper.modelToMessage(now()));
        left.sendMatch();
        right.sendMatch();
        roundMediationManager.notifyRoundResult(getRound());
    }

    private GregorianCalendar now() {
        return new GregorianCalendar();
    }

    private int getIteration() {
        return getRound().getRule().getGameCount();
    }

    private boolean isLeft(SessionHandler session) {
        return session == left;
    }

    public RoundMessage getRound() {
        return round;
    }

    public SessionHandler getLeft() {
        return left;
    }

    public SessionHandler getRight() {
        return right;
    }
}
