package Datenbank;

public class UserLoginResult {

    private final int mnr;
    private final String rolle;

    public UserLoginResult(int mnr, String rolle) {
        this.mnr = mnr;
        this.rolle = rolle;
    }

    public int getMnr() {
        return mnr;
    }

    public String getRolle() {
        return rolle;
    }
}
        