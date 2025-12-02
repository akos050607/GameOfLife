package view;

import controller.GameController;
import model.CellState;

import javax.swing.*;
import java.awt.*;

public class ToolsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
	private GameController controller;

    public ToolsPanel(GameController controller) {
        this.controller = controller;
        
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createTitledBorder("Eszközök (Wireworld)"));

        createToolButton("Vezető", CellState.CONDUCTOR, Color.YELLOW);
        createToolButton("Fej", CellState.HEAD, Color.BLUE);
        createToolButton("Farok", CellState.TAIL, Color.RED);
        createToolButton("Törlés", CellState.EMPTY, Color.WHITE);
    }

    private void createToolButton(String name, CellState state, Color color) {
        JButton btn = new JButton(name);
        btn.setBackground(color);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        
        btn.addActionListener(e -> controller.setSelectedToolState(state));
        
        add(btn);
    }
}