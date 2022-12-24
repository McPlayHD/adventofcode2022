package net.mcplayhd.adventofcode2022.tasks.task24;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.*;

public class Task24Part1 extends Task {
    static int HEIGHT, WIDTH;
    static Block[][] blocks; // [y][x]
    static Block start, goal;
    static Set<Current> currents = new HashSet<>();
    static Map<Block, Human> humans = new HashMap<>();

    @Override
    public String getResult(String[] input) {
        for (String line : input) {
            if (line.isEmpty())
                continue;
            HEIGHT ++;
            WIDTH = line.length();
        }
        blocks = new Block[HEIGHT][WIDTH];
        int y = 0;
        for (String line : input) {
            if (line.isEmpty())
                continue;
            char[] chars = line.toCharArray();
            for (int x = 0; x < chars.length; x ++) {
                char c = chars[x];
                boolean isWall = chars[x] == '#';
                Vector loc = new Vector(x, y);
                Block block = new Block(isWall, loc);
                if (!isWall) {
                    if (y == 0) {
                        start = block;
                    }
                    if (y == HEIGHT - 1) {
                        goal = block;
                    }
                    if (c != '.') {
                        Current current = new Current(Direction.getFromChar(c), loc);
                        block.currents.add(current);
                        currents.add(current);
                    }
                }
                blocks[y][x] = block;
            }
            y ++;
        }
        int minute = 0;
        Human adam = new Human(start);
        humans.put(start, adam);
        printBoard();
        for (; !humans.containsKey(goal); minute ++) {
            for (Current current : currents) {
                current.move();
            }
            Set<Human> aliveHumans = new HashSet<>(humans.values());
            humans.clear();
            for (Human human : aliveHumans) {
                human.moveToAllPossible();
            }
        }
        printBoard();
        return Integer.toString(minute);
    }

    static Block getBlock(Vector vector) {
        int x = vector.x;
        int y = vector.y;
        if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) {
            return null;
        }
        return blocks[y][x];
    }

    void printBoard() {
        for (int y = 0; y < HEIGHT; y ++) {
            for (int x = 0; x < WIDTH; x ++) {
                Block block = blocks[y][x];
                if (block.isWall) {
                    System.out.print('#');
                } else if (block.currents.isEmpty()) {
                    boolean hasHuman = false;
                    for (Human human : humans.values()) {
                        if (human.block == block) {
                            hasHuman = true;
                            break;
                        }
                    }
                    if (hasHuman) {
                        System.out.print('E');
                    } else {
                        System.out.print('.');
                    }
                } else if (block.currents.size() == 1) {
                    Current current = block.currents.stream().findFirst().get();
                    System.out.print(current.direction.c);
                } else {
                    System.out.print(block.currents.size());
                }
            }
            System.out.println();
        }
    }

    static class Human {
        Block block;

        public Human(Block block) {
            this.block = block;
        }

        void moveToAllPossible() {
            if (block.currents.isEmpty()) {
                // can stay
                humans.put(block, new Human(block));
            }
            for (Direction direction : Direction.values()) {
                Block target = getBlock(block.loc.clone().add(direction.vector));
                if (target == null || target.isWall) {
                    continue;
                }
                if (!target.currents.isEmpty()) {
                    continue;
                }
                // we can go there
                humans.put(target, new Human(target));
            }
        }
    }

    static class Current {
        final Direction direction;
        Vector loc;

        public Current(Direction direction, Vector loc) {
            this.direction = direction;
            this.loc = loc;
        }

        void move() {
            Block target = getBlock(loc.clone().add(direction.vector));
            assert target != null; // we just hope currents can't escape the board
            if (target.isWall) {
                target = getBlock(target.getOppositeWall().loc.clone().add(direction.vector));
            }
            assert target != null;
            Block currentBlock = getBlock(loc);
            assert currentBlock != null;
            currentBlock.currents.remove(this);
            target.currents.add(this);
            loc = target.loc.clone();
        }
    }

    static class Block {
        final boolean isWall;
        final Vector loc;
        Set<Current> currents = new HashSet<>();

        public Block(boolean isWall, Vector loc) {
            this.isWall = isWall;
            this.loc = loc;
        }

        Block getOppositeWall() {
            if (!isWall) {
                throw new RuntimeException("I do not identify as a wall.");
            }
            if (loc.x == 0) {
                return getBlock(new Vector(WIDTH - 1, loc.y));
            }
            if (loc.x == WIDTH - 1) {
                return getBlock(new Vector(0, loc.y));
            }
            if (loc.y == 0) {
                return getBlock(new Vector(loc.x, HEIGHT - 1));
            }
            if (loc.y == HEIGHT - 1) {
                return getBlock(new Vector(loc.x, 0));
            }
            throw new RuntimeException("How tf can I be a wall?");
        }
    }

    enum Direction {
        UP(new Vector(0, -1), '^'),
        RIGHT(new Vector(1, 0), '>'),
        DOWN(new Vector(0, 1), 'v'),
        LEFT(new Vector(-1, 0), '<');

        final Vector vector;
        final char c;

        Direction(Vector vector, char c) {
            this.vector = vector;
            this.c = c;
        }

        static Direction getFromChar(char c) {
            for (Direction direction : Direction.values()) {
                if (direction.c == c) {
                    return direction;
                }
            }
            throw new RuntimeException("Unknown direction '" + c + "'");
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
