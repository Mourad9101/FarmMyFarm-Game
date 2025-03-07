package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inventory {
    private HashMap<String, InventoryItem> storage;

    public Inventory() {
        this.storage = new HashMap<>();
    }

    public void addItem(String name, int quantity, String type, String imagePath) {
        InventoryItem item = storage.get(name);
        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            item = new InventoryItem(name, quantity, type, imagePath);
            storage.put(name, item);
        }
    }

    public int getQuantity(String name) {
        InventoryItem item = storage.get(name);
        return item != null ? item.getQuantity() : 0;
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

    public void clear() {
        storage.clear();
    }
}
