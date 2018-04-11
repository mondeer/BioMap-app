package ke.co.imond.fingermap.Data;

/**
 * Developed by root on 12/10/17.
 */

public class Clockouts {
    private String userID;
    private String ClockinID;
    private String ClockoutID;
    private String Clocktime;

    public Clockouts() {

    }

    public Clockouts(String userID, String clockID, String clockoutID, String clocktime) {
        this.userID = userID;
        this.ClockinID = clockID;
        this.ClockoutID = clockoutID;
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

    public String getClockoutID() {
        return ClockoutID;
    }

    public void setClockoutID(String clockoutID) {
        ClockoutID = clockoutID;
    }

    public String getClocktime() {
        return Clocktime;
    }

    public void setClocktime(String clocktime) {
        Clocktime = clocktime;
    }
}
