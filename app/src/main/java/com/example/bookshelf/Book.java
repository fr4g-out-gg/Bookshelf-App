package com.example.bookshelf;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Book {
    private String title;
    private String author;
    private String description;
    private String genre;
    private String imageUrl;
    private boolean read;
    private Timestamp timestamp;

    // 1. TENTO KONŠTRUKTOR JE KRITICKÝ - Firebase ho potrebuje!
    public Book() {
        // Prázdne telo je v poriadku
    }

    // 2. Hlavný konštruktor pre tvoje použitie v AddBook
    public Book(String title, String author, String description, String genre, String imageUrl, boolean read, Timestamp timestamp) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.read = read;
        this.timestamp = timestamp;
    }

    // 3. Gettery (potrebné pre nahrávanie a zobrazovanie)
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public String getImageUrl() { return imageUrl; }
    public boolean isRead() { return read; }
    public Timestamp getTimestamp() { return timestamp; }

    // 4. Settery (Firebase ich používa pri deserializácii / metóde toObject)
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setDescription(String description) { this.description = description; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setRead(boolean read) { this.read = read; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
