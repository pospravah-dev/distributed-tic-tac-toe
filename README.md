# Distributed Tic Tac Toe - API Gateway Architecture

A distributed Tic Tac Toe microservices application with automated game simulation, built with modern technologies.

## ️Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      React 19 UI (Frontend)                     │
│                   (http://localhost:3000)                       │
└──────────────┬──────────────────────────────────────────────────┘
               │
               │ Single URL: http://localhost:8080/api
               ▼
    ┌────────────────────────────────────────────────────────────┐
    │                    API Gateway (Port 8080)                  │
    │  ┌──────────────────────────────────────────────────────┐  │
    │  │  Routes:                                              │  │
    │  │  /api/games/**     → http://localhost:8081/games/**   │  │
    │  │  /api/sessions/**  → http://localhost:8082/sessions/**│  │
    │  └──────────────────────────────────────────────────────┘  │
    │  ┌──────────────────────────────────────────────────────┐  │
    │  │  Cross-Cutting Concerns:                              │  │
    │  │  ✓ CORS Configuration (centralized)                   │  │
    │  │  ✓ Request/Response Logging                           │  │
    │  └──────────────────────────────────────────────────────┘  │
    └────────────────────────────────────────────────────────────┘
               │
               ├──────────────────┬──────────────────┐
               ▼                  ▼                  ▼
    ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
    │ Game Engine      │ │ Game Session     │ │   API Gateway    │
    │ Service :8081    │ │ Service :8082    │ │      :8080       │
    │ /games/**        │ │ /sessions/**     │ │  /api/**         │
    └──────────────────┘ └──────────────────┘ └──────────────────┘
```

## Requirements Coverage

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Start Simulation button | ✅ | Triggers automated game simulation via API Gateway |
| Render 3x3 board | ✅ | Board component with Tailwind CSS styling |
| Real-time updates | ✅ | SSE (Server-Sent Events) through API Gateway |
| Show game status | ✅ | GameStatusDisplay component |
| Move history log | ✅ | MoveHistory component |
| Error handling | ✅ | Exception handlers, validation, notification system |
| CORS handling | ✅ | Centralized in API Gateway |

## Project Structure

```
flamingo-tic-tac-toe/
├── backend/
│   ├── api-gateway/                    # NEW: API Gateway Service
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/main/java/.../gateway/
│   │       ├── ApiGatewayApplication.java
│   │       └── resources/application.yml
│   ├── game-engine-service/
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/main/java/.../gameengine/
│   │       ├── GameEngineApplication.java
│   │       ├── controller/
│   │       │   └── GameController.java
│   │       ├── service/
│   │       │   └── GameService.java
│   │       └── domain/
│   └── game-session-service/
│       ├── pom.xml
│       ├── Dockerfile
│       └── src/main/java/.../gamesession/
│           ├── GameSessionApplication.java
│           ├── controller/
│           │   ├── SessionController.java
│           │   └── SimulationController.java
│           └── service/
│               └── SessionService.java
├── frontend/
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── App.tsx
│       ├── components/
│       ├── hooks/
│       └── types/
├── docker-compose.yml
└── README.md
```

## Getting Started

### Prerequisites

- **Java 25** (LTS)
- **Maven 3.8+**
- **Node.js 20+**
- **Docker & Docker Compose** (optional)

### Quick Start (Containerized)

```bash
# Build and run all services
docker-compose up --build

# Or run detached
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Manual Start (Development)

#### 1. API Gateway (Port 8080)
```bash
cd backend/api-gateway
mvn clean spring-boot:run
```

#### 2. Game Engine Service (Port 8081)
```bash
cd backend/game-engine-service
mvn clean spring-boot:run
```

#### 3. Game Session Service (Port 8082)
```bash
cd backend/game-session-service
mvn clean spring-boot:run
```

#### 4. Frontend (Port 3000)
```bash
cd frontend
npm install
npm run dev
```

## API Endpoints

### Through API Gateway (`localhost:8080/api`)

| Method | Endpoint | Service | Description |
|--------|----------|---------|-------------|
| POST | `/api/sessions` | Session | Create new session |
| POST | `/api/sessions/{id}/simulate` | Session | Start simulation |
| GET | `/api/sessions/{id}` | Session | Get session details |
| POST | `/api/games/{id}/move` | Engine | Make a move |
| GET | `/api/games/{id}` | Engine | Get game state |
| GET | `/api/games/{id}/events` | Engine | SSE stream |

### Direct Service Access (Internal)

| Service | Port | Purpose |
|---------|------|---------|
| API Gateway | 8080 | Single entry point |
| Game Engine | 8081 | Internal - game logic |
| Game Session | 8082 | Internal - session management |

## How to Use

1. **Start all services:**
   ```bash
   docker-compose up --build
   ```

2. **Open browser:** `http://localhost:3000`

3. **Click "Start Simulation":**
   - Creates session via API Gateway
   - Triggers automated simulation
   - Real-time updates via SSE

4. **Watch the game:**
   - Board updates in real-time
   - Status shows IN_PROGRESS/WON/DRAW
   - Move history is logged

## Technical Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Spring Boot 4, WebFlux, Java 25 |
| **API Gateway** | Spring Cloud Gateway |
| **Frontend** | React 19, Vite, Tailwind CSS |
| **Real-time** | SSE (Server-Sent Events) |
| **Communication** | HTTP/REST |
| **Container** | Docker, Docker Compose |

## Benefits of API Gateway

| Benefit | Description |
|---------|-------------|
| **Single Entry Point** | Frontend calls only one URL (`:8080/api`) |
| **Centralized CORS** | Configured once at gateway level |
| **Service Abstraction** | Frontend doesn't know about internal services |
| **Easy Scaling** | Add/remove services without frontend changes |
| **Centralized Logging** | All requests logged at gateway |
| **Future-Ready** | Easy to add auth, rate limiting, etc. |

## Notes

- Game uses in-memory storage (ConcurrentHashMap)
- Move validation prevents invalid moves
- Automated simulation uses random move generation
- SSE reconnects automatically on network errors
- All services communicate through API Gateway

## Troubleshooting

**Gateway won't start:**
- Ensure ports 8080, 8081, 8082 are free
- Check logs: `docker-compose logs api-gateway`

**Frontend can't reach backend:**
- Verify API Gateway is running on port 8080
- Check CORS configuration in `application.yml`

**SSE connection failing:**
- Ensure all three backend services are running
- Check gateway routing logs

## License

Educational purposes only.

---