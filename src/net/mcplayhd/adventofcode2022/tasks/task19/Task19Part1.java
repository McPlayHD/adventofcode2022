package net.mcplayhd.adventofcode2022.tasks.task19;

import com.google.gson.Gson;
import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Task19Part1 extends Task {
    private static final int MAX_MINUTES = 24;

    Set<Blueprint> blueprints = new HashSet<>();

    @Override
    public String getResult(String[] input) {
        for (String line : input) {
            if (line.isEmpty())
                continue;
            String[] idSplit = line.split(":");
            int id = Integer.parseInt(idSplit[0].replaceAll("[^0-9]", ""));
            Blueprint blueprint = new Blueprint(id);
            String[] robotsSplit = idSplit[1].split("\\.");
            for (String robotSt : robotsSplit) {
                String[] robotSp = robotSt.replace("costs", "\n").split("\n");
                Rock collects = Rock.valueOf(robotSp[0].split(" ")[2].toUpperCase());
                Robot robot = new Robot(collects);
                String[] costsSp = robotSp[1].replace("and", "\n").split("\n");
                for (String costSt : costsSp) {
                    String[] costSp = costSt.split(" ");
                    int amount = Integer.parseInt(costSp[1]);
                    Rock currency = Rock.valueOf(costSp[2].toUpperCase());
                    robot.costs.add(new Cost(currency, amount));
                }
                blueprint.store.put(collects, robot);
            }
            blueprints.add(blueprint);
        }
        int summed = 0;
        for (Blueprint blueprint : blueprints) {
            DiggingSimulationStep start = new DiggingSimulationStep(blueprint);
            start.haveRobots.put(Rock.ORE, 1);
            summed += blueprint.dp(start);
        }
        return Integer.toString(summed);
    }

    static class DiggingSimulationStep implements Cloneable {
        int depth = 0;
        Blueprint blueprint;
        int currentMinute;
        Map<Rock, Integer> haveRobots = new HashMap<>();
        Map<Rock, Integer> haveRocks = new HashMap<>();

        public DiggingSimulationStep(Blueprint blueprint) {
            this.blueprint = blueprint;
        }

        public int getQualityLevel() {
            return blueprint.id * haveRocks.getOrDefault(Rock.GEODE, 0);
        }

        public long getGoodness() {
            long goodness = 0;
            for (Map.Entry<Rock, Integer> robots : haveRobots.entrySet()) {
                long factor = 1 << (robots.getKey().ordinal() * 7);
                goodness += factor * robots.getValue();
            }
            return goodness;
        }

        String getDPKey() {
            String key = currentMinute + ":";
            for (Rock rock : Rock.values()) {
                int available = haveRocks.getOrDefault(rock, 0);
                int robots = haveRocks.getOrDefault(rock, 0);
                key += available + "," + robots + ":";
            }
            return key;
        }

        DiggingSimulationStep dp() {
            // dig and increase time
            for (Map.Entry<Rock, Integer> robot : haveRobots.entrySet()) {
                haveRocks.merge(robot.getKey(), robot.getValue(), Integer::sum);
            }
            currentMinute++;
            if (currentMinute > MAX_MINUTES)
                // we reached the time limit so returning the state
                return this;
            // check if DP table of blueprint contains value
            DiggingSimulationStep dpEntry = blueprint.DP.get(getDPKey());
            if (dpEntry != null)
                return dpEntry;
            Set<DiggingSimulationStep> allPossible = new HashSet<>();
            // buy new things
            for (Robot robot : blueprint.store.values()) {
                // check if buying that one robot and then doing recursion again is better
                if (canBuy(robot)) {
                    DiggingSimulationStep clone = this.clone();
                    clone.buy(robot);
                    DiggingSimulationStep bestWhenBuyingThis = clone.dp();
                    allPossible.add(bestWhenBuyingThis);
                }
            }
            // check if this it is better to not buy
            DiggingSimulationStep ifIContinueWithoutBuying = this.clone().dp();
            allPossible.add(ifIContinueWithoutBuying);
            // return best
            DiggingSimulationStep best = null;
            for (DiggingSimulationStep all : allPossible) {
                if (best == null || best.getQualityLevel() < all.getQualityLevel()) {
                    best = all;
                }
            }
            blueprint.DP.put(getDPKey(), best);
            return best;
        }

        boolean canBuy(Robot robot) {
            for (Cost cost : robot.costs) {
                if (haveRocks.getOrDefault(cost.currency, 0) < cost.amount) {
                    return false;
                }
            }
            return true;
        }

        void buy(Robot robot) {
            for (Cost cost : robot.costs) {
                haveRocks.merge(cost.currency, -cost.amount, Integer::sum);
            }
            haveRobots.merge(robot.collects, 1, Integer::sum);
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public DiggingSimulationStep clone() {
            try {
                DiggingSimulationStep clone = (DiggingSimulationStep) super.clone();
                clone.depth = depth + 1;
                clone.blueprint = blueprint;
                clone.currentMinute = currentMinute;
                clone.haveRobots = new HashMap<>();
                clone.haveRobots.putAll(haveRobots);
                clone.haveRocks.putAll(haveRocks);
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    static class Blueprint {
        int id;
        Map<Rock, Robot> store = new HashMap<>();
        Map<String, DiggingSimulationStep> DP = new HashMap<>();

        public Blueprint(int id) {
            this.id = id;
        }

        int dp(DiggingSimulationStep start) {
            DiggingSimulationStep best = start.dp();
            return best.getQualityLevel();
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    static class Robot {
        Rock collects;
        Set<Cost> costs = new HashSet<>();

        public Robot(Rock collects) {
            this.collects = collects;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    static class Cost {
        Rock currency;
        int amount;

        public Cost(Rock currency, int amount) {
            this.currency = currency;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    enum Rock {
        ORE,
        CLAY,
        OBSIDIAN,
        GEODE
    }
}
