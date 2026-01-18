package com.example.bookshelf;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChosenShelf extends AppCompatActivity {

    private String shelfName;
    private RecyclerView recyclerView;
    private List<String> bookList;
    private BookshelfAdapter adapter; // Môžeš použiť rovnaký adaptér alebo vytvoriť nový BookAdapter
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_shelf);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        shelfName = getIntent().getStringExtra("CHOSEN_SHELF_NAME");

        // 1. Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(shelfName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 2. RecyclerView (Nastavíme GridLayout - 3 knihy vedľa seba)
        recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        bookList = new ArrayList<>();
        adapter = new BookshelfAdapter(bookList); // Použijeme tvoj existujúci adaptér
        recyclerView.setAdapter(adapter);

        // 3. Načítanie kníh
        loadBooksFromFirestore();

        // 4. Pridávanie kníh (FAB)
        ImageButton fab = findViewById(R.id.fab_add_book);
        fab.setOnClickListener(v -> showAddBookDialog());
    }

    private void showAddBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pridať knihu do " + shelfName);

        final EditText input = new EditText(this);
        input.setHint("Názov knihy...");
        builder.setView(input);

        builder.setPositiveButton("Pridať", (dialog, which) -> {
            String bookTitle = input.getText().toString().trim();
            if (!bookTitle.isEmpty()) {
                saveBookToFirestore(bookTitle);
            }
        });
        builder.setNegativeButton("Zrušiť", null);
        builder.show();
    }

    private void saveBookToFirestore(String title) {
        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> bookData = new HashMap<>();
        bookData.put("title", title);
        bookData.put("timestamp", com.google.firebase.Timestamp.now());

        // Ukladáme pod konkrétnu poličku (dokument shelfName)
        db.collection("users").document(uid)
                .collection("custom_shelves").document(shelfName)
                .collection("books")
                .add(bookData)
                .addOnSuccessListener(documentReference -> {
                    bookList.add(title);
                    adapter.notifyItemInserted(bookList.size() - 1);
                    Toast.makeText(this, "Kniha pridaná!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Chyba pri zápise", e));
    }

    private void loadBooksFromFirestore() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .collection("custom_shelves").document(shelfName)
                .collection("books")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String title = doc.getString("title");
                        if (title != null) bookList.add(title);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Chyba pri načítaní", e));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}