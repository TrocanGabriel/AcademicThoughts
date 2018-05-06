package graduation.trocan.academicthoughts.model;

public class ProfessorMark {

    private String course;
    private String email;
    private String first_name;
    private String last_name;
    private String group;
    private int mark;

    public ProfessorMark( ) {
    }

    public ProfessorMark(String course, String email, String first_name, String last_name, String group, int mark) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.mark = mark;
        this.group = group;
    }

    public ProfessorMark(String course, String email, String first_name, String last_name, String group) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.group = group;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getGroup() {return group;}

    public void setGroup(String group) {this.group = group;}

    public String getCourse() {return course;}

    public void setCourse(String course) {this.course = course;}
}
