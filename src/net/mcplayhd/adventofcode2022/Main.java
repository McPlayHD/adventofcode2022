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
        Scanner scanner = new Scanner(System.in);
        System.out.print("Which task should be performed? ");
        int taskNumber = Integer.parseInt(scanner.nextLine().replaceAll("[^0-9]", ""));
        File inputFile = new File("input/" + taskNumber + ".txt");
        if (!inputFile.exists()) {
            System.err.println("Input file " + inputFile.getPath() + " not found.");
            return;
        }
        String input = FileHelper.readFile(inputFile);
        Task task = createTask(taskNumber);
        String output = task.getResult(input);
        File outputFile = new File("output/" + taskNumber + ".txt");
        FileHelper.writeFile(outputFile, output);
    }

    private static Task createTask(int task) {
        try {
            Class<?> clazz = Class.forName("net.mcplayhd.adventofcode2022.tasks.Task" + task);
            Constructor<?> ctor = clazz.getConstructor();
            return (Task) ctor.newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}