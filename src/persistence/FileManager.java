package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.*;

import java.io.*;

/**
 * A szimuláció mentését és betöltését végző osztály JSON formátumban.
 * A Google Gson könyvtárat használja a szerializációhoz.
 */
public class FileManager {

    private final Gson gson;

    public FileManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Elmenti a rács állapotát és a szabályokat egy JSON fájlba.
     * @param file A célfájl.
     * @param grid A mentendő rács.
     * @param rule A mentendő szabályrendszer.
     */
    public void saveSimulation(File file, Grid grid, SimulationRule rule) throws IOException {
        SimulationSaveData data = new SimulationSaveData();
        
        data.width = grid.getWidth();
        data.height = grid.getHeight();
        
        data.cells = new CellState[data.height][data.width];
        for (int y = 0; y < data.height; y++) {
            for (int x = 0; x < data.width; x++) {
                data.cells[y][x] = grid.getCell(x, y);
            }
        }

        if (rule instanceof WireWorldRule) {
            data.type = "WIREWORLD";
            data.survivalRules = null;
            data.birthRules = null;
        } else if (rule instanceof GameOfLifeRule) {
            data.type = "GAMEOFLIFE";
            GameOfLifeRule golRule = (GameOfLifeRule) rule;
            data.survivalRules = golRule.getSurvivalRules();
            data.birthRules = golRule.getBirthRules();
        }

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        }
    }

    /**
     * Betölt egy korábban mentett szimulációt.
     * Visszaállítja a rácsot, a cellákat és a szabályrendszert.
     * @return SimulationState objektum, ami tartalmazza a rácsot és a szabályt.
     */
    public SimulationState loadSimulation(File file) throws IOException {
        SimulationSaveData data;
        try (Reader reader = new FileReader(file)) {
            data = gson.fromJson(reader, SimulationSaveData.class);
        }

        CellState defaultState = "WIREWORLD".equals(data.type) ? CellState.EMPTY : CellState.DEAD;
        Grid grid = new Grid(data.width, data.height, defaultState);

        if (data.cells != null) {
            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    if (y < data.cells.length && x < data.cells[y].length) {
                        grid.setCell(x, y, data.cells[y][x]);
                    }
                }
            }
        }

        SimulationRule rule;
        if ("WIREWORLD".equals(data.type)) {
            rule = new WireWorldRule();
        } else {
            GameOfLifeRule golRule = new GameOfLifeRule();
            if (data.survivalRules != null && data.birthRules != null) {
                golRule.setRules(data.survivalRules, data.birthRules);
            }
            rule = golRule;
        }

        SimulationState state = new SimulationState();
        state.grid = grid;
        state.rule = rule;
        return state;
    }
}