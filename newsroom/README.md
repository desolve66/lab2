# REST API «Редакция новостного издания» — Вариант 27

Spring Boot 3.3 · Java 17 · Maven. Данные хранятся в памяти (БД не нужна).

## Как запустить

1. Распакуйте архив и в IntelliJ IDEA выберите **File → Open** → папку `newsroom` (с файлом `pom.xml`).
2. Дождитесь, пока Maven скачает зависимости (нужен интернет). Если IDEA спросит — **Trust Project** / **Load Maven Project**.
3. Проверьте, что выбран JDK 17 или новее: **File → Project Structure → SDK**.
4. Запустите класс `NewsroomApplication` (зелёная стрелка). Сервер: `http://localhost:8080`.

Без IDEA: `mvn spring-boot:run` в папке проекта.

## Проверка

Файл `requests.http` — готовые запросы по сценарию: пользователь → рубрика → статья → проверка → выпуск → публикация → каскадное удаление.
В IDEA Ultimate запускайте их зелёной стрелкой; в Community — через Postman или curl.

## Эндпоинты

| Ресурс | URL |
|---|---|
| Пользователи | `/api/users` |
| Рубрики | `/api/editorial-sections` |
| Статьи | `/api/articles` |
| Редакторские проверки | `/api/editorial-reviews` |
| Выпуски | `/api/edition-issues` |

Для каждого: `GET` (список), `GET /{id}`, `POST` (201), `PATCH /{id}`, `DELETE /{id}` (204).
Ошибки: 404 — объект не найден, 400 — ошибка валидации или бизнес-правила.

## Структура

```
src/main/java/com/example/newsroom/
├── controller/   приём запросов, маппинг DTO ↔ сущность
├── service/      бизнес-логика, проверки, каскадное удаление, время
├── repository/   хранение в памяти
├── model/        сущности и enum'ы статусов
├── dto/          запросы и ответы API
└── exception/    исключения и глобальный обработчик ошибок
```
