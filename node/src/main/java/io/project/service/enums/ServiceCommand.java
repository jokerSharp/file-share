package io.project.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ServiceCommand {

    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommand fromValue(String command) {
        return Arrays.stream(ServiceCommand.values())
                .filter(c -> c.getValue().equals(command))
                .findFirst()
                .orElse(null);
    }
}
