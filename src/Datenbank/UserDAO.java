package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Util.PasswortUtil;


public class UserDAO {

	
	//Prüft Login-Daten und gibt Benutzerinformationen zurück. 
	//@return UserLoginResult oder null bei falschen Zugangsdaten
	
	public static UserLoginResult login(String email, String passwort) throws Exception {

		String sql = """
			    SELECT MNR, rolle, passwort
			    FROM studentendb
			    WHERE email = ?
			    LIMIT 1
			""";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, email);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            String gespeicherterHash = rs.getString("passwort");

	            if (PasswortUtil.pruefePasswort(passwort, gespeicherterHash)) {
	                int mnr = rs.getInt("MNR");
	                String rolle = rs.getString("rolle");
	                return new UserLoginResult(mnr, rolle);
	            }
	        }

	        return null;
	    }
	}
}