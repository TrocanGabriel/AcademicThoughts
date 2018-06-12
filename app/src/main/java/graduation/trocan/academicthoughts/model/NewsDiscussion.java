package graduation.trocan.academicthoughts.model;

import java.util.Date;

public class NewsDiscussion {

    private String author;
    private String message;
    private Date postingDate;

    public NewsDiscussion(String author, String message, Date postingDate) {
        this.author = author;
        this.message = message;
        this.postingDate = postingDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }
}
