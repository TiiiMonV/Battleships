package de.schiffe_versenken;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;


public class Spielbrett extends Parent {
    private final VBox rows = new VBox();
    private final boolean enemy;
    public int ships = 5;

    public Spielbrett(boolean enemy, EventHandler<? super MouseEvent> handler) {
        this.enemy = enemy;
        for (int y = 0; y < 10; y++) {
            HBox row = new HBox();
            for (int x = 0; x < 10; x++) {
                Zelle c = new Zelle(x, y, this);
                c.setOnMouseClicked(handler);
                row.getChildren().add(c);
            }

            rows.getChildren().add(row);
        }

        getChildren().add(rows);
    }

    public boolean placeShip(Schiff schiff, int x, int y) {
        if (canPlaceShip(schiff, x, y)) {
            int length = schiff.type;

            if (schiff.vertical) {
                for (int i = y; i < y + length; i++) {
                    Zelle zelle = getZelle(x, i);
                    zelle.schiff = schiff;
                    if (!enemy) {
                        zelle.setFill(Color.LIGHTGREEN);
                        zelle.setStroke(Color.GREEN);
                    }
                }
            }
            else {
                for (int i = x; i < x + length; i++) {
                    Zelle zelle = getZelle(i, y);
                    zelle.schiff = schiff;
                    if (!enemy) {
                        zelle.setFill(Color.LIGHTGREEN);
                        zelle.setStroke(Color.GREEN);
                    }
                }
            }

            return true;
        }

        return false;
    }

    public Zelle getZelle(int x, int y) {
        return (Zelle)((HBox)rows.getChildren().get(y)).getChildren().get(x);
    }



    //Nimmt den aktuellen Wert der X bzw. Y Achse und rechnet -1 bzw. +1 und speichert das in einem Array!
    private Zelle[] getNeighbors(int x, int y) {
        Point2D[] points = new Point2D[] {
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1)
        };

        List<Zelle> neighbors = new ArrayList<>();

        for (Point2D p : points) {
            if (istValiderPunkt(p)) {
                neighbors.add(getZelle((int)p.getX(), (int)p.getY()));
            }
        }

        return neighbors.toArray(new Zelle[0]);
    }



    //Prüft, ob der Mausklick valide ist, sprich auf dem Spielfeld!

    //Prüft, ob in der Zelle schon ein Schiff vorhanden ist!

    // Prüft, ob das zu platzierende Schiff schon Nachbarn hat (rechnet auf den aktuellen Wert auf der
    // x - y-Achse +1 bzw. -1)!

    //Prüft, das entweder für X oder Y Achse je nachdem wie man das Schiff platziert!

    private boolean canPlaceShip(Schiff schiff, int x, int y) {
        int length = schiff.type;

        if (schiff.vertical) {
            for (int i = y; i < y + length; i++) {
                if (!istValiderPunkt(x, i)) {
                    return false;
                }

                Zelle zelle = getZelle(x, i);
                if (zelle.schiff != null) {
                    return false;
                }

                for (Zelle nachbar : getNeighbors(x, i)) {
                    if (!istValiderPunkt(x, i)) {
                        return false;
                    }

                    if (nachbar.schiff != null) {
                        return false;
                    }
                }
            }
        }
        else {
            for (int i = x; i < x + length; i++) {
                if (!istValiderPunkt(i, y))
                    return false;

                Zelle zelle = getZelle(i, y);
                if (zelle.schiff != null)
                    return false;

                for (Zelle nachbar : getNeighbors(i, y)) {
                    if (!istValiderPunkt(i, y))
                        return false;

                    if (nachbar.schiff != null)
                        return false;
                }
            }
        }

        return true;
    }

    private boolean istValiderPunkt(Point2D punkt) {
        return istValiderPunkt(punkt.getX(), punkt.getY());
    }

    private boolean istValiderPunkt(double x, double y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

    public static class Zelle extends Rectangle {
        public int x, y;
        public Schiff schiff = null;
        public boolean wasShot = false;

        private final Spielbrett spielbrett;


        // Größe und Farbe der Zelle bzw. der Border!
        public Zelle(int x, int y, Spielbrett spielbrett) {
            super(30, 30);
            this.x = x;
            this.y = y;
            this.spielbrett = spielbrett;
            setFill(Color.LIGHTGRAY);
            setStroke(Color.BLACK);
        }

        public boolean shoot(){
            wasShot = true;
            setFill(Color.BLACK);

            if (schiff != null) {
                schiff.hit();
                System.out.println("Treffer!");
                setFill(Color.RED);
                if (!schiff.isAlive()) {
                    spielbrett.ships--;
                }
                return true;
            }
            System.out.println("Kein Treffer!");
            return false;
        }
    }
}
