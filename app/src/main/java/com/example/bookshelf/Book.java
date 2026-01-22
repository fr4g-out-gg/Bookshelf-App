package com.example.bookshelf;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Book {
    // Pridaná chýbajúca deklarácia premennej
    private String id;
    private String title;
    private String author;
    private String description;
    private String genre;
    private String imageUrl;
    private boolean read;
    private Timestamp timestamp;

    // 1. Povinný prázdny konštruktor pre Firebase
    public Book() {
    }

    // 2. Hlavný konštruktor
    public Book(String title, String author, String description, String genre, String imageUrl, boolean read, Timestamp timestamp, String id) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.read = read;
        this.timestamp = timestamp;
    }

    // 3. Gettery
    // @Exclude zabezpečí, že ID sa nebude duplicitne ukladať do vnútra dokumentu
    @Exclude
    public String getId() { return id; }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public String getImageUrl() { return imageUrl; }
    public boolean isRead() { return read; }
    public Timestamp getTimestamp() { return timestamp; }

    // 4. Settery
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setDescription(String description) { this.description = description; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setRead(boolean read) { this.read = read; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}