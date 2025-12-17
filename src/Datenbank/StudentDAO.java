package Datenbank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // ---------------- STUDENT INFO CLASS ----------------
    public static class StudentInfo {
        public int mnr;
        public String name;   // ✅ nur EIN Name
        public String thema;

        public StudentInfo(int mnr, String name, String thema) {
            this.mnr = mnr;
            this.name = name;
            this.thema = thema;
        }

        @Override
        public String toString() {
            return name + " (" + mnr + ")";
        }
    }

    // ---------------- STUDENTEN SUCHE ----------------
    public static List<StudentInfo> sucheStudenten(String name) throws Exception {
        List<StudentInfo> result = new ArrayList<>();

        String sql = """
            SELECT s.MNR, s.Vorname, a.thema
            FROM studentendb s
            LEFT JOIN allgemeine_informationen a ON s.MNR = a.mnr
            WHERE s.Vorname LIKE ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new StudentInfo(
                        rs.getInt("MNR"),
                        rs.getString("Vorname"), // ✅ Benutzername
                        rs.getString("thema")
                ));
            }
        }

        return result;
    }

    // ---------------- EINZELNER STUDENT ----------------
    public static StudentInfo getStudentInfo(int mnr) throws Exception {
        String sql = """
            SELECT s.MNR, s.Vorname, a.thema
            FROM studentendb s
            LEFT JOIN allgemeine_informationen a ON s.MNR = a.mnr
            WHERE s.MNR = ?
            LIMIT 1
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mnr);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new StudentInfo(
                        rs.getInt("MNR"),
                        rs.getString("Vorname"),
                        rs.getString("thema")
                );
            }
            return null;
        }
    }
}
