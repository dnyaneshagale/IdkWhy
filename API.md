# API Documentation

Base URL: `/api/rooms`

## Create Room

`POST /api/rooms`

Request body:

```json
{
  "roomNumber": "123456",
  "password": "optional-password"
}
```

Response: `201 Created`

## Join Room

`POST /api/rooms/join`

Request body:

```json
{
  "roomNumber": "123456",
  "password": "optional-password"
}
```

Response: `200 OK`

## Get Room

`GET /api/rooms/{room}`

Optional header for protected rooms:

- `X-Room-Password: <password>`

Response: `200 OK`

## Send Message

`POST /api/rooms/{room}/messages`

Optional header for protected rooms:

- `X-Room-Password: <password>`

Request body:

```json
{
  "text": "clipboard content"
}
```

Response: `201 Created`

## Delete Message

`DELETE /api/rooms/{room}/messages/{id}`

Optional header for protected rooms:

- `X-Room-Password: <password>`

Response: `204 No Content`

## Clear Messages

`DELETE /api/rooms/{room}/messages`

Optional header for protected rooms:

- `X-Room-Password: <password>`

Response: `200 OK`

## Delete Room

`DELETE /api/rooms/{room}`

Optional header for protected rooms:

- `X-Room-Password: <password>`

Response: `204 No Content`

## Error Shape

```json
{
  "timestamp": "2026-07-19T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed.",
  "path": "/api/rooms",
  "details": ["roomNumber: Room number is required."]
}
```

## Status Codes

- `400 Bad Request` for invalid room numbers, validation errors, and malformed requests
- `403 Forbidden` for wrong passwords
- `404 Not Found` for missing rooms or messages
- `409 Conflict` for duplicate room creation
- Rooms are automatically removed after 2 days of inactivity
