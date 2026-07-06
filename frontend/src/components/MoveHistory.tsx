import React from 'react';
import { Move } from '../types/game';

interface MoveHistoryProps {
  moves: Move[];
}

export const MoveHistory: React.FC<MoveHistoryProps> = ({ moves }) => {
  return (
    <div className="mt-6 p-4 bg-slate-800 rounded-lg">
      <h3 className="text-lg font-semibold mb-2 text-slate-300">Move History</h3>
      {moves.length === 0 ? (
        <p className="text-slate-500 italic">No moves yet</p>
      ) : (
        <ol className="space-y-1 text-sm">
          {moves.map((move, idx) => (
            <li key={idx} className="flex justify-between">
              <span>Move {idx + 1}</span>
              <span className={move.player === 'X' ? 'text-red-400' : 'text-blue-400'}>
                Player {move.player} → Position {move.position}
              </span>
            </li>
          ))}
        </ol>
      )}
    </div>
  );
};
