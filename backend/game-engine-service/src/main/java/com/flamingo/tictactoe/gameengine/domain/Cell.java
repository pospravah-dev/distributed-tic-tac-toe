package com.flamingo.tictactoe.gameengine.domain;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final String value;

    public static final Cell EMPTY = new Cell(" ");

    public Cell(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public boolean isEmpty() {
        return " ".equals(value);
    }
}
