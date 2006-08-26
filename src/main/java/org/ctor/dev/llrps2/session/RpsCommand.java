package org.ctor.dev.llrps2.session;

import static org.ctor.dev.llrps2.session.RpsCommandParameter.*;

import org.apache.commons.lang.builder.ToStringBuilder;

public enum RpsCommand {
    // coordinator HELLO (only if coordinator opened).
    C_HELLO(RpsRole.COORDINATOR, "HELLO"),

    // agent HELLO (only if agent opened).
    A_HELLO(RpsRole.AGENT, "HELLO"),

    // coordinator INITIATE for a session.
    C_INITIATE(RpsRole.COORDINATOR, "INITIATE", SessionId),

    // agent INITIATE for a session
    A_INITIATE(RpsRole.AGENT, "INITIATE", SessionId, AgentName, Capacity),

    // coordinator READY for a match.
    C_READY(RpsRole.COORDINATOR, "READY", SessionId, RoundId, Iteration, RuleId),

    // agent READY for a match.
    A_READY(RpsRole.AGENT, "READY", SessionId, RoundId),

    // coordinator CALL.
    C_CALL(RpsRole.COORDINATOR, "CALL", SessionId, RoundId),

    // agent MOVE.
    A_MOVE(RpsRole.AGENT, "MOVE", SessionId, RoundId, Move),

    // coordinator RESULT update.
    C_RESULT(RpsRole.COORDINATOR, "RESULT", SessionId, RoundId,
            PreviousOppositeMove),

    // coordinator called MATCH.
    C_MATCH(RpsRole.COORDINATOR, "MATCH", SessionId, RoundId),

    // coordinator noticed CLOSE.
    C_CLOSE(RpsRole.COORDINATOR, "CLOSE", SessionId);

    private final RpsRole role;

    private final String command;

    private final RpsCommandParameter[] parameterDefinitions;

    private RpsCommand(RpsRole role, String command,
            RpsCommandParameter... parameters) {
        this.role = role;
        this.command = command;
        this.parameterDefinitions = parameters;
    }

    public RpsRole getRole() {
        return role;
    }

    public String getCommand() {
        return command;
    }

    public RpsCommandParameter[] getParameterDefinitions() {
        return parameterDefinitions;
    }

    public static RpsCommand valueOf(RpsRole targetRole, String targetCommand)
            throws NoSuchRpsCommandException {
        for (RpsCommand ele : RpsCommand.values()) {
            if (ele.getRole() == targetRole
                    && ele.getCommand().equals(targetCommand)) {
                return ele;
            }
        }
        throw new NoSuchRpsCommandException(targetRole, targetCommand);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(command).append(
                parameterDefinitions).toString();
    }
}
