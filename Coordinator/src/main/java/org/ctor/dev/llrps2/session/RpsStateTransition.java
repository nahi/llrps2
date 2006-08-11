package org.ctor.dev.llrps2.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RpsStateTransition {
    private static final Log LOG = LogFactory.getLog(RpsStateTransition.class);

    private static final Map<RpsState, Set<RpsState>> map = new HashMap<RpsState, Set<RpsState>>();

    static void define(RpsState from, RpsState... candidate) {
        final Set<RpsState> set = new HashSet<RpsState>();
        for (RpsState to : candidate) {
            set.add(to);
        }
        map.put(from, set);
        LOG.info(String.format("Transition definition: %s -> [%s]", from,
                StringUtils.join(candidate, ", ")));
    }

    private RpsState state = RpsState.START;

    private final String identifier;

    public RpsStateTransition(String identifier) {
        this.identifier = identifier;
    }

    public void transition(RpsState to)
            throws IllegalRpsStateTransitionException {
        final Set<RpsState> candidate = map.get(state);
        if (candidate == null) {
            throw new IllegalStateException(String.format(
                    "[%s] undefined state: %s", identifier, state));
        }
        if (!candidate.contains(to)) {
            throw new IllegalRpsStateTransitionException(state, to);
        }
        LOG.info(String.format("[%s] %s -> %s", identifier, state, to));
        state = to;
    }
}
