# Health Control App



- **health-data-service** — сервис для хранения и обработки данных о здоровье.
- **telegram-input-service** — Telegram-бот для ввода данных пользователями.

---

## Структура проекта

health-control-app/

├── health-data-service/ # Микросервис хранения данных

├── telegram-input-service/ # Микросервис Telegram-бота

├── pom.xml # Родительский POM файл монорепозитория

├── .gitignore # Общий gitignore

└── README.md # Этот файл

---

## Запуск микросервисов

1. Запустить `health-data-service` (порт 8080 по умолчанию):

```bash
cd health-data-service
mvn spring-boot:run
```
2. Запустить telegram-input-service (порт 8081 по умолчанию):

```bash
cd telegram-input-service
mvn spring-boot:run
```

## Конфигурация
Файлы конфигурации находятся в src/main/resources каждого микросервиса.
Секретные настройки хранятся в application-secrets.yaml — этот файл исключён из Git.
Рекомендуется создавать application-secrets.yaml локально с необходимыми настройками (пример файла application-secrets.yaml.example).

## Полезные команды Maven
Сборка всего проекта:
```bash
mvn clean install
```
Запуск одного из микросервисов:
```bash
cd <имя-сервиса>
mvn spring-boot:run
```