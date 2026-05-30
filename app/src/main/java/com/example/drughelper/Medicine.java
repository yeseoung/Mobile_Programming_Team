package com.example.drughelper;

public class Medicine {
    private String imageUrl;
    private String itemName;
    private String durNotice;
    public Medicine(String itemName, String imageUrl, String durNotice) {
        this.itemName = itemName;
        this.imageUrl = imageUrl;
        this.durNotice = durNotice;
    }
    public String getItemName() {return itemName;}
    public String getImageUrl() { return this.imageUrl; }
    public String getDurNotice() { return this.durNotice; }
}
