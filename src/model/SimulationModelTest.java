package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

public class SimulationModelTest {

    @Test
    public void testGridGetCellOutOfBounds() {
        // Teszteljük, hogy a pályán kívüli koordináta az alapértelmezett értéket adja-e
        Grid grid = new Grid(10, 10, CellState.DEAD);
        
        // Mínusz koordináták
        assertEquals(CellState.DEAD, grid.getCell(-1, -1));
        // Túl nagy koordináták
        assertEquals(CellState.DEAD, grid.getCell(100, 100));
    }

    @Test
    public void testGridSetAndGetCell() {
        // Teszteljük a cella írását és olvasását
        Grid grid = new Grid(5, 5, CellState.DEAD);
        
        grid.setCell(2, 2, CellState.ALIVE);
        
        // Ellenőrizzük, hogy megváltozott-e
        assertEquals(CellState.ALIVE, grid.getCell(2, 2));
        // Ellenőrizzük, hogy más nem változott-e
        assertEquals(CellState.DEAD, grid.getCell(0, 0));
    }

    @Test
    public void testGridResize() {
        // Teszteljük az átméretezést
        Grid grid = new Grid(5, 5, CellState.DEAD);
        grid.setCell(0, 0, CellState.ALIVE); // Ez el fog veszni
        
        grid.resize(20, 10);
        
        assertEquals(20, grid.getWidth());
        assertEquals(10, grid.getHeight());
        // Az átméretezés törli a tartalmat a specifikáció szerint, tehát DEAD-nek kell lennie
        assertEquals(CellState.DEAD, grid.getCell(0, 0)); 
    }

    @Test
    public void testGoL_Underpopulation() {
        Grid grid = new Grid(3, 3, CellState.DEAD);
        grid.setCell(1, 1, CellState.ALIVE); // Középső élő
        grid.setCell(0, 0, CellState.ALIVE); // Csak 1 szomszéd
        
        GameOfLifeRule rule = new GameOfLifeRule();
        CellState next = rule.calculateNextState(grid, 1, 1);
        
        assertEquals(CellState.DEAD, next);
    }

    @Test
    public void testGoL_Survival() {
        Grid grid = new Grid(3, 3, CellState.DEAD);
        grid.setCell(1, 1, CellState.ALIVE); // Középső élő
        grid.setCell(0, 0, CellState.ALIVE); // 1. szomszéd
        grid.setCell(0, 1, CellState.ALIVE); // 2. szomszéd
        
        GameOfLifeRule rule = new GameOfLifeRule();
        CellState next = rule.calculateNextState(grid, 1, 1);
        
        assertEquals(CellState.ALIVE, next);
    }

    @Test
    public void testGoL_Overpopulation() {
        Grid grid = new Grid(3, 3, CellState.DEAD);
        grid.setCell(1, 1, CellState.ALIVE); // Középső
        // 4 szomszéd beállítása
        grid.setCell(0, 0, CellState.ALIVE);
        grid.setCell(0, 1, CellState.ALIVE);
        grid.setCell(0, 2, CellState.ALIVE);
        grid.setCell(1, 0, CellState.ALIVE);
        
        GameOfLifeRule rule = new GameOfLifeRule();
        CellState next = rule.calculateNextState(grid, 1, 1);
        
        assertEquals(CellState.DEAD, next);
    }

    @Test
    public void testGoL_Birth() {
        Grid grid = new Grid(3, 3, CellState.DEAD); // Középső HALOTT
        // 3 szomszéd
        grid.setCell(0, 0, CellState.ALIVE);
        grid.setCell(0, 1, CellState.ALIVE);
        grid.setCell(0, 2, CellState.ALIVE);
        
        GameOfLifeRule rule = new GameOfLifeRule();
        CellState next = rule.calculateNextState(grid, 1, 1);
        
        assertEquals(CellState.ALIVE, next);
    }

    @Test
    public void testGoL_ParsingRules() {
        GameOfLifeRule rule = new GameOfLifeRule();
        rule.setRulesFromString("5,6", "7"); // Custom szabályok
        
        Set<Integer> survival = rule.getSurvivalRules();
        Set<Integer> birth = rule.getBirthRules();
        
        assertTrue(survival.contains(5));
        assertTrue(survival.contains(6));
        assertFalse(survival.contains(2)); // Régi szabály törlődött
        assertTrue(birth.contains(7));
    }

    @Test
    public void testWireWorld_BasicTransitions() {
        Grid grid = new Grid(3, 3, CellState.EMPTY);
        WireWorldRule rule = new WireWorldRule();

        // Üres -> Üres
        grid.setCell(0, 0, CellState.EMPTY);
        assertEquals(CellState.EMPTY, rule.calculateNextState(grid, 0, 0));

        // Fej -> Farok
        grid.setCell(0, 1, CellState.HEAD);
        assertEquals(CellState.TAIL, rule.calculateNextState(grid, 0, 1));

        // Farok -> Vezető
        grid.setCell(0, 2, CellState.TAIL);
        assertEquals(CellState.CONDUCTOR, rule.calculateNextState(grid, 0, 2));
    }

    @Test
    public void testWireWorld_ConductorToHead() {
        Grid grid = new Grid(3, 3, CellState.EMPTY);
        grid.setCell(1, 1, CellState.CONDUCTOR); // Középen vezető
        
        // 1. eset: 1 Fej szomszéd -> Fejjé válik
        grid.setCell(0, 0, CellState.HEAD); 
        WireWorldRule rule = new WireWorldRule();
        assertEquals(CellState.HEAD, rule.calculateNextState(grid, 1, 1));
        
        // 2. eset: 2 Fej szomszéd -> Fejjé válik
        grid.setCell(0, 1, CellState.HEAD);
        assertEquals(CellState.HEAD, rule.calculateNextState(grid, 1, 1));
    }
    
    @Test
    public void testWireWorld_ConductorStaysConductor() {
        Grid grid = new Grid(3, 3, CellState.EMPTY);
        grid.setCell(1, 1, CellState.CONDUCTOR);
        
        // 3 Fej szomszéd (túl sok)
        grid.setCell(0, 0, CellState.HEAD);
        grid.setCell(0, 1, CellState.HEAD);
        grid.setCell(0, 2, CellState.HEAD);
        
        WireWorldRule rule = new WireWorldRule();
        assertEquals(CellState.CONDUCTOR, rule.calculateNextState(grid, 1, 1));
    }
}