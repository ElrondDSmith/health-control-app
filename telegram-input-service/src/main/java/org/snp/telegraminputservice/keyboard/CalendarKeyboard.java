package org.snp.telegraminputservice.keyboard;

public class CalendarKeyboard {

    /**
     *     private InlineKeyboardMarkup buildCalendar(LocalDate date) {
     *         List<List<InlineKeyboardButton>> rows = new ArrayList<>();
     *
     *         int year = date.getYear();
     *         int month = date.getMonthValue();
     *
     *
     *         YearMonth yearMonth = YearMonth.of(date.getYear(), date.getMonth());
     *         LocalDate firstDay = YearMonth.now().atDay(1);
     *         DayOfWeek firstWeekDay = firstDay.getDayOfWeek();
     *
     *         List<InlineKeyboardButton> monthRow = List.of(
     *                 InlineKeyboardButton
     *                         .builder()
     *                         .text("<<")
     *                         .callbackData("PREV_MONTH_" + date.minusMonths(1))
     *                         .build(),
     * //                InlineKeyboardButton.builder()
     * //                        .text(date.getMonth().name() + " " + date.getYear())
     * //                        .callbackData("SELECT_MONTH_YEAR")
     * //                        .build(),
     *                 InlineKeyboardButton.builder()
     *                         .text(date.getMonth().name())
     *                         .callbackData("SELECT_MONTH")
     *                         .build(),
     *                 InlineKeyboardButton.builder()
     *                         .text(String.valueOf(date.getYear()))
     *                         .callbackData("SELECT_YEAR")
     *                         .build(),
     *                 InlineKeyboardButton.builder()
     *                         .text(">>")
     *                         .callbackData("NEXT_MONTH_" + date.plusMonths(1))
     *                         .build()
     *         );
     *         rows.add(monthRow);
     *
     *         //заголовок с днями недели
     *         List<InlineKeyboardButton> weekDays = Arrays.stream(DayOfWeek.values())
     *                 .map(day -> InlineKeyboardButton.builder().text(day.name().substring(0, 2)).callbackData("IGNORE").build())
     *                 .toList();
     *         rows.add(weekDays);
     *         //пустые кнопки для смещения
     *         List<InlineKeyboardButton> weekRow = new ArrayList<>();
     *         int dayOfWeekValue = firstWeekDay.getValue();
     *         for (int i = 1; i < dayOfWeekValue; i++) {
     *             weekRow.add(InlineKeyboardButton.builder().text(" ").callbackData("IGNORE").build());
     *         }
     *         for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
     *             LocalDate current = yearMonth.atDay(day);
     *             String dayStr = String.valueOf(day);
     *             String callback = "DATE_" + current;
     *
     *             weekRow.add(InlineKeyboardButton.builder().text(dayStr).callbackData(callback).build());
     *             if (weekRow.size() == 7) {
     *                 rows.add(new ArrayList<>(weekRow));
     *                 weekRow.clear();
     *             }
     *         }
     *         if (!weekRow.isEmpty()) {
     *             while (weekRow.size() < 7) {
     *                 weekRow.add(InlineKeyboardButton.builder().text(" ").callbackData("IGNORE").build());
     *             }
     *             rows.add(weekRow);
     *         }
     *         return InlineKeyboardMarkup.builder().keyboard(rows).build();
     *     }
     *
     *     private InlineKeyboardMarkup buildMonthYearSelector() {
     *         List<List<InlineKeyboardButton>> rows = new ArrayList<>();
     *         int currentYear = LocalDate.now().getYear();
     *
     *         Month[] months = Month.values();
     *         for (int i = 0; i < months.length; i += 4) {
     *             List<InlineKeyboardButton> row = new ArrayList<>();
     *             for (int j = i; j < i + 4 && j < months.length; j++) {
     *                 String callback = "Month_" + months[j].getValue();
     *                 row.add(InlineKeyboardButton.builder()
     *                         .text(months[j].name().substring(0, 3))
     *                         .callbackData(callback)
     *                         .build());
     *             }
     *             rows.add(row);
     *         }
     *
     *         List<InlineKeyboardButton> yearRow = new ArrayList<>();
     *         for (int i = currentYear; i <= currentYear + 1; i++) {
     *             yearRow.add(InlineKeyboardButton.builder()
     *                     .text(String.valueOf(i))
     *                     .callbackData("YEAR_" + i)
     *                     .build());
     *         }
     *         rows.add(yearRow);
     *
     *         return InlineKeyboardMarkup.builder().keyboard(rows).build();
     *     }
     *
     *
     *         public InlineKeyboardMarkup buildMonthSelection() {
     *         List<List<InlineKeyboardButton>> rows = new ArrayList<>();
     *         Month[] months = Month.values();
     *         for (int i = 0; i < months.length; i += 4) {
     *             List<InlineKeyboardButton> row = new ArrayList<>();
     *             for (int j = i; j < i + 4 && j < months.length; j++) {
     *                 String callback = "Month_" + months[j].getValue();
     *                 row.add(InlineKeyboardButton.builder()
     *                         .text(months[j].name().substring(0, 3))
     *                         .callbackData(callback)
     *                         .build());
     *             }
     *             rows.add(row);
     *         }
     *         return InlineKeyboardMarkup.builder().keyboard(rows).build();
     *     }
     *
     *     public InlineKeyboardMarkup buildYearSelections() {
     *         List<List<InlineKeyboardButton>> rows = new ArrayList<>();
     *         int currentYear = LocalDate.now().getYear();
     *         List<InlineKeyboardButton> yearRow = new ArrayList<>();
     *
     *         for (int i = currentYear - NUMBER_OF_YEARS_AGO; i <= currentYear; i++) {
     *             String callback = "YEAR_" + i;
     *             yearRow.add(InlineKeyboardButton.builder()
     *                     .text(String.valueOf(i))
     *                     .callbackData(callback)
     *                     .build());
     *         }
     *         rows.add(yearRow);
     *
     *         return InlineKeyboardMarkup.builder().keyboard(rows).build();
     *     }
     *
     *
     *
     *     public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
     *
     *
     *             if (update.hasCallbackQuery()) {
     *             String callbackData = update.getCallbackQuery().getData();
     *             Long chatId = update.getCallbackQuery().getMessage().getChatId();
     *             UserSession session = sessions.computeIfAbsent(chatId, id -> new UserSession());
     *             SendMessage sendMessage = new SendMessage();
     *             sendMessage.setChatId(chatId.toString());
     *             if (callbackData.startsWith("DATE_")) {
     *                 String selectDate = callbackData.replace("DATE_", "");
     *
     *                 sendMessage.setText("Вы выбрали дату: " + selectDate);
     *                 goToMainMenu(sendMessage, session);
     *                 return sendMessage;
     *             }
     *             if (callbackData.startsWith("PREV_MONTH_") || callbackData.startsWith("NEXT_MONTH_")) {
     *                 String dateStr = callbackData.replace("PREV_MONTH_", "").replace("NEXT_MONTH_", "");
     *                 LocalDate newDate = LocalDate.parse(dateStr);
     *                 sendMessage.setReplyMarkup(buildCalendar(newDate));
     *                 sendMessage.setText("Выберите дату:");
     *                 return sendMessage;
     *             }
     *             if (callbackData.equals("SELECT_MONTH_YEAR")) {
     *                 sendMessage.setReplyMarkup(buildMonthYearSelector());
     *                 sendMessage.setText("Выберите месяц и год:");
     *                 return sendMessage;
     *             }
     *             if (callbackData.startsWith("MONTH_")) {
     *                 int month = Integer.parseInt(callbackData.replace("MONTH_", ""));
     *                 session.setSelectedMonth(month);
     *                 if (session.getSelectedYear() != 0) {
     *                     LocalDate selectedDate = LocalDate.of(session.getSelectedYear(), month, 1);
     *                     sendMessage.setReplyMarkup(buildCalendar(selectedDate));
     *                     sendMessage.setText("Выберите дату:");
     *                     return sendMessage;
     *                 } else {
     *                     sendMessage.setText("Теперь выберите год:");
     *                     return sendMessage;
     *                 }
     *             }
     *             if (callbackData.startsWith("YEAR_")) {
     *                 int year = Integer.parseInt(callbackData.replace("YEAR_", ""));
     *                 session.setSelectedYear(year);
     *                 if (session.getSelectedMonth() != 0) {
     *                     LocalDate selectedDate = LocalDate.of(year, session.getSelectedMonth(), 1);
     *                     sendMessage.setReplyMarkup(buildCalendar(selectedDate));
     *                     sendMessage.setText("Выберите дату:");
     *                     return sendMessage;
     *                 } else {
     *                     sendMessage.setText("Теперь выберите месяц");
     *                     return sendMessage;
     *                 }
     *             }
     *             if (callbackData.startsWith("SELECT_MONTH_")) {
     * //                int month = Integer.parseInt(callbackData.replace("SELECT_MONTH_", ""));
     * //                session.setSelectedMonth(month);
     *                 sendMessage.setReplyMarkup(calendarKeyboardBuilder.buildMonthSelection());
     *                 sendMessage.setText("Выберите месяц");
     *                 return sendMessage;
     *             }
     *         }
     *
     *
     *
     *
     *                         case PRESSURE_MENU -> {
     *                     switch (messageText) {
     *                         case "За дату" -> {
     *                             sendMessage.setText("Введите дату в формате ДД.ММ.ГГГГ (пример 01.01.2025)");
     *                             session.setUserState(UserState.WAITING_FOR_DATE);
     *                         }
     * //                        case "За дату" -> {
     * //                            sendMessage.setText("Введите дату: ");
     * //                            sendMessage.setReplyMarkup(buildCalendar(LocalDate.now()));
     * //                            session.setUserState(UserState.WAITING_FOR_DATE_CALENDAR);
     * //                        }
     *
     *
     *
     *
     *
     */
}
