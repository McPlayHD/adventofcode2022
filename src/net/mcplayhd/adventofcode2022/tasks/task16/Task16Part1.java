package net.mcplayhd.adventofcode2022.tasks.task16;

import net.mcplayhd.adventofcode2022.tasks.Task;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Task16Part1 extends Task {
    private static final int ATTEMPTS = 1000000;
    private static final int MINUTES_LIMIT = 30;
    private static final int MINUTES_TO_OPEN_VALVE = 1;
    private static final int MINUTES_TO_MOVE = 1;
    private static final boolean DEBUG = false;

    private static final Set<Valve> valves = new HashSet<>();

    @Override
    public String getResult(String[] input) {
        // reading the input
        Map<String, Valve> tmpNames = new HashMap<>();
        Map<Valve, String[]> tmpNeighbours = new HashMap<>();
        for (String line : input) {
            if (line.isEmpty())
                continue;
            String[] split = line.split(" ");
            String name = split[1];
            int flowRate = Integer.parseInt(split[4].replaceAll("[^0-9]", ""));
            Valve valve = new Valve(name, flowRate);
            valves.add(valve);
            String[] neighbourNames = line.replace("to valve", "\n").split("\n")[1].replaceAll("[^(A-Z,)]", "").split(",");
            tmpNames.put(name, valve);
            tmpNeighbours.put(valve, neighbourNames);
        }
        for (Valve valve : valves) {
            String[] neighbours = tmpNeighbours.get(valve);
            for (String neighbour : neighbours) {
                valve.neighbours.add(tmpNames.get(neighbour));
            }
        }
        int bestTotalFlow = 0;
        Random rnd = new Random();
        for (int attempt = 0; attempt < ATTEMPTS; attempt ++) {
            Valve current = tmpNames.get("AA");
            int currentTime = 0;
            int totalFlow = 0;
            Set<Valve> open = new HashSet<>();
            // starting from current valve calculate for all valves how long it would take to get to
            while (currentTime < MINUTES_LIMIT) {
                if (DEBUG) {
                    System.out.println("Current: " + current.name);
                }
                Map<Valve, Integer> moveTimes = current.calculateMoveTimesToAllValves();
                if (DEBUG) {
                    System.out.println("Move times: ");
                    for (Map.Entry<Valve, Integer> entry : moveTimes.entrySet()) {
                        System.out.println(entry.getKey().name + " -> " + entry.getValue());
                    }
                }
                // for every valve calculate how much total pressure I could get out if I now went there and opened it
                List<ValvePotential> potentials = new ArrayList<>();
                for (Valve valve : valves) {
                    if (open.contains(valve) || valve.flowRate == 0)
                        continue;
                    int timeToGo = moveTimes.get(valve);
                    int timeLeft = MINUTES_LIMIT - currentTime - timeToGo - MINUTES_TO_OPEN_VALVE;
                    if (timeLeft <= 0)
                        continue;
                    potentials.add(new ValvePotential(valve, valve.flowRate * timeLeft));
                }
                // if potentials is empty there is nothing left we can do
                if (potentials.isEmpty())
                    break;
                // move on the direct way to the best valve and open it
                Collections.sort(potentials);
                Collections.reverse(potentials);
                if (DEBUG) {
                    System.out.println("Potentials: ");
                    for (ValvePotential potential : potentials) {
                        System.out.println(potential.valve.name + ": " + potential.totalFlow);
                    }
                }

                // randomized
                int goToIndex = 0;
                while (goToIndex < potentials.size() - 1 && rnd.nextBoolean()) {
                    goToIndex ++;
                }

                ValvePotential goTo = potentials.get(goToIndex);
                current = goTo.valve;
                currentTime += moveTimes.get(current) + MINUTES_TO_OPEN_VALVE;
                open.add(current);
                totalFlow += goTo.totalFlow;
            }
            bestTotalFlow = Math.max(totalFlow, bestTotalFlow);
        }
        return Integer.toString(bestTotalFlow);
    }

    static class Valve {
        String name;
        int flowRate;
        Set<Valve> neighbours = new HashSet<>();

        public Valve(String name, int flowRate) {
            this.name = name;
            this.flowRate = flowRate;
        }

        Map<Valve, Integer> calculateMoveTimesToAllValves() {
            Map<Valve, Integer> moveTimes = new HashMap<>();
            PriorityQueue<MoveInstruction> moveInstructions = new PriorityQueue<>();
            moveInstructions.add(new MoveInstruction(0, this));
            while (!moveInstructions.isEmpty()) {
                MoveInstruction instruction = moveInstructions.poll();
                int time = instruction.totalTime;
                Valve valve = instruction.target;
                if (moveTimes.containsKey(valve))
                    continue; // we already visited this valve before
                moveTimes.put(valve, time);
                for (Valve neighbour : valve.neighbours) {
                    moveInstructions.add(new MoveInstruction(time + MINUTES_TO_MOVE, neighbour));
                }
            }
            return moveTimes;
        }
    }

    static class MoveInstruction implements Comparable<MoveInstruction> {
        int totalTime;
        Valve target;

        public MoveInstruction(int totalTime, Valve target) {
            this.totalTime = totalTime;
            this.target = target;
        }

        @Override
        public int compareTo(@NotNull MoveInstruction o) {
            return Integer.compare(totalTime, o.totalTime);
        }
    }

    static class ValvePotential implements Comparable<ValvePotential> {
        Valve valve;
        int totalFlow;

        public ValvePotential(Valve valve, int totalFlow) {
            this.valve = valve;
            this.totalFlow = totalFlow;
        }

        @Override
        public int compareTo(@NotNull ValvePotential o) {
            return Integer.compare(totalFlow, o.totalFlow);
        }
    }
}
