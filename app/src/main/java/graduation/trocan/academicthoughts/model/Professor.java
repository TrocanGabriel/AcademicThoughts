package graduation.trocan.academicthoughts.model;

import java.util.ArrayList;

public class Professor {

    private String firstName;
    private String lastName;
    private ArrayList<String> course;
    private ArrayList<String> groups;

    public Professor() {}


    public Professor(String firstName, String lastName, ArrayList<String> course, ArrayList<String> groups) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.course = course;
        this.groups = groups;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<String> getCourse() {
        return course;
    }

    public void setCourse(ArrayList<String> course) {
        this.course = course;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }
}
