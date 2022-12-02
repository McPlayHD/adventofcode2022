package net.mcplayhd.adventofcode2022.tasks.task2;

import net.mcplayhd.adventofcode2022.tasks.Task;

public class Task2Part1 extends Task {
    @Override
    public String getResult(String[] input) {
        int points = 0;
        for (String line : input) {
            if (line.isEmpty())
                continue;
            char[] lineChars = line.toCharArray();
            Move opponentMove = Move.getFromChar(lineChars[0]);
            Move myMove = Move.getFromChar(lineChars[2]);
            assert myMove != null;
            points += myMove.pickPoints;
            Outcome outcome = myMove.getOutcome(opponentMove);
            assert outcome != null;
            points += outcome.outcomePoints;
        }
        return Integer.toString(points);
    }

    enum Move {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        final int pickPoints;

        Move(int pickPoints) {
            this.pickPoints = pickPoints;
        }

        Outcome getOutcome(Move opponentMove) {
            if (this == opponentMove) {
                return Outcome.DRAW;
            }
            switch (this) {
                case ROCK:
                    switch (opponentMove) {
                        case PAPER:
                            return Outcome.LOSS;
                        case SCISSORS:
                            return Outcome.WIN;
                    }
                    break;
                case PAPER:
                    switch (opponentMove) {
                        case ROCK:
                            return Outcome.WIN;
                        case SCISSORS:
                            return Outcome.LOSS;
                    }
                    break;
                case SCISSORS:
                    switch (opponentMove) {
                        case ROCK:
                            return Outcome.LOSS;
                        case PAPER:
                            return Outcome.WIN;
                    }
                    break;
            }
            return null;
        }

        static Move getFromChar(char c) {
            switch (c) {
                case 'A':
                case 'X':
                    return ROCK;
                case 'B':
                case 'Y':
                    return PAPER;
                case 'C':
                case 'Z':
                    return SCISSORS;
            }
            return null;
        }
    }

    enum Outcome {
        LOSS(0),
        DRAW(3),
        WIN(6);

        final int outcomePoints;

        Outcome(int outcomePoints) {
            this.outcomePoints = outcomePoints;
        }
    }
}
