package com.example.androidpv_016.category;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidpv_016.R;

public class CategoryCardViewHolder extends RecyclerView.ViewHolder {
    private int categoryId;
    private final ImageView categoryImage;
    private final TextView categoryName;
    private final TextView categoryDescription;

    private final Button btnEditCategory;
    private final Button btnDeleteCategory;

    public CategoryCardViewHolder(@NonNull View itemView) {
        super(itemView);

        categoryName = itemView.findViewById(R.id.categoryName);
        categoryImage = itemView.findViewById(R.id.categoryImage);
        categoryDescription = itemView.findViewById(R.id.categoryDescription);

        btnEditCategory = itemView.findViewById(R.id.btnEditCategory);
        btnDeleteCategory = itemView.findViewById(R.id.btnDeleteCategory);
    }

    public Button getBtnEditCategory() {
        return btnEditCategory;
    }

    public Button getBtnDeleteCategory() {
        return btnDeleteCategory;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public ImageView getCategoryImage() {
        return categoryImage;
    }

    public TextView getCategoryName() {
        return categoryName;
    }

    public TextView getCategoryDescription() {
        return categoryDescription;
    }
}
