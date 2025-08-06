package org.xsx;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class HelloTask extends DefaultTask {
    private String message;

    @TaskAction
    public void greet() {
        System.out.println(message);
    }

    @Input
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}