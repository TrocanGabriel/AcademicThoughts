package graduation.trocan.academicthoughts.model;

import java.util.Date;

public class ProposedDays {

    private String student;
    private Date date;

    public ProposedDays() {
    }

    public ProposedDays(String student, Date date) {
        this.student = student;
        this.date = date;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
