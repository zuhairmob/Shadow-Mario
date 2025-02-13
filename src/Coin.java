import bagel.Image;
import bagel.Input;
import bagel.Keys;
import java.util.Properties;

/**
 * Class for the coin entity.
 */
public class Coin {
    private final double RADIUS;
    private final int SPEED_X;
    private final int VALUE;
    private final int COLLISION_SPEED = -10;
    private final int MAX_COORDINATE;
    private int x;
    private int y;
    private int speedY = 0;
    private Image image;
    private boolean isCollided = false;

    /***
     * Constructor for the coin object.
     * @param x X position of the coin.
     * @param y Y position of the coin.
     * @param props Properties object to be used to obtain coin properties.
     */
    public Coin(int x, int y, Properties props) {
        this.x = x;
        this.y = y;
        this.MAX_COORDINATE = x;
        this.RADIUS = Double.parseDouble(props.getProperty("gameObjects.coin.radius"));
        this.VALUE = Integer.parseInt(props.getProperty("gameObjects.coin.value"));
        this.SPEED_X = Integer.parseInt(props.getProperty("gameObjects.coin.speed"));
        this.image = new Image(props.getProperty("gameObjects.coin.image"));
    }

    /***
     * Method that updates the coin movement and draws it. Also checks for collisions with the player.
     */
    public int updateWithTarget(Input input, Player target) {
        move(input);
        image.draw(x, y);

        if (CollisionDetector.isCollided(target, this.x, this.y, this.RADIUS) && !isCollided) {
            isCollided = true;
            speedY = COLLISION_SPEED;
            if (DoubleScorePower.isActive()){
                return VALUE * 2;
            }
            else {
                return VALUE;
            }
        }

        return 0;
    }

    /***
     * Method that moves the coin based on the player's movement.
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
}