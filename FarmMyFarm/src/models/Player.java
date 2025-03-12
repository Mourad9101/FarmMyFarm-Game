package models;

public class Player implements Savable{
    private int money;
    private int totalRevenue;
    private int totalExpenses;
    private int totalProfit;

    public Player(int startingMoney) {
        this.money = startingMoney;
        this.totalRevenue = 0;
        this.totalExpenses = 0;
        this.totalProfit = 0;
    }
    public int getMoney() {
        return money;
    }
    public void setMoney(int money){
        this.money = money;
    }

    public void updateProfit() {
        totalProfit = totalRevenue - totalExpenses;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public boolean spendMoney(int amount) {
        if(this.money >= amount) {
            this.money -= amount;
            addExpense(amount);
            return true;
        }
        return false;
    }

    public void addRevenue(int amount) {
        this.totalRevenue += amount;
    }

    public void addExpense(int amount) {
        this.totalExpenses += amount;
    }

    public int getTotalRevenue() {
        return totalRevenue;
    }

    public int getTotalExpenses() {
        return totalExpenses;
    }
    public int getTotalProfit() {
        return totalProfit;
    }
}
