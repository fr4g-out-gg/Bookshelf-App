package com.example.bookshelf;

// FIX 1: Use the Firestore version of this annotation
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.Timestamp;

@IgnoreExtraProperties
public class Book {
    private String title;
    private String author;
    private String description;
    private String genre;
    private String imageUrl;
    private boolean read;

    // FIX 2: Add the missing timestamp field
    private Timestamp timestamp;

    // Empty constructor for Firebase
    public Book(String title, String trim, String trimmed, String s, String downloadUrl, boolean checked) {}

    public Book(String title, String author, String description, String genre, String imageUrl, boolean read, Timestamp timestamp) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.read = read;
        this.timestamp = timestamp;
    }

    // Getters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public String getImageUrl() { return imageUrl; }
    public boolean isRead() { return read; }

    // FIX 3: Add Getter and Setter for the timestamp
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    // Setters (Adding these ensures Firestore can map data even if reflection fails)
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setDescription(String description) { this.description = description; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setRead(boolean read) { this.read = read; }
}