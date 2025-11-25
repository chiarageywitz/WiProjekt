package de.hftstuttgart.oh.bachelorverwaltung.dbaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import de.hftstuttgart.oh.bachelorverwaltung.businessobjects.Student;

public class DBAccess {

    private static Connection conn;

    // Verbindung zur DB aufbauen
    private static void initDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://3.69.96.96:80/db1",
                    "db1",
                    "!db1.wip25?SS1"
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Holt Student mit Matrikelnummer
    public static Student getStudentByMNR(int mnr) {
        if (conn == null) {
            initDB();
        }

        Student student = null;

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT MNR, Nachname, Vorname FROM studenten WHERE MNR = " + mnr);

            if (rs.next()) {
                student = new Student(
                        rs.getInt("MNR"),
                        rs.getString("Vorname"),
                        rs.getString("Nachname")
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return student;
    }
}