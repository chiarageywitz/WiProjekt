package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AbgabeDAO {

    public static void speichern(int mnr, String dateipfad) throws Exception {
        String sql = """
            INSERT INTO abgabe_bachelorarbeit (mnr, dateipfad)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE
                dateipfad = VALUES(dateipfad)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mnr);
            ps.setString(2, dateipfad);
            ps.executeUpdate();
        }
    }
}
