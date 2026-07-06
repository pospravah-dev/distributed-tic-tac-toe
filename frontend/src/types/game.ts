export type Player = 'X' | 'O';

export type GameStatus = 'IN_PROGRESS' | 'WON' | 'DRAW';

export interface GameBoard {
  cells: (Player | null)[][];
}

export interface Move {
  player: Player;
  position: number;
}

export interface GameState {
  board: GameBoard;
  currentPlayer: Player;
  status: GameStatus;
  winner: Player | 'DRAW' | null;
  moveCount: number;
  moveHistory: Move[];
}