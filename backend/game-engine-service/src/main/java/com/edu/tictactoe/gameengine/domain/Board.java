package com.edu.tictactoe.gameengine.domain;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final List<Cell> cells;

    public Board() {
        this.cells = new ArrayList<>(List.of(
            Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
            Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
            Cell.EMPTY, Cell.EMPTY, Cell.EMPTY
        ));
    }

    public List<Cell> getCells() {
        return List.copyOf(cells);
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }

    public void setCell(int index, Cell cell) {
        cells.set(index, cell);
    }

    public boolean isOccupied(int index) {
        return !cells.get(index).isEmpty();
    }
}
