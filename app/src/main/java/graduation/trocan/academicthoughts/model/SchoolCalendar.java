package graduation.trocan.academicthoughts.model;

import java.util.ArrayList;

public class SchoolCalendar {

    private ArrayList<String> groups;
    private String day;
    private String hours;
    private String semigroup;
    private String title;
    private String type;
    private String week;
    private String professor;

    public SchoolCalendar(){

    }
    public SchoolCalendar(ArrayList<String> groups, String day, String hours, String semigroup, String title, String type, String week,  String professor) {
        this.groups = groups;
        this.day = day;
        this.hours = hours;
        this.semigroup = semigroup;
        this.title = title;
        this.type = type;
        this.week = week;
        this.professor = professor;
    }

    public SchoolCalendar(ArrayList<String> groups, String day, String hours, String title, String type, String professor) {
        this.groups = groups;
        this.day = day;
        this.hours = hours;
        this.semigroup = semigroup;
        this.title = title;
        this.type = type;
        this.professor = professor;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }
    public String showGroups(SchoolCalendar SchoolCalendar){
        StringBuilder showGroups = new StringBuilder();
        for (String group: SchoolCalendar.getGroups()
             ) {
            showGroups.append(group + " ");
        }
        return showGroups.toString();
    }
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getSemigroup() {
        return semigroup;
    }

    public void setSemigroup(String semigroup) {
        this.semigroup = semigroup;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }
}
