package com.example.bookshelf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<String> bookTitles;

    public BookAdapter(List<String> bookTitles) {
        this.bookTitles = bookTitles;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Použijeme tvoj item_book.xml, ale v ChosenShelf ho Grid rozloží do stĺpcov
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.btnBook.setText(bookTitles.get(position));
        // Tu môžeš neskôr pridať otváranie detailu knihy alebo čítačky
    }

    @Override
    public int getItemCount() {
        return bookTitles.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        Button btnBook;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            btnBook = itemView.findViewById(R.id.btnBookItem);
        }
    }
}