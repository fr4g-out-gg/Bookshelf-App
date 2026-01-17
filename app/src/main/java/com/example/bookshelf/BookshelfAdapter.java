package com.example.bookshelf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.ViewHolder> {

    private List<String> bookList;

    public BookshelfAdapter(List<String> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String bookTitle = bookList.get(position);
        holder.bookButton.setText(bookTitle);
        holder.bookButton.setOnClickListener(v -> {
            // Tu pridaj akciu po kliknutí na knihu
        });

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button bookButton;
        public ViewHolder(View itemView) {
            super(itemView);
            bookButton = itemView.findViewById(R.id.btnBookItem);
        }
    }
}