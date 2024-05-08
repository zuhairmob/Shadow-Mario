import bagel.*;

import java.util.Arrays;
import java.util.Properties;

/**
 * Skeleton Code for SWEN20003 Project 1, Semester 1, 2024
 *
 * Please enter your name below
 * @author Zuhair Mobasshar
 */
public class ShadowMario extends AbstractGame {

    // Declare variable which are used in multiple methods at the beginning of the class
    private final Image BACKGROUND_IMAGE;
    private final Font TITLE_FONT;
    private final String TITLE_TEXT;
    private final Font INSTR_FONT;
    private final String[] INSTR_TEXT;
    private final Font SCORE_FONT;
    private final Font HEALTH_FONT;
    private final Font MESSAGE_FONT;
    private final Image PLATFORM_IMAGE;
    private int platformBuffer = 0;
    private final Image PLAYER_IMAGE_LEFT;
    private final Image PLAYER_IMAGE_RIGHT;
    private int playerBuffer = 0;
    private final Image FLAG_IMAGE;
    private int flagBuffer = 0;
    private final Image COIN_IMAGE;
    private int coinBuffer = 0;
    private final Image ENEMY_IMAGE;
    private int enemyBuffer = 0;
    private static Properties gameProps;
    private static Properties messageProps;
    private Boolean startedGame = false;
    private final String[][] LEVEL;
    private Boolean pressedRight = false;
    private Boolean pressedLeft = false;
    private Boolean pressedUp = false;
    private char lastMove = 'r';
    private int speed = 0;
    private double score = 0;
    private double health;
    private final double COIN_VALUE;
    private final double ENEMY_DAMAGE;
    private final double COIN_RANGE;
    private final double ENEMY_RANGE;
    private final double FLAG_RANGE;
    private final int[] COIN_BUFFER_Y = new int[50];
    private final double[] COIN_DISTANCES = new double[50];
    private final int[] HIT_COIN_IDS = new int[50];
    private final double[] ENEMY_DISTANCES = new double[50];
    private final int[] HIT_ENEMY_IDS = new int[50];
    private Boolean win = false;

    /**
     * The constructor
     */
    public ShadowMario(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("windowWidth")),
              Integer.parseInt(gameProps.getProperty("windowHeight")),
              messageProps.getProperty("title"));


        // you can initialise other values from the property files here

        BACKGROUND_IMAGE = new Image(gameProps.getProperty("backgroundImage"));
        TITLE_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("title.fontSize")));
        TITLE_TEXT = messageProps.getProperty("title");
        INSTR_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("instruction.fontSize")));
        INSTR_TEXT =  messageProps.getProperty("instruction").split("\n");
        SCORE_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("score.fontSize")));
        HEALTH_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("health.fontSize")));
        MESSAGE_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("message.fontSize")));

        // Initialise images
        PLATFORM_IMAGE = new Image(gameProps.getProperty("gameObjects.platform.image"));
        PLAYER_IMAGE_LEFT = new Image(gameProps.getProperty("gameObjects.player.imageLeft"));
        PLAYER_IMAGE_RIGHT = new Image(gameProps.getProperty("gameObjects.player.imageRight"));
        FLAG_IMAGE = new Image(gameProps.getProperty("gameObjects.endFlag.image"));
        COIN_IMAGE = new Image(gameProps.getProperty("gameObjects.coin.image"));
        ENEMY_IMAGE = new Image(gameProps.getProperty("gameObjects.enemy.image"));

        COIN_VALUE = Double.parseDouble(gameProps.getProperty("gameObjects.coin.value"));
        ENEMY_DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.enemy.damageSize"));
        COIN_RANGE = (Double.parseDouble(gameProps.getProperty("gameObjects.coin.radius"))
                + Double.parseDouble(gameProps.getProperty("gameObjects.player.radius")));
        ENEMY_RANGE = (Double.parseDouble(gameProps.getProperty("gameObjects.enemy.radius"))
                + Double.parseDouble(gameProps.getProperty("gameObjects.player.radius")));
        FLAG_RANGE = (Double.parseDouble(gameProps.getProperty("gameObjects.endFlag.radius"))
                + Double.parseDouble(gameProps.getProperty("gameObjects.player.radius")));

        health = Double.parseDouble(gameProps.getProperty("gameObjects.player.health"));
        // Initialize a 2D array for the elements of the level
        LEVEL = IOUtils.readCsv(gameProps.getProperty("levelFile"));

    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        gameProps = IOUtils.readPropertiesFile("res/app.properties");
        messageProps = IOUtils.readPropertiesFile("res/message_en.properties");

        ShadowMario game = new ShadowMario(gameProps, messageProps);
        game.run();
    }

    /**
     * Performs a state update.
     * Allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {
        // close window
        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        if (input.wasPressed(Keys.SPACE)) {
            startedGame = true;
        }



        // Display title and instructions if the user has not pressed SPACE yet
        if (!startedGame) {
            BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);
            TITLE_FONT.drawString(TITLE_TEXT, Integer.parseInt(gameProps.getProperty("title.x")), Integer.parseInt(gameProps.getProperty("title.x")));
            INSTR_FONT.drawString(INSTR_TEXT[0], (Window.getWidth() / 2.0 - INSTR_FONT.getWidth((INSTR_TEXT[0])) / 2.0), Integer.parseInt(gameProps.getProperty("instruction.y")));
            INSTR_FONT.drawString(INSTR_TEXT[1], (Window.getWidth() / 2.0 - INSTR_FONT.getWidth((INSTR_TEXT[1])) / 2.0), Integer.parseInt(gameProps.getProperty("instruction.y")) + 40);
        }
        else if (((Integer.parseInt(LEVEL[1][2]) + playerBuffer) <= (Window.getHeight() + 64)) && (!win)){ // Only display background image and platform once the user has pressed SPACE
            BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
            SCORE_FONT.drawString((messageProps.getProperty("score") + (int)score), Integer.parseInt(gameProps.getProperty("score.x")), Integer.parseInt(gameProps.getProperty("score.y")));
            HEALTH_FONT.drawString((messageProps.getProperty("health") + (int)(health*100)), Integer.parseInt(gameProps.getProperty("health.x")), Integer.parseInt(gameProps.getProperty("health.y")));

            for(int i = 0; i < LEVEL.length; i++){
                switch (LEVEL[i][0]) {
                    case "PLATFORM":
                        PLATFORM_IMAGE.draw(Integer.parseInt(LEVEL[i][1]) + platformBuffer, Integer.parseInt(LEVEL[i][2]));
                        break;
                    case "PLAYER":
                        if (lastMove == 'r') {
                            PLAYER_IMAGE_RIGHT.draw(Integer.parseInt(LEVEL[i][1]), Integer.parseInt(LEVEL[i][2]) + playerBuffer);
                        } else {
                            PLAYER_IMAGE_LEFT.draw(Integer.parseInt(LEVEL[i][1]), Integer.parseInt(LEVEL[i][2]) + playerBuffer);
                        }
                        break;
                    case "COIN":
                        if (HIT_COIN_IDS[i] == 1) {
                            COIN_IMAGE.draw(Integer.parseInt(LEVEL[i][1]) + coinBuffer, Integer.parseInt(LEVEL[i][2]) + COIN_BUFFER_Y[i]);
                        } else if (HIT_COIN_IDS[i] == 0) {
                            COIN_IMAGE.draw(Integer.parseInt(LEVEL[i][1]) + coinBuffer, Integer.parseInt(LEVEL[i][2]));
                        }
                        break;
                    case "ENEMY":
                        ENEMY_IMAGE.draw(Integer.parseInt(LEVEL[i][1]) + enemyBuffer, Integer.parseInt(LEVEL[i][2]));
                        break;
                    case "END_FLAG":
                        FLAG_IMAGE.draw(Integer.parseInt(LEVEL[i][1]) + flagBuffer, Integer.parseInt(LEVEL[i][2]));
                        break;
                }
            }




            // Right movement logic
            if (input.wasPressed(Keys.RIGHT)) {
                pressedRight = true;
                lastMove = 'r';
            }
            if (pressedRight) {
                coinBuffer -= Integer.parseInt(gameProps.getProperty("gameObjects.coin.speed"));
                enemyBuffer -= Integer.parseInt(gameProps.getProperty("gameObjects.enemy.speed"));
                platformBuffer -= Integer.parseInt(gameProps.getProperty("gameObjects.platform.speed"));
                flagBuffer -= Integer.parseInt(gameProps.getProperty("gameObjects.endFlag.speed"));
            }
            if (input.wasReleased(Keys.RIGHT)) {
                pressedRight = false;
            }


            // Left movement logic
            if (input.wasPressed(Keys.LEFT)) {
                pressedLeft = true;
                lastMove = 'l';
            }
            if (pressedLeft && (Integer.parseInt(LEVEL[0][1]) + platformBuffer < 3000)) {
                coinBuffer += Integer.parseInt(gameProps.getProperty("gameObjects.coin.speed"));
                enemyBuffer += Integer.parseInt(gameProps.getProperty("gameObjects.enemy.speed"));
                platformBuffer += Integer.parseInt(gameProps.getProperty("gameObjects.platform.speed"));
                flagBuffer += Integer.parseInt(gameProps.getProperty("gameObjects.endFlag.speed"));
            }
            if (input.wasReleased(Keys.LEFT)) {
                pressedLeft = false;
            }


            // Up movement logic
            if (input.wasPressed(Keys.UP)) {
                pressedUp = true;
            }
            if (pressedUp && (Integer.parseInt(LEVEL[1][2]) + playerBuffer) == (Integer.parseInt(LEVEL[1][2])) ) {
                playerBuffer -= 20;
                speed = -20;
            }
            if (input.wasReleased(Keys.UP)) {
                pressedUp = false;
            }
            if ((Integer.parseInt(LEVEL[1][2]) + playerBuffer) < (Integer.parseInt(LEVEL[1][2]))){
                speed++;
                playerBuffer += speed;
            }




            // Coin collision detection and scoring
            calculateCoinDistances();  // Calculate distances to all coins in the level

            // Set the ID of a coin to 1 if the distance to it is less than the coin range
            for (int i = 0; i < 50; i++){
                if(COIN_DISTANCES[i] <= COIN_RANGE && LEVEL[i][0].equals("COIN")){
                    HIT_COIN_IDS[i] = 1;
                }
            }

            // Add 10 to the COIN_BUFFER_Y of each coin that is hit, so it is 10 pixels lower in each frame
            for (int i = 0; i < 50; i++){
                if (HIT_COIN_IDS[i] == 1){
                    COIN_BUFFER_Y[i] += 10;
                }
            }

            // Re-calculate score by counting number of hit coins in the array
            score = 0;
            for(int i: HIT_COIN_IDS){
                if (i == 1){
                    score += COIN_VALUE;
                }
            }




            // Enemy collision detection and health
            calculateEnemyDistances(); // Calculate distances to all coins in the level

            // Set the ID of an enemy in the array to 1 if the distance to it is less than its range
            for (int i = 0; i < 50; i++){
                if (ENEMY_DISTANCES[i] <= ENEMY_RANGE && LEVEL[i][0].equals("ENEMY")){
                    HIT_ENEMY_IDS[i] = 1;
                }
            }

            // Re-calculate score by counting number of hit enemies in the array
            health = Double.parseDouble(gameProps.getProperty("gameObjects.player.health"));
            for (int i: HIT_ENEMY_IDS){
                if (i == 1){
                    health -= ENEMY_DAMAGE;
                }
            }

            // Make player fall off the screen if health is <= 0
            if (health <= 0){
                playerBuffer += 2;
            }



            // Set Boolean win to true if the flag is within range
            double flagDistance = calculateFlagDistance();
            if (flagDistance <= FLAG_RANGE){
                win = true;
            }

        }
        else {
            BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

            if (win){
                String[] winMessage = messageProps.getProperty("gameWon").split("\n");
                MESSAGE_FONT.drawString(winMessage[0], (Window.getWidth() / 2.0 - MESSAGE_FONT.getWidth((winMessage[0])) / 2.0), Integer.parseInt(gameProps.getProperty("message.y")));
                MESSAGE_FONT.drawString(winMessage[1], (Window.getWidth() / 2.0 - MESSAGE_FONT.getWidth((winMessage[1])) / 2.0), Integer.parseInt(gameProps.getProperty("message.y")) + 40);
            }
            else {
                String[] loseMessage = messageProps.getProperty("gameOver").split("\n");
                MESSAGE_FONT.drawString(loseMessage[0], (Window.getWidth() / 2.0 - MESSAGE_FONT.getWidth((loseMessage[0])) / 2.0), Integer.parseInt(gameProps.getProperty("message.y")));
                MESSAGE_FONT.drawString(loseMessage[1], (Window.getWidth() / 2.0 - MESSAGE_FONT.getWidth((loseMessage[1])) / 2.0), Integer.parseInt(gameProps.getProperty("message.y")) + 40);
            }

            if (input.wasPressed(Keys.SPACE)){
                // Reset all variables to starting conditions
                startedGame = false;
                health = Double.parseDouble(gameProps.getProperty("gameObjects.player.health"));
                score = 0;
                coinBuffer = 0;
                enemyBuffer = 0;
                playerBuffer = 0;
                platformBuffer = 0;
                flagBuffer = 0;
                Arrays.fill(HIT_COIN_IDS, 0);
                Arrays.fill(HIT_ENEMY_IDS, 0);
                Arrays.fill(COIN_BUFFER_Y, 0);
                pressedLeft = false;
                pressedRight = false;
                pressedUp = false;
                win = false;
            }
        }


    }

    /**
     * Calculate distances to every coin in the level
     * and store them in the COIN_DISTANCES array
     */
    public void calculateCoinDistances(){
        for (int i = 0; i < LEVEL.length; i++) {
            String[] entities = LEVEL[i];
            if (entities[0].equals("COIN")) {
                double x1 = Double.parseDouble(entities[1]) + coinBuffer;
                double y1 = Double.parseDouble(entities[2]) + COIN_BUFFER_Y[i];
                double x2 = Double.parseDouble(LEVEL[1][1]);
                double y2 = Double.parseDouble(LEVEL[1][2]) + playerBuffer;

                // Calculate x and y distances
                double xDist = Math.abs(x2 - x1);
                double yDist = Math.abs(y2 - y1);

                // Calculate Euclidean distance using the Math.hypot method
                COIN_DISTANCES[i] = Math.hypot(xDist, yDist);
            }
        }
    }

    /**
     * Calculate distances to every enemy in the level
     * and store them in the ENEMY_DISTANCES array
     */
    public void calculateEnemyDistances(){
        for (int i = 0; i < LEVEL.length; i++) {
            String[] entities = LEVEL[i];
            if (entities[0].equals("ENEMY")) {
                double x1 = Double.parseDouble(entities[1]) + enemyBuffer;
                double y1 = Double.parseDouble(entities[2]);
                double x2 = Double.parseDouble(LEVEL[1][1]);
                double y2 = Double.parseDouble(LEVEL[1][2]) + playerBuffer;

                // Calculate x and y distances
                double xDist = Math.abs(x2 - x1);
                double yDist = Math.abs(y2 - y1);

                // Calculate Euclidean distance using the Math.hypot method
                ENEMY_DISTANCES[i] = Math.hypot(xDist, yDist);
            }
        }
    }

    /**
     * Calculate the distance to the flag
     * and return it as an integer
     * @return integer
     */
    public double calculateFlagDistance(){
        for (String[] strings : LEVEL) {
            if (strings[0].equals("END_FLAG")) {
                double x1 = Double.parseDouble(strings[1]) + flagBuffer;
                double y1 = Double.parseDouble(strings[2]);
                double x2 = Double.parseDouble(LEVEL[1][1]);
                double y2 = Double.parseDouble(LEVEL[1][2]) + playerBuffer;

                // Calculate x and y distances
                double xDist = Math.abs(x2 - x1);
                double yDist = Math.abs(y2 - y1);

                // Calculate Euclidean distance using the Math.hypot method
                return Math.hypot(xDist, yDist);
            }
        }
        return FLAG_RANGE + 1;
    }
}
