package com.example.myapplication;

public class Fish_Item {
    private  int image;
    private String name;
    private String price;
    private String classLabel;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getClassLabel() { return this.classLabel; }

    public Fish_Item(int image, String name, String price, String classLabel) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.classLabel = classLabel;
    }

}
