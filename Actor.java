import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Actor extends Circle {

    public static final double ACTOR_RADIUS = 7;
    public static final double ACTOR_MIN_MOVESPEED = 1;
    public static final double ACTOR_MAX_MOVESPEED = 3;

    private double moveSpeed;
    private boolean isZombie = false;

    private Line face;
    private Point2D faceUnitVector;

    public Actor(Pane canvas, double centerX, double centerY) {
        super(centerX, centerY, ACTOR_RADIUS, Color.GREEN);

        moveSpeed = (Math.random() * (ACTOR_MAX_MOVESPEED - ACTOR_MIN_MOVESPEED)) + ACTOR_MIN_MOVESPEED;

        setStroke(Color.BLACK);
        setStrokeWidth(2);

        face = new Line();
        face.setStroke(Color.web("#98FB98"));
        face.setStrokeWidth(3);
        face.setStrokeLineCap(StrokeLineCap.ROUND);
        canvas.getChildren().add(face);

        faceUnitVector = new Point2D(0, 0);
        orientFace();
    }

    public void act(int canvasWidth, int canvasHeight, List<Actor> actors) {
        Point2D unitVector = new Point2D(0, 0);

        if (isZombie()) {
            Actor nearestHuman = findNearestActorWithAttributes(actors, false);

            //TODO figure out actual root to NPE problem & remove quickfix
            if (nearestHuman != null && distanceTo(nearestHuman) < 50) {
                moveTowards(nearestHuman, 45, false);
            } else {
                moveRandomly();
            }
        } else {
            Actor nearestZombie = findNearestActorWithAttributes(actors, true);

            //TODO figure out actual root of NPE problem & remove quickfix
            if (nearestZombie != null && distanceTo(nearestZombie) < 40) {
                moveTowards(nearestZombie, 45, true);
            } else {
                moveRandomly();
            }
        }

        orientFace();
        forceActorWithinBounds(canvasWidth, canvasHeight);
    }

    private void moveTowards(Actor actor, double angleSpread, boolean reverse) {
        double dx = actor.getCenterX() - getCenterX();
        double dy = actor.getCenterY() - getCenterY();

        // move away from actor
        if (reverse) {
            dx *= -1;
            dy *= -1;
        }

        // establish linear trajectory towards or away from actor
        double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        dx = (dx / dist) * moveSpeed;
        dy = (dy / dist) * moveSpeed;

        double centerX = getCenterX();
        double centerY = getCenterY();
        double newCenterX = getCenterX() + dx;
        double newCenterY = getCenterY() + dy;

        // vary linear path by angle spread
        double rot = Math.toRadians(Math.random() * (angleSpread * 2) - angleSpread);
        newCenterX = centerX + (Math.cos(rot) * (newCenterX - centerX) + Math.sin(rot) * (newCenterY - centerY));
        newCenterY = centerY + (-1 * Math.sin(rot) * (newCenterX - centerX) + Math.cos(rot) * (newCenterY - centerY));

        setCenterX(newCenterX);
        setCenterY(newCenterY);

        double uX = (newCenterX - centerX) / moveSpeed;
        double uY = (newCenterY - centerY) / moveSpeed;
        Point2D unitVector = new Point2D(uX, uY);

        faceUnitVector = unitVector;
    }

    private void moveRandomly() {
        double dx = Math.random();
        double dy = Math.random();

        dx = (dx * moveSpeed * 2) - moveSpeed;
        dy = (dy * moveSpeed * 2) - moveSpeed;
        Point2D unitVector = new Point2D(dx / moveSpeed, dy / moveSpeed);

        setCenterX(getCenterX() + dx);
        setCenterY(getCenterY() + dy);

        faceUnitVector = unitVector;
    }

    private double distanceTo(Actor actor) {
        double x = getCenterX();
        double y = getCenterY();
        double aX = actor.getCenterX();
        double aY = actor.getCenterY();

        return Math.sqrt(Math.pow(aX - x, 2) + Math.pow(aY - y, 2));
    }

    private Actor findNearestActorWithAttributes(List<Actor> actors, boolean targetIsZombie) {
        Actor closestActor = null;
        double closestActorDist = Double.MAX_VALUE;

        for (Actor actor : actors) {
            if (actor != this && actor.isZombie() == targetIsZombie) {
                double dist = distanceTo(actor);

                if (dist < closestActorDist) {
                    closestActor = actor;
                    closestActorDist = dist;
                }
            }
        }

        return closestActor;
    }

    private void forceActorWithinBounds(int canvasWidth, int canvasHeight) {
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

    private void orientFace() {
        double cX = getCenterX();
        double cY = getCenterY();

        double theta = Math.atan2(faceUnitVector.getY(), faceUnitVector.getX());

        face.setStartX(cX);
        face.setStartY(cY);
        face.setEndX(cX + Math.cos(theta) * ACTOR_RADIUS);
        face.setEndY(cY + Math.sin(theta) * ACTOR_RADIUS);

        face.toFront();
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
