package net.mcplayhd.adventofcode2022.tasks.task15;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.*;

public class Task15Part1 extends Task {
    private static final int Y_OF_INTEREST = 2000000;

    private final Set<Sensor> sensors = new HashSet<>();
    private final Set<Loc> beacons = new HashSet<>();

    @Override
    public String getResult(String[] input) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        for (String line : input) {
            if (line.isEmpty())
                continue;
            line = line.replaceAll("[^(0-9,:)]", "");
            String[] split = line.split(":");
            String[] sensorXY = split[0].split(",");
            String[] beaconXY = split[1].split(",");
            Loc sensorLoc = new Loc(Integer.parseInt(sensorXY[0]), Integer.parseInt(sensorXY[1]));
            Loc beaconLoc = new Loc(Integer.parseInt(beaconXY[0]), Integer.parseInt(beaconXY[1]));
            int distance = sensorLoc.distance(beaconLoc);
            sensors.add(new Sensor(sensorLoc, distance));
            beacons.add(beaconLoc);
            minX = Math.min(minX, sensorLoc.x - distance);
            maxX = Math.max(maxX, sensorLoc.x + distance);
        }
        int noBeacon = 0;
        for (int x = minX; x <= maxX; x++) {
            Loc loc = new Loc(x, Y_OF_INTEREST);
            if (beacons.contains(loc))
                continue;
            for (Sensor sensor : sensors) {
                if (sensor.watches(loc)) {
                    noBeacon++;
                    break;
                }
            }
        }
        return Integer.toString(noBeacon);
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
            return '{' + x + "," + y + '}';
        }
    }
}
