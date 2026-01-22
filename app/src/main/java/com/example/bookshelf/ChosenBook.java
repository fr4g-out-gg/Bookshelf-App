package com.example.bookshelf;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChosenBook extends AppCompatActivity {

    private EditText editTitle, editAuthor, editGenre, editDescription;
    private ImageView imgCover;
    private CheckBox checkRead;
    private String bookId, shelfName;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosen_book);

        // Inicializácia Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Získanie dát z Intentu (z BookAdaptera)
        bookId = getIntent().getStringExtra("BOOK_ID");
        shelfName = getIntent().getStringExtra("SHELF_NAME");

        // Inicializácia UI prvkov
        editTitle = findViewById(R.id.edit_book_title);
        editAuthor = findViewById(R.id.edit_book_author);
        editGenre = findViewById(R.id.edit_book_genre);
        editDescription = findViewById(R.id.edit_book_description);
        imgCover = findViewById(R.id.img_book_cover);
        checkRead = findViewById(R.id.check_is_read);

        // Tlačidlá
        Button btnSave = findViewById(R.id.btn_save_book);
        ImageButton btnDeleteBook = findViewById(R.id.btnDeleteBook);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Nastavenie poslucháčov (Listeners)
        btnSave.setOnClickListener(v -> updateBook());
        btnDeleteBook.setOnClickListener(v -> showDeleteBookDialog());

        // Kliknutie na toolbar (šípka späť)
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Načítanie existujúcich dát
        loadBookDetails();
    }

    private void loadBookDetails() {
        android.util.Log.d("FIREBASE_CHECK", "Hľadám v poličke: " + shelfName);
        android.util.Log.d("FIREBASE_CHECK", "Hľadám dokument s ID: " + bookId);
        if (mAuth.getCurrentUser() == null || shelfName == null || bookId == null) {
            Toast.makeText(this, "Chýbajúce údaje o knihe", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .collection("custom_shelves").document(shelfName)
                .collection("books").document(bookId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // Vyplnenie polí dátami z databázy
                        editTitle.setText(doc.getString("title"));
                        editAuthor.setText(doc.getString("author"));
                        editGenre.setText(doc.getString("genre"));
                        editDescription.setText(doc.getString("description"));

                        Boolean isRead = doc.getBoolean("read");
                        checkRead.setChecked(isRead != null && isRead);

                        String url = doc.getString("imageUrl");

                        // Bezpečné načítanie obrázka
                        if (!isFinishing() && !isDestroyed()) {
                            Glide.with(this)
                                    .load(url)
                                    .placeholder(R.drawable.no_cover_available)
                                    .error(R.drawable.no_cover_available)
                                    .centerCrop()
                                    .into(imgCover);
                        }
                    } else {
                        android.util.Log.e("FIREBASE_CHECK", "Dokument neexistuje! Skontroluj cestu.");
                        Toast.makeText(this, "Kniha sa nenašla", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Chyba: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateBook() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        // Príprava dát na aktualizáciu
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", editTitle.getText().toString().trim());
        updates.put("author", editAuthor.getText().toString().trim());
        updates.put("genre", editGenre.getText().toString().trim());
        updates.put("description", editDescription.getText().toString().trim());
        updates.put("read", checkRead.isChecked());

        db.collection("users").document(uid)
                .collection("custom_shelves").document(shelfName)
                .collection("books").document(bookId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Kniha bola úspešne upravená", Toast.LENGTH_SHORT).show();
                    finish(); // Návrat do poličky
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Chyba pri ukladaní: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showDeleteBookDialog() {
        String currentTitle = editTitle.getText().toString();

        new AlertDialog.Builder(this)
                .setTitle("Zmazať knihu")
                .setMessage("Naozaj chcete zmazať knihu '" + currentTitle + "'?")
                .setPositiveButton("Zmazať", (dialog, which) -> deleteBook())
                .setNegativeButton("Zrušiť", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteBook() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .collection("custom_shelves").document(shelfName)
                .collection("books").document(bookId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Kniha bola odstránená", Toast.LENGTH_SHORT).show();
                    finish(); // Návrat do poličky
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Chyba pri mazaní: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}