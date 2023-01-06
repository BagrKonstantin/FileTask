package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyFile implements Comparable {
    ArrayList<Path> dependencies;
    String data;
    Path filepath;
    String name;
    Color color;
    boolean wasWritten;
    static Pattern pattern = Pattern.compile("require ‘(?<path>.*)’");
    MyFile(Path filepath) {
        wasWritten = false;
        color = Color.BLANK;
        dependencies = new ArrayList<>();
        this.filepath = filepath;
        name = filepath.getFileName().toString();
        StringBuilder stringBuilder = new StringBuilder();

        try (Scanner file_scanner = new Scanner(filepath.toFile())) {
            while (file_scanner.hasNextLine()) {
                String line = file_scanner.nextLine();
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    dependencies.add(Path.of(Program.root.toString(), matcher.group("path")));
                }
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
            data = stringBuilder.toString();
        } catch (IOException e) {
            System.out.printf("Can't read %s", filepath);
        }
    }

    @Override
    public String toString() {
        return this.data;
    }

    @Override
    public int compareTo(Object o) {
        return name.compareTo(((MyFile)o).name);
    }
}
