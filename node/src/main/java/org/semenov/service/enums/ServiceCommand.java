package org.semenov.service.enums;
/*
 * Набор сервисных комманд для View Телеграм
 */

public enum ServiceCommand {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;


    ServiceCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommand fromValue(String incomeValue) {
        for (ServiceCommand command : ServiceCommand.values()) {
            if (command.value.equals(incomeValue)) {
                return command;
            }
        }
        return null;
    }
}
