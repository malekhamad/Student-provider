package com.astudent.partner.Models;


public class ServiceListModel {
    String name, pallete, carton, weight;
    String image;
    int img;
    String id;
    String description;
    String price;
    private boolean isSelected;
    String available,pricePerHour;
    String subservices;

    public ServiceListModel(String name, int img) {
        this.name = name;
        this.img = img;
    }

    public ServiceListModel() {
    }

    public ServiceListModel(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(String pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setsubservices(String subservices) {
        this.subservices = subservices;
    }

    public String getsubservices() {
        return subservices;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getPallete() {
        return pallete;
    }

    public void setPallete(String pallete) {
        this.pallete = pallete;
    }

    public String getCarton() {
        return carton;
    }

    public void setCarton(String carton) {
        this.carton = carton;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ServiceListModel{" +
                "name='" + name + '\'' +
                ", pallete='" + pallete + '\'' +
                ", carton='" + carton + '\'' +
                ", weight='" + weight + '\'' +
                ", image='" + image + '\'' +
                ", img=" + img +
                ", id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", isSelected=" + isSelected +
                ", available='" + available + '\'' +
                ", pricePerHour='" + pricePerHour + '\'' +
                ", subservices='" + subservices + '\'' +
                '}';
    }
}
