# Distributed Tic Tac Toe - Frontend

A React-based frontend for a distributed Tic Tac Toe game with real-time simulation via Server-Sent Events (SSE).

## Features

- Real-time game state updates via SSE
- Interactive game board with visual feedback
- Move history tracking
- Game status display (in-progress, won, drawn)
- Automated game simulation
- Responsive UI with TailwindCSS

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| React | 19.0.0 | UI framework |
| TypeScript | 5.5.4 | Type safety |
| Vite | 8.1.3 | Build tool & dev server |
| TailwindCSS | 3.4.11 | Styling |
| ESLint | 8.57.0 | Code linting |
| typescript-eslint | 8.62.1 | TypeScript linting |

## Prerequisites

Ensure the following are installed:

- **Node.js** >= 18.x (LTS recommended)
- **npm** >= 9.x
- **Backend server** running on `http://localhost:8080` (see [Backend API](#backend-api) section)

## Installation

1. **Clone the repository** (if not already done):
   ```bash
   cd \flamingo-tic-tac-toe\frontend
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Verify installation**:
   ```bash
   npm run lint
   ```

## Running the Application

### Development Mode

1. **Start the backend server** (required):
   - The backend must be running on `http://localhost:8080`
   - See [Backend API](#backend-api) section for backend requirements

2. **Start the development server**:
   ```bash
   npm run dev
   ```

3. **Open the application**:
   - Navigate to `http://localhost:3000` in your browser

### Production Build

1. **Build for production**:
   ```bash
   npm run build
   ```
   - Output: `dist/` directory
   - Includes source maps for debugging

2. **Preview production build**:
   ```bash
   npm run preview
   ```

## Testing & Quality

### Linting

```bash
npm run lint
```

Runs ESLint with TypeScript support across all source files.

### Type Checking

TypeScript type checking runs automatically during build:

```bash
npm run build
```

For explicit type checking only:

```bash
npx tsc --noEmit
```

## Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── Board.tsx           # Game board component
│   │   ├── GameStatusDisplay.tsx  # Status/winner display
│   │   └── MoveHistory.tsx     # Move history list
│   ├── hooks/
│   │   ├── index.ts            # Hook exports
│   │   ├── useGameLogic.ts     # Game state management
│   │   └── useSSE.ts           # SSE connection hook
│   ├── types/
│   │   ├── game.ts             # Game state types
│   │   ├── gameState.ts        # Additional state types
│   │   ├── index.ts            # Type exports
│   │   └── sse.ts              # SSE event types
│   ├── utils/
│   │   ├── boardUtils.ts       # Board manipulation utilities
│   │   └── index.ts            # Utility exports
│   ├── App.tsx                 # Main application component
│   ├── main.tsx                # Entry point
│   └── index.css               # Global styles (Tailwind)
├── index.html                  # HTML entry point
├── package.json                # Dependencies & scripts
├── tsconfig.json               # TypeScript configuration
├── vite.config.ts              # Vite configuration
├── tailwind.config.js          # TailwindCSS configuration
└── postcss.config.mjs          # PostCSS configuration
```

## Backend API

The frontend expects a backend server running at `http://localhost:8080` with the following endpoints:

### REST Endpoints

#### `POST /api/sessions`
Creates a new game session.

**Request:**
```json
{}
```

**Response:**
```json
{
  "id": "session-xxx",
  "state": "ACTIVE",
  "currentPlayer": "X",
  "moveHistory": [],
  "winner": null,
  "createdAt": "2026-07-06T12:34:22.46613Z",
  "lastUpdated": "2026-07-06T12:34:22.46613Z"
}
```

#### `POST /api/sessions/{sessionId}/simulate`
Starts an automated game simulation.

**Response:** `200 OK` (empty body)

### SSE Endpoint

#### `GET /api/events/{sessionId}`
Server-Sent Events stream for real-time game updates.

**Event Format:**
```
event: game-update
data: {
  "gameId": "session-xxx",
  "moveNumber": 1,
  "boardState": [" "," ","X"," "," "," "," "," "," "],
  "currentPlayer": "O",
  "status": "IN_PROGRESS",
  "winner": null
}
```

**Board State Array:**
- Index 0-8 represent positions left-to-right, top-to-bottom
- Values: `" "` (empty), `"X"`, or `"O"`

**Status Values:**
- `"IN_PROGRESS"` - Game is ongoing
- `"WON"` - A player has won
- `"DRAW"` - Game ended in a draw

**Winner Values:**
- `"X"` or `"O"` - Winning player
- `null` - No winner yet
- `"DRAW"` - Draw game

## Configuration

### Vite Configuration (`vite.config.ts`)

- **Dev server port:** 3000
- **API proxy:** `/api` → `http://localhost:8080`
- **Build output:** `dist/`
- **Source maps:** Enabled

### TypeScript Configuration (`tsconfig.json`)

- **Target:** ES2022
- **Strict mode:** Enabled
- **Module resolution:** bundler
- **JSX:** react-jsx

### TailwindCSS Configuration (`tailwind.config.js`)

Custom color palette:
- `tictactoe-blue`: #3B82F6
- `tictactoe-red`: #EF4444
- `tictactoe-purple`: #8B5CF6
- `tictactoe-green`: #10B981

## Usage

### Manual Play

1. Click **"Start Simulation"** to create a session
2. The board will update in real-time as moves are made
3. Game status updates automatically
4. Move history displays all moves

### Automated Simulation

1. Click **"Start Simulation"**
2. Backend runs automated game
3. Board updates via SSE as moves occur
4. Winner displayed when game ends

### Starting a New Game

After a game ends (win/draw), click **"Start New Game"** to reset.

## Troubleshooting

### No Board Updates During Simulation

**Symptom:** Events visible in network tab, but board doesn't update

**Solution:** Check browser console for JavaScript errors. Verify SSE events are being received:
```javascript
// In browser console, check EventSource state
// Events should fire for 'game-update' event type
```

### CORS Errors

**Symptom:** `Access-Control-Allow-Origin` errors in console

**Solution:** Ensure backend has CORS configured for `http://localhost:3000`

**Backend CORS headers required:**
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Credentials: true
```

### ESLint Configuration Errors

**Symptom:** `No ESLint configuration found` errors

**Solution:** Create `eslint.config.js` in the frontend root:

```javascript
import js from "@eslint/js";
import globals from "globals";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import tseslint from "typescript-eslint";

export default tseslint.config(
  { ignores: ["dist", "node_modules"] },
  {
    extends: [js.configs.recommended, ...tseslint.configs.recommended],
    files: ["**/*.{ts,tsx}"],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
    plugins: {
      "react-hooks": reactHooks,
      "react-refresh": reactRefresh,
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      "react-refresh/only-export-components": [
        "warn",
        { allowConstantExport: true },
      ],
    },
  }
);
```

Then install dependencies:
```bash
npm install -D globals typescript-eslint --legacy-peer-deps
```

### Backend Not Responding

**Symptom:** "Failed to create session" error

**Solution:**
1. Verify backend is running: `curl http://localhost:8080/api/sessions -X POST`
2. Check backend logs for errors
3. Ensure port 8080 is not blocked by firewall

### Build Failures

**Symptom:** `npm run build` fails

**Solutions:**
1. Clear node_modules and reinstall:
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```
2. Check TypeScript errors:
   ```bash
   npx tsc --noEmit
   ```
3. Verify all imports use correct paths

## Scripts Reference

| Command | Description |
|---------|-------------|
| `npm run dev` | Start development server on port 3000 |
| `npm run build` | Build for production |
| `npm run preview` | Preview production build locally |
| `npm run lint` | Run ESLint on all source files |

## License

Private - Internal use only
