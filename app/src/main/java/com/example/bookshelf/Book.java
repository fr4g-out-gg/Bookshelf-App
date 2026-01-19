package com.example.bookshelf;

public class Book {
    private String title;
    private String author;
    private String description;
    private String genre;
    private String imageUrl;
    private boolean read;

    // Prázdny konštruktor pre Firebase
    public Book() {}

    public Book(String title, String author, String description, String genre, String imageUrl, boolean read) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.read = read;
    }

    // Gettery (Firebase ich potrebuje na čítanie dát)
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public String getImageUrl() { return imageUrl; }
    public boolean isRead() { return read; }
}