package org.snp.telegraminputservice.util;

import org.telegram.telegrambots.meta.api.objects.Message;

public class TelegramUserUtil {

    private TelegramUserUtil() {
    }

    public static String resolveUserName(Message message) {
        if (message == null || message.getFrom() == null) {
            return "Unknown user";
        }

        String userName = message.getFrom().getUserName();
        if (userName != null && !userName.isBlank()) {
            return userName;
        }
        String firstName = message.getFrom().getFirstName();
        String lastName = message.getFrom().getLastName();
        if (!firstName.isBlank()) {
            return firstName + (lastName != null && !lastName.isBlank() ? " " + lastName : "");
        }
        return "User-" + message.getFrom().getId();
    }
}
