import bagel.Image;
import bagel.Input;
import bagel.Keys;

import java.util.Properties;

/**
 * Class for the fireball.
 */
public class Fireball {
    private final int Y;
    private final double RADIUS;
    private final int SPEED_X = 5;
    private final int SHOOT_SPEED;
    private final double DAMAGE_SIZE;
    private Image image;
    private final int MAX_COORDINATE;
    private int x;
    private boolean killedTarget = false;
    private boolean hitPlayer = false;
    private boolean hitEnemyBoss = false;
    private final char direction;
    private final char RIGHT = 'r';
    private final char LEFT = 'l';

    public Fireball(int x, int y, Properties props, char direction) {
        this.x = x;
        this.Y = y;
        this.MAX_COORDINATE = x;
        this.RADIUS = Double.parseDouble(props.getProperty("gameObjects.fireball.radius"));
        this.DAMAGE_SIZE = Double.parseDouble(props.getProperty("gameObjects.fireball.damageSize"));
        this.SHOOT_SPEED = Integer.parseInt(props.getProperty("gameObjects.fireball.speed"));
        this.image = new Image(props.getProperty("gameObjects.fireball.image"));
        this.direction = direction;
    }

    /***
     * Method that updates the fireball movement and draws it. Also checks for collisions with the player.
     */
    public void updateWithTarget(Input input, Player player, EnemyBoss enemyBoss) {
        move(input);
        if (!hitPlayer && !hitEnemyBoss) image.draw(x, Y);

        if (this.direction == LEFT) {
            if (player != null && CollisionDetector.isCollided(player, this.x, this.Y, this.RADIUS) && !hitPlayer) {
                hitPlayer = true;
                if (!InvinciblePower.isActive()) {
                    damagePlayer(player);
                }
            }
        } else if (this.direction == RIGHT) {
            if (enemyBoss != null && CollisionDetector.isCollided(enemyBoss, this.x, this.Y, this.RADIUS) && !hitEnemyBoss) {
                hitEnemyBoss = true;
                damageEnemyBoss(enemyBoss);
            }
        }
    }

    /***
     * Method that moves the fireball based on the player's movement.
     */
    private void move(Input input) {
        if (direction == LEFT){
            this.x -= SHOOT_SPEED;
        } else if (direction == RIGHT) {
            this.x += SHOOT_SPEED;
        }

        if (input.isDown(Keys.RIGHT)){
            this.x -= SPEED_X;
        } else if (input.isDown(Keys.LEFT)){
            if (this.x < MAX_COORDINATE){
                this.x += SPEED_X;
            }
        }
    }

    /***
     * Method that damages the player. If the health of the player is less than or equal to 0,
     * the player will be marked as dead.
     */
    private void damagePlayer(Player player) {
        double newHealth = player.getHealth() - DAMAGE_SIZE;
        player.setHealth(newHealth);

        if (newHealth <= 0 && !killedTarget) {
            player.dead();
            player.setHealth(0.0);
            killedTarget = true;
        }
    }

    /***
     * Method that damages the enemy boss. If the health of the enemy boss is less than or equal to 0,
     * the enemy boss will be marked as dead.
     */
    private void damageEnemyBoss(EnemyBoss enemyBoss) {
        double newHealth = enemyBoss.getHealth() - DAMAGE_SIZE;
        enemyBoss.setHealth(newHealth);

        if (newHealth <= 0 && !killedTarget) {
            enemyBoss.dead();
            enemyBoss.setHealth(0.0);
            killedTarget = true;
        }
    }
}
