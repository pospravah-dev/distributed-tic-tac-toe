interface GameState {
  board: { cells: (string | null)[][] };
  currentPlayer: string;
  status: string;
  winner: string | null;
  moveCount: number;
  moveHistory: { player: string; position: number }[];
}
