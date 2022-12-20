package net.mcplayhd.adventofcode2022.tasks.task20;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Task20Part1 extends Task {
    @Override
    public String getResult(String[] input) {
        List<Entry<Integer>> initialOrder = new ArrayList<>();
        Entry<Integer> first = null;
        Entry<Integer> last = null;
        Entry<Integer> zero = null;
        for (String line : input) {
            if (line.isEmpty())
                continue;
            int number = Integer.parseInt(line);
            Entry<Integer> entry = new Entry<>(number);
            if (number == 0) {
                zero = entry;
            }
            if (first == null) {
                first = entry;
            }
            if (last != null) {
                entry.last = last;
                last.next = entry;
            }
            last = entry;
            initialOrder.add(entry);
        }
        // well should never happen
        assert first != null;
        // connect ring
        first.last = last;
        last.next = first;
        // create sequence
        Sequence<Integer> sequence = new Sequence<>(zero, initialOrder.size());
        // move entries;
        for (Entry<Integer> entry : initialOrder) {
            int moveAmount = entry.value;
            int ringLength = sequence.length - 1;
            if (moveAmount > sequence.length) {
                moveAmount -= ringLength * (moveAmount / ringLength);
            }
            if (moveAmount < -sequence.length) {
                moveAmount += ringLength * (-moveAmount / ringLength);
            }
            entry.move(moveAmount);
        }
        // get 1000th, 2000th and 3000th
        int number1000 = sequence.get(1000);
        int number2000 = sequence.get(2000);
        int number3000 = sequence.get(3000);
        // calculate sum
        int sum = number1000 + number2000 + number3000;
        // return result
        return Integer.toString(sum);
    }

    static class Sequence<V> {
        Entry<V> zero;
        int length;

        public Sequence(Entry<V> zero, int length) {
            this.zero = zero;
            this.length = length;
        }

        V get(int index) {
            return zero.get(index % length);
        }
    }

    static class Entry<V> {
        Entry<V> last, next;
        V value;

        public Entry(V value) {
            this.value = value;
        }

        void move(int amount) {
            if (amount < 0) {
                last.last.next = this;
                next.last = last;
                Entry<V> tmpLast = last.last;
                last.next = next;
                last.last = this;
                this.next = last;
                this.last = tmpLast;
                move(amount + 1);
            } else if (amount > 0) {
                next.next.last = this;
                last.next = next;
                Entry<V> tmpNext = next.next;
                next.last = last;
                next.next = this;
                this.last = next;
                this.next = tmpNext;
                move(amount - 1);
            }
        }

        V get(int offset) {
            if (offset > 0) {
                return next.get(offset - 1);
            }
            if (offset < 0) {
                return last.get(offset + 1);
            }
            return value;
        }
    }
}
