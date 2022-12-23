package net.mcplayhd.adventofcode2022.tasks.task22;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.*;

public class Task22Part2 extends Task {
    static int WIDTH = 0;
    static int HEIGHT = 0;
    static final int CUBE_SIDE = 50;

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
        printBlockSides();
        for (MoveInstruction instruction : moveInstructions) {
            standingOn.facingWhenStoodOn = facing;
            if (instruction.turning != null) {
                // turn
                facing = facing.getRelative(instruction.turning);
            }
            // move
            for (int count = 0; count < instruction.moveAmount; count++) {
                standingOn.facingWhenStoodOn = facing;
                MoveResult moveTo = standingOn.getRelative(facing);
                if (moveTo == null) {
                    throw new RuntimeException("MoveTo is null for " + standingOn.vector.x + ":" + standingOn.vector.y + " -> " + facing);
                }
                if (moveTo.target.wall) {
                    break;
                }
                standingOn = moveTo.target;
                facing = moveTo.newFacing;
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
        for (CubeSide side : CubeSide.values()) {
            side.connectInsides();
        }
        for (CubeSide side : CubeSide.values()) {
            side.connectEdges();
        }
    }

    static Block getBlock(Vector loc) {
        return getBlock(loc.x, loc.y);
    }

    static Block getBlock(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return null;
        }
        return field[y][x];
    }

    void printBoard(Block... highlighted) {
        System.out.println();
        Set<Block> highlightedBlocks = new HashSet<>(Arrays.asList(highlighted));
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Block block = getBlock(x, y);
                if (highlightedBlocks.contains(block)) {
                    System.out.print('o');
                    continue;
                }
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

    void printBlockSides(Block... highlighted) {
        System.out.println();
        Set<Block> highlightedBlocks = new HashSet<>(Arrays.asList(highlighted));
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Block block = getBlock(x, y);
                if (highlightedBlocks.contains(block)) {
                    System.out.print('o');
                    continue;
                }
                if (block == null) {
                    System.out.print(" ");
                } else {
                    if (block.cubeSide != null) {
                        System.out.print(block.cubeSide.ordinal());
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

    static class MoveResult {
        Block target;
        Facing newFacing;

        public MoveResult(Block target, Facing newFacing) {
            this.target = target;
            this.newFacing = newFacing;
        }
    }

    static class Block {
        Vector vector;
        boolean wall;
        Map<Facing, MoveResult> relatives = new HashMap<>();
        CubeSide cubeSide = null;

        Facing facingWhenStoodOn = null;

        public Block(int x, int y, boolean wall) {
            this.vector = new Vector(x, y);
            this.wall = wall;
        }

        MoveResult getRelative(Facing facing) {
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

    enum CubeSide {
        FRONT(new Vector[]{
                new Vector(50, 0),
                new Vector(100, 50)
        }),
        BACK(new Vector[]{
                new Vector(50, 100),
                new Vector(100, 150)
        }),
        LEFT(new Vector[]{
                new Vector(0, 100),
                new Vector(50, 150)
        }),
        RIGHT(new Vector[]{
                new Vector(100, 0),
                new Vector(150, 50)
        }),
        TOP(new Vector[]{
                new Vector(0, 150),
                new Vector(50, 200)
        }),
        BOTTOM(new Vector[]{
                new Vector(50, 50),
                new Vector(100, 100)
        });


        final Vector[] minMax;

        CubeSide(Vector[] minMax) {
            this.minMax = minMax;
        }

        void connectInsides() {
            for (int y = 0; y < CUBE_SIDE; y ++) {
                for (int x = 0; x < CUBE_SIDE; x ++) {
                    Vector loc = new Vector(x, y).add(minMax[0]);
                    Block block = getBlock(loc);
                    block.cubeSide = this;
                    if (x > 0) {
                        block.relatives.put(Facing.LEFT, new MoveResult(getBlock(new Vector(loc).add(new Vector(-1, 0))), Facing.LEFT));
                    }
                    if (x < CUBE_SIDE - 1) {
                        block.relatives.put(Facing.RIGHT, new MoveResult(getBlock(new Vector(loc).add(new Vector(1, 0))), Facing.RIGHT));
                    }
                    if (y > 0) {
                        block.relatives.put(Facing.UP, new MoveResult(getBlock(new Vector(loc).add(new Vector(0, -1))), Facing.UP));
                    }
                    if (y < CUBE_SIDE - 1) {
                        block.relatives.put(Facing.DOWN, new MoveResult(getBlock(new Vector(loc).add(new Vector(0, 1))), Facing.DOWN));
                    }
                }
            }
        }

        void connectEdges() {
            switch (this) {
                case FRONT: {
                    // up
                    for (int x1 = 50, y2 = 150; x1 < 100; x1 ++, y2 ++) {
                        Block me = getBlock(x1, 0);
                        assert me != null;
                        Block target = getBlock(0, y2);
                        assert target != null;
                        assert target.cubeSide == TOP;
                        me.relatives.put(Facing.UP, new MoveResult(target, Facing.RIGHT));
                    }
                    // right
                    for (int y1 = 0, y2 = 0; y1 < 50; y1 ++, y2 ++) {
                        Block me = getBlock(99, y1);
                        assert me != null;
                        Block target = getBlock(100, y2);
                        assert target != null;
                        assert target.cubeSide == RIGHT;
                        me.relatives.put(Facing.RIGHT, new MoveResult(target, Facing.RIGHT));
                    }
                    // down
                    for (int x1 = 50, x2 = 50; x1 < 100; x1 ++, x2 ++) {
                        Block me = getBlock(x1, 49);
                        assert me != null;
                        Block target = getBlock(x2, 50);
                        assert target != null;
                        assert target.cubeSide == BOTTOM;
                        me.relatives.put(Facing.DOWN, new MoveResult(target, Facing.DOWN));
                    }
                    // left
                    for (int y1 = 0, y2 = 149; y1 < 50; y1 ++, y2 --) {
                        Block me = getBlock(50, y1);
                        assert me != null;
                        Block target = getBlock(0, y2);
                        assert target != null;
                        assert target.cubeSide == LEFT;
                        me.relatives.put(Facing.LEFT, new MoveResult(target, Facing.RIGHT));
                    }
                    break;
                }
                case BACK: {
                    // up
                    for (int x1 = 50, x2 = 50; x1 < 100; x1 ++, x2 ++) {
                        Block me = getBlock(x1, 100);
                        assert me != null;
                        Block target = getBlock(x2, 99);
                        assert target != null;
                        assert target.cubeSide == BOTTOM;
                        me.relatives.put(Facing.UP, new MoveResult(target, Facing.UP));
                    }
                    // right
                    for (int y1 = 100, y2 = 49; y1 < 150; y1 ++, y2 --) {
                        Block me = getBlock(99, y1);
                        assert me != null;
                        Block target = getBlock(149, y2);
                        assert target != null;
                        assert target.cubeSide == RIGHT;
                        me.relatives.put(Facing.RIGHT, new MoveResult(target, Facing.LEFT));
                    }
                    // down
                    for (int x1 = 50, y2 = 150; x1 < 100; x1 ++, y2 ++) {
                        Block me = getBlock(x1, 149);
                        assert me != null;
                        Block target = getBlock(49, y2);
                        assert target != null;
                        assert target.cubeSide == TOP;
                        me.relatives.put(Facing.DOWN, new MoveResult(target, Facing.LEFT));
                    }
                    // left
                    for (int y1 = 100, y2 = 100; y1 < 150; y1 ++, y2 ++) {
                        Block me = getBlock(50, y1);
                        assert me != null;
                        Block target = getBlock(49, y2);
                        assert target != null;
                        assert target.cubeSide == LEFT;
                        me.relatives.put(Facing.LEFT, new MoveResult(target, Facing.LEFT));
                    }
                    break;
                }
                case LEFT: {
                    // up
                    for (int x1 = 0, y2 = 50; x1 < 50; x1 ++, y2 ++) {
                        Block me = getBlock(x1, 100);
                        assert me != null;
                        Block target = getBlock(50, y2);
                        assert target != null;
                        assert target.cubeSide == BOTTOM;
                        me.relatives.put(Facing.UP, new MoveResult(target, Facing.RIGHT));
                    }
                    // right
                    for (int y1 = 100, y2 = 100; y1 < 150; y1 ++, y2 ++) {
                        Block me = getBlock(49, y1);
                        assert me != null;
                        Block target = getBlock(50, y2);
                        assert target != null;
                        assert target.cubeSide == BACK;
                        me.relatives.put(Facing.RIGHT, new MoveResult(target, Facing.RIGHT));
                    }
                    // down
                    for (int x1 = 0, x2 = 0; x1 < 50; x1 ++, x2 ++) {
                        Block me = getBlock(x1, 149);
                        assert me != null;
                        Block target = getBlock(x2, 150);
                        assert target != null;
                        assert target.cubeSide == TOP;
                        me.relatives.put(Facing.DOWN, new MoveResult(target, Facing.DOWN));
                    }
                    // left
                    for (int y1 = 100, y2 = 49; y1 < 150; y1 ++, y2 --) {
                        Block me = getBlock(0, y1);
                        assert me != null;
                        Block target = getBlock(50, y2);
                        assert target != null;
                        assert target.cubeSide == FRONT;
                        me.relatives.put(Facing.LEFT, new MoveResult(target, Facing.RIGHT));
                    }
                    break;
                }
                case RIGHT: {
                    // up
                    for (int x1 = 100, x2 = 0; x1 < 150; x1 ++, x2 ++) {
                        Block me = getBlock(x1, 0);
                        assert me != null;
                        Block target = getBlock(x2, 199);
                        assert target != null;
                        assert target.cubeSide == TOP;
                        me.relatives.put(Facing.UP, new MoveResult(target, Facing.UP));
                    }
                    // right
                    for (int y1 = 0, y2 = 149; y1 < 50; y1 ++, y2 --) {
                        Block me = getBlock(149, y1);
                        assert me != null;
                        Block target = getBlock(99, y2);
                        assert target != null;
                        assert target.cubeSide == BACK;
                        me.relatives.put(Facing.RIGHT, new MoveResult(target, Facing.LEFT));
                    }
                    // down
                    for (int x1 = 100, y2 = 50; x1 < 150; x1 ++, y2 ++) {
                        Block me = getBlock(x1, 49);
                        assert me != null;
                        Block target = getBlock(99, y2);
                        assert target != null;
                        assert target.cubeSide == BOTTOM;
                        me.relatives.put(Facing.DOWN, new MoveResult(target, Facing.LEFT));
                    }
                    // left
                    for (int y1 = 0, y2 = 0; y1 < 50; y1 ++, y2 ++) {
                        Block me = getBlock(100, y1);
                        assert me != null;
                        Block target = getBlock(99, y2);
                        assert target != null;
                        assert target.cubeSide == FRONT;
                        me.relatives.put(Facing.LEFT, new MoveResult(target, Facing.LEFT));
                    }
                    break;
                }
                case TOP: {
                    // up
                    for (int x1 = 0, x2 = 0; x1 < 50; x1 ++, x2 ++) {
                        Block me = getBlock(x1, 150);
                        assert me != null;
                        Block target = getBlock(x2, 149);
                        assert target != null;
                        assert target.cubeSide == LEFT;
                        me.relatives.put(Facing.UP, new MoveResult(target, Facing.UP));
                    }
                    // right
                    for (int y1 = 150, x2 = 50; y1 < 200; y1 ++, x2 ++) {
                        Block me = getBlock(49, y1);
                        assert me != null;
                        Block target = getBlock(x2, 149);
                        assert target != null;
                        assert target.cubeSide == BACK;
                        me.relatives.put(Facing.RIGHT, new MoveResult(target, Facing.UP));
                    }
                    // down
                    for (int x1 = 0, x2 = 100; x1 < 50; x1 ++, x2 ++) {
                        Block me = getBlock(x1, 199);
                        assert me != null;
                        Block target = getBlock(x2, 0);
                        assert target != null;
                        assert target.cubeSide == RIGHT;
                        me.relatives.put(Facing.DOWN, new MoveResult(target, Facing.DOWN));
                    }
                    // left
                    for (int y1 = 150, x2 = 50; y1 < 200; y1 ++, x2 ++) {
                        Block me = getBlock(0, y1);
                        assert me != null;
                        Block target = getBlock(x2, 0);
                        assert target != null;
                        assert target.cubeSide == FRONT;
                        me.relatives.put(Facing.LEFT, new MoveResult(target, Facing.DOWN));
                    }
                    break;
                }
                case BOTTOM: {
                    // up
                    for (int x1 = 50, x2 = 50; x1 < 100; x1 ++, x2 ++) {
                        Block me = getBlock(x1, 50);
                        assert me != null;
                        Block target = getBlock(x2, 49);
                        assert target != null;
                        assert target.cubeSide == FRONT;
                        me.relatives.put(Facing.UP, new MoveResult(target, Facing.UP));
                    }
                    // right
                    for (int y1 = 50, x2 = 100; y1 < 100; y1 ++, x2 ++) {
                        Block me = getBlock(99, y1);
                        assert me != null;
                        Block target = getBlock(x2, 49);
                        assert target != null;
                        assert target.cubeSide == RIGHT;
                        me.relatives.put(Facing.RIGHT, new MoveResult(target, Facing.UP));
                    }
                    // down
                    for (int x1 = 50, x2 = 50; x1 < 100; x1 ++, x2 ++) {
                        Block me = getBlock(x1, 99);
                        assert me != null;
                        Block target = getBlock(x2, 100);
                        assert target != null;
                        assert target.cubeSide == BACK;
                        me.relatives.put(Facing.DOWN, new MoveResult(target, Facing.DOWN));
                    }
                    // left
                    for (int y1 = 50, x2 = 0; y1 < 100; y1 ++, x2 ++) {
                        Block me = getBlock(50, y1);
                        assert me != null;
                        Block target = getBlock(x2, 100);
                        assert target != null;
                        assert target.cubeSide == LEFT;
                        me.relatives.put(Facing.LEFT, new MoveResult(target, Facing.DOWN));
                    }
                    break;
                }
            }
        }
    }
}
