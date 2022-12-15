package net.mcplayhd.adventofcode2022.tasks.task15;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Task15Part2 extends Task {
    private static final Loc MIN = new Loc(0, 0);
    private static final Loc MAX = new Loc(4000000, 4000000);

    private static final Set<Sensor> sensors = new HashSet<>();

    private static long frequency = -1;

    @Override
    public String getResult(String[] input) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        for (String line : input) {
            if (line.isEmpty())
                continue;
            line = line.replaceAll("[^(0-9,:\\-)]", "");
            String[] split = line.split(":");
            String[] sensorXY = split[0].split(",");
            String[] beaconXY = split[1].split(",");
            Loc sensorLoc = new Loc(Integer.parseInt(sensorXY[0]), Integer.parseInt(sensorXY[1]));
            Loc beaconLoc = new Loc(Integer.parseInt(beaconXY[0]), Integer.parseInt(beaconXY[1]));
            int distance = sensorLoc.distance(beaconLoc);
            sensors.add(new Sensor(sensorLoc, distance));
            minX = Math.min(minX, sensorLoc.x - distance);
            maxX = Math.max(maxX, sensorLoc.x + distance);
        }
        Section root = new Section(MIN, MAX);
        root.divide();
        return Long.toString(frequency);
    }

    static class Section {
        Loc min, max;

        public Section(Loc min, Loc max) {
            this.min = min;
            this.max = max;
        }

        boolean isFullyInsideSensor() {
            for (Sensor sensor : sensors) {
                if (sensor.watches(min)
                        && sensor.watches(max)
                        && sensor.watches(new Loc(min.x, max.y))
                        && sensor.watches(new Loc(max.x, min.y))) {
                    return true;
                }
            }
            return false;
        }

        void divide() {
            if (min.equals(max)) {
                for (Sensor sensor : sensors) {
                    if (sensor.watches(min)) {
                        return;
                    }
                }
                System.out.println("Found point: " + min);
                frequency = min.x * 4000000L + min.y;
                return;
            }
            if (isFullyInsideSensor()) {
                return;
            }
            int midX = (max.x + min.x) / 2;
            int midY = (max.y + min.y) / 2;
            Section topLeft = new Section(min, new Loc(midX, midY));
            topLeft.divide();
            Section topRight = new Section(new Loc(midX + 1, min.y), new Loc(max.x, midY));
            topRight.divide();
            Section bottomLeft = new Section(new Loc(min.x, midY + 1), new Loc(midX, max.y));
            bottomLeft.divide();
            Section bottomRight = new Section(new Loc(midX + 1, midY + 1), max);
            bottomRight.divide();
        }
    }

    static class Sensor {
        Loc loc;
        int strength;

        public Sensor(Loc loc, int strength) {
            this.loc = loc;
            this.strength = strength;
        }

        boolean watches(Loc loc) {
            return this.loc.distance(loc) <= strength;
        }
    }

    static class Loc {
        int x, y;

        public Loc(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int distance(Loc other) {
            return Math.abs(other.x - x) + Math.abs(other.y - y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Loc loc = (Loc) o;
            return x == loc.x && y == loc.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "{" + x + "," + y + "}";
        }
    }
}
