package graduation.trocan.academicthoughts.model;

public class StudentMark {
    private String professor;
    private String course;
    private int mark;

    public StudentMark(String professor, String course, int mark) {
        this.professor = professor;
        this.course = course;
        this.mark = mark;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }
}
