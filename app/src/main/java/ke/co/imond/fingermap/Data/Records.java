package ke.co.imond.fingermap.Data;

public class Records {
    public String emppf_no;
    public String empName;
    public String clockin_time;
    public String clockout_time;
    public String time_in;

    public Records() {
    }

    public Records(String emppf_no, String empName, String clockin_time,
                   String clockout_time, String time_in) {
        this.emppf_no = emppf_no;
        this.empName = empName;
        this.clockin_time = clockin_time;
        this.clockout_time = clockout_time;
        this.time_in = time_in;
    }

    public String getEmppf_no() {
        return emppf_no;
    }

    public String getEmpName() {
        return empName;
    }

    public String getClockin_time() {
        return clockin_time;
    }

    public String getClockout_time() {
        return clockout_time;
    }

    public String getTime_in() {
        return time_in;
    }
}
