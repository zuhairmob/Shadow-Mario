import bagel.Image;
import bagel.Input;
import bagel.Keys;

import java.util.Properties;

/***
 * Class for the flying platform.
 */
public class FlyingPlatform {
    private final int Y;
    private final int SPEED_X;
    private int x;
    private Image image;
    private final int RANDOM_SPEED;
    private final int MAX_RANDOM_DISPLACEMENT;
    private final int HALF_LENGTH;
    private final int HALF_HEIGHT;
    private static Boolean playerLandedDuringThisFrame = false;
    private final int RAND_LEFT = 0;
    private final int RAND_RIGHT = 1;
    private int randMove = (int) Math.round(Math.random());
    int displacement = 0;

    public FlyingPlatform(int x, int y, Properties props){
        this.x = x;
        this.Y = y;
        this.SPEED_X = Integer.parseInt(props.getProperty("gameObjects.flyingPlatform.speed"));
        this.image = new Image(props.getProperty("gameObjects.flyingPlatform.image"));
        this.RANDOM_SPEED = Integer.parseInt(props.getProperty("gameObjects.flyingPlatform.randomSpeed"));
        this.MAX_RANDOM_DISPLACEMENT = Integer.parseInt(props.getProperty("gameObjects.flyingPlatform.maxRandomDisplacementX"));
        this.HALF_LENGTH = Integer.parseInt(props.getProperty("gameObjects.flyingPlatform.halfLength"));
        this.HALF_HEIGHT = Integer.parseInt(props.getProperty("gameObjects.flyingPlatform.halfHeight"));
    }

    /***
     * Method that updates the flying platform movement and draws it.
     * Also checks if the player is on it.
     */
    public void updateWithTarget(Input input, Player target) {
        move(input);
        randomMove(randMove);
        image.draw(x, Y);

        // Check if the player is falling
        boolean isFalling = target.getY() > target.getPrevY();

        if (!isFalling && (Math.abs(target.getX() - this.x) < this.HALF_LENGTH) &&
            (Math.abs(target.getY() - this.Y) <= this.HALF_HEIGHT) &&
            (Math.abs(target.getY() - this.Y) >= (this.HALF_HEIGHT - 1))){
            target.setIsOnFlyingPlatform(true);
            playerLandedDuringThisFrame = true;
        } else if (!playerLandedDuringThisFrame) {
            target.setIsOnFlyingPlatform(false);
        }
    }

    /***
     * Method that moves the flying platform based on the player's movement.
     */
    private void move(Input input) {
        if (input.isDown(Keys.RIGHT)){
            this.x -= SPEED_X;
        } else if (input.isDown(Keys.LEFT)){
            this.x += SPEED_X;
        }
    }

    private void randomMove(int randMove){
        if (this.randMove == this.RAND_RIGHT){
            this.x += RANDOM_SPEED;
            displacement += 1;
            if (Math.abs(displacement) >= MAX_RANDOM_DISPLACEMENT) {
                this.randMove = this.RAND_LEFT;
            }
        } else if (this.randMove == this.RAND_LEFT) {
            this.x -= 1;
            displacement -= RANDOM_SPEED;
            if (Math.abs(displacement) >= MAX_RANDOM_DISPLACEMENT) {
                this.randMove = this.RAND_RIGHT;
            }
        }
    }

    public static void setPlayerLandedDuringThisFrame(Boolean b) {
        playerLandedDuringThisFrame = b;
    }
}
