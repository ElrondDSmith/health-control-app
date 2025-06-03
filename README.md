# Health Control App
> Текущая версия: **0.2.1**

Health Control App — система для ввода, хранения и анализа артериального давления и пульса с помощью Telegram-бота и микросервисной архитектуры.

- **health-data-service** — микросервис для хранения и обработки данных о здоровье.
- **telegram-input-service** — Telegram-бот для ввода данных пользователями.

---

## 📦 Структура проекта

```
health-control-app/
├── health-data-service/            	# Микросервис хранения и обработки данных
├── telegram-input-service/         	# Микросервис Telegram-бота для ввода и получения данных
├── Dockerfile-health-data-service  	# Docker-файл для health-data-service
├── Dockerfile-telegram-input-service   # Docker-файл для telegram-input-service
├── pom.xml                         	# Родительский POM файл (Maven multi-module)
├── .gitignore                      	# Общий gitignore
├── CHANGELOG.md                       	# Журнал изменений
└── README.md                       	# Этот файл
```

---

## ▶️ Запуск микросервисов

1. **Запустите сервис хранения данных** (`health-data-service`, порт `8080`):

```bash
cd health-data-service
mvn spring-boot:run
```

2. **Запустите Telegram Input-сервис** (`telegram-input-service`, порт `8081`):

```bash
cd telegram-input-service
mvn spring-boot:run
```

---

## 🧪 Выбор профиля для запуска
Активный Spring-профиль выбирается с помощью переменной окружения SPRING_PROFILES_ACTIVE:

Пример для локальной разработки:
```bash
SPRING_PROFILES_ACTIVE=local mvn spring-boot:run
```
Пример для продакшн-среды:
```bash
SPRING_PROFILES_ACTIVE=prod mvn spring-boot:run
```

---

## ⚙️ Конфигурация

Каждый микросервис использует следующую систему конфигурации:

- `application.yaml` — основной файл, включающий настройки по умолчанию
- `application-local.yaml` — настройки для локальной разработки.
- `application-prod.yaml` — настройки для продакшн-среды с использованием переменных окружения.

### 🛡️ Секреты и переменные

- **Чувствительные данные** (токены, пароли и т.п.) должны находиться в `application-local.yaml` (локально) и передаваться через переменные окружения в продакшене.
- Пример локального файла приведён в `application-local.yaml.example`, чтобы упростить создание собственного `application-local.yaml`. Этот файл **не должен попадать в Git**.
- Никогда не добавляйте .yaml файлы с секретами в Git.
---

## 🛠 Сборка

Сборка всего проекта:

```bash
mvn clean install
```

Запуск одного из микросервисов:

```bash
cd <имя-сервиса>
mvn spring-boot:run
```

---

## 📦 Docker

Сборка и запуск с использованием Docker:

### health-data-service:

```bash
docker build -t health-data-service -f Dockerfile-health-data-service .
docker run -p 8080:8080 health-data-service
```

### telegram-input-service:

```bash
docker build -t telegram-input-service -f Dockerfile-telegram-input-service .
docker run -p 8081:8081 telegram-input-service
```

---

## 📃 CHANGELOG

Все изменения см. в [CHANGELOG.md](./CHANGELOG.md)