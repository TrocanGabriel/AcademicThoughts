package graduation.trocan.academicthoughts.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Gabi on 29/04/2018.
 */

public class News {

    private Date date;
    private String text;
    private String uid;
    private String author;
    private ArrayList<String> target;

    public News(){ }

    public News(Date date, String text, String uid, ArrayList<String> target){
        this.uid = uid;
        this.date = date;
        this.text = text;
        this.target = target;
    }
    public News(Date date, String text, String uid, String author){
        this.uid = uid;
        this.date = date;
        this.text = text;
        this.author = author;

    }
    public News(Date date, String text){
        this.date = date;
        this.text = text;
    }




    public Date getDate() {return date;}

    public String getText() {return text;}

    public void setDate(Date date) {this.date = date;}

    public void setText(String text) {this.text = text;}

    public String getUid() {return uid;}

    public void setUid(String uid) {this.uid = uid;}

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public ArrayList<String> getTarget() {
        return target;
    }

    public void setTarget(ArrayList<String> target) {
        this.target = target;
    }

}
