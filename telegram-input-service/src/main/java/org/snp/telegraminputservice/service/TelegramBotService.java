package org.snp.telegraminputservice.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.snp.telegraminputservice.configuration.TelegramBotProperties;
import org.snp.telegraminputservice.dto.PressureRecordDtoRq;
import org.snp.telegraminputservice.dto.PressureRecordDtoRs;
import org.snp.telegraminputservice.dto.UserRegDto;
import org.snp.telegraminputservice.formatter.PressureMessageFormatter;
import org.snp.telegraminputservice.model.UserSession;
import org.snp.telegraminputservice.model.UserState;
import org.snp.telegraminputservice.provider.UrlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


@Slf4j
public class TelegramBotService implements WebhookBot {

//    private static final String USER_REG_URL = "http://localhost:8080/save_user";
//    private static final String BLOOD_PRESSURE_SAVE_URL = "http://localhost:8080/save";
//    private static final String GET_ALL_RECORDS_BY_ID_URL = "http://localhost:8080/user/{tgId}";
//    private static final String GET_RECORDS_BY_ID_AND_DATE = "http://localhost:8080/user/{tgId}/date?date={date}";
//    private static final String GET_RECORDS_BY_ID_AND_LAST_N_DAYS = "http://localhost:8080/user/{tgId}/number_of_days?numberOfDays={numberOfDays}";
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


    private final TelegramBotProperties telegramBotProperties;
    private final RestTemplate restTemplate;
    private final PressureMessageFormatter pressureMessageFormatter;
    private final UrlProvider urlProvider;
    private final Map<Long, UserSession> sessions = new HashMap<>();

    @Autowired
    public TelegramBotService(TelegramBotProperties telegramBotProperties,
                              RestTemplate restTemplate,
                              PressureMessageFormatter pressureMessageFormatter,
                              UrlProvider urlProvider) {
        this.telegramBotProperties = telegramBotProperties;
        this.restTemplate = restTemplate;
        this.pressureMessageFormatter = pressureMessageFormatter;
        this.urlProvider = urlProvider;
    }

    @PostConstruct
    public void init() {
        System.out.println("Bot is initializing...");
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }

    @Override
    public String getBotPath() {
        return telegramBotProperties.getWebhookPath();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            UserSession session = sessions.computeIfAbsent(chatId, id -> new UserSession());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));

            switch (session.getUserState()) {
                case NONE -> {
                    switch (messageText) {
                        case "/start", "Старт" -> {
                            sendMessage.setText(START_MESSAGE);
                            sendMessage.setReplyMarkup(mainMenuKeyboard());
                            UserRegDto userRegDto = new UserRegDto();
                            userRegDto.setTgId(update.getMessage().getFrom().getId());
                            userRegDto.setUserName(update.getMessage().getFrom().getUserName());
                            try {
                                restTemplate.postForEntity(urlProvider.getUserRegUrl(), userRegDto, Void.class); //заменить на postForObject и обработать json
                                log.info("Данные пользователя отправлены");
                            } catch (Exception e) {
                                log.error("Ошибка при отправке пользователя в сервис БД: " + e.getMessage());
                            }
                        }
                        case "/help", "Помощь" -> sendMessage.setText(MAIN_MENU_HELP_MESSAGE);
                        case "Отправить данные о давлении" -> {
                            session.setUserState(UserState.WAITING_FOR_SYSTOLIC);
                            sendMessage.setReplyMarkup(goToMenuKeyboard());
                            session.setUserId(update.getMessage().getFrom().getId());
                            session.setUserName(update.getMessage().getFrom().getUserName());
                            sendMessage.setText("🔼 Введите верхнее давление (систолическое):");
                        }
                        case "Получить данные о давлении" -> {
                            session.setUserState(UserState.PRESSURE_MENU);
                            sendMessage.setText("Выберите действие:");
                            sendMessage.setReplyMarkup(pressureSubMenuKeyboard());
                        }
                        case "Главное меню" -> {
                            sendMessage.setText("Вы вернулись в главное меню");
                            goToMainMenu(sendMessage, session);
                        }
                        default -> sendMessage.setText("Неизвестная команда, используйте /start для вызова меню");
                    }
                }
                case WAITING_FOR_SYSTOLIC -> {
                    if ("Главное меню".equals(messageText)) {
                        sendMessage.setText("Вы вернулись в главное меню");
                        goToMainMenu(sendMessage, session);
                    } else {
                        try {
                            int systolic = Integer.parseInt(messageText);
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
                }
                case WAITING_FOR_DIASTOLIC -> {
                    if ("Главное меню".equals(messageText)) {
                        sendMessage.setText("Вы вернулись в главное меню");
                        goToMainMenu(sendMessage, session);
                    } else {
                        try {
                            int diastolic = Integer.parseInt(messageText);
                            if (diastolic < MIN_DIA || diastolic > MAX_DIA) {
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
                }
                case WAITING_FOR_PULSE -> {
                    if ("Главное меню".equals(messageText)) {
                        sendMessage.setText("Вы вернулись в главное меню");
                        goToMainMenu(sendMessage, session);
                    } else {
                        try {
                            int pulse = Integer.parseInt(messageText);
                            if (pulse < MIN_PULSE || pulse > MAX_PULSE) {
                                throw new NumberFormatException();
                            }
                            session.setPulse(Integer.parseInt(messageText));
                            session.setUserState(UserState.WAITING_FOR_SEND);
                            sendMessage.setText(String.format("""
                                    Проверьте введенные данные:
                                    🔼 Верхнее давление: %s
                                    🔽 Нижнее давление: %s
                                    ❤️ Пульс: %s
                                    Если данные верны, нажмите кнопку отправить.
                                    """, session.getSystolic(), session.getDiastolic(), session.getPulse()));
                            sendMessage.setReplyMarkup(sendMenuKeyboard());
                        } catch (NumberFormatException e) {
                            sendMessage.setText("❌ Не корректный ввод.\n❤️ Пожалуйста введите пульс еще раз" +
                                    String.format("\nМинимальное значение: %d\nМаксимальное значение: %d", MIN_PULSE, MAX_PULSE));
                        }
                    }
                }
                case WAITING_FOR_SEND -> {
                    switch (messageText) {
                        case "ОТПРАВИТЬ" -> {
                            session.setUserState(UserState.NONE);
                            try {
                                restTemplate.postForEntity(urlProvider.getBloodPressureSaveUrl(), pressureDtoCreate(session), String.class);
                                log.info("Данные давления успешно отправлены; {}", pressureDtoCreate(session));
                            } catch (Exception e) {
                                log.error("Ошибка при отправке данных давления: {}", e.getMessage());
                            }
                            sendMessage.setText("Данные давления успешно отправлены");
                            goToMainMenu(sendMessage, session);
                        }
                        case "Исправить данные" -> {
                            session.setUserState(UserState.WAITING_FOR_SYSTOLIC);
                            sendMessage.setReplyMarkup(goToMenuKeyboard());
                            session.setUserId(update.getMessage().getFrom().getId());
                            session.setUserName(update.getMessage().getFrom().getUserName());
                            sendMessage.setText("Введите верхнее давление (систолическое):");
                        }
                        case "Главное меню" -> {
                            sendMessage.setText("Вы вернулись в главное меню");
                            goToMainMenu(sendMessage, session);
                        }
                    }
                }
                case PRESSURE_MENU -> {
                    switch (messageText) {
                        case "За дату" -> {
                            sendMessage.setText("Введите дату в формате ДД.ММ.ГГГГ (пример 01.01.2025)");
                            sendMessage.setReplyMarkup(goToMenuKeyboard());
                            session.setUserState(UserState.WAITING_FOR_DATE);
                        }
                        case "За последние N дней" -> {
                            sendMessage.setText("Введите количество дней:");
                            sendMessage.setReplyMarkup(goToMenuKeyboard());
                            session.setUserState(UserState.WAITING_FOR_DAYS);
                        }
                        case "Все записи" -> {
                            sendMessage.setText(pressureMessageFormatter.formatListOfRecords(
                                    getAllPressureRecordsById(update.getMessage().getFrom().getId())
                            ));
                            goToMainMenu(sendMessage, session);

                        }
                        case "Главное меню" -> {
                            sendMessage.setText("Вы вернулись в главное меню");
                            goToMainMenu(sendMessage, session);
                        }
                        case "Помощь" -> {
                            sendMessage.setText(PRESSURE_MENU_HELP_MESSAGE);
                        }
                        default -> sendMessage.setText("выберите один из пунктов меню");
                    }
                }
                case WAITING_FOR_DATE -> {
                    if ("Главное меню".equals(messageText)) {
                        sendMessage.setText("Вы вернулись в главное меню");
                        goToMainMenu(sendMessage, session);
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                        try {
                            LocalDate date = LocalDate.parse(messageText.trim(), formatter);
                            sendMessage.setText("вы запросили данные за " + messageText);
                            sendMessage.setText(
                                    pressureMessageFormatter.formatListOfRecords(
                                            getPressureRecordsByIdAndDate(update.getMessage().getFrom().getId(), date))
                            );
                            goToMainMenu(sendMessage, session);
                        } catch (DateTimeParseException e) {
                            sendMessage.setText("Некорректный формат даты\nВведите дату в формате ДД.ММ.ГГГГ (пример 01.01.2025)");
                        }
                    }
                }
                case WAITING_FOR_DAYS -> {
                    if ("Главное меню".equals(messageText)) {
                        sendMessage.setText("Вы вернулись в главное меню");
                        goToMainMenu(sendMessage, session);
                    } else {
                        int days = Integer.parseInt(messageText);
                        sendMessage.setText(String.format("Вы запросили данные за последние %s дней", days));
                        sendMessage.setText(pressureMessageFormatter.formatListOfRecords(
                                getPressureRecordsByIdAndDays(update.getMessage().getFrom().getId(), days))
                        );
                        goToMainMenu(sendMessage, session);
                    }
                }
            }
            return sendMessage;
        }
        return null;
    }

    @Override
    public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {
        System.out.println("Webhook установлен: " + setWebhook.getUrl());
    }

    private ReplyKeyboardMarkup mainMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        ArrayList<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Отправить данные о давлении");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("Получить данные о давлении");
        KeyboardRow row3 = new KeyboardRow();
        row3.add("Старт");
        row3.add("Помощь");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup pressureSubMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        ArrayList<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("За дату");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("За последние N дней");
        KeyboardRow row3 = new KeyboardRow();
        row3.add("Все записи");
        KeyboardRow row4 = new KeyboardRow();
        row4.add("Главное меню");
        row4.add("Помощь");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup sendMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        ArrayList<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("ОТПРАВИТЬ");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("Исправить данные");
        row2.add("Главное меню");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup goToMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        ArrayList<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Главное меню");

        keyboard.add(row1);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private void goToMainMenu(SendMessage sendMessage, UserSession session) {
        sendMessage.setReplyMarkup(mainMenuKeyboard());
        session.setUserState(UserState.NONE);
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

    private List<PressureRecordDtoRs> getPressureRecordsByIdAndDate(Long tgId, LocalDate date) {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("tgId", tgId);
        uriVariables.put("date", date.toString());
        ResponseEntity<PressureRecordDtoRs[]> response = restTemplate.getForEntity(
                urlProvider.getRecordsByIdAndDate(), PressureRecordDtoRs[].class, uriVariables);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            return Collections.emptyList();
        }
    }

    private List<PressureRecordDtoRs> getPressureRecordsByIdAndDays(Long tgId, int numberOfDays) {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("tgId", tgId);
        uriVariables.put("numberOfDays", numberOfDays);
        ResponseEntity<PressureRecordDtoRs[]> response = restTemplate.getForEntity(
                urlProvider.getRecordsByIdAndLastNDays(), PressureRecordDtoRs[].class, uriVariables);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            return Collections.emptyList();
        }
    }

    private List<PressureRecordDtoRs> getAllPressureRecordsById(Long tgId) {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("tgId", tgId);
        ResponseEntity<PressureRecordDtoRs[]> response = restTemplate.getForEntity(
                urlProvider.getAllRecordsByIdUrl(), PressureRecordDtoRs[].class, uriVariables);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            return Collections.emptyList();
        }
    }
}
