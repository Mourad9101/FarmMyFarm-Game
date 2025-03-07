package models;

import javafx.scene.image.Image;

public class Crop {
    private String name;
    private int cost;
    private int harvestValue;
    private int growthTime;
    private Image[] growthStages;
    private int sellPrice;

    public Crop(String name, int cost, int harvestValue, int growthTime, String[] imagePaths) {
        this.name = name;
        this.cost = cost;
        this.harvestValue = harvestValue;
        this.growthTime = growthTime;
        this.growthStages = new Image[4];

        for (int i = 0; i < 4; i++) {
            this.growthStages[i] = new Image(getClass().getResourceAsStream(imagePaths[i]));
        }
        this.sellPrice = harvestValue / 2;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public String getName() {
        return name; }

    public int getCost() {
        return cost; }

    public int getHarvestValue() {
        return harvestValue; }

    public int getGrowthTime() {
        return growthTime; }

    public Image[] getGrowthStages() {
        return growthStages; }
}
