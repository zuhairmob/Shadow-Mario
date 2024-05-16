import bagel.Image;
import bagel.Input;
import bagel.Keys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * Class for the player.
 */
public class Player {
    private final int X;
    private final int INITIAL_Y;
    private final double RADIUS;
    private final Properties PROPS;
    private final int INITIAL_JUMP_SPEED = -20;
    private final int FALL_SPEED = 2;
    private int y;
    private int speedY = 0;
    private double health;
    private Image image;
    private Boolean isOnFlyingPlatform = false;
    private int previousStationaryY;
    private boolean fallingFromPlatform = false;
    private final char RIGHT = 'r';
    private final char LEFT = 'l';

    /***
     * Constructor for the player object.
     * @param x X position of the player
     * @param y Y position of the player
     * @param props Properties object to be used to obtain player properties
     */
    public Player(int x, int y, Properties props) {
        this.X = x;
        this.y = y;
        this.previousStationaryY = y;
        this.INITIAL_Y = y;
        this.PROPS = props;
        this.RADIUS = Double.parseDouble(props.getProperty("gameObjects.player.radius"));
        this.image = new Image(props.getProperty("gameObjects.player.imageRight"));
        this.health = Double.parseDouble(props.getProperty("gameObjects.player.health"));
    }

    /**
     * Method that updates the image rendered based on user input and draws it.
     */
    public void update(Input input, ArrayList<Fireball> fireballs, EnemyBoss enemyBoss) {
        if (input.wasPressed(Keys.LEFT)) {
            image = new Image(this.PROPS.getProperty("gameObjects.player.imageLeft"));
        }
        if (input.wasPressed(Keys.RIGHT)) {
            image = new Image(this.PROPS.getProperty("gameObjects.player.imageRight"));
        }
        if (enemyBoss != null) {
            if (input.wasPressed(Keys.S) && Math.abs(this.X - enemyBoss.getX()) <= 500) {
                fire(fireballs);
            }
        }
        jump(input);
        image.draw(X, y);
    }

    /**
     * Method that handles the player's jumping movement.
     */
    public void jump(Input input) {

        // on a platform and up arrow key is pressed
        if (input.wasPressed(Keys.UP) && (y == INITIAL_Y || this.isOnFlyingPlatform)) {
            speedY = INITIAL_JUMP_SPEED;
            this.isOnFlyingPlatform = false;
        }

        // mid jump
        if (y < INITIAL_Y) {
            speedY += 1;
        }

        // finishing jump
        if (speedY > 0 && y >= INITIAL_Y && !isDead()) {
            speedY = 0;
            y = INITIAL_Y;
        }

        if (this.isOnFlyingPlatform) {
            speedY = 0;
        } else if (speedY > 0) {
            this.isOnFlyingPlatform = false;
        }

        this.y += speedY;

        // set previous stationary Y position
        if (isOnFlyingPlatform || y == INITIAL_Y) {
            previousStationaryY = this.y;
        }

        // set falling from platform boolean
        fallingFromPlatform = previousStationaryY != INITIAL_Y && speedY != 0 && y > previousStationaryY;
    }

    /**
     * Method that shoots a new fireball.
     */
    private void fire(ArrayList<Fireball> fireballs) {
        Fireball fireball = new Fireball(this.X, this.y, ShadowMario.getPROPS(), RIGHT);
        fireballs.add(fireball);
    }

    /**
     * Method that sets the fall speed if the player's health has reached 0.
     */
    public void dead() {
        this.speedY = FALL_SPEED;
    }

    /**
     * Getter for the player's x position.
     * @return int representing the player's x position
     */
    public int getX() {
        return X;
    }

    /**
     * Setter for the player's y position.
     * @return int representing the player's y position
     */
    public int getY() {
        return y;
    }

    /**
     * Getter for the player's radius.
     * @return double representing the player's collision radius
     */
    public double getRADIUS() {
        return RADIUS;
    }

    /**
     * Getter for the player's health.
     * @return double representing the player's health out of 1
     */
    public double getHealth() {
        return health;
    }

    /**
     * Getter for the fallingFromPlatform attribute.
     * @return Boolean which is true if the player is falling down from a higher platform
     */
    public boolean getisFallingFromPlatform() {
        return fallingFromPlatform;
    }

    /**
     * Setter for the player's health.
     * @param health double representing the player's health out of 1
     */
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * Method that returns true if the player is dead and false otherwise.
     * @return boolean which is true if the player's health is less than or equal to zero and false otherwise
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Method that sets the indicator of if the player is on a flying platform.
     */
    public void setIsOnFlyingPlatform(Boolean isOnFlyingPlatform) {
        this.isOnFlyingPlatform = isOnFlyingPlatform;
    };
}