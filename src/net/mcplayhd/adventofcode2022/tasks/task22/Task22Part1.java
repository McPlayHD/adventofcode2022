package net.mcplayhd.adventofcode2022.tasks.task22;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task22Part1 extends Task {
    static int WIDTH = 0;
    static int HEIGHT = 0;

    static Block[][] field;
    static List<MoveInstruction> moveInstructions = new ArrayList<>();

    static Block standingOn;
    static Facing facing = Facing.RIGHT; // initial facing

    @Override
    public String getResult(String[] input) {
        for (String line : input) {
            if (line.isEmpty())
                break;
            HEIGHT++;
            WIDTH = Math.max(WIDTH, line.length());
        }
        field = new Block[HEIGHT][WIDTH];
        int y = 0;
        for (String line : input) {
            if (line.isEmpty())
                continue;
            char[] chars = line.toCharArray();
            if (chars[0] == ' ' || chars[0] == '.' || chars[0] == '#') {
                for (int x = 0; x < chars.length; x++) {
                    if (chars[x] == ' ')
                        continue;
                    field[y][x] = new Block(x, y, chars[x] == '#');
                    if (y == 0 && standingOn == null) {
                        standingOn = getBlock(x, y);
                    }
                }
                y++;
            } else {
                int numberStartIndex = 0; // we know that we are always starting with a number
                Turning turning = null;
                for (int x = 1; x < chars.length; x++) {
                    if (chars[x] == 'R' || chars[x] == 'L') {
                        int moveAmount = Integer.parseInt(line.substring(numberStartIndex, x));
                        MoveInstruction instruction = new MoveInstruction(turning, moveAmount);
                        moveInstructions.add(instruction);
                        turning = chars[x] == 'R' ? Turning.R : Turning.L;
                        numberStartIndex = x + 1;
                    }
                }
                // adding last instruction
                int moveAmount = Integer.parseInt(line.substring(numberStartIndex, chars.length));
                MoveInstruction instruction = new MoveInstruction(turning, moveAmount);
                moveInstructions.add(instruction);
            }
        }
        printBoard();
        printInstructions();
        connectPieces();
        for (MoveInstruction instruction : moveInstructions) {
            standingOn.facingWhenStoodOn = facing;
            if (instruction.turning != null) {
                // turn
                facing = facing.getRelative(instruction.turning);
            }
            // move
            for (int count = 0; count < instruction.moveAmount; count ++) {
                standingOn.facingWhenStoodOn = facing;
                Block moveTo = standingOn.getRelative(facing);
                if (moveTo.wall) {
                    break;
                }
                standingOn = moveTo;
            }
        }
        printBoard();
        int row = standingOn.vector.y + 1;
        int col = standingOn.vector.x + 1;
        int fac = facing.ordinal();
        System.out.println(row + ":" + col + ":" + fac);
        int code = 1000 * row + 4 * col + fac;
        return Integer.toString(code);
    }

    void connectPieces() {
        for (int y = 0; y < HEIGHT; y ++) {
            for (int x = 0; x < WIDTH; x++) {
                Block block = getBlock(x, y);
                if (block == null)
                    continue;
                block.connectRelatives();
            }
        }
    }

    static Block getBlock(Vector loc) {
        return getBlock(loc.x, loc.y);
    }

    static Block getBlock(int x, int y) {
        if (x < 0) {
            x = x + WIDTH;
        }
        if (x >= WIDTH) {
            x = x - WIDTH;
        }
        if (y < 0) {
            y = y + HEIGHT;
        }
        if (y >= HEIGHT) {
            y = y - HEIGHT;
        }
        return field[y][x];
    }

    void printBoard() {
        for (int y = 0; y < HEIGHT; y ++) {
            for (int x = 0; x < WIDTH; x ++) {
                Block block = getBlock(x, y);
                if (block == null) {
                    System.out.print(" ");
                } else {
                    if (block.facingWhenStoodOn != null) {
                        System.out.print(block.facingWhenStoodOn.c);
                    } else {
                        System.out.print(block.wall ? '#' : '.');
                    }
                }
            }
            System.out.println();
        }
    }

    void printInstructions() {
        for (MoveInstruction instruction : moveInstructions) {
            System.out.print(instruction.turning + ":" + instruction.moveAmount + ",");
        }
        System.out.println();
    }

    static class Block {
        Vector vector;
        boolean wall;
        Map<Facing, Block> relatives = new HashMap<>();

        Facing facingWhenStoodOn = null;

        public Block(int x, int y, boolean wall) {
            this.vector = new Vector(x, y);
            this.wall = wall;
        }

        void connectRelatives() {
            for (Facing facing : Facing.values()) {
                Vector tmp = new Vector(vector).add(facing.vector);
                while (getBlock(tmp) == null) {
                    // this will search for a block in that direction
                    tmp.add(facing.vector);
                }
                relatives.put(facing, getBlock(tmp));
            }
        }

        Block getRelative(Facing facing) {
            return relatives.get(facing);
        }
    }

    static class Vector {
        int x, y;

        public Vector(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vector(Vector other) {
            this.x = other.x;
            this.y = other.y;
        }

        public Vector add(Vector other) {
            x += other.x;
            y += other.y;
            return this;
        }
    }

    enum Facing {
        RIGHT(new Vector(1, 0), '>'),
        DOWN(new Vector(0, 1), 'v'),
        LEFT(new Vector(-1, 0), '<'),
        UP(new Vector(0, -1), '^');

        final Vector vector;
        final char c;

        Facing(Vector vector, char c) {
            this.vector = vector;
            this.c = c;
        }

        Facing getRelative(Turning turning) {
            int index = ordinal() + (turning == Turning.L ? -1 : 1);
            if (index < 0) {
                index += Facing.values().length;
            }
            if (index >= Facing.values().length) {
                index -= Facing.values().length;
            }
            return Facing.values()[index];
        }
    }

    enum Turning {
        R,
        L
    }

    static class MoveInstruction {
        Turning turning;
        int moveAmount;

        public MoveInstruction(Turning turning, int moveAmount) {
            this.turning = turning;
            this.moveAmount = moveAmount;
        }
    }
}
