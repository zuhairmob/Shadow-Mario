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
    private int Y;
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

    public EnemyBoss(int x, int y, Properties props) {
        this.x = x;
        this.Y = y;
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
        image.draw(x, Y);

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

        this.Y += speedY;
    }

    /***
     * Method that shoots a new fireball.
     */
    private void fire(ArrayList<Fireball> fireballs) {
        Fireball fireball = new Fireball(this.x, this.Y, ShadowMario.getPROPS(), LEFT);
        fireballs.add(fireball);
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.Y;
    }

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
