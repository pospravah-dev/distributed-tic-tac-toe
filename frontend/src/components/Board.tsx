import React from 'react';
import { Player } from '../types/game';

interface BoardProps {
  cells: (Player | null)[][];
  onMove: (row: number, col: number) => void;
  disabled: boolean;
}

export const Board: React.FC<BoardProps> = ({ cells, onMove, disabled }) => {
  return (
    <div className="grid grid-cols-3 gap-2 p-4 bg-slate-800 rounded-lg shadow-lg">
      {cells.map((row, rowIndex) => (
        row.map((cell, colIndex) => (
          <button
            key={`${rowIndex}-${colIndex}`}
            onClick={() => onMove(rowIndex, colIndex)}
            disabled={disabled || cell !== null}
            className={`
              w-20 h-20 text-5xl font-bold rounded-lg transition-all duration-200
              ${cell === 'X' ? 'text-red-400 bg-slate-700' : ''}
              ${cell === 'O' ? 'text-blue-400 bg-slate-700' : ''}
              ${cell === null && !disabled
                ? 'bg-slate-800 hover:bg-slate-700 cursor-pointer' 
                : 'bg-slate-800 cursor-default'}
              ${!disabled && cell === null ? 'hover:scale-105' : ''}
            `}
          >
            {cell ?? ''}
          </button>
        ))
      ))}
    </div>
  );
};
