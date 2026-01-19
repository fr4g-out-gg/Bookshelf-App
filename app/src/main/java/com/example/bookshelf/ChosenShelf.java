package com.example.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChosenShelf extends AppCompatActivity {

    private String shelfName;
    private RecyclerView recyclerView;
    private List<Book> bookList; // ZMENA: List objektov Book
    private BookAdapter adapter; // ZMENA: Použitie nového BookAdaptera
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_shelf);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Získanie názvu poličky
        shelfName = getIntent().getStringExtra("CHOSEN_SHELF_NAME");

        // 1. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(shelfName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 2. RecyclerView (3 stĺpce pre mriežku kníh)
        recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        bookList = new ArrayList<>();
        adapter = new BookAdapter(bookList); // Používame nový adaptér
        recyclerView.setAdapter(adapter);

        // 3. Tlačidlo na pridanie knihy (Otvára tvoju novú AddBook aktivitu)
        ImageButton fab = findViewById(R.id.fab_add_book);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(ChosenShelf.this, AddBook.class); // Skontroluj názov triedy (AddBookActivity)
            intent.putExtra("CHOSEN_SHELF_NAME", shelfName);
            startActivity(intent);
        });
    }

    // Refresh dát pri každom návrate do aktivity
    @Override
    protected void onStart() {
        super.onStart();
        loadBooksFromFirestore();
    }

    private void loadBooksFromFirestore() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        // Cesta v databáze: users -> UID -> custom_shelves -> shelfName -> books
//        db.collection("users").document(uid)
//                .collection("custom_shelves").document(shelfName)
//                .collection("books")
//                .orderBy("title", Query.Direction.ASCENDING) // Zoradenie podľa názvu
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    bookList.clear();
//                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                        // Firebase automaticky premení dokument na objekt triedy Book
//                        Book book = doc.toObject(Book.class);
//                        if (book != null) {
//                            bookList.add(book);
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("Firebase", "Chyba pri načítaní kníh", e);
//                    Toast.makeText(this, "Nepodarilo sa načítať knihy", Toast.LENGTH_SHORT).show();
//                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}