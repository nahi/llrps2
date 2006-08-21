package org.ctor.dev.llrps2.coordinator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.GameMessage;
import org.ctor.dev.llrps2.message.RoundMapper;
import org.ctor.dev.llrps2.message.RoundMessage;
import org.ctor.dev.llrps2.model.Agent;
import org.ctor.dev.llrps2.model.Contest;
import org.ctor.dev.llrps2.model.DateTimeMapper;
import org.ctor.dev.llrps2.model.Game;
import org.ctor.dev.llrps2.model.Round;
import org.ctor.dev.llrps2.model.RoundRule;
import org.ctor.dev.llrps2.persistence.RoundDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoundManager {
    private static final Log LOG = LogFactory.getLog(RoundManager.class);

    private RoundConnector roundConnector = null;

    private RoundDao roundDao = null;

    private int roundCounter = 0;

    public void requestRoundMediation(Contest contest, Agent left, Agent right,
            RoundRule rule) {
        final String roundName = String.format("%s_R%d", contest.getName(),
                nextRoundCount());
        final Round round = Round.create(contest, roundName, left, right, rule);
        roundDao.save(round);
        roundDao.flush();
        final RoundMessage roundMessage = RoundMapper.modelToMessage(round);
        LOG.info("sending round mediation request: " + roundMessage);
        getRoundConnector().requestRoundMediation(roundMessage);
    }

    public void persistRound(RoundMessage roundMessage) {
        LOG.info("persisting round result");
        final Round round = roundDao.findByName(roundMessage.getRoundId());
        if (round == null) {
            LOG.error("round not found: " + roundMessage);
            return;
        }
        round.setStartDateTime(DateTimeMapper.messageToModel(roundMessage
                .getStartDateTime()));
        round.setFinishDateTime(DateTimeMapper.messageToModel(roundMessage
                .getFinishDateTime()));
        final List<Game> games = new ArrayList<Game>();
        for (GameMessage gameMessage : roundMessage.getGames()) {
            final Game game = Game.create(gameMessage.getGameNumber(), round);
            game.setLeftMove(gameMessage.getLeftMove());
            game.setRightMove(gameMessage.getRightMove());
            games.add(game);
        }
        round.count();
        roundDao.flush();
    }

    private int nextRoundCount() {
        return ++roundCounter;
    }

    public void setRoundConnector(RoundConnector roundConnector) {
        this.roundConnector = roundConnector;
    }

    public RoundConnector getRoundConnector() {
        return roundConnector;
    }

    public void setRoundDao(RoundDao roundDao) {
        this.roundDao = roundDao;
    }

    public RoundDao getRoundDao() {
        return roundDao;
    }
}
