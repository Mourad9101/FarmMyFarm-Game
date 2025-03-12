package utils;

import models.Savable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {

    private static final String FILE_PATH = "farm_save.dat";
    public static void saveFarm(List<Savable> objects) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(objects);
            System.out.println("Sauvegarde réussie !");
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde !");
            e.printStackTrace();
        }
    }

    public static List<Savable> loadFarm() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("Aucune sauvegarde trouvée.");
            return new ArrayList<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Savable>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur lors du chargement de la sauvegarde.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
