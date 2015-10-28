package gui.listeners;

import gui.view.ChartPanel;
import gui.view.ETVPanel;
import gui.view.MainFrame;
import gui.view.VTEPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuItemListener implements ActionListener {

    private MainFrame jFrame;

    public MenuItemListener(MainFrame jFrame) {
        this.jFrame = jFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if(cmd.equals("velocity > energy")) {
            reload(new VTEPanel());
        } else if(cmd.equals("energy > velocity")) {
            reload(new ETVPanel());
        } else if(cmd.equals("chart")) {
            reload(new ChartPanel());
        }
    }

    private void reload(Component c) {
        JPanel jPanel = (JPanel) jFrame.getContentPane();
        jPanel.removeAll();
        jPanel.add(c);
        jFrame.setContentPane(jPanel);
        jPanel.revalidate();
        jPanel.repaint();
        jFrame.jPanel = jPanel;
    }
}
