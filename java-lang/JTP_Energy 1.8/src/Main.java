import GUI.MyFrame;

import java.awt.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Opening GUI..");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyFrame();
            }
        });
    }
}
