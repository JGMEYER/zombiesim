import java.util.ArrayList;
import javafx.application.Application;
import javafx.animation.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.util.Duration;

public class ZombieSim extends Application {

    public static final int CANVAS_WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth();
    public static final int CANVAS_HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight();
    public static final int NUM_STARTING_ACTORS = 1000;
    public static final int NUM_ALPHA_ZOMBIES = 5;

    private Pane canvas;
    private ArrayList<Actor> actors;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        canvas = new Pane();
        final Scene scene = new Scene(canvas, CANVAS_WIDTH, CANVAS_HEIGHT, Color.web("#98FB98"));
        primaryStage.setTitle("Zombie Outbreak Simulator v0");
        primaryStage.setScene(scene);
        primaryStage.show();

        actors = new ArrayList<Actor>();
        // create actors
        for (int i = 0; i < NUM_STARTING_ACTORS; i++) {
            double x = Math.random() * CANVAS_WIDTH;
            double y = Math.random() * CANVAS_HEIGHT;

            Actor a = new Actor(canvas, x, y);

            actors.add(a);
            canvas.getChildren().add(a);
        }
        // designate alpha zombies
        for (int i = 0; i < NUM_ALPHA_ZOMBIES; i++) {
            actors.get(i).makeZombie();
        }

        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isSecondaryButtonDown()) {
                    for (Actor a : actors) {
                        if (a.isPlayerControlled()) {
                            a.setPointTarget(event.getX(), event.getY());
                        }
                    }
                }
            }
        });
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isSecondaryButtonDown()) {
                    for (Actor a : actors) {
                        if (a.isPlayerControlled()) {
                            a.setPointTarget(event.getX(), event.getY());
                        }
                    }
                }
            }
        });

        final Timeline loop = new Timeline(new KeyFrame(Duration.millis(100),
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        for (Actor a : actors) {
                            a.act(actors);
                        }

                        spreadInfectionOnCollision();
                    }
                }));
        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
    }

    //TODO make far more efficient, consider grid system
    private void spreadInfectionOnCollision() {
        Actor actorA;
        Actor actorB;

        for (int i = 0; i < actors.size(); i++) {
            for (int j = i+1; j < actors.size(); j++) {
                actorA = actors.get(i);
                actorB = actors.get(j);

                if (actorA.isZombie() || actorB.isZombie()) {
                    if (actorA.collidesWith(actorB)) {
                        actorA.makeZombie();
                        actorB.makeZombie();
                    }
                }
            }
        }
    }
}
