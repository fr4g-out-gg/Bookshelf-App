package com.example.bookshelf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Nezabudni pridať Glide do build.gradle
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    // Zmena: List teraz obsahuje objekty triedy Book, nie Stringy
    private List<Book> bookList;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Použijeme tvoj XML layout pre položku knihy
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shelf, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = bookList.get(position);

        // Nastavenie textu (ak máš v item_book.xml TextView pre názov)
        if (holder.txtTitle != null) {
            holder.txtTitle.setText(currentBook.getTitle());
        }

        // Načítanie obrázka pomocou Glide
        if (currentBook.getImageUrl() != null && !currentBook.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentBook.getImageUrl())
                    .placeholder(R.drawable.default_cover) // Vyrob si jednoduchý sivý obrázok v drawables
                    .centerCrop()
                    .into(holder.imgCover);
        } else {
            // Ak kniha nemá obrázok, nastavíme predvolený
            holder.imgCover.setImageResource(R.drawable.default_cover);
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            // Tu musia ID súhlasiť s tvojím item_book.xml
            imgCover = itemView.findViewById(R.id.imgBookItemCover);
            txtTitle = itemView.findViewById(R.id.btnBookItem); // Ak používaš Button ako pozadie s textom
        }
    }
}