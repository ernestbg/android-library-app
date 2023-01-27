package com.example.proyectoappredsocial.providers;

import android.content.Context;
import android.net.Uri;

import com.example.proyectoappredsocial.utils.CompressorBitmapImage;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class ImageProvider {


    StorageReference storageReference;

    public ImageProvider() {

        storageReference = FirebaseStorage.getInstance().getReference();


    }

    public UploadTask save(Context context, File file) {

        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);

        StorageReference storage = FirebaseStorage.getInstance().getReference().child(new Date() + ".jpg");
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    public StorageReference getStorage() {


        return storageReference;
    }


    /*public Task<Void> delete(String urlImage) {
        return storageReference.getStorage().getReferenceFromUrl();
    }*/


}
