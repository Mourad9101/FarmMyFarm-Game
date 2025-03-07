package controllers;

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
import models.*;

import java.util.HashMap;
import java.util.Objects;

public class GameController {

    @FXML
    private TableColumn<ShopItem, Image> columnShopImage;

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
    private TableColumn<ShopItem, Integer> columnShopPrice;

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

    private void initializeCrops() {
        crops.put("Blé", new Crop("Blé", 20, 50, 60, new String[]{
                "/assets/images/crops/wheat_stage1.png",
                "/assets/images/crops/wheat_stage2.png",
                "/assets/images/crops/wheat_stage3.png",
                "/assets/images/crops/wheat_stage4.png"
        }));

        crops.put("Tomate", new Crop("Tomate", 25, 70, 80, new String[]{
                "/assets/images/crops/tomato_stage1.png",
                "/assets/images/crops/tomato_stage2.png",
                "/assets/images/crops/tomato_stage3.png",
                "/assets/images/crops/tomato_stage4.png"
        }));

        crops.put("Pomme de terre", new Crop("Pomme de terre", 25, 70, 80, new String[]{
                "/assets/images/crops/potato_stage1.png",
                "/assets/images/crops/potato_stage2.png",
                "/assets/images/crops/potato_stage3.png",
                "/assets/images/crops/potato_stage4.png"
        }));
    }

    private void initializeShop() {
        if (!inventory.getStorage().containsKey("Blé") || !inventory.getStorage().get("Blé").getType().equals("graine")) {
            shopData.add(new ShopItem("Graine de Blé", 10, "/assets/images/icons/wheat.png"));
        }

        if (!inventory.getStorage().containsKey("Tomate") || !inventory.getStorage().get("Tomate").getType().equals("graine")) {
            shopData.add(new ShopItem("Graine de Tomate", 15, "/assets/images/icons/tomato.png"));
        }

        if (!inventory.getStorage().containsKey("Pomme de terre") || !inventory.getStorage().get("Pomme de terre").getType().equals("graine")) {
            shopData.add(new ShopItem("Graine de Pomme de terre", 12, "/assets/images/icons/potato.png"));
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
            System.out.println("Graine de " + selectedCrop + " plantée !");

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

    private void animateGrowth(Rectangle parcel, Crop crop) {
        new Thread(() -> {
            try {
                // Calculate time per stage (25% of total growth time per stage)
                long timePerStage = crop.getGrowthTime() * 130;

                for (int i = 0; i < 4; i++) {
                    Image growthStage = crop.getGrowthStages()[i];
                    Thread.sleep(timePerStage);

                    int finalStage = i;
                    javafx.application.Platform.runLater(() -> {
                        parcel.setFill(new ImagePattern(growthStage));
                        if (finalStage == 3) {
                            parcel.setStroke(Color.GREEN);
                            parcel.setOnMouseClicked(event -> {
                                inventory.addItem(crop.getName() + "_récolte", 1, "récolte",
                                        "/assets/images/icons/" + crop.getName().toLowerCase() + ".png");
                                updateInventoryDisplay();
                                resetParcel(parcel);
                            });
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }



    private void resetParcel(Rectangle parcel) {
        Image soilTexture = new Image(getClass().getResourceAsStream("/assets/images/terrain/soil.jpg"));
        parcel.setFill(new ImagePattern(soilTexture));
        parcel.setUserData(null); // Clear the crop data

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
            inventoryData.add(item);
        }

        inventoryTable.refresh();
    }


    @FXML
    private void sellCrops() {
        int totalMoney = 0;
        ArrayList<InventoryItem> itemsToRemove = new ArrayList<>();

        // Calculate total money and identify items to remove
        for (InventoryItem item : inventory.getItems()) {
            if (item.isHarvestedCrop()) {
                String baseCropName = item.getBaseCropName();
                Crop crop = crops.get(baseCropName);

                if (crop != null) {
                    totalMoney += crop.getSellPrice() * item.getQuantity();
                    itemsToRemove.add(item);
                }
            }
        }

        player.addMoney(totalMoney);

        for (InventoryItem item : itemsToRemove) {
            inventory.removeItem(item.getName(), item.getQuantity());
        }

        updateMoneyDisplay();
        updateInventoryDisplay();
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

            String cropName = selectedItem.getName().replace("Graine de ", "");
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
                System.out.println("Graine " + cropName + " achetée !");
            } else {
                inventory.addItem(cropName, quantityToBuy, "graine", imagePath);
                System.out.println("Vous avez déjà cette graine. Quantité mise à jour.");
            }

            if (player.spendMoney(selectedItem.getPrice() * quantityToBuy)) {
                updateMoneyDisplay();
            }

            updateInventoryDisplay();
            updateShopDisplay();
        }
    }

    private void updateShopDisplay() {
        shopTable.refresh();
    }

}
