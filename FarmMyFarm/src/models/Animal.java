package models;

public class Animal {
    private String name;
    private int age;
    private boolean isFed;
    private String type;

    public Animal(String name, int age, String type) {
        this.name = name;
        this.age = age;
        this.type = type;
        this.isFed = false;
    }

    // Getter pour le nom
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isFed() {
        return isFed;
    }

    public void feed() {
        this.isFed = true;
    }

    public void feedAndGrow() {
        feed();
        this.age++;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return "Nom : " + name + ", Type : " + type + ", Âge : " + age + ", Nourri : " + (isFed ? "Oui" : "Non");
    }
}
