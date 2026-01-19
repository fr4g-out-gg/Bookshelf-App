package com.example.bookshelf;

import android.content.Intent;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shelf, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String shelfName = bookList.get(position);
        holder.bookButton.setText(shelfName);
        holder.bookButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChosenShelf.class);

            // Odovzdanie názvu poličky
            intent.putExtra("CHOSEN_SHELF_NAME", shelfName);

            v.getContext().startActivity(intent);
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
            bookButton = itemView.findViewById(R.id.btnShelfItem);
        }
    }
}