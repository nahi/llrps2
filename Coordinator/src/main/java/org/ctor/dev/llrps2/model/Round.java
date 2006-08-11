package org.ctor.dev.llrps2.model;


public class Round {
    private final Agent left;

    private final Agent right;

    Round(Agent left, Agent right) {
        this.left = left;
        this.right = right;
    }

    public Agent getLeft() {
        return left;
    }

    public Agent getRight() {
        return right;
    }
}
