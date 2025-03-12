package models;

import javafx.scene.image.Image;

public class ShopItem {
    private String name;
    private int price;
    private Image icon;

    public ShopItem(String name, int price, String imagePath) {
        this.name = name;
        this.price = price;
        try {
            this.icon = new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e) {
            this.icon = new Image(getClass().getResourceAsStream("/assets/images/icons/default.png"));
        }
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public Image getIcon() {
        return icon;
    }
}