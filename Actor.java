import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Actor extends Circle {

    public static final double ACTOR_RADIUS = 4;
    public static final double ACTOR_MOVESPEED = 1;
    private boolean isZombie = false;

    public Actor(double centerX, double centerY) {
        super(centerX, centerY, ACTOR_RADIUS, Color.GREEN);
    }

    public void act(int canvasWidth, int canvasHeight) {
        double dx = (Math.random() * ACTOR_MOVESPEED * 2) - ACTOR_MOVESPEED;
        double dy = (Math.random() * ACTOR_MOVESPEED * 2) - ACTOR_MOVESPEED;

        setCenterX(getCenterX() + dx);
        setCenterY(getCenterY() + dy);

        if (getCenterX() < 0) {
            setCenterX(0);
        }
        if (getCenterX() > canvasWidth) {
            setCenterX(canvasWidth);
        }
        if (getCenterY() < 0) {
            setCenterY(0);
        }
        if (getCenterY() > canvasHeight) {
            setCenterY(canvasHeight);
        }
    }

    public boolean isZombie() {
        return isZombie;
    }

    public void makeZombie() {
        if (!isZombie) {
            isZombie = true;
            setFill(Color.RED);
        }
    }
}
