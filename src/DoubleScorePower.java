import bagel.Image;
import bagel.Input;
import bagel.Keys;
import java.util.Properties;

/**
 * Class for the double score power entity.
 */
public class DoubleScorePower {
    private final double RADIUS;
    private final int SPEED_X;
    private final int COLLISION_SPEED = -10;
    private final int MAX_COORDINATE;
    private int x;
    private int y;
    private int speedY = 0;
    private Image image;
    private boolean isCollided = false;
    private final int maxFrames;
    private static boolean isActive = false;
    private int activeFrames = 0;

    public DoubleScorePower(int x, int y, Properties props) {
        this.x = x;
        this.y = y;
        this.MAX_COORDINATE = x;
        this.RADIUS = Double.parseDouble(props.getProperty("gameObjects.doubleScore.radius"));
        this.SPEED_X = Integer.parseInt(props.getProperty("gameObjects.doubleScore.speed"));
        this.image = new Image(props.getProperty("gameObjects.doubleScore.image"));
        this.maxFrames = Integer.parseInt(props.getProperty("gameObjects.doubleScore.maxFrames"));
    }

    /***
     * Method that updates the double score power movement and draws it. Also checks for collisions with the player.
     */
    public void updateWithTarget(Input input, Player target) {
        move(input);
        image.draw(x, y);

        if (CollisionDetector.isCollided(target, this.x, this.y, this.RADIUS) && !isCollided) {
            isCollided = true;
            isActive = true;
            speedY = COLLISION_SPEED;
        }

        if (activeFrames > maxFrames) {
            isActive = false;
        }

        if (isActive) {
            activeFrames++;
        }
    }

    /***
     * Method that moves the double score power based on the player's movement.
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
     * Getter method for isActive variable.
     */
    public static boolean isActive() {
        return isActive;
    }
}
