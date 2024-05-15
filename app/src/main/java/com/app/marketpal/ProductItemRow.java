package com.app.marketpal;

public class ProductItemRow {
    private String productName;
    private String imageUrl;

    public ProductItemRow(String productName, String imageUrl) {
        this.productName = productName;
        this.imageUrl = imageUrl;
    }

    public String getProductNameRow() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

