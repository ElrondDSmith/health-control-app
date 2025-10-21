# Health Control App
> Текущая версия: **0.4.0**

Health Control App — система для ввода, хранения и анализа артериального давления и пульса с помощью Telegram-бота и микросервисной архитектуры.

- **health-data-service** — микросервис для хранения и обработки данных о здоровье.
- **telegram-input-service** — Telegram-бот для ввода данных пользователями.
- **pdf-service** — микросервис для генерации PDF-отчётов на основе данных пользователя.

---

## 📦 Структура проекта

```
health-control-app/
├── health-data-service/            	# Микросервис хранения и обработки данных
├── telegram-input-service/         	# Микросервис Telegram-бота для ввода и получения данных
├── pdf-service/                        # Микросервис генерации PDF отчётов
├── Dockerfile-health-data-service  	# Docker-файл для health-data-service
├── Dockerfile-telegram-input-service   # Docker-файл для telegram-input-service
├── Dockerfile-pdf-service              # Docker-файл для pdf-service
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

3. **Запустите Pdf-сервис** (`pdf-service`, порт `8083`):

---

## ☑️ Выбор профиля для запуска
Активный Spring-профиль выбирается с помощью переменной окружения SPRING_PROFILES_ACTIVE:

Пример для локальной разработки:
```bash
SPRING_PROFILES_ACTIVE=local mvn spring-boot:run
```
Пример для продакшн-среды:
```bash
SPRING_PROFILES_ACTIVE=prod mvn spring-boot:run
```

Для `telegram-input-service` доступен дополнительный профиль `sleep`, который используется совместно с профилями 
`local` или `prod`. При активации добавляет в сервис возможность разбудить уснувшие сервера.

Пример для продакшн-среды:
```bash
SPRING_PROFILES_ACTIVE="prod,sleep" mvn spring-boot:run
```
---

## ⚙️ Конфигурация

Каждый микросервис использует следующую систему конфигурации:

- `application.yaml` — основной файл, включающий настройки по умолчанию
- `application-local.yaml` — настройки для локальной разработки.
- `application-prod.yaml` — настройки для продакшн-среды с использованием переменных окружения.
- `messages.yaml` — содержит текстовые сообщения сервисов (включая сообщения логов и исключений).

`telegram-input-service` использует дополнительный файл конфигурации для профиля `sleep`:
- `application-sleep.yaml` — настройки профиля `sleep`.

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

## 🐋 Docker

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

### pdf-service:

```bash
docker build -t pdf-service -f Dockerfile-pdf-service .
docker run -p 8083:8083 pdf-service
```

---

### 📡 Ping endpoint

Во всех микросервисах реализован простой эндпоинт `/ping`, предназначенный для проверки доступности сервиса.  
При успешном GET-запросе `/ping` возвращается строка в формате: `"Ping <service-name>: OK"`

---

## 📃 CHANGELOG

Все изменения см. в [CHANGELOG.md](./CHANGELOG.md)