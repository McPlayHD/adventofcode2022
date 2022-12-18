package net.mcplayhd.adventofcode2022.tasks.task18;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Task18Part1 extends Task {
    private final Set<Vector> blocks = new HashSet<>();

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
        }
        int freeFaces = 0;
        for (Vector block : blocks) {
            for (BlockFace blockFace : BlockFace.values()) {
                Vector check = block.clone().add(blockFace.offset);
                if (!blocks.contains(check)) {
                    freeFaces ++;
                }
            }
        }
        return Integer.toString(freeFaces);
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
