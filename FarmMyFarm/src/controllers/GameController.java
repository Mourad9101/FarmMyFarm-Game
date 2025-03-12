package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.util.Duration;
import models.*;
import utils.SaveManager;
import models.Savable;
import java.util.List;



import java.util.HashMap;

public class GameController {

    @FXML
    private GridPane farmGrid;

    @FXML
    private Label moneyLabel;

    @FXML
    private ChoiceBox<String> seedChoiceBox;

    @FXML
    private TableView<InventoryItem> inventoryTable;

    @FXML
    private TableColumn<InventoryItem, String> columnItem;

    @FXML
    private TableColumn<InventoryItem, Integer> columnQuantity;

    @FXML
    private AnchorPane shopPane;

    @FXML
    private TableView<ShopItem> shopTable;

    @FXML
    private TableColumn<ShopItem, String> columnShopItem;

    @FXML
    private TableColumn<ShopItem, Image> columnShopImage;

    @FXML
    private ChoiceBox<String> animalChoiceBox;

    @FXML
    private TableColumn<ShopItem, Integer> columnShopPrice;

    @FXML
    private Label labelBalance;
    @FXML
    private Label labelTotalRevenue;
    @FXML
    private Label labelTotalExpenses;
    @FXML
    private Label labelNetProfit;
    @FXML
    private Label weatherLabel;


    private static final int ROWS = 10;
    private static final int COLS = 10;
    private Player player;
    private Inventory inventory = new Inventory();
    private HashMap<String, Crop> crops = new HashMap<>();
    private ObservableList<InventoryItem> inventoryData = FXCollections.observableArrayList();
    private ObservableList<ShopItem> shopData = FXCollections.observableArrayList();

    public void initialize() {
        player = new Player(500);
        initializeCrops();
        initializeShop();
        initializeAnimals();
        startWeatherCycle();
        updateMoneyDisplay();
        createFarmGrid();
        updateInventoryDisplay();


        columnItem.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        inventoryTable.setItems(inventoryData);

        columnShopImage.setCellValueFactory(new PropertyValueFactory<>("icon"));
        columnShopItem.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnShopPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        shopTable.setItems(shopData);

        seedChoiceBox.getItems().addAll(crops.keySet());
        seedChoiceBox.setValue("Blé");

        shopPane.setVisible(false);
    }

    private void initializeAnimals() {
        animalChoiceBox.getItems().addAll("Vache", "Poule", "Mouton");
        animalChoiceBox.setValue("Vache");
    }

    private void initializeCrops() {
        crops.put("Blé", new Crop("Blé", 20, 50, 60, new String[]{
                "/assets/images/crops/wheat_stage1.png",
                "/assets/images/crops/wheat_stage2.png",
                "/assets/images/crops/wheat_stage3.png",
                "/assets/images/crops/wheat_stage4.png"
        }));

        crops.put("Tomate", new Crop("Tomate", 30, 50, 60, new String[]{
                "/assets/images/crops/tomato_stage1.png",
                "/assets/images/crops/tomato_stage2.png",
                "/assets/images/crops/tomato_stage3.png",
                "/assets/images/crops/tomato_stage4.png"
        }));

        crops.put("Pomme de terre", new Crop("Pomme de terre", 15, 25, 60, new String[]{
                "/assets/images/crops/potato_stage1.png",
                "/assets/images/crops/potato_stage2.png",
                "/assets/images/crops/potato_stage3.png",
                "/assets/images/crops/potato_stage4.png"
        }));
    }

    private void initializeShop() {
        if (!inventory.getStorage().containsKey("Blé") || !inventory.getStorage().get("Blé").getType().equals("graine")) {
            shopData.add(new ShopItem("Graine de Blé", 20, "/assets/images/icons/wheat.png"));
        }

        if (!inventory.getStorage().containsKey("Tomate") || !inventory.getStorage().get("Tomate").getType().equals("graine")) {
            shopData.add(new ShopItem("Graine de Tomate", 30, "/assets/images/icons/tomato.png"));
        }

        if (!inventory.getStorage().containsKey("Pomme de terre") || !inventory.getStorage().get("Pomme de terre").getType().equals("graine")) {
            shopData.add(new ShopItem("Graine de Pomme de terre", 15, "/assets/images/icons/potato.png"));
        }
    }

    private Rectangle selectedParcel = null;

    private void createFarmGrid() {
        double cellWidth = farmGrid.getPrefWidth() / COLS;
        double cellHeight = farmGrid.getPrefHeight() / ROWS;

        Image soilTexture = new Image(getClass().getResourceAsStream("/assets/images/terrain/soil.jpg"));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Rectangle parcel = new Rectangle(cellWidth, cellHeight);
                parcel.setFill(new ImagePattern(soilTexture));
                parcel.setStroke(Color.DARKGOLDENROD);

                parcel.setOnMouseClicked(event -> {
                    if (selectedParcel != null) {
                        selectedParcel.setStroke(Color.DARKGOLDENROD);
                    }

                    selectedParcel = parcel;
                    selectedParcel.setStroke(Color.RED);
                });

                farmGrid.add(parcel, col, row);
            }
        }
    }

    @FXML
    private void plantCrop() {
        String selectedCrop = seedChoiceBox.getValue();

        if (selectedCrop == null || selectedParcel == null) {
            System.out.println("Veuillez sélectionner une graine et une parcelle.");
            return;
        }

        if(selectedParcel.getUserData()instanceof Animal){
            System.out.println("Impossible de planter ici, un animal est déjà présent");
            return;
        }

        if (selectedParcel.getUserData() != null) {
            System.out.println("Cette parcelle est déjà occupée !");
            return;
        }

        if (!inventory.getStorage().containsKey(selectedCrop) ||
                !inventory.getStorage().get(selectedCrop).getType().equals("graine")) {
            System.out.println("Vous devez acheter cette graine avant de pouvoir la planter.");
            return;
        }

        Crop crop = crops.get(selectedCrop);
        if (crop == null) {
            System.out.println("Culture non reconnue.");
            return;
        }

        if (inventory.removeItem(selectedCrop, 1)) {
            System.out.println(selectedCrop + " plantée !");

            selectedParcel.setUserData(crop);
            animateGrowth(selectedParcel, crop);

            updateInventoryDisplay();

            Rectangle previousParcel = selectedParcel;
            selectedParcel = null;
            previousParcel.setStroke(Color.DARKGOLDENROD);
        } else {
            System.out.println("Erreur lors de la plantation.");
        }
    }

    private void animateGrowth(Rectangle selectedParcel, Crop crop) {
        double timePerStage = crop.getGrowthTime() * 0.2;

        if(weather.equals("Pluie")){
            timePerStage *= 0.75;
        }else if (weather.equals("Sécheresse")){
            timePerStage *= 1.5;
        }

        Timeline timeline = new Timeline();

        for(int i = 0; i < 4; i++){
            final int stage = i;
            final Image growthStage = crop.getGrowthStages()[i];

            KeyFrame kf = new KeyFrame(Duration.seconds(timePerStage * (stage + 1)), event -> {
                selectedParcel.setFill(new ImagePattern(growthStage));

                if(stage == 3){
                    selectedParcel.setStroke(Color.GREEN);
                    selectedParcel.setOnMouseClicked(event1 -> {
                        inventory.addItem(crop.getName() + "_récolte", 1, "récolte", "/assets/images/crops/" + crop.getName().toLowerCase() + ".png");
                        updateInventoryDisplay();
                        resetParcel(selectedParcel);
                    });
                }
            });
            timeline.getKeyFrames().add(kf);
        }
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void resetParcel(Rectangle parcel) {
        Image soilTexture = new Image(getClass().getResourceAsStream("/assets/images/terrain/soil.jpg"));
        parcel.setFill(new ImagePattern(soilTexture));
        parcel.setUserData(null);

        parcel.setOnMouseClicked(event -> {
            if (selectedParcel != null) {
                selectedParcel.setStroke(Color.DARKGOLDENROD);
            }
            selectedParcel = parcel;
            selectedParcel.setStroke(Color.RED);
        });
    }


    private void updateMoneyDisplay() {
        moneyLabel.setText("Pièces: " + String.format("%,d", player.getMoney()));
    }


    private void updateInventoryDisplay() {
        inventoryData.clear();

        for (InventoryItem item : inventory.getItems()) {
            if(item.getType().equals("animal")){
                System.out.println("Animal présent dans l'inventaire : " + item.getName());
            }
            inventoryData.add(item);
        }

        inventoryTable.refresh();
    }

    private void updateFinancialDashboard() {
        labelBalance.setText("💰 " + player.getMoney());
        labelTotalRevenue.setText("📈 " + player.getTotalRevenue());
        labelTotalExpenses.setText("📉 " + player.getTotalExpenses());
        labelNetProfit.setText("⚖️ " + player.getTotalProfit());
    }

    @FXML
    private void sellCrops() {
        int totalMoneyEarned = 0;
        ArrayList<InventoryItem> itemsToRemove = new ArrayList<>();

        for (InventoryItem item : inventory.getItems()) {
            if (item.isHarvestedCrop()) {
                String baseCropName = item.getBaseCropName();
                Crop crop = crops.get(baseCropName);

                if (crop != null) {
                    int saleAmount = crop.getSellPrice() * item.getQuantity();
                    totalMoneyEarned += saleAmount;
                    itemsToRemove.add(item);
                }
            }
        }

        if (totalMoneyEarned > 0) {
            player.addMoney(totalMoneyEarned);
            player.addRevenue(totalMoneyEarned);
            updateFinancialDashboard();

            for (InventoryItem item : itemsToRemove) {
                inventory.removeItem(item.getName(), item.getQuantity());
            }

            updateMoneyDisplay();
            updateInventoryDisplay();
            System.out.println("Récoltes vendues pour " + totalMoneyEarned + " pièces !");
        } else {
            System.out.println("Aucune récolte à vendre.");
        }
    }

    @FXML
    private void sellAnimals() {
        String selectedAnimal = animalChoiceBox.getValue();

        InventoryItem item = inventory.getStorage().get(selectedAnimal);

        if(item != null && item.getType().equals("animal")){
            Animal animal = item.getAnimal();
            int animalSellPrice = animal.getSellPrice();

            if(inventory.removeItem(selectedAnimal, 1)){
                player.addMoney(animalSellPrice);
                player.addRevenue(animalSellPrice);
                player.updateProfit();

                updateMoneyDisplay();
                updateInventoryDisplay();
                updateFinancialDashboard();
                removeAnimalFromFarm(selectedAnimal);
                System.out.println("Vous avez vendu votre " + selectedAnimal + " pour " + animalSellPrice + " pièces.");
            }else{
                System.out.println("Aucun animal à vendre");
            }
        }
    }

    private void animateAnimalGrowth(Rectangle animalSprite, Animal animal) {
        double timePerStage = 5;

        Timeline timeline = new Timeline();

        for (int i = 0; i < 4; i++) {
            final int stage = i;
            final Image growthStage = new Image(getClass().getResourceAsStream(animal.getGrowthStages()[i]));

            KeyFrame kf = new KeyFrame(Duration.seconds(timePerStage * (stage + 1)), event -> {
                animalSprite.setFill(new ImagePattern(growthStage));

                if (stage == 3) {
                    animalSprite.setStroke(Color.GREEN);
                }
            });
            timeline.getKeyFrames().add(kf);
        }

        timeline.setCycleCount(1);
        timeline.play();
    }

    private void displayAnimalOnFarm(Animal animal) {
        double cellWidth = farmGrid.getPrefWidth() / COLS;
        double cellHeight = farmGrid.getPrefHeight() / ROWS;

        int randomRow = (int) (Math.random() * ROWS);
        int randomCol = (int) (Math.random() * COLS);

        Rectangle animalSprite = new Rectangle(cellWidth, cellHeight);
        Image initialImage = new Image(getClass().getResourceAsStream(animal.getGrowthStages()[0]));
        animalSprite.setFill(new ImagePattern(initialImage));

        animalSprite.setUserData(animal.getName());

        farmGrid.add(animalSprite, randomCol, randomRow);

        animateAnimalGrowth(animalSprite, animal);
    }


    @FXML
    private void openShop() {
        shopPane.setVisible(true);
    }

    @FXML
    private void closeShop() {
        shopPane.setVisible(false);
    }

    @FXML
    private void buyItem() {
        ShopItem selectedItem = shopTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            if (player.getMoney() < selectedItem.getPrice()) {
                System.out.println("Vous n'avez pas assez d'argent pour acheter cette graine.");
                return;
            }

            String cropName = selectedItem.getName();

            if (cropName.startsWith("Graine de ")) {
                cropName = cropName.replace("Graine de ", "");
            }

            System.out.println("Nom après modification : " + cropName);

            String imagePath;

            switch (cropName) {
                case "Blé":
                    imagePath = "/assets/images/icons/wheat.png";
                    break;
                case "Tomate":
                    imagePath = "/assets/images/icons/tomato.png";
                    break;
                case "Pomme de terre":
                    imagePath = "/assets/images/icons/potato.png";
                    break;
                default:
                    imagePath = "/assets/images/icons/default.png";
                    break;
            }

            int quantityToBuy = 1;

            if (!inventory.getStorage().containsKey(cropName)) {
                inventory.addItem(cropName, quantityToBuy, "graine", imagePath);
                System.out.println(cropName + " achetée !");
            } else {
                inventory.addItem(cropName, quantityToBuy, "graine", imagePath);
                System.out.println("Vous avez déjà cette graine. Quantité mise à jour.");
            }

            if (player.spendMoney(selectedItem.getPrice() * quantityToBuy)) {
                player.addExpense(selectedItem.getPrice() * quantityToBuy);
                updateFinancialDashboard();
                updateMoneyDisplay();
            }

            updateInventoryDisplay();
            updateShopDisplay();
        }
    }

    @FXML
    private void buyAnimal() {
        String selectedAnimal = animalChoiceBox.getValue();
        Animal animal = null;

        switch (selectedAnimal) {
            case "Vache":
                animal = new Animal("Vache", "élevage", new String[]{
                        "/assets/images/animals/cow_stage1.png",
                        "/assets/images/animals/cow_stage2.png",
                        "/assets/images/animals/cow_stage3.png",
                        "/assets/images/animals/cow_stage4.png"
                }, "Lait", 50, 50);
                break;
            case "Poule":
                animal = new Animal("Poule", "élevage", new String[]{
                        "/assets/images/animals/chicken_stage1.png",
                        "/assets/images/animals/chicken_stage2.png",
                        "/assets/images/animals/chicken_stage3.png",
                        "/assets/images/animals/chicken_stage4.png"
                }, "Oeuf", 30, 30);
                break;
            case "Mouton":
                animal = new Animal("Mouton", "élevage", new String[]{
                        "/assets/images/animals/sheep_stage1.png",
                        "/assets/images/animals/sheep_stage2.png",
                        "/assets/images/animals/sheep_stage3.png",
                        "/assets/images/animals/sheep_stage4.png"
                }, "Laine", 60, 60);
                break;
        }

        if (animal != null) {
            int animalPrice = 100;
            if (player.getMoney() >= animalPrice) {
                player.spendMoney(animalPrice);
                player.addExpense(animalPrice);

                inventory.addItem(animal, 1);
                updateMoneyDisplay();
                updateInventoryDisplay();
                updateFinancialDashboard();

                displayAnimalOnFarm(animal);
                System.out.println(animal.getName() + " acheté et placé dans la ferme !");
            } else {
                System.out.println("Vous n'avez pas assez d'argent pour acheter cet animal.");
            }
        }
    }

    @FXML
    private void feedAnimal() {
        String selectedAnimal = animalChoiceBox.getValue();

        for(InventoryItem item : inventory.getItems()){
            if(item.getName().equals(selectedAnimal) && item.getType().equals("animal")){
                Animal animal = item.getAnimal();

                if (animal != null) {
                    if (player.spendMoney(40)) { // 🔥 Déduit 40 pièces si le joueur a assez d'argent
                        animal.feed();
                        updateMoneyDisplay(); // 🔥 Met à jour l'affichage du solde
                        updateFinancialDashboard(); // 🔥 Met à jour le tableau de bord
                        System.out.println(animal.getName() + " a été nourri !");
                    } else {
                        System.out.println("Pas assez d'argent pour nourrir " + animal.getName() + " !");
                    }
                    return;
                }
            }
        }
        System.out.println("Cet animal n'est pas présent dans ta ferme.");
    }

    @FXML
    private void collectAnimalProduct() {
        String selectedAnimal = animalChoiceBox.getValue();

        for(InventoryItem item : inventory.getItems()){
            if(item.getName().equals(selectedAnimal) && item.getType().equals("animal")){
                Animal animal = item.getAnimal();

                if(animal != null){
                    String resource = animal.produceResource();

                    if(resource != null){
                        inventory.addItem(resource, 1,"produit", null);
                        animal.setFed(false);
                        updateInventoryDisplay();
                        System.out.println(animal.getName() + " a produit " + resource);
                    } else {
                        System.out.println(animal.getName() + " doit être nourri avant de produire !");
                    }
                    return;
                }
            }
        }
        System.out.println("Cet animal n'est pas présent pas dans ta ferme.");
    }

    @FXML
    private void sellAnimalProducts(){
        int totalMoney = 0;
        ArrayList<InventoryItem> itemsToRemove = new ArrayList<>();

        for(InventoryItem item : inventory.getItems()){
            if(item.getType().equals("produit")){
            int resourceValue = 50;
            totalMoney += resourceValue * item.getQuantity();
            itemsToRemove.add(item);
            }
        }

        player.addMoney(totalMoney);
        player.addRevenue(totalMoney);
        player.updateProfit();

        for(InventoryItem item : itemsToRemove){
            inventory.removeItem(item.getName(), item.getQuantity());
        }

        updateMoneyDisplay();
        updateInventoryDisplay();
        updateFinancialDashboard();

        System.out.println("Vous avez vendu vos produits d'animaux pour " + totalMoney + " pièces.");
    }

    private void removeAnimalFromFarm(String animalName) {
        for (javafx.scene.Node node : new ArrayList<>(farmGrid.getChildren())) {
            if (node instanceof Rectangle) {
                Rectangle rect = (Rectangle) node;

                if (rect.getUserData() != null && rect.getUserData().equals(animalName)) {
                    Image soilTexture = new Image(getClass().getResourceAsStream("/assets/images/terrain/soil.jpg"));
                    rect.setFill(new ImagePattern(soilTexture));

                    rect.setUserData(null);

                    farmGrid.getChildren().remove(rect);
                    break;
                }
            }
        }
    }


    private void updateShopDisplay() {
        shopTable.refresh();
    }

    private String weather = "Soleil";
    private void startWeatherCycle() {
        Timeline weatherTimeline = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
            String[] weatherTypes = {"Soleil", "Pluie", "Sécheresse"};
            weather = weatherTypes[(int) (Math.random() * weatherTypes.length)];

            String emoji = weather.equals("Pluie") ? "🌧️" : weather.equals("Sécheresse") ? "🔥" : "☀️";
            weatherLabel.setText(emoji + " Météo : " + weather);
        }));
        weatherTimeline.setCycleCount(Timeline.INDEFINITE);
        weatherTimeline.play();
    }

    @FXML
    private void saveFarmState() {
        List<Savable> farmState = new ArrayList<>();
        farmState.addAll(inventory.getItems());
        farmState.add(player);

        SaveManager.saveFarm(farmState);
        System.out.println("État de la ferme sauvegardé !");
    }

    @FXML
    private void loadFarmState() {
        List<Savable> loadedData = SaveManager.loadFarm();

        if (loadedData != null) {
            inventory.getItems().clear();

            for (Savable obj : loadedData) {
                if (obj instanceof InventoryItem) {
                    InventoryItem item = (InventoryItem) obj;
                    inventory.addItem(item);

                    if (item.getType().equals("animal") && item.getAnimal() != null) {
                        displayAnimalOnFarm(item.getAnimal());
                    }
                } else if (obj instanceof Player) {
                    player.setMoney(((Player) obj).getMoney());
                }
            }

            updateInventoryDisplay();
            updateMoneyDisplay();

            System.out.println("État de la ferme chargé !");
        } else {
            System.out.println("Aucun fichier de sauvegarde trouvé.");
        }
    }

}



