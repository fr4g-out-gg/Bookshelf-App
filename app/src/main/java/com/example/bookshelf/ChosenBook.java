package com.example.bookshelf;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookshelf.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Inicializácia UI
        editTitle = findViewById(R.id.edit_book_title);
        editAuthor = findViewById(R.id.edit_book_author);
        editGenre = findViewById(R.id.edit_book_genre);
        editDescription = findViewById(R.id.edit_book_description);
        imgCover = findViewById(R.id.img_book_cover);
        checkRead = findViewById(R.id.check_is_read);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Získanie dát z Intentu
        bookId = getIntent().getStringExtra("BOOK_ID");
        shelfName = getIntent().getStringExtra("SHELF_NAME");

        loadBookDetails();

        findViewById(R.id.btn_save_book).setOnClickListener(v -> updateBook());
        findViewById(R.id.toolbar).setOnClickListener(v -> finish()); // Späť
    }

    private void loadBookDetails() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .collection("custom_shelves").document(shelfName)
                .collection("books").document(bookId)
                .get().addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        editTitle.setText(doc.getString("title"));
                        editAuthor.setText(doc.getString("author"));
                        editGenre.setText(doc.getString("genre"));
                        editDescription.setText(doc.getString("description"));
                        Boolean isRead = doc.getBoolean("read");
                        checkRead.setChecked(isRead != null && isRead);

                        String url = doc.getString("imageUrl");
                        Glide.with(this).load(url).placeholder(R.drawable.no_cover_available).into(imgCover);
                    }
                });
    }

    private void updateBook() {
        // Tu pridáš kód na uloženie zmien späť do Firestore (db.update(...))
        Toast.makeText(this, "Zmeny uložené", Toast.LENGTH_SHORT).show();
    }
}