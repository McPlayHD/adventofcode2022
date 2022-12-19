package net.mcplayhd.adventofcode2022.tasks.task19;

import com.google.gson.Gson;
import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Task19Part1 extends Task {
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
                blueprint.robots.put(collects, robot);
            }
            blueprints.add(blueprint);
        }
        for (Blueprint blueprint : blueprints) {
            System.out.println(blueprint);
        }
        // TODO: 19/12/2022 well that's not so easy now
        return null;
    }

    static class Blueprint {
        int id;
        Map<Rock, Robot> robots = new HashMap<>();

        public Blueprint(int id) {
            this.id = id;
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
