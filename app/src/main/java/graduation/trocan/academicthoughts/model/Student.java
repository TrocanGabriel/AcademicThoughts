package graduation.trocan.academicthoughts.model;

public class Student {

    private String first_name;
    private String last_name;
    private String group;
    private String semigroup;

    public Student () {}

    public Student(String first_name, String last_name, String group, String semigroup) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.group = group;
        this.semigroup = semigroup;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSemigroup() {
        return semigroup;
    }

    public void setSemigroup(String semigroup) {
        this.semigroup = semigroup;
    }
}
