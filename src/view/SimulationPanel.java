package view;

import controller.GameController;
import model.CellState;
import model.Grid;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A szimulációs rács grafikus megjelenítéséért felelős Swing komponens.
 * Kezeli a kirajzolást és az egérinterakciót.
 */
public class SimulationPanel extends JPanel {

    private static final long serialVersionUID = 1L;
	private GameController controller;
    private int cellSize = 20;
    private boolean showGridLines = true;
    private CellState activeDragState = null; 

    public SimulationPanel(GameController controller) {
        this.controller = controller;
        if (controller != null) {
            controller.setView(this);
        }

        this.setBackground(Color.GRAY);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePress(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDrag(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                activeDragState = null;
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    /**
     * A komponens újrarajzolása.
     * Végigmegy a rácson és minden cellát a megfelelő színű négyzettel jelenít meg.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (controller == null || controller.getGrid() == null) return;

        Grid grid = controller.getGrid();
        int width = grid.getWidth();
        int height = grid.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CellState state = grid.getCell(x, y);
                g.setColor(state.getDefaultColor());
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);

                if (showGridLines) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    /**
     * Egérkattintás (lenyomás) kezelése.
     * Eldönti, hogy milyen műveletet kell végezni (Wireworld rajzolás vagy GoL toggle),
     * és beállítja az activeDragState-et a húzáshoz.
     */
    private void handleMousePress(MouseEvent e) {
        if (controller == null) return;
        
        int gridX = e.getX() / cellSize;
        int gridY = e.getY() / cellSize;
        Grid grid = controller.getGrid();

        if (isValidCoordinate(gridX, gridY, grid)) {
            CellState clickedCell = grid.getCell(gridX, gridY);

            if (clickedCell.isWireworld() || clickedCell == CellState.EMPTY) {
                activeDragState = controller.getSelectedToolState();
            } else {
                if (clickedCell == CellState.ALIVE) {
                    activeDragState = CellState.DEAD;
                } else {
                    activeDragState = CellState.ALIVE;
                }
            }

            controller.paintCell(gridX, gridY, activeDragState);
        }
    }
    
    /**
     * Egérhúzás kezelése.
     * A lenyomáskor meghatározott állapotot festi a cellákra.
     */
    private void handleMouseDrag(MouseEvent e) {
        if (controller == null || activeDragState == null) return;

        int gridX = e.getX() / cellSize;
        int gridY = e.getY() / cellSize;
        
        if (isValidCoordinate(gridX, gridY, controller.getGrid())) {
            controller.paintCell(gridX, gridY, activeDragState);
        }
    }
    
    private boolean isValidCoordinate(int x, int y, Grid grid) {
        return x >= 0 && x < grid.getWidth() && y >= 0 && y < grid.getHeight();
    }

    public void setCellSize(int size) {
        if (size > 0) {
            this.cellSize = size;
            repaint();
        }
    }
}