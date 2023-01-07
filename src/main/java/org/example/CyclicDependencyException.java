package org.example;

import java.util.ArrayList;

public class CyclicDependencyException extends Exception{
    String message;
    CyclicDependencyException(ArrayList<String> files) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cycle in these files:\n");
        for (String cycled_files : files) {
            stringBuilder.append(cycled_files);
            stringBuilder.append('\n');
        }
        message = stringBuilder.toString();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
