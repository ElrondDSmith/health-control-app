package org.snp.telegraminputservice.handler;

import lombok.RequiredArgsConstructor;
import org.snp.telegraminputservice.common.RequestResult;
import org.snp.telegraminputservice.dto.PressureRecordDtoRq;
import org.snp.telegraminputservice.dto.PressureRecordDtoRs;
import org.snp.telegraminputservice.dto.UserRegDto;
import org.snp.telegraminputservice.formatter.PressureMessageFormatter;
import org.snp.telegraminputservice.keyboard.KeyboardFactory;
import org.snp.telegraminputservice.model.UserSession;
import org.snp.telegraminputservice.model.UserState;
import org.snp.telegraminputservice.service.HealthDataClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramCommandHandler {

    private final HealthDataClient healthDataClient;
    private final PressureMessageFormatter pressureMessageFormatter;

    private static final Integer MIN_SYS = 40;
    private static final Integer MAX_SYS = 300;
    private static final Integer MIN_DIA = 30;
    private static final Integer MAX_DIA = 200;
    private static final Integer MIN_PULSE = 20;
    private static final Integer MAX_PULSE = 300;
    private static final String START_MESSAGE = """
            Привет! добро пожаловать в бот контроля за давлением.
            Выберите команду из меню:
            """;
    private static final String MAIN_MENU_HELP_MESSAGE =
            String.format("Описание команд:\n" +
                    "\nОтправить данные о давлении - " +
                    "отправляет и сохраняет данные о вашем давлении (временем и датой измерения считается дата отправки результатов)\n" +
                    "допустимые значения:\n" +
                    "   🔼 систолическое: %d - %d\n" +
                    "   🔽 диастолическое: %d - %d\n" +
                    "   ❤️ пульс: %d - %d\n", MIN_SYS, MAX_SYS, MIN_DIA, MAX_DIA, MIN_PULSE, MAX_PULSE) +
                    "\nПолучить данные о давлении - переводит в меню выбора получения информации о давлении\n" +
                    "\nСтарт - регистрация в приложении\n" +
                    "\nПомощь - информация о командах";
    private static final String PRESSURE_MENU_HELP_MESSAGE = """
            Описание команд:
            \nЗа дату - показывает информацию о давлении за выбранную дату.
            \nЗа последние N дней - показывает информацию за выбранное количество дней, начиная с сегодня.
            \nВсе записи - показывает все записи о давлении для пользователя.
            \nГлавное меню - возврат в главное меню.
            """;

    public BotApiMethod<?> handleMainMenuCommand(Message message, UserSession session) {
        Long chatId = message.getChatId();
        String text = message.getText();
        Long userId = message.getFrom().getId();
        String userName = message.getFrom().getUserName();

        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "");

        if (session.getUserState() != UserState.NONE) {
            return null;
        }

        switch (text) {
            case "/start", "Старт" -> {
                sendMessage.setReplyMarkup(KeyboardFactory.mainMenuKeyboard());
                session.setUserState(UserState.NONE);
                UserRegDto userRegDto = new UserRegDto(userId, userName);
                RequestResult<String> result = healthDataClient.registerUser(userRegDto);
                if (result.isSuccess()) {
                    sendMessage.setText(START_MESSAGE);
                } else {
                    sendMessage.setText("Ошибка при регистрации пользователя.");
                }
            }
            case "/help", "Помощь" -> sendMessage.setText(MAIN_MENU_HELP_MESSAGE);
            case "Отправить данные о давлении" -> {
                session.setUserState(UserState.WAITING_FOR_SYSTOLIC);
                sendMessage.setReplyMarkup(KeyboardFactory.goToMainMenuKeyboard());
                session.setUserId(userId);
                session.setUserName(userName);
                sendMessage.setText("🔼 Введите верхнее давление (систолическое):");
            }
            case "Получить данные о давлении" -> toPressureMenu(sendMessage, session);
            default -> sendMessage.setText("Неизвестная команда, используйте /start для вызова меню");
        }
        return sendMessage;
    }

    public BotApiMethod<?> handlePressureMenuCommand(Message message, UserSession session) {
        Long chatId = message.getChatId();
        String text = message.getText();
        Long userId = message.getFrom().getId();

        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "");

        if (session.getUserState() != UserState.PRESSURE_MENU) {
            return null;
        }
        switch (text) {
            case "За дату" -> {
                sendMessage.setText("Введите дату в формате ДД.ММ.ГГГГ (пример 01.01.2025)");
                sendMessage.setReplyMarkup(KeyboardFactory.goBackOrMainMenuKeyboard());
                session.setUserState(UserState.WAITING_FOR_DATE);
            }
            case "За последние N дней" -> {
                sendMessage.setText("Введите количество дней:");
                sendMessage.setReplyMarkup(KeyboardFactory.goBackOrMainMenuKeyboard());
                session.setUserState(UserState.WAITING_FOR_DAYS);
            }
            case "Все записи" -> {
                RequestResult<List<PressureRecordDtoRs>> result = healthDataClient.getAllById(userId);
                if (!result.isSuccess()) {
                    sendMessage.setText("⚠️ Не удалось получить записи: " + result.errorMessage());
                    goToMainMenu(sendMessage, session);
                    return sendMessage;
                }
                List<PressureRecordDtoRs> recordDtoRs = result.data();
                if (recordDtoRs == null || recordDtoRs.isEmpty()) {
                    sendMessage.setText("Записи отсутствуют.");
                    goToMainMenu(sendMessage, session);
                    return sendMessage;
                } else {
                    sendMessage.setText(pressureMessageFormatter.formatListOfRecords(result.data()));
                    goToMainMenu(sendMessage, session);
                }
            }
            case "Главное меню" -> {
                returnToMainMenu(sendMessage, session);
            }
            case "/help", "Помощь" -> sendMessage.setText(PRESSURE_MENU_HELP_MESSAGE);
            default ->
                    sendMessage.setText("выберите один из пунктов меню, или используйте /start для вызова главного меню");
        }
        return sendMessage;
    }

    public BotApiMethod<?> handleSystolicInput(Message message, UserSession session) {
        Long chatId = message.getChatId();
        String text = message.getText();

        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "");

        if (session.getUserState() != UserState.WAITING_FOR_SYSTOLIC) {
            return null;
        }

        if ("Главное меню".equals(text)) {
            returnToMainMenu(sendMessage, session);
            return sendMessage;
        } else {
            try {
                int systolic = Integer.parseInt(text);
                if (systolic < MIN_SYS || systolic > MAX_SYS) {
                    throw new NumberFormatException();
                }
                session.setSystolic(systolic);
                session.setUserState(UserState.WAITING_FOR_DIASTOLIC);
                sendMessage.setText("🔽 Введите нижнее давление (диастолическое):");
            } catch (NumberFormatException e) {
                sendMessage.setText("❌ Не корректный ввод.\n🔼 Пожалуйста введите верхнее давление (систолическое) еще раз:" +
                        String.format("\nМинимальное значение: %d\nМаксимальное значение: %d", MIN_SYS, MAX_SYS));
            }
        }
        return sendMessage;
    }

    public BotApiMethod<?> handleDiastolicInput(Message message, UserSession session) {
        Long chatId = message.getChatId();
        String text = message.getText();

        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "");

        if (session.getUserState() != UserState.WAITING_FOR_DIASTOLIC) {
            return null;
        }

        if ("Главное меню".equals(text)) {
            returnToMainMenu(sendMessage, session);
            return sendMessage;
        } else {
            try {
                int diastolic = Integer.parseInt(text);
                if (diastolic < MIN_SYS || diastolic > MAX_SYS) {
                    throw new NumberFormatException();
                }
                session.setDiastolic(diastolic);
                session.setUserState(UserState.WAITING_FOR_PULSE);
                sendMessage.setText("❤️ Введите пульс:");
            } catch (NumberFormatException e) {
                sendMessage.setText("❌ Не корректный ввод.\n🔽 Пожалуйста введите нижнее давление (диастолическое) еще раз:" +
                        String.format("\nМинимальное значение: %d\nМаксимальное значение: %d", MIN_DIA, MAX_DIA));
            }
        }
        return sendMessage;
    }

    public BotApiMethod<?> handlePulseInput(Message message, UserSession session) {
        Long chatId = message.getChatId();
        String text = message.getText();

        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "");

        if (session.getUserState() != UserState.WAITING_FOR_PULSE) {
            return null;
        }

        if ("Главное меню".equals(text)) {
            returnToMainMenu(sendMessage, session);
            return sendMessage;
        } else {
            try {
                int pulse = Integer.parseInt(text);
                if (pulse < MIN_PULSE || pulse > MAX_PULSE) {
                    throw new NumberFormatException();
                }
                session.setPulse(Integer.parseInt(text));
                session.setUserState(UserState.WAITING_FOR_SEND);
                sendMessage.setText(String.format("""
                        Проверьте введенные данные:
                        🔼 Верхнее давление: %s
                        🔽 Нижнее давление: %s
                        ❤️ Пульс: %s
                        Если данные верны, нажмите кнопку отправить.
                        """, session.getSystolic(), session.getDiastolic(), session.getPulse()));
                sendMessage.setReplyMarkup(KeyboardFactory.sendMenuKeyboard());
            } catch (NumberFormatException e) {
                sendMessage.setText("❌ Не корректный ввод.\n❤️ Пожалуйста введите пульс еще раз" +
                        String.format("\nМинимальное значение: %d\nМаксимальное значение: %d", MIN_PULSE, MAX_PULSE));
            }
        }
        return sendMessage;
    }

    public BotApiMethod<?> handleSendInput(Message message, UserSession session) {
        Long chatId = message.getChatId();
        String text = message.getText();
        Long userId = message.getFrom().getId();
        String userName = message.getFrom().getUserName();

        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "");

        if (session.getUserState() != UserState.WAITING_FOR_SEND) {
            return null;
        }
        switch (text) {
            case "ОТПРАВИТЬ" -> {
                sendMessage.setReplyMarkup(KeyboardFactory.mainMenuKeyboard());
                session.setUserState(UserState.NONE);
                RequestResult<String> result = healthDataClient.savePressure(pressureDtoCreate(session));
                if (result.isSuccess()) {
                    sendMessage.setText("Данные давления успешно отправлены");
                } else {
                    sendMessage.setText("Ошибка при отправке данных давления: " + result.errorMessage());
                }
            }
            case "Исправить данные" -> {
                session.setUserState(UserState.WAITING_FOR_SYSTOLIC);
                sendMessage.setReplyMarkup(KeyboardFactory.goBackOrMainMenuKeyboard());
                session.setUserId(userId);
                session.setUserName(userName);
                sendMessage.setText("🔼 Введите верхнее давление (систолическое):");
            }
            case "Главное меню" -> returnToMainMenu(sendMessage, session);
        }
        return sendMessage;
    }

    public BotApiMethod<?> handleDateInput(Message message, UserSession session) {
        Long chatId = message.getChatId();
        String text = message.getText();
        Long userId = message.getFrom().getId();

        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "");

        if (session.getUserState() != UserState.WAITING_FOR_DATE) {
            return null;
        }
        if ("Главное меню".equals(text)) {
            returnToMainMenu(sendMessage, session);
            return sendMessage;
        }
        if ("Назад".equals(text)) {
            toPressureMenu(sendMessage, session);
            return sendMessage;
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            try {
                LocalDate date = LocalDate.parse(text.trim(), formatter);
                RequestResult<List<PressureRecordDtoRs>> result = healthDataClient.getByIdAndDate(userId, date);
                if (!result.isSuccess()) {
                    sendMessage.setText("⚠️ Не удалось получить записи: " + result.errorMessage());
                    goToMainMenu(sendMessage, session);
                    return sendMessage;
                }
                List<PressureRecordDtoRs> recordDtoRs = result.data();
                if (recordDtoRs == null || recordDtoRs.isEmpty()) {
                    sendMessage.setText("За указанную дату записи отсутствуют.");
                    goToMainMenu(sendMessage, session);
                    return sendMessage;
                }
                sendMessage.setText(pressureMessageFormatter.formatListOfRecords(recordDtoRs));
                goToMainMenu(sendMessage, session);


            } catch (DateTimeParseException e) {
                sendMessage.setText("❌ Некорректный формат даты\nВведите дату в формате ДД.ММ.ГГГГ (пример 01.01.2025)");
            }
        }
        return sendMessage;
    }

    public BotApiMethod<?> handleDaysInput(Message message, UserSession session) {
        Long chatId = message.getChatId();
        String text = message.getText();
        Long userId = message.getFrom().getId();

        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "");

        if (session.getUserState() != UserState.WAITING_FOR_DAYS) {
            return null;
        }
        if ("Главное меню".equals(text)) {
            returnToMainMenu(sendMessage, session);
            return sendMessage;
        }
        if ("Назад".equals(text)) {
            toPressureMenu(sendMessage, session);
            return sendMessage;
        } else {
            try {
                int days = Integer.parseInt(text.trim());
                RequestResult<List<PressureRecordDtoRs>> result = healthDataClient.getByIdAndDays(userId, days);
                if (!result.isSuccess()) {
                    sendMessage.setText("⚠️ Не удалось получить записи: " + result.errorMessage());
                    goToMainMenu(sendMessage, session);
                    return sendMessage;
                }
                List<PressureRecordDtoRs> recordDtoRs = result.data();
                if (recordDtoRs == null || recordDtoRs.isEmpty()) {
                    sendMessage.setText("За указанные дни записи отсутствуют.");
                    goToMainMenu(sendMessage, session);
                    return sendMessage;
                } else {
                    sendMessage.setText(pressureMessageFormatter.formatListOfRecords(result.data()));
                    goToMainMenu(sendMessage, session);
                }
            } catch (NumberFormatException e) {
                sendMessage.setText("❌ Введите корректное положительное число дней, например: 3, 7 или 30.");
            }
        }
        return sendMessage;
    }

    private void goToMainMenu(SendMessage sendMessage, UserSession session) {
        sendMessage.setReplyMarkup(KeyboardFactory.mainMenuKeyboard());
        session.setUserState(UserState.NONE);
    }

    private void returnToMainMenu(SendMessage sendMessage, UserSession session) {
        goToMainMenu(sendMessage, session);
        sendMessage.setText("Вы вернулись в главное меню");
    }

    private void toPressureMenu(SendMessage sendMessage, UserSession session) {
        session.setUserState(UserState.PRESSURE_MENU);
        sendMessage.setReplyMarkup(KeyboardFactory.pressureSubMenuKeyboard());
        sendMessage.setText("Выберите действие:");
    }

    private PressureRecordDtoRq pressureDtoCreate(UserSession session) {
        PressureRecordDtoRq dto = new PressureRecordDtoRq();
        dto.setTgId(session.getUserId());
        dto.setUserName(session.getUserName());
        dto.setSystolicPressure(session.getSystolic());
        dto.setDiastolicPressure(session.getDiastolic());
        dto.setPulse(session.getPulse());
        dto.setTimestamp(LocalDateTime.now().toString());
        return dto;
    }
}