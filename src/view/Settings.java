package view;

import model.GameOfLifeRule;
import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class Settings extends JDialog {

    private static final long serialVersionUID = 1L;
	private JTextField surviveField;
    private JTextField birthField;
    private boolean saved = false;

    public Settings(Frame owner, GameOfLifeRule rule) {
        super(owner, "Szabályok testreszabása", true);
        setLayout(new GridLayout(3, 2, 10, 10));
        setSize(300, 150);
        setLocationRelativeTo(owner);

        String currentSurvive = rule.getSurvivalRules().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        
        String currentBirth = rule.getBirthRules().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        add(new JLabel("Túlélés ($S$) pl. 2,3:"));
        surviveField = new JTextField(currentSurvive);
        add(surviveField);

        add(new JLabel("Születés ($B$) pl. 3:"));
        birthField = new JTextField(currentBirth);
        add(birthField);

        JButton saveButton = new JButton("Mentés");
        JButton cancelButton = new JButton("Mégse");

        saveButton.addActionListener(e -> {
            try {
                rule.setRulesFromString(surviveField.getText(), birthField.getText());
                saved = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hibás formátum!", "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        add(saveButton);
        add(cancelButton);
    }

    public boolean isSaved() {
        return saved;
    }
}