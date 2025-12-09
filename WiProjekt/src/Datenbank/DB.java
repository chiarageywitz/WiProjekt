package Datenbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DB {
	
	public static void main(String[] args) {
		System.out.println("MySQL Connect Example V2 AWS");
		
		Connection conn = null;
		
		String url = "jdbc:mysql://3.69.96.96:80/";
		String dbName = "db1";
		
		String driver = "com.mysql.cj.jdbc.Driver";
		String userName = "db1";
		
		String password = "!db1.wip25?SS1";
		
		try {
			
			Class.forName(driver);
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			System.out.println("Connected to the database");
			
			Statement stmt = conn.createStatement();
			ResultSet rs;
			
			rs = stmt.executeQuery("SELECT MNR, Nachname, Vorname, email FROM studentendb");
			
			while (rs.next()) {
				int mnr = rs.getInt("MNR");
				String nachname = rs.getString("Nachname");
				String vorname = rs.getString("Vorname");
				String email = rs.getString("email");
				System.out.println("Matrikelnummer: " + mnr + " Nachname: " + nachname + " Vorname: " + vorname + " Email: " + email);
			}
			
			conn.close();
			System.out.println("Disconnected from database");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}