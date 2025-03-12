package models;

public class InventoryItem implements Savable {
    private String name;
    private int quantity;
    private String type;
    private Animal animal;

    public InventoryItem(String name, int quantity, String type, Animal animal) {
        this.name = name;
        this.quantity = quantity;
        this.type = type;
        this.animal = animal;
    }

    public String getName() {
        if(type.equals("animal")) {
            if (animal != null) {
                return animal.getName();
            } else {
                return "Animal inconnu";
            }
        }
        return name;
    }


    public int getQuantity() {
        return quantity;
    }

    public String getType() {
        return type;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Animal getAnimal() {
        return animal;
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
