import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Actor extends Circle {

    public static final double ACTOR_RADIUS = 4;
    public static final double ACTOR_MOVESPEED = 1;
    private boolean isZombie = false;

    public Actor(double centerX, double centerY) {
        super(centerX, centerY, ACTOR_RADIUS, Color.GREEN);
    }

    public void act() {
        double dx = (Math.random() * ACTOR_MOVESPEED * 2) - ACTOR_MOVESPEED;
        double dy = (Math.random() * ACTOR_MOVESPEED * 2) - ACTOR_MOVESPEED;

        setTranslateX(dx);
        setTranslateY(dy);

        setCenterX(getCenterX() + getTranslateX());
        setCenterY(getCenterY() + getTranslateY());
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
