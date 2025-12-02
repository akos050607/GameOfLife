package model;

import java.util.HashSet;
import java.util.Set;

/**
 * A klasszikus Conway-féle Életjáték (Game of Life) szabályait megvalósító osztály.
 * Támogatja az egyedi S/B szabályokat is.
 */
public class GameOfLifeRule implements SimulationRule {
    
    private Set<Integer> survivalRules;
    private Set<Integer> birthRules;

    public GameOfLifeRule() {
        this.survivalRules = new HashSet<>();
        this.birthRules = new HashSet<>();
        
        // Alapértelmezett szabályok beállítása
        survivalRules.add(2);
        survivalRules.add(3);
        birthRules.add(3);
    }

    public void setRules(Set<Integer> survival, Set<Integer> birth) {
        this.survivalRules = new HashSet<>(survival);
        this.birthRules = new HashSet<>(birth);
    }
    
    /**
     * Szöveges input alapján állítja be a szabályokat.
     * Kiszűri a nem szám karaktereket.
     * @param survival Túlélési szabályok stringje (pl. "2,3" vagy "23")
     * @param birth Születési szabályok stringje (pl. "3")
     */
    public void setRulesFromString(String survival, String birth) {
        this.survivalRules = parseRuleString(survival);
        this.birthRules = parseRuleString(birth);
    }
    
    /**
     * Segédfüggvény a stringek számhalmazzá alakításához.
     */
    private Set<Integer> parseRuleString(String input) {
        Set<Integer> rules = new HashSet<>();
        if (input == null || input.trim().isEmpty()) return rules;
        
        String clean = input.replaceAll("[^0-9,]", "");
        
        if (clean.contains(",")) {
            String[] parts = clean.split(",");
            for (String p : parts) {
                if (!p.isEmpty()) rules.add(Integer.parseInt(p));
            }
        } else {
            for (char c : clean.toCharArray()) {
                rules.add(Character.getNumericValue(c));
            }
        }
        return rules;
    }
    public HashSet<Integer> getSurvivalRules()
    {
    	return (HashSet<Integer>) survivalRules;
    }
    public HashSet<Integer> getBirthRules()
    {
    	return (HashSet<Integer>) birthRules;
    }
    
    /**
     * Kiszámolja a cella következő állapotát.
     */
    @Override
    public CellState calculateNextState(Grid grid, int x, int y) {
        CellState currentState = grid.getCell(x, y);
        int aliveNeighbors = countAliveNeighbors(grid, x, y);

        if (currentState == CellState.ALIVE) {
            if (survivalRules.contains(aliveNeighbors)) {
                return CellState.ALIVE;
            } else {
                return CellState.DEAD;
            }
        } else {
            if (birthRules.contains(aliveNeighbors)) {
                return CellState.ALIVE;
            } else {
                return CellState.DEAD;
            }
        }
    }

    /*
     * Megszámolja az élő szomszédokat
     */
    private int countAliveNeighbors(Grid grid, int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (grid.getCell(x + j, y + i) == CellState.ALIVE) {
                    count++;
                }
            }
        }
        return count;
    }
}
