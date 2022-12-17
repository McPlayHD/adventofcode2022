package net.mcplayhd.adventofcode2022.tasks.task17;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Task17Part2 extends Task {
    private static final long ROCKS_TO_SPAWN = 1_000_000_000_000L;
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
    private final boolean[][] field = new boolean[WIDTH][100_000_000]; // {x,y}
    private final int[] heightmap = new int[WIDTH]; // here I just store the highest piece
    private long computedYOffset = 0;

    private final List<PointOfInterest> interests = new ArrayList<>();

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
        long start = System.nanoTime();
        spawnRocks();
        long time = System.nanoTime() - start;
        System.out.println("Falling took " + (time / 1000000.) + "ms");
        return Long.toString(computedYOffset + getTallestY());
    }

    int getTallestY() {
        int max = 0;
        for (int height : heightmap) {
            max = Math.max(height, max);
        }
        return max;
    }

    void spawnRocks() {
        int lastHighestY = 0;
        for (long rock = 0; rock < ROCKS_TO_SPAWN; rock++) {
            tick = tick % windDirections.length;
            int highestY = getTallestY();
            if (computedYOffset == 0 && rock > 10) { // do not try to compare the first few times or if already computed
                int tickOffset = (int) tick;
                int dy = highestY - lastHighestY;
                PointOfInterest interest = new PointOfInterest(tickOffset, rock, dy, heightmap);
                interests.add(interest);
                for (int startIndex = pieces.length; startIndex < interests.size() / 2 - 1; startIndex += pieces.length) {
                    // making steps of 5
                    PointOfInterest pointer = interests.get(interests.size() - 1 - startIndex);
                    if (pointer.equals(interest)) {
                        boolean isEqual = true;
                        for (int index = 0; index < startIndex; index++) {
                            PointOfInterest point1 = interests.get(interests.size() - 1 - index);
                            PointOfInterest point2 = interests.get(interests.size() - 1 - startIndex - index);
                            if (!point1.equals(point2)) {
                                isEqual = false;
                                break;
                            }
                        }
                        if (!isEqual) {
                            continue;
                        }
                        System.out.println("Found equal section!!!");
                        PointOfInterest AStart = interests.get(interests.size() - 1 - startIndex - startIndex);
                        System.out.println("A: From:" + AStart.piece + " To:" + pointer.piece);
                        System.out.println("B: From:" + pointer.piece + " To:" + interest.piece);
                        long deltaPieces = interest.piece - pointer.piece;
                        long deltaY = interest.currentHeightMap[0] - pointer.currentHeightMap[0];
                        System.out.println("Pieces in between: " + deltaPieces);
                        System.out.println("Delta Y: " + deltaY);
                        for (int x = 1; x < WIDTH; x++) {
                            int yDiff = interest.currentHeightMap[x] - pointer.currentHeightMap[x];
                            assert yDiff == deltaY; // just to make sure my algorithm is correct
                        }
                        while (rock + deltaPieces < ROCKS_TO_SPAWN) {
                            rock += deltaPieces;
                            computedYOffset += deltaY;
                        }
                        System.out.println("The Y offset is " + computedYOffset);
                        break;
                    }
                }
            }
            lastHighestY = highestY;
            Piece piece = pieces[(int) (rock % pieces.length)];
            Vector pos = new Vector(2, getTallestY() + 4);
            boolean canFall = true;
            for (; canFall; tick++) {
                WindDirection windDirection = windDirections[(int) (tick % windDirections.length)];
                if (!checkForObstacle(pos, piece, windDirection.vector)) {
                    pos.x += windDirection.vector.x;
                }
                if (checkForObstacle(pos, piece, GRAVITY)) {
                    canFall = false;
                } else {
                    pos.y += GRAVITY.y;
                }
            }
            placePiece(pos, piece);
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

    static class PointOfInterest {
        int windOffset;
        long piece;
        int dyFromLastPiece;
        int[] currentHeightMap;

        public PointOfInterest(int windOffset, long piece, int dyFromLastPiece, int[] heightmap) {
            this.windOffset = windOffset;
            this.piece = piece;
            this.dyFromLastPiece = dyFromLastPiece;
            this.currentHeightMap = new int[heightmap.length];
            System.arraycopy(heightmap, 0, currentHeightMap, 0, heightmap.length);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PointOfInterest that = (PointOfInterest) o;
            return windOffset == that.windOffset && dyFromLastPiece == that.dyFromLastPiece;
        }

        @Override
        public int hashCode() {
            return Objects.hash(windOffset, dyFromLastPiece);
        }
    }
}
