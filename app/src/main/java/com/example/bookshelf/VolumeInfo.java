package com.example.bookshelf;
import java.util.List;

public class VolumeInfo {
    private String title;
    private List<String> authors;
    private String description;
    private ImageLinks imageLinks;

    public String getTitle() { return title; }
    public List<String> getAuthors() { return authors; }
    public String getDescription() { return description; }
    public ImageLinks getImageLinks() { return imageLinks; }

    public static class ImageLinks {
        private String thumbnail;
        public String getThumbnail() {
            return thumbnail != null ? thumbnail.replace("http://", "https://") : null;
        }
    }
}