package model;

import java.awt.Color;

public enum CellState {
    // game of life
    DEAD,
    ALIVE,

    // wireworld
    EMPTY,
    CONDUCTOR,
    HEAD,
    TAIL;

    public boolean isGameOfLife() {
        return this == DEAD || this == ALIVE;
    }

    public boolean isWireworld() {
        return this == EMPTY || this == CONDUCTOR || this == HEAD || this == TAIL;
    }

    public Color getDefaultColor() {
        switch (this) {
	        case DEAD: return Color.WHITE;
            case ALIVE: return Color.BLACK;
            
            case EMPTY: return Color.WHITE;
            case CONDUCTOR: return Color.YELLOW;
            case HEAD: return Color.BLUE;
            case TAIL: return Color.RED;
            default: return Color.GRAY;
        }
    }
}
