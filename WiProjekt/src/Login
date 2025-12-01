package Fachobjekte;

import javax.swing.*;
import java.awt.*;

public class LoginFenster extends JFrame {

	public LoginFenster() {
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 250);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);

		ImageIcon logo = new ImageIcon();
		JLabel logoLabel = new JLabel(logo);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(logoLabel, gbc);

		JLabel userLabel = new JLabel("Benutzername:");
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(userLabel, gbc);

		JTextField userField = new JTextField(15);
		gbc.gridx = 1;
		panel.add(userField, gbc);

		JLabel passLabel = new JLabel("Passwort:");
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(passLabel, gbc);

		JPasswordField passField = new JPasswordField(15);
		gbc.gridx = 1;
		panel.add(passField, gbc);

		JButton loginButton = new JButton("Einloggen");
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(loginButton, gbc);

		add(panel);
		
		loginButton.addActionListener(e -> {
		    String user = userField.getText();
		    String pass = new String(passField.getPassword());

		    if(user.equals("admin") && pass.equals("1234")) {
		        JOptionPane.showMessageDialog(null, "Login erfolgreich!");
		    } else {
		        JOptionPane.showMessageDialog(null, "Benutzername oder Passwort falsch!");
		    }
		});
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {
			new LoginFenster().setVisible(true);
		});

	}

}
