package GUI;

import logic.Calculate;
import logic.ParticleCalc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Lukasz on 2014-05-12.
 */

public class MyFrame extends JFrame implements ActionListener {
    private JButton button1;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JTextField textField1;
    private ButtonGroup buttonGroup;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JPanel radioPanel;
    private JComboBox<String> energyList;
    private JPanel jPanel;

    public MyFrame() {
        super("Homework 8");

        // create jPanel
        jPanel = new JPanel();

        // creating components
        createComponents();

        jPanel.add(label1);
        jPanel.add(radioPanel);
        jPanel.add(label2);
        jPanel.add(energyList);
        jPanel.add(label3);
        jPanel.add(textField1);
        jPanel.add(new JPanel());
        jPanel.add(button1);
        jPanel.setBorder(new EmptyBorder(15, 10, 15, 10));

        pack();
        setContentPane(jPanel);
        setLayout(new GridLayout(4, 2, 5, 5));
        setSize(300, 200);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public void createComponents() {
        jPanel = new JPanel();
        label1 = new JLabel("Particle");
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label2 = new JLabel("Energy unit");
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        label3 = new JLabel("Enter energy");
        label3.setHorizontalAlignment(SwingConstants.CENTER);
        button1 = new JButton("Calculate");
        button1.addActionListener(this);
        textField1 = new JTextField("", 15);
        radioButton1 = new JRadioButton("electron");
        radioButton1.setSelected(true);
        radioButton2 = new JRadioButton("proton");
        buttonGroup = new ButtonGroup();
        buttonGroup.add(radioButton1);
        buttonGroup.add(radioButton2);

        //Put the radio buttons in a column in a panel.
        radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.add(radioButton1);
        radioPanel.add(radioButton2);

        // create combobox
        String[] energyUnits = { "MeV", "keV", "eV", "GeV" };
        energyList = new JComboBox<String>(energyUnits);
        energyList.setSelectedIndex(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Calculate calc = new ParticleCalc();
            String energyType = (String) energyList.getSelectedItem();
            double energyValue = Double.parseDouble(textField1.getText());
            double en = calc.getEnergyJ(energyType, energyValue);

            String particle = "";
            if (radioButton1.isSelected()) particle = radioButton1.getText();
            if (radioButton2.isSelected()) particle = radioButton2.getText();
            double mass = calc.getMass(particle);
            double c = calc.getC();

            double result = calc.getRelativeSpeed(en, mass, c);

            JOptionPane.showMessageDialog(null, "Relativistic speed of " + particle + " with " + energyValue + " " +
                    energyType + " energy is " + result);

        } catch (NumberFormatException e1) {
            JOptionPane.showMessageDialog(null, "Please enter double value");
        }
    }
}
