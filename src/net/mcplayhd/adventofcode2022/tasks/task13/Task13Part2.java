package net.mcplayhd.adventofcode2022.tasks.task13;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Task13Part2 extends Task {
    @Override
    public String getResult(String[] input) {
        List<NumberList> all = new ArrayList<>();
        NumberList divider1 = new NumberList();
        fillNumberList(divider1, "[[2]]");
        all.add(divider1);
        NumberList divider2 = new NumberList();
        fillNumberList(divider2, "[[6]]");
        all.add(divider2);
        for (int index = 0, pairIndex = 1; index < input.length; index += 3, pairIndex ++) {
            NumberList left = new NumberList();
            fillNumberList(left, input[index]);
            NumberList right = new NumberList();
            fillNumberList(right, input[index + 1]);
            all.add(left);
            all.add(right);
        }
        Collections.sort(all);
        int index1 = all.indexOf(divider1) + 1;
        int index2 = all.indexOf(divider2) + 1;
        return Integer.toString(index1 * index2);
    }

    void fillNumberList(NumberList addTo, String input) {
        char[] chars = input.toCharArray();
        NumberList list = new NumberList();
        int startIndexOfSublist = -1;
        int foundOpenInBetween = 0;
        int foundClosedInBetween = 0;
        for (int index = 1; index < chars.length - 1; index ++) {
            if (chars[index] == '[') {
                if (startIndexOfSublist == -1) {
                    startIndexOfSublist = index;
                } else {
                    foundOpenInBetween ++;
                }
            } else if (chars[index] == ']') {
                if (foundOpenInBetween == foundClosedInBetween) {
                    String passOn = input.substring(startIndexOfSublist, index + 1);
                    fillNumberList(list, passOn);
                    startIndexOfSublist = -1;
                    foundOpenInBetween = 0;
                    foundClosedInBetween = 0;
                } else {
                    foundClosedInBetween ++;
                }
            } else if (isNumber(chars[index]) && startIndexOfSublist == -1) {
                int numberStart = index;
                while (isNumber(chars[++index]));
                int number = Integer.parseInt(input.substring(numberStart, index));
                list.list.add(new NumberEntry(number));
            }
        }
        addTo.list.add(list);
    }

    boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    static abstract class Listable implements Comparable<Listable> {
        abstract CompareResult compare(Listable other);

        @Override
        public int compareTo(Listable o) {
            switch (compare(o)) {
                case SMALLER:
                    return -1;
                case SAME:
                    return 0;
                case BIGGER:
                    return 1;
            }
            throw new RuntimeException("wtf");
        }
    }

    static class NumberList extends Listable {
        List<Listable> list = new ArrayList<>();

        @Override
        public String toString() {
            return "[" + list.stream().map(Object::toString).collect(Collectors.joining(",")) + "]";
        }

        @Override
        CompareResult compare(Listable other) {
            if (other instanceof NumberList) {
                int index = 0;
                NumberList otherList = (NumberList) other;
                for (; index < Math.min(list.size(), otherList.list.size()); index ++) {
                    Listable left = list.get(index);
                    Listable right = otherList.list.get(index);
                    CompareResult result = left.compare(right);
                    if (result != CompareResult.SAME) {
                        return result;
                    }
                }
                if (list.size() < otherList.list.size())
                    return CompareResult.SMALLER;
                if (list.size() > otherList.list.size())
                    return CompareResult.BIGGER;
                return CompareResult.SAME;
            } else {
                NumberList numberList = new NumberList();
                numberList.list.add(other);
                return this.compare(numberList);
            }
        }
    }

    static class NumberEntry extends Listable {
        int number;

        public NumberEntry(int number) {
            this.number = number;
        }

        @Override
        public String toString() {
            return "" + number;
        }

        @Override
        CompareResult compare(Listable other) {
            if (other instanceof NumberEntry) {
                NumberEntry otherNumber = (NumberEntry) other;
                if (number < otherNumber.number)
                    return CompareResult.SMALLER;
                if (number > otherNumber.number)
                    return CompareResult.BIGGER;
                return CompareResult.SAME;
            } else {
                NumberList list = new NumberList();
                list.list.add(this);
                return list.compare(other);
            }
        }
    }

    enum CompareResult {
        SMALLER,
        SAME,
        BIGGER
    }
}
