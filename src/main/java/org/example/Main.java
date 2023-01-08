package org.example;

import org.example.exceptions.CyclicDependencyException;
import org.example.program.FileProcessor;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private final static String instruction = """
            Please enter the absolute path to the directory with files you want to process.
            Example: "C:\\programming\\Java\\FileTask\\test".
            Then enter output filepath.
            Example: "C:\\programming\\Java\\FileTask\\res\\result.txt"
            """;
    public static void main(String[] args) {
        System.out.println(instruction);
        Scanner scanner = new Scanner(System.in);
        String directory = scanner.nextLine();
        String result = scanner.nextLine();
        File dir = new File(directory);
        File res = new File(result);
        if (dir.exists() && dir.isDirectory() && res.exists() && res.canWrite()) {
            FileProcessor program = new FileProcessor(dir.toPath(), res.toPath());
            try {
                program.run();
            } catch (IOException | CyclicDependencyException exception) {
                System.out.println(exception.getMessage());
            }
        } else {
            if (!dir.exists()) {
                System.out.printf("directory %s doesn't exist\n", dir.toPath());
            } else if (!dir.isDirectory()) {
                System.out.printf("%s isn't a directory\n", dir.toPath());
            }
            if (!res.exists()) {
                System.out.printf("directory %s doesn't exist\n", res.toPath());
            } else if (!res.exists()) {
                System.out.printf("can't write into %s\n", res.toPath());
            }
        }
    }
}

/*
C:\programming\Java\FileTask\test
C:\programming\Java\FileTask\res\result.txt
* */