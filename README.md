# IdkWhy

IdkWhy is a temporary clipboard sharing service built with Spring Boot and a static frontend. It lets you create a short-lived room on one device, paste text into it, and read it on another device without chat, accounts, databases, Redis, or external services.

## Stack

- Java 21
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Validation
- Lombok
- HTML5, CSS3, Vanilla JavaScript

## Run

```bash
mvn spring-boot:run
```

Then open:

- Home: http://localhost:8080/
- Room page: http://localhost:8080/room.html?room=123456

## Features

- Create public or password-protected rooms
- Join existing rooms
- Send, copy, and delete messages
- Auto-refresh every 2 seconds on the room screen
- Maximum 50 messages per room, newest first
- Automatic room cleanup after 2 days of inactivity
- Thread-safe in-memory storage using ConcurrentHashMap and ConcurrentLinkedDeque

## API Password Header

For protected rooms, read and message endpoints expect the header:

- X-Room-Password: <password>

## Project Layout

- src/main/java/com/idkwhy/controller
- src/main/java/com/idkwhy/service
- src/main/java/com/idkwhy/service/impl
- src/main/java/com/idkwhy/model
- src/main/java/com/idkwhy/dto/request
- src/main/java/com/idkwhy/dto/response
- src/main/java/com/idkwhy/exception
- src/main/java/com/idkwhy/config
- src/main/java/com/idkwhy/util
- src/main/resources/static
