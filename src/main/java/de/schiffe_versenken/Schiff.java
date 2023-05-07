package de.schiffe_versenken;


import javafx.scene.Parent;

public class Schiff extends Parent{
    public int type;
    public boolean vertical;

    private int health;

    public Schiff(int type, boolean vertical) {
        this.type = type;
        this.vertical = vertical;
        health = type;
    }

    public void hit() {
        health--;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
