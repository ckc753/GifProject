package com.example.chlru.gifproject;

import com.google.firebase.storage.StorageReference;

public class Item {
    StorageReference image;
    String title;

    StorageReference getImage() {
        return this.image;
    }
    String getTitle() {
        return this.title;
    }

    Item(StorageReference image, String title) {
        this.image = image;
        this.title = title;
    }
}