/*
 * Copyright (c) 2019 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.spacefx;

import com.jpro.webapi.WebAPI;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static eu.hansolo.spacefx.Config.*;


public class SpaceFX extends Application {
    private static final LogManager                 LOG_MANAGER             = LogManager.INSTANCE;
    private static final long                       SCREEN_TOGGLE_INTERVAL  = 10_000_000_000l;
    private static final Random                     RND                     = new Random();
    private static final String                     SPACE_BOY;
    private static       String                     spaceBoyName;
    private static final boolean                    IS_BROWSER              = WebAPI.isBrowser();
    private static final Level1                     LEVEL_1                 = new Level1();
    private static final Level2                     LEVEL_2                 = new Level2();
    private static final Level3                     LEVEL_3                 = new Level3();
    private              Scene                      scene;
    private              long                       lastScreenToggle;
    private              boolean                    showHallOfFame;
    private              boolean                    running;
    private              boolean                    gameOverScreen;
    private              Properties                 properties;
    private              Label                      playerInitialsLabel;
    private              TextField                  playerInitials;
    private              List<Player>               hallOfFame;
    private              VBox                       hallOfFameBox;
    private              Level                      level                   = LEVEL_1;
    private final        Image                      startImg                = new Image(getClass().getResourceAsStream("startscreen.png"));
    private final        Image                      gameOverImg             = new Image(getClass().getResourceAsStream("gameover.png"));
    private final        Image                      hallOfFameImg           = new Image(getClass().getResourceAsStream("halloffamescreen.jpg"));
    private final        Image[]                    asteroidImages          = { new Image(getClass().getResourceAsStream("asteroid1.png"), 140 * SCALING_FACTOR, 140 * SCALING_FACTOR, true, false),
                                                                                new Image(getClass().getResourceAsStream("asteroid2.png"), 140 * SCALING_FACTOR, 140 * SCALING_FACTOR, true, false),
                                                                                new Image(getClass().getResourceAsStream("asteroid3.png"), 140 * SCALING_FACTOR, 140 * SCALING_FACTOR, true, false),
                                                                                new Image(getClass().getResourceAsStream("asteroid4.png"), 110 * SCALING_FACTOR, 110 * SCALING_FACTOR, true, false),
                                                                                new Image(getClass().getResourceAsStream("asteroid5.png"), 100 * SCALING_FACTOR, 100 * SCALING_FACTOR, true, false),
                                                                                new Image(getClass().getResourceAsStream("asteroid6.png"), 120 * SCALING_FACTOR, 120 * SCALING_FACTOR, true, false),
                                                                                new Image(getClass().getResourceAsStream("asteroid7.png"), 110 * SCALING_FACTOR, 110 * SCALING_FACTOR, true, false),
                                                                                new Image(getClass().getResourceAsStream("asteroid8.png"), 100 * SCALING_FACTOR, 100 * SCALING_FACTOR, true, false),
                                                                                new Image(getClass().getResourceAsStream("asteroid9.png"), 130 * SCALING_FACTOR, 130 * SCALING_FACTOR, true, false),
                                                                                new Image(getClass().getResourceAsStream("asteroid10.png"), 120 * SCALING_FACTOR, 120 * SCALING_FACTOR, true, false),
                                                                                new Image(getClass().getResourceAsStream("asteroid11.png"), 140 * SCALING_FACTOR, 140 * SCALING_FACTOR, true, false) };
    private final        Image                      torpedoButtonImg        = new Image(getClass().getResourceAsStream("torpedoButton.png"), 64 * SCALING_FACTOR, 64 * SCALING_FACTOR, true, false);
    private final        Image                      rocketButtonImg         = new Image(getClass().getResourceAsStream("rocketButton.png"), 64 * SCALING_FACTOR, 64 * SCALING_FACTOR, true, false);
    private final        Image                      shieldButtonImg         = new Image(getClass().getResourceAsStream("shieldButton.png"), 64 * SCALING_FACTOR, 64 * SCALING_FACTOR, true, false);
    private final        Image                      spaceshipImg            = new Image(getClass().getResourceAsStream("spaceship.png"), 48 * SCALING_FACTOR, 48 * SCALING_FACTOR, true, false);
    private final        Image                      spaceshipUpImg          = new Image(getClass().getResourceAsStream("spaceshipUp.png"), 48 * SCALING_FACTOR, 48 * SCALING_FACTOR, true, false);
    private final        Image                      spaceshipDownImg        = new Image(getClass().getResourceAsStream("spaceshipDown.png"), 48 * SCALING_FACTOR, 48 * SCALING_FACTOR, true, false);
    private final        Image                      miniSpaceshipImg        = new Image(getClass().getResourceAsStream("spaceship.png"), 16 * SCALING_FACTOR, 16 * SCALING_FACTOR, true, false);
    private final        Image                      deflectorShieldImg      = new Image(getClass().getResourceAsStream("deflectorshield.png"), 100 * SCALING_FACTOR, 100 * SCALING_FACTOR, true, false);
    private final        Image                      miniDeflectorShieldImg  = new Image(getClass().getResourceAsStream("deflectorshield.png"), 16 * SCALING_FACTOR, 16 * SCALING_FACTOR, true, false);
    private final        Image                      torpedoImg              = new Image(getClass().getResourceAsStream("torpedo.png"), 17 * SCALING_FACTOR, 20 * SCALING_FACTOR, true, false);
    private final        Image                      asteroidExplosionImg    = new Image(getClass().getResourceAsStream("asteroidExplosion.png"), 2048 * SCALING_FACTOR, 1792 * SCALING_FACTOR, true, false);
    private final        Image                      spaceShipExplosionImg   = new Image(getClass().getResourceAsStream("spaceshipexplosion.png"), 800 * SCALING_FACTOR, 600 * SCALING_FACTOR, true, false);
    private final        Image                      hitImg                  = new Image(getClass().getResourceAsStream("torpedoHit2.png"), 400 * SCALING_FACTOR, 160 * SCALING_FACTOR, true, false);
    private final        Image                      shieldUpImg             = new Image(getClass().getResourceAsStream("shieldUp.png"), 50 * SCALING_FACTOR, 50 * SCALING_FACTOR, true, false);
    private final        Image                      lifeUpImg               = new Image(getClass().getResourceAsStream("lifeUp.png"), 50 * SCALING_FACTOR, 50 * SCALING_FACTOR, true, false);
    private final        Image                      upExplosionImg          = new Image(getClass().getResourceAsStream("upExplosion.png"), 400 * SCALING_FACTOR, 700 * SCALING_FACTOR, true, false);
    private final        Image                      rocketExplosionImg      = new Image(getClass().getResourceAsStream("rocketExplosion.png"), 960 * SCALING_FACTOR, 768 * SCALING_FACTOR, true, false);
    private final        Image                      rocketImg               = new Image(getClass().getResourceAsStream("rocket.png"), 17 * SCALING_FACTOR, 50 * SCALING_FACTOR, true, false);
    private final        AudioClip                  laserSound              = new AudioClip(getClass().getResource("laserSound.wav").toExternalForm());
    private final        AudioClip                  rocketLaunchSound       = new AudioClip(getClass().getResource("rocketLaunch.wav").toExternalForm());
    private final        AudioClip                  rocketExplosionSound    = new AudioClip(getClass().getResource("rocketExplosion.wav").toExternalForm());
    private final        AudioClip                  enemyLaserSound         = new AudioClip(getClass().getResource("enemyLaserSound.wav").toExternalForm());
    private final        AudioClip                  enemyBombSound          = new AudioClip(getClass().getResource("enemyBomb.wav").toExternalForm());
    private final        AudioClip                  explosionSound          = new AudioClip(getClass().getResource("explosionSound.wav").toExternalForm());
    private final        AudioClip                  asteroidExplosionSound  = new AudioClip(getClass().getResource("asteroidExplosion.wav").toExternalForm());
    private final        AudioClip                  torpedoHitSound         = new AudioClip(getClass().getResource("hit.wav").toExternalForm());
    private final        AudioClip                  spaceShipExplosionSound = new AudioClip(getClass().getResource("spaceShipExplosionSound.wav").toExternalForm());
    private final        AudioClip                  enemyBossExplosionSound = new AudioClip(getClass().getResource("enemyBossExplosion.wav").toExternalForm());
    private final        AudioClip                  gameoverSound           = new AudioClip(getClass().getResource("gameover.wav").toExternalForm());
    private final        AudioClip                  shieldHitSound          = new AudioClip(getClass().getResource("shieldhit.wav").toExternalForm());
    private final        AudioClip                  enemyHitSound           = new AudioClip(getClass().getResource("enemyBossShieldHit.wav").toExternalForm());
    private final        AudioClip                  deflectorShieldSound    = new AudioClip(getClass().getResource("deflectorshieldSound.wav").toExternalForm());
    private final        AudioClip                  levelBossTorpedoSound   = new AudioClip(getClass().getResource("levelBossTorpedo.wav").toExternalForm());
    private final        AudioClip                  levelBossRocketSound    = new AudioClip(getClass().getResource("levelBossRocket.wav").toExternalForm());
    private final        AudioClip                  levelBossBombSound      = new AudioClip(getClass().getResource("levelBossBomb.wav").toExternalForm());
    private final        AudioClip                  levelBossExplosionSound = new AudioClip(getClass().getResource("explosionSound1.wav").toExternalForm());
    private final        AudioClip                  shieldUpSound           = new AudioClip(getClass().getResource("shieldUp.wav").toExternalForm());
    private final        AudioClip                  lifeUpSound             = new AudioClip(getClass().getResource("lifeUp.wav").toExternalForm());
    private final        Media                      gameSoundTheme          = new Media(getClass().getResource("RaceToMars.mp3").toExternalForm());
    private final        Media                      soundTheme              = new Media(getClass().getResource("CityStomper.mp3").toExternalForm());
    private final        MediaPlayer                gameMediaPlayer         = new MediaPlayer(gameSoundTheme);
    private final        MediaPlayer                mediaPlayer             = new MediaPlayer(soundTheme);
    private final        double                     deflectorShieldRadius   = deflectorShieldImg.getRequestedWidth() * 0.5;
    private              boolean                    levelBossActive;
    private              Font                       scoreFont;
    private              double                     backgroundViewportY;
    private              Canvas                     canvas;
    private              GraphicsContext            ctx;
    private              Star[]                     stars;
    private              Asteroid[]                 asteroids;
    private              SpaceShip                  spaceShip;
    private              SpaceShipExplosion         spaceShipExplosion;
    private              List<Wave>                 waves;
    private              List<Wave>                 wavesToRemove;
    private              List<EnemyBoss>            enemyBosses;
    private              List<EnemyBoss>            enemyBossesToRemove;
    private              List<LevelBoss>            levelBosses;
    private              List<LevelBoss>            levelBossesToRemove;
    private              List<ShieldUp>             shieldUps;
    private              List<ShieldUp>             shieldUpsToRemove;
    private              List<LifeUp>               lifeUps;
    private              List<LifeUp>               lifeUpsToRemove;
    private              List<Torpedo>              torpedos;
    private              List<Torpedo>              torpedosToRemove;
    private              List<Rocket>               rockets;
    private              List<Rocket>               rocketsToRemove;
    private              List<EnemyTorpedo>         enemyTorpedos;
    private              List<EnemyTorpedo>         enemyTorpedosToRemove;
    private              List<EnemyBomb>            enemyBombs;
    private              List<EnemyBomb>            enemyBombsToRemove;
    private              List<EnemyBossTorpedo>     enemyBossTorpedos;
    private              List<EnemyBossTorpedo>     enemyBossTorpedosToRemove;
    private              List<EnemyBossRocket>      enemyBossRockets;
    private              List<EnemyBossRocket>      enemyBossRocketsToRemove;
    private              List<LevelBossTorpedo>     levelBossTorpedos;
    private              List<LevelBossTorpedo>     levelBossTorpedosToRemove;
    private              List<LevelBossRocket>      levelBossRockets;
    private              List<LevelBossRocket>      levelBossRocketsToRemove;
    private              List<LevelBossBomb>        levelBossBombs;
    private              List<LevelBossBomb>        levelBossBombsToRemove;
    private              List<LevelBossExplosion>   levelBossExplosions;
    private              List<LevelBossExplosion>   levelBossExplosionsToRemove;
    private              List<EnemyBossExplosion>   enemyBossExplosions;
    private              List<EnemyBossExplosion>   enemyBossExplosionsToRemove;
    private              List<EnemyRocketExplosion> enemyRocketExplosions;
    private              List<EnemyRocketExplosion> enemyRocketExplosionsToRemove;
    private              List<RocketExplosion>      rocketExplosions;
    private              List<RocketExplosion>      rocketExplosionsToRemove;
    private              List<Explosion>            explosions;
    private              List<Explosion>            explosionsToRemove;
    private              List<AsteroidExplosion>    asteroidExplosions;
    private              List<AsteroidExplosion>    asteroidExplosionsToRemove;
    private              List<UpExplosion>          upExplosions;
    private              List<UpExplosion>          upExplosionsToRemove;
    private              List<Hit>                  hits;
    private              List<Hit>                  hitsToRemove;
    private              List<EnemyHit>             enemyHits;
    private              List<EnemyHit>             enemyHitsToRemove;
    private              long                       score;
    private              long                       levelKills;
    private              long                       kills;
    private              double                     scorePosX;
    private              double                     scorePosY;
    private              boolean                    hasBeenHit;
    private              int                        noOfLifes;
    private              int                        noOfShields;
    private              long                       lastShieldActivated;
    private              long                       lastEnemyBossAttack;
    private              long                       lastShieldUp;
    private              long                       lastLifeUp;
    private              long                       lastWave;
    private              long                       lastBombDropped;
    private              long                       lastTimerCall;
    private              AnimationTimer             timer;
    private              AnimationTimer             screenTimer;
    private              boolean                    shipPressed;
    private              EventHandler<MouseEvent>   mouseHandler;

    static {
        try {
            spaceBoyName = Font.loadFont(SpaceFX.class.getResourceAsStream("spaceboy.ttf"), 10).getName();
        } catch (Exception exception) { }
        SPACE_BOY = spaceBoyName;
    }


    // ******************** Methods *******************************************
    @Override public void init() {
        scoreFont        = spaceBoy(60 * SCALING_FACTOR);
        running          = false;
        gameOverScreen   = false;
        levelBossActive  = false;
        lastScreenToggle = System.nanoTime();
        showHallOfFame   = false;

        playerInitialsLabel = new Label("Type in your initials");
        playerInitialsLabel.setTranslateY(HEIGHT * 0.1);
        Helper.enableNode(playerInitialsLabel, false);

        playerInitials   = new TextField("--");
        playerInitials.setTranslateY(HEIGHT * 0.2);
        playerInitials.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().length() > 2) { return null; }
            if (change.getText().toUpperCase().matches("[A-Z\\-]{0,2}")) {
                change.setText(change.getText().toUpperCase());
                return change;
            } else {
                return null;
            }
        }));
        playerInitials.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER) && playerInitials.getText().length() == 2) {
                hallOfFame.add(new Player(playerInitials.getText(), score));
                Collections.sort(hallOfFame);
                hallOfFame = hallOfFame.stream().limit(3).collect(Collectors.toList());

                // Store hall of fame in properties
                properties.setProperty("hallOfFame1", hallOfFame.get(0).toPropertyString());
                properties.setProperty("hallOfFame2", hallOfFame.get(1).toPropertyString());
                properties.setProperty("hallOfFame3", hallOfFame.get(2).toPropertyString());
                PropertyManager.INSTANCE.storeProperties();

                HBox p1Entry  = createHallOfFameEntry(new Player(properties.getProperty("hallOfFame1")));
                HBox p2Entry  = createHallOfFameEntry(new Player(properties.getProperty("hallOfFame2")));
                HBox p3Entry  = createHallOfFameEntry(new Player(properties.getProperty("hallOfFame3")));
                hallOfFameBox.getChildren().setAll(p1Entry, p2Entry, p3Entry);

                Helper.enableNode(playerInitialsLabel, false);
                Helper.enableNode(playerInitials, false);

                PauseTransition waitForHallOfFame = new PauseTransition(Duration.millis(3000));
                waitForHallOfFame.setOnFinished(a -> reInitGame());
                waitForHallOfFame.play();
            }
        });
        Helper.enableNode(playerInitials, false);

        // PreFill hall of fame
        properties = PropertyManager.INSTANCE.getProperties();

        Player p1 = new Player(properties.getProperty("hallOfFame1"));
        Player p2 = new Player(properties.getProperty("hallOfFame2"));
        Player p3 = new Player(properties.getProperty("hallOfFame3"));

        LOG_MANAGER.logInfo(SpaceFX.class, "Player 1: " + p1);
        LOG_MANAGER.logInfo(SpaceFX.class, "Player 2: " + p2);
        LOG_MANAGER.logInfo(SpaceFX.class, "Player 3: " + p3);

        hallOfFame = new ArrayList<>(3);
        hallOfFame.add(p1);
        hallOfFame.add(p2);
        hallOfFame.add(p3);

        HBox p1Entry  = createHallOfFameEntry(p1);
        HBox p2Entry  = createHallOfFameEntry(p2);
        HBox p3Entry  = createHallOfFameEntry(p3);
        hallOfFameBox = new VBox(20, p1Entry, p2Entry, p3Entry);
        hallOfFameBox.setMaxWidth(WIDTH * 0.4);
        hallOfFameBox.setAlignment(Pos.CENTER);
        hallOfFameBox.setTranslateY(-HEIGHT * 0.1);
        hallOfFameBox.setMouseTransparent(true);
        Helper.enableNode(hallOfFameBox, false);

        // Mediaplayer for background music
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.2);

        // Mediaplayer for game background music
        gameMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        gameMediaPlayer.setVolume(0.5);

        // Adjust audio clip volumes
        explosionSound.setVolume(0.5);
        torpedoHitSound.setVolume(0.5);

        // Variable initialization
        backgroundViewportY           = 2079; //backgroundImg.getHeight() - HEIGHT;
        canvas                        = new Canvas(WIDTH, HEIGHT);
        ctx                           = canvas.getGraphicsContext2D();
        stars                         = new Star[NO_OF_STARS];
        asteroids                     = new Asteroid[NO_OF_ASTEROIDS];
        spaceShip                     = new SpaceShip(spaceshipImg, spaceshipUpImg, spaceshipDownImg);
        spaceShipExplosion            = new SpaceShipExplosion(0, 0);
        waves                         = new ArrayList<>();
        wavesToRemove                 = new ArrayList<>();
        enemyBosses                   = new ArrayList<>();
        enemyBossesToRemove           = new ArrayList<>();
        levelBosses                   = new ArrayList<>();
        levelBossesToRemove           = new ArrayList<>();
        shieldUps                     = new ArrayList<>();
        shieldUpsToRemove             = new ArrayList<>();
        lifeUps                       = new ArrayList<>();
        lifeUpsToRemove               = new ArrayList<>();
        rockets                       = new ArrayList<>();
        rocketsToRemove               = new ArrayList<>();
        torpedos                      = new ArrayList<>();
        torpedosToRemove              = new ArrayList<>();
        enemyRocketExplosions         = new ArrayList<>();
        enemyRocketExplosionsToRemove = new ArrayList<>();
        explosions                    = new ArrayList<>();
        explosionsToRemove            = new ArrayList<>();
        asteroidExplosions            = new ArrayList<>();
        asteroidExplosionsToRemove    = new ArrayList<>();
        upExplosions                  = new ArrayList<>();
        upExplosionsToRemove          = new ArrayList<>();
        enemyTorpedos                 = new ArrayList<>();
        enemyTorpedosToRemove         = new ArrayList<>();
        enemyBombs                    = new ArrayList<>();
        enemyBombsToRemove            = new ArrayList<>();
        enemyBossTorpedos             = new ArrayList<>();
        enemyBossTorpedosToRemove     = new ArrayList<>();
        enemyBossRockets              = new ArrayList<>();
        enemyBossRocketsToRemove      = new ArrayList<>();
        levelBossTorpedos             = new ArrayList<>();
        levelBossTorpedosToRemove     = new ArrayList<>();
        levelBossRockets              = new ArrayList<>();
        levelBossRocketsToRemove      = new ArrayList<>();
        levelBossBombs                = new ArrayList<>();
        levelBossBombsToRemove        = new ArrayList<>();
        levelBossExplosions           = new ArrayList<>();
        levelBossExplosionsToRemove   = new ArrayList<>();
        enemyBossExplosions           = new ArrayList<>();
        enemyBossExplosionsToRemove   = new ArrayList<>();
        rocketExplosions              = new ArrayList<>();
        rocketExplosionsToRemove      = new ArrayList<>();
        hits                          = new ArrayList<>();
        hitsToRemove                  = new ArrayList<>();
        enemyHits                     = new ArrayList<>();
        enemyHitsToRemove             = new ArrayList<>();
        score                         = 0;
        levelKills                    = 0;
        kills                         = 0;
        hasBeenHit                    = false;
        noOfLifes                     = LIFES;
        noOfShields                   = SHIELDS;
        lastShieldActivated           = 0;
        lastEnemyBossAttack           = System.nanoTime();
        lastShieldUp                  = System.nanoTime();
        lastLifeUp                    = System.nanoTime();
        lastWave                      = System.nanoTime();
        long deltaTime                = IS_BROWSER ? FPS_30 : FPS_60;
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall) {
                    lastTimerCall = now + deltaTime;
                    updateAndDraw();
                }
                if (now > lastEnemyBossAttack + ENEMY_BOSS_ATTACK_INTERVAL) {
                    spawnEnemyBoss(spaceShip);
                    lastEnemyBossAttack = now;
                }
                if (now > lastShieldUp + SHIELD_UP_SPAWN_INTERVAL) {
                    spawnShieldUp();
                    lastShieldUp = now;
                }
                if (now > lastLifeUp + LIFE_UP_SPAWN_INTERVAL) {
                    spawnLifeUp();
                    lastLifeUp = now;
                }
                if (now > lastWave + WAVE_SPAWN_INTERVAL && SHOW_ENEMIES) {
                    spawnWave();
                    lastWave = now;
                }
            }
        };
        screenTimer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (!running && now > lastScreenToggle + SCREEN_TOGGLE_INTERVAL) {
                    if (showHallOfFame) {
                        ctx.drawImage(hallOfFameImg, 0, 0);
                        Helper.enableNode(hallOfFameBox, true);
                    } else {
                        ctx.drawImage(startImg, 0, 0);
                        Helper.enableNode(hallOfFameBox, false);
                    }
                    showHallOfFame = !showHallOfFame;
                    lastScreenToggle = now;
                }
            }
        };

        mouseHandler = e -> {
            // boolean snythesized = e.isSynthesized(); // detect if mouse event is coming from touch event
            EventType<? extends MouseEvent> type = e.getEventType();
            double mouseX = e.getSceneX();
            double mouseY = e.getSceneY();
            if (type.equals(MouseEvent.MOUSE_PRESSED)) {
                shipPressed = Helper.isInsideCircle(spaceShip.x, spaceShip.y, spaceShip.radius, mouseX, mouseY);
                if (SHOW_BUTTONS) {
                    if (Helper.isInsideCircle(TORPEDO_BUTTON_CX, TORPEDO_BUTTON_CY, TORPEDO_BUTTON_R, mouseX, mouseY)) {
                        spawnTorpedo(spaceShip.x, spaceShip.y);
                    }
                    if (Helper.isInsideCircle(ROCKET_BUTTON_CX, ROCKET_BUTTON_CY, ROCKET_BUTTON_R, mouseX, mouseY)) {
                        if (rockets.size() < MAX_NO_OF_ROCKETS) {
                            spawnRocket(spaceShip.x, spaceShip.y);
                        }
                    }
                    if (Helper.isInsideCircle(SHIELD_BUTTON_CX, SHIELD_BUTTON_CY, SHIELD_BUTTON_R, mouseX, mouseY)) {
                        if (noOfShields > 0 && !spaceShip.shield) {
                            lastShieldActivated = System.currentTimeMillis();
                            spaceShip.shield = true;
                            playSound(deflectorShieldSound);
                        }
                    }
                }
            } else if (type.equals(MouseEvent.MOUSE_DRAGGED)) {
                if (shipPressed) {
                    spaceShip.x = mouseX;
                    spaceShip.y = mouseY;
                }
            } else if (type.equals(MouseEvent.MOUSE_RELEASED)) {
                shipPressed = false;
            }
        };

        initStars();
        initAsteroids();

        scorePosX = WIDTH * 0.5;
        scorePosY = 40;

        // Preparing GraphicsContext
        ctx.setFont(scoreFont);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.drawImage(startImg, 0, 0);

        screenTimer.start();
    }

    private void initStars() {
        for (int i = 0; i < NO_OF_STARS; i++) {
            Star star = new Star();
            star.y = RND.nextDouble() * HEIGHT;
            stars[i] = star;
        }
    }

    private void initAsteroids() {
        for (int i = 0 ; i < NO_OF_ASTEROIDS ; i++) {
            asteroids[i] = new Asteroid(asteroidImages[RND.nextInt(asteroidImages.length)]);
        }
    }

    @Override public void start(final Stage stage) {
        StackPane pane = new StackPane(canvas, hallOfFameBox, playerInitialsLabel, playerInitials);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        scene = new Scene(pane);
        scene.getStylesheets().add(SpaceFX.class.getResource("spacefx.css").toExternalForm());

        // Setup key listener
        scene.setOnKeyPressed(e -> {
            if (running) {
                switch(e.getCode()) {
                    case UP:
                        spaceShip.vY = -5;
                        break;
                    case RIGHT:
                        spaceShip.vX = 5;
                        break;
                    case DOWN:
                        spaceShip.vY = 5;
                        break;
                    case LEFT:
                        spaceShip.vX = -5;
                        break;
                    case S:
                        if (noOfShields > 0 && !spaceShip.shield) {
                            lastShieldActivated = System.currentTimeMillis();
                            spaceShip.shield = true;
                            playSound(deflectorShieldSound);
                        }
                        break;
                    case R:
                        // Max 5 rockets at the same time
                        if (rockets.size() < MAX_NO_OF_ROCKETS) {
                            spawnRocket(spaceShip.x, spaceShip.y);
                        }
                        break;
                    case SPACE:
                        spawnTorpedo(spaceShip.x, spaceShip.y);
                        break;
                }
            } else if (e.getCode() == KeyCode.P && !gameOverScreen) {
                ctx.clearRect(0, 0, WIDTH, HEIGHT);
                if (SHOW_BACKGROUND) {
                    ctx.drawImage(level.getBackgroundImg(), 0, 0);
                }
                if (PLAY_MUSIC) {
                    mediaPlayer.pause();
                    gameMediaPlayer.play();
                }
                Helper.enableNode(hallOfFameBox, false);
                screenTimer.stop();
                running = true;
                timer.start();
            }
        });
        scene.setOnKeyReleased(e -> {
            if (running) {
                switch (e.getCode()) {
                    case UP   : spaceShip.vY = 0; break;
                    case RIGHT: spaceShip.vX = 0; break;
                    case DOWN : spaceShip.vY = 0; break;
                    case LEFT : spaceShip.vX = 0; break;
                }
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);

        stage.setTitle("SpaceFX");
        stage.setScene(scene);
        stage.show();

        // Start playing background music
        if (PLAY_MUSIC) { mediaPlayer.play(); }
    }

    @Override public void stop() {
        if (!IS_BROWSER) {
            Platform.exit();
            System.exit(0);
        }
    }


    // Update and draw
    private void updateAndDraw() {
        torpedosToRemove.clear();
        rocketsToRemove.clear();
        explosionsToRemove.clear();
        hitsToRemove.clear();
        asteroidExplosionsToRemove.clear();
        rocketExplosionsToRemove.clear();
        upExplosionsToRemove.clear();
        shieldUpsToRemove.clear();
        lifeUpsToRemove.clear();
        wavesToRemove.clear();
        enemyHitsToRemove.clear();
        enemyTorpedosToRemove.clear();
        enemyBombsToRemove.clear();
        enemyRocketExplosionsToRemove.clear();
        enemyBossesToRemove.clear();
        enemyBossExplosionsToRemove.clear();
        enemyBossTorpedosToRemove.clear();
        enemyBossRocketsToRemove.clear();
        levelBossesToRemove.clear();
        levelBossTorpedosToRemove.clear();
        levelBossRocketsToRemove.clear();
        levelBossBombsToRemove.clear();
        levelBossExplosionsToRemove.clear();

        ctx.clearRect(0, 0, WIDTH, HEIGHT);

        // Draw background
        if (SHOW_BACKGROUND) {
            backgroundViewportY -= 0.5;
            if (backgroundViewportY <= 0) {
                backgroundViewportY = 2079; //backgroundImg.getHeight() - HEIGHT;
            }
            ctx.drawImage(level.getBackgroundImg(), 0, backgroundViewportY, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT);
        }

        // Draw Stars
        if (SHOW_STARS) {
            ctx.setFill(Color.rgb(255, 255, 255, 0.9));
            for (int i = 0; i < NO_OF_STARS; i++) {
                Star star = stars[i];
                star.update();
                ctx.fillOval(star.x, star.y, star.size, star.size);
            }
        }

        // Draw Asteroids
        for (int i = 0 ; i < NO_OF_ASTEROIDS ; i++) {
            Asteroid asteroid = asteroids[i];
            asteroid.update();
            ctx.save();
            ctx.translate(asteroid.cX, asteroid.cY);
            ctx.rotate(asteroid.rot);
            ctx.scale(asteroid.scale, asteroid.scale);
            ctx.translate(-asteroid.imgCenterX, -asteroid.imgCenterY);
            ctx.drawImage(asteroid.image, 0, 0);
            ctx.restore();

            // Check for torpedo hits
            for (Torpedo torpedo : torpedos) {
                if (isHitCircleCircle(torpedo.x, torpedo.y, torpedo.radius, asteroid.cX, asteroid.cY, asteroid.radius)) {
                    asteroid.hits--;
                    if (asteroid.hits == 0) {
                        asteroidExplosions.add(new AsteroidExplosion(asteroid.cX - AsteroidExplosion.FRAME_CENTER * asteroid.scale, asteroid.cY - AsteroidExplosion.FRAME_CENTER * asteroid.scale, asteroid.vX, asteroid.vY, asteroid.scale));
                        score += asteroid.value;
                        asteroid.respawn();
                        torpedosToRemove.add(torpedo);
                        playSound(asteroidExplosionSound);
                    } else {
                        hits.add(new Hit(torpedo.x - Hit.FRAME_CENTER, torpedo.y - Hit.FRAME_HEIGHT, asteroid.vX, asteroid.vY));
                        torpedosToRemove.add(torpedo);
                        playSound(torpedoHitSound);
                    }
                }
            }

            // Check for rocket hits
            for (Rocket rocket : rockets) {
                if (isHitCircleCircle(rocket.x, rocket.y, rocket.radius, asteroid.cX, asteroid.cY, asteroid.radius)) {
                    rocketExplosions.add(new RocketExplosion(asteroid.cX - RocketExplosion.FRAME_CENTER * asteroid.scale, asteroid.cY - RocketExplosion.FRAME_CENTER * asteroid.scale, asteroid.vX, asteroid.vY, asteroid.scale));
                    score += asteroid.value;
                    asteroid.respawn();
                    rocketsToRemove.add(rocket);
                    playSound(rocketExplosionSound);
                }
            }

            // Check for space ship hit
            if (spaceShip.isVulnerable && !hasBeenHit) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, asteroid.cX, asteroid.cY, asteroid.radius);
                } else {
                    hit = isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, asteroid.cX, asteroid.cY, asteroid.radius);
                }
                if (hit) {
                    spaceShipExplosion.countX = 0;
                    spaceShipExplosion.countY = 0;
                    spaceShipExplosion.x      = spaceShip.x - SpaceShipExplosion.FRAME_WIDTH;
                    spaceShipExplosion.y      = spaceShip.y - SpaceShipExplosion.FRAME_HEIGHT;
                    if (spaceShip.shield) {
                        playSound(explosionSound);
                        asteroidExplosions.add(new AsteroidExplosion(asteroid.cX - AsteroidExplosion.FRAME_CENTER * asteroid.scale, asteroid.cY - AsteroidExplosion.FRAME_CENTER * asteroid.scale, asteroid.vX, asteroid.vY, asteroid.scale));
                    } else {
                        playSound(spaceShipExplosionSound);
                        hasBeenHit = true;
                        noOfLifes--;
                        if (0 == noOfLifes) {
                            gameOver();
                        }
                    }
                    asteroid.respawn();
                }
            }
        }

        // Draw Wave
        for (Wave wave : waves) {
            if (wave.isRunning) {
                wave.update(ctx);
            } else {
                wavesToRemove.add(wave);
            }
        }
        waves.removeAll(wavesToRemove);

        // Draw EnemyBoss
        for (EnemyBoss enemyBoss : enemyBosses) {
            enemyBoss.update();
            ctx.save();
            ctx.translate(enemyBoss.x - enemyBoss.radius, enemyBoss.y - enemyBoss.radius);
            ctx.save();
            ctx.translate(enemyBoss.radius, enemyBoss.radius);
            ctx.rotate(enemyBoss.r);
            ctx.translate(-enemyBoss.radius, -enemyBoss.radius);
            ctx.drawImage(enemyBoss.image, 0, 0);
            ctx.restore();
            ctx.restore();

            // Check for torpedo hits with enemy boss
            for (Torpedo torpedo : torpedos) {
                if (isHitCircleCircle(torpedo.x, torpedo.y, torpedo.radius, enemyBoss.x, enemyBoss.y, enemyBoss.radius)) {
                    enemyBoss.hits--;
                    if (enemyBoss.hits == 0) {
                        enemyBossExplosions.add(
                            new EnemyBossExplosion(enemyBoss.x - EnemyBossExplosion.FRAME_WIDTH * 0.25, enemyBoss.y - EnemyBossExplosion.FRAME_HEIGHT * 0.25, enemyBoss.vX,
                                                   enemyBoss.vY, 0.5));
                        score += enemyBoss.value;
                        kills++;
                        levelKills++;
                        enemyBossesToRemove.add(enemyBoss);
                        torpedosToRemove.add(torpedo);
                        playSound(enemyBossExplosionSound);
                    } else {
                        enemyHits.add(new EnemyHit(torpedo.x - Hit.FRAME_CENTER, torpedo.y - Hit.FRAME_HEIGHT, enemyBoss.vX, enemyBoss.vY));
                        torpedosToRemove.add(torpedo);
                        playSound(enemyHitSound);
                    }
                }
            }

            // Check for rocket hits with enemy boss
            for (Rocket rocket : rockets) {
                if (isHitCircleCircle(rocket.x, rocket.y, rocket.radius, enemyBoss.x, enemyBoss.y, enemyBoss.radius)) {
                    enemyBossExplosions.add(
                        new EnemyBossExplosion(enemyBoss.x - EnemyBossExplosion.FRAME_WIDTH * 0.25, enemyBoss.y - EnemyBossExplosion.FRAME_HEIGHT * 0.25, enemyBoss.vX, enemyBoss.vY, 0.5));
                    score += enemyBoss.value;
                    kills++;
                    levelKills++;
                    enemyBossesToRemove.add(enemyBoss);
                    rocketsToRemove.add(rocket);
                    playSound(enemyBossExplosionSound);
                }
            }


            // Check for space ship hit with enemy boss
            if (spaceShip.isVulnerable && !hasBeenHit) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, enemyBoss.x, enemyBoss.y, enemyBoss.radius);
                } else {
                    hit = isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, enemyBoss.x, enemyBoss.y, enemyBoss.radius);
                }
                if (hit) {
                    if (spaceShip.shield) {
                        enemyBossExplosions.add(new EnemyBossExplosion(enemyBoss.x - EnemyBossExplosion.FRAME_WIDTH * 0.125, enemyBoss.y - EnemyBossExplosion.FRAME_HEIGHT * 0.125, enemyBoss.vX, enemyBoss.vY, 0.5));
                        playSound(enemyBossExplosionSound);
                    } else {
                        spaceShipExplosion.countX = 0;
                        spaceShipExplosion.countY = 0;
                        spaceShipExplosion.x = spaceShip.x - SpaceShipExplosion.FRAME_WIDTH;
                        spaceShipExplosion.y = spaceShip.y - SpaceShipExplosion.FRAME_HEIGHT;
                        playSound(spaceShipExplosionSound);
                        hasBeenHit = true;
                        noOfLifes--;
                        if (0 == noOfLifes) {
                            gameOver();
                        }
                    }
                    enemyBossesToRemove.add(enemyBoss);
                }
            }
        }
        enemyBosses.removeAll(enemyBossesToRemove);

        // Draw LevelBoss
        for (LevelBoss levelBoss : levelBosses) {
            levelBoss.update();
            ctx.save();
            ctx.translate(levelBoss.x - levelBoss.radius, levelBoss.y - levelBoss.radius);
            ctx.save();
            ctx.translate(levelBoss.radius, levelBoss.radius);
            ctx.rotate(levelBoss.r);
            ctx.translate(-levelBoss.radius, -levelBoss.radius);
            ctx.drawImage(levelBoss.image, 0, 0);
            ctx.restore();
            ctx.restore();

            // Check for torpedo hits with enemy boss
            for (Torpedo torpedo : torpedos) {
                if (isHitCircleCircle(torpedo.x, torpedo.y, torpedo.radius, levelBoss.x, levelBoss.y, levelBoss.radius)) {
                    levelBoss.hits--;
                    if (levelBoss.hits == 0) {
                        levelBossExplosions.add(new LevelBossExplosion(levelBoss.x - LevelBossExplosion.FRAME_WIDTH * 0.25, levelBoss.y - LevelBossExplosion.FRAME_HEIGHT * 0.25, levelBoss.vX, levelBoss.vY, 1.0));
                        score += levelBoss.value;
                        kills++;
                        levelBossesToRemove.add(levelBoss);
                        levelBossActive = false;
                        levelKills = 0;
                        nextLevel();
                        torpedosToRemove.add(torpedo);
                        playSound(levelBossExplosionSound);
                    } else {
                        enemyHits.add(new EnemyHit(torpedo.x - Hit.FRAME_CENTER, torpedo.y - Hit.FRAME_HEIGHT, levelBoss.vX, levelBoss.vY));
                        torpedosToRemove.add(torpedo);
                        playSound(enemyHitSound);
                    }
                }
            }

            // Check for rocket hits with level boss
            for (Rocket rocket : rockets) {
                if (isHitCircleCircle(rocket.x, rocket.y, rocket.radius, levelBoss.x, levelBoss.y, levelBoss.radius)) {
                    levelBoss.hits -= 3;
                    if (levelBoss.hits <= 0) {
                        levelBossExplosions.add(new LevelBossExplosion(levelBoss.x - LevelBossExplosion.FRAME_WIDTH * 0.25, levelBoss.y - LevelBossExplosion.FRAME_HEIGHT * 0.25, levelBoss.vX, levelBoss.vY, 1.0));
                        score += levelBoss.value;
                        kills++;
                        levelKills++;
                        levelBossesToRemove.add(levelBoss);
                        levelBossActive = false;
                        levelKills = 0;
                        nextLevel();
                        rocketsToRemove.add(rocket);
                        playSound(levelBossExplosionSound);
                    } else {
                        enemyHits.add(new EnemyHit(rocket.x - Hit.FRAME_CENTER, rocket.y - Hit.FRAME_HEIGHT, levelBoss.vX, levelBoss.vY));
                        rocketsToRemove.add(rocket);
                        playSound(enemyHitSound);
                    }
                }
            }

            // Check for space ship hit with level boss
            if (spaceShip.isVulnerable && !hasBeenHit) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, levelBoss.x, levelBoss.y, levelBoss.radius);
                } else {
                    hit = isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, levelBoss.x, levelBoss.y, levelBoss.radius);
                }
                if (hit) {
                    if (spaceShip.shield) {
                        explosions.add(new Explosion(levelBoss.x - Explosion.FRAME_WIDTH * 0.125, levelBoss.y - Explosion.FRAME_HEIGHT * 0.125, levelBoss.vX, levelBoss.vY, 0.5));
                        playSound(spaceShipExplosionSound);
                    } else {
                        spaceShipExplosion.countX = 0;
                        spaceShipExplosion.countY = 0;
                        spaceShipExplosion.x = spaceShip.x - SpaceShipExplosion.FRAME_WIDTH;
                        spaceShipExplosion.y = spaceShip.y - SpaceShipExplosion.FRAME_HEIGHT;
                        playSound(spaceShipExplosionSound);
                        hasBeenHit = true;
                        noOfLifes--;
                        if (0 == noOfLifes) {
                            gameOver();
                        }
                    }
                    levelBossesToRemove.add(levelBoss);
                    levelBossActive = false;
                    levelKills = 0;
                    nextLevel();
                }
            }
        }
        levelBosses.removeAll(levelBossesToRemove);

        // Draw ShieldUps
        for (ShieldUp shieldUp : shieldUps) {
            shieldUp.update();
            ctx.save();
            ctx.translate(shieldUp.cX, shieldUp.cY);
            ctx.rotate(shieldUp.rot);
            ctx.translate(-shieldUp.imgCenterX, -shieldUp.imgCenterY);
            ctx.drawImage(shieldUp.image, 0, 0);
            ctx.restore();

            // Check for space ship contact
            boolean hit;
            if (spaceShip.shield) {
                hit = isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, shieldUp.cX, shieldUp.cY, shieldUp.radius);
            } else {
                hit = isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, shieldUp.cX, shieldUp.cY, shieldUp.radius);
            }
            if (hit) {
                if (noOfShields <= SHIELDS - 1) { noOfShields++; }
                upExplosions.add(new UpExplosion(shieldUp.cX - UpExplosion.FRAME_CENTER, shieldUp.cY - UpExplosion.FRAME_CENTER, shieldUp.vX, shieldUp.vY, 1.0));
                playSound(shieldUpSound);
                shieldUpsToRemove.add(shieldUp);
            }
        }
        shieldUps.removeAll(shieldUpsToRemove);

        // Draw LifeUps
        for (LifeUp lifeUp : lifeUps) {
            lifeUp.update();
            ctx.save();
            ctx.translate(lifeUp.cX, lifeUp.cY);
            ctx.rotate(lifeUp.rot);
            ctx.translate(-lifeUp.imgCenterX, -lifeUp.imgCenterY);
            ctx.drawImage(lifeUp.image, 0, 0);
            ctx.restore();

            // Check for space ship contact
            boolean hit;
            if (spaceShip.shield) {
                hit = isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, lifeUp.cX, lifeUp.cY, lifeUp.radius);
            } else {
                hit = isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, lifeUp.cX, lifeUp.cY, lifeUp.radius);
            }
            if (hit) {
                if (noOfLifes <= LIFES - 1) { noOfLifes++; }
                upExplosions.add(new UpExplosion(lifeUp.cX - UpExplosion.FRAME_CENTER, lifeUp.cY - UpExplosion.FRAME_CENTER, lifeUp.vX, lifeUp.vY, 1.0));
                playSound(lifeUpSound);
                lifeUpsToRemove.add(lifeUp);
            }
        }
        lifeUps.removeAll(lifeUpsToRemove);

        // Draw Torpedos
        for (Torpedo torpedo : torpedos) {
            torpedo.update();
            ctx.drawImage(torpedo.image, torpedo.x - torpedo.radius, torpedo.y - torpedo.radius);
        }
        torpedos.removeAll(torpedosToRemove);

        // Draw Rockets
        for (Rocket rocket : rockets) {
            rocket.update();
            ctx.drawImage(rocket.image, rocket.x - rocket.halfWidth, rocket.y - rocket.halfHeight);
        }
        rockets.removeAll(rocketsToRemove);

        // Draw EnemyTorpedos
        for (EnemyTorpedo enemyTorpedo : enemyTorpedos) {
            enemyTorpedo.update();
            ctx.drawImage(enemyTorpedo.image, enemyTorpedo.x, enemyTorpedo.y);
        }
        enemyTorpedos.removeAll(enemyTorpedosToRemove);

        // Draw EnemyBombs
        for (EnemyBomb enemyBomb : enemyBombs) {
            enemyBomb.update();
            ctx.drawImage(enemyBomb.image, enemyBomb.x, enemyBomb.y);
        }
        enemyBombs.removeAll(enemyBombsToRemove);

        // Draw EnemyBossTorpedos
        for (EnemyBossTorpedo enemyBossTorpedo : enemyBossTorpedos) {
            enemyBossTorpedo.update();
            ctx.drawImage(enemyBossTorpedo.image, enemyBossTorpedo.x, enemyBossTorpedo.y);
        }
        enemyBossTorpedos.removeAll(enemyBossTorpedosToRemove);

        // Draw EnemyBossRockets
        for (EnemyBossRocket enemyBossRocket : enemyBossRockets) {
            enemyBossRocket.update();
            ctx.save();
            ctx.translate(enemyBossRocket.x - enemyBossRocket.width / 2, enemyBossRocket.y - enemyBossRocket.height / 2);
            ctx.save();
            ctx.translate(enemyBossRocket.width / 2, enemyBossRocket.height / 2);
            ctx.rotate(enemyBossRocket.r);
            ctx.translate(-enemyBossRocket.width / 2, -enemyBossRocket.height / 2);
            ctx.drawImage(enemyBossRocket.image, 0, 0);
            ctx.restore();
            ctx.restore();
        }
        enemyBossRockets.removeAll(enemyBossRocketsToRemove);

        // Draw LevelBossTorpedos
        for (LevelBossTorpedo levelBossTorpedo : levelBossTorpedos) {
            levelBossTorpedo.update();
            ctx.save();
            ctx.translate(levelBossTorpedo.x - levelBossTorpedo.width / 2, levelBossTorpedo.y - levelBossTorpedo.height / 2);
            ctx.save();
            ctx.translate(levelBossTorpedo.width / 2, levelBossTorpedo.height / 2);
            ctx.rotate(levelBossTorpedo.r);
            ctx.translate(-levelBossTorpedo.width / 2, -levelBossTorpedo.height / 2);
            ctx.drawImage(levelBossTorpedo.image, 0, 0);
            ctx.restore();
            ctx.restore();
        }
        levelBossTorpedos.removeAll(levelBossTorpedosToRemove);

        // Draw LevelBossRockets
        for (LevelBossRocket levelBossRocket : levelBossRockets) {
            levelBossRocket.update();
            ctx.save();
            ctx.translate(levelBossRocket.x - levelBossRocket.width / 2, levelBossRocket.y - levelBossRocket.height / 2);
            ctx.save();
            ctx.translate(levelBossRocket.width / 2, levelBossRocket.height / 2);
            ctx.rotate(levelBossRocket.r);
            ctx.translate(-levelBossRocket.width / 2, -levelBossRocket.height / 2);
            ctx.drawImage(levelBossRocket.image, 0, 0);
            ctx.restore();
            ctx.restore();
        }
        levelBossRockets.removeAll(levelBossRocketsToRemove);

        // Draw LevelBossBombs
        for (LevelBossBomb levelBossBomb : levelBossBombs) {
            levelBossBomb.update();
            ctx.drawImage(levelBossBomb.image, levelBossBomb.x, levelBossBomb.y);
        }
        levelBossBombs.removeAll(levelBossBombsToRemove);

        // Draw Explosions
        for (Explosion explosion : explosions) {
            explosion.update();
            ctx.drawImage(level.getExplosionImg(), explosion.countX * Explosion.FRAME_WIDTH, explosion.countY * Explosion.FRAME_HEIGHT, Explosion.FRAME_WIDTH, Explosion.FRAME_HEIGHT,
                          explosion.x, explosion.y, Explosion.FRAME_WIDTH * explosion.scale, Explosion.FRAME_HEIGHT * explosion.scale);
        }
        explosions.removeAll(explosionsToRemove);

        // Draw AsteroidExplosions
        for (AsteroidExplosion asteroidExplosion : asteroidExplosions) {
            asteroidExplosion.update();
            ctx.drawImage(asteroidExplosionImg, asteroidExplosion.countX * AsteroidExplosion.FRAME_WIDTH, asteroidExplosion.countY * AsteroidExplosion.FRAME_HEIGHT,
                          AsteroidExplosion.FRAME_WIDTH, AsteroidExplosion.FRAME_HEIGHT, asteroidExplosion.x, asteroidExplosion.y,
                          AsteroidExplosion.FRAME_WIDTH * asteroidExplosion.scale, AsteroidExplosion.FRAME_HEIGHT * asteroidExplosion.scale);
        }
        asteroidExplosions.removeAll(asteroidExplosionsToRemove);

        // Draw RocketExplosions
        for (RocketExplosion rocketExplosion : rocketExplosions) {
            rocketExplosion.update();
            ctx.drawImage(rocketExplosionImg, rocketExplosion.countX * rocketExplosion.FRAME_WIDTH, rocketExplosion.countY * RocketExplosion.FRAME_HEIGHT, RocketExplosion.FRAME_WIDTH, RocketExplosion.FRAME_HEIGHT, rocketExplosion.x, rocketExplosion.y, RocketExplosion.FRAME_WIDTH * rocketExplosion.scale,
                          RocketExplosion.FRAME_HEIGHT * rocketExplosion.scale);
        }
        rocketExplosions.removeAll(rocketExplosionsToRemove);

        // Draw EnemyRocketExplosions
        for (EnemyRocketExplosion enemyRocketExplosion : enemyRocketExplosions) {
            enemyRocketExplosion.update();
            ctx.drawImage(level.getEnemyRocketExplosionImg(), enemyRocketExplosion.countX * EnemyRocketExplosion.FRAME_WIDTH, enemyRocketExplosion.countY * EnemyRocketExplosion.FRAME_HEIGHT, EnemyRocketExplosion.FRAME_WIDTH, EnemyRocketExplosion.FRAME_HEIGHT, enemyRocketExplosion.x, enemyRocketExplosion.y, EnemyRocketExplosion.FRAME_WIDTH * enemyRocketExplosion.scale,
                          EnemyRocketExplosion.FRAME_HEIGHT * enemyRocketExplosion.scale);
        }
        enemyRocketExplosions.removeAll(enemyRocketExplosionsToRemove);

        // Draw EnemyBossExplosions
        for (EnemyBossExplosion enemyBossExplosion : enemyBossExplosions) {
            enemyBossExplosion.update();
            ctx.drawImage(level.getEnemyBossExplosionImg(), enemyBossExplosion.countX * EnemyBossExplosion.FRAME_WIDTH, enemyBossExplosion.countY * EnemyBossExplosion.FRAME_HEIGHT,
                          EnemyBossExplosion.FRAME_WIDTH, EnemyBossExplosion.FRAME_HEIGHT, enemyBossExplosion.x, enemyBossExplosion.y,
                          EnemyBossExplosion.FRAME_WIDTH * enemyBossExplosion.scale, EnemyBossExplosion.FRAME_HEIGHT * enemyBossExplosion.scale);
        }
        enemyBossExplosions.removeAll(enemyBossExplosionsToRemove);

        // Draw LevelBossExplosions
        for (LevelBossExplosion levelBossExplosion : levelBossExplosions) {
            levelBossExplosion.update();
            ctx.drawImage(level.getLevelBossExplosionImg(), levelBossExplosion.countX * LevelBossExplosion.FRAME_WIDTH, levelBossExplosion.countY * LevelBossExplosion.FRAME_HEIGHT,
                          LevelBossExplosion.FRAME_WIDTH, LevelBossExplosion.FRAME_HEIGHT, levelBossExplosion.x, levelBossExplosion.y,
                          LevelBossExplosion.FRAME_WIDTH * levelBossExplosion.scale, LevelBossExplosion.FRAME_HEIGHT * levelBossExplosion.scale);
        }
        levelBossExplosions.removeAll(levelBossExplosionsToRemove);

        // Draw UpExplosions
        for (UpExplosion upExplosion : upExplosions) {
            upExplosion.update();
            ctx.drawImage(upExplosionImg, upExplosion.countX * UpExplosion.FRAME_WIDTH, upExplosion.countY * UpExplosion.FRAME_HEIGHT, UpExplosion.FRAME_WIDTH, UpExplosion.FRAME_HEIGHT, upExplosion.x, upExplosion.y,
                          UpExplosion.FRAME_WIDTH * upExplosion.scale, UpExplosion.FRAME_HEIGHT * upExplosion.scale);
        }
        upExplosions.removeAll(upExplosionsToRemove);

        // Draw Hits
        for (Hit hit : hits) {
            hit.update();
            ctx.drawImage(hitImg, hit.countX * Hit.FRAME_WIDTH, hit.countY * Hit.FRAME_HEIGHT, Hit.FRAME_WIDTH, Hit.FRAME_HEIGHT, hit.x, hit.y, Hit.FRAME_WIDTH, Hit.FRAME_HEIGHT);
        }
        hits.removeAll(hitsToRemove);

        // Draw EnemyBoss Hits
        for (EnemyHit hit : enemyHits) {
            hit.update();
            ctx.drawImage(level.getEnemyBossHitImg(), hit.countX * EnemyHit.FRAME_WIDTH, hit.countY * EnemyHit.FRAME_HEIGHT, EnemyHit.FRAME_WIDTH, EnemyHit.FRAME_HEIGHT, hit.x, hit.y, EnemyHit.FRAME_WIDTH, EnemyHit.FRAME_HEIGHT);
        }
        enemyHits.removeAll(enemyHitsToRemove);

        // Draw Spaceship, score, lifes and shields
        if (noOfLifes > 0) {
            // Draw Spaceship or it's explosion
            if (hasBeenHit) {
                spaceShipExplosion.update();
                ctx.drawImage(spaceShipExplosionImg, spaceShipExplosion.countX * spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.countY * spaceShipExplosion.FRAME_HEIGHT,
                              spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.FRAME_HEIGHT, spaceShip.x - spaceShipExplosion.FRAME_CENTER, spaceShip.y - spaceShipExplosion.FRAME_CENTER,
                              spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.FRAME_HEIGHT);
                spaceShip.respawn();
            } else {
                // Draw space ship
                spaceShip.update();

                ctx.save();
                ctx.setGlobalAlpha(spaceShip.isVulnerable ? 1.0 : 0.5);
                if (spaceShip.vY < 0) {
                    ctx.drawImage(spaceshipUpImg,spaceShip.x - spaceShip.radius, spaceShip.y - spaceShip.radius);
                } else if (spaceShip.vY > 0) {
                    ctx.drawImage(spaceshipDownImg,spaceShip.x - spaceShip.radius, spaceShip.y - spaceShip.radius);
                } else {
                    ctx.drawImage(spaceshipImg,spaceShip.x - spaceShip.radius, spaceShip.y - spaceShip.radius);
                }

                ctx.restore();

                if (spaceShip.shield) {
                    long delta = System.currentTimeMillis() - lastShieldActivated;
                    if (delta > DEFLECTOR_SHIELD_TIME) {
                        spaceShip.shield = false;
                        noOfShields--;
                    } else {
                        ctx.setStroke(SPACEFX_COLOR);
                        ctx.setFill(SPACEFX_COLOR);
                        ctx.strokeRect(SHIELD_INDICATOR_X, SHIELD_INDICATOR_Y, SHIELD_INDICATOR_WIDTH, SHIELD_INDICATOR_HEIGHT);
                        ctx.fillRect(SHIELD_INDICATOR_X, SHIELD_INDICATOR_Y, SHIELD_INDICATOR_WIDTH - SHIELD_INDICATOR_WIDTH * delta / DEFLECTOR_SHIELD_TIME,
                                     SHIELD_INDICATOR_HEIGHT);
                        ctx.setGlobalAlpha(RND.nextDouble() * 0.5 + 0.1);
                        ctx.drawImage(deflectorShieldImg, spaceShip.x - deflectorShieldRadius, spaceShip.y - deflectorShieldRadius);
                        ctx.setGlobalAlpha(1);
                    }
                }
            }

            // Draw score
            ctx.setFill(SPACEFX_COLOR);
            ctx.setFont(scoreFont);
            ctx.fillText(Long.toString(score), scorePosX, scorePosY);

            // Draw lifes
            for (int i = 0 ; i < noOfLifes ; i++) {
                ctx.drawImage(miniSpaceshipImg, i * miniSpaceshipImg.getWidth() + 10, 20);
            }

            // Draw shields
            for (int i = 0 ; i < noOfShields ; i++) {
                ctx.drawImage(miniDeflectorShieldImg, WIDTH - i * (miniDeflectorShieldImg.getWidth() + 5), 20);
            }
        }

        // Draw Buttons
        if (SHOW_BUTTONS) {
            ctx.drawImage(torpedoButtonImg, TORPEDO_BUTTON_X, TORPEDO_BUTTON_Y);
            ctx.drawImage(rocketButtonImg, ROCKET_BUTTON_X, ROCKET_BUTTON_Y);
            ctx.drawImage(shieldButtonImg, SHIELD_BUTTON_X, SHIELD_BUTTON_Y);
        }
    }


    // Spawn different objects
    private void spawnTorpedo(final double x, final double y) {
        torpedos.add(new Torpedo(torpedoImg, x, y));
        playSound(laserSound);
    }

    private void spawnRocket(final double x, final double y) {
        rockets.add(new Rocket(rocketImg, x, y));
        playSound(rocketLaunchSound);
    }

    private void spawnEnemyTorpedo(final double x, final double y, final double vX, final double vY) {
        double vFactor = ENEMY_TORPEDO_SPEED / Math.abs(vY); // make sure the speed is always the defined one
        enemyTorpedos.add(new EnemyTorpedo(level.getEnemyTorpedoImg(), x, y, vFactor * vX, vFactor * vY));
        playSound(enemyLaserSound);
    }

    private void spawnEnemyBomb(final double x, final double y) {
        enemyBombs.add(new EnemyBomb(level.getEnemyBombImg(), x, y, 0, ENEMY_BOMB_SPEED));
        playSound(enemyBombSound);
    }

    private void spawnEnemyBoss(final SpaceShip spaceShip) {
        if (levelBossActive || !SHOW_ENEMY_BOSS) { return; }
        enemyBosses.add(new EnemyBoss(spaceShip, level.getEnemyBossImg4(), RND.nextBoolean()));
    }

    private void spawnLevelBoss(final SpaceShip spaceShip) {
        if (levelBossActive) { return; }
        levelBossActive = true;
        levelBosses.add(new LevelBoss(spaceShip, level.getLevelBossImg(), true, true));
    }

    private void spawnShieldUp() {
        shieldUps.add(new ShieldUp(shieldUpImg));
    }

    private void spawnLifeUp() {
        lifeUps.add(new LifeUp(lifeUpImg));
    }

    private void spawnWave() {
        switch (level.getDifficulty()) {
            case EASY:
                if (levelKills < NO_OF_KILLS_STAGE_1 && !levelBossActive) {
                    waves.add(new Wave(WAVE_TYPES_SLOW[RND.nextInt(WAVE_TYPES_SLOW.length)], spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], false, false));
                } else if (levelKills >= NO_OF_KILLS_STAGE_1 && levelKills < NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_MEDIUM[RND.nextInt(WAVE_TYPES_MEDIUM.length)], spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_SLOW, WaveType.TYPE_11_SLOW, spaceShip, 10, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], false, false));
                    }

                } else if (levelKills >= NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    spawnLevelBoss(spaceShip);
                } else if (!levelBossActive) {
                    waves.add(new Wave(WAVE_TYPES_MEDIUM[RND.nextInt(WAVE_TYPES_MEDIUM.length)], spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                }
                break;
            case NORMAL:
                if (levelKills < NO_OF_KILLS_STAGE_1 && !levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_MEDIUM[RND.nextInt(WAVE_TYPES_MEDIUM.length)], spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], false, false));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_MEDIUM, WaveType.TYPE_11_MEDIUM, spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], false, false));
                    }
                } else if (levelKills >= NO_OF_KILLS_STAGE_1 && levelKills < NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_FAST[RND.nextInt(WAVE_TYPES_FAST.length)], spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_FAST, WaveType.TYPE_11_FAST, spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    }
                } else if (levelKills >= NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    spawnLevelBoss(spaceShip);
                } else if (!levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_FAST[RND.nextInt(WAVE_TYPES_FAST.length)], spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_FAST, WaveType.TYPE_11_FAST, spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    }
                }
                break;
            case HARD:
                if (levelKills < NO_OF_KILLS_STAGE_1 && !levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_FAST[RND.nextInt(WAVE_TYPES_FAST.length)], spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_FAST, WaveType.TYPE_11_FAST, spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    }
                } else if (levelKills >= NO_OF_KILLS_STAGE_1 && levelKills < NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_FAST[RND.nextInt(WAVE_TYPES_FAST.length)], spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_FAST, WaveType.TYPE_11_FAST, spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                    }
                } else if (levelKills >= NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    spawnLevelBoss(spaceShip);
                } else if (!levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_FAST[RND.nextInt(WAVE_TYPES_FAST.length)], spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_FAST, WaveType.TYPE_11_FAST, spaceShip, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                    }
                }
                break;
        }
    }

    private void spawnEnemyBossTorpedo(final double x, final double y, final double vX, final double vY) {
        double vFactor = ENEMY_BOSS_TORPEDO_SPEED / Math.abs(vY); // make sure the speed is always the defined one
        enemyBossTorpedos.add(new EnemyBossTorpedo(level.getEnemyBossTorpedoImg(), x, y, vFactor * vX, vFactor * vY));
        playSound(enemyLaserSound);
    }

    private void spawnEnemyBossRocket(final double x, final double y) {
        enemyBossRockets.add(new EnemyBossRocket(spaceShip, level.getEnemyBossRocketImg(), x, y));
        playSound(rocketLaunchSound);
    }

    private void spawnLevelBossTorpedo(final double x, final double y, final double vX, final double vY, final double r) {
        double vFactor = LEVEL_BOSS_TORPEDO_SPEED / Math.abs(vY); // make sure the speed is always the defined one
        levelBossTorpedos.add(new LevelBossTorpedo(level.getLevelBossTorpedoImg(), x, y, vFactor * vX, vFactor * vY, r));
        playSound(levelBossTorpedoSound);
    }

    private void spawnLevelBossRocket(final double x, final double y) {
        levelBossRockets.add(new LevelBossRocket(spaceShip, level.getLevelBossRocketImg(), x, y));
        playSound(levelBossRocketSound);
    }

    private void spawnLevelBossBomb(final double x, final double y) {
        levelBossBombs.add(new LevelBossBomb(level.getLevelBossBombImg(), x, y, 0, LEVEL_BOSS_BOMB_SPEED));
        playSound(levelBossBombSound);
    }

    // Hit test
    private boolean isHitCircleCircle(final double c1X, final double c1Y, final double c1R, final double c2X, final double c2Y, final double c2R) {
        double distX    = c1X - c2X;
        double distY    = c1Y - c2Y;
        double distance = Math.sqrt((distX * distX) + (distY * distY));
        return (distance <= c1R + c2R);
    }


    // Game Over
    private void gameOver() {
        timer.stop();
        running = false;
        gameOverScreen = true;
        if (PLAY_MUSIC) {
            gameMediaPlayer.pause();
        }

        boolean isInHallOfFame = score > hallOfFame.get(2).score;

        PauseTransition pauseBeforeGameOverScreen = new PauseTransition(Duration.millis(1000));
        pauseBeforeGameOverScreen.setOnFinished(e -> {
            ctx.clearRect(0, 0, WIDTH, HEIGHT);
            ctx.drawImage(gameOverImg, 0, 0, WIDTH, HEIGHT);
            ctx.setFill(SPACEFX_COLOR);
            ctx.setFont(spaceBoy(60 * SCALING_FACTOR));
            ctx.fillText(Long.toString(score), scorePosX, HEIGHT * 0.25);
            playSound(gameoverSound);
        });
        pauseBeforeGameOverScreen.play();

        if (isInHallOfFame) {
            PauseTransition pauseInGameOverScreen = new PauseTransition(Duration.millis(5000));
            pauseInGameOverScreen.setOnFinished(e -> {
                // Add player to hall of fame
                ctx.clearRect(0, 0, WIDTH, HEIGHT);
                ctx.drawImage(hallOfFameImg, 0, 0);

                Helper.enableNode(hallOfFameBox, true);
                Helper.enableNode(playerInitialsLabel, true);
                Helper.enableNode(playerInitials, true);
                Platform.runLater(() -> {
                    playerInitials.requestFocus();
                    playerInitials.selectAll();
                });
            });
            pauseInGameOverScreen.play();
        } else {
            // Back to StartScreen
            PauseTransition pauseInGameOverScreen = new PauseTransition(Duration.millis(5000));
            pauseInGameOverScreen.setOnFinished(a -> reInitGame());
            pauseInGameOverScreen.play();
        }
    }


    // Reinitialize game
    private void reInitGame() {
        ctx.clearRect(0, 0, WIDTH, HEIGHT);
        ctx.drawImage(startImg, 0, 0);

        Helper.enableNode(hallOfFameBox, false);
        gameOverScreen = false;
        explosions.clear();
        torpedos.clear();
        enemyTorpedos.clear();
        enemyBombs.clear();
        enemyBossTorpedos.clear();
        enemyBossRockets.clear();
        enemyBosses.clear();
        levelBosses.clear();
        levelBossTorpedos.clear();
        levelBossRockets.clear();
        levelBossBombs.clear();
        shieldUps.clear();
        lifeUps.clear();
        waves.clear();
        initAsteroids();
        spaceShip.init();
        hasBeenHit = false;
        noOfLifes = LIFES;
        noOfShields = SHIELDS;
        score = 0;
        kills = 0;
        levelKills = 0;
        if (PLAY_MUSIC) {
            mediaPlayer.play();
        }

        screenTimer.start();
    }


    // Create Hall of Fame entry
    private HBox createHallOfFameEntry(final Player player) {
        Font hofFont  = spaceBoy(30 * SCALING_FACTOR);

        Label playerName  = new Label(player.name);
        playerName.setFont(hofFont);
        playerName.setTextFill(SPACEFX_COLOR);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label playerScore = new Label(Long.toString(player.score));
        playerScore.setFont(hofFont);
        playerScore.setTextFill(SPACEFX_COLOR);
        playerScore.setAlignment(Pos.CENTER_RIGHT);

        return new HBox(20, playerName, spacer, playerScore);
    }


    // Play audio clips in a separate thread
    private void playSound(final AudioClip audioClip) {
        if (PLAY_SOUND) {
            audioClip.play();
        }
    }


    // Font definition
    private static Font spaceBoy(final double size) { return new Font(SPACE_BOY, size); }

    private void nextLevel() {
        if (LEVEL_3.equals(level)) {
            level = LEVEL_1;
            return;
        } else if (LEVEL_2.equals(level)) {
            level = LEVEL_3;
            return;
        } else if (LEVEL_1.equals(level)) {
            level = LEVEL_2;
            return;
        }
    }


    // ******************** Misc **********************************************
    public static void main(String[] args) {
        launch(args);
    }


    // ******************** Space Object Classes ******************************
    private class Star {
        private final Random rnd        = new Random();
        private final double xVariation = 0;
        private final double minSpeedY  = 4;
        private       double x;
        private       double y;
        private       double size;
        private       double vX;
        private       double vY;
        private       double vYVariation;


        public Star() {
            // Random size
            size = rnd.nextInt(2) + 1;

            // Position
            x = (int)(rnd.nextDouble() * WIDTH);
            y = -size;

            // Random Speed
            vYVariation = (rnd.nextDouble() * 0.5) + 0.2;

            // Velocity
            vX = (int) (Math.round((rnd.nextDouble() * xVariation) - xVariation * 0.5));
            vY = (int) (Math.round(((rnd.nextDouble() * 1.5) + minSpeedY) * vYVariation));
        }


        private void respawn() {
            x = (int) (RND.nextDouble() * WIDTH);
            y = -size;
        }

        private void update() {
            x += vX;
            y += vY;

            // Respawn star
            if(y > HEIGHT + size) {
                respawn();
            }
        }
    }

    private class Asteroid {
        private static final int     MAX_VALUE      = 10;
        private final        Random  rnd            = new Random();
        private final        double  xVariation     = 2;
        private final        double  minSpeedY      = 2;
        private final        double  minRotationR   = 0.1;
        private              Image   image;
        private              double  x;
        private              double  y;
        private              double  width;
        private              double  height;
        private              double  size;
        private              double  imgCenterX;
        private              double  imgCenterY;
        private              double  radius;
        private              double  cX;
        private              double  cY;
        private              double  rot;
        private              double  vX;
        private              double  vY;
        private              double  vR;
        private              boolean rotateRight;
        private              double  scale;
        private              double  vYVariation;
        private              int     value;
        private              int     hits;


        public Asteroid(final Image image) {
            // Image
            this.image = image;
            init();
        }


        private void init() {
            // Position
            x   = rnd.nextDouble() * WIDTH;
            y   = -image.getHeight();
            rot = 0;

            // Random Size
            scale = (rnd.nextDouble() * 0.4) + 0.4;

            // No of hits (0.2 - 0.8)
            hits = (int) (scale * 5.0);

            // Value
            value = (int) (1 / scale * MAX_VALUE);

            // Random Speed
            vYVariation = (rnd.nextDouble() * 0.5) + 0.2;

            width      = image.getWidth() * scale;
            height     = image.getHeight() * scale;
            size       = width > height ? width : height;
            radius     = size * 0.5;
            imgCenterX = image.getWidth() * 0.5;
            imgCenterY = image.getHeight() * 0.5;

            // Velocity
            vX          = ((rnd.nextDouble() * xVariation) - xVariation * 0.5) * VELOCITY_FACTOR_X;
            vY          = (((rnd.nextDouble() * 1.5) + minSpeedY * 1/scale) * vYVariation) * VELOCITY_FACTOR_Y;
            vR          = ((rnd.nextDouble() * 0.5) + minRotationR) * VELOCITY_FACTOR_R;
            rotateRight = rnd.nextBoolean();
        }

        private void respawn() {
            this.image = asteroidImages[RND.nextInt(asteroidImages.length)];
            init();
        }

        private void update() {
            x += vX;
            y += vY;

            cX = x + imgCenterX;
            cY = y + imgCenterY;

            if (rotateRight) {
                rot += vR;
                if (rot > 360) { rot = 0; }
            } else {
                rot -= vR;
                if (rot < 0) { rot = 360; }
            }

            // Respawn asteroid
            if(x < -size || x - radius > WIDTH || y - height > HEIGHT) {
                respawn();
            }
        }
    }

    private class SpaceShip {
        private static final long INVULNERABLE_TIME = 3_000_000_000l;
        private final Image     image;
        private final Image     imageUp;
        private final Image     imageDown;
        private       long      born;
        private       double    x;
        private       double    y;
        private       double    size;
        private       double    radius;
        private       double    width;
        private       double    height;
        private       double    vX;
        private       double    vY;
        private       boolean   shield;
        public        boolean   isVulnerable;


        public SpaceShip(final Image image, final Image imageUp, final Image imageDown) {
            this.image     = image;
            this.imageUp   = imageUp;
            this.imageDown = imageDown;
            init();
        }


        public void init() {
            this.born         = System.nanoTime();
            this.x            = WIDTH * 0.5;
            this.y            = HEIGHT - 2 * image.getHeight();
            this.width        = image.getWidth();
            this.height       = image.getHeight();
            this.size         = width > height ? width : height;
            this.radius       = size * 0.5;
            this.vX           = 0;
            this.vY           = 0;
            this.shield       = false;
            this.isVulnerable = false;
        }

        public void respawn() {
            this.vX           = 0;
            this.vY           = 0;
            this.shield       = false;
            this.born         = System.nanoTime();
            this.isVulnerable = false;
        }

        private void update() {
            if (!isVulnerable && System.nanoTime() - born > INVULNERABLE_TIME) {
                isVulnerable = true;
            }
            x += vX;
            y += vY;
            if (x + width * 0.5 > WIDTH) {
                x = WIDTH - width * 0.5;
            }
            if (x - width * 0.5 < 0) {
                x = width * 0.5;
            }
            if (y + height * 0.5 > HEIGHT) {
                y = HEIGHT - height * 0.5;
            }
            if (y - height * 0.5 < 0) {
                y = height * 0.5;
            }
        }
    }

    private class Torpedo {
        private final Image  image;
        private       double x;
        private       double y;
        private       double width;
        private       double height;
        private       double size;
        private       double radius;
        private       double vX;
        private       double vY;


        public Torpedo(final Image image, final double x, final double y) {
            this.image  = image;
            this.x      = x;
            this.y      = y - image.getHeight();
            this.width  = image.getWidth();
            this.height = image.getHeight();
            this.size   = width > height ? width : height;
            this.radius = size * 0.5;
            this.vX     = 0;
            this.vY     = TORPEDO_SPEED;
        }


        private void update() {
            y -= vY;
            if (y < -size) {
                torpedosToRemove.add(Torpedo.this);
            }
        }
    }

    private class Rocket {
        private final Image  image;
        private       double x;
        private       double y;
        private       double width;
        private       double height;
        private       double halfWidth;
        private       double halfHeight;
        private       double size;
        private       double radius;
        private       double vX;
        private       double vY;


        public Rocket(final Image image, final double x, final double y) {
            this.image = image;
            this.x = x;
            this.y = y - image.getHeight();
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.halfWidth = width * 0.5;
            this.halfHeight = height * 0.5;
            this.size = width > height ? width : height;
            this.radius = size * 0.5;
            this.vX = 0;
            this.vY = ROCKET_SPEED;
        }


        private void update() {
            y -= vY;
            if (y < -size) {
                rocketsToRemove.add(Rocket.this);
            }
        }
    }

    private class EnemyRocketExplosion {
        private static final double FRAME_WIDTH  = 128 * SCALING_FACTOR;
        private static final double FRAME_HEIGHT = 128 * SCALING_FACTOR;
        private static final double FRAME_CENTER = FRAME_WIDTH * 0.5;
        private static final int    MAX_FRAME_X  = 4;
        private static final int    MAX_FRAME_Y  = 7;
        private              double x;
        private              double y;
        private              double vX;
        private              double vY;
        private              double scale;
        private              int    countX;
        private              int    countY;


        public EnemyRocketExplosion(final double x, final double y, final double vX, final double vY, final double scale) {
            this.x = x;
            this.y = y;
            this.vX = vX;
            this.vY = vY;
            this.scale = scale;
            this.countX = 0;
            this.countY = 0;
        }


        private void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == MAX_FRAME_X) {
                countY++;
                if (countX == MAX_FRAME_X && countY == MAX_FRAME_Y) {
                    enemyRocketExplosionsToRemove.add(EnemyRocketExplosion.this);
                }
                countX = 0;
                if (countY == MAX_FRAME_Y) {
                    countY = 0;
                }
            }
        }
    }

    private class AsteroidExplosion {
        private static final double FRAME_WIDTH  = 256 * SCALING_FACTOR;
        private static final double FRAME_HEIGHT = 256 * SCALING_FACTOR;
        private static final double FRAME_CENTER = FRAME_WIDTH * 0.5;
        private static final int    MAX_FRAME_X  = 8;
        private static final int    MAX_FRAME_Y  = 7;
        private              double x;
        private              double y;
        private              double vX;
        private              double vY;
        private              double scale;
        private              int    countX;
        private              int    countY;


        public AsteroidExplosion(final double x, final double y, final double vX, final double vY, final double scale) {
            this.x = x;
            this.y = y;
            this.vX = vX;
            this.vY = vY;
            this.scale = scale;
            this.countX = 0;
            this.countY = 0;
        }


        private void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == MAX_FRAME_X) {
                countY++;
                if (countX == MAX_FRAME_X && countY == MAX_FRAME_Y) {
                    asteroidExplosionsToRemove.add(AsteroidExplosion.this);
                }
                countX = 0;
                if (countY == MAX_FRAME_Y) {
                    countY = 0;
                }
            }
        }
    }

    private class Explosion {
        private static final double FRAME_WIDTH  = 256 * SCALING_FACTOR;
        private static final double FRAME_HEIGHT = 256 * SCALING_FACTOR;
        private static final double FRAME_CENTER = FRAME_WIDTH * 0.5;
        private static final int    MAX_FRAME_X  = 8 ;
        private static final int    MAX_FRAME_Y  = 7;
        private              double x;
        private              double y;
        private              double vX;
        private              double vY;
        private              double scale;
        private              int    countX;
        private              int    countY;


        public Explosion(final double x, final double y, final double vX, final double vY, final double scale) {
            this.x = x;
            this.y = y;
            this.vX = vX;
            this.vY = vY;
            this.scale = scale;
            this.countX = 0;
            this.countY = 0;
        }


        private void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == MAX_FRAME_X) {
                countY++;
                if (countX == MAX_FRAME_X && countY == MAX_FRAME_Y) {
                    explosionsToRemove.add(Explosion.this);
                }
                countX = 0;
                if (countY == MAX_FRAME_Y) {
                    countY = 0;
                }
            }
        }
    }

    private class UpExplosion {
        private static final double FRAME_WIDTH  = 100 * SCALING_FACTOR;
        private static final double FRAME_HEIGHT = 100 * SCALING_FACTOR;
        private static final double FRAME_CENTER = FRAME_WIDTH * 0.5;
        private static final int    MAX_FRAME_X  = 4;
        private static final int    MAX_FRAME_Y  = 7;
        private              double x;
        private              double y;
        private              double vX;
        private              double vY;
        private              double scale;
        private              int    countX;
        private              int    countY;


        public UpExplosion(final double x, final double y, final double vX, final double vY, final double scale) {
            this.x      = x;
            this.y      = y;
            this.vX     = vX;
            this.vY     = vY;
            this.scale  = scale;
            this.countX = 0;
            this.countY = 0;
        }


        private void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == MAX_FRAME_X) {
                countY++;
                if (countX == MAX_FRAME_X && countY == MAX_FRAME_Y) {
                    upExplosionsToRemove.add(UpExplosion.this);
                }
                countX = 0;
                if (countY == MAX_FRAME_Y) {
                    countY = 0;
                }
            }
        }
    }

    private class SpaceShipExplosion {
        private static final double FRAME_WIDTH  = 100 * SCALING_FACTOR;
        private static final double FRAME_HEIGHT = 100 * SCALING_FACTOR;
        private static final double FRAME_CENTER = FRAME_WIDTH * 0.5;
        private static final int    MAX_FRAME_X  = 8;
        private static final int    MAX_FRAME_Y  = 6;
        private              double x;
        private              double y;
        private              int    countX;
        private              int    countY;


        public SpaceShipExplosion(final double x, final double y) {
            this.x = x;
            this.y = y;
            this.countX = 0;
            this.countY = 0;
        }


        private void update() {
            countX++;
            if (countX == MAX_FRAME_X) {
                countX = 0;
                countY++;
                if (countY == MAX_FRAME_Y) {
                    countY = 0;
                }
                if (countX == 0 && countY == 0) {
                    hasBeenHit = false;
                    spaceShip.x = WIDTH * 0.5;
                    spaceShip.y = HEIGHT - 2 * spaceShip.height;
                }
            }
        }
    }

    private class RocketExplosion {
        private static final double FRAME_WIDTH  = 192 * SCALING_FACTOR;
        private static final double FRAME_HEIGHT = 192 * SCALING_FACTOR;
        private static final double FRAME_CENTER = FRAME_WIDTH * 0.5;
        private static final int    MAX_FRAME_X  = 5;
        private static final int    MAX_FRAME_Y  = 4;
        private              double x;
        private              double y;
        private              double vX;
        private              double vY;
        private              double scale;
        private              int    countX;
        private              int    countY;


        public RocketExplosion(final double x, final double y, final double vX, final double vY, final double scale) {
            this.x      = x;
            this.y      = y;
            this.vX     = vX;
            this.vY     = vY;
            this.scale  = scale;
            this.countX = 0;
            this.countY = 0;
        }


        private void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == MAX_FRAME_X) {
                countY++;
                if (countX == MAX_FRAME_X && countY == MAX_FRAME_Y) {
                    rocketExplosionsToRemove.add(RocketExplosion.this);
                }
                countX = 0;
                if (countY == MAX_FRAME_Y) {
                    countY = 0;
                }
            }
        }
    }

    private class Hit {
        private static final double FRAME_WIDTH  = 80 * SCALING_FACTOR;
        private static final double FRAME_HEIGHT = 80 * SCALING_FACTOR;
        private static final double FRAME_CENTER = FRAME_WIDTH * 0.5;
        private static final int    MAX_FRAME_X  = 5;
        private static final int    MAX_FRAME_Y  = 2;
        private              double x;
        private              double y;
        private              double vX;
        private              double vY;
        private              int    countX;
        private              int    countY;


        public Hit(final double x, final double y, final double vX, final double vY) {
            this.x = x;
            this.y = y;
            this.vX = vX;
            this.vY = vY;
            this.countX = 0;
            this.countY = 0;
        }


        private void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == MAX_FRAME_X) {
                countY++;
                if (countX == MAX_FRAME_X && countY == MAX_FRAME_Y) {
                    hitsToRemove.add(Hit.this);
                }
                countX = 0;
                if (countY == MAX_FRAME_Y) {
                    countY = 0;
                }
            }
        }
    }

    private class EnemyHit {
        private static final double FRAME_WIDTH  = 80 * SCALING_FACTOR;
        private static final double FRAME_HEIGHT = 80 * SCALING_FACTOR;
        private static final double FRAME_CENTER = FRAME_WIDTH * 0.5;
        private static final int    MAX_FRAME_X  = 5;
        private static final int    MAX_FRAME_Y  = 2;
        private              double x;
        private              double y;
        private              double vX;
        private              double vY;
        private              int    countX;
        private              int    countY;


        public EnemyHit(final double x, final double y, final double vX, final double vY) {
            this.x = x;
            this.y = y;
            this.vX = vX;
            this.vY = vY;
            this.countX = 0;
            this.countY = 0;
        }


        private void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == MAX_FRAME_X) {
                countY++;
                if (countX == MAX_FRAME_X && countY == MAX_FRAME_Y) {
                    enemyHitsToRemove.add(EnemyHit.this);
                }
                countX = 0;
                if (countY == MAX_FRAME_Y) {
                    countY = 0;
                }
            }
        }
    }

    private class ShieldUp {
        private final Random  rnd          = new Random();
        private final double  xVariation   = 2;
        private final double  minSpeedY    = 2;
        private final double  minRotationR = 0.1;
        private       Image   image;
        private       double  x;
        private       double  y;
        private       double  width;
        private       double  height;
        private       double  size;
        private       double  imgCenterX;
        private       double  imgCenterY;
        private       double  radius;
        private       double  cX;
        private       double  cY;
        private       double  rot;
        private       double  vX;
        private       double  vY;
        private       double  vR;
        private       boolean rotateRight;
        private       double  vYVariation;


        public ShieldUp(final Image image) {
            // Image
            this.image = image;
            init();
        }


        private void init() {
            // Position
            x = rnd.nextDouble() * WIDTH;
            y = -image.getHeight();
            rot = 0;

            // Random Speed
            vYVariation = (rnd.nextDouble() * 0.5) + 0.2;

            width = image.getWidth();
            height = image.getHeight();
            size = width > height ? width : height;
            radius = size * 0.5;
            imgCenterX = image.getWidth() * 0.5;
            imgCenterY = image.getHeight() * 0.5;

            // Velocity
            if (x < FIRST_QUARTER_WIDTH) {
                vX = rnd.nextDouble() * VELOCITY_FACTOR_X;
            } else if (x > LAST_QUARTER_WIDTH) {
                vX = -rnd.nextDouble() * VELOCITY_FACTOR_X;
            } else {
                vX = ((rnd.nextDouble() * xVariation) - xVariation * 0.5) * VELOCITY_FACTOR_X;
            }
            vY = (((rnd.nextDouble() * 1.5) + minSpeedY) * vYVariation) * VELOCITY_FACTOR_Y;
            vR = (((rnd.nextDouble()) * 0.5) + minRotationR) * VELOCITY_FACTOR_R;
            rotateRight = rnd.nextBoolean();
        }

        private void update() {
            x += vX;
            y += vY;

            cX = x + imgCenterX;
            cY = y + imgCenterY;

            if (rotateRight) {
                rot += vR;
                if (rot > 360) { rot = 0; }
            } else {
                rot -= vR;
                if (rot < 0) { rot = 360; }
            }

            // Remove shieldUp
            if (x < -size || x - radius > WIDTH || y - height > HEIGHT) {
                shieldUpsToRemove.add(ShieldUp.this);
            }
        }
    }

    private class LifeUp {
        private final Random  rnd          = new Random();
        private final double  xVariation   = 2;
        private final double  minSpeedY    = 2;
        private final double  minRotationR = 0.1;
        private       Image   image;
        private       double  x;
        private       double  y;
        private       double  width;
        private       double  height;
        private       double  size;
        private       double  imgCenterX;
        private       double  imgCenterY;
        private       double  radius;
        private       double  cX;
        private       double  cY;
        private       double  rot;
        private       double  vX;
        private       double  vY;
        private       double  vR;
        private       boolean rotateRight;
        private       double  vYVariation;


        public LifeUp(final Image image) {
            // Image
            this.image = image;
            init();
        }


        private void init() {
            // Position
            x = rnd.nextDouble() * WIDTH;
            y = -image.getHeight();
            rot = 0;

            // Random Speed
            vYVariation = (rnd.nextDouble() * 0.5) + 0.2;

            width = image.getWidth();
            height = image.getHeight();
            size = width > height ? width : height;
            radius = size * 0.5;
            imgCenterX = image.getWidth() * 0.5;
            imgCenterY = image.getHeight() * 0.5;

            // Velocity
            if (x < FIRST_QUARTER_WIDTH) {
                vX = rnd.nextDouble() * VELOCITY_FACTOR_X;
            } else if (x > LAST_QUARTER_WIDTH) {
                vX = -rnd.nextDouble() * VELOCITY_FACTOR_X;
            } else {
                vX = ((rnd.nextDouble() * xVariation) - xVariation * 0.5) * VELOCITY_FACTOR_X;
            }
            vY = (((rnd.nextDouble() * 1.5) + minSpeedY) * vYVariation) * VELOCITY_FACTOR_Y;
            vR = (((rnd.nextDouble()) * 0.5) + minRotationR) * VELOCITY_FACTOR_R;
            rotateRight = rnd.nextBoolean();
        }

        private void update() {
            x += vX;
            y += vY;

            cX = x + imgCenterX;
            cY = y + imgCenterY;

            if (rotateRight) {
                rot += vR;
                if (rot > 360) { rot = 0; }
            } else {
                rot -= vR;
                if (rot < 0) { rot = 360; }
            }

            // Remove lifeUp
            if (x < -size || x - radius > WIDTH || y - height > HEIGHT) {
                lifeUpsToRemove.add(LifeUp.this);
            }
        }
    }

    private class Enemy {
        public  static final long      TIME_BETWEEN_SHOTS  = 500_000_000l;
        public  static final long      TIME_BETWEEN_BOMBS  = 1_000_000_000l;
        public  static final double    HALF_ANGLE_OF_SIGHT = 5;
        private static final double    BOMB_RANGE          = 10;
        private static final int       MAX_VALUE           = 50;
        private final        Random    rnd                 = new Random();
        private final        WaveType  waveType;
        private              int       frameCounter;
        private              SpaceShip spaceShip;
        public               Image     image;
        public               boolean   canFire;
        public               boolean   canBomb;
        private              int       noOfBombs;
        private              double    oldX;
        private              double    oldY;
        public               double    x;
        public               double    y;
        public               double    r;
        public               double    width;
        public               double    height;
        private              double    size;
        public               double    radius;
        public               double    vX;
        public               double    vY;
        public               int       value;
        public               long      lastShot;
        public               long      lastBomb;
        public               boolean   toBeRemoved;


        public Enemy(final WaveType waveType, final SpaceShip spaceShip, final Image image, final boolean canFire, final boolean canBomb) {
            this.waveType     = waveType;
            this.frameCounter = 0;
            this.spaceShip    = spaceShip;
            this.image        = image;
            this.canFire      = canFire;
            this.canBomb      = canBomb;
            this.noOfBombs    = NO_OF_ENEMY_BOMBS;
            this.toBeRemoved  = false;
            init();
        }


        private void init() {
            x    = waveType.coordinates.get(0).x;
            y    = waveType.coordinates.get(0).y;
            r    = waveType.coordinates.get(0).r;
            oldX = x;
            oldY = y;

            // Value
            value = rnd.nextInt(MAX_VALUE) + 1;

            width  = image.getWidth();
            height = image.getHeight();
            size   = width > height ? width : height;
            radius = size * 0.5;

            // Velocity
            vX = 0;
            vY = 1;

            lastShot = System.nanoTime();
        }

        private void update() {
            if (toBeRemoved) { return; }
            oldX = x;
            oldY = y;
            x    = waveType.coordinates.get(frameCounter).x;
            y    = waveType.coordinates.get(frameCounter).y;
            r    = waveType.coordinates.get(frameCounter).r;
            vX   = x - oldX;
            vY   = y - oldY;

            long now = System.nanoTime();

            if (canFire) {
                if (now - lastShot > TIME_BETWEEN_SHOTS) {
                    double[] p0 = { x, y };
                    double[] p1 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, -HALF_ANGLE_OF_SIGHT);
                    double[] p2 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, HALF_ANGLE_OF_SIGHT);

                    double area = 0.5 * (-p1[1] * p2[0] + p0[1] * (-p1[0] + p2[0]) + p0[0] * (p1[1] - p2[1]) + p1[0] * p2[1]);
                    double s    = 1 / (2 * area) * (p0[1] * p2[0] - p0[0] * p2[1] + (p2[1] - p0[1]) * spaceShip.x + (p0[0] - p2[0]) * spaceShip.y);
                    double t    = 1 / (2 * area) * (p0[0] * p1[1] - p0[1] * p1[0] + (p0[1] - p1[1]) * spaceShip.x + (p1[0] - p0[0]) * spaceShip.y);
                    if (s > 0 && t > 0 && 1 - s - t > 0) {
                        spawnEnemyTorpedo(x, y, vX * 2, vY * 2);
                        lastShot = now;
                    }
                }
            }

            if (canBomb && now - lastBombDropped > BOMB_DROP_INTERVAL && noOfBombs > 0) {
                if (now - lastBomb > TIME_BETWEEN_BOMBS && spaceShip.y > y) {
                    if (spaceShip.x > x - BOMB_RANGE && spaceShip.x < x + BOMB_RANGE) {
                        spawnEnemyBomb(x, y);
                        lastBomb        = now;
                        lastBombDropped = now;
                        noOfBombs--;
                    }
                }
            }

            // Remove Enemy
            frameCounter++;
            if (frameCounter >= waveType.totalFrames) {
                toBeRemoved = true;
            }
        }
    }

    private class EnemyTorpedo {
        private final Image  image;
        private       double x;
        private       double y;
        private       double width;
        private       double height;
        private       double size;
        private       double radius;
        private       double vX;
        private       double vY;


        public EnemyTorpedo(final Image image, final double x, final double y, final double vX, final double vY) {
            this.image = image;
            this.x = x - image.getWidth() / 2.0;
            this.y = y;
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.size = width > height ? width : height;
            this.radius = size * 0.5;
            this.vX = vX;
            this.vY = vY;
        }


        private void update() {
            x += vX;
            y += vY;

            if (spaceShip.isVulnerable && !hasBeenHit) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                } else {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                }
                if (hit) {
                    enemyTorpedosToRemove.add(EnemyTorpedo.this);
                    if (spaceShip.shield) {
                        playSound(shieldHitSound);
                    } else {
                        hasBeenHit = true;
                        playSound(spaceShipExplosionSound);
                        noOfLifes--;
                        if (0 == noOfLifes) {
                            gameOver();
                        }
                    }
                }
            } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                enemyTorpedosToRemove.add(EnemyTorpedo.this);
            }
        }
    }

    private class EnemyBomb {
        private final Image  image;
        private       double x;
        private       double y;
        private       double width;
        private       double height;
        private       double size;
        private       double radius;
        private       double vX;
        private       double vY;


        public EnemyBomb(final Image image, final double x, final double y, final double vX, final double vY) {
            this.image  = image;
            this.x      = x - image.getWidth() / 2.0;
            this.y      = y;
            this.width  = image.getWidth();
            this.height = image.getHeight();
            this.size   = width > height ? width : height;
            this.radius = size * 0.5;
            this.vX     = vX;
            this.vY     = vY;
        }


        private void update() {
            x += vX;
            y += vY;

            if (spaceShip.isVulnerable && !hasBeenHit) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                } else {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                }
                if (hit) {
                    enemyBombsToRemove.add(EnemyBomb.this);
                    if (spaceShip.shield) {
                        playSound(shieldHitSound);
                    } else {
                        hasBeenHit = true;
                        playSound(spaceShipExplosionSound);
                        noOfLifes--;
                        if (0 == noOfLifes) {
                            gameOver();
                        }
                    }
                }
            } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                enemyBombsToRemove.add(EnemyBomb.this);
            }
        }
    }

    private class EnemyBoss {
        private static final int       MAX_VALUE            = 100;
        private static final long      TIME_BETWEEN_SHOTS   = 500_000_000l;
        private static final long      TIME_BETWEEN_ROCKETS = 5_000_000_000l;
        private static final double    HALF_ANGLE_OF_SIGHT  = 10;
        private final        Random    rnd                  = new Random();
        private final        SpaceShip spaceShip;
        private              Image     image;
        private              double    x;
        private              double    y;
        private              double    r;
        private              double    dX;
        private              double    dY;
        private              double    dist;
        private              double    factor;
        private              double    width;
        private              double    height;
        private              double    size;
        private              double    radius;
        private              double    vX;
        private              double    vY;
        private              int       value;
        private              int       hits;
        private              long      lastShot;
        private              long      lastRocket;
        private              boolean   hasRockets;


        public EnemyBoss(final SpaceShip spaceShip, final Image image, final boolean hasRockets) {
            this.spaceShip  = spaceShip;
            this.image      = image;
            this.hasRockets = hasRockets;
            init();
        }


        private void init() {
            // Position
            x = rnd.nextDouble() * WIDTH;
            y = -image.getHeight();

            // Value
            value = rnd.nextInt(MAX_VALUE) + 1;

            width  = image.getWidth();
            height = image.getHeight();
            size   = width > height ? width : height;
            radius = size * 0.5;

            // Velocity
            vX = 0;
            vY = ENEMY_BOSS_SPEED;

            // No of hits
            hits = 5;
        }

        private void update() {
            dX     = spaceShip.x - x;
            dY     = spaceShip.y - y;
            dist   = Math.sqrt(dX * dX + dY * dY);
            factor = ENEMY_BOSS_SPEED / dist;
            vX     = dX * factor;
            vY     = dY * factor;

            x += vX;
            y += vY;

            r = Math.toDegrees(Math.atan2(vY, vX)) - 90;

            long now = System.nanoTime();

            if (hasRockets) {
                if (now - lastRocket > TIME_BETWEEN_ROCKETS) {
                    double[] p0 = { x, y };
                    double[] p1 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, -HALF_ANGLE_OF_SIGHT);
                    double[] p2 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, HALF_ANGLE_OF_SIGHT);

                    double area = 0.5 * (-p1[1] * p2[0] + p0[1] * (-p1[0] + p2[0]) + p0[0] * (p1[1] - p2[1]) + p1[0] * p2[1]);
                    double s    = 1 / (2 * area) * (p0[1] * p2[0] - p0[0] * p2[1] + (p2[1] - p0[1]) * spaceShip.x + (p0[0] - p2[0]) * spaceShip.y);
                    double t    = 1 / (2 * area) * (p0[0] * p1[1] - p0[1] * p1[0] + (p0[1] - p1[1]) * spaceShip.x + (p1[0] - p0[0]) * spaceShip.y);
                    if (s > 0 && t > 0 && 1 - s - t > 0) {
                        spawnEnemyBossRocket(x, y);
                        lastRocket = now;
                    }
                }
            } else {
                if (now - lastShot > TIME_BETWEEN_SHOTS) {
                    double[] p0 = { x, y };
                    double[] p1 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, -HALF_ANGLE_OF_SIGHT);
                    double[] p2 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, HALF_ANGLE_OF_SIGHT);

                    double area = 0.5 * (-p1[1] * p2[0] + p0[1] * (-p1[0] + p2[0]) + p0[0] * (p1[1] - p2[1]) + p1[0] * p2[1]);
                    double s    = 1 / (2 * area) * (p0[1] * p2[0] - p0[0] * p2[1] + (p2[1] - p0[1]) * spaceShip.x + (p0[0] - p2[0]) * spaceShip.y);
                    double t    = 1 / (2 * area) * (p0[0] * p1[1] - p0[1] * p1[0] + (p0[1] - p1[1]) * spaceShip.x + (p1[0] - p0[0]) * spaceShip.y);
                    if (s > 0 && t > 0 && 1 - s - t > 0) {
                        spawnEnemyBossTorpedo(x, y, vX, vY);
                        lastShot = now;
                    }
                }
            }

            switch (hits) {
                case 5: image = level.getEnemyBossImg4();break;
                case 4: image = level.getEnemyBossImg3();break;
                case 3: image = level.getEnemyBossImg2();break;
                case 2: image = level.getEnemyBossImg1();break;
                case 1: image = level.getEnemyBossImg0();break;
            }
        }
    }

    private class EnemyBossTorpedo {
        private final Image  image;
        private       double x;
        private       double y;
        private       double width;
        private       double height;
        private       double size;
        private       double radius;
        private       double vX;
        private       double vY;


        public EnemyBossTorpedo(final Image image, final double x, final double y, final double vX, final double vY) {
            this.image  = image;
            this.x      = x - image.getWidth() / 2.0;
            this.y      = y;
            this.width  = image.getWidth();
            this.height = image.getHeight();
            this.size   = width > height ? width : height;
            this.radius = size * 0.5;
            this.vX     = vX;
            this.vY     = vY;
        }


        private void update() {
            x += vX;
            y += vY;

            if (spaceShip.isVulnerable && !hasBeenHit) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                } else {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                }
                if (hit) {
                    enemyBossTorpedosToRemove.add(EnemyBossTorpedo.this);
                    if (spaceShip.shield) {
                        playSound(shieldHitSound);
                    } else {
                        hasBeenHit = true;
                        playSound(spaceShipExplosionSound);
                        noOfLifes--;
                        if (0 == noOfLifes) {
                            gameOver();
                        }
                    }
                }
            } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                enemyBossTorpedosToRemove.add(EnemyBossTorpedo.this);
            }
        }
    }

    private class EnemyBossRocket {
        private final long      rocketLifespan = 2_500_000_000l;
        private final SpaceShip spaceShip;
        private final Image     image;
        private       long      born;
        private       double    x;
        private       double    y;
        private       double    r;
        private       double    dX;
        private       double    dY;
        private       double    dist;
        private       double    factor;
        private       double    width;
        private       double    height;
        private       double    size;
        private       double    radius;
        private       double    vX;
        private       double    vY;


        public EnemyBossRocket(final SpaceShip spaceShip, final Image image, final double x, final double y) {
            this.spaceShip = spaceShip;
            this.image     = image;
            this.x         = x - image.getWidth() / 2.0;
            this.y         = y;
            this.width     = image.getWidth();
            this.height    = image.getHeight();
            this.size      = width > height ? width : height;
            this.radius    = size * 0.5;
            this.vX        = 0;
            this.vY        = 1;
            this.born      = System.nanoTime();
        }


        private void update() {
            dX     = spaceShip.x - x;
            dY     = spaceShip.y - y;
            dist   = Math.sqrt(dX * dX + dY * dY);
            factor = ENEMY_BOSS_ROCKET_SPEED / dist;
            vX     = dX * factor;
            vY     = dY * factor;

            x += vX;
            y += vY;

            r = Math.toDegrees(Math.atan2(vY, vX)) - 90;

            if (spaceShip.isVulnerable && !hasBeenHit) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                } else {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                }
                if (hit) {
                    enemyBossRocketsToRemove.add(EnemyBossRocket.this);
                    if (spaceShip.shield) {
                        playSound(shieldHitSound);
                    } else {
                        hasBeenHit = true;
                        playSound(spaceShipExplosionSound);
                        noOfLifes--;
                        if (0 == noOfLifes) {
                            gameOver();
                        }
                    }
                }
            } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                enemyBossRocketsToRemove.add(EnemyBossRocket.this);
            }
            if (System.nanoTime() - born > rocketLifespan) {
                enemyRocketExplosions.add(new EnemyRocketExplosion(x - EnemyRocketExplosion.FRAME_WIDTH * 0.25, y - EnemyRocketExplosion.FRAME_HEIGHT * 0.25, vX, vY, 0.5));
                enemyBossRocketsToRemove.add(EnemyBossRocket.this);
            }
        }
    }

    private class EnemyBossExplosion {
        private static final double FRAME_WIDTH  = 200 * SCALING_FACTOR;
        private static final double FRAME_HEIGHT = 200 * SCALING_FACTOR;
        private static final double FRAME_CENTER = FRAME_WIDTH * 0.5;
        private static final int    MAX_FRAME_X  = 4;
        private static final int    MAX_FRAME_Y  = 7;
        private              double x;
        private              double y;
        private              double vX;
        private              double vY;
        private              double scale;
        private              int    countX;
        private              int    countY;


        public EnemyBossExplosion(final double x, final double y, final double vX, final double vY, final double scale) {
            this.x      = x;
            this.y      = y;
            this.vX     = vX;
            this.vY     = vY;
            this.scale  = scale;
            this.countX = 0;
            this.countY = 0;
        }


        private void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == MAX_FRAME_X) {
                countY++;
                if (countX == MAX_FRAME_X && countY == MAX_FRAME_Y) {
                    enemyBossExplosionsToRemove.add(EnemyBossExplosion.this);
                }
                countX = 0;
                if (countY == MAX_FRAME_Y) {
                    countY = 0;
                }
            }
        }
    }

    private class LevelBoss {
        private static final int       MAX_VALUE            = 500;
        private static final long      TIME_BETWEEN_SHOTS   = 400_000_000l;
        private static final long      TIME_BETWEEN_ROCKETS = 3_500_000_000l;
        private static final long      TIME_BETWEEN_BOMBS   = 2_500_000_000l;
        private static final double    HALF_ANGLE_OF_SIGHT  = 22;
        private static final double    BOMB_RANGE           = 50;
        private static final long      WAITING_PHASE        = 10_000_000_000l;
        private final        SpaceShip spaceShip;
        private              Image     image;
        private              double    x;
        private              double    y;
        private              double    r;
        private              double    dX;
        private              double    dY;
        private              double    dist;
        private              double    factor;
        private              double    width;
        private              double    height;
        private              double    weaponSpawnY;
        private              double    size;
        private              double    radius;
        private              double    vX;
        private              double    vY;
        private              double    vpX;
        private              double    vpY;
        private              int       value;
        private              int       hits;
        private              long      lastShot;
        private              long      lastRocket;
        private              boolean   hasRockets;
        private              boolean   hasBombs;
        private              long      waitingStart;


        public LevelBoss(final SpaceShip spaceShip, final Image image, final boolean hasRockets, final boolean hasBombs) {
            this.spaceShip  = spaceShip;
            this.image      = image;
            this.hasRockets = hasRockets;
            this.hasBombs   = hasBombs;
            init();
        }


        private void init() {
            // Position
            x = 0.5 * WIDTH;
            y = -image.getHeight();

            // Value
            value = MAX_VALUE;

            width        = image.getWidth();
            height       = image.getHeight();
            weaponSpawnY = height * 0.4;
            size         = width > height ? width : height;
            radius       = size * 0.5;

            // Velocity
            vX = 0;
            vY = LEVEL_BOSS_SPEED;

            // Rotation
            r = 0;

            // No of hits
            hits = 80;

            waitingStart = 0;
        }

        private void update() {
            if (y < height * 0.6) {
                // Approaching
                vY = LEVEL_BOSS_SPEED;
            } else {
                if (waitingStart == 0) {
                    waitingStart = System.nanoTime();
                }
                dX     = spaceShip.x - x;
                dY     = spaceShip.y - y;
                dist   = Math.sqrt(dX * dX + dY * dY);
                factor = LEVEL_BOSS_SPEED / dist;
                vpX    = dX * factor;
                vpY    = dY * factor;

                if (System.nanoTime() < waitingStart + WAITING_PHASE) {
                    // Waiting
                    vX = dX * factor * 10;
                    vY = 0;
                } else {
                    // Attacking
                    vX = vpX;
                    vY = vpY;
                    r  = Math.toDegrees(Math.atan2(vY, vX)) - 90;
                }
            }

            x += vX;
            y += vY;

            long now = System.nanoTime();

            if (hasRockets) {
                if (now - lastRocket > TIME_BETWEEN_ROCKETS) {
                    double[] p0 = { x, y };
                    double[] p1 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vpX, y + HEIGHT * vpY, x, y, -HALF_ANGLE_OF_SIGHT);
                    double[] p2 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vpX, y + HEIGHT * vpY, x, y, HALF_ANGLE_OF_SIGHT);

                    double area = 0.5 * (-p1[1] * p2[0] + p0[1] * (-p1[0] + p2[0]) + p0[0] * (p1[1] - p2[1]) + p1[0] * p2[1]);
                    double s    = 1 / (2 * area) * (p0[1] * p2[0] - p0[0] * p2[1] + (p2[1] - p0[1]) * spaceShip.x + (p0[0] - p2[0]) * spaceShip.y);
                    double t    = 1 / (2 * area) * (p0[0] * p1[1] - p0[1] * p1[0] + (p0[1] - p1[1]) * spaceShip.x + (p1[0] - p0[0]) * spaceShip.y);
                    if (s > 0 && t > 0 && 1 - s - t > 0) {
                        spawnLevelBossRocket(x, y + weaponSpawnY);
                        lastRocket = now;
                    }
                }
            }
            if (hasBombs) {
                if (now - lastBombDropped > TIME_BETWEEN_BOMBS && spaceShip.y > y) {
                    if (spaceShip.x > x - BOMB_RANGE && spaceShip.x < x + BOMB_RANGE) {
                        spawnLevelBossBomb(x, y);
                        lastBombDropped = now;
                    }
                }
            }

            if (now - lastShot > TIME_BETWEEN_SHOTS) {
                double[] p0 = { x, y };
                double[] p1 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vpX, y + HEIGHT * vpY, x, y, -HALF_ANGLE_OF_SIGHT);
                double[] p2 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vpX, y + HEIGHT * vpY, x, y, HALF_ANGLE_OF_SIGHT);

                double area = 0.5 * (-p1[1] * p2[0] + p0[1] * (-p1[0] + p2[0]) + p0[0] * (p1[1] - p2[1]) + p1[0] * p2[1]);
                double s    = 1 / (2 * area) * (p0[1] * p2[0] - p0[0] * p2[1] + (p2[1] - p0[1]) * spaceShip.x + (p0[0] - p2[0]) * spaceShip.y);
                double t    = 1 / (2 * area) * (p0[0] * p1[1] - p0[1] * p1[0] + (p0[1] - p1[1]) * spaceShip.x + (p1[0] - p0[0]) * spaceShip.y);
                if (s > 0 && t > 0 && 1 - s - t > 0) {
                    double[] tp = Helper.rotatePointAroundRotationCenter(x, y + radius, x, y, r);
                    spawnLevelBossTorpedo(tp[0], tp[1], vX, vY, r);
                    lastShot = now;
                }
            }
        }
    }

    private class LevelBossTorpedo {
        private final Image  image;
        private       double x;
        private       double y;
        private       double width;
        private       double height;
        private       double size;
        private       double radius;
        private       double vX;
        private       double vY;
        private       double r;


        public LevelBossTorpedo(final Image image, final double x, final double y, final double vX, final double vY, final double r) {
            this.image  = image;
            this.x      = x - image.getWidth() / 2.0;
            this.y      = y;
            this.width  = image.getWidth();
            this.height = image.getHeight();
            this.size   = width > height ? width : height;
            this.radius = size * 0.5;
            this.vX     = vX;
            this.vY     = vY;
            this.r      = r;
        }


        private void update() {
            x += vX;
            y += vY;

            if (spaceShip.isVulnerable && !hasBeenHit) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                } else {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                }
                if (hit) {
                    levelBossTorpedosToRemove.add(LevelBossTorpedo.this);
                    if (spaceShip.shield) {
                        playSound(shieldHitSound);
                    } else {
                        hasBeenHit = true;
                        playSound(spaceShipExplosionSound);
                        noOfLifes--;
                        if (0 == noOfLifes) {
                            gameOver();
                        }
                    }
                }
            } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                levelBossTorpedosToRemove.add(LevelBossTorpedo.this);
            }
        }
    }

    private class LevelBossRocket {
        private final long      rocketLifespan = 3_000_000_000l;
        private final SpaceShip spaceShip;
        private final Image     image;
        private       long      born;
        private       double    x;
        private       double    y;
        private       double    r;
        private       double    dX;
        private       double    dY;
        private       double    dist;
        private       double    factor;
        private       double    width;
        private       double    height;
        private       double    size;
        private       double    radius;
        private       double    vX;
        private       double    vY;


        public LevelBossRocket(final SpaceShip spaceShip, final Image image, final double x, final double y) {
            this.spaceShip = spaceShip;
            this.image     = image;
            this.x         = x - image.getWidth() / 2.0;
            this.y         = y;
            this.width     = image.getWidth();
            this.height    = image.getHeight();
            this.size      = width > height ? width : height;
            this.radius    = size * 0.5;
            this.vX        = 0;
            this.vY        = 1;
            this.born      = System.nanoTime();
        }


        private void update() {
            dX     = spaceShip.x - x;
            dY     = spaceShip.y - y;
            dist   = Math.sqrt(dX * dX + dY * dY);
            factor = ENEMY_BOSS_ROCKET_SPEED / dist;
            vX     = dX * factor;
            vY     = dY * factor;

            x += vX;
            y += vY;

            r = Math.toDegrees(Math.atan2(vY, vX)) - 90;

            if (spaceShip.isVulnerable && !hasBeenHit) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                } else {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                }
                if (hit) {
                    levelBossRocketsToRemove.add(LevelBossRocket.this);
                    if (spaceShip.shield) {
                        playSound(shieldHitSound);
                    } else {
                        hasBeenHit = true;
                        playSound(spaceShipExplosionSound);
                        noOfLifes--;
                        if (0 == noOfLifes) {
                            gameOver();
                        }
                    }
                }
            } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                levelBossRocketsToRemove.add(LevelBossRocket.this);
            }
            if (System.nanoTime() - born > rocketLifespan) {
                enemyRocketExplosions.add(new EnemyRocketExplosion(x - EnemyRocketExplosion.FRAME_WIDTH * 0.25, y - EnemyRocketExplosion.FRAME_HEIGHT * 0.25, vX, vY, 0.5));
                levelBossRocketsToRemove.add(LevelBossRocket.this);
            }
        }
    }

    private class LevelBossBomb {
        private final Image  image;
        private       double x;
        private       double y;
        private       double width;
        private       double height;
        private       double size;
        private       double radius;
        private       double vX;
        private       double vY;


        public LevelBossBomb(final Image image, final double x, final double y, final double vX, final double vY) {
            this.image  = image;
            this.x      = x - image.getWidth() / 2.0;
            this.y      = y;
            this.width  = image.getWidth();
            this.height = image.getHeight();
            this.size   = width > height ? width : height;
            this.radius = size * 0.5;
            this.vX     = vX;
            this.vY     = vY;
        }


        private void update() {
            x += vX;
            y += vY;

            if (spaceShip.isVulnerable && !hasBeenHit) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                } else {
                    hit = isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                }
                if (hit) {
                    levelBossBombsToRemove.add(LevelBossBomb.this);
                    if (spaceShip.shield) {
                        playSound(shieldHitSound);
                    } else {
                        hasBeenHit = true;
                        playSound(spaceShipExplosionSound);
                        noOfLifes--;
                        if (0 == noOfLifes) {
                            gameOver();
                        }
                    }
                }
            } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                levelBossBombsToRemove.add(LevelBossBomb.this);
            }
        }
    }

    private class LevelBossExplosion {
        private static final double FRAME_WIDTH  = 256 * SCALING_FACTOR;
        private static final double FRAME_HEIGHT = 256 * SCALING_FACTOR;
        private static final double FRAME_CENTER = FRAME_WIDTH * 0.5;
        private static final int    MAX_FRAME_X  = 8;
        private static final int    MAX_FRAME_Y  = 3;
        private              double x;
        private              double y;
        private              double vX;
        private              double vY;
        private              double scale;
        private              int    countX;
        private              int    countY;


        public LevelBossExplosion(final double x, final double y, final double vX, final double vY, final double scale) {
            this.x      = x;
            this.y      = y;
            this.vX     = vX;
            this.vY     = vY;
            this.scale  = scale;
            this.countX = 0;
            this.countY = 0;
        }


        private void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == MAX_FRAME_X) {
                countY++;
                if (countX == MAX_FRAME_X && countY == MAX_FRAME_Y) {
                    levelBossExplosionsToRemove.add(LevelBossExplosion.this);
                }
                countX = 0;
                if (countY == MAX_FRAME_Y) {
                    countY = 0;
                }
            }
        }
    }

    private class Player implements Comparable<Player> {
        private final String id;
        private       String name;
        private       Long   score;


        public Player(final String propertyString) {
            this(propertyString.split(",")[0], propertyString.split(",")[1], Long.valueOf(propertyString.split(",")[2]));
        }
        public Player(final String name, final Long score) {
            this(UUID.randomUUID().toString(), name, score);
        }
        public Player(final String id, final String name, final Long score) {
            this.id    = id;
            this.name  = name;
            this.score = score;
        }


        @Override public int compareTo(final Player player) {
            return Long.compare(player.score, this.score);
        }

        public String toPropertyString() {
            return new StringBuilder(this.id).append(",").append(this.name).append(",").append(this.score).toString();
        }

        @Override public String toString() {
            return new StringBuilder().append("{ ")
                                      .append("\"id\"").append(":").append(id).append(",")
                                      .append("\"name\"").append(":").append(name).append(",")
                                      .append("\"score\"").append(":").append(score)
                                      .append(" }")
                                      .toString();
        }
    }

    private class Wave {
        private static final long        ENEMY_SPAWN_INTERVAL = 250_000_000l;
        private        final WaveType    waveType1;
        private        final WaveType    waveType2;
        private        final SpaceShip   spaceShip;
        private        final int         noOfEnemies;
        private        final Image       image;
        private        final boolean     canFire;
        private        final boolean     canBomb;
        private        final List<Enemy> enemies;
        private        final List<Enemy> enemiesToRemove;
        private              int         enemiesSpawned;
        private              long        lastEnemySpawned;
        private              boolean     alternateWaveType;
        private              boolean     toggle;
        private              boolean     isRunning;


        public Wave(final WaveType waveType, final SpaceShip spaceShip, final int noOfEnemies, final Image image, final boolean canFire, final boolean canBomb) {
            this(waveType, null, spaceShip, noOfEnemies, image, canFire, canBomb);
        }
        public Wave(final WaveType waveType1, final WaveType waveType2, final SpaceShip spaceShip, final int noOfEnemies, final Image image, final boolean canFire, final boolean canBomb) {
            if (null == waveType1) { throw new IllegalArgumentException("You need at least define one wave type."); }
            this.waveType1         = waveType1;
            this.waveType2         = waveType2;
            this.spaceShip         = spaceShip;
            this.noOfEnemies       = noOfEnemies;
            this.image             = image;
            this.canFire           = canFire;
            this.canBomb           = canBomb;
            this.enemies           = new ArrayList<>(noOfEnemies);
            this.enemiesToRemove   = new ArrayList<>();
            this.enemiesSpawned    = 0;
            this.alternateWaveType = null == waveType2 ? false : true;
            this.toggle            = true;
            this.isRunning         = true;
        }


        public void update(final GraphicsContext ctx) {
            if (isRunning) {
                if (enemiesSpawned < noOfEnemies && System.nanoTime() - lastEnemySpawned > ENEMY_SPAWN_INTERVAL) {
                    spawnEnemy();
                    lastEnemySpawned = System.nanoTime();
                }

                enemies.forEach(enemy -> {
                    enemy.update();
                    if (enemy.toBeRemoved) {
                        enemiesToRemove.add(enemy);
                    }

                    ctx.save();
                    ctx.translate(enemy.x - enemy.radius, enemy.y - enemy.radius);
                    ctx.save();
                    ctx.translate(enemy.radius, enemy.radius);
                    ctx.rotate(enemy.r);
                    ctx.translate(-enemy.radius, -enemy.radius);
                    ctx.drawImage(enemy.image, 0, 0);
                    ctx.restore();
                    ctx.restore();

                    // Check for torpedo hits
                    for (Torpedo torpedo : torpedos) {
                        if (isHitCircleCircle(torpedo.x, torpedo.y, torpedo.radius, enemy.x, enemy.y, enemy.radius)) {
                            explosions.add(new Explosion(enemy.x - Explosion.FRAME_WIDTH * 0.25, enemy.y - Explosion.FRAME_HEIGHT * 0.25, enemy.vX, enemy.vY, 0.35));
                            score += enemy.value;
                            kills++;
                            levelKills++;
                            enemiesToRemove.add(enemy);
                            torpedosToRemove.add(torpedo);
                            playSound(spaceShipExplosionSound);
                        }
                    }

                    // Check for rocket hits
                    for (Rocket rocket : rockets) {
                        if (isHitCircleCircle(rocket.x, rocket.y, rocket.radius, enemy.x, enemy.y, enemy.radius)) {
                            rocketExplosions.add(new RocketExplosion(enemy.x - RocketExplosion.FRAME_WIDTH * 0.25, enemy.y - RocketExplosion.FRAME_HEIGHT * 0.25, enemy.vX, enemy.vY, 0.5));
                            score += enemy.value;
                            kills++;
                            levelKills++;
                            enemiesToRemove.add(enemy);
                            rocketsToRemove.add(rocket);
                            playSound(rocketExplosionSound);
                        }
                    }

                    // Check for space ship hit
                    if (spaceShip.isVulnerable && !hasBeenHit) {
                        boolean hit;
                        if (spaceShip.shield) {
                            hit = isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, enemy.x, enemy.y, enemy.radius);
                        } else {
                            hit = isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, enemy.x, enemy.y, enemy.radius);
                        }
                        if (hit) {
                            if (spaceShip.shield) {
                                explosions.add(new Explosion(enemy.x - Explosion.FRAME_WIDTH * 0.125, enemy.y - Explosion.FRAME_HEIGHT * 0.125, enemy.vX, enemy.vY, 0.35));
                                playSound(spaceShipExplosionSound);
                            } else {
                                spaceShipExplosion.countX = 0;
                                spaceShipExplosion.countY = 0;
                                spaceShipExplosion.x      = spaceShip.x - SpaceShipExplosion.FRAME_WIDTH;
                                spaceShipExplosion.y      = spaceShip.y - SpaceShipExplosion.FRAME_HEIGHT;
                                playSound(spaceShipExplosionSound);
                                hasBeenHit = true;
                                noOfLifes--;
                                if (0 == noOfLifes) {
                                    gameOver();
                                }
                            }
                            enemiesToRemove.add(enemy);
                        }
                    }
                });

                enemies.removeAll(enemiesToRemove);
                enemiesToRemove.clear();
                if (enemies.isEmpty() && enemiesSpawned == noOfEnemies) { isRunning = false; }
            }
        }

        private void spawnEnemy() {
            Enemy enemy;
            if (alternateWaveType) {
                enemy = new Enemy(toggle ? waveType1 : waveType2, spaceShip, image, canFire, canBomb);
            } else {
                enemy = new Enemy(waveType1, spaceShip, image, canFire, canBomb);
            }
            toggle = !toggle;
            enemies.add(enemy);
            enemiesSpawned++;
        }
    }
}