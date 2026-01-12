package GUIKlassen;

import javax.swing.*;
import java.awt.*;

/**
 * Basisklasse für alle Hauptfenster der Anwendung.
 * Stellt Standardgröße, Positionierung und grundlegende Einstellungen bereit.
 */
public class BaseFrame extends JFrame {

    /** Standardbreite des Fensters */
    public static final int WIDTH = 1100;

    /** Standardhöhe des Fensters */
    public static final int HEIGHT = 650;

    /**
     * Konstruktor für ein Fenster mit Standardgröße, zentrierter Position
     * und festen Layout-Einstellungen.
     *
     * @param title Der Titel des Fensters
     */
    public BaseFrame(String title) {
        super(title);

        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);     // Fenster immer zentriert
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);                 // bewusstes Null-Layout
        setResizable(false);             // Größe fixiert für Einheitlichkeit
    }
}
