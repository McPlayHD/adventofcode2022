package net.mcplayhd.adventofcode2022;

import net.mcplayhd.adventofcode2022.helpers.FileHelper;
import net.mcplayhd.adventofcode2022.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        // preparation
        Scanner scanner = new Scanner(System.in);
        System.out.print("Which task should be performed? ");
        int taskNumber = Integer.parseInt(scanner.nextLine().replaceAll("[^0-9]", ""));
        File inputFile = new File("input/" + taskNumber + ".txt");
        if (!inputFile.exists()) {
            System.err.println("Input file " + inputFile.getPath() + " not found.");
            return;
        }
        System.out.print("Which part should be performed? ");
        int partNumber = Integer.parseInt(scanner.nextLine().replaceAll("[^0-9]", ""));
        File outputFile = new File("output/" + taskNumber + "_" + partNumber + ".txt");
        // performing the task
        String[] input = FileHelper.getLines(inputFile);
        Task task = createTask(taskNumber, partNumber);
        String output = task.getResult(input);
        // output
        FileHelper.writeFile(outputFile, output);
        System.out.println(output);
    }

    private static Task createTask(int task, int part) {
        try {
            Class<?> clazz = Class.forName("net.mcplayhd.adventofcode2022.tasks.task" + task + ".Task" + task + "Part" + part);
            Constructor<?> constructor = clazz.getConstructor();
            return (Task) constructor.newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}