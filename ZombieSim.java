import java.util.ArrayList;
import javafx.application.Application;
import javafx.animation.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.util.Duration;

public class ZombieSim extends Application {

    public static final int CANVAS_WIDTH = (int) Screen.getPrimary().getVisualBounds().getWidth();
    public static final int CANVAS_HEIGHT = (int) Screen.getPrimary().getVisualBounds().getHeight();
    public static final int NUM_STARTING_ACTORS = 1000;

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
        for (int i = 0; i < 5; i++) {
            actors.get(i).makeZombie();
        }

        final Timeline loop = new Timeline(new KeyFrame(Duration.millis(100),
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        for (Actor a : actors) {
                            a.act(CANVAS_WIDTH, CANVAS_HEIGHT, actors);
                        }

                        spreadInfectionOnCollision();
                    }
                }));
        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
    }

    //TODO make far more efficient, consider grid system
    //TODO move collision logic to Actor
    private void spreadInfectionOnCollision() {
        Actor actorA;
        Actor actorB;

        for (int i = 0; i < actors.size(); i++) {
            for (int j = i+1; j < actors.size(); j++) {
                actorA = actors.get(i);
                actorB = actors.get(j);

                if (actorA.isZombie() || actorB.isZombie()) {
                    double aX = actorA.getCenterX();
                    double aY = actorA.getCenterY();
                    double bX = actorB.getCenterX();
                    double bY = actorB.getCenterY();

                    double dist = Math.sqrt(Math.pow(bX - aX, 2) + Math.pow(bY - aY, 2));

                    if (dist <= Actor.ACTOR_RADIUS * 2) {
                        actorA.makeZombie();
                        actorB.makeZombie();
                    }
                }
            }
        }
    }
}
