package com.example.myapplication;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Fish_Item {
    private  int image;
    private String name;
    private String price;
    private String classLabel;

    public Fish_Item() {
    }

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

    @Override
    public String toString() {
        return "Fish_Item{" +
                "image=" + image +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", classLabel='" + classLabel + '\'' +
                '}';
    }

    public Fish_Item(int image, String name, String price, String classLabel) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.classLabel = classLabel;
    }

    public boolean isLabel(String label) {
        return this.classLabel.equals(label);
    }

    public static String[] getClassLabelList(ArrayList<Fish_Item> fishItems) {
        ArrayList<String> labels = new ArrayList<>();
        for (Fish_Item fish : fishItems) {
            String label = fish.getClassLabel();
            if (!labels.contains(label)) {
                labels.add(label);
            }
        }
        String[] result = new String[labels.size()];
        result = labels.toArray(result);
        return result;
    }

    public boolean isFavourite() {
        if (LoginActivity.currentUser == null)
            return false;
        return LoginActivity.currentUser.getFavouriteFishes().contains(this.classLabel);
    }

}
