package com.example.drughelper;

public class Medicine {
    private String itemImage;
    private String itemName;

    public Medicine(String itemName, String itemImage) {
        this.itemName = itemName;
        this.itemImage = itemImage;
    }
    public String getItemName() {return itemName;}
    public String getItemImage() {return itemImage;}
}
