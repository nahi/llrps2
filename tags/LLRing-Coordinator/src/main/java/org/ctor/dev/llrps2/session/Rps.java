package org.ctor.dev.llrps2.session;

public enum Rps {
    // NaM
    NotAMove("0"),
    // rock
    Rock("1"),
    // scissors
    Scissors("2"),
    // paper
    Paper("3");

    private static final Rps[] candidates = new Rps[] { Rock, Scissors, Paper };

    private final String representation;

    private Rps(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

    public static Rps parse(String representation)
            throws NoSuchRpsMoveException {
        for (Rps rps : values()) {
            if (rps.getRepresentation().equals(representation)) {
                return rps;
            }
        }
        throw new NoSuchRpsMoveException(representation);
    }

    public static Rps[] getCandidates() {
        return candidates;
    }
}