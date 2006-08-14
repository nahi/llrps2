package org.ctor.dev.llrps2.model;

import java.util.HashSet;
import java.util.Set;

import static org.ctor.dev.llrps2.model.Move.*;

public enum GameRule {
    // general R-P-S
    Normal(Rock, Scissors, Scissors, Paper, Paper, Rock, Rock, NotAMove,
            Scissors, NotAMove, Paper, NotAMove);

    private Set<MovePair> decisionSet = new HashSet<MovePair>();

    private GameRule(Move... moves) {
        if (moves.length % 2 != 0) {
            throw new IllegalArgumentException();
        }
        for (int idx = 0; idx < moves.length / 2; ++idx) {
            decisionSet.add(new MovePair(moves[idx * 2], moves[idx * 2 + 1]));
        }
    }

    public int judge(Move left, Move right) {
        if (decisionSet.contains(new MovePair(left, right))) {
            return -1;
        } else if (decisionSet.contains(new MovePair(right, left))) {
            return 1;
        }
        return 0;
    }
}