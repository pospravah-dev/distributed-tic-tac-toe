export interface DeltaPatchEvent {
  gameId: string;
  moveNumber: number;
  boardState: string[];
  currentPlayer: string;
  status: string;
  winner: string | null;
}
