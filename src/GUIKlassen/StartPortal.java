package GUIKlassen;

import javax.swing.SwingUtilities;

/**
 * Startklasse des Hochschulportals.
 *
 * Initialisiert beim Start der Anwendung das Login-Fenster und sorgt dafür,
 * dass die GUI im Event-Dispatch-Thread ausgeführt wird.
 */
public class StartPortal {

	/**
	 * Main-Methode zum Starten des Portals.
	 *
	 * @param args Kommandozeilenargumente, werden nicht verwendet
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			LoginFenster login = new LoginFenster();
			login.setVisible(true);
		});
	}
}