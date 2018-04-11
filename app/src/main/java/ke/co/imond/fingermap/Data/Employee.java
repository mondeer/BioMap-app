package ke.co.imond.fingermap.Data;

/**
 * Developed by root on 12/25/17.
 */

public class Employee {
    private String empID;
    private String pf_no;
    private String first_name;
    private String last_name;
    private String designation;
    private String EmpNFC;
    private String fingerThumb;
    private String fingerIndex;
    private String department;
    private String section_name;
    private String section_id;

    public Employee(){

    }

    public Employee(String empID, String pf_no, String first_name, String last_name,
                    String designation, String empNFC, String fingerThumb,
                    String fingerIndex, String department, String section_name, String section_id) {
        this.empID = empID;
        this.pf_no = pf_no;
        this.first_name = first_name;
        this.last_name = last_name;
        this.designation = designation;
        EmpNFC = empNFC;
        this.fingerThumb = fingerThumb;
        this.fingerIndex = fingerIndex;
        this.department = department;
        this.section_name = section_name;
        this.section_id = section_id;
    }

    public String getEmpID() {
        return empID;
    }

    public String getPf_no() {
        return pf_no;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getDesignation() {
        return designation;
    }

    public String getEmpNFC() {
        return EmpNFC;
    }

    public String getFingerThumb() {
        return fingerThumb;
    }

    public String getFingerIndex() {
        return fingerIndex;
    }

    public String getDepartment() {
        return department;
    }

    public String getSection_name() {
        return section_name;
    }

    public String getSection_id() {
        return section_id;
    }
}
