package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A szimulációs teret reprezentáló rács.
 * Tárolja a cellák állapotát és kezeli a méreteket.
 */
public class Grid {
    private List<List<CellState>> cells;
    private int width;
    private int height;
    private CellState defaultState;

    public Grid(int width, int height, CellState defaultState) {
        this.width = width;
        this.height = height;
        this.defaultState = defaultState;
        this.cells = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            List<CellState> row = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                row.add(defaultState);
            }
            cells.add(row);
        }
    }
    /**
     * Visszaadja egy adott koordinátán lévő cella állapotát.
     * Ha a koordináta a rácson kívül esik, az alapértelmezett állapotot adja vissza.
     * @param x A cella oszlopindexe.
     * @param y A cella sorindexe.
     * @return A cella állapota (CellState).
     */
    public CellState getCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return defaultState;
        }
        return cells.get(y).get(x);
    }
    
    /**
     * Beállítja egy adott cella állapotát.
     * Csak akkor hajtódik végre, ha a koordináták érvényesek.
     */
    public void setCell(int x, int y, CellState state) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            cells.get(y).set(x, state);
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    /**
     * Átméretezi a rácsot az új szélességre és magasságra.
     * A jelenlegi tartalom törlődik, és egy üres rács jön létre!
     */
    public void resize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
        this.cells = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            List<CellState> row = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                row.add(defaultState);
            }
            cells.add(row);
        }
    }
}
