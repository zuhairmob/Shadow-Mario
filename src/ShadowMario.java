import bagel.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * Extended from the sample solution for SWEN20003 Project 1, Semester 1, 2024
 * Authored by Dimuthu Kariyawasan & Tharun Dharmawickrema
 *
 * Please enter your name below
 * @author Zuhair Mobasshar
 */
public class ShadowMario extends AbstractGame {

    private final int WINDOW_HEIGHT;
    private final String GAME_TITLE;
    private final Image BACKGROUND_IMAGE;
    private final String FONT_FILE;
    private final Font TITLE_FONT;
    private final int TITLE_X;
    private final int TITLE_Y;
    private final String INSTRUCTION;
    private final Font INSTRUCTION_FONT;
    private final int INS_Y;
    private final Font MESSAGE_FONT;
    private final int MESSAGE_Y;
    private static Properties PROPS;
    private final Properties MESSAGE_PROPS;
    private final Font SCORE_FONT;
    private final int SCORE_X;
    private final int SCORE_Y;
    private final Font PLAYER_HEALTH_FONT;
    private final int PLAYER_HEALTH_X;
    private final int PLAYER_HEALTH_Y;
    private final Font ENEMY_BOSS_HEALTH_FONT;
    private final int ENEMY_BOSS_HEALTH_X;
    private final int ENEMY_BOSS_HEALTH_Y;
    private final DrawOptions ENEMY_BOSS_HEALTH_OPTIONS;
    private int score;
    private boolean finished = false;
    private Player player;
    private Platform platform;
    private Enemy[] enemies;
    private Coin[] coins;
    private FlyingPlatform[] flyingPlatforms;
    private DoubleScorePower[] doubleScorePowers;
    private InvinciblePower[] invinciblePowers;
    private EnemyBoss enemyBoss;
    private ArrayList<Fireball> fireballs = new ArrayList<>();
    private EndFlag endFlag;
    private boolean started = false;

    public ShadowMario(Properties game_props, Properties message_props) {
        super(Integer.parseInt(game_props.getProperty("windowWidth")),
              Integer.parseInt(game_props.getProperty("windowHeight")),
              message_props.getProperty("title"));

        WINDOW_HEIGHT = Integer.parseInt(game_props.getProperty("windowHeight"));
        GAME_TITLE = message_props.getProperty("title");
        BACKGROUND_IMAGE = new Image(game_props.getProperty("backgroundImage"));
        FONT_FILE = game_props.getProperty("font");

        TITLE_FONT = new Font(FONT_FILE, Integer.parseInt(game_props.getProperty("title.fontSize")));
        TITLE_X = Integer.parseInt(game_props.getProperty("title.x"));
        TITLE_Y = Integer.parseInt(game_props.getProperty("title.y"));

        SCORE_FONT = new Font(FONT_FILE, Integer.parseInt(game_props.getProperty("score.fontSize")));
        SCORE_X = Integer.parseInt(game_props.getProperty("score.x"));
        SCORE_Y = Integer.parseInt(game_props.getProperty("score.y"));

        INSTRUCTION = message_props.getProperty("instruction");
        INSTRUCTION_FONT = new Font(FONT_FILE, Integer.parseInt(game_props.getProperty("instruction.fontSize")));
        INS_Y = Integer.parseInt(game_props.getProperty("instruction.y"));

        MESSAGE_FONT = new Font(FONT_FILE, Integer.parseInt(game_props.getProperty("message.fontSize")));
        MESSAGE_Y = Integer.parseInt(game_props.getProperty("message.y"));

        PLAYER_HEALTH_FONT = new Font(FONT_FILE, Integer.parseInt(game_props.getProperty("playerHealth.fontSize")));
        PLAYER_HEALTH_X = Integer.parseInt(game_props.getProperty("playerHealth.x"));
        PLAYER_HEALTH_Y = Integer.parseInt(game_props.getProperty("playerHealth.y"));

        ENEMY_BOSS_HEALTH_FONT = new Font(FONT_FILE, Integer.parseInt(game_props.getProperty("enemyBossHealth.fontSize")));
        ENEMY_BOSS_HEALTH_X = Integer.parseInt(game_props.getProperty("enemyBossHealth.x"));
        ENEMY_BOSS_HEALTH_Y = Integer.parseInt(game_props.getProperty("enemyBossHealth.y"));
        ENEMY_BOSS_HEALTH_OPTIONS = new DrawOptions();
        ENEMY_BOSS_HEALTH_OPTIONS.setBlendColour(1.0, 0.0, 0.0);

        PROPS = game_props;
        this.MESSAGE_PROPS = message_props;
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        Properties game_props = IOUtils.readPropertiesFile("res/app.properties");
        Properties message_props = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowMario game = new ShadowMario(game_props, message_props);
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

        BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        if (!started) {
            TITLE_FONT.drawString(GAME_TITLE, TITLE_X, TITLE_Y);
            INSTRUCTION_FONT.drawString(INSTRUCTION,
                    (double) Window.getWidth() / 2 - INSTRUCTION_FONT.getWidth(INSTRUCTION)/2, INS_Y);

            if (input.wasPressed(Keys.NUM_1)) {
                started = true;
                finished = false;
                score = 0;

                String[][] lines = IOUtils.readCsv(PROPS.getProperty("level1File"));
                populateGameObjects(lines);
            } else if (input.wasPressed(Keys.NUM_2)) {
                started = true;
                finished = false;
                score = 0;

                String[][] lines = IOUtils.readCsv(PROPS.getProperty("level2File"));
                populateGameObjects(lines);
            } else if (input.wasPressed(Keys.NUM_3)) {
                started = true;
                finished = false;
                score = 0;

                String[][] lines = IOUtils.readCsv(PROPS.getProperty("level3File"));
                populateGameObjects(lines);
            }
        } else if (player.isDead() && player.getY() > WINDOW_HEIGHT) {
            String message = MESSAGE_PROPS.getProperty("gameOver");
            MESSAGE_FONT.drawString(message,
                    (double) Window.getWidth() / 2 - MESSAGE_FONT.getWidth(message)/2,
                    MESSAGE_Y);
            if (input.wasPressed(Keys.SPACE)) {
                started = false;
            }
        } else {
            if (finished) {
                String message = MESSAGE_PROPS.getProperty("gameWon");
                MESSAGE_FONT.drawString(message,
                        (double) Window.getWidth() / 2 - MESSAGE_FONT.getWidth(message)/2,
                        MESSAGE_Y);
                if(input.wasPressed(Keys.SPACE)) {
                    started = false;
                 }
            } else {
                // game is running
                SCORE_FONT.drawString(MESSAGE_PROPS.getProperty("score") + score, SCORE_X, SCORE_Y);
                PLAYER_HEALTH_FONT.drawString(MESSAGE_PROPS.getProperty("health") + Math.round(player.getHealth()*100),
                        PLAYER_HEALTH_X, PLAYER_HEALTH_Y);
                if (enemyBoss != null) {
                    ENEMY_BOSS_HEALTH_FONT.drawString(MESSAGE_PROPS.getProperty("health") + Math.round(enemyBoss.getHealth()*100),
                            ENEMY_BOSS_HEALTH_X, ENEMY_BOSS_HEALTH_Y, ENEMY_BOSS_HEALTH_OPTIONS);
                }
                updateGameObjects(input);
            }
        }
    }

    /**
     * Method that creates the game objects using the lines read from the CSV file.
     */
    private void populateGameObjects(String[][] lines) {
        int coinCount = (int) Arrays.stream(lines).filter(s -> "COIN".equals(s[0])).count();
        coins = new Coin[coinCount];
        int enemyCount = (int) Arrays.stream(lines).filter(s -> "ENEMY".equals(s[0])).count();
        enemies = new Enemy[enemyCount];
        int flyingPlatformCount = (int) Arrays.stream(lines).filter(s -> "FLYING_PLATFORM".equals(s[0])).count();
        flyingPlatforms = new FlyingPlatform[flyingPlatformCount];
        int doubleScorePowerCount = (int) Arrays.stream(lines).filter(s -> "DOUBLE_SCORE".equals(s[0])).count();
        doubleScorePowers = new DoubleScorePower[doubleScorePowerCount];
        int invinciblePowerCount = (int) Arrays.stream(lines).filter(s -> "INVINCIBLE_POWER".equals(s[0])).count();
        invinciblePowers = new InvinciblePower[invinciblePowerCount];

        int enemyIndex = 0;
        int coinIndex = 0;
        int flyingPlatformIndex = 0;
        int doubleScorePowerIndex = 0;
        int invinciblePowerIndex = 0;

        for(String[] lineElement: lines) {
            int x = Integer.parseInt(lineElement[1]);
            int y = Integer.parseInt(lineElement[2]);

            switch (lineElement[0]) {
                case "PLAYER":
                    player = new Player(x, y, PROPS);
                    break;
                case "PLATFORM":
                    platform = new Platform(x, y, PROPS);
                    break;
                case "ENEMY":
                    Enemy enemy = new Enemy(x, y, PROPS);
                    enemies[enemyIndex++] = enemy;
                    break;
                case "COIN":
                    Coin coin = new Coin(x, y, PROPS);
                    coins[coinIndex++] = coin;
                    break;
                case "FLYING_PLATFORM":
                    FlyingPlatform flyingPlatform = new FlyingPlatform(x, y, PROPS);
                    flyingPlatforms[flyingPlatformIndex++] = flyingPlatform;
                    break;
                case "DOUBLE_SCORE":
                    DoubleScorePower doubleScorePower = new DoubleScorePower(x, y, PROPS);
                    doubleScorePowers[doubleScorePowerIndex++] = doubleScorePower;
                    break;
                case "INVINCIBLE_POWER":
                    InvinciblePower invinciblePower = new InvinciblePower(x, y, PROPS);
                    invinciblePowers[invinciblePowerIndex++] = invinciblePower;
                    break;
                case "ENEMY_BOSS":
                    enemyBoss = new EnemyBoss(x, y, PROPS);
                    break;
                case "END_FLAG":
                    endFlag = new EndFlag(x, y, PROPS);
                    break;
            }
        }
    }

    /**
     * Method that updates the game objects each frame, when the game is running.
     */
    public void updateGameObjects(Input input) {

        platform.update(input);

        for(Enemy e: enemies) {
            e.updateWithTarget(input, player);
        }

        for(Coin c: coins) {
            score += c.updateWithTarget(input, player);
        }

        for(FlyingPlatform fp: flyingPlatforms) {
            fp.updateWithTarget(input, player);
        }

        for (DoubleScorePower dsp: doubleScorePowers) {
            dsp.updateWithTarget(input, player);
        }

        for (InvinciblePower ip: invinciblePowers) {
            ip.updateWithTarget(input, player);
        }

        for (Fireball fb: fireballs){
            fb.updateWithTarget(input, player, enemyBoss);
        }

        player.update(input, fireballs, enemyBoss);
        if (enemyBoss != null) enemyBoss.updateWithTarget(input, player, fireballs);
        endFlag.updateWithTarget(input, player);
        FlyingPlatform.setPlayerLandedDuringThisFrame(false);

        if(endFlag.getIsCollided()) {
            finished = true;
        }
    }

    /**
     * Getter for the Properties file.
     * @return Properties object which is being used to get the game properties
     */
    public static Properties getPROPS(){
        return PROPS;
    }
}