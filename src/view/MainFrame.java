package view;

import controller.GameController;
import model.*;
import persistence.FileManager;
import persistence.SimulationState;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Az alkalmazás főablaka (JFrame).
 * Összefogja a menüt, a vezérlőgombokat és a szimulációs panelt.
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
	private GameController controller;
    private SimulationPanel simulationPanel;
    private ToolsPanel toolPanel;

    public MainFrame() {
        setTitle("Sejtautomaták - Game of Life & Wireworld");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Kezdőállapot
        Grid initialGrid = new Grid(50, 30, CellState.DEAD);
        SimulationRule initialRule = new GameOfLifeRule();
        
        controller = new GameController(initialGrid, initialRule);
        simulationPanel = new SimulationPanel(controller);
        controller.setView(simulationPanel);
        buildMenuBar();
        buildControlPanel();
        toolPanel = new ToolsPanel(controller);
        add(toolPanel, BorderLayout.NORTH);

        add(simulationPanel, BorderLayout.CENTER);

        setupKeyBindings();
        updateToolPanelVisibility(); 
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * A felső menüsor felépítése (Fájl, Szerkesztés).
     */
    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Fájl menü
        JMenu fileMenu = new JMenu("Fájl");
        JMenuItem newItem = new JMenuItem("Új szimuláció");
        newItem.addActionListener(e -> showNewSimulationDialog());
        
        JMenuItem saveItem = new JMenuItem("Mentés");
        saveItem.addActionListener(e -> saveSimulation());

        JMenuItem loadItem = new JMenuItem("Betöltés");
        loadItem.addActionListener(e -> loadSimulation());

        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
 
        JMenu editMenu = new JMenu("Szerkesztés");
        JMenuItem rulesItem = new JMenuItem("Szabályok testreszabása");
        rulesItem.addActionListener(e -> showRuleCustomizationDialog());
        editMenu.add(rulesItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Felépíti az alsó vezérlőpanelt, amely a szimuláció indításáért és megállításáért
     * felelős gombokat, valamint az információs címkét tartalmazza.
     */
    private void buildControlPanel() {
        JPanel controlPanel = new JPanel();
        
        JButton startButton = new JButton("Indítás");
        JButton stopButton = new JButton("Megállítás");
        
        startButton.addActionListener(e -> controller.startSimulation());
        stopButton.addActionListener(e -> controller.stopSimulation());
        
        JLabel infoLabel = new JLabel(" | Sebesség: Fel/Le | Rajz: Katt/Húzás");

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(infoLabel);

        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Megjelenít egy párbeszédablakot az új szimuláció paramétereinek (méret, típus)
     * megadásához, majd a felhasználó döntése alapján inicializálja az új rácsot.
     */
    private void showNewSimulationDialog() {
        JTextField widthField = new JTextField("50");
        JTextField heightField = new JTextField("30");
        String[] types = {"Életjáték (Game of Life)", "Wireworld"};
        JComboBox<String> typeCombo = new JComboBox<>(types);

        Object[] message = {
            "Típus:", typeCombo, "Szélesség:", widthField, "Magasság:", heightField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Új szimuláció", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                int w = Integer.parseInt(widthField.getText());
                int h = Integer.parseInt(heightField.getText());
                String selectedType = (String) typeCombo.getSelectedItem();
                
                Grid newGrid;
                SimulationRule newRule;
                CellState defaultState;

                if (selectedType.contains("Wireworld")) {
                    defaultState = CellState.EMPTY;
                    newRule = new WireWorldRule();
                } else {
                    defaultState = CellState.DEAD;
                    newRule = new GameOfLifeRule();
                }
                newGrid = new Grid(w, h, defaultState);

                resetController(newGrid, newRule);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Hibás számformátum!", "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Megnyit egy fájlválasztó ablakot a szimuláció jelenlegi állapotának
     * JSON formátumban történő mentéséhez a FileManager segítségével.
     */
    private void saveSimulation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Szimuláció mentése");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".json")) {
                file = new File(file.getAbsolutePath() + ".json");
            }
            
            try {
                FileManager fm = new FileManager();
                fm.saveSimulation(file, controller.getGrid(), controller.getRule()); 
                JOptionPane.showMessageDialog(this, "Sikeres mentés!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hiba: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Megnyit egy fájlválasztó ablakot egy korábban mentett szimuláció betöltéséhez,
     * majd frissíti a játékteret a betöltött adatokkal.
     */
    private void loadSimulation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Szimuláció betöltése");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileManager fm = new FileManager();
                SimulationState state = fm.loadSimulation(file); 
                resetController(state.grid, state.rule);
                JOptionPane.showMessageDialog(this, "Sikeres betöltés!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hiba: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Alaphelyzetbe állítja a vezérlőt és a felhasználói felületet egy új rács és szabályrendszer alapján.
     * @param newGrid Az új rács objektum.
     * @param newRule Az új szabályrendszer.
     */
    private void resetController(Grid newGrid, SimulationRule newRule) {
        controller.stopSimulation();
        remove(simulationPanel);
        remove(toolPanel);
        
        controller = new GameController(newGrid, newRule);
        
        simulationPanel = new SimulationPanel(controller);
        toolPanel = new ToolsPanel(controller);
        
        controller.setView(simulationPanel);
        
        add(simulationPanel, BorderLayout.CENTER);
        add(toolPanel, BorderLayout.NORTH);
        
        updateToolPanelVisibility();
        revalidate();
        repaint();
    }

    /**
     * Megjeleníti a szabályszerkesztő ablakot (Settings), ahol a túlélési és születési
     * szabályok módosíthatók. Ez a funkció csak Életjáték módban érhető el.
     */
    private void showRuleCustomizationDialog() {
        if (controller.getRule() instanceof GameOfLifeRule) {
            GameOfLifeRule rule = (GameOfLifeRule) controller.getRule();
            
            Settings settingsDialog = new Settings(this, rule);
            settingsDialog.setVisible(true);
            
        } else {
            JOptionPane.showMessageDialog(this, "Csak Életjátéknál elérhető!");
        }
    }

    /**
     * Frissíti az eszköztár láthatóságát az aktuális játéktípus alapján.
     */
    private void updateToolPanelVisibility() {
        SimulationRule rule = controller.getRule();
        toolPanel.setVisible(rule instanceof WireWorldRule);
    }
    
    /**
     * Beállítja a billentyűparancsokat a sebesség vezérléséhez (Fel/Le nyilak)
     */
    private void setupKeyBindings() {
        JRootPane rootPane = this.getRootPane();
        
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("UP"), "SPEED_UP");
                
        rootPane.getActionMap().put("SPEED_UP", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controller.increaseSpeed();
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("DOWN"), "SPEED_DOWN");
                
        rootPane.getActionMap().put("SPEED_DOWN", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controller.decreaseSpeed();
            }
        });
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}