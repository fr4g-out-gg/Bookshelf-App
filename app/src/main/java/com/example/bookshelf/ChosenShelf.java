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

        shelfName = getIntent().getStringExtra("CHOSEN_SHELF_NAME");
        ImageButton btnDeleteShelf = findViewById(R.id.btnDeleteShelf);
        btnDeleteShelf.setOnClickListener(v -> showDeleteShelfDialog());

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

    private void showDeleteShelfDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Zmazať poličku")
                .setMessage("Naozaj chcete zmazať celú poličku '" + shelfName + "'? Táto akcia je nevratná.")
                .setPositiveButton("Zmazať", (dialog, which) -> deleteShelf())
                .setNegativeButton("Zrušiť", null)
                .show();
    }

    private void deleteShelf() {
        String uid = mAuth.getCurrentUser().getUid();

        // Cesta k dokumentu poličky
        db.collection("users").document(uid)
                .collection("custom_shelves").document(shelfName)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ChosenShelf.this, "Polička zmazaná", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChosenShelf.this, "Chyba pri mazaní: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Refresh dát pri každom návrate do aktivity
    @Override
    protected void onStart() {
        super.onStart();
        loadBooksFromFirestore();
    }

    private void loadBooksFromFirestore() {
        if (mAuth.getCurrentUser() == null) {
            Log.e("Firebase", "Používateľ nie je prihlásený");
            return;
        }

        // Overenie, či shelfName nie je null, inak cesta v DB zlyhá
        if (shelfName == null || shelfName.isEmpty()) {
            Log.e("Firebase", "Názov police (shelfName) je prázdny!");
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        // 1. Odkaz na správnu kolekciu
        db.collection("users").document(uid)
                .collection("custom_shelves").document(shelfName)
                .collection("books")
                // 2. Zoradenie (POZOR: Ak toto spôsobí chybu, pozri bod nižšie o indexoch)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        bookList.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            try {
                                // Firebase premení dokument na objekt
                                Book book = doc.toObject(Book.class);
                                if (book != null) {
                                    book.setId(doc.getId());
                                    bookList.add(book);
                                }
                            } catch (Exception e) {
                                Log.e("Firebase", "Chyba pri mapovaní dokumentu ID: " + doc.getId(), e);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firebase", "Nenašli sa žiadne knihy.");
                        bookList.clear();
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Chyba pri načítaní kníh z cesty: " + shelfName, e);
                    Toast.makeText(this, "Chyba: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public String getShelfName() {
        return shelfName;
    }
}