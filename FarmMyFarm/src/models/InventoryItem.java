package models;

import javafx.scene.image.Image;

public class InventoryItem {
    private String name;
    private int quantity;
    private String type;
    private Image icon;
    private double sellPrice;

    public InventoryItem(String name, int quantity, String type, String imagePath) {
        this(name, quantity, type, imagePath, 0);
    }

    public InventoryItem(String name, int quantity, String type, String imagePath, double sellPrice) {
        this.name = name;
        this.quantity = quantity;
        this.type = type;
        this.sellPrice = sellPrice;

        try {
            this.icon = new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e) {
            System.err.println("Erreur : Impossible de charger l'image " + imagePath);
            this.icon = new Image(getClass().getResourceAsStream("/assets/images/icons/default.png"));
        }
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getType() {
        return type;
    }

    public Image getIcon() {
        return icon;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isHarvestedCrop() {
        return type.equals("récolte");
    }

    public String getBaseCropName() {
        if (isHarvestedCrop() && name.endsWith("_récolte")) {
            return name.substring(0, name.length() - 8);
        }
        return name;
    }
}
