package slotmachine;

import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SlotMachine extends Application {

    double bank = 1000.00;
    Image fruit = new Image(SlotMachine.class.getResourceAsStream("fruits.jpg"));
    Rectangle2D[] viewportRect = new Rectangle2D[9];
    ImageView[][] image = new ImageView[3][3];
    int a = 0, b = 0, c = 0; // these values are used to store the last state of the wheel
    Label label = new Label("$" + Double.toString(bank));

    Button btn = new Button();
    @Override
    public void start(Stage primaryStage) {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        //grid.setHgap(10);

        
        btn.setText("Try your luck!");
        grid.add(btn, 1, 0);

        //StringProperty stringProperty = new SimpleStringProperty();
        //label.textProperty().bind(stringProperty);
        grid.add(label, 2, 0);

        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                viewportRect[k] = new Rectangle2D(
                        (((int) fruit.getWidth() / 3) * i), //Xpos
                        (((int) fruit.getHeight() / 3) * j), //Ypos
                        (int) fruit.getHeight() / 3, //height
                        (int) fruit.getWidth() / 3 //width
                );

                image[i][j] = new ImageView(fruit);
                image[i][j].setViewport(viewportRect[k]);
                //image[i][j].setPreserveRatio(true);
                //image[i][j].setSmooth(true);
                if (j % 2 == 0) {
                    image[i][j].setFitHeight(50);
                } else {
                    image[i][j].setFitHeight(115);
                }
                image[i][j].setFitWidth(115);

                grid.add(image[i][j], i, j + 1);
                k++;
            }
        }

        Scene scene = new Scene(grid, 360, 300);
        scene.getStylesheets().add(SlotMachine.class.getResource("Gambler.css").toExternalForm());

        primaryStage.setTitle("Slot Machine");
        primaryStage.setScene(scene);
        primaryStage.show();

        btn.setOnAction(e -> {
            btn.setDisable(true); // disable the button while the wheels are spinning
            spin();
            
            if (bank <= 0.0) {
                primaryStage.close();
            }

        });

    }

    public static void main(String[] args) {
        launch(args);
    }

    void spin() {

        int time = 200;
        int rand = ((int) (Math.random() * ((30 - 10) + 1)) + 10);
        
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < rand; i++, a++) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                playSound(2);
                                image[0][0].setViewport(viewportRect[a % 9]);
                                image[0][1].setViewport(viewportRect[(a + 1) % 9]);
                                image[0][2].setViewport(viewportRect[(a + 2) % 9]);
                                //System.out.println("spin a = " + a);
                            }
                        });
                        Thread.sleep(time);
                    }
                } catch (InterruptedException ex) {
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < rand + (rand % 5); i++, b++) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                playSound(2);
                                image[1][0].setViewport(viewportRect[(b + 3) % 9]);
                                image[1][1].setViewport(viewportRect[(b + 4) % 9]);
                                image[1][2].setViewport(viewportRect[(b + 5) % 9]);
                                //System.out.println("spin b = " + b);
                            }
                        });
                        Thread.sleep(time + 100);
                    }
                } catch (InterruptedException ex) {
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < rand + (rand % 10); i++, c++) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                playSound(2);
                                image[2][0].setViewport(viewportRect[(c + 6) % 9]);
                                image[2][1].setViewport(viewportRect[(c + 7) % 9]);
                                image[2][2].setViewport(viewportRect[(c + 8) % 9]);
                                //System.out.println("spin c = " + c);
                            }
                        });
                        Thread.sleep(time + 200);
                    }
                    if (win()) {
                        bank += 1000.00;
                        playSound(1);
                    } else {
                        bank -= 10.00;
                        playSound(0);
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            label.setText("$" + Double.toString(bank));
                            btn.setDisable(false);
                        }
                    });
                    
                } catch (InterruptedException ex) {
                }
            }
        }).start();

    }

    boolean win() {

        if (image[0][1].getViewport() == image[1][1].getViewport()
                && image[1][1].getViewport() == image[2][1].getViewport()) {
            return true;
        } else {
            return false;
        }
    }

    void playSound(int p) {

        Duration duration = new Duration(2000);
        String PATH = "";
        // must change the file path accordingly
        PATH = "C:\\Users\\trini\\Documents\\NetBeansProjects\\SlotMachine\\src\\slotmachine\\you_WinLose.WAV";

        if (p == 2) // tick
        {
            // must change the file path accordingly
            PATH = "C:\\Users\\trini\\Documents\\NetBeansProjects\\SlotMachine\\src\\slotmachine\\tick.WAV";
        }

        File f = new File(PATH);
        Media media = new Media(f.toURI().toString());
        MediaPlayer mPlayer = new MediaPlayer(media);

        if (p == 0) {
            mPlayer.setStartTime(duration);
        } else if (p == 1) {
            duration.subtract(duration);
            mPlayer.setStopTime(duration);
        }

        mPlayer.play();
    }

}
