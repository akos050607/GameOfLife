package persistence;

import java.util.Set;

import model.CellState;

public class SimulationSaveData {
    public int width;
    public int height;
    public String type; // GAMEOFLIFE vagy WIREWORLD
    public CellState[][] cells;
    
    // Csak Életjátéknál releváns mezők (nullable)
    public Set<Integer> survivalRules;
    public Set<Integer> birthRules;

    public SimulationSaveData() {}
}
