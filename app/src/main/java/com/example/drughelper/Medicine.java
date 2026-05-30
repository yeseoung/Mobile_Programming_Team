package com.example.drughelper;

public class Medicine {
    private String imageUrl;
    private String itemName;

    public Medicine(String itemName, String imageUrl) {
        this.itemName = itemName;
        this.imageUrl = imageUrl;
    }
    public String getItemName() {return itemName;}
    public String getImageUrl() { return this.imageUrl; }
}
