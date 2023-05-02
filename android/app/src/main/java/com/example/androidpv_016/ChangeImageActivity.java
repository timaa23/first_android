package com.example.androidpv_016;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.oginotihiro.cropview.CropUtil;
import com.oginotihiro.cropview.CropView;

import java.io.File;

public class ChangeImageActivity extends AppCompatActivity {
    private static int PICK_IMAGE_RESULT = 20;
    private CropView cropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_image);
        cropView = findViewById(R.id.cropView);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_RESULT) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectImage = data.getData();
                cropView.of(selectImage).asSquare().initialize(this);

            } else finish();
        } else finish();
    }

    public void onClickRightRotate(View view) {
        cropView.setRotation(cropView.getRotation() + 90);
    }

    public void onClickLeftRotate(View view) {
        cropView.setRotation(cropView.getRotation() - 90);
    }

    public void onClickCrop(View view) {
        String fileTemp = java.util.UUID.randomUUID().toString();
        Bitmap croppedBitmap = cropView.getOutput();
        Matrix matrix = new Matrix();
        matrix.postRotate(cropView.getRotation());
        Bitmap rotatedBitmap = Bitmap.createBitmap(croppedBitmap, 0, 0, croppedBitmap.getWidth(), croppedBitmap.getHeight(), matrix, true);

        Uri fileSavePath = Uri.fromFile(new File(getCacheDir(), fileTemp));
        CropUtil.saveOutput(this, fileSavePath, rotatedBitmap, 90);

        //Вертаємо результат - Шлях до файлу, який збережено через кропер
        Intent intent = new Intent();
        intent.putExtra("croppedUri", fileSavePath);
        setResult(1000, intent);
        finish();
    }
}