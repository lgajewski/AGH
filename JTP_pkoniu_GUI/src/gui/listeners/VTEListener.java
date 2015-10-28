package gui.listeners;

import gui.view.VTEPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VTEListener implements ActionListener {

    VTEPanel vtePanel;

    public VTEListener(VTEPanel vtePanel) {
        this.vtePanel = vtePanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String energyType = (String) vtePanel.energyComboBox.getSelectedItem();
        String particle = (String) vtePanel.particleComboBox.getSelectedItem();
        String energy = vtePanel.energyTextField.getText();
        String velocity = vtePanel.velocityTextField.getText();
        System.out.println("Energy: " + energy + " type: " + energyType + " particle: " + particle
         + " velocity: " + velocity);
    }
}
