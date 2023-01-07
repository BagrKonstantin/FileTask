package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyFile implements Comparable {
    private ArrayList<Path> dependencies = new ArrayList<>();
    private String data;
    private final Path filepath;
    private final String name;
    private Color color = Color.BLANK;
    private boolean written = false;
    private static final Pattern pattern = Pattern.compile("require ‘(?<path>.*)’");

     private static String root;
    MyFile(Path filepath) throws IOException {
        this.filepath = filepath;
        name = filepath.getFileName().toString();
        scanFile();
    }

    private void scanFile() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (Scanner file_scanner = new Scanner(filepath.toFile())) {
            while (file_scanner.hasNextLine()) {
                String line = file_scanner.nextLine();
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    dependencies.add(Path.of(root, matcher.group("path")));
                }
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
            data = stringBuilder.toString();
        } catch (IOException e) {
            throw new IOException(String.format("Can't read %s", filepath));
        }
    }

    public Path getFilepath() {
        return filepath;
    }

    public ArrayList<Path> getDependencies() {
        return dependencies;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean wasWritten() {
        return written;
    }

    public void setWritten(boolean written) {
        this.written = written;
    }

    public static void setRoot(String root) {
        MyFile.root = root;
    }

    public String getName() {
        return name;
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
