package com.example.bookshelf;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Bookshelf extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshelf);

        // 1. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Moja Knižnica");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 2. RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewBookshelf);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Testovacie dáta (v reálnej app by išli z Firestore)
        List<String> myBooks = new ArrayList<>();
        myBooks.add("Zaklínac");
        myBooks.add("Harry Potter");
        myBooks.add("Pán Prsteňov");
        myBooks.add("Malý Princ");
        myBooks.add("Duna");
        myBooks.add("Duna");
        myBooks.add("Duna");
        myBooks.add("Duna");
        myBooks.add("Duna");
        myBooks.add("Duna");
        myBooks.add("Duna");
        myBooks.add("Duna");
        myBooks.add("Duna");

        // 4. Nastavenie Adaptéra
        BookshelfAdapter adapter = new BookshelfAdapter(myBooks);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}