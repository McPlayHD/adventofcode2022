package net.mcplayhd.adventofcode2022.tasks.task5;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Task5Part2 extends Task {
    @Override
    public String getResult(String[] input) {
        List<char[]> charLists = new ArrayList<>();
        List<Instruction> instructions = new ArrayList<>();
        for (String line : input) {
            if (line.contains("[")) {
                charLists.add(parseStackLine(line));
            } else if (line.startsWith("move")) {
                instructions.add(parseInstructionLine(line));
            }
        }
        Port port = new Port(charLists);
        for (Instruction instruction : instructions) {
            port.performInstruction(instruction);
        }
        return port.getTopCrateIds();
    }

    private char[] parseStackLine(String line) {
        line += " "; // so that it is dividable through 4
        int amount = line.length() / 4;
        char[] result = new char[amount];
        for (int index = 0; index < amount; index++) {
            int lower = index * 4;
            int upper = (index + 1) * 4;
            String part = line.substring(lower, upper);
            part = part.replaceAll("[^A-Z]", "");
            result[index] = part.isEmpty() ? '\0' : part.toCharArray()[0];
        }
        return result;
    }

    private Instruction parseInstructionLine(String line) {
        String[] split = line.split(" ");
        int amount = Integer.parseInt(split[1]);
        int from = Integer.parseInt(split[3]);
        int to = Integer.parseInt(split[5]);
        return new Instruction(amount, from, to);
    }

    static class Port {
        List<Stack<Character>> stacks = new ArrayList<>();

        public Port(List<char[]> charLists) {
            for (int index = 0; index < charLists.get(0).length; index++) {
                stacks.add(new Stack<>());
            }
            for (int row = charLists.size() - 1; row >= 0; row--) {
                char[] chars = charLists.get(row);
                for (int col = 0; col < chars.length; col++) {
                    char c = chars[col];
                    if (c == '\0')
                        continue;
                    stacks.get(col).add(c);
                }
            }
        }

        public void performInstruction(Instruction instruction) {
            int amount = instruction.amount;
            int from = instruction.from - 1;
            int to = instruction.to - 1;
            Stack<Character> temp = new Stack<>();
            for (int count = 0; count < amount; count++) {
                temp.add(stacks.get(from).pop());
            }
            for (int count = 0; count < amount; count++) {
                stacks.get(to).add(temp.pop());
            }
        }

        public String getTopCrateIds() {
            char[] row = new char[stacks.size()];
            for (int col = 0; col < stacks.size(); col++) {
                row[col] = stacks.get(col).peek();
            }
            return new String(row);
        }
    }

    static class Instruction {
        int amount;
        int from;
        int to;

        public Instruction(int amount, int from, int to) {
            this.amount = amount;
            this.from = from;
            this.to = to;
        }
    }
}
