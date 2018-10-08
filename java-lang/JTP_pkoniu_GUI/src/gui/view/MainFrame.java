package gui.view;

import gui.listeners.MenuItemListener;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public JPanel jPanel;

    public MainFrame() {
        super("Tytul");
        setLayout(new BorderLayout());      // layout frame'a
        jPanel = new JPanel();

        createMenu();

        pack();
        setContentPane(jPanel);         // content pane
        setSize(600, 300);
        setLocationRelativeTo(null);    // wysrodkowanie
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    private void createMenu() {
        JMenuBar jMenuBar = new JMenuBar();

        JMenu show = new JMenu("Show");

        JMenuItem energyToVelocity = new JMenuItem("energy > velocity");
        JMenuItem velocityToEnergy = new JMenuItem("velocity > energy");
        JMenuItem chart = new JMenuItem("chart");

        energyToVelocity.addActionListener(new MenuItemListener(this));
        velocityToEnergy.addActionListener(new MenuItemListener(this));
        chart.addActionListener(new MenuItemListener(this));

        show.add(energyToVelocity);
        show.add(velocityToEnergy);
        show.add(chart);

        jMenuBar.add(show);

        this.setJMenuBar(jMenuBar);
    }

}
