package models;

public class Player {
    private int money;

    public Player(int startingMoney) {
        this.money = startingMoney;
    }
    public int getMoney() {
        return money;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public boolean spendMoney(int amount) {
        if(this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }
}
