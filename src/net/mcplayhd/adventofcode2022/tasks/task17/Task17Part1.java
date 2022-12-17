package net.mcplayhd.adventofcode2022.tasks.task17;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.Scanner;

public class Task17Part1 extends Task {
    private static final boolean DEBUG = false;
    private static final long ROCKS_TO_SPAWN = 2022;
    private static final int WIDTH = 7;
    private static final Vector GRAVITY = new Vector(0, -1);
    private static WindDirection[] windDirections;
    private static final Piece[] pieces = new Piece[]{
            new LineHorizontal(),
            new Cross(),
            new LMirrored(),
            new LineVertical(),
            new Block()
    };

    private long tick;
    private final boolean[][] field = new boolean[WIDTH][100_000]; // {x,y}
    private final int[] heightmap = new int[WIDTH]; // here I just store the highest piece

    @Override
    public String getResult(String[] input) {
        char[] chars = input[0].toCharArray();
        windDirections = new WindDirection[chars.length];
        for (int index = 0; index < windDirections.length; index++) {
            windDirections[index] = chars[index] == '<' ? WindDirection.LEFT : WindDirection.RIGHT;
        }
        // placing ground
        for (int x = 0; x < WIDTH; x++) {
            field[x][0] = true;
        }
        spawnRocks();
        return Integer.toString(getTallestY());
    }

    int getTallestY() {
        int max = 0;
        for (int height : heightmap) {
            max = Math.max(height, max);
        }
        return max;
    }

    void spawnRocks() {
        for (int rock = 0; rock < ROCKS_TO_SPAWN; rock++) {
            Piece piece = pieces[rock % pieces.length];
            Vector pos = new Vector(2, getTallestY() + 4);
            boolean canFall = true;
            for (; canFall; tick++) {
                if (DEBUG) {
                    System.out.println("Spawning/Falling");
                    drawBoard(piece, pos);
                }
                WindDirection windDirection = windDirections[(int) (tick % windDirections.length)];
                if (!checkForObstacle(pos, piece, windDirection.vector)) {
                    pos.x += windDirection.vector.x;
                }
                if (DEBUG) {
                    System.out.println(windDirection);
                    drawBoard(piece, pos);
                }
                if (checkForObstacle(pos, piece, GRAVITY)) {
                    canFall = false;
                } else {
                    pos.y += GRAVITY.y;
                }
            }
            placePiece(pos, piece);
            if (DEBUG) {
                System.out.println("Placing");
                drawBoard(null, null);
            }
        }
    }

    Scanner scanner = new Scanner(System.in);

    void drawBoard(Piece piece, Vector pos) {
        int maxY = getTallestY() + 10;
        int minY = maxY - 20;
        for (int y = maxY; y >= minY - 10 && y >= 0; y--) {
            for (int x = 0; x < WIDTH; x++) {
                char c = field[x][y] ? '#' : '.';
                if (piece != null) {
                    for (Vector pixel : piece.vectors) {
                        int X = pos.x + pixel.x;
                        int Y = pos.y + pixel.y;
                        if (x == X && y == Y) {
                            c = 'o';
                            break;
                        }
                    }
                }
                System.out.print(c);
            }
            System.out.println();
        }
        scanner.nextLine();
        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
    }

    boolean checkForObstacle(Vector pos, Piece piece, Vector vector) {
        for (Vector pixel : piece.vectors) {
            int x = pos.x + pixel.x + vector.x;
            int y = pos.y + pixel.y + vector.y;
            if (x < 0 || x >= WIDTH || field[x][y]) {
                return true;
            }
        }
        return false;
    }

    void placePiece(Vector pos, Piece piece) {
        for (Vector pixel : piece.vectors) {
            int x = pos.x + pixel.x;
            int y = pos.y + pixel.y;
            field[x][y] = true;
            heightmap[x] = Math.max(y, heightmap[x]);
        }
    }

    enum WindDirection {
        LEFT(new Vector(-1, 0)),
        RIGHT(new Vector(1, 0));

        final Vector vector;

        WindDirection(Vector vector) {
            this.vector = vector;
        }
    }

    static class Vector {
        int x, y;

        public Vector(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Piece {
        Vector[] vectors;
    }

    static class LineHorizontal extends Piece {
        public LineHorizontal() {
            vectors = new Vector[4];
            vectors[0] = new Vector(0, 0);
            vectors[1] = new Vector(1, 0);
            vectors[2] = new Vector(2, 0);
            vectors[3] = new Vector(3, 0);
        }
    }

    static class Cross extends Piece {
        public Cross() {
            vectors = new Vector[5];
            vectors[0] = new Vector(1, 0);
            vectors[1] = new Vector(0, 1);
            vectors[2] = new Vector(1, 1);
            vectors[3] = new Vector(2, 1);
            vectors[4] = new Vector(1, 2);
        }
    }

    static class LMirrored extends Piece {
        public LMirrored() {
            vectors = new Vector[5];
            vectors[0] = new Vector(0, 0);
            vectors[1] = new Vector(1, 0);
            vectors[2] = new Vector(2, 0);
            vectors[3] = new Vector(2, 1);
            vectors[4] = new Vector(2, 2);
        }
    }

    static class LineVertical extends Piece {
        public LineVertical() {
            vectors = new Vector[4];
            vectors[0] = new Vector(0, 0);
            vectors[1] = new Vector(0, 1);
            vectors[2] = new Vector(0, 2);
            vectors[3] = new Vector(0, 3);
        }
    }

    static class Block extends Piece {
        public Block() {
            vectors = new Vector[4];
            vectors[0] = new Vector(0, 0);
            vectors[1] = new Vector(0, 1);
            vectors[2] = new Vector(1, 0);
            vectors[3] = new Vector(1, 1);
        }
    }
}
