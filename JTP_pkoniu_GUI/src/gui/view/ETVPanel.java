package gui.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Lukasz on 2014-05-28.
 */

public class ETVPanel extends JPanel {

    public JComboBox<String> energyComboBox;
    public JComboBox<String> particleComboBox;

    public JTextField energyTextField;
    public JTextField velocityTextField;

    private JButton convert;
    private JButton save;
    private JButton reset;
    private JButton close;

    public ETVPanel() {
        setLayout(new BorderLayout(20, 20));
        setPreferredSize(new Dimension(550, 200));

        // initialize components
        velocityTextField = new JTextField();
        velocityTextField.setPreferredSize(new Dimension(500, 30));
        energyTextField = new JTextField();
        energyTextField.setPreferredSize(new Dimension(500, 30));

        convert = new JButton("Convert");
        save = new JButton("Save");
        reset = new JButton("Reset");
        close = new JButton("close");

        // top
        JPanel top = new JPanel();
        top.setLayout(new GridLayout(2, 1, 5, 5));
        top.add(new JLabel("Blablalbalbalblablablalballbalblablablalbalbalbalblblablalbalbalbalb"));
        top.add(getComboPanel());

        // center
        JPanel center = new JPanel();
        center.setLayout(new FlowLayout());
        center.add(new JLabel("V ="));
        center.add(energyTextField);
        center.add(new JLabel("K ="));
        center.add(velocityTextField);

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

    private JPanel getComboPanel() {
        // create combo boxes
        String[] particles = { "electron", "proton"};
        particleComboBox = new JComboBox<String>(particles);
        particleComboBox.setSelectedIndex(0);

        // panel with combos
        JPanel top_combos = new JPanel();
        top_combos.setLayout(new GridLayout(1, 1, 5, 5));
        top_combos.add(particleComboBox);

        return top_combos;
    }

}
