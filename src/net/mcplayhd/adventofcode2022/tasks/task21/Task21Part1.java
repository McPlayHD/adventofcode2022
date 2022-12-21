package net.mcplayhd.adventofcode2022.tasks.task21;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.*;

public class Task21Part1 extends Task {
    private static final Map<String, Long> values = new HashMap<>();
    private static final Map<String, Set<Calculation>> calculationsWaitingForThisKey = new HashMap<>();

    @Override
    public String getResult(String[] input) {
        for (String line : input) {
            if (line.isEmpty())
                continue;
            String[] split = line.split(": ");
            String key = split[0];
            String rightSide = split[1];
            if (rightSide.matches("^[0-9]+$")) {
                long number = Long.parseLong(rightSide);
                values.put(key, number);
                checkWaiting(key);
            } else {
                String[] operationSplit = rightSide.split(" ");
                String keyLeft = operationSplit[0];
                Operation operation = Operation.parse(operationSplit[1].toCharArray()[0]);
                String keyRight = operationSplit[2];
                Calculation calculation = new Calculation(key, keyLeft, operation, keyRight);
                if (!calculation.canCalculate()) {
                    if (!values.containsKey(keyLeft)) {
                        calculationsWaitingForThisKey.computeIfAbsent(keyLeft, s -> new HashSet<>()).add(calculation);
                    }
                    if (!values.containsKey(keyRight)) {
                        calculationsWaitingForThisKey.computeIfAbsent(keyRight, s -> new HashSet<>()).add(calculation);
                    }
                } else {
                    calculation.calculate();
                }
            }
        }
        return Long.toString(values.get("root"));
    }

    static void checkWaiting(String key) {
        Set<Calculation> waiting = calculationsWaitingForThisKey.remove(key);
        if (waiting != null) {
            for (Calculation calculation : waiting) {
                if (calculation.canCalculate()) {
                    calculation.calculate();
                }
            }
        }
    }

    static class Calculation {
        String key;
        String leftKey;
        Operation operation;
        String rightKey;

        public Calculation(String key, String leftKey, Operation operation, String rightKey) {
            this.key = key;
            this.leftKey = leftKey;
            this.operation = operation;
            this.rightKey = rightKey;
        }

        boolean canCalculate() {
            return values.containsKey(leftKey) && values.containsKey(rightKey);
        }

        void calculate() {
            long value = operation.operationHandler.calculate(values.get(leftKey), values.get(rightKey));
            values.put(key, value);
            checkWaiting(key);
        }
    }

    enum Operation {
        ADD('+', Long::sum),
        SUBTRACT('-', (a, b) -> a - b),
        MULTIPLY('*', (a, b) -> a * b),
        DIVIDE('/', (a, b) -> a / b);

        final char c;
        final OperationHandler operationHandler;

        Operation(char c, OperationHandler operationHandler) {
            this.c = c;
            this.operationHandler = operationHandler;
        }

        public static Operation parse(char c) {
            for (Operation operation : values()) {
                if (operation.c == c) {
                    return operation;
                }
            }
            throw new RuntimeException("Unknown operation '" + c + "'");
        }
    }

    interface OperationHandler {
        long calculate(long a, long b);
    }
}
