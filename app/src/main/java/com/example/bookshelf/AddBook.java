package com.example.bookshelf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddBook extends AppCompatActivity {

    private EditText editTitle, editAuthor, editGenre, editDescription;
    private CheckBox checkRead;
    private ImageView imgCover;
    private Uri imageUri;
    private String shelfName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Získanie mena poličky, do ktorej pridávame
        shelfName = getIntent().getStringExtra("CHOSEN_SHELF_NAME");

        // Inicializácia UI
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add book");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        imgCover = findViewById(R.id.img_book_cover);
        editTitle = findViewById(R.id.edit_book_title);
        editAuthor = findViewById(R.id.edit_book_author);
        editGenre = findViewById(R.id.edit_book_genre);
        editDescription = findViewById(R.id.edit_book_description);
        checkRead = findViewById(R.id.check_is_read);

        Button btnSelectImg = findViewById(R.id.btn_select_image);
        Button btnSave = findViewById(R.id.btn_save_book);

        // Launcher pre výber obrázka
        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imgCover.setImageURI(imageUri);
                    }
                }
        );

        btnSelectImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> uploadImageAndSaveBook());
    }

    private void uploadImageAndSaveBook() {
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) {
            editTitle.setError("Title is required");
            return;
        }

        if (imageUri != null) {
            uploadToStorage(title);
        } else {
            saveBookToFirestore(title, ""); // Bez obrázka
        }
    }

    private void uploadToStorage(String title) {
        String fileName = UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("book_covers/" + fileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveBookToFirestore(title, uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Log.e("StorageError", "Error: ", e);
                    Toast.makeText(this, "Error uploading image.", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveBookToFirestore(String title, String downloadUrl) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null || shelfName == null) return;

        // OPRAVA: Voláme nový konštruktor modelu Book so všetkými parametrami.
        // ID posielame ako null, pretože ho vygeneruje Firestore.
        Book book = new Book(
                title,
                editAuthor.getText().toString().trim(),
                editDescription.getText().toString().trim(),
                editGenre.getText().toString().trim(),
                downloadUrl,
                checkRead.isChecked(),
                com.google.firebase.Timestamp.now(),
                null // Toto je parameter 'id'
        );

        FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .collection("custom_shelves").document(shelfName)
                .collection("books")
                .add(book) // Používame .add() pre náhodné ID
                .addOnSuccessListener(doc -> {
                    int readIncrement = checkRead.isChecked() ? 1 : 0;
                    updateUserCounts(1, readIncrement);
                    Toast.makeText(this, "Kniha uložená!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error while writing: ", e);
                    Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updateUserCounts(int libraryDelta, int readDelta) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        Map<String, Object> updates = new HashMap<>();
        if (libraryDelta != 0) {
            updates.put("libraryCount", com.google.firebase.firestore.FieldValue.increment(libraryDelta));
        }
        if (readDelta != 0) {
            updates.put("readCount", com.google.firebase.firestore.FieldValue.increment(readDelta));
        }

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .update(updates)
                .addOnFailureListener(e -> Log.e("CountUpdate", "Error while updating count.", e));
    }
}