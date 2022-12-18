package net.mcplayhd.adventofcode2022.tasks.task18;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.*;

public class Task18Part2 extends Task {
    private final Set<Vector> blocks = new HashSet<>();
    private final Vector MIN = new Vector(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    private final Vector MAX = new Vector(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    @Override
    public String getResult(String[] input) {
        for (String line : input) {
            if (line.isEmpty())
                continue;
            String[] split = line.split(",");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            int z = Integer.parseInt(split[2]);
            blocks.add(new Vector(x, y, z));
            MIN.x = Math.min(MIN.x, x);
            MIN.y = Math.min(MIN.y, y);
            MIN.z = Math.min(MIN.z, z);
            MAX.x = Math.max(MAX.x, x);
            MAX.y = Math.max(MAX.y, y);
            MAX.z = Math.max(MAX.z, z);
        }
        MIN.add(new Vector(-1, -1, -1));
        MAX.add(new Vector(1, 1, 1));
        return Integer.toString(fillWithWaterAndCountFaces());
    }

    boolean isOutOfBounds(Vector vector) {
        return vector.x < MIN.x || vector.y < MIN.y || vector.z < MIN.z ||
                vector.x > MAX.x || vector.y > MAX.y || vector.z > MAX.z;
    }

    int fillWithWaterAndCountFaces() {
        int faceCount = 0;
        Set<Vector> visited = new HashSet<>();
        Queue<Vector> toVisit = new ArrayDeque<>();
        toVisit.add(MIN);
        while (!toVisit.isEmpty()) {
            Vector check = toVisit.poll();
            if (isOutOfBounds(check) || visited.contains(check)) {
                continue;
            }
            if (blocks.contains(check)) {
                faceCount ++;
                continue;
            }
            visited.add(check);
            for (BlockFace blockFace : BlockFace.values()) {
                toVisit.add(check.clone().add(blockFace.offset));
            }
        }
        return faceCount;
    }

    enum BlockFace {
        POSITIVE_X(new Vector(1, 0, 0)),
        NEGATIVE_X(new Vector(-1, 0, 0)),
        POSITIVE_Y(new Vector(0, 1, 0)),
        NEGATIVE_Y(new Vector(0, -1, 0)),
        POSITIVE_Z(new Vector(0, 0, 1)),
        NEGATIVE_Z(new Vector(0, 0, -1));

        final Vector offset;

        BlockFace(Vector offset) {
            this.offset = offset;
        }
    }

    static class Vector implements Cloneable {
        int x, y, z;

        public Vector(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Vector add(Vector vector) {
            x += vector.x;
            y += vector.y;
            z += vector.z;
            return this;
        }

        @Override
        public Vector clone() {
            try {
                Vector clone = (Vector) super.clone();
                clone.x = x;
                clone.y = y;
                clone.z = z;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vector vector = (Vector) o;
            return x == vector.x && y == vector.y && z == vector.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }
}
