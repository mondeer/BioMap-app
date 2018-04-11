package ke.co.imond.fingermap.Data;

/**
 * Developed by root on 12/10/17.
 */

public class Clockins {
    private String userID;
    private String ClockinID;
    private String Clocktime;

    public Clockins() {

    }

    public Clockins(String userID, String clockinID, String clocktime) {
        this.userID = userID;
        this.ClockinID = clockinID;
        this.Clocktime = clocktime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getClockinID() {
        return ClockinID;
    }

    public void setClockinID(String clockinID) {
        ClockinID = clockinID;
    }

    public String getClocktime() {
        return Clocktime;
    }

    public void setClocktime(String clocktime) {
        Clocktime = clocktime;
    }
}
