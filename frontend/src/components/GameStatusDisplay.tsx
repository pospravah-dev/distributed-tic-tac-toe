import React from 'react';
import { GameStatus, Player } from '../types/game';

interface GameStatusProps {
  status: GameStatus;
  winner: Player | 'DRAW' | null;
}

export const GameStatusDisplay: React.FC<GameStatusProps> = ({ status, winner }) => {
  const getStatusMessage = () => {
    switch (status) {
      case 'WON':
        return winner === 'DRAW' 
          ? 'Game Draw!' 
          : `Player ${winner} Wins! 🎉`;
      case 'DRAW':
        return 'Game Draw! 🤝';
      default:
        return 'Game in Progress...';
    }
  };

  return (
    <div className="text-center py-4">
      <h2 className={`text-3xl font-bold ${status === 'WON' || status === 'DRAW' ? 'text-green-400' : 'text-slate-300'}`}>
        {getStatusMessage()}
      </h2>
    </div>
  );
};
