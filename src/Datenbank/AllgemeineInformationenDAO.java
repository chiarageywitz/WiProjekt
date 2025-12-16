package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AllgemeineInformationenDAO {
	
	    public static void speichern(
	            int mnr,
	            String thema,
	            String unternehmen,
	            String zeitraum,
	            String betreuerHft,
	            String betreuerUnternehmen,
	            boolean ndaNoetig,
	            String ndaPfad
	    ) throws Exception {

	        String sql = """
	            INSERT INTO allgemeine_informationen
	            (mnr, thema, unternehmen, zeitraum, betreuer_hft, betreuer_unternehmen, nda_noetig, nda_dateipfad)
	            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
	            ON DUPLICATE KEY UPDATE
	                thema = VALUES(thema),
	                unternehmen = VALUES(unternehmen),
	                zeitraum = VALUES(zeitraum),
	                betreuer_hft = VALUES(betreuer_hft),
	                betreuer_unternehmen = VALUES(betreuer_unternehmen),
	                nda_noetig = VALUES(nda_noetig),
	                nda_dateipfad = VALUES(nda_dateipfad)
	        """;

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setInt(1, mnr);
	            ps.setString(2, thema);
	            ps.setString(3, unternehmen);
	            ps.setString(4, zeitraum);
	            ps.setString(5, betreuerHft);
	            ps.setString(6, betreuerUnternehmen);
	            ps.setBoolean(7, ndaNoetig);
	            ps.setString(8, ndaPfad);

	            ps.executeUpdate();
	        }
	    }
	}

