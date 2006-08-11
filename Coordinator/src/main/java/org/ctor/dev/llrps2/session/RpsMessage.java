package org.ctor.dev.llrps2.session;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class RpsMessage {
    private static final Pattern commandParsePattern = Pattern
            .compile("^(\\w+)(?:\\s+(.+))?$");

    private static final Pattern parameterSplitPattern = Pattern
            .compile("\\s+");

    private final RpsCommand command;

    private final RpsCommandParameter[] definitions;

    private final String[] parameters;

    private RpsMessage(RpsCommand command, RpsCommandParameter[] definitions,
            String[] parameters) {
        assert (definitions.length == parameters.length);
        this.command = command;
        this.definitions = definitions;
        this.parameters = parameters;
    }

    public static RpsMessage create(RpsCommand command, String[] parameters) {
        return new RpsMessage(command, command.getParameterDefinitions(),
                parameters);
    }

    public static RpsMessage parse(RpsRole role, String line)
            throws RpsSessionException {
        final Matcher matcher = commandParsePattern.matcher(line);
        if (!matcher.matches()) {
            throw new IllegalRpsMessageException(line);
        }
        final String commandString = matcher.group(1);
        final RpsCommand command = RpsCommand.valueOf(role, commandString);
        final RpsCommandParameter[] definitions = command
                .getParameterDefinitions();
        final String[] parameters;
        if (matcher.group(2) == null) {
            parameters = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        else {
            parameters = parameterSplitPattern.split(matcher.group(2));
        }
        if (parameters.length != definitions.length) {
            throw new IllegalRpsMessageException(String.format(
                    "%s - illegal number of parameters: %d for %d (%s)",
                    command, parameters.length, definitions.length, line));
        }
        return new RpsMessage(command, definitions, parameters);
    }

    public RpsCommand getCommand() {
        return command;
    }

    public String getParameter(RpsCommandParameter param) {
        assert (definitions.length == parameters.length);
        final int idx = ArrayUtils.indexOf(definitions, param);
        if (idx == -1) {
            throw new IllegalArgumentException(String.format(
                    "Parameter %s not defined for this command", param));
        }
        return parameters[idx];
    }

    public String[] getParameters() {
        return parameters;
    }

    public String dump() {
        if (parameters.length > 0) {
            return String.format("%s %s", command.getCommand(), StringUtils
                    .join(parameters, ' '));
        }
        else {
            return command.getCommand();
        }
    }

    public void checkSessionId(String sessionId)
            throws IllegalRpsMessageException {
        if (!sessionId.equals(getParameter(RpsCommandParameter.SessionId))) {
            final String msg = String.format(
                    "sessionId does not match: %s for %s",
                    getParameter(RpsCommandParameter.SessionId), sessionId);
            throw new IllegalRpsMessageException(msg);
        }
    }

    public void checkRoundId(String roundId) throws IllegalRpsMessageException {
        if (!roundId.equals(getParameter(RpsCommandParameter.RoundId))) {
            final String msg = String.format(
                    "roundId does not match: %s for %s",
                    getParameter(RpsCommandParameter.RoundId), roundId);
            throw new IllegalRpsMessageException(msg);
        }
    }
}
