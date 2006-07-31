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

    private final String representation;

    private Rps(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }
}