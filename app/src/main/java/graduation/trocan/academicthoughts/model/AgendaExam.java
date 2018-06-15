package graduation.trocan.academicthoughts.model;

import java.util.ArrayList;
import java.util.Date;

public class AgendaExam {

    private String course;
    private String professor;
    private Date date;
    private ArrayList<String> groups;
    private String uid;
    private Boolean isSet;

    public AgendaExam(String course, String professor, Date date, ArrayList<String> groups, String uid, Boolean isSet) {
        this.course = course;
        this.professor = professor;
        this.date = date;
        this.groups = groups;
        this.uid = uid;
        this.isSet = isSet;
    }



    public AgendaExam (){}

    public AgendaExam(String course, String professor, Date date, ArrayList<String> groups) {
        this.course = course;
        this.professor = professor;
        this.date = date;
        this.groups = groups;
    }

    public AgendaExam(String course, String professor, Date date) {
        this.course = course;
        this.professor = professor;
        this.date = date;
    }

    public AgendaExam(String course, Date date) {
        this.course = course;
        this.date = date;
    }

    public Boolean getSet() {
        return isSet;
    }

    public void setSet(Boolean set) {
        isSet = set;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
