package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inventory implements Savable {
    private HashMap<String, InventoryItem> storage;

    public Inventory() {
        this.storage = new HashMap<>();
    }

    public void addItem(String name, int quantity, String type, String imagePath) {
        InventoryItem item = storage.get(name);
        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            item = new InventoryItem(name, quantity, type,null);
            storage.put(name, item);
        }
    }

    public void addItem(InventoryItem item) {
        InventoryItem existingItem = storage.get(item.getName());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            storage.put(item.getName(), item);
        }
    }


    public void addItem(Animal animal, int quantity) {
        InventoryItem item = storage.get(animal.getName());
        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            item = new InventoryItem(animal.getName(), quantity, "animal", animal);
            storage.put(animal.getName(), item);
        }
    }

    public boolean removeItem(String name, int quantity) {
        InventoryItem item = storage.get(name);
        if (item != null && item.getQuantity() >= quantity) {
            item.setQuantity(item.getQuantity() - quantity);
            if (item.getQuantity() == 0) {
                storage.remove(name);
            }
            return true;
        }
        return false;
    }

    public HashMap<String, InventoryItem> getStorage() {
        return storage;
    }

    public List<InventoryItem> getItems() {
        return new ArrayList<>(storage.values());
    }



}
