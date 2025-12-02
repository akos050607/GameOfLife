package persistence;

import model.Grid;
import model.SimulationRule;

public class SimulationState {
    public Grid grid;
    public SimulationRule rule;
    
    public SimulationState(Grid grid, SimulationRule rule) {
        this.grid = grid;
        this.rule = rule;
    }

    public SimulationState() {
    }
}