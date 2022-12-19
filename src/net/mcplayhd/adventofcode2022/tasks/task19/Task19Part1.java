package net.mcplayhd.adventofcode2022.tasks.task19;

import com.google.gson.Gson;
import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.*;

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
                blueprint.addToStore(robot);
            }
            blueprints.add(blueprint);
        }
        int summed = 0;
        for (Blueprint blueprint : blueprints) {
            System.out.println(blueprint);
            DiggingSimulationStep start = new DiggingSimulationStep(blueprint);
            start.haveRobots.put(Rock.ORE, 1);
            summed += blueprint.dp(start);
        }
        return Integer.toString(summed);
    }

    static class DiggingSimulationStep implements Cloneable {
        transient Blueprint blueprint;
        int currentMinute;
        Map<Rock, Integer> haveRobots = new HashMap<>();
        Map<Rock, Integer> haveRocks = new HashMap<>();

        public DiggingSimulationStep(Blueprint blueprint) {
            this.blueprint = blueprint;
        }

        public int getQualityLevel() {
            return blueprint.id * haveRocks.getOrDefault(Rock.GEODE, 0);
        }

        String getDPKey() {
            String key = currentMinute + ":";
            for (Rock rock : Rock.values()) {
                int available = haveRocks.getOrDefault(rock, 0);
                int robots = haveRobots.getOrDefault(rock, 0);
                key += available + "," + robots + ":";
            }
            return key;
        }

        DiggingSimulationStep dp(Robot toPurchase) {
            // if we want to purchase something we have to pay for it
            if (toPurchase != null) {
                for (Cost cost : toPurchase.costs) {
                    haveRocks.merge(cost.currency, -cost.amount, Integer::sum);
                }
            }
            // gather resources.
            for (Map.Entry<Rock, Integer> robot : haveRobots.entrySet()) {
                haveRocks.merge(robot.getKey(), robot.getValue(), Integer::sum);
            }
            // this took exactly one minute of time.
            currentMinute++;
            // if we ordered a robot it will now arrive.
            if (toPurchase != null) {
                haveRobots.merge(toPurchase.collects, 1, Integer::sum);
            }
            // if we reached the time limit, we can't get better results.
            if (currentMinute == MAX_MINUTES) {
                return this;
            }
            // check if DP table of blueprint contains the current state
            String dpKey = getDPKey();
            DiggingSimulationStep dpEntry = blueprint.DP.get(dpKey);
            if (dpEntry != null) {
                return dpEntry;
            }
            Set<DiggingSimulationStep> allPossible = new HashSet<>();
            // buy new things if possible
            for (Robot robot : blueprint.store) {
                if (shouldBuy(robot) && canBuy(robot)) {
                    DiggingSimulationStep bestWhenBuyingThis = clone().dp(robot);
                    allPossible.add(bestWhenBuyingThis);
                }
            }
            // maybe it's better not to buy.
            DiggingSimulationStep ifIContinueWithoutBuying = dp(null);
            allPossible.add(ifIContinueWithoutBuying);
            // get the best result.
            DiggingSimulationStep best = null;
            for (DiggingSimulationStep all : allPossible) {
                if (best == null || best.getQualityLevel() < all.getQualityLevel()) {
                    best = all;
                }
            }
            // storing the best state in the DP
            blueprint.DP.put(dpKey, best);
            // returning the best state
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

        boolean shouldBuy(Robot robot) {
            if (robot.collects == Rock.GEODE)
                return true;
            int max = blueprint.maxCosts.getOrDefault(robot.collects, 0);
            return haveRobots.getOrDefault(robot.collects, 0) < max;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public DiggingSimulationStep clone() {
            try {
                DiggingSimulationStep clone = (DiggingSimulationStep) super.clone();
                clone.blueprint = blueprint;
                clone.currentMinute = currentMinute;
                clone.haveRobots = new HashMap<>(haveRobots);
                clone.haveRocks = new HashMap<>(haveRocks);
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    static class Blueprint {
        int id;
        Set<Robot> store = new HashSet<>();
        Map<String, DiggingSimulationStep> DP = new HashMap<>();
        Map<Rock, Integer> maxCosts = new HashMap<>();

        public Blueprint(int id) {
            this.id = id;
        }

        void addToStore(Robot robot) {
            store.add(robot);
            for (Cost cost : robot.costs) {
                maxCosts.merge(cost.currency, cost.amount, Integer::max);
            }
        }

        int dp(DiggingSimulationStep start) {
            DiggingSimulationStep best = start.dp(null);
            System.out.println(best);
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
