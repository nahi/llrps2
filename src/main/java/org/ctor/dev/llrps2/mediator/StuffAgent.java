package org.ctor.dev.llrps2.mediator;

import java.util.List;

public class StuffAgent {
    int strategyTypeNum;

    StuffAgent(int strategyTypeNum) {
        this.strategyTypeNum = strategyTypeNum;
    }

    public String getAgentName() {
        switch (strategyTypeNum) {
        case 0:
            return "JustRock";
        case 1:
            return "Rotate";
        case 2:
            return "Copy";
        case 3:
            return "Copy2";
        default:
            throw new IllegalStateException("unknown strategy: "
                    + strategyTypeNum);
        }
    }

    public String getCapacity() {
        return "1";
    }

    public int getMove(List<Integer> myMoveStack, List<Integer> enemyMoveStack) {
        // nakahiro> > * JustRock ... ぐーしかださない。
        // nakahiro> > * Rotate ... ぐーちょきぱーの繰り返し。
        // nakahiro> > * Copy ... 直前の相手の手を出す。
        // nakahiro> > * Copy2 ... ふたつ前の相手の手を出す。

        // デフォはグー
        Integer move = new Integer(1);
        if (strategyTypeNum == 0) {
            move = new Integer(1);
            myMoveStack.add(0, move);
        } else if (strategyTypeNum == 1) {
            if (myMoveStack.isEmpty()) {
                move = new Integer(1);
                myMoveStack.add(0, move);
            } else {
                Integer pastMoveNum = myMoveStack.get(0);
                if (pastMoveNum.intValue() == 1) {
                    move = new Integer(2);
                    myMoveStack.add(0, move);
                } else if (pastMoveNum.intValue() == 2) {
                    move = new Integer(3);
                    myMoveStack.add(0, move);
                } else if (pastMoveNum.intValue() == 3) {
                    move = new Integer(1);
                    myMoveStack.add(0, move);
                } else {
                    move = new Integer(1);
                    myMoveStack.add(0, move);
                }
            }
        } else if (strategyTypeNum == 2) {
            if (enemyMoveStack.isEmpty()) {
                move = new Integer(1);
                myMoveStack.add(0, move);
            } else {
                Integer pastEnemyMoveNum = enemyMoveStack.get(0);
                move = pastEnemyMoveNum;
                myMoveStack.add(0, move);
            }
        } else if (strategyTypeNum == 3) {
            if (enemyMoveStack.isEmpty()) {
                move = new Integer(1);
                myMoveStack.add(0, move);
            } else {
                if (myMoveStack.size() >= 2) {
                    Integer pastEnemyMoveNum = enemyMoveStack.get(1);
                    move = pastEnemyMoveNum;
                    myMoveStack.add(0, move);
                } else {
                    Integer pastEnemyMoveNum = enemyMoveStack.get(0);
                    move = pastEnemyMoveNum;
                    myMoveStack.add(0, move);
                }
            }

        } else {
            System.out.println("[Stuff Agent] Illegal strategyTypeNum => "
                    + strategyTypeNum);
        }
        return move.intValue();
    }
}
