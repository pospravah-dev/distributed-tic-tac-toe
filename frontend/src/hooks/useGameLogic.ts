import { useState, useCallback } from 'react';
import { createBoard, checkWin, checkDraw, getPosition } from '../utils/boardUtils';
import {GameState, GameStatus, Player} from '../types/game';

export function useGameLogic() {
  const [gameState, setGameState] = useState<GameState>({
    board: { cells: createBoard() },
    currentPlayer: 'X',
    status: 'IN_PROGRESS',
    winner: null,
    moveCount: 0,
    moveHistory: [],
  });

  const handleSSEEvent = useCallback((gameState: {
    gameId: string;
    moveNumber: number;
    boardState: string[];
    currentPlayer: string;
    status: string;
    winner: string | null;
  }) => {
    const boardCells = gameState.boardState.map((cell: string) => cell === ' ' ? null : cell as Player);
    setGameState({
      board: { cells: convertTo2D(boardCells) },
      currentPlayer: gameState.currentPlayer as Player,
      status: gameState.status as GameStatus,
      winner: gameState.winner as Player | 'DRAW' | null,
      moveCount: gameState.moveNumber,
      moveHistory: Array.from({ length: gameState.moveNumber }, (_, i) => ({
        player: i % 2 === 0 ? 'X' : 'O' as Player,
        position: i,
      })),
    });
  }, []);

  const isValidMove = useCallback((row: number, col: number) => {
    return (
      gameState.status === 'IN_PROGRESS' &&
      gameState.board.cells[row][col] === null
    );
  }, [gameState]);

  const makeMove = useCallback((row: number, col: number) => {
    if (!isValidMove(row, col)) return;

    const position = getPosition(row, col);
    const newBoard = JSON.parse(JSON.stringify(gameState.board.cells));
    newBoard[row][col] = gameState.currentPlayer;

    const win = checkWin(newBoard, gameState.currentPlayer as Player);
    const draw = checkDraw(newBoard);

    setGameState({
      board: { cells: newBoard },
      currentPlayer: gameState.currentPlayer === 'X' ? 'O' : 'X',
      status: win ? 'WON' : draw ? 'DRAW' : 'IN_PROGRESS',
      winner: win ? gameState.currentPlayer : draw ? 'DRAW' : null,
      moveCount: gameState.moveCount + 1,
      moveHistory: [
        ...gameState.moveHistory,
        { player: gameState.currentPlayer, position },
      ],
    });
  }, [gameState, isValidMove]);

  const resetGame = useCallback(() => {
    setGameState({
      board: { cells: createBoard() },
      currentPlayer: 'X' as Player,
      status: 'IN_PROGRESS' as GameStatus,
      winner: null,
      moveCount: 0,
      moveHistory: [],
    });
  }, []);

  return {
    gameState,
    handleSSEEvent,
    isValidMove,
    makeMove,
    resetGame,
  };
}

function convertTo2D(arr: (string | null)[]): (string | null)[][] {
  const result: (string | null)[][] = [];
  for (let i = 0; i < 3; i++) {
    result.push([]);
    for (let j = 0; j < 3; j++) {
      result[i].push(arr[i * 3 + j] ?? null);
    }
  }
  return result;
}
