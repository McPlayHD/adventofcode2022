package net.mcplayhd.adventofcode2022.tasks.task21;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Task21Part2 extends Task {
    private static final Map<String, Long> values = new HashMap<>();
    private static final Map<String, Set<Calculation>> calculationsWaitingForThisKey = new HashMap<>();
    private static final Map<String, Calculation> calculationByKey = new HashMap<>();

    private static Calculation root;

    @Override
    public String getResult(String[] input) {
        for (String line : input) {
            if (line.isEmpty())
                continue;
            String[] split = line.split(": ");
            String key = split[0];
            if (key.equals("humn"))
                continue;
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
                calculationByKey.put(key, calculation);
                if (key.equals("root")) {
                    root = calculation;
                    root.operation = Operation.EQUALS;
                }
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
        // printing for debug
        printWaiting();
        // root is equation so make equal
        root.makeEqual();
        // now we should be left with less noise
        printWaiting();
        return Long.toString(values.get("humn"));
    }

    static void printWaiting() {
        System.out.println("Waiting: ");
        Set<Calculation> allWaiting = new HashSet<>();
        for (Set<Calculation> entry : calculationsWaitingForThisKey.values()) {
            allWaiting.addAll(entry);
        }
        for (Calculation calculation : allWaiting) {
            System.out.println(calculation);
        }
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
        Calculation backwards = calculationByKey.get(key);
        if (backwards != null) {
            backwards.resultHasToBe(values.get(key));
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
            if (operation == Operation.EQUALS)
                return;
            long value = operation.operationHandler.calculate(values.get(leftKey), values.get(rightKey));
            values.put(key, value);
            checkWaiting(key);
        }

        void resultHasToBe(long result) {
            if (values.containsKey(leftKey) && values.containsKey(rightKey))
                return;
            if (values.containsKey(leftKey)) {
                long left = values.get(leftKey);
                values.put(rightKey, operation.reverseOperationHandler.getBIfHaveA(left, result));
                checkWaiting(rightKey);
                return;
            }
            if (values.containsKey(rightKey)) {
                long right = values.get(rightKey);
                values.put(leftKey, operation.reverseOperationHandler.getAIfHaveB(right, result));
                checkWaiting(leftKey);
                return;
            }
            throw new RuntimeException("Both sides are unknown and I'm too stupid to know what to do now.");
        }

        void makeEqual() {
            if (values.containsKey(leftKey) && values.containsKey(rightKey))
                throw new RuntimeException("Both sides already computed.");
            if (values.containsKey(leftKey)) {
                long left = values.get(leftKey);
                values.put(rightKey, left);
                checkWaiting(rightKey);
                return;
            }
            if (values.containsKey(rightKey)) {
                long right = values.get(rightKey);
                values.put(leftKey, right);
                checkWaiting(leftKey);
                return;
            }
            throw new RuntimeException("Both sides are unknown and I'm too stupid to know what to do now.");
        }

        @Override
        public String toString() {
            String left = values.containsKey(leftKey) ? Long.toString(values.get(leftKey)) : leftKey;
            String right = values.containsKey(rightKey) ? Long.toString(values.get(rightKey)) : rightKey;
            return key + ": " + left + " " + operation.c + " " + right;
        }
    }

    enum Operation {
        ADD('+', Long::sum, new ReverseOperationHandler() {
            @Override
            public long getBIfHaveA(long a, long result) {
                return result - a;
            }

            @Override
            public long getAIfHaveB(long b, long result) {
                return result - b;
            }
        }),
        SUBTRACT('-', (a, b) -> a - b, new ReverseOperationHandler() {
            @Override
            public long getBIfHaveA(long a, long result) {
                return a - result;
            }

            @Override
            public long getAIfHaveB(long b, long result) {
                return result + b;
            }
        }),
        MULTIPLY('*', (a, b) -> a * b, new ReverseOperationHandler() {
            @Override
            public long getBIfHaveA(long a, long result) {
                return result / a;
            }

            @Override
            public long getAIfHaveB(long b, long result) {
                return result / b;
            }
        }),
        DIVIDE('/', (a, b) -> a / b, new ReverseOperationHandler() {
            @Override
            public long getBIfHaveA(long a, long result) {
                return a / result;
            }

            @Override
            public long getAIfHaveB(long b, long result) {
                return b * result;
            }
        }),
        EQUALS('=', null, null);

        final char c;
        final OperationHandler operationHandler;
        final ReverseOperationHandler reverseOperationHandler;

        Operation(char c, OperationHandler operationHandler, ReverseOperationHandler reverseOperationHandler) {
            this.c = c;
            this.operationHandler = operationHandler;
            this.reverseOperationHandler = reverseOperationHandler;
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

    interface ReverseOperationHandler {
        long getBIfHaveA(long a, long result);
        long getAIfHaveB(long b, long result);
    }
}
