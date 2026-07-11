``# Distributed Tic Tac Toe - Backend

Microservices backend for real-time Tic Tac Toe game simulation with Server-Sent Events (SSE).

## Architecture

```
┌─────────────┐      ┌──────────────────────┐      ┌──────────────────────┐
│   Frontend  │─────▶│    API Gateway       │─────▶│  Game Session Service│
│  (Port 3000)│      │    (Port 8080)       │      │      (Port 8082)     │
│             │◀─────│                      │◀─────│                      │
└─────────────┘      └──────────────────────┘      └──────────────────────┘
                            │                                │
                            │                                ▼
                            │                       ┌──────────────────────┐
                            └──────────────────────▶│   Game Engine Service│
                                                    │      (Port 8081)     │
                                                    └──────────────────────┘
```

### Request Flow

1. **Frontend** → **API Gateway**: Create session request
2. **API Gateway** → **Session Service**: Routes to session service
3. **Session Service** → **Engine Service**: Creates game in engine
4. **Frontend** → **API Gateway**: Start simulation
5. **Session Service**: Runs simulation loop (picks random moves)
6. **Session Service** → **Engine Service**: Makes each move
7. **Engine Service**: Publishes SSE event for each move
8. **Frontend** ← **API Gateway** ← **Engine Service**: Receives SSE stream

## Services Overview

| Service | Port | Java Version | Purpose |
|---------|------|--------------|---------|
| api-gateway | 8080 | 21 | Reverse proxy, CORS, routing |
| game-engine-service | 8081 | 25 | Game logic, board state, SSE events |
| game-session-service | 8082 | 21 | Session management, simulation orchestration |

## Prerequisites

- **Java 25** JDK installed
- **Maven 3.8+** installed
- **Docker & Docker Compose** (optional, for containerized deployment)

## Quick Start

### Option 1: Manual (Maven)

**Start order matters** - services must start in this sequence:

#### Terminal 1 - Game Engine Service (must start first)

```bash
cd game-engine-service
# Ensure Java 25 is active
mvn spring-boot:run
```

#### Terminal 2 - Game Session Service

```bash
cd game-session-service
# Ensure Java 21 is active
mvn spring-boot:run
```

#### Terminal 3 - API Gateway

```bash
cd api-gateway
# Ensure Java 21 is active
mvn spring-boot:run
```

**Verify services are running:**
```bash
curl http://localhost:8081/actuator/health  # Engine service
curl http://localhost:8082/actuator/health  # Session service
curl http://localhost:8080/actuator/health  # Gateway
```

### Option 2: Docker Compose

From the project root directory:

```bash
cd D:\playground\flamingo-tic-tac-toe
docker-compose up --build
```

This starts all services including the frontend:
- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8080
- **Game Engine**: http://localhost:8081
- **Game Session**: http://localhost:8082

**Stop services:**
```bash
docker-compose down
```

**Rebuild from source:**
```bash
docker-compose up --build --force-recreate
```

**Note:** The root `docker-compose.yml` includes the frontend service. For backend-only Docker deployment, see the backend directory.

## API Testing with curl

### 1. Create a New Session

Creates a new game session and returns session details.

```bash
curl -X POST http://localhost:8080/api/sessions \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "id": "session-25aface4",
  "state": "ACTIVE",
  "currentPlayer": "X",
  "moveHistory": [],
  "winner": null,
  "createdAt": "2026-07-06T12:34:22.46613Z",
  "lastUpdated": "2026-07-06T12:34:22.46613Z"
}
```

### 2. Start Automated Simulation

Triggers an automated game where the backend plays against itself.

```bash
curl -X POST http://localhost:8080/api/sessions/{sessionId}/simulate \
  -H "Content-Type: application/json"
```

**Response:** `200 OK` (empty body, simulation runs asynchronously)

**Example:**
```bash
curl -X POST http://localhost:8080/api/sessions/session-25aface4/simulate
```

### 3. Subscribe to SSE Events (Real-time Updates)

Streams game state updates as Server-Sent Events.

```bash
curl -N http://localhost:8080/api/events/{sessionId}
```

**Example (streaming output):**
```bash
curl -N http://localhost:8080/api/events/session-25aface4
```

**Event Format:**
```
event: game-update
data: {"gameId":"session-25aface4","moveNumber":1,"boardState":[" "," "," "," "," "," "," "," ","X"],"currentPlayer":"O","status":"IN_PROGRESS","winner":null}

event: game-update
data: {"gameId":"session-25aface4","moveNumber":2,"boardState":[" "," "," "," "," "," ","O"," ","X"],"currentPlayer":"X","status":"IN_PROGRESS","winner":null}

event: game-update
data: {"gameId":"session-25aface4","moveNumber":7,"boardState":[" "," ","X","O","X","O","O","X","X"],"currentPlayer":"O","status":"IN_PROGRESS","winner":null}

event: game-update
data: {"gameId":"session-25aface4","moveNumber":8,"boardState":["O"," ","X","O","X","O","O","X","X"],"currentPlayer":"X","status":"WON","winner":"O"}
```

**Board State Array:**
- 9 elements representing positions 0-8 (left-to-right, top-to-bottom)
- Values: `" "` (empty), `"X"`, or `"O"`

**Status Values:**
- `"IN_PROGRESS"` - Game is ongoing
- `"WON"` - A player has won
- `"DRAW"` - Game ended in a draw

### 4. Get Game State

Retrieves current game state directly from the engine.

```bash
curl http://localhost:8080/api/games/{gameId}
```

**Example:**
```bash
curl http://localhost:8080/api/games/session-25aface4
```

**Response:**
```json
{
  "id": "session-25aface4",
  "board": {
    "cells": [
      {"value": "O"}, {"value": " "}, {"value": "X"},
      {"value": "O"}, {"value": "X"}, {"value": "O"},
      {"value": "O"}, {"value": "X"}, {"value": "X"}
    ]
  },
  "status": "WON",
  "currentPlayer": "X",
  "winner": "O",
  "moveCount": 8,
  "moveHistory": [
    {"position": 8, "player": "X"},
    {"position": 6, "player": "O"},
    ...
  ]
}
```

### 5. Make a Manual Move

Make a move in a specific game (used for manual play, not simulation).

```bash
curl -X POST http://localhost:8080/api/games/{gameId}/move \
  -H "Content-Type: application/json" \
  -d '{"player": "X", "position": 4}'
```

**Request Body:**
```json
{
  "player": "X",
  "position": 4
}
```

**Response:**
```json
{
  "status": "IN_PROGRESS",
  "winner": null,
  "boardState": [" ", " ", " ", " ", "X", " ", " ", " ", " "]
}
```

**Position Mapping:**
```
0 | 1 | 2
---------
3 | 4 | 5
---------
6 | 7 | 8
```

## Full Testing Workflow

### Complete Simulation Test

```bash
# Step 1: Create session
SESSION_ID=$(curl -s -X POST http://localhost:8080/api/sessions | jq -r '.id')
echo "Created session: $SESSION_ID"

# Step 2: Start simulation (runs in background)
curl -X POST "http://localhost:8080/api/sessions/$SESSION_ID/simulate" &

# Step 3: Watch SSE events (in separate terminal)
curl -N "http://localhost:8080/api/events/$SESSION_ID"

# Step 4: Check final game state
curl "http://localhost:8080/api/games/$SESSION_ID" | jq
```

### PowerShell (Windows)

```powershell
# Step 1: Create session
$session = Invoke-RestMethod -Uri "http://localhost:8080/api/sessions" -Method POST
$sessionId = $session.id
Write-Host "Created session: $sessionId"

# Step 2: Start simulation
Invoke-RestMethod -Uri "http://localhost:8080/api/sessions/$sessionId/simulate" -Method POST

# Step 3: Watch SSE events (run in separate terminal)
# curl -N "http://localhost:8080/api/events/$sessionId"

# Step 4: Check game state
Invoke-RestMethod -Uri "http://localhost:8080/api/games/$sessionId" | ConvertTo-Json
```

## Service Details

### API Gateway (Port 8080)

**Purpose:** Reverse proxy and CORS handler

**Routes:**
| Path Pattern | Target Service | Strip Prefix |
|--------------|----------------|--------------|
| `/api/sessions/**` | `http://localhost:8082` | Yes (1 level) |
| `/api/sessions` | `http://localhost:8082` | No |
| `/api/events/**` | `http://localhost:8081` | Yes (1 level) |
| `/api/games/**` | `http://localhost:8081` | Yes (1 level) |

**CORS Configuration:**
- Allowed origins: `http://localhost:3000`
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS
- Credentials: Allowed

**Configuration:** `api-gateway/src/main/resources/application.yml`

### Game Engine Service (Port 8081)

**Purpose:** Core game logic and real-time event publishing

**Features:**
- In-memory game state storage (ConcurrentHashMap)
- Move validation (turn order, position availability)
- Win detection (8 winning patterns)
- Draw detection (9 moves with no winner)
- SSE event publishing via `Sinks.Many`

**Domain Model:**
```
Game
├── id: String
├── board: Board (9 cells)
├── status: GameState (IN_PROGRESS, WON, DRAW)
├── currentPlayer: String (X or O)
├── winner: String (null, X, or O)
├── moveCount: int
└── moveHistory: List<Move>
```

**Endpoints:**
- `POST /games/{gameId}` - Create game
- `GET /games/{gameId}` - Get game state
- `POST /games/{gameId}/move` - Make move
- `GET /events/{gameId}` - SSE event stream

**Configuration:** `game-engine-service/src/main/resources/application.yml`

### Game Session Service (Port 8082)

**Purpose:** Session management and simulation orchestration

**Features:**
- Session creation with unique IDs
- In-memory session repository
- Simulation loop (random move selection)
- Calls Game Engine Service for each move
- Tracks session state and move history

**Simulation Loop:**
1. Get current game state from Engine Service
2. Check if game has ended (WON/DRAW)
3. Get available positions from move history
4. Pick random available position
5. Call Engine Service to make move
6. Update session with move
7. Repeat until game ends

**Configuration:** `game-session-service/src/main/resources/application.yml`

## Configuration Summary

### Port Mapping

| Service | Port | Environment Variable |
|---------|------|---------------------|
| API Gateway | 8080 | `server.port` |
| Game Engine | 8081 | `server.port` |
| Game Session | 8082 | `server.port` |

### Inter-Service Communication

- **Session Service → Engine Service**: `http://localhost:8081` (hardcoded in `GameEngineClient.java`)
- **Gateway → Session Service**: `http://localhost:8082` (configured in `application.yml`)
- **Gateway → Engine Service**: `http://localhost:8081` (configured in `application.yml`)

### Logging Levels

All services use:
- `root`: INFO
- `com.flamingo.tictactoe.*`: DEBUG
- `org.springframework.web`: INFO
- `org.springframework.web.cors`: DEBUG

## Project Structure

```
backend/
├── api-gateway/
│   ├── src/main/java/com/flamingo/tictactoe/gateway/
│   │   └── ApiGatewayApplication.java
│   ├── src/main/resources/application.yml
│   ├── Dockerfile
│   └── pom.xml
├── game-engine-service/
│   ├── src/main/java/com/flamingo/tictactoe/gameengine/
│   │   ├── GameEngineApplication.java
│   │   ├── controller/
│   │   │   ├── GameController.java
│   │   │   └── EventController.java
│   │   ├── domain/
│   │   │   ├── Game.java
│   │   │   ├── Board.java
│   │   │   ├── Cell.java
│   │   │   ├── GameState.java
│   │   │   ├── Move.java
│   │   │   └── DeltaPatchEvent.java
│   │   ├── service/
│   │   │   └── GameService.java
│   │   ├── event/
│   │   │   └── EventEmitterRepository.java
│   │   ├── dto/
│   │   │   ├── MoveRequest.java
│   │   │   └── MoveResponse.java
│   │   └── exception/
│   │       ├── GameNotFoundException.java
│   │       ├── GameAlreadyEndedException.java
│   │       └── InvalidMoveException.java
│   ├── src/main/resources/application.yml
│   ├── Dockerfile
│   └── pom.xml
├── game-session-service/
│   ├── src/main/java/com/flamingo/tictactoe/gamesession/
│   │   ├── GameSessionApplication.java
│   │   ├── controller/
│   │   │   └── SessionController.java
│   │   ├── domain/
│   │   │   ├── Session.java
│   │   │   ├── GameSessionState.java
│   │   │   ├── Move.java
│   │   │   ├── MoveRequest.java
│   │   │   └── MoveResponse.java
│   │   ├── service/
│   │   │   └── SessionService.java
│   │   ├── client/
│   │   │   └── GameEngineClient.java
│   │   ├── util/
│   │   │   └── IdGenerator.java
│   │   └── exception/
│   │       ├── SessionNotFoundException.java
│   │       └── SimulationException.java
│   ├── src/main/resources/application.yml
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
└── README.md
```

## Building Services

### Build All Services

```bash
# API Gateway (Java 21)
cd api-gateway
mvn clean package -DskipTests

# Game Engine Service (Java 25)
cd ../game-engine-service
mvn clean package -DskipTests

# Game Session Service (Java 21)
cd ../game-session-service
mvn clean package -DskipTests
```

### Build Single Service

```bash
cd api-gateway
mvn clean package -DskipTests
```

### Run Tests

```bash
mvn test
```

## Troubleshooting

### Port Already in Use

**Error:** `Port 8080 is already in use`

**Solution:**
```bash
# Windows - Find and kill process
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Java Version Mismatch

**Error:** `Unsupported class file major version` or `UnsupportedClassVersionError`

**Solution:** Ensure correct Java version for each service:
```bash
# Check current Java version
java -version

# Use SDKMAN to switch versions
sdk use java 21.0.0-tem  # For gateway and session
sdk use java 25.0.0-tem  # For engine
```

### Services Won't Start in Correct Order

**Error:** Session service fails to connect to engine service

**Solution:** Start services in order:
1. Game Engine Service (8081) - wait for "Started GameEngineApplication"
2. Game Session Service (8082) - wait for "Started GameSessionApplication"
3. API Gateway (8080) - wait for "Started ApiGatewayApplication"

### CORS Errors from Frontend

**Error:** `Access-Control-Allow-Origin` header missing

**Solution:** Verify gateway CORS configuration in `api-gateway/src/main/resources/application.yml`:
```yaml
allowedOrigins:
  - "http://localhost:3000"
allowedMethods:
  - GET
  - POST
  - PUT
  - DELETE
  - OPTIONS
allowCredentials: true
```

### SSE Connection Drops Immediately

**Error:** Connection closes without receiving events

**Possible causes:**
1. Session ID doesn't exist in engine service
2. Emitter not created before subscription
3. Network connectivity issue

**Debug steps:**
```bash
# Check if game exists
curl http://localhost:8080/api/games/{sessionId}

# Check service health
curl http://localhost:8081/actuator/health
```

### Docker Compose Fails to Build

**Error:** Build fails with Maven errors

**Solutions:**
1. Clear Maven cache:
   ```bash
   mvn clean
   docker-compose build --no-cache
   ```

2. Check Java version in Dockerfile matches pom.xml

3. Verify internet connection for dependency download

### Simulation Doesn't Update Board

**Symptom:** Simulation starts but board doesn't update

**Check:**
1. SSE endpoint is accessible: `curl -N http://localhost:8080/api/events/{sessionId}`
2. Events are being emitted (check engine service logs)
3. Frontend is listening to `game-update` event (not generic `message`)

### Game Engine Client Connection Refused

**Error:** `Connection refused: localhost/127.0.0.1:8081`

**Cause:** Game Engine Service not running or wrong port

**Solution:**
```bash
# Verify engine service is running
curl http://localhost:8081/actuator/health

# Check logs for startup errors
# In game-engine-service terminal, look for "Started GameEngineApplication"
```

## Actuator Endpoints

All services expose Spring Boot Actuator endpoints:

```bash
# Health check
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8080/actuator/health

# Metrics (engine service)
curl http://localhost:8081/actuator/metrics

# Info endpoint
curl http://localhost:8081/actuator/info
```

## License

Private - Internal use only
