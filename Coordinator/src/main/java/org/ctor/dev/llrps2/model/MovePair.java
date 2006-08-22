package org.ctor.dev.llrps2.model;

import org.apache.commons.lang.Validate;

public class MovePair extends Pair<Move> {
    public static MovePair create(Move left, Move right) {
        Validate.notNull(left);
        Validate.notNull(right);
        return new MovePair(left, right);
    }
    
    private MovePair(Move left, Move right) {
        super(left, right);
    }
}
