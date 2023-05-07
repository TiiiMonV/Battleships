package de.schiffe_versenken;

import de.schiffe_versenken.Spielbrett.Zelle;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;


public class BattleShipHauptklasse extends Application {
    private boolean running = false;
    private Spielbrett gegnerSpielbrett, spielerSpielbrett;

    private int shipsToPlace = 5;

    private boolean enemyTurn = false;

    private final Random random = new Random();

    private PauseTransition pauseTransition = null;

    private Parent createContent() {

        //Größe des Fensters festlegen!
        BorderPane root = new BorderPane();
        root.setPrefSize(900, 700);

        //Textfelder beschriften!
        Text gegnerText = new Text("Gegnerisches Spielbrett");
        Text spielerText = new Text("Spieler Spielbrett");


        gegnerSpielbrett = new Spielbrett(true, event -> {
            if (!running) {
                return;
            }

            Zelle zelle = (Zelle) event.getSource();
            if (zelle.wasShot) {
                return;
            }


            enemyTurn = !zelle.shoot();


            if (gegnerSpielbrett.ships == 0) {
                System.out.println("YOU WIN");
                System.exit(0);
            }

            if (enemyTurn) {
                enemyMove();
            }
        });

        // Schiffe auf dem Spielbrett platzieren!
        spielerSpielbrett = new Spielbrett(false, event -> {
            if (running) {
                return;
            }
            Zelle zelle = (Zelle) event.getSource();
            if (spielerSpielbrett.placeShip(new Schiff(shipsToPlace, event.getButton() == MouseButton.PRIMARY), zelle.x, zelle.y)) {
                if (--shipsToPlace == 0) {
                    startGame();
                }
            }
        });


        //Spielfelder anzeigen lassen bzw. setzen mit Abstand (25) und beschriften!
        VBox vbox = new VBox(25, gegnerSpielbrett, spielerSpielbrett);
        vbox.setAlignment(Pos.CENTER);

        HBox gegnerBox = new HBox(gegnerText);
        HBox spielerBox = new HBox(spielerText);
        gegnerBox.setAlignment(Pos.CENTER);
        spielerBox.setAlignment(Pos.CENTER);

        root.setTop(gegnerBox);
        root.setBottom(spielerBox);

        root.setCenter(vbox);

        return root;
    }

    //GegnerZug mit Überprüfung, ob er getroffen hat und dann nochmal schießen darf!
    private void enemyMove() {
        pauseTransition.setOnFinished(event -> {
            int x = random.nextInt(10);
            int y = random.nextInt(10);


            Zelle zelle = spielerSpielbrett.getZelle(x, y);
            if (zelle.wasShot) {
                enemyMove();
            } else {
                boolean getroffen = zelle.shoot();

                if (spielerSpielbrett.ships == 0) {
                    System.out.println("YOU LOSE");
                    System.exit(0);
                } else if (getroffen) {
                    enemyMove();
                }
            }
        });
        pauseTransition.play();
    }


    // Gegner Schiffe werden zufällig platziert
    private void startGame() {

        pauseTransition = new PauseTransition(Duration.millis(400));

        int type = 5;
        while (type > 0) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            if (gegnerSpielbrett.placeShip(new Schiff(type, Math.random() < 0.5), x, y)) {
                type--;
            }
        }

        running = true;
    }

    public void start(Stage primaryStage) {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Schiffe versenken - Ediz / Finn / Timo");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            // Es wird ein Fenster angezeigt!
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText("Sind Sie sicher, dass Sie das Fenster schließen möchten?");
            alert.showAndWait().ifPresent(response -> {
                // Wenn der Benutzer OK klickt, wird das Fenster geschlossen
                if (response == javafx.scene.control.ButtonType.OK) {
                    primaryStage.close();
                }
            });
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
