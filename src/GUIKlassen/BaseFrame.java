package GUIKlassen;

import javax.swing.*;
import java.awt.*;

public class BaseFrame extends JFrame {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 650;

    public BaseFrame(String title) {
        super(title);

        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);     // immer zentriert
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);                 // ihr arbeitet bewusst mit null
        setResizable(false);             // wichtig für Einheitlichkeit
    }
}