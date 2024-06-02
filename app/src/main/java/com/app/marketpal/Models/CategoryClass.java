package com.app.marketpal.Models;

import androidx.recyclerview.widget.RecyclerView;

public class CategoryClass {

    String category_title;
    String category_desc;
    String category_brand;
    RecyclerView.Adapter category_holder;

    public static final int DEFAULT_TYPE  =  0;
    public static final int HEADER_TYPE   =  1;
    public static final int NO_TITLE_TYPE =  2;

    public int type;

    public CategoryClass(){this.type = 0;};
    public CategoryClass(int type){
        this.type = type;
    };

    public String getCategory_title() {
        return category_title;
    }
    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    public String getCategory_desc() {
        return category_desc;
    }
    public void setCategory_desc(String category_desc) {
        this.category_desc = category_desc;
    }

    public String getCategory_brand() {
        return category_brand;
    }
    public void setCategory_brand(String category_brand) {
        this.category_brand = category_brand;
    }

    public RecyclerView.Adapter getCategory_holder() {
        return category_holder;
    }
    public void setCategory_holder(RecyclerView.Adapter category_holder) {
        this.category_holder = category_holder;
    }



    CategoryClass(String API,String category_title,
                  String category_desc,
                  String category_brand,
                  RecyclerView.Adapter category_holder){
        this.category_title = category_title;
        this.category_desc = category_desc;
        this.category_brand = category_brand;
        this.category_holder = category_holder;
    };


}
