package graduation.trocan.academicthoughts.model;

import java.util.Date;

/**
 * Created by Gabi on 29/04/2018.
 */

public class News {

    private Date date;
    private String text;
    private String uid;
    private String author;

    public News(){ }

    public News(Date date, String text, String uid){
        this.uid = uid;
        this.date = date;
        this.text = text;
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
}
