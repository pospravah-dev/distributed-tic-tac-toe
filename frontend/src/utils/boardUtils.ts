import { GameBoard, GameStatus, Player } from '../types/game';

const WIN_PATTERNS = [
  [0, 1, 2], [3, 4, 5], [6, 7, 8],
  [0, 3, 6], [1, 4, 7], [2, 5, 8],
  [0, 4, 8], [2, 4, 6]
];

export function createBoard(): (Player | null)[][] {
  return Array(3).fill(null).map(() => Array(3).fill(null));
}

export function checkWin(board: (Player | null)[][], player: Player): boolean {
  return WIN_PATTERNS.some(pattern =>
    pattern.every(pos => board[Math.floor(pos / 3)][pos % 3] === player)
  );
}

export function checkDraw(board: (Player | null)[][]): boolean {
  return board.every(row => row.every(cell => cell !== null));
}

export function convertArrayTo2D(arr: (Player | null)[]): (Player | null)[][] {
  const result: (Player | null)[][] = [];
  for (let i = 0; i < 3; i++) {
    result.push([]);
    for (let j = 0; j < 3; j++) {
      result[i].push(arr[i * 3 + j]);
    }
  }
  return result;
}

export function getPosition(row: number, col: number): number {
  return row * 3 + col;
}
