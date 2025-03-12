package models;

public class Animal implements Savable {
    private String name;
    private boolean isFed;
    private String type;
    private String[] growthStages;
    private String resourceProduced;
    private int resourceValue;
    private int sellPrice;

    public Animal(String name, String type, String[] growthStages, String resourceProduced, int resourceValue, int sellPrice) {
        this.name = name;
        this.type = type;
        this.isFed = false;
        this.growthStages = growthStages;
        this.resourceProduced = resourceProduced;
        this.resourceValue = resourceValue;
        this.sellPrice = sellPrice;
    }


    public void feed(){
        this.isFed = true;
    }

    public String produceResource(){
        if(this.isFed){
            this.isFed = false;
            return this.resourceProduced;
        }
        return null;
    }

    public void setFed(boolean isFed) {
        this.isFed = isFed;
    }


    public String getName() {
        return name;
    }

    public String[] getGrowthStages() {
        return growthStages;
    }

    public int getResourceValue() {
        return resourceValue;
    }

    public int getSellPrice() {
        return sellPrice;
    }
}
