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

    private boolean isPlayerControlled = false;

    //TODO make zombie and human perception based on ACTOR_RADIUS

    private double moveSpeed;
    private boolean isZombie = false;

    private Line face;
    private double heading;

    private Point2D pointTarget;

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

        setHeading(Math.random() * (2 * Math.PI));
        orientFace();
    }

    public void act(List<Actor> actors) {
        if (isPlayerControlled) {
            moveTowardsPointTarget();
        } else {
            if (isZombie()) {
                Actor nearestHuman = findNearestActorWithAttributes(actors, false);

                if (nearestHuman != null && distanceTo(nearestHuman) < 50) {
                    moveTowards(nearestHuman, Math.toRadians(45), false);
                } else {
                    moveRandomly();
                }
            } else {
                Actor nearestZombie = findNearestActorWithAttributes(actors, true);

                if (nearestZombie != null && distanceTo(nearestZombie) < 40) {
                    moveTowards(nearestZombie, Math.toRadians(45), true);
                } else {
                    moveRandomly();
                }
            }
        }

        forceActorWithinBounds(ZombieSim.CANVAS_WIDTH, ZombieSim.CANVAS_HEIGHT);
        orientFace();
    }

    private void moveTowards(Actor target, double angleSpread, boolean reverse) {
        moveTowards(target.getCenterX(), target.getCenterY(), angleSpread, reverse);
    }

    private void moveTowards(Point2D target, double angleSpread, boolean reverse) {
        moveTowards(target.getX(), target.getY(), angleSpread, reverse);
    }

    private void moveTowards(double x, double y, double angleSpread, boolean reverse) {
        double dx = x - getCenterX();
        double dy = y - getCenterY();

        if (reverse) { // move away from target
            dx *= -1;
            dy *= -1;
        }

        rotateTowardsVector(dx, dy);
        move(0);
    }

    private void moveRandomly() {
        if (pointTarget == null) {
            double searchRadius = Math.random() * (20 * ACTOR_RADIUS) + 10;
            double randomAngle = Math.random() * (2 * Math.PI);

            double x = getCenterX() + Math.cos(randomAngle) * searchRadius;
            double y = getCenterY() + Math.sin(randomAngle) * searchRadius;

            setPointTarget(x, y);
        }

        moveTowardsPointTarget();
    }

    private void moveTowardsPointTarget() {
        if (pointTarget != null && hasReachedPointTarget()) {
            pointTarget = null;
        }

        if (pointTarget != null) {
            double dx = pointTarget.getX() - getCenterX();
            double dy = pointTarget.getY() - getCenterY();

            rotateTowardsVector(dx, dy);
            move(0);
        }
    }

    private void rotateTowardsVector(double dx, double dy) {
        double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

        // unit vectors
        Point2D headingVector = new Point2D(Math.cos(heading), Math.sin(heading));
        Point2D targetVector = new Point2D(dx / dist, dy / dist);

        double headingAngle = Math.atan2(headingVector.getY(), headingVector.getX());
        double targetAngle = Math.atan2(targetVector.getY(), targetVector.getX());

        double deltaAngle = (targetAngle - headingAngle);
        deltaAngle = normalizeAngleForRotation(deltaAngle);

        if (deltaAngle > 0) {
            setHeading(heading + Math.toRadians(15)); // CCW
        } else {
            setHeading(heading - Math.toRadians(15)); // CW
        }
    }

    private void move(double angleSpread) {
        double dx = Math.cos(heading) * moveSpeed;
        double dy = Math.sin(heading) * moveSpeed;

        double centerX = getCenterX();
        double centerY = getCenterY();
        double newCenterX = getCenterX() + dx;
        double newCenterY = getCenterY() + dy;

        //TODO remove angleSpread?
        // vary linear path by angle spread
        double rot = Math.random() * (angleSpread * 2) - angleSpread;
        newCenterX = centerX + (Math.cos(rot) * (newCenterX - centerX) + Math.sin(rot) * (newCenterY - centerY));
        newCenterY = centerY + (-1 * Math.sin(rot) * (newCenterX - centerX) + Math.cos(rot) * (newCenterY - centerY));

        setCenterX(newCenterX);
        setCenterY(newCenterY);

        orientFace();
    }

    private void setHeading(double theta) {
        while (theta <= 2 * Math.PI) theta += 2 * Math.PI;
        while (theta > 2 * Math.PI) theta -= 2 * Math.PI;

        heading = theta;
        this.heading = heading;
    }

    private void orientFace() {
        double cX = getCenterX();
        double cY = getCenterY();

        face.setStartX(cX);
        face.setStartY(cY);
        face.setEndX(cX + Math.cos(heading) * ACTOR_RADIUS);
        face.setEndY(cY + Math.sin(heading) * ACTOR_RADIUS);

        face.toFront();
    }

    private double distanceTo(Actor actor) {
        return distanceTo(actor.getCenterX(), actor.getCenterY());
    }

    private double distanceTo(Point2D point) {
        return distanceTo(point.getX(), point.getY());
    }

    private double distanceTo(double x, double y) {
        double cX = getCenterX();
        double cY = getCenterY();

        return Math.sqrt(Math.pow(x - cX, 2) + Math.pow(y - cY, 2));
    }

    private double normalizeAngleForRotation(double angle) {
        double twopi = 2 * Math.PI;

        // fit to target range (-PI < angle <= PI)
        angle =  angle % (2 * Math.PI);
        angle = (angle + (2 * Math.PI)) % (2 * Math.PI);
        if (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }

        return angle;
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

    private boolean hasReachedPointTarget() {
        return distanceTo(pointTarget) <= ACTOR_RADIUS * 2;
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

    public void setPlayerControlled(boolean isPlayerControlled) {
        this.isPlayerControlled = isPlayerControlled;
    }

    public boolean isPlayerControlled() {
        return isPlayerControlled;
    }

    public void setPointTarget(double x, double y) {
        if (x < 0) x = 0;
        if (x > ZombieSim.CANVAS_WIDTH) x = ZombieSim.CANVAS_WIDTH;
        if (y < 0) y = 0;
        if (y > ZombieSim.CANVAS_HEIGHT) y = ZombieSim.CANVAS_HEIGHT;

        pointTarget = new Point2D(x, y);
    }

    public boolean collidesWith(Actor a) {
        return distanceTo(a) <= ACTOR_RADIUS * 2;
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
