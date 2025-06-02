# Changelog

## [0.2.0] - 2025-06-02
### Added
- Делегирование логики из `TelegramBotService` в `TelegramCommandHandler` для соблюдения принципа единой ответственности (SRP).
- Класс `TelegramCommandHandler` для обработки текстовых команд и переходов между состояниями пользователя.
- Класс `KeyboardFactory` для генерации клавиатур Telegram (главное меню, подменю и т.п.).
- Класс `HealthDataClient` для взаимодействия с REST API сервиса хранения данных с обработкой ошибок.

### Changed
- `TelegramBotService` теперь делегирует всю логику и содержит только минимальную маршрутизацию

### Removed
- Избыточные зависимости (`RestTemplate`, `HealthDataClient`, и др.) удалены из `TelegramBotService`