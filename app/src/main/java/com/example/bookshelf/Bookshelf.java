package com.example.bookshelf;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bookshelf extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookshelfAdapter adapter;
    private List<String> shelfList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshelf);

        // Inicializácia Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Nastavenie Toolbaru
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Shelves");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Nastavenie RecyclerView
        recyclerView = findViewById(R.id.recyclerViewBookshelf);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        shelfList = new ArrayList<>();
        adapter = new BookshelfAdapter(shelfList);
        recyclerView.setAdapter(adapter);

        // Načítanie dát z Firebase
        loadShelvesFromFirestore();

        // Tlačidlo na pridanie novej poličky
        ImageButton btnAddShelf = findViewById(R.id.btn_add_shelf);
        btnAddShelf.setOnClickListener(v -> showAddShelfDialog());
    }

    private void showAddShelfDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New category");

        final EditText input = new EditText(this);
        input.setHint("Shelf name...");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String shelfName = input.getText().toString().trim();
            if (!shelfName.isEmpty()) {
                saveShelfToFirestore(shelfName);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveShelfToFirestore(String name) {
        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> shelfData = new HashMap<>();
        shelfData.put("name", name);
        shelfData.put("timestamp", com.google.firebase.Timestamp.now());

        // ZMENA: Namiesto .add() použijeme .document(name).set()
        db.collection("users").document(uid)
                .collection("custom_shelves")
                .document(name) // Týmto nastavíme ID dokumentu na názov poličky
                .set(shelfData)
                .addOnSuccessListener(aVoid -> {
                    // Po úspešnom uložení pridáme do zoznamu a refreshneme UI
                    shelfList.add(name);
                    adapter.notifyItemInserted(shelfList.size() - 1);
                    Toast.makeText(this, "Shelf " + name + " added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Save error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadShelvesFromFirestore() {
        String uid = mAuth.getCurrentUser().getUid();

        // Načítame poličky zoradené podľa času vytvorenia
        db.collection("users").document(uid).collection("custom_shelves")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    shelfList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        if (name != null) {
                            shelfList.add(name);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadShelvesFromFirestore();
    }
}