package GUIKlassen;

import javax.swing.SwingUtilities;

/**
 * Startklasse des Portals. Initialisiert das Login-Fenster beim Start der
 * Anwendung.
 * 
 */
public class StartPortal {

	/**
	 * Main-Methode zum Starten des Hochschulportals.
	 * Startet die GUI im Event-Dispatch-Thread und zeigt das Login-Fenster an.
	 * 
	 * @param args Kommandozeilenargumente (werden nicht verwendet)
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			LoginFenster login = new LoginFenster();
			login.setVisible(true);
		});
	}
}