package org.snp.telegraminputservice.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {

    public static ReplyKeyboardMarkup mainMenuKeyboard() {
        return createReplyKeyboard(List.of(
                List.of("Сохранить данные о давлении"),
                List.of("Получить данные о давлении"),
                List.of("Старт", "Помощь")
        ));
    }

    public static ReplyKeyboardMarkup pressureSubMenuKeyboard() {
        return createReplyKeyboard(List.of(
                List.of("За дату"),
                List.of("За последние N дней"),
                List.of("Все записи"),
                List.of("Главное меню", "Помощь")
        ));
    }

    public static ReplyKeyboardMarkup sendMenuKeyboard() {
        return createReplyKeyboard(List.of(
                List.of("СОХРАНИТЬ"),
                List.of("Исправить данные", "Главное меню")
        ));
    }

    public static ReplyKeyboardMarkup goToMainMenuKeyboard() {
        return createReplyKeyboard(List.of(
                List.of("Главное меню")
        ));
    }

    public static ReplyKeyboardMarkup goBackOrMainMenuKeyboard() {
        return createReplyKeyboard(List.of(
                List.of("Назад", "Главное меню")
        ));
    }

    public static ReplyKeyboardMarkup receiveMenuKeyboard() {
        return createReplyKeyboard(List.of(
                List.of("Сообщение", "PDF"),
                List.of("Главное меню", "Помощь")
        ));
    }

    public static ReplyKeyboardMarkup pdfMenuKeyboard() {
        return createReplyKeyboard(List.of(
                List.of("За последние 7 дней"),
                List.of("За последние 14 дней"),
                List.of("За последние 30 дней"),
                List.of("Назад", "Главное меню")
        ));
    }

    public static ReplyKeyboardMarkup wakeUpKeyboard() {
        return createReplyKeyboard(List.of(
                List.of("РАЗБУДИТЬ СЕРВИСЫ")
        ));
    }

    private static ReplyKeyboardMarkup createReplyKeyboard(List<List<String>> buttonRows) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        for (List<String> row : buttonRows) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.addAll(row);
            keyboard.add(keyboardRow);
        }
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        return keyboardMarkup;
    }
}
