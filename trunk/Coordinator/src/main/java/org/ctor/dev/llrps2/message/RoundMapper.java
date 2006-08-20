package org.ctor.dev.llrps2.message;

import org.ctor.dev.llrps2.model.Round;

public final class RoundMapper {
    private RoundMapper() {
        // prohibited
    }

    public static RoundMessage modelToMessage(Round round) {
        final AgentMessage left = AgentMapper.modelToMessage(round
                .getLeftPlayer().getAgent());
        final AgentMessage right = AgentMapper.modelToMessage(round
                .getRightPlayer().getAgent());
        return RoundMessage.create(round.getName(), round.getRule(), left,
                right);
    }
}
