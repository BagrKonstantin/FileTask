package org.example;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;

public class MyFile {
    ArrayList<Path> dependencies;
    String data;
    Path filepath;
    int dependenciesNumber;
    boolean wasWritten;
    MyFile(Path filepath) {
        try(FileReader fr = new FileReader(filepath.toFile())) {

        } catch (Exception e) {

        }
    }
}
