import React, { useState, useEffect } from 'react';
import { Board } from './components/Board';
import { GameStatusDisplay } from './components/GameStatusDisplay';
import { MoveHistory } from './components/MoveHistory';
import { useGameLogic } from './hooks/useGameLogic';

const API_BASE = 'http://localhost:8080/api';

export default function App() {
  const { gameState, handleSSEEvent, makeMove, resetGame } = useGameLogic();
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [connectError, setConnectError] = useState<string | null>(null);
  const [isSimulating, setIsSimulating] = useState(false);

  useEffect(() => {
    if (!sessionId) return;

    const eventSource = new EventSource(`${API_BASE}/events/${sessionId}`);
    
    eventSource.addEventListener('game-update', (event) => {
      const gameState = JSON.parse(event.data);
      handleSSEEvent(gameState);
    });

    eventSource.onerror = (error) => {
      console.error('SSE connection error:', error);
      eventSource.close();
    };

    return () => {
      eventSource.close();
    };
  }, [sessionId, handleSSEEvent]);

  const startSimulation = async () => {
    try {
      const sessionResponse = await fetch(`${API_BASE}/sessions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
      });

      if (!sessionResponse.ok) {
        throw new Error('Failed to create session');
      }

      const sessionData = await sessionResponse.json();
      setSessionId(sessionData.id);
      setIsSimulating(true);
      setConnectError(null);

      const simulateResponse = await fetch(`${API_BASE}/sessions/${sessionData.id}/simulate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
      });

      if (!simulateResponse.ok) {
        throw new Error('Failed to start simulation');
      }

    } catch (error) {
      setConnectError(`Failed to start simulation: ${error instanceof Error ? error.message : 'Unknown error'}`);
      setIsSimulating(false);
    }
  };

  const handleMove = (row: number, col: number) => {
    if (!sessionId) return;
    makeMove(row, col);
  };

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 p-8">
      <div className="max-w-2xl mx-auto">
        <h1 className="text-4xl font-bold text-center mb-8 text-transparent bg-clip-text bg-gradient-to-r from-red-400 to-blue-400">
          Distributed Tic Tac Toe
        </h1>

        {connectError && (
          <div className="bg-red-900/50 text-red-100 p-4 rounded-lg mb-4">
            Error: {connectError}
          </div>
        )}

        <div className="text-center mb-6">
          <button
            onClick={startSimulation}
            disabled={!!sessionId || isSimulating}
            className="bg-blue-600 hover:bg-blue-500 disabled:bg-gray-400 text-white px-6 py-3 rounded-lg font-semibold transition-all"
          >
            {sessionId ? 'Simulation Running' : 'Start Simulation'}
          </button>
        </div>

        <GameStatusDisplay status={gameState.status} winner={gameState.winner} />

        <div className="flex justify-center mb-8">
          <Board
            cells={gameState.board.cells}
            onMove={handleMove}
            disabled={gameState.status !== 'IN_PROGRESS' || !sessionId}
          />
        </div>

        <MoveHistory moves={gameState.moveHistory} />

        {sessionId && gameState.status !== 'IN_PROGRESS' && (
          <div className="text-center mt-6">
            <button
              onClick={resetGame}
              className="text-slate-400 hover:text-white transition-colors"
            >
              Start New Game
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
