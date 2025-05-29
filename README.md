# Health Control App

- **health-data-service** — микросервис для хранения и обработки данных о здоровье.
- **telegram-input-service** — Telegram-бот для ввода данных пользователями.

---

## 📦 Структура проекта

```
health-control-app/
├── health-data-service/            # Микросервис хранения данных
├── telegram-input-service/         # Микросервис Telegram-бота
├── pom.xml                         # Родительский POM файл монорепозитория
├── .gitignore                      # Общий .gitignore
└── README.md                       # Этот файл
```

---

## ▶️ Запуск микросервисов

1. Запустить `health-data-service` (порт `8080` по умолчанию):

```bash
cd health-data-service
mvn spring-boot:run
```

2. Запустить `telegram-input-service` (порт `8081` по умолчанию):

```bash
cd telegram-input-service
mvn spring-boot:run
```

---

## ⚙️ Конфигурация

Каждый микросервис использует следующую систему конфигурации:

- `application.yaml` — основной файл, включающий настройки по умолчанию и выбор активного профиля.
- `application-local.yaml` — настройки для локальной разработки.
- `application-prod.yaml` — настройки для продакшн-среды с использованием переменных окружения.

### 🛡️ Секреты и переменные

- **Чувствительные данные** (токены, пароли и т.п.) должны находиться в `application-local.yaml` (локально) и передаваться через переменные окружения в продакшене.
- Пример локального файла приведён в `application-local.yaml.example`, чтобы упростить создание собственного `application-local.yaml`. Этот файл **не должен попадать в Git**.

---

## 🛠 Полезные команды Maven

Сборка всего проекта:

```bash
mvn clean install
```

Запуск одного из микросервисов:

```bash
cd <имя-сервиса>
mvn spring-boot:run
```