package controller;

import model.CellState;
import model.Grid;
import model.SimulationRule;
import view.SimulationPanel;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A játék logikáját és az időzítést vezérlő osztály (Controller).
 * Kapcsolatot tart a Modell (Grid) és a Nézet (SimulationPanel) között.
 */
public class GameController {

    private Grid grid;
    private SimulationRule currentRule;
    private SimulationPanel view;

    private Timer simulationTimer;
    private int speedDelay = 500;
    
    private CellState selectedToolState = CellState.CONDUCTOR;

    public GameController(Grid grid, SimulationRule rule) {
        this.grid = grid;
        this.currentRule = rule;

        this.simulationTimer = new Timer(speedDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextGeneration();
            }
        });
    }
    
    public SimulationRule getRule() {
        return currentRule;
    }

    public void setView(SimulationPanel view) {
        this.view = view;
    }

    public void startSimulation() {
        if (!simulationTimer.isRunning()) {
            simulationTimer.start();
        }
    }

    public void stopSimulation() {
        if (simulationTimer.isRunning()) {
            simulationTimer.stop();
        }
    }
    
    public boolean isRunning() {
        return simulationTimer.isRunning();
    }

    /**
     * Egy lépés a szimulációban (Generáció váltás).
     * Külön tömbbe számolja ki az új állapotokat,
     * hogy a frissítés "egyszerre" történjen
     */
    public void nextGeneration() {
        int width = grid.getWidth();
        int height = grid.getHeight();

        CellState[][] nextStates = new CellState[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                nextStates[y][x] = currentRule.calculateNextState(grid, x, y);
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid.setCell(x, y, nextStates[y][x]);
            }
        }

        if (view != null) {
            view.repaint();
        }
    }

    /**
     * Egérkattintás kezelése.
     * Életjátéknál: Váltogatja az állapotot (Alive/Dead).
     * Wireworldnél: Az eszköztáron kiválasztott elemet rajzolja.
     */
    public void handleCellClick(int x, int y) {
        CellState current = grid.getCell(x, y);

        // Ellenőrizzük, hogy Életjáték vagy Wireworld módban vagyunk-e
        boolean isGameOfLife = current.isGameOfLife(); 

        if (isGameOfLife) {
            // Életjáték: Toggle
            if (current == CellState.ALIVE) {
                grid.setCell(x, y, CellState.DEAD);
            } else {
                grid.setCell(x, y, CellState.ALIVE);
            }
        } else {
            // Wireworld logika: "Ecset" használata
            grid.setCell(x, y, selectedToolState);
        }

        if (view != null) view.repaint();
    }

    public void increaseSpeed() {
        if (speedDelay > 50) {
            speedDelay -= 50;
            updateTimerSpeed();
        }
    }

    public void decreaseSpeed() {
        if (speedDelay < 2000) {
            speedDelay += 50;
            updateTimerSpeed();
        }
    }

    private void updateTimerSpeed() {
        boolean wasRunning = simulationTimer.isRunning();
        simulationTimer.setDelay(speedDelay);
        if (wasRunning) {
            simulationTimer.restart();
        }
    }
    
    /**
     * Közvetlen festés egy cellára (pl. egérhúzásnál).
     * Nem váltogat (toggle), hanem felülírja az állapotot.
     */
    public void paintCell(int x, int y, CellState state) {
        grid.setCell(x, y, state);
        if (view != null) view.repaint();
    }

    public CellState getSelectedToolState() {
        return selectedToolState;
    }
    
    public void setSelectedToolState(CellState state) {
        this.selectedToolState = state;
    }

    public void setRule(SimulationRule rule) {
        this.currentRule = rule;
    }

    public Grid getGrid() { return grid; }
}