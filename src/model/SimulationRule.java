package model;

public interface SimulationRule {
	CellState calculateNextState(Grid grid, int x, int y);
}
