import bagel.Image;
import bagel.Input;
import bagel.Keys;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

/**
 * Class for the enemy boss.
 */
public class EnemyBoss {
    private int y;
    private final double RADIUS;
    private final int SPEED_X;
    private Image image;
    private final int MAX_COORDINATE;
    private int x;
    private double health;
    private final Random RANDOM;
    private int frameCount = 0;
    private final int ACTIVATION_RADIUS;
    private int speedY = 0;
    private final int FALL_SPEED = 2;
    private final char RIGHT = 'r';
    private final char LEFT = 'l';

    /***
     * Constructor for the EnemyBoss object.
     * @param x X position of the enemy boss.
     * @param y Y position of the enemy boss.
     * @param props Properties object to be used to obtain enemy boss properties
     */
    public EnemyBoss(int x, int y, Properties props) {
        this.x = x;
        this.y = y;
        this.MAX_COORDINATE = x;
        this.RADIUS = Double.parseDouble(props.getProperty("gameObjects.enemyBoss.radius"));
        this.SPEED_X = Integer.parseInt(props.getProperty("gameObjects.enemyBoss.speed"));
        this.image = new Image(props.getProperty("gameObjects.enemyBoss.image"));
        this.health = Double.parseDouble(props.getProperty("gameObjects.enemyBoss.health"));
        this.ACTIVATION_RADIUS = Integer.parseInt(props.getProperty("gameObjects.enemyBoss.activationRadius"));
        this.RANDOM = new Random();
    }

    /***
     * Method that updates the enemy boss movement and draws it. Also fires fireballs randomly when the player is within
     * the activation radius.
     */
    public void updateWithTarget(Input input, Player target, ArrayList<Fireball> fireballs) {
        frameCount++;
        move(input);
        image.draw(x, y);

        if (target != null && Math.abs(target.getX() - this.x) <= ACTIVATION_RADIUS && health > 0) {
            if (RANDOM.nextBoolean() && frameCount % 100 == 0) {
                frameCount = 0;
                fire(fireballs);
            }
        }
    }

    /***
     * Method that moves the enemy boss based on the player's movement.
     */
    private void move(Input input) {
        if (input.isDown(Keys.RIGHT)){
            this.x -= SPEED_X;
        } else if (input.isDown(Keys.LEFT)){
            if (this.x < MAX_COORDINATE){
                this.x += SPEED_X;
            }
        }

        this.y += speedY;
    }

    /***
     * Method that shoots a new fireball.
     */
    private void fire(ArrayList<Fireball> fireballs) {
        Fireball fireball = new Fireball(this.x, this.y, ShadowMario.getPROPS(), LEFT);
        fireballs.add(fireball);
    }

    /***
     * Getter for the double health attribute.
     * @return double health representing the enemy boss's health out of 1
     */
    public double getHealth() {
        return health;
    }

    /***
     * Setter for the double health attribute.
     * @param health double health representing the enemy boss's health out of 1
     */
    public void setHealth(double health) {
        this.health = health;
    }

    /***
     * Getter for the x position attribute.
     * @return int x representing the enemy boss's x position
     */
    public int getX() {
        return this.x;
    }

    /***
     * Getter for the y position attribute.
     * @return int y representing the enemy boss's y position
     */
    public int getY() {
        return this.y;
    }

    /***
     * Getter for the radius attribute.
     * @return double radius representing the collision radius of the enemy boss
     */
    public double getRADIUS() {
        return this.RADIUS;
    }

    /**
     * Method that sets the fall speed if the enemy boss's health has reached 0.
     */
    public void dead() {
        this.speedY = FALL_SPEED;
    }
}
