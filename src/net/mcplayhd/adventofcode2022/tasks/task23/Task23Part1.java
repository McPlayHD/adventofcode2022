package net.mcplayhd.adventofcode2022.tasks.task23;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.*;

public class Task23Part1 extends Task {
    private static final int MAX_ROUNDS = 10;

    static Map<Vector, Block> blocks = new HashMap<>();
    static Set<Elf> elves = new HashSet<>();
    static Map<Vector, Integer> wantsToMoveThereCounter = new HashMap<>();
    static int round = 0;

    @Override
    public String getResult(String[] input) {
        int y = 0;
        for (String line : input) {
            if (line.isEmpty())
                continue;
            char[] chars = line.toCharArray();
            for (int x = 0; x < chars.length; x ++) {
                Vector loc = new Vector(x, y);
                Block block = new Block(loc);
                if (chars[x] == '#') {
                    Elf elf = new Elf(block);
                    elves.add(elf);
                    block.elf = elf;
                }
                blocks.put(loc, block);
            }
            y ++;
        }
        printBoard();
        for (round = 0; round < MAX_ROUNDS; round ++) {
            wantsToMoveThereCounter.clear();
            for (Elf elf : elves) {
                elf.considerMoving();
            }
            for (Elf elf : elves) {
                elf.move();
            }
            printBoard();
        }
        int empty = countEmptyGroundTiles();
        return Integer.toString(empty);
    }

    static int countEmptyGroundTiles() {
        Vector min = new Vector(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector max = new Vector(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Block block : blocks.values()) {
            if (block.elf == null) {
                continue;
            }
            min.x = Math.min(min.x, block.loc.x);
            max.x = Math.max(max.x, block.loc.x);
            min.y = Math.min(min.y, block.loc.y);
            max.y = Math.max(max.y, block.loc.y);
        }
        int count = 0;
        for (int y = min.y; y <= max.y; y ++) {
            for (int x = min.x; x <= max.x; x ++) {
                Vector loc = new Vector(x, y);
                Block block = getBlock(loc);
                if (block.elf == null) {
                    count ++;
                }
            }
        }
        return count;
    }

    static void printBoard() {
        Vector min = new Vector(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector max = new Vector(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Block block : blocks.values()) {
            if (block.elf == null) {
                continue;
            }
            min.x = Math.min(min.x, block.loc.x);
            max.x = Math.max(max.x, block.loc.x);
            min.y = Math.min(min.y, block.loc.y);
            max.y = Math.max(max.y, block.loc.y);
        }
        int elves = 0;
        int free = 0;
        for (int y = min.y; y <= max.y; y ++) {
            for (int x = min.x; x <= max.x; x ++) {
                Vector loc = new Vector(x, y);
                Block block = getBlock(loc);
                if (block.elf == null) {
                    free ++;
                    System.out.print(".");
                } else {
                    elves ++;
                    System.out.print("#");
                }
            }
            System.out.println();
        }
        System.out.println("Elves: " + elves + " - Free: " + free);
    }

    static Block getBlock(Vector loc) {
        return blocks.computeIfAbsent(loc, b -> new Block(loc));
    }

    static class Elf {
        Block block;
        Block wantsToGoTo;

        public Elf(Block block) {
            this.block = block;
        }

        void considerMoving() {
            boolean found = false;
            for (Direction direction : Direction.values()) {
                Block target = getBlock(block.loc.clone().add(direction.vector));
                if (target.elf != null) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // we do not want to move this round
                return;
            }
            for (int offset = 0; offset < MoveDirection.values().length; offset ++) {
                MoveDirection direction = MoveDirection.values()[(round + offset) % MoveDirection.values().length];
                found = true;
                for (Direction check : direction.check) {
                    Block target = getBlock(block.loc.clone().add(check.vector));
                    if (target.elf != null) {
                        found = false;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
                Block wantsToGo = getBlock(block.loc.clone().add(direction.moveDirection.vector));
                wantsToMoveThereCounter.merge(wantsToGo.loc, 1, Integer::sum);
                wantsToGoTo = wantsToGo;
                break;
            }
        }

        void move() {
            if (wantsToGoTo == null || wantsToMoveThereCounter.get(wantsToGoTo.loc) > 1) {
                wantsToGoTo = null;
                return;
            }
            block.elf = null;
            wantsToGoTo.elf = this;
            block = wantsToGoTo;
            wantsToGoTo = null;
        }
    }

    static class Block {
        Vector loc;
        Elf elf;

        public Block(Vector loc) {
            this.loc = loc;
        }
    }

    enum MoveDirection {
        NORTH(Direction.N, new Direction[] {Direction.NW, Direction.N, Direction.NE}),
        SOUTH(Direction.S, new Direction[] {Direction.SE, Direction.S, Direction.SW}),
        WEST(Direction.W, new Direction[] {Direction.SW, Direction.W, Direction.NW}),
        EAST(Direction.E, new Direction[] {Direction.NE, Direction.E, Direction.SE});

        final Direction moveDirection;
        final Direction[] check;

        MoveDirection(Direction moveDirection, Direction[] check) {
            this.moveDirection = moveDirection;
            this.check = check;
        }
    }

    enum Direction {
        N(new Vector(0, -1)),
        NE(new Vector(1, -1)),
        E(new Vector(1, 0)),
        SE(new Vector(1, 1)),
        S(new Vector(0, 1)),
        SW(new Vector(-1, 1)),
        W(new Vector(-1, 0)),
        NW(new Vector(-1, -1));

        final Vector vector;

        Direction(Vector vector) {
            this.vector = vector;
        }
    }

    static class Vector implements Cloneable {
        int x, y;

        public Vector(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vector add(Vector other) {
            this.x += other.x;
            this.y += other.y;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vector vector = (Vector) o;
            return x == vector.x && y == vector.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Vector{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        @Override
        public Vector clone() {
            try {
                return (Vector) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
