package org.ctor.dev.llrps2.coordinator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.GameMessage;
import org.ctor.dev.llrps2.message.RoundMapper;
import org.ctor.dev.llrps2.message.RoundMediationStatusMessage;
import org.ctor.dev.llrps2.message.RoundMessage;
import org.ctor.dev.llrps2.model.Agent;
import org.ctor.dev.llrps2.model.AgentPair;
import org.ctor.dev.llrps2.model.Contest;
import org.ctor.dev.llrps2.model.DateTimeMapper;
import org.ctor.dev.llrps2.model.Game;
import org.ctor.dev.llrps2.model.Round;
import org.ctor.dev.llrps2.model.RoundRule;
import org.ctor.dev.llrps2.persistence.ContestDao;
import org.ctor.dev.llrps2.persistence.RoundDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoundManager {
    private static final Log LOG = LogFactory.getLog(RoundManager.class);

    private RoundConnector roundConnector = null;

    private ContestDao contestDao = null;

    private RoundDao roundDao = null;

    private int roundCounter = 0;

    // must be synchronized with #persistRound
    public synchronized void requestRoundMediations(String contestName,
            int rounds, RoundRule rule) {
        final Contest contest = contestDao.findByName(contestName);
        if (contest == null) {
            throw new IllegalArgumentException("contest not found: "
                    + contestName);
        }
        contest.incrementMediationCount();
        final List<Agent> contestants = contest.getContestants();
        int count = 0;
        for (int idx = 0; idx < rounds; ++idx) {
            for (int i = 0; i < contestants.size(); ++i) {
                for (int j = i + 1; j < contestants.size(); ++j) {
                    final Agent first = contestants.get(i);
                    final Agent second = contestants.get(j);
                    final List<Round> matchups = contestDao.findByMatchup(
                            contest, first, second);
                    if (matchups.size() >= rounds) {
                        LOG.info(String.format(
                                "no more round mediation needed "
                                        + "for '%s' and '%s'", first.getName(),
                                second.getName()));
                        continue;
                    }
                    final AgentPair pair = createMatchup(matchups.size(),
                            contestants.get(i), contestants.get(j));
                    requestRoundMediation(contest, pair.getFirst(), pair
                            .getSecond(), rule);
                    count += 1;
                }
            }
        }
        LOG.info(String.format("requested %d round mediations", count));
        roundConnector.requestStartRoundMediation(contestName);
        contest.start();
        LOG.info(String.format("%s started", contestName));
    }

    public synchronized void persistRound(RoundMessage roundMessage) {
        LOG.debug("persisting round result: " + roundMessage);
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
        roundDao.save(round);
        roundDao.flush();
    }

    public void removeRound(Round round) {
        LOG.info("removed round: " + round);
        roundDao.remove(round);
        roundDao.flush();
    }

    void notifyRoundMediationStatus(RoundMediationStatusMessage status) {
        LOG.info("+-- round status --+");
        LOG
                .info(String.format("| waiting:  %06d |", status
                        .getWaitingRounds()));
        LOG.info(String
                .format("| done:     %06d |", status.getMediatedRounds()));
        LOG.info("+------------------+");
    }

    private void requestRoundMediation(Contest contest, Agent left,
            Agent right, RoundRule rule) {
        final String roundName = String.format("%s_%s_R%d", contest.getName(),
                contest.getMediationCount(), nextRoundCount());

        final Round round = Round.create(contest, roundName, left, right, rule);
        roundDao.save(round);
        roundDao.flush();
        final RoundMessage roundMessage = RoundMapper.modelToMessage(round);
        LOG.info("sending round mediation request: " + roundMessage);
        roundConnector.requestRoundMediation(roundMessage);
    }

    private int nextRoundCount() {
        return ++roundCounter;
    }

    private AgentPair createMatchup(int idx, Agent left, Agent right) {
        if ((idx % 2) == 0) {
            return AgentPair.create(left, right);
        } else {
            return AgentPair.create(right, left);
        }
    }

    public void setRoundConnector(RoundConnector roundConnector) {
        this.roundConnector = roundConnector;
    }

    public RoundConnector getRoundConnector() {
        return roundConnector;
    }

    public void setContestDao(ContestDao contestDao) {
        this.contestDao = contestDao;
    }

    public ContestDao getContestDao() {
        return contestDao;
    }

    public void setRoundDao(RoundDao roundDao) {
        this.roundDao = roundDao;
    }

    public RoundDao getRoundDao() {
        return roundDao;
    }
}
