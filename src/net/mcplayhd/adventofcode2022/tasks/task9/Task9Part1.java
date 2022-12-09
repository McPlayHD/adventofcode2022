package net.mcplayhd.adventofcode2022.tasks.task9;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.HashSet;
import java.util.Set;

public class Task9Part1 extends Task {
    Loc head = new Loc(0, 0);
    Loc tail = new Loc(0, 0);

    Set<String> visited = new HashSet<>();

    @Override
    public String getResult(String[] input) {
        for (String line : input) {
            if (line.isEmpty())
                continue;
            String[] sp = line.split(" ");
            Direction direction = Direction.valueOf(sp[0]);
            int amount = Integer.parseInt(sp[1]);
            for (int n = 0; n < amount; n++) {
                moveHead(direction);
                moveTail();
                visited.add(tail.x + "," + tail.y);
            }
        }
        return Integer.toString(visited.size());
    }

    void moveHead(Direction direction) {
        head.add(direction.getVector());
    }

    void moveTail() {
        Loc delta = tail.difference(head);
        if (delta.hasOnlyOnesAndZeros())
            return;
        delta = delta.unify();
        tail.add(delta);
    }

    enum Direction {
        U(new Loc(0, -1)),
        D(new Loc(0, 1)),
        L(new Loc(-1, 0)),
        R(new Loc(1, 0));

        final Loc vector;

        Direction(Loc vector) {
            this.vector = vector;
        }

        public Loc getVector() {
            return vector.clone();
        }
    }

    static class Loc implements Cloneable {
        int x, y;

        public Loc(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Loc add(Loc other) {
            x += other.x;
            y += other.y;
            return this;
        }

        public Loc difference(Loc other) {
            return new Loc(other.x - x, other.y - y);
        }

        public boolean hasOnlyOnesAndZeros() {
            return Math.abs(x) < 2 && Math.abs(y) < 2;
        }

        public Loc unify() {
            x = x == 0 ? 0 : (int) Math.signum(x);
            y = y == 0 ? 0 : (int) Math.signum(y);
            return this;
        }

        @Override
        public Loc clone() {
            try {
                Loc clone = (Loc) super.clone();
                clone.x = x;
                clone.y = y;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
