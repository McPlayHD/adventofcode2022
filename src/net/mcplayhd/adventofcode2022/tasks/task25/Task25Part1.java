package net.mcplayhd.adventofcode2022.tasks.task25;

import net.mcplayhd.adventofcode2022.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class Task25Part1 extends Task {
    static Map<Character, Integer> snafuToFives = new HashMap<>();
    static Map<Integer, SNAFUConversion> fivesToSnafu = new HashMap<>();

    static {
        snafuToFives.put('=', -2);
        snafuToFives.put('-', -1);
        snafuToFives.put('0', 0);
        snafuToFives.put('1', 1);
        snafuToFives.put('2', 2);
        fivesToSnafu.put(0, new SNAFUConversion('0', 0));
        fivesToSnafu.put(1, new SNAFUConversion('1', 0));
        fivesToSnafu.put(2, new SNAFUConversion('2', 0));
        fivesToSnafu.put(3, new SNAFUConversion('=', 1));
        fivesToSnafu.put(4, new SNAFUConversion('-', 1));
    }

    @Override
    public String getResult(String[] input) {
        long sum = 0;
        for (String snafu : input) {
            if (snafu.isEmpty()) {
                continue;
            }
            long decimal = snafuToDecimal(snafu);
            System.out.println(snafu + " -> " + decimal);
            sum += decimal;
        }
        System.out.println("Sum: " + sum);
        String snafu = decimalToSnafu(sum);
        System.out.println("SNAFU: " + snafu);
        return snafu;
    }

    long snafuToDecimal(String snafu) {
        long decimal = 0;
        char[] chars = snafu.toCharArray();
        for (int exponent = 0; exponent < chars.length; exponent ++) {
            char c = chars[snafu.length() - 1 - exponent];
            int fives = snafuToFives.get(c);
            long multiply = (long) Math.pow(5, exponent);
            decimal += fives * multiply;
        }
        return decimal;
    }

    String decimalToSnafu(long decimal) {
        StringBuilder snafu = new StringBuilder();
        while (decimal > 0) {
            int fives = (int) (decimal % 5);
            SNAFUConversion conversion = fivesToSnafu.get(fives);
            snafu.insert(0, conversion.snafuChar);
            decimal /= 5;
            decimal += conversion.addToNext;
        }
        return snafu.toString();
    }

    static class SNAFUConversion {
        char snafuChar;
        int addToNext;

        public SNAFUConversion(char snafuChar, int addToNext) {
            this.snafuChar = snafuChar;
            this.addToNext = addToNext;
        }
    }
}
