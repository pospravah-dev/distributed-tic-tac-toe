package com.edu.tictactoe.gameengine.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardTest {

    @Test
    void constructor_shouldInitializeWithNineEmptyCells() {
        Board board = new Board();

        assertThat(board.getCells()).hasSize(9);
        assertThat(board.getCells()).allMatch(Cell::isEmpty);
    }

    @Test
    void getCell_shouldReturnCellAtIndex() {
        Board board = new Board();

        Cell cell = board.getCell(4);

        assertThat(cell).isNotNull();
        assertThat(cell.value()).isEqualTo(" ");
    }

    @Test
    void setCell_shouldUpdateCellAtIndex() {
        Board board = new Board();

        board.setCell(4, new Cell("X"));

        assertThat(board.getCell(4).value()).isEqualTo("X");
    }

    @Test
    void isOccupied_shouldReturnFalseForEmptyCell() {
        Board board = new Board();

        assertThat(board.isOccupied(0)).isFalse();
    }

    @Test
    void isOccupied_shouldReturnTrueForOccupiedCell() {
        Board board = new Board();
        board.setCell(4, new Cell("X"));

        assertThat(board.isOccupied(4)).isTrue();
    }

    @Test
    void isOccupied_shouldReturnFalseForDifferentIndex() {
        Board board = new Board();
        board.setCell(4, new Cell("X"));

        assertThat(board.isOccupied(0)).isFalse();
    }

    @Test
    void getCells_shouldReturnUnmodifiableCopy() {
        Board board = new Board();

        assertThatThrownBy(() -> board.getCells().add(new Cell("X")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void setCell_shouldAllowOverwriting() {
        Board board = new Board();
        board.setCell(0, new Cell("X"));

        board.setCell(0, new Cell("O"));

        assertThat(board.getCell(0).value()).isEqualTo("O");
    }

    @Test
    void isOccupied_shouldHandleAllPositions() {
        Board board = new Board();

        for (int i = 0; i < 9; i++) {
            assertThat(board.isOccupied(i)).isFalse();
            board.setCell(i, new Cell("X"));
            assertThat(board.isOccupied(i)).isTrue();
        }
    }
}
