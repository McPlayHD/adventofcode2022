package net.mcplayhd.adventofcode2022.tasks.task11;

import net.mcplayhd.adventofcode2022.tasks.Task;
import netscape.security.UserTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task11Part1 extends Task {
    Map<Integer, Monkey> monkeys = new HashMap<>();

    @Override
    public String getResult(String[] input) {
        for (int index = 0; index < input.length; index ++) {
            String line = input[index];
            if (line.isEmpty())
                continue;
            int monkeyNumber = Integer.parseInt(line.substring("Monkey ".length()).replace(":", ""));
            // hardcoded
            String[] startingRaw = input[++index].substring("  Starting items: ".length()).replace(" ", "").split(",");
            List<Integer> startingItems = new ArrayList<>();
            for (String st : startingRaw) {
                startingItems.add(Integer.parseInt(st));
            }
            Operation operation = getOperation(monkeyNumber);
            index += 2;
            int divide = Integer.parseInt(input[index].substring("  Test: divisible by ".length()));
            int monkeyIfTrue = Integer.parseInt(input[++index].substring("    If true: throw to monkey ".length()));
            int monkeyIfFalse = Integer.parseInt(input[++index].substring("    If false: throw to monkey ".length()));
            Monkey monkey = new Monkey(startingItems, operation, divide, monkeyIfTrue, monkeyIfFalse);
            monkeys.put(monkeyNumber, monkey);
        }
        for (int round = 0; round < 20; round ++) {
            for (Monkey monkey : monkeys.values()) {
                monkey.inspectAllItems();
            }
        }
        Monkey top = getMostActive();
        Monkey second = getMostActive(top);
        return Integer.toString(top.inspectionCounter * second.inspectionCounter);
    }

    private Monkey getMostActive(Monkey ... except) {
        Monkey result = null;
        top: for (Monkey monkey : monkeys.values()) {
            for (Monkey m : except) {
                if (monkey == m) {
                    continue top;
                }
            }
            if (result == null || result.inspectionCounter < monkey.inspectionCounter) {
                result = monkey;
            }
        }
        return result;
    }

    private Operation getOperation(int monkeyNumber) {
        switch (monkeyNumber) {
            case 0:
                return old -> old * 13;
            case 1:
                return old -> old + 2;
            case 2:
                return old -> old + 6;
            case 3:
                return old -> old * old;
            case 4:
                return old -> old + 3;
            case 5:
                return old -> old * 7;
            case 6:
                return old -> old + 4;
            case 7:
                return old -> old + 7;
        }
        throw new RuntimeException();
    }

    class Monkey {
        List<Integer> items;
        Operation operation;
        int divideTest;
        int monkeyIfTrue;
        int monkeyIfFalse;

        int inspectionCounter = 0;

        public Monkey(List<Integer> items, Operation operation, int divideTest, int monkeyIfTrue, int monkeyIfFalse) {
            this.items = items;
            this.operation = operation;
            this.divideTest = divideTest;
            this.monkeyIfTrue = monkeyIfTrue;
            this.monkeyIfFalse = monkeyIfFalse;
        }

        void inspectAllItems() {
            for (Integer item : items) {
                inspectionCounter ++;
                int newNumber = operation.performOperation(item);
                newNumber = newNumber / 3;
                if (newNumber % divideTest == 0) {
                    monkeys.get(monkeyIfTrue).items.add(newNumber);
                } else {
                    monkeys.get(monkeyIfFalse).items.add(newNumber);
                }
            }
            items.clear();
        }
    }

    interface Operation {
        int performOperation(int old);
    }
}
