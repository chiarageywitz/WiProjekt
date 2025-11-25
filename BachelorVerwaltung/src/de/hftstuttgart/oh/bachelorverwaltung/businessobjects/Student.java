package de.hftstuttgart.oh.bachelorverwaltung.businessobjects;

public class Student {
	private int mnr;
	private String vorname;
	private String nachname;
	
	public Student(int mnr, String vorname, String nachname) {
		this.mnr = mnr;
		this.vorname = vorname;
		this.nachname = nachname;
	}
	public int getMnr() {
		return mnr;
	}
	public void setMnr(int mnr) {
		this.mnr = mnr;
	}
	public String getVorname() {
		return vorname;
	}
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	public String getNachname() {
		return nachname;
	}
	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

}
