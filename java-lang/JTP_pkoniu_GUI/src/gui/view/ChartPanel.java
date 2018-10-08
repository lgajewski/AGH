package gui.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Lukasz on 2014-05-28.
 */

public class ChartPanel extends JPanel {

    public JComboBox<String> particleComboBox;
    public JComboBox<String> typeComboBox;

    public JTextField startFromField;
    public JTextField endAtField;

    private JButton convert;
    private JButton save;
    private JButton reset;
    private JButton close;

    public ChartPanel() {
        setLayout(new BorderLayout(20, 20));
        setPreferredSize(new Dimension(550, 200));

        // initialize components
        endAtField = new JTextField();
        endAtField.setPreferredSize(new Dimension(300, 30));
        startFromField = new JTextField();
        startFromField.setPreferredSize(new Dimension(300, 30));

        convert = new JButton("Check data");
        save = new JButton("Create chart");
        reset = new JButton("Save to *.csv");
        close = new JButton("Close");

        String[] particles = { "electron", "proton" };
        particleComboBox = new JComboBox<String>(particles);
        particleComboBox.setPreferredSize(new Dimension(150, 30));
        particleComboBox.setSelectedIndex(0);

        String[] types = { "E(V)", "V(E)" };
        typeComboBox = new JComboBox<String>(types);
        typeComboBox.setPreferredSize(new Dimension(150, 30));
        typeComboBox.setSelectedIndex(0);

        // top
        JPanel top = new JPanel();
        top.setLayout(new FlowLayout());
        top.add(new JLabel("Blablalbalbalblablablalballbalblablablalbalbalbalblblablalbalbalbalb"));

        // center
        JLabel start = new JLabel("Start from: ");
        JLabel end = new JLabel("End at: ");
        start.setPreferredSize(new Dimension(70, 30));
        end.setPreferredSize(new Dimension(70, 30));
        JPanel center = new JPanel();
        center.setLayout(new FlowLayout());
        center.add(start);
        center.add(startFromField);
        center.add(particleComboBox);
        center.add(end);
        center.add(endAtField);
        center.add(typeComboBox);

        // bottom
        JPanel bottom = new JPanel();
        bottom.setLayout(new GridLayout(1, 4, 5, 5));
        bottom.add(convert);
        bottom.add(save);
        bottom.add(reset);
        bottom.add(close);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

}
