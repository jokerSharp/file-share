package io.project.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceCommands {

    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String command;

    @Override
    public String toString() {
        return command;
    }

    public boolean equals(String command) {
        return this.command.equals(command);
    }
}
