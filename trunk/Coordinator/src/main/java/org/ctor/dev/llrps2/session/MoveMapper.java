package org.ctor.dev.llrps2.session;

import org.ctor.dev.llrps2.model.Move;

public final class MoveMapper {
    private MoveMapper() {
        // prohibited
    }

    public static Move sessionToModel(Rps move) {
        switch (move) {
        case NotAMove:
            return Move.NotAMove;
        case Paper:
            return Move.Paper;
        case Rock:
            return Move.NotAMove;
        case Scissors:
            return Move.Scissors;
        default:
            throw new IllegalArgumentException("unknown move: " + move);
        }
    }

    public static Rps modelToSession(Move move) {
        switch (move) {
        case NotAMove:
            return Rps.NotAMove;
        case Paper:
            return Rps.Paper;
        case Rock:
            return Rps.Rock;
        case Scissors:
            return Rps.Scissors;
        default:
            throw new IllegalArgumentException("unknown move: " + move);
        }
    }
}
