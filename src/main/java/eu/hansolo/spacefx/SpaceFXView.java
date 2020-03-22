/*
 * Copyright (c) 2020 by Gerrit Grunwald
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
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

//import static com.gluonhq.attach.util.Platform.isDesktop;
//import static com.gluonhq.attach.util.Platform.isIOS;
import static eu.hansolo.spacefx.Config.*;
import static eu.hansolo.spacefx.Players.PLAYER_1;


public class SpaceFXView extends StackPane {
    private static final long                       SCREEN_TOGGLE_INTERVAL  = 10_000_000_000l;
    private static final Random                     RND                     = new Random();
    private static final boolean                    IS_BROWSER              = WebAPI.isBrowser();
    private              Task<Boolean>              initTask;
    private              Level1                     level1;
    private              Level2                     level2;
    private              Level3                     level3;
    private              long                       lastScreenToggle;
    private              boolean                    readyToStart;
    private              boolean                    running;
    private              long                       gameOverStart;
    private              boolean                    hallOfFameScreen;
    private              Properties                 properties;
    private              Label                      playerInitialsLabel;
    private              InitialDigit               digit1;
    private              InitialDigit               digit2;
    private              HBox                       playerInitialsDigits;
    private              Button                     saveInitialsButton;
    private              List<Player>               hallOfFame;
    private              VBox                       hallOfFameBox;
    private              Level                      level;
    private final        Image                      startImg                = new Image(getClass().getResourceAsStream("startscreen.jpg"));
    private final        Image                      gameOverImg             = new Image(getClass().getResourceAsStream("gameover.jpg"));
    private final        Image                      hallOfFameImg           = new Image(getClass().getResourceAsStream("halloffamescreen.jpg"));
    //private final        Image                      startImg                = isDesktop() ? new Image(getClass().getResourceAsStream("startscreen.jpg")) : isIOS() ? new Image(getClass().getResourceAsStream("startscreenIOS.jpg")) : new Image(getClass().getResourceAsStream("startscreenAndroid.png"));
    //private final        Image                      gameOverImg             = isDesktop() ? new Image(getClass().getResourceAsStream("gameover.jpg")) : isIOS() ? new Image(getClass().getResourceAsStream("gameoverIOS.jpg")) : new Image(getClass().getResourceAsStream("gameoverAndroid.png"));
    //private final        Image                      hallOfFameImg           = isDesktop() ? new Image(getClass().getResourceAsStream("halloffamescreen.jpg")) : isIOS() ? new Image(getClass().getResourceAsStream("halloffamescreenIOS.jpg")) : new Image(getClass().getResourceAsStream("halloffamescreenAndroid.png"));
    private              Image[]                    asteroidImages;
    private              Image                      torpedoButtonImg;
    private              Image                      rocketButtonImg;
    private              Image                      shieldButtonImg;

    private              Image                      spaceshipImg;
    private              Image                      spaceshipUpImg;
    private              Image                      spaceshipDownImg;
    private              Image                      miniSpaceshipImg;
    private              Image                      deflectorShieldImg;
    private              Image                      miniDeflectorShieldImg;
    private              Image                      torpedoImg;
    private              Image                      rocketImg;
    private              Image                      bigTorpedoImg;
    private              Image                      spaceship1Img;
    private              Image                      spaceshipUp1Img;
    private              Image                      spaceshipDown1Img;
    private              Image                      miniSpaceship1Img;
    private              Image                      deflectorShield1Img;
    private              Image                      miniDeflectorShield1Img;
    private              Image                      torpedo1Img;
    private              Image                      rocket1Img;
    private              Image                      bigTorpedo1Img;
    private              Image                      asteroidExplosionImg;
    private              Image                      asteroidExplosion1Img;
    private              Image                      spaceShipExplosionImg;
    private              Image                      hitImg;
    private              Image                      shieldUpImg;
    private              Image                      lifeUpImg;
    private              Image                      bigTorpedoBonusImg;
    private              Image                      starburstBonusImg;
    private              Image                      miniBigTorpedoBonusImg;
    private              Image                      miniStarburstBonusImg;
    private              Image                      miniBigTorpedoBonus1Img;
    private              Image                      miniStarburstBonus1Img;
    private              Image                      upExplosionImg;
    private              Image                      rocketExplosionImg;
    private              AudioClip                  laserSound;
    private              AudioClip                  rocketLaunchSound;
    private              AudioClip                  rocketExplosionSound;
    private              AudioClip                  enemyLaserSound;
    private              AudioClip                  enemyBombSound;
    private              AudioClip                  explosionSound;
    private              AudioClip                  asteroidExplosionSound;
    private              AudioClip                  torpedoHitSound;
    private              AudioClip                  spaceShipExplosionSound;
    private              AudioClip                  enemyBossExplosionSound;
    private              AudioClip                  gameoverSound;
    private              AudioClip                  shieldHitSound;
    private              AudioClip                  enemyHitSound;
    private              AudioClip                  deflectorShieldSound;
    private              AudioClip                  levelBossTorpedoSound;
    private              AudioClip                  levelBossRocketSound;
    private              AudioClip                  levelBossBombSound;
    private              AudioClip                  levelBossExplosionSound;
    private              AudioClip                  shieldUpSound;
    private              AudioClip                  lifeUpSound;
    private              AudioClip                  levelUpSound;
    private              AudioClip                  bonusSound;
    private final        Media                      gameSoundTheme          = new Media(getClass().getResource("RaceToMars.mp3").toExternalForm());
    private final        Media                      soundTheme              = new Media(getClass().getResource("CityStomper.mp3").toExternalForm());
    private final        MediaPlayer                gameMediaPlayer         = new MediaPlayer(gameSoundTheme);
    private final        MediaPlayer                mediaPlayer             = new MediaPlayer(soundTheme);
    private              double                     deflectorShieldRadius;
    private              boolean                    levelBossActive;
    private              Font                       scoreFont;
    private              double                     backgroundViewportY;
    private              Canvas                     canvas;
    private              GraphicsContext            ctx;
    private              Star[]                     stars;
    private              Asteroid[]                 asteroids;
    private              SpaceShip                  spaceShipPlayer1;
    private              SpaceShip                  spaceShipPlayer2;
    private              List<Player>               players;
    private              List<SpaceShip>            spaceShips;
    private              List<Wave>                 waves;
    private              List<Wave>                 wavesToRemove;
    private              List<EnemyBoss>            enemyBosses;
    private              List<LevelBoss>            levelBosses;
    private              List<Bonus>                bonuses;
    private              List<Torpedo>              torpedos;
    private              List<BigTorpedo>           bigTorpedos;
    private              List<Rocket>               rockets;
    private              List<EnemyTorpedo>         enemyTorpedos;
    private              List<EnemyBomb>            enemyBombs;
    private              List<EnemyBossTorpedo>     enemyBossTorpedos;
    private              List<EnemyBossRocket>      enemyBossRockets;
    private              List<LevelBossTorpedo>     levelBossTorpedos;
    private              List<LevelBossRocket>      levelBossRockets;
    private              List<LevelBossBomb>        levelBossBombs;
    private              List<LevelBossExplosion>   levelBossExplosions;
    private              List<EnemyBossExplosion>   enemyBossExplosions;
    private              List<EnemyRocketExplosion> enemyRocketExplosions;
    private              List<RocketExplosion>      rocketExplosions;
    private              List<Explosion>            explosions;
    private              List<SpaceShipExplosion>   spaceShipExplosions;
    private              List<AsteroidExplosion>    asteroidExplosions;
    private              List<UpExplosion>          upExplosions;
    private              List<Hit>                  hits;
    private              List<EnemyHit>             enemyHits;
    private              long                       levelKills;
    private              long                       kills;
    private              double                     scorePosX;
    private              double                     scorePosY;
    private              double                     mobileOffsetY;
    private              long                       lastEnemyBossAttack;
    private              long                       lastShieldUp;
    private              long                       lastLifeUp;
    private              long                       lastWave;
    private              long                       lastBombDropped;
    private              long                       lastTorpedoFired;
    private              long                       lastStarBlast;
    private              long                       lastBigTorpedoBonus;
    private              long                       lastStarburstBonus;
    private              long                       lastTimerCall;
    private              AnimationTimer             timer;
    private              AnimationTimer             screenTimer;
    private              Circle                     shipTouchArea;
    private              EventHandler<TouchEvent>   touchHandler;


    // ******************** Constructor ***************************************
    public SpaceFXView() {
        init();
        initOnBackground();

        Pane pane = new Pane(canvas, shipTouchArea, hallOfFameBox, playerInitialsLabel, playerInitialsDigits, saveInitialsButton);
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        if (SHOW_BUTTONS) {
            canvas.addEventHandler(TouchEvent.TOUCH_PRESSED, touchHandler);
            shipTouchArea.setOnTouchMoved(e -> {
                if (null == spaceShipPlayer1) { return; }
                spaceShipPlayer1.x = e.getTouchPoint().getX();
                spaceShipPlayer1.y = e.getTouchPoint().getY();
            });
        } else {
            shipTouchArea.setOnMouseDragged(e -> {
                if (null == spaceShipPlayer1) { return; }
                spaceShipPlayer1.x = e.getX();
                spaceShipPlayer1.y = e.getY();
            });
        }

        setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        getChildren().add(pane);

        // Start playing background music
        if (PLAY_MUSIC) { mediaPlayer.play(); }

        // Start timer to toggle between start screen and hall of fame
        screenTimer.start();
    }


    // ******************** Methods *******************************************
    public void init() {
        scoreFont        = Fonts.spaceBoy(SCORE_FONT_SIZE);
        running          = false;
        gameOverStart    = -1;
        levelBossActive  = false;
        lastScreenToggle = System.nanoTime();
        hallOfFameScreen = false;

        playerInitialsLabel = new Label("Type in your initials");
        playerInitialsLabel.setAlignment(Pos.CENTER);
        playerInitialsLabel.setPrefWidth(WIDTH);
        Helper.enableNode(playerInitialsLabel, false);

        digit1 = new InitialDigit();
        digit2 = new InitialDigit();
        ToggleGroup toggleGroup = new ToggleGroup();
        digit1.setToggleGroup(toggleGroup);
        digit2.setToggleGroup(toggleGroup);
        digit1.setSelected(true);
        playerInitialsDigits = new HBox(0, digit1, digit2);
        Helper.enableNode(playerInitialsDigits, false);

        saveInitialsButton = new Button("Save Initials");
        saveInitialsButton.setPrefWidth(WIDTH * 0.6);
        Helper.enableNode(saveInitialsButton, false);

        // PreFill hall of fame
        properties = PropertyManager.INSTANCE.getProperties();

        Player p1 = new Player(properties.getProperty("hallOfFame1"));
        Player p2 = new Player(properties.getProperty("hallOfFame2"));
        Player p3 = new Player(properties.getProperty("hallOfFame3"));

        hallOfFame = new ArrayList<>(3);
        hallOfFame.add(p1);
        hallOfFame.add(p2);
        hallOfFame.add(p3);

        HBox p1Entry  = createHallOfFameEntry(p1);
        HBox p2Entry  = createHallOfFameEntry(p2);
        HBox p3Entry  = createHallOfFameEntry(p3);
        hallOfFameBox = new VBox(20, p1Entry, p2Entry, p3Entry);
        hallOfFameBox.setPrefWidth(WIDTH * 0.6);
        hallOfFameBox.setAlignment(Pos.CENTER);
        hallOfFameBox.setTranslateY(-HEIGHT * 0.1);
        hallOfFameBox.setMouseTransparent(true);
        hallOfFameBox.relocate((WIDTH - hallOfFameBox.getPrefWidth()) * 0.5, (HEIGHT - hallOfFameBox.getPrefHeight()) * 0.5);
        Helper.enableNode(hallOfFameBox, false);

        // Mediaplayer for background music
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.2);

        // Mediaplayer for game background music
        gameMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        gameMediaPlayer.setVolume(0.5);

        // Variable initialization
        backgroundViewportY           = SWITCH_POINT;
        canvas                        = new Canvas(WIDTH, HEIGHT);
        ctx                           = canvas.getGraphicsContext2D();
        stars                         = new Star[NO_OF_STARS];
        asteroids                     = new Asteroid[NO_OF_ASTEROIDS];
        players                       = new ArrayList<>();
        spaceShips                    = new ArrayList<>();
        waves                         = new ArrayList<>();
        wavesToRemove                 = new ArrayList<>();
        enemyBosses                   = new ArrayList<>();
        levelBosses                   = new ArrayList<>();
        bonuses                       = new ArrayList<>();
        rockets                       = new ArrayList<>();
        torpedos                      = new ArrayList<>();
        bigTorpedos                   = new ArrayList<>();
        enemyRocketExplosions         = new ArrayList<>();
        explosions                    = new ArrayList<>();
        spaceShipExplosions           = new ArrayList<>();
        asteroidExplosions            = new ArrayList<>();
        upExplosions                  = new ArrayList<>();
        enemyTorpedos                 = new ArrayList<>();
        enemyBombs                    = new ArrayList<>();
        enemyBossTorpedos             = new ArrayList<>();
        enemyBossRockets              = new ArrayList<>();
        levelBossTorpedos             = new ArrayList<>();
        levelBossRockets              = new ArrayList<>();
        levelBossBombs                = new ArrayList<>();
        levelBossExplosions           = new ArrayList<>();
        enemyBossExplosions           = new ArrayList<>();
        rocketExplosions              = new ArrayList<>();
        hits                          = new ArrayList<>();
        enemyHits                     = new ArrayList<>();
        levelKills                    = 0;
        kills                         = 0;
        lastEnemyBossAttack           = System.nanoTime();
        lastShieldUp                  = System.nanoTime();
        lastLifeUp                    = System.nanoTime();
        lastWave                      = System.nanoTime();
        lastTorpedoFired              = System.nanoTime();
        lastStarBlast                 = System.nanoTime();
        lastBigTorpedoBonus           = System.nanoTime();
        lastStarburstBonus            = System.nanoTime();
        //long deltaTime                = FPS_60;
        long deltaTime                = IS_BROWSER ? FPS_30 : FPS_60;
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall) {
                    lastTimerCall = now + deltaTime;
                    updateAndDraw();
                }
                if (now > lastEnemyBossAttack + ENEMY_BOSS_ATTACK_INTERVAL) {
                    spawnEnemyBoss(spaceShips);
                    lastEnemyBossAttack = now;
                }
                if (now > lastShieldUp + SHIELD_UP_SPAWN_INTERVAL && spaceShips.stream().filter(sp -> sp.noOfShields < NO_OF_SHIELDS).count() > 0) {
                    spawnShieldUp();
                    lastShieldUp = now;
                }
                if (now > lastLifeUp + LIFE_UP_SPAWN_INTERVAL && spaceShips.stream().filter(sp -> sp.noOfLifes < NO_OF_LIFES).count() > 0) {
                    spawnLifeUp();
                    lastLifeUp = now;
                }
                if (now > lastWave + WAVE_SPAWN_INTERVAL && SHOW_ENEMIES) {
                    spawnWave();
                    lastWave = now;
                }
                if (now > lastBigTorpedoBonus + BIG_TORPEDO_BONUS_INTERVAL) {
                    spawnBigTorpedoBonus();
                    lastBigTorpedoBonus = now;
                }
                if (now > lastStarburstBonus + STARBURST_BONUS_INTERVAL) {
                    spawnStarburstBonus();
                    lastStarburstBonus = now;
                }
            }
        };
        screenTimer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (!running && now > lastScreenToggle + SCREEN_TOGGLE_INTERVAL) {
                    if (hallOfFameScreen) {
                        ctx.drawImage(hallOfFameImg, 0, 0, WIDTH, HEIGHT);
                        Helper.enableNode(hallOfFameBox, true);
                    } else {
                        ctx.drawImage(startImg, 0, 0, WIDTH, HEIGHT);
                        Helper.enableNode(hallOfFameBox, false);
                    }
                    hallOfFameScreen = !hallOfFameScreen;
                    lastScreenToggle = now;
                }
            }
        };

        shipTouchArea = new Circle();

        touchHandler = e -> {
            EventType<TouchEvent>  type  = e.getEventType();
            if (null == spaceShipPlayer1) { return; }
            if (TouchEvent.TOUCH_PRESSED.equals(type)) {
                if (SHOW_BUTTONS) {
                    double x = e.getTouchPoint().getX();
                    double y = e.getTouchPoint().getY();
                    if (Helper.isInsideCircle(TORPEDO_BUTTON_CX, TORPEDO_BUTTON_CY, TORPEDO_BUTTON_R, x, y)) {
                        spawnWeapon(spaceShipPlayer1);
                    } else if (Helper.isInsideCircle(ROCKET_BUTTON_CX, ROCKET_BUTTON_CY, ROCKET_BUTTON_R, x, y)) {
                        if (rockets.size() < MAX_NO_OF_ROCKETS) {
                            spawnRocket(spaceShipPlayer1);
                        }
                    } else if (Helper.isInsideCircle(SHIELD_BUTTON_CX, SHIELD_BUTTON_CY, SHIELD_BUTTON_R, x, y)) {
                        if (spaceShipPlayer1.noOfShields > 0 && !spaceShipPlayer1.shield) {
                            spaceShipPlayer1.lastShieldActivated = System.nanoTime();
                            spaceShipPlayer1.shield = true;
                            playSound(deflectorShieldSound);
                        }
                    }
                }
            }
        };

        initStars();

        scorePosX = WIDTH * 0.5;
        scorePosY = 40 * SCALING_FACTOR;

        //mobileOffsetY = isIOS() ? 30 : 0;
        mobileOffsetY = 0;

        // Preparing GraphicsContext
        ctx.setFont(scoreFont);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.drawImage(startImg, 0, 0, WIDTH, HEIGHT);
    }

    private void initOnBackground() {
        initTask = new Task<>() {
            @Override protected Boolean call() {
                // Init levels
                level1 = new Level1();
                level2 = new Level2();
                level3 = new Level3();
                level  = level1;

                // Load images
                asteroidImages          = new Image[] { new Image(getClass().getResourceAsStream("asteroid1.png"), 140 * SCALING_FACTOR, 140 * SCALING_FACTOR, true, false),
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
                torpedoButtonImg        = new Image(getClass().getResourceAsStream("torpedoButton.png"), 64 * SCALING_FACTOR, 64 * SCALING_FACTOR, true, false);
                rocketButtonImg         = new Image(getClass().getResourceAsStream("rocketButton.png"), 64 * SCALING_FACTOR, 64 * SCALING_FACTOR, true, false);
                shieldButtonImg         = new Image(getClass().getResourceAsStream("shieldButton.png"), 64 * SCALING_FACTOR, 64 * SCALING_FACTOR, true, false);
                spaceshipImg            = new Image(getClass().getResourceAsStream("spaceship.png"), 48 * SCALING_FACTOR, 48 * SCALING_FACTOR, true, false);
                spaceshipUpImg          = new Image(getClass().getResourceAsStream("spaceshipUp.png"), 48 * SCALING_FACTOR, 48 * SCALING_FACTOR, true, false);
                spaceshipDownImg        = new Image(getClass().getResourceAsStream("spaceshipDown.png"), 48 * SCALING_FACTOR, 48 * SCALING_FACTOR, true, false);
                miniSpaceshipImg        = new Image(getClass().getResourceAsStream("spaceship.png"), 16 * SCALING_FACTOR, 16 * SCALING_FACTOR, true, false);
                deflectorShieldImg      = new Image(getClass().getResourceAsStream("deflectorshield.png"), 100 * SCALING_FACTOR, 100 * SCALING_FACTOR, true, false);
                miniDeflectorShieldImg  = new Image(getClass().getResourceAsStream("deflectorshield.png"), 16 * SCALING_FACTOR, 16 * SCALING_FACTOR, true, false);
                torpedoImg              = new Image(getClass().getResourceAsStream("torpedo.png"), 17 * SCALING_FACTOR, 20 * SCALING_FACTOR, true, false);
                rocketImg               = new Image(getClass().getResourceAsStream("rocket.png"), 17 * SCALING_FACTOR, 50 * SCALING_FACTOR, true, false);
                bigTorpedoImg           = new Image(getClass().getResourceAsStream("bigtorpedo.png"), 22 * SCALING_FACTOR, 40 * SCALING_FACTOR, true, false);
                spaceship1Img           = new Image(getClass().getResourceAsStream("spaceship1.png"), 48 * SCALING_FACTOR, 48 * SCALING_FACTOR, true, false);
                spaceshipUp1Img         = new Image(getClass().getResourceAsStream("spaceshipUp1.png"), 48 * SCALING_FACTOR, 48 * SCALING_FACTOR, true, false);
                spaceshipDown1Img       = new Image(getClass().getResourceAsStream("spaceshipDown1.png"), 48 * SCALING_FACTOR, 48 * SCALING_FACTOR, true, false);
                miniSpaceship1Img       = new Image(getClass().getResourceAsStream("spaceship1.png"), 16 * SCALING_FACTOR, 16 * SCALING_FACTOR, true, false);
                deflectorShield1Img     = new Image(getClass().getResourceAsStream("deflectorshield1.png"), 100 * SCALING_FACTOR, 100 * SCALING_FACTOR, true, false);
                miniDeflectorShield1Img = new Image(getClass().getResourceAsStream("deflectorshield1.png"), 16 * SCALING_FACTOR, 16 * SCALING_FACTOR, true, false);
                torpedo1Img             = new Image(getClass().getResourceAsStream("torpedo1.png"), 17 * SCALING_FACTOR, 20 * SCALING_FACTOR, true, false);
                rocket1Img              = new Image(getClass().getResourceAsStream("rocket1.png"), 17 * SCALING_FACTOR, 50 * SCALING_FACTOR, true, false);
                bigTorpedo1Img          = new Image(getClass().getResourceAsStream("bigtorpedo1.png"), 22 * SCALING_FACTOR, 40 * SCALING_FACTOR, true, false);
                asteroidExplosionImg    = new Image(getClass().getResourceAsStream("asteroidExplosion.png"), 2048 * SCALING_FACTOR, 1792 * SCALING_FACTOR, true, false);
                asteroidExplosion1Img   = new Image(getClass().getResourceAsStream("asteroidExplosion1.png"), 2048 * SCALING_FACTOR, 1792 * SCALING_FACTOR, true, false);
                spaceShipExplosionImg   = new Image(getClass().getResourceAsStream("spaceshipexplosion.png"), 800 * SCALING_FACTOR, 600 * SCALING_FACTOR, true, false);
                hitImg                  = new Image(getClass().getResourceAsStream("torpedoHit2.png"), 400 * SCALING_FACTOR, 160 * SCALING_FACTOR, true, false);
                shieldUpImg             = new Image(getClass().getResourceAsStream("shieldUp.png"), 50 * SCALING_FACTOR, 50 * SCALING_FACTOR, true, false);
                lifeUpImg               = new Image(getClass().getResourceAsStream("lifeUp.png"), 50 * SCALING_FACTOR, 50 * SCALING_FACTOR, true, false);
                bigTorpedoBonusImg      = new Image(getClass().getResourceAsStream("bigTorpedoBonus.png"), 50 * SCALING_FACTOR, 50 * SCALING_FACTOR, true, false);
                starburstBonusImg       = new Image(getClass().getResourceAsStream("starburstBonus.png"), 50 * SCALING_FACTOR, 50 * SCALING_FACTOR, true, false);
                miniBigTorpedoBonusImg  = new Image(getClass().getResourceAsStream("bigTorpedoBonus.png"), 20 * SCALING_FACTOR, 20 * SCALING_FACTOR, true, false);
                miniStarburstBonusImg   = new Image(getClass().getResourceAsStream("starburstBonus.png"), 20 * SCALING_FACTOR, 20 * SCALING_FACTOR, true, false);
                miniBigTorpedoBonus1Img = new Image(getClass().getResourceAsStream("bigTorpedoBonus1.png"), 20 * SCALING_FACTOR, 20 * SCALING_FACTOR, true, false);
                miniStarburstBonus1Img  = new Image(getClass().getResourceAsStream("starburstBonus1.png"), 20 * SCALING_FACTOR, 20 * SCALING_FACTOR, true, false);
                upExplosionImg          = new Image(getClass().getResourceAsStream("upExplosion.png"), 400 * SCALING_FACTOR, 700 * SCALING_FACTOR, true, false);
                rocketExplosionImg      = new Image(getClass().getResourceAsStream("rocketExplosion.png"), 960 * SCALING_FACTOR, 768 * SCALING_FACTOR, true, false);
                // Load sounds
                laserSound              = new AudioClip(getClass().getResource("laserSound.wav").toExternalForm());
                rocketLaunchSound       = new AudioClip(getClass().getResource("rocketLaunch.wav").toExternalForm());
                rocketExplosionSound    = new AudioClip(getClass().getResource("rocketExplosion.wav").toExternalForm());
                enemyLaserSound         = new AudioClip(getClass().getResource("enemyLaserSound.wav").toExternalForm());
                enemyBombSound          = new AudioClip(getClass().getResource("enemyBomb.wav").toExternalForm());
                explosionSound          = new AudioClip(getClass().getResource("explosionSound.wav").toExternalForm());
                asteroidExplosionSound  = new AudioClip(getClass().getResource("asteroidExplosion.wav").toExternalForm());
                torpedoHitSound         = new AudioClip(getClass().getResource("hit.wav").toExternalForm());
                spaceShipExplosionSound = new AudioClip(getClass().getResource("spaceShipExplosionSound.wav").toExternalForm());
                enemyBossExplosionSound = new AudioClip(getClass().getResource("enemyBossExplosion.wav").toExternalForm());
                gameoverSound           = new AudioClip(getClass().getResource("gameover.wav").toExternalForm());
                shieldHitSound          = new AudioClip(getClass().getResource("shieldhit.wav").toExternalForm());
                enemyHitSound           = new AudioClip(getClass().getResource("enemyBossShieldHit.wav").toExternalForm());
                deflectorShieldSound    = new AudioClip(getClass().getResource("deflectorshieldSound.wav").toExternalForm());
                levelBossTorpedoSound   = new AudioClip(getClass().getResource("levelBossTorpedo.wav").toExternalForm());
                levelBossRocketSound    = new AudioClip(getClass().getResource("levelBossRocket.wav").toExternalForm());
                levelBossBombSound      = new AudioClip(getClass().getResource("levelBossBomb.wav").toExternalForm());
                levelBossExplosionSound = new AudioClip(getClass().getResource("explosionSound1.wav").toExternalForm());
                shieldUpSound           = new AudioClip(getClass().getResource("shieldUp.wav").toExternalForm());
                lifeUpSound             = new AudioClip(getClass().getResource("lifeUp.wav").toExternalForm());
                levelUpSound            = new AudioClip(getClass().getResource("levelUp.wav").toExternalForm());
                bonusSound              = new AudioClip(getClass().getResource("bonus.wav").toExternalForm());

                deflectorShieldRadius   = deflectorShieldImg.getRequestedWidth() * 0.5;

                // Adjust audio clip volumes
                explosionSound.setVolume(0.5);
                torpedoHitSound.setVolume(0.5);

                initAsteroids();

                return true;
            }
        };
        initTask.setOnSucceeded(e -> readyToStart = true);
        initTask.setOnFailed(e -> readyToStart = false);
        new Thread(initTask, "initThread").start();
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


    // Update and draw
    private void updateAndDraw() {
        ctx.clearRect(0, 0, WIDTH, HEIGHT);

        // Draw background
        if (SHOW_BACKGROUND) {
            backgroundViewportY -= 0.5;
            if (backgroundViewportY <= 0) {
                backgroundViewportY = SWITCH_POINT; //backgroundImg.getHeight() - HEIGHT;
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
                if (Helper.isHitCircleCircle(torpedo.x, torpedo.y, torpedo.radius, asteroid.cX, asteroid.cY, asteroid.radius)) {
                    asteroid.hits--;
                    if (asteroid.hits <= 0) {
                        asteroidExplosions.add(new AsteroidExplosion(torpedo.spaceShip.isPlayer1, asteroid.cX - ASTEROID_EXPLOSION_FRAME_CENTER * asteroid.scale, asteroid.cY - ASTEROID_EXPLOSION_FRAME_CENTER * asteroid.scale, asteroid.vX, asteroid.vY, asteroid.scale));
                        torpedo.spaceShip.score += asteroid.value;
                        torpedo.spaceShip.asteroidsDestroyed++;
                        asteroid.respawn();
                        torpedo.toBeRemoved = true;
                        playSound(asteroidExplosionSound);
                    } else {
                        hits.add(new Hit(torpedo.x - HIT_FRAME_CENTER, torpedo.y - HIT_FRAME_HEIGHT, asteroid.vX, asteroid.vY));
                        torpedo.toBeRemoved = true;
                        playSound(torpedoHitSound);
                    }
                }
            }

            // Check for bigTorpedo hits
            for (BigTorpedo bigTorpedo : bigTorpedos) {
                if (Helper.isHitCircleCircle(bigTorpedo.x, bigTorpedo.y, bigTorpedo.radius, asteroid.cX, asteroid.cY, asteroid.radius)) {
                    asteroid.hits--;
                    if (asteroid.hits <= 0) {
                        asteroidExplosions.add(new AsteroidExplosion(bigTorpedo.spaceShip.isPlayer1, asteroid.cX - ASTEROID_EXPLOSION_FRAME_CENTER * asteroid.scale, asteroid.cY - ASTEROID_EXPLOSION_FRAME_CENTER * asteroid.scale, asteroid.vX, asteroid.vY, asteroid.scale));
                        bigTorpedo.spaceShip.score += asteroid.value;
                        bigTorpedo.spaceShip.asteroidsDestroyed++;
                        asteroid.respawn();
                        bigTorpedo.toBeRemoved = true;
                        playSound(asteroidExplosionSound);
                    } else {
                        hits.add(new Hit(bigTorpedo.x - HIT_FRAME_CENTER, bigTorpedo.y - HIT_FRAME_HEIGHT, asteroid.vX, asteroid.vY));
                        bigTorpedo.toBeRemoved = true;
                        playSound(torpedoHitSound);
                    }
                }
            }

            // Check for rocket hits
            for (Rocket rocket : rockets) {
                if (Helper.isHitCircleCircle(rocket.x, rocket.y, rocket.radius, asteroid.cX, asteroid.cY, asteroid.radius)) {
                    rocketExplosions.add(new RocketExplosion(asteroid.cX - ROCKET_EXPLOSION_FRAME_CENTER * asteroid.scale, asteroid.cY - ROCKET_EXPLOSION_FRAME_CENTER * asteroid.scale, asteroid.vX, asteroid.vY, asteroid.scale));
                    rocket.spaceShip.score += asteroid.value;
                    rocket.spaceShip.asteroidsDestroyed++;
                    asteroid.respawn();
                    rocket.toBeRemoved = true;
                    playSound(rocketExplosionSound);
                }
            }

            // Check for space ship hit
            spaceShips.forEach(spaceShip -> {
                if (spaceShip.isVulnerable && !spaceShip.hasBeenHit) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = Helper.isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, asteroid.cX, asteroid.cY, asteroid.radius);
                } else {
                    hit = Helper.isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, asteroid.cX, asteroid.cY, asteroid.radius);
                }
                if (hit) {
                    spaceShipExplosions.add(new SpaceShipExplosion(spaceShip,spaceShip.x - SPACESHIP_EXPLOSION_FRAME_WIDTH * 0.5, spaceShip.y - SPACESHIP_EXPLOSION_FRAME_HEIGHT * 0.5, spaceShip.vX, spaceShip.vY));
                    if (spaceShip.shield) {
                        playSound(explosionSound);
                        asteroidExplosions.add(new AsteroidExplosion(spaceShip.isPlayer1, asteroid.cX - ASTEROID_EXPLOSION_FRAME_CENTER * asteroid.scale, asteroid.cY - ASTEROID_EXPLOSION_FRAME_CENTER * asteroid.scale, asteroid.vX, asteroid.vY, asteroid.scale));
                    } else {
                        playSound(spaceShipExplosionSound);
                        spaceShip.hasBeenHit = true;
                        spaceShip.noOfLifes--;
                        if (0 == spaceShip.noOfLifes) {
                            spaceShip.toBeRemoved = true;
                            players.add(new Player(spaceShip.score, spaceShip.isPlayer1 ? SPACEFX_COLOR : SPACEFX_COLOR1));
                        }
                    }
                    asteroid.respawn();
                }
            }
            });
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
                if (Helper.isHitCircleCircle(torpedo.x, torpedo.y, torpedo.radius, enemyBoss.x, enemyBoss.y, enemyBoss.radius)) {
                    enemyBoss.hits -= TORPEDO_DAMAGE;
                    if (enemyBoss.hits == 0) {
                        enemyBossExplosions.add(
                            new EnemyBossExplosion(enemyBoss.x - ENEMY_BOSS_EXPLOSION_FRAME_WIDTH * 0.25, enemyBoss.y - ENEMY_BOSS_EXPLOSION_FRAME_HEIGHT * 0.25, enemyBoss.vX,
                                                           enemyBoss.vY, 0.5));
                        torpedo.spaceShip.score += enemyBoss.value;
                        torpedo.spaceShip.enemyBossesKilled++;
                        kills++;
                        levelKills++;
                        enemyBoss.toBeRemoved = true;
                        torpedo.toBeRemoved = true;
                        playSound(enemyBossExplosionSound);
                    } else {
                        enemyHits.add(new EnemyHit(torpedo.x - HIT_FRAME_CENTER, torpedo.y - HIT_FRAME_HEIGHT, enemyBoss.vX, enemyBoss.vY));
                        torpedo.toBeRemoved = true;
                        playSound(enemyHitSound);
                    }
                }
            }

            // Check for bigTorpedo hits with enemy boss
            for (BigTorpedo bigTorpedo : bigTorpedos) {
                if (Helper.isHitCircleCircle(bigTorpedo.x, bigTorpedo.y, bigTorpedo.radius, enemyBoss.x, enemyBoss.y, enemyBoss.radius)) {
                    enemyBoss.hits -= BIG_TORPEDO_DAMAGE;
                    if (enemyBoss.hits <= 0) {
                        enemyBossExplosions.add(
                            new EnemyBossExplosion(enemyBoss.x - ENEMY_BOSS_EXPLOSION_FRAME_WIDTH * 0.25, enemyBoss.y - ENEMY_BOSS_EXPLOSION_FRAME_HEIGHT * 0.25, enemyBoss.vX,
                                                   enemyBoss.vY, 0.5));
                        bigTorpedo.spaceShip.score += enemyBoss.value;
                        bigTorpedo.spaceShip.enemyBossesKilled++;
                        kills++;
                        levelKills++;
                        enemyBoss.toBeRemoved = true;
                        bigTorpedo.toBeRemoved = true;
                        playSound(enemyBossExplosionSound);
                    } else {
                        enemyHits.add(new EnemyHit(bigTorpedo.x - HIT_FRAME_CENTER, bigTorpedo.y - HIT_FRAME_HEIGHT, enemyBoss.vX, enemyBoss.vY));
                        bigTorpedo.toBeRemoved = true;
                        playSound(enemyHitSound);
                    }
                }
            }

            // Check for rocket hits with enemy boss
            for (Rocket rocket : rockets) {
                if (Helper.isHitCircleCircle(rocket.x, rocket.y, rocket.radius, enemyBoss.x, enemyBoss.y, enemyBoss.radius)) {
                    enemyBossExplosions.add(
                        new EnemyBossExplosion(enemyBoss.x - ENEMY_BOSS_EXPLOSION_FRAME_WIDTH * 0.25, enemyBoss.y - ENEMY_BOSS_EXPLOSION_FRAME_HEIGHT * 0.25, enemyBoss.vX, enemyBoss.vY, 0.5));
                    rocket.spaceShip.score += enemyBoss.value;
                    rocket.spaceShip.enemyBossesKilled++;
                    kills++;
                    levelKills++;
                    enemyBoss.toBeRemoved = true;
                    rocket.toBeRemoved = true;
                    playSound(enemyBossExplosionSound);
                }
            }


            // Check for space ship hit with enemy boss
            spaceShips.forEach(spaceShip -> {
                if (spaceShip.isVulnerable && !spaceShip.hasBeenHit) {
                    boolean hit;
                    if (spaceShip.shield) {
                        hit = Helper.isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, enemyBoss.x, enemyBoss.y, enemyBoss.radius);
                    } else {
                        hit = Helper.isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, enemyBoss.x, enemyBoss.y, enemyBoss.radius);
                    }
                    if (hit) {
                        if (spaceShip.shield) {
                            enemyBossExplosions.add(new EnemyBossExplosion(enemyBoss.x - ENEMY_BOSS_EXPLOSION_FRAME_WIDTH * 0.125, enemyBoss.y - ENEMY_BOSS_EXPLOSION_FRAME_HEIGHT * 0.125, enemyBoss.vX, enemyBoss.vY, 0.5));
                            //playSound(enemyBossExplosionSound);
                        } else {
                            spaceShipExplosions.add(new SpaceShipExplosion(spaceShip,spaceShip.x - SPACESHIP_EXPLOSION_FRAME_WIDTH * 0.5, spaceShip.y - SPACESHIP_EXPLOSION_FRAME_HEIGHT * 0.5, spaceShip.vX, spaceShip.vY));
                            playSound(spaceShipExplosionSound);
                            spaceShip.hasBeenHit = true;
                            spaceShip.noOfLifes--;
                            if (0 == spaceShip.noOfLifes) {
                                spaceShip.toBeRemoved = true;
                                players.add(new Player(spaceShip.score, spaceShip.isPlayer1 ? SPACEFX_COLOR : SPACEFX_COLOR1));
                            }
                        }
                        enemyBoss.toBeRemoved = true;
                    }
                }
            });
        }

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
                if (Helper.isHitCircleCircle(torpedo.x, torpedo.y, torpedo.radius, levelBoss.x, levelBoss.y, levelBoss.radius)) {
                    levelBoss.hits -= TORPEDO_DAMAGE;
                    if (levelBoss.hits <= 0) {
                        levelBossExplosions.add(new LevelBossExplosion(levelBoss.x - LEVEL_BOSS_EXPLOSION_FRAME_WIDTH * 0.25, levelBoss.y - LEVEL_BOSS_EXPLOSION_FRAME_HEIGHT * 0.25, levelBoss.vX, levelBoss.vY, 1.0));
                        torpedo.spaceShip.score += levelBoss.value;
                        torpedo.spaceShip.levelBossedKilled++;
                        kills++;
                        levelBoss.toBeRemoved = true;
                        levelBossActive = false;
                        levelKills = 0;
                        nextLevel();
                        torpedo.toBeRemoved = true;
                        playSound(levelBossExplosionSound);
                    } else {
                        enemyHits.add(new EnemyHit(torpedo.x - HIT_FRAME_CENTER, torpedo.y - HIT_FRAME_HEIGHT, levelBoss.vX, levelBoss.vY));
                        torpedo.toBeRemoved = true;
                        playSound(enemyHitSound);
                    }
                }
            }

            // Check for bigTorpedo hits with enemy boss
            for (BigTorpedo bigTorpedo : bigTorpedos) {
                if (Helper.isHitCircleCircle(bigTorpedo.x, bigTorpedo.y, bigTorpedo.radius, levelBoss.x, levelBoss.y, levelBoss.radius)) {
                    levelBoss.hits -= BIG_TORPEDO_DAMAGE;
                    if (levelBoss.hits <= 0) {
                        levelBossExplosions.add(new LevelBossExplosion(levelBoss.x - LEVEL_BOSS_EXPLOSION_FRAME_WIDTH * 0.25, levelBoss.y - LEVEL_BOSS_EXPLOSION_FRAME_HEIGHT * 0.25, levelBoss.vX, levelBoss.vY, 1.0));
                        bigTorpedo.spaceShip.score += levelBoss.value;
                        bigTorpedo.spaceShip.levelBossedKilled++;
                        kills++;
                        levelBoss.toBeRemoved = true;
                        levelBossActive = false;
                        levelKills = 0;
                        nextLevel();
                        bigTorpedo.toBeRemoved = true;
                        playSound(levelBossExplosionSound);
                    } else {
                        enemyHits.add(new EnemyHit(bigTorpedo.x - HIT_FRAME_CENTER, bigTorpedo.y - HIT_FRAME_HEIGHT, levelBoss.vX, levelBoss.vY));
                        bigTorpedo.toBeRemoved = true;
                        playSound(enemyHitSound);
                    }
                }
            }

            // Check for rocket hits with level boss
            for (Rocket rocket : rockets) {
                if (Helper.isHitCircleCircle(rocket.x, rocket.y, rocket.radius, levelBoss.x, levelBoss.y, levelBoss.radius)) {
                    levelBoss.hits -= ROCKET_DAMAGE;
                    if (levelBoss.hits <= 0) {
                        levelBossExplosions.add(new LevelBossExplosion(levelBoss.x - LEVEL_BOSS_EXPLOSION_FRAME_WIDTH * 0.25, levelBoss.y - LEVEL_BOSS_EXPLOSION_FRAME_HEIGHT * 0.25, levelBoss.vX, levelBoss.vY, 1.0));
                        rocket.spaceShip.score += levelBoss.value;
                        rocket.spaceShip.levelBossedKilled++;
                        kills++;
                        levelKills++;
                        levelBoss.toBeRemoved = true;
                        levelBossActive = false;
                        levelKills = 0;
                        nextLevel();
                        rocket.toBeRemoved = true;
                        playSound(levelBossExplosionSound);
                    } else {
                        enemyHits.add(new EnemyHit(rocket.x - HIT_FRAME_CENTER, rocket.y - HIT_FRAME_HEIGHT, levelBoss.vX, levelBoss.vY));
                        rocket.toBeRemoved = true;
                        playSound(enemyHitSound);
                    }
                }
            }

            // Check for space ship hit with level boss
            spaceShips.forEach(spaceShip -> {
                if (spaceShip.isVulnerable && !spaceShip.hasBeenHit) {
                    boolean hit;
                    if (spaceShip.shield) {
                        hit = Helper.isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, levelBoss.x, levelBoss.y, levelBoss.radius);
                    } else {
                        hit = Helper.isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, levelBoss.x, levelBoss.y, levelBoss.radius);
                    }
                    if (hit) {
                        if (spaceShip.shield) {
                            spaceShip.lastShieldActivated = 0;
                            levelBoss.hits -= SHIELD_DAMAGE;
                            if (levelBoss.hits <= 0) {
                                levelBossExplosions.add(new LevelBossExplosion(levelBoss.x - LEVEL_BOSS_EXPLOSION_FRAME_WIDTH * 0.25, levelBoss.y - LEVEL_BOSS_EXPLOSION_FRAME_HEIGHT * 0.25, levelBoss.vX, levelBoss.vY, 1.0));
                                spaceShip.score += levelBoss.value;
                                spaceShip.levelBossedKilled++;
                                kills++;
                                levelKills++;
                                levelBoss.toBeRemoved = true;
                                levelBossActive = false;
                                levelKills = 0;
                                nextLevel();
                                playSound(levelBossExplosionSound);
                            }
                        } else {
                            spaceShipExplosions.add(new SpaceShipExplosion(spaceShip,spaceShip.x - SPACESHIP_EXPLOSION_FRAME_WIDTH * 0.5, spaceShip.y - SPACESHIP_EXPLOSION_FRAME_HEIGHT * 0.5, spaceShip.vX, spaceShip.vY));
                            playSound(spaceShipExplosionSound);
                            spaceShip.hasBeenHit = true;
                            spaceShip.noOfLifes--;
                            if (0 == spaceShip.noOfLifes) {
                                spaceShip.toBeRemoved = true;
                                players.add(new Player(spaceShip.score, spaceShip.isPlayer1 ? SPACEFX_COLOR : SPACEFX_COLOR1));
                            }
                        }
                        levelBoss.toBeRemoved = true;
                        levelBossActive = false;
                        levelKills = 0;
                        nextLevel();
                    }
                }
            });
        }

        // Draw Bonuses
        for (Bonus bonus : bonuses) {
            bonus.update();
            ctx.save();
            ctx.translate(bonus.cX, bonus.cY);
            ctx.rotate(bonus.rot);
            ctx.translate(-bonus.imgCenterX, -bonus.imgCenterY);
            ctx.drawImage(bonus.image, 0, 0);
            ctx.restore();

            // Check for space ship contact
            spaceShips.forEach(spaceShip -> {
                boolean hit;
                if (spaceShip.shield) {
                    hit = Helper.isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, bonus.cX, bonus.cY, bonus.radius);
                } else {
                    hit = Helper.isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, bonus.cX, bonus.cY, bonus.radius);
                }
                if (hit) {
                    if (bonus instanceof LifeUp) {
                        if (spaceShip.noOfLifes <= NO_OF_LIFES - 1) {
                            spaceShip.noOfLifes++;
                            spaceShip.lifesCollected++;
                        }
                        playSound(lifeUpSound);
                    } else if (bonus instanceof ShieldUp) {
                        if (spaceShip.noOfShields <= NO_OF_SHIELDS - 1) {
                            spaceShip.noOfShields++;
                            spaceShip.shieldsCollected++;
                        }
                        playSound(shieldUpSound);
                    } else if (bonus instanceof BigTorpedoBonus) {
                        spaceShip.bigTorpedosEnabled = true;
                        spaceShip.bigTorpedosCollected++;
                        playSound(bonusSound);
                    } else if (bonus instanceof StarburstBonus) {
                        spaceShip.starburstEnabled = true;
                        spaceShip.starburstsCollected++;
                        playSound(bonusSound);
                    }
                    upExplosions.add(new UpExplosion(bonus.cX - UP_EXPLOSION_FRAME_CENTER, bonus.cY - UP_EXPLOSION_FRAME_CENTER, bonus.vX, bonus.vY, 1.0));
                    bonus.toBeRemoved = true;
                }
            });
        }

        // Draw Torpedos
        for (Torpedo torpedo : torpedos) {
            torpedo.update();
            ctx.drawImage(torpedo.image, torpedo.x - torpedo.radius, torpedo.y - torpedo.radius);
        }

        // Draw BigTorpedos
        for (BigTorpedo bigTorpedo : bigTorpedos) {
            bigTorpedo.update();
            ctx.save();
            ctx.translate(bigTorpedo.x - bigTorpedo.width / 2, bigTorpedo.y - bigTorpedo.height / 2);
            ctx.save();
            ctx.translate(bigTorpedo.width / 2, bigTorpedo.height / 2);
            ctx.rotate(bigTorpedo.r - 45);
            ctx.translate(-bigTorpedo.width / 2, -bigTorpedo.height / 2);
            ctx.drawImage(bigTorpedo.image, 0, 0);
            ctx.restore();
            ctx.restore();
        }

        // Draw Rockets
        for (Rocket rocket : rockets) {
            rocket.update();
            ctx.drawImage(rocket.image, rocket.x - rocket.halfWidth, rocket.y - rocket.halfHeight);
        }

        // Draw EnemyTorpedos
        for (EnemyTorpedo enemyTorpedo : enemyTorpedos) {
            enemyTorpedo.update();
            ctx.drawImage(enemyTorpedo.image, enemyTorpedo.x, enemyTorpedo.y);
        }

        // Draw EnemyBombs
        for (EnemyBomb enemyBomb : enemyBombs) {
            enemyBomb.update();
            ctx.drawImage(enemyBomb.image, enemyBomb.x, enemyBomb.y);
        }

        // Draw EnemyBossTorpedos
        for (EnemyBossTorpedo enemyBossTorpedo : enemyBossTorpedos) {
            enemyBossTorpedo.update();
            ctx.drawImage(enemyBossTorpedo.image, enemyBossTorpedo.x, enemyBossTorpedo.y);
        }

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

        // Draw LevelBossBombs
        for (LevelBossBomb levelBossBomb : levelBossBombs) {
            levelBossBomb.update();
            ctx.drawImage(levelBossBomb.image, levelBossBomb.x, levelBossBomb.y);
        }

        // Draw SpaceShipExplosions
        for (SpaceShipExplosion spaceShipExplosion : spaceShipExplosions) {
            spaceShipExplosion.update();
            ctx.drawImage(spaceShipExplosionImg, spaceShipExplosion.countX * SPACESHIP_EXPLOSION_FRAME_WIDTH, spaceShipExplosion.countY * SPACESHIP_EXPLOSION_FRAME_HEIGHT, SPACESHIP_EXPLOSION_FRAME_WIDTH, SPACESHIP_EXPLOSION_FRAME_HEIGHT,
                          spaceShipExplosion.x, spaceShipExplosion.y, SPACESHIP_EXPLOSION_FRAME_WIDTH * spaceShipExplosion.scale, SPACESHIP_EXPLOSION_FRAME_HEIGHT * spaceShipExplosion.scale);
        }

        // Draw Explosions
        for (Explosion explosion : explosions) {
            explosion.update();
            ctx.drawImage(level.getExplosionImg(), explosion.countX * EXPLOSION_FRAME_WIDTH, explosion.countY * EXPLOSION_FRAME_HEIGHT, EXPLOSION_FRAME_WIDTH, EXPLOSION_FRAME_HEIGHT,
                          explosion.x, explosion.y, EXPLOSION_FRAME_WIDTH * explosion.scale, EXPLOSION_FRAME_HEIGHT * explosion.scale);
        }

        // Draw AsteroidExplosions
        for (AsteroidExplosion asteroidExplosion : asteroidExplosions) {
            asteroidExplosion.update();
            ctx.drawImage(asteroidExplosion.player1Explosion ? asteroidExplosionImg :  asteroidExplosion1Img, asteroidExplosion.countX * ASTEROID_EXPLOSION_FRAME_WIDTH, asteroidExplosion.countY * ASTEROID_EXPLOSION_FRAME_HEIGHT,
                          ASTEROID_EXPLOSION_FRAME_WIDTH, ASTEROID_EXPLOSION_FRAME_HEIGHT, asteroidExplosion.x, asteroidExplosion.y,
                          ASTEROID_EXPLOSION_FRAME_WIDTH * asteroidExplosion.scale, ASTEROID_EXPLOSION_FRAME_HEIGHT * asteroidExplosion.scale);
        }

        // Draw RocketExplosions
        for (RocketExplosion rocketExplosion : rocketExplosions) {
            rocketExplosion.update();
            ctx.drawImage(rocketExplosionImg, rocketExplosion.countX * ROCKET_EXPLOSION_FRAME_WIDTH, rocketExplosion.countY * ROCKET_EXPLOSION_FRAME_HEIGHT, ROCKET_EXPLOSION_FRAME_WIDTH, ROCKET_EXPLOSION_FRAME_HEIGHT, rocketExplosion.x, rocketExplosion.y, ROCKET_EXPLOSION_FRAME_WIDTH * rocketExplosion.scale,
                          ROCKET_EXPLOSION_FRAME_HEIGHT * rocketExplosion.scale);
        }

        // Draw EnemyRocketExplosions
        for (EnemyRocketExplosion enemyRocketExplosion : enemyRocketExplosions) {
            enemyRocketExplosion.update();
            ctx.drawImage(level.getEnemyRocketExplosionImg(), enemyRocketExplosion.countX * ENEMY_ROCKET_EXPLOSION_FRAME_WIDTH, enemyRocketExplosion.countY * ENEMY_ROCKET_EXPLOSION_FRAME_HEIGHT, ENEMY_ROCKET_EXPLOSION_FRAME_WIDTH, ENEMY_ROCKET_EXPLOSION_FRAME_HEIGHT, enemyRocketExplosion.x, enemyRocketExplosion.y, ENEMY_ROCKET_EXPLOSION_FRAME_WIDTH * enemyRocketExplosion.scale,
                          ENEMY_ROCKET_EXPLOSION_FRAME_HEIGHT * enemyRocketExplosion.scale);
        }

        // Draw EnemyBossExplosions
        for (EnemyBossExplosion enemyBossExplosion : enemyBossExplosions) {
            enemyBossExplosion.update();
            ctx.drawImage(level.getEnemyBossExplosionImg(), enemyBossExplosion.countX * ENEMY_BOSS_EXPLOSION_FRAME_WIDTH, enemyBossExplosion.countY * ENEMY_BOSS_EXPLOSION_FRAME_HEIGHT,
                          ENEMY_BOSS_EXPLOSION_FRAME_WIDTH, ENEMY_BOSS_EXPLOSION_FRAME_HEIGHT, enemyBossExplosion.x, enemyBossExplosion.y,
                          ENEMY_BOSS_EXPLOSION_FRAME_WIDTH * enemyBossExplosion.scale, ENEMY_BOSS_EXPLOSION_FRAME_HEIGHT * enemyBossExplosion.scale);
        }

        // Draw LevelBossExplosions
        for (LevelBossExplosion levelBossExplosion : levelBossExplosions) {
            levelBossExplosion.update();
            ctx.drawImage(level.getLevelBossExplosionImg(), levelBossExplosion.countX * LEVEL_BOSS_EXPLOSION_FRAME_WIDTH, levelBossExplosion.countY * LEVEL_BOSS_EXPLOSION_FRAME_HEIGHT,
                          LEVEL_BOSS_EXPLOSION_FRAME_WIDTH, LEVEL_BOSS_EXPLOSION_FRAME_HEIGHT, levelBossExplosion.x, levelBossExplosion.y,
                          LEVEL_BOSS_EXPLOSION_FRAME_WIDTH * levelBossExplosion.scale, LEVEL_BOSS_EXPLOSION_FRAME_HEIGHT * levelBossExplosion.scale);
        }

        // Draw UpExplosions
        for (UpExplosion upExplosion : upExplosions) {
            upExplosion.update();
            ctx.drawImage(upExplosionImg, upExplosion.countX * UP_EXPLOSION_FRAME_WIDTH, upExplosion.countY * UP_EXPLOSION_FRAME_HEIGHT, UP_EXPLOSION_FRAME_WIDTH, UP_EXPLOSION_FRAME_HEIGHT, upExplosion.x, upExplosion.y,
                          UP_EXPLOSION_FRAME_WIDTH * upExplosion.scale, UP_EXPLOSION_FRAME_HEIGHT * upExplosion.scale);
        }

        // Draw Hits
        for (Hit hit : hits) {
            hit.update();
            ctx.drawImage(hitImg, hit.countX * HIT_FRAME_WIDTH, hit.countY * HIT_FRAME_HEIGHT, HIT_FRAME_WIDTH, HIT_FRAME_HEIGHT, hit.x, hit.y, HIT_FRAME_WIDTH, HIT_FRAME_HEIGHT);
        }

        // Draw EnemyBoss Hits
        for (EnemyHit hit : enemyHits) {
            hit.update();
            ctx.drawImage(level.getEnemyBossHitImg(), hit.countX * ENEMY_HIT_FRAME_WIDTH, hit.countY * ENEMY_HIT_FRAME_HEIGHT, ENEMY_HIT_FRAME_WIDTH, ENEMY_HIT_FRAME_HEIGHT, hit.x, hit.y, ENEMY_HIT_FRAME_WIDTH, ENEMY_HIT_FRAME_HEIGHT);
        }

        // Draw Spaceship, score, lifes and shields
        spaceShips.forEach(spaceShip -> {
            if (spaceShip.noOfLifes > 0) {
                // Draw Spaceship or it's explosion
                if (spaceShip.hasBeenHit) {
                    spaceShip.respawn();
                } else {
                    // Draw space ship
                    spaceShip.update();

                    ctx.save();
                    ctx.setGlobalAlpha(spaceShip.isVulnerable ? 1.0 : 0.5);
                    if (spaceShip.vY < 0) {
                        ctx.drawImage(spaceShip.isPlayer1 ? spaceshipUpImg : spaceshipUp1Img, spaceShip.x - spaceShip.radius, spaceShip.y - spaceShip.radius);
                    } else if (spaceShip.vY > 0) {
                        ctx.drawImage(spaceShip.isPlayer1 ? spaceshipDownImg : spaceshipDown1Img, spaceShip.x - spaceShip.radius, spaceShip.y - spaceShip.radius);
                    } else {
                        ctx.drawImage(spaceShip.isPlayer1 ? spaceshipImg : spaceship1Img, spaceShip.x - spaceShip.radius, spaceShip.y - spaceShip.radius);
                    }

                    ctx.restore();

                    if (spaceShip.shield) {
                        long delta = System.nanoTime() - spaceShip.lastShieldActivated;
                        if (delta > DEFLECTOR_SHIELD_TIME) {
                            spaceShip.shield = false;
                            spaceShip.noOfShields--;
                        } else {
                            if (spaceShip.isPlayer1) {
                                ctx.setStroke(SPACEFX_COLOR_TRANSLUCENT);
                                ctx.setFill(SPACEFX_COLOR_TRANSLUCENT);
                                ctx.strokeRect(SHIELD_INDICATOR_X, SHIELD_INDICATOR_Y + mobileOffsetY, SHIELD_INDICATOR_WIDTH, SHIELD_INDICATOR_HEIGHT);
                                ctx.fillRect(SHIELD_INDICATOR_X, SHIELD_INDICATOR_Y + mobileOffsetY, SHIELD_INDICATOR_WIDTH - SHIELD_INDICATOR_WIDTH * delta / DEFLECTOR_SHIELD_TIME, SHIELD_INDICATOR_HEIGHT);
                                ctx.setGlobalAlpha(RND.nextDouble() * 0.5 + 0.1);
                                ctx.drawImage(deflectorShieldImg, spaceShip.x - deflectorShieldRadius, spaceShip.y - deflectorShieldRadius);
                                ctx.setGlobalAlpha(1);
                            } else {
                                ctx.setStroke(SPACEFX_COLOR1_TRANSLUCENT);
                                ctx.setFill(SPACEFX_COLOR1_TRANSLUCENT);
                                ctx.strokeRect(SHIELD_INDICATOR_X, SECOND_PLAYER_OFFSET_Y + 20 + mobileOffsetY, SHIELD_INDICATOR_WIDTH, SHIELD_INDICATOR_HEIGHT);
                                ctx.fillRect(SHIELD_INDICATOR_X, SECOND_PLAYER_OFFSET_Y + 20 + mobileOffsetY, SHIELD_INDICATOR_WIDTH - SHIELD_INDICATOR_WIDTH * delta / DEFLECTOR_SHIELD_TIME, SHIELD_INDICATOR_HEIGHT);
                                ctx.setGlobalAlpha(RND.nextDouble() * 0.5 + 0.1);
                                ctx.drawImage(deflectorShieldImg, spaceShip.x - deflectorShieldRadius, spaceShip.y - deflectorShieldRadius);
                                ctx.setGlobalAlpha(1);
                            }
                        }
                    }

                    if (spaceShip.bigTorpedosEnabled) {
                        long delta = System.nanoTime() - lastBigTorpedoBonus;
                        if (delta > BIG_TORPEDO_TIME) {
                            spaceShip.bigTorpedosEnabled = false;
                        }
                    }

                    if (spaceShip.starburstEnabled) {
                        long delta = System.nanoTime() - lastStarburstBonus;
                        if (delta > STARBURST_TIME) {
                            spaceShip.starburstEnabled = false;
                        }
                    }
                }

                ctx.setFont(scoreFont);
                if (spaceShip.isPlayer1) {
                    // Draw score
                    ctx.setFill(SPACEFX_COLOR);
                    ctx.fillText(Long.toString(spaceShip.score), scorePosX, scorePosY + mobileOffsetY);

                    // Draw lifes
                    for (int i = 0 ; i < spaceShip.noOfLifes ; i++) {
                        ctx.drawImage(miniSpaceshipImg, i * miniSpaceshipImg.getWidth() + 10, 20 + mobileOffsetY);
                    }

                    // Draw shields
                    for (int i = 0 ; i < spaceShip.noOfShields ; i++) {
                        ctx.drawImage(miniDeflectorShieldImg, WIDTH - i * (miniDeflectorShieldImg.getWidth() + 5), 20 + mobileOffsetY);
                    }

                    // Draw bigTorpedo and starburst icon
                    if (spaceShip.starburstEnabled) {
                        ctx.drawImage(miniStarburstBonusImg, 10, 40 + mobileOffsetY);
                    } else if (spaceShip.bigTorpedosEnabled) {
                        ctx.drawImage(miniBigTorpedoBonusImg, 10, 40 + mobileOffsetY);
                    }
                } else {
                    // Draw score
                    ctx.setFill(SPACEFX_COLOR1);
                    ctx.fillText(Long.toString(spaceShip.score), scorePosX, scorePosY + mobileOffsetY + scoreFont.getSize() * 2);

                    // Draw lifes
                    for (int i = 0 ; i < spaceShip.noOfLifes ; i++) {
                        ctx.drawImage(miniSpaceship1Img, i * miniSpaceship1Img.getWidth() + 10, 20 + SECOND_PLAYER_OFFSET_Y + mobileOffsetY);
                    }

                    // Draw shields
                    for (int i = 0 ; i < spaceShip.noOfShields ; i++) {
                        ctx.drawImage(miniDeflectorShield1Img, WIDTH - i * (miniDeflectorShield1Img.getWidth() + 5), 20 + SECOND_PLAYER_OFFSET_Y + mobileOffsetY);
                    }

                    // Draw bigTorpedo and starburst icon
                    if (spaceShip.starburstEnabled) {
                        ctx.drawImage(miniStarburstBonus1Img, 10, 40 + SECOND_PLAYER_OFFSET_Y + mobileOffsetY);
                    } else if (spaceShip.bigTorpedosEnabled) {
                        ctx.drawImage(miniBigTorpedoBonus1Img, 10, 40 + SECOND_PLAYER_OFFSET_Y + mobileOffsetY);
                    }
                }
            }
        });

        // Draw Buttons
        if (SHOW_BUTTONS) {
            ctx.drawImage(torpedoButtonImg, TORPEDO_BUTTON_X, TORPEDO_BUTTON_Y);
            ctx.drawImage(rocketButtonImg, ROCKET_BUTTON_X, ROCKET_BUTTON_Y);
            ctx.drawImage(shieldButtonImg, SHIELD_BUTTON_X, SHIELD_BUTTON_Y);
        }

        // Remove sprites
        spaceShips.removeIf(sprite -> sprite.toBeRemoved);
        spaceShipExplosions.removeIf(sprite -> sprite.toBeRemoved);
        enemyBosses.removeIf(sprite -> sprite.toBeRemoved);
        levelBosses.removeIf(sprite -> sprite.toBeRemoved);
        bonuses.removeIf(sprite -> sprite.toBeRemoved);
        torpedos.removeIf(sprite -> sprite.toBeRemoved);
        bigTorpedos.removeIf(sprite -> sprite.toBeRemoved);
        rockets.removeIf(sprite -> sprite.toBeRemoved);
        enemyTorpedos.removeIf(sprite -> sprite.toBeRemoved);
        enemyBombs.removeIf(sprite -> sprite.toBeRemoved);
        enemyBossTorpedos.removeIf(sprite -> sprite.toBeRemoved);
        enemyBossRockets.removeIf(sprite -> sprite.toBeRemoved);
        levelBossTorpedos.removeIf(sprite -> sprite.toBeRemoved);
        levelBossRockets.removeIf(sprite -> sprite.toBeRemoved);
        levelBossBombs.removeIf(sprite -> sprite.toBeRemoved);
        levelBossExplosions.removeIf(sprite -> sprite.toBeRemoved);
        enemyBossExplosions.removeIf(sprite -> sprite.toBeRemoved);
        enemyRocketExplosions.removeIf(sprite -> sprite.toBeRemoved);
        rocketExplosions.removeIf(sprite -> sprite.toBeRemoved);
        explosions.removeIf(sprite -> sprite.toBeRemoved);
        asteroidExplosions.removeIf(sprite -> sprite.toBeRemoved);
        upExplosions.removeIf(sprite -> sprite.toBeRemoved);
        hits.removeIf(sprite -> sprite.toBeRemoved);
        enemyHits.removeIf(sprite -> sprite.toBeRemoved);

        if (spaceShips.isEmpty()) {
            if (gameOverStart < 0) {
                gameOverStart = System.nanoTime();
                playSound(gameoverSound);
            }
            if (System.nanoTime() - gameOverStart < 5_000_000_000l) {
                ctx.setFont(GAME_OVER_FONT);
                ctx.setFill(SPACEFX_COLOR);
                ctx.fillText("GAME", WIDTH * 0.5, HEIGHT * 0.5 - GAME_OVER_FONT.getSize() * 0.75);
                ctx.fillText("OVER", WIDTH * 0.5, HEIGHT * 0.5 + GAME_OVER_FONT.getSize() * 0.75);
            } else {
                gameOverStart = -1;
                gameOver();
            }
        }

        if (null != spaceShipPlayer1 && spaceShipPlayer1.toBeRemoved) { spaceShipPlayer1 = null; }
        if (null != spaceShipPlayer2 && spaceShipPlayer2.toBeRemoved) { spaceShipPlayer2 = null; }

        // Remove waves
        wavesToRemove.clear();
    }


    // Spawn different objects
    private void spawnSpaceShip() {
        if (spaceShips.size() < 2) {
            if (spaceShips.stream().filter(sp -> sp.isPlayer1).findFirst().isPresent()) {
                spaceShipPlayer2 = new SpaceShip(spaceship1Img, spaceshipUp1Img, spaceshipDown1Img);
                spaceShipPlayer2.x = WIDTH * 0.6;
                spaceShipPlayer2.y = HEIGHT - 2 * spaceShipPlayer2.height;
                spaceShips.add(spaceShipPlayer2);
            } else {
                spaceShipPlayer1 = new SpaceShip(true, spaceshipImg, spaceshipUpImg, spaceshipDownImg);
                spaceShipPlayer1.x = MULTI_PLAYER ? WIDTH * 0.4 : WIDTH * 0.5;
                spaceShipPlayer1.y = HEIGHT - 2 * spaceShipPlayer1.height;
                shipTouchArea.setCenterX(spaceShipPlayer1.x);
                shipTouchArea.setCenterY(spaceShipPlayer1.y);
                shipTouchArea.setRadius(deflectorShieldRadius);
                shipTouchArea.setStroke(Color.TRANSPARENT);
                shipTouchArea.setFill(Color.TRANSPARENT);
                spaceShips.add(spaceShipPlayer1);
            }
        }
    }

    private void spawnWeapon(final SpaceShip spaceShip) {
        if (spaceShip.starburstEnabled) {
            fireStarburst(spaceShip);
        } else if (spaceShip.bigTorpedosEnabled) {
            bigTorpedos.add(new BigTorpedo(spaceShip, spaceShip.isPlayer1 ? bigTorpedoImg : bigTorpedo1Img, spaceShip.x, spaceShip.y, 0, -BIG_TORPEDO_SPEED * 2.333333, 45));
        } else {
            torpedos.add(new Torpedo(spaceShip, spaceShip.isPlayer1 ? torpedoImg : torpedo1Img, spaceShip.x, spaceShip.y));
        }
        playSound(laserSound);
    }

    private void spawnBigTorpedo(final SpaceShip spaceShip) {
        bigTorpedos.add(new BigTorpedo(spaceShip, spaceShip.isPlayer1 ? bigTorpedoImg : bigTorpedo1Img, spaceShip.x, spaceShip.y, 0, -BIG_TORPEDO_SPEED * 2.333333, 45));
        playSound(laserSound);
    }

    private void spawnRocket(final SpaceShip spaceShip) {
        rockets.add(new Rocket(spaceShip, spaceShip.isPlayer1 ? rocketImg : rocket1Img, spaceShip.x, spaceShip.y));
        playSound(rocketLaunchSound);
    }

    private void spawnEnemyTorpedo(final double x, final double y, final double vX, final double vY) {
        double vFactor = ENEMY_TORPEDO_SPEED / Math.abs(vY); // make sure the speed is always the defined one
        enemyTorpedos.add(new EnemyTorpedo(spaceShips, level.getEnemyTorpedoImg(), x, y, vFactor * vX, vFactor * vY));
        playSound(enemyLaserSound);
    }

    private void spawnEnemyBomb(final double x, final double y) {
        enemyBombs.add(new EnemyBomb(spaceShips, level.getEnemyBombImg(), x, y, 0, ENEMY_BOMB_SPEED));
        playSound(enemyBombSound);
    }

    private void spawnEnemyBoss(final List<SpaceShip> spaceShips) {
        if (levelBossActive || !SHOW_ENEMY_BOSS) { return; }
        enemyBosses.add(new EnemyBoss(spaceShips, level.getEnemyBossImg4(), RND.nextBoolean()));
    }

    private void spawnLevelBoss(final List<SpaceShip> spaceShips) {
        if (levelBossActive) { return; }
        levelBossActive = true;
        levelBosses.add(new LevelBoss(spaceShips, level.getLevelBossImg(), true, true));
    }

    private void spawnShieldUp() {
        bonuses.add(new ShieldUp(shieldUpImg));
    }

    private void spawnLifeUp() {
        bonuses.add(new LifeUp(lifeUpImg));
    }

    private void spawnBigTorpedoBonus() {
        bonuses.add(new BigTorpedoBonus(bigTorpedoBonusImg));
    }

    private void spawnStarburstBonus() {
        if (level.equals(level1)) { return; }
        bonuses.add(new StarburstBonus(starburstBonusImg));
    }

    private void spawnWave() {
        switch (level.getDifficulty()) {
            case EASY:
                if (levelKills < NO_OF_KILLS_STAGE_1 && !levelBossActive) {
                    waves.add(new Wave(WAVE_TYPES_SLOW[RND.nextInt(WAVE_TYPES_SLOW.length)], spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], false, false));
                } else if (levelKills >= NO_OF_KILLS_STAGE_1 && levelKills < NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_MEDIUM[RND.nextInt(WAVE_TYPES_MEDIUM.length)], spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_SLOW, WaveType.TYPE_11_SLOW, spaceShips, 10, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], false, false));
                    }
                } else if (levelKills >= NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    spawnLevelBoss(spaceShips);
                } else if (!levelBossActive) {
                    waves.add(new Wave(WAVE_TYPES_MEDIUM[RND.nextInt(WAVE_TYPES_MEDIUM.length)], spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                }
                break;
            case NORMAL:
                if (levelKills < NO_OF_KILLS_STAGE_1 && !levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_MEDIUM[RND.nextInt(WAVE_TYPES_MEDIUM.length)], spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], false, false));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_MEDIUM, WaveType.TYPE_11_MEDIUM, spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], false, false));
                    }
                } else if (levelKills >= NO_OF_KILLS_STAGE_1 && levelKills < NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_FAST[RND.nextInt(WAVE_TYPES_FAST.length)], spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_FAST, WaveType.TYPE_11_FAST, spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    }
                } else if (levelKills >= NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    spawnLevelBoss(spaceShips);
                } else if (!levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_FAST[RND.nextInt(WAVE_TYPES_FAST.length)], spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_FAST, WaveType.TYPE_11_FAST, spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    }
                }
                break;
            case HARD:
                if (levelKills < NO_OF_KILLS_STAGE_1 && !levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_FAST[RND.nextInt(WAVE_TYPES_FAST.length)], spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_FAST, WaveType.TYPE_11_FAST, spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, false));
                    }
                } else if (levelKills >= NO_OF_KILLS_STAGE_1 && levelKills < NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_FAST[RND.nextInt(WAVE_TYPES_FAST.length)], spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_FAST, WaveType.TYPE_11_FAST, spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                    }
                } else if (levelKills >= NO_OF_KILLS_STAGE_2 && !levelBossActive) {
                    spawnLevelBoss(spaceShips);
                } else if (!levelBossActive) {
                    if (RND.nextBoolean()) {
                        waves.add(new Wave(WAVE_TYPES_FAST[RND.nextInt(WAVE_TYPES_FAST.length)], spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                    } else {
                        waves.add(new Wave(WaveType.TYPE_10_FAST, WaveType.TYPE_11_FAST, spaceShips, level.getDifficulty().noOfEnemies, level.getEnemyImages()[RND.nextInt(level.getEnemyImages().length)], true, true));
                    }
                }
                break;
        }
    }

    private void spawnEnemyBossTorpedo(final double x, final double y, final double vX, final double vY) {
        double factor = ENEMY_BOSS_TORPEDO_SPEED / Math.abs(vY); // make sure the speed is always the defined one
        enemyBossTorpedos.add(new EnemyBossTorpedo(spaceShips, level.getEnemyBossTorpedoImg(), x, y, factor * vX, factor * vY));
        playSound(enemyLaserSound);
    }

    private void spawnEnemyBossRocket(final double x, final double y) {
        Map<SpaceShip, Double> shipDistanceMap = new HashMap<>();
        spaceShips.forEach(spaceShip -> {
            double dX = spaceShip.x - x;
            double dY = spaceShip.y - y;
            shipDistanceMap.put(spaceShip, Math.sqrt(dX * dX + dY * dY));
        });
        SpaceShip spaceShipToAttack = shipDistanceMap.entrySet()
                                           .stream()
                                           .min(Comparator.comparingDouble(Map.Entry::getValue))
                                           .map(Map.Entry::getKey).orElse(null);
        enemyBossRockets.add(new EnemyBossRocket(spaceShipToAttack, spaceShips, level.getEnemyBossRocketImg(), x, y));
        playSound(rocketLaunchSound);
    }

    private void spawnLevelBossTorpedo(final double x, final double y, final double vX, final double vY, final double r) {
        double factor = LEVEL_BOSS_TORPEDO_SPEED / Math.abs(vY); // make sure the speed is always the defined one
        levelBossTorpedos.add(new LevelBossTorpedo(spaceShips, level.getLevelBossTorpedoImg(), x, y, factor * vX, factor * vY, r));
        playSound(levelBossTorpedoSound);
    }

    private void spawnLevelBossRocket(final double x, final double y) {
        Map<SpaceShip, Double> shipDistanceMap = new HashMap<>();
        spaceShips.forEach(spaceShip -> {
            double dX = spaceShip.x - x;
            double dY = spaceShip.y - y;
            shipDistanceMap.put(spaceShip, Math.sqrt(dX * dX + dY * dY));
        });
        SpaceShip spaceShipToAttack = shipDistanceMap.entrySet()
                                                     .stream()
                                                     .min(Comparator.comparingDouble(Map.Entry::getValue))
                                                     .map(Map.Entry::getKey).orElse(null);
        levelBossRockets.add(new LevelBossRocket(spaceShipToAttack, spaceShips, level.getLevelBossRocketImg(), x, y));
        playSound(levelBossRocketSound);
    }

    private void spawnLevelBossBomb(final double x, final double y) {
        levelBossBombs.add(new LevelBossBomb(spaceShips, level.getLevelBossBombImg(), x, y, 0, LEVEL_BOSS_BOMB_SPEED));
        playSound(levelBossBombSound);
    }
    

    // Game Over
    private void gameOver() {
        timer.stop();
        running = false;
        if (PLAY_MUSIC) {
            gameMediaPlayer.pause();
        }

        ctx.clearRect(0, 0, WIDTH, HEIGHT);
        ctx.drawImage(hallOfFameImg, 0, 0, WIDTH, HEIGHT);
        players.forEach(player -> {
            boolean isInHallOfFame = player.score > hallOfFame.get(2).score;
            if (isInHallOfFame) {
                // Add player to hall of fame
                digit1.reset();
                digit2.reset();
                hallOfFameScreen = true;
                Helper.enableNode(hallOfFameBox, true);
                Helper.enableNode(playerInitialsLabel, true);
                Helper.enableNode(playerInitialsDigits, true);
                Helper.enableNode(saveInitialsButton, true);
                playerInitialsLabel.relocate((WIDTH - playerInitialsLabel.getPrefWidth()) * 0.5, HEIGHT * 0.7);
                playerInitialsDigits.relocate((WIDTH - digit1.getPrefWidth() - digit2.getPrefWidth()) * 0.5, HEIGHT * 0.8);
                saveInitialsButton.relocate((WIDTH - saveInitialsButton.getPrefWidth()) * 0.5, HEIGHT - saveInitialsButton.getPrefHeight() - HEIGHT * 0.075);
                saveInitialsButton.setOnAction(evt -> storePlayer(player));
                Platform.runLater(() -> playerInitialsDigits.requestFocus());
            }
        });

        PauseTransition pauseInHallOfFameScreen = new PauseTransition(Duration.millis(5000));
        pauseInHallOfFameScreen.setOnFinished(e -> reInitGame());
        pauseInHallOfFameScreen.play();
    }


    // Reinitialize game
    private void reInitGame() {
        ctx.clearRect(0, 0, WIDTH, HEIGHT);
        ctx.drawImage(startImg, 0, 0, WIDTH, HEIGHT);

        Helper.enableNode(hallOfFameBox, false);
        spaceShips.clear();
        spaceShipExplosions.clear();
        enemyBosses.clear();
        levelBosses.clear();
        bonuses.clear();
        torpedos.clear();
        bigTorpedos.clear();
        rockets.clear();
        enemyTorpedos.clear();
        enemyBombs.clear();
        enemyBossTorpedos.clear();
        enemyBossRockets.clear();
        levelBossTorpedos.clear();
        levelBossRockets.clear();
        levelBossBombs.clear();
        levelBossExplosions.clear();
        enemyBossExplosions.clear();
        enemyRocketExplosions.clear();
        rocketExplosions.clear();
        explosions.clear();
        asteroidExplosions.clear();
        upExplosions.clear();
        hits.clear();
        enemyHits.clear();
        players.clear();
        initAsteroids();
        level       = level1;
        kills       = 0;
        levelKills  = 0;
        if (PLAY_MUSIC) {
            mediaPlayer.play();
        }

        screenTimer.start();
    }


    // Create Hall of Fame entry
    private HBox createHallOfFameEntry(final Player player) {
        Label playerName  = new Label(player.name);
        playerName.setTextFill(SPACEFX_COLOR);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label playerScore = new Label(Long.toString(player.score));
        playerScore.setTextFill(SPACEFX_COLOR);
        playerScore.setAlignment(Pos.CENTER_RIGHT);

        HBox entry = new HBox(20, playerName, spacer, playerScore);
        entry.setPrefWidth(WIDTH);
        return entry;
    }


    // Play audio clips
    private void playSound(final AudioClip audioClip) {
        if (PLAY_SOUND) {
            audioClip.play();
        }
    }


    // Iterate through levels
    private void nextLevel() {
        playSound(levelUpSound);
        if (level3.equals(level)) {
            level = level1;
            return;
        } else if (level2.equals(level)) {
            level = level3;
            return;
        } else if (level1.equals(level)) {
            level = level2;
            return;
        }
    }


    private void increaseSpaceShipVx(final SpaceShip spaceShip) {
        if (null == spaceShip) { return; }
        spaceShip.vX = 5;
    }

    private void decreaseSpaceShipVx(final SpaceShip spaceShip) {
        if (null == spaceShip) { return; }
        spaceShip.vX = -5;
    }

    private void stopSpaceShipVx(final SpaceShip spaceShip) {
        if (null == spaceShip) { return; }
        spaceShip.vX = 0;
    }

    private void increaseSpaceShipVy(final SpaceShip spaceShip) {
        if (null == spaceShip) { return; }
        spaceShip.vY = -5;
    }

    private void decreaseSpaceShipVy(final SpaceShip spaceShip) {
        if (null == spaceShip) { return; }
        spaceShip.vY = 5;
    }

    private void stopSpaceShipVy(final SpaceShip spaceShip) {
        if (null == spaceShip) { return; }
        spaceShip.vY = 0;
    }

    private void activateSpaceShipShield(final SpaceShip spaceShip) {
        if (null == spaceShip) { return; }
        if (spaceShip.noOfShields > 0 && !spaceShip.shield) {
            spaceShip.lastShieldActivated = System.nanoTime();
            spaceShip.shield = true;
            playSound(deflectorShieldSound);
        }
    }

    private void fireSpaceShipRocket(final SpaceShip spaceShip) {
        if (null == spaceShip) { return; }
        if (rockets.size() < MAX_NO_OF_ROCKETS * spaceShips.size()) {
            spawnRocket(spaceShip);
        }
    }

    private void fireSpaceShipWeapon(final SpaceShip spaceShip) {
        if (null == spaceShip) { return; }
        if (System.nanoTime() - spaceShip.lastTorpedoFired < MIN_TORPEDO_INTERVAL) { return; }
        spawnWeapon(spaceShip);
        spaceShip.lastTorpedoFired = System.nanoTime();
    }

    private void fireStarburst(final SpaceShip spaceShip) {
        if (null == spaceShip) { return; }
        if (!spaceShip.starburstEnabled || (System.nanoTime() - lastStarBlast < MIN_STARBURST_INTERVAL)) { return; }
        double offset    = Math.toRadians(-135);
        double angleStep = Math.toRadians(22.5);
        double angle     = 0;
        double x         = spaceShip.x;
        double y         = spaceShip.y;
        double vX;
        double vY;
        for (int i = 0 ; i < 5 ; i++) {
            vX = BIG_TORPEDO_SPEED * Math.cos(offset + angle);
            vY = BIG_TORPEDO_SPEED * Math.sin(offset + angle);
            bigTorpedos.add(new BigTorpedo(spaceShip, spaceShip.isPlayer1 ? bigTorpedoImg : bigTorpedo1Img, x, y, vX * BIG_TORPEDO_SPEED, vY * BIG_TORPEDO_SPEED, Math.toDegrees(angle)));
            angle += angleStep;
        }
        lastStarBlast = System.nanoTime();
        playSound(laserSound);
    }


    // ******************** Public Methods ************************************
    public void startGame() {
        if (running) { return; }
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
        spawnSpaceShip();
        running = true;
        timer.start();
    }

    public boolean isReadyToStart() { return readyToStart; }

    public boolean isRunning() { return running; }

    public boolean isHallOfFameScreen() { return hallOfFameScreen; }

    public void upPressed(final Players player) {
        switch(player) {
            case PLAYER_1:
                increaseSpaceShipVy(spaceShipPlayer1);
                break;
            case PLAYER_2:
                increaseSpaceShipVy(spaceShipPlayer2);
                break;
        }
    }
    public void upReleased(final Players player) {
        switch(player) {
            case PLAYER_1:
                stopSpaceShipVy(spaceShipPlayer1);
                break;
            case PLAYER_2:
                stopSpaceShipVy(spaceShipPlayer2);
                break;
        }
    }

    public void rightPressed(final Players player) {
        switch(player) {
            case PLAYER_1:
                increaseSpaceShipVx(spaceShipPlayer1);
                break;
            case PLAYER_2:
                increaseSpaceShipVx(spaceShipPlayer2);
                break;
        }
    }
    public void rightReleased(final Players player) {
        switch(player) {
            case PLAYER_1:
                stopSpaceShipVx(spaceShipPlayer1);
                break;
            case PLAYER_2:
                stopSpaceShipVx(spaceShipPlayer2);
                break;
        }
    }

    public void downPressed(final Players player) {
        switch(player) {
            case PLAYER_1:
                decreaseSpaceShipVy(spaceShipPlayer1);
                break;
            case PLAYER_2:
                decreaseSpaceShipVy(spaceShipPlayer2);
                break;
        }
    }
    public void downReleased(final Players player) {
        switch(player) {
            case PLAYER_1:
                stopSpaceShipVy(spaceShipPlayer1);
                break;
            case PLAYER_2:
                stopSpaceShipVy(spaceShipPlayer2);
                break;
        }
    }

    public void leftPressed(final Players player) {
        switch(player) {
            case PLAYER_1:
                decreaseSpaceShipVx(spaceShipPlayer1);
                break;
            case PLAYER_2:
                decreaseSpaceShipVx(spaceShipPlayer2);
                break;
        }
    }
    public void leftReleased(final Players player) {
        switch(player) {
            case PLAYER_1:
                stopSpaceShipVx(spaceShipPlayer1);
                break;
            case PLAYER_2:
                stopSpaceShipVx(spaceShipPlayer2);
                break;
        }
    }

    public void fireWeaponPressed(final Players player) {
        switch(player) {
            case PLAYER_1:
                fireSpaceShipWeapon(spaceShipPlayer1);
                break;
            case PLAYER_2:
                fireSpaceShipWeapon(spaceShipPlayer2);
                break;
            case NONE:
                saveInitialsButton.fireEvent(new ActionEvent(saveInitialsButton, null));
                break;
        }
    }

    public void fireRocketPressed(final Players player) {
        switch(player) {
            case PLAYER_1:
                fireSpaceShipRocket(spaceShipPlayer1);
                break;
            case PLAYER_2:
                fireSpaceShipRocket(spaceShipPlayer2);
                break;
        }
    }

    public void deflectorShieldPressed(final Players player) {
        switch(player) {
            case PLAYER_1:
                activateSpaceShipShield(spaceShipPlayer1);
                break;
            case PLAYER_2:
                activateSpaceShipShield(spaceShipPlayer2);
                break;
        }
    }

    public InitialDigit getDigit1() { return digit1; }
    public InitialDigit getDigit2() { return digit2; }

    public void addPlayer() {
        spawnSpaceShip();
    }

    public void storePlayer(final Player player) {
        player.name = (digit1.getCharacter() + digit2.getCharacter());
        hallOfFame.add(player);
        Collections.sort(hallOfFame);
        hallOfFame = hallOfFame.stream().limit(3).collect(Collectors.toList());

        // Store hall of fame in properties
        properties.setProperty("hallOfFame1", hallOfFame.get(0).toPropertyString());
        properties.setProperty("hallOfFame2", hallOfFame.get(1).toPropertyString());
        properties.setProperty("hallOfFame3", hallOfFame.get(2).toPropertyString());
        PropertyManager.INSTANCE.storeProperties();

        HBox p1Entry = createHallOfFameEntry(new Player(properties.getProperty("hallOfFame1")));
        HBox p2Entry = createHallOfFameEntry(new Player(properties.getProperty("hallOfFame2")));
        HBox p3Entry = createHallOfFameEntry(new Player(properties.getProperty("hallOfFame3")));
        hallOfFameBox.getChildren().setAll(p1Entry, p2Entry, p3Entry);
        hallOfFameBox.relocate((WIDTH - hallOfFameBox.getPrefWidth()) * 0.5, (HEIGHT - hallOfFameBox.getPrefHeight()) * 0.5);

        Helper.enableNode(playerInitialsLabel, false);
        Helper.enableNode(playerInitialsDigits, false);
        Helper.enableNode(saveInitialsButton, false);
    }


    // ******************** Space Object Classes ******************************
    private abstract class Sprite {
        protected final Random rnd;
        public          Image   image;
        public          double  x;
        public          double  y;
        public          double  r;
        public          double  vX;
        public          double  vY;
        public          double  vR;
        public          double  width;
        public          double  height;
        public          double  size;
        public          double  radius;
        public          boolean toBeRemoved;


        public Sprite() {
            this(null, 0, 0, 0, 0, 0, 0);
        }
        public Sprite(final Image image) {
            this(image, 0, 0, 0, 0, 0, 0);
        }
        public Sprite(final Image image, final double x, final double y) {
            this(image, x, y, 0, 0, 0, 0);
        }
        public Sprite(final Image image, final double x, final double y, final double vX, final double vY) {
            this(image, x, y, 0, vX, vY, 0);
        }
        public Sprite(final Image image, final double x, final double y, final double r, final double vX, final double vY) {
            this(image, x, y, r, vX, vY, 0);
        }
        public Sprite(final Image image, final double x, final double y, final double r, final double vX, final double vY, final double vR) {
            this.rnd         = new Random();
            this.image       = image;
            this.x           = x;
            this.y           = y;
            this.r           = r;
            this.vX          = vX;
            this.vY          = vY;
            this.vR          = vR;
            this.width       = null == image ? 0 : image.getWidth();
            this.height      = null == image ? 0 : image.getHeight();
            this.size        = this.width > this.height ? this.width : this.height;
            this.radius      = this.size * 0.5;
            this.toBeRemoved = false;
        }


        protected void init() {};

        public void respawn() {}

        public abstract void update();
    }

    private abstract class AnimatedSprite extends Sprite {
        protected final int    maxFrameX;
        protected final int    maxFrameY;
        protected       double scale;
        protected       int    countX;
        protected       int    countY;


        public AnimatedSprite(final int maxFrameX, final int maxFrameY, final double scale) {
            this(0, 0, 0, 0, 0, 0, maxFrameX, maxFrameY, scale);
        }
        public AnimatedSprite(final double x, final double y, final double vX, final double vY, final int maxFrameX, final int maxFrameY, final double scale) {
            this(x, y, 0, vX, vY, 0, maxFrameX, maxFrameY, scale);
        }
        public AnimatedSprite(final double x, final double y, final double r, final double vX, final double vY, final double vR, final int maxFrameX, final int maxFrameY, final double scale) {
            super(null, x, y, r, vX, vY, vR);
            this.maxFrameX = maxFrameX;
            this.maxFrameY = maxFrameY;
            this.scale     = scale;
            this.countX    = 0;
            this.countY    = 0;
        }


        @Override public void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private abstract class Bonus extends Sprite {
        protected final double  xVariation   = 2;
        protected final double  minSpeedY    = 2;
        protected final double  minRotationR = 0.1;
        protected       double  imgCenterX;
        protected       double  imgCenterY;
        protected       double  cX;
        protected       double  cY;
        protected       double  rot;
        protected       boolean rotateRight;
        protected       double  vYVariation;

        public Bonus(final Image image) {
            super(image);
        }
    }

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

    private class Player implements Comparable<Player> {
        private final String id;
        public        String name;
        public        Long   score;
        public        Color  playerColor;


        public Player(final String propertyString) {
            this(propertyString.split(",")[0], propertyString.split(",")[1], Long.parseLong(propertyString.split(",")[2]), SPACEFX_COLOR);
        }
        public Player(final long score, final Color playerColor) {
            this(UUID.randomUUID().toString(), "--", score, playerColor);
        }
        public Player(final String id, final String name, final long score, final Color playerColor) {
            this.id          = id;
            this.name        = name;
            this.score       = score;
            this.playerColor = playerColor;
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
        private static final long            ENEMY_SPAWN_INTERVAL = 250_000_000l;
        private final        WaveType        waveType1;
        private final        WaveType        waveType2;
        private final        List<SpaceShip> spaceShips;
        private final        int             noOfEnemies;
        private final        int             noOfSmartEnemies;
        private final        Image           image;
        private final        boolean         canFire;
        private final        boolean         canBomb;
        private final        List<Enemy>     enemies;
        private final        List<Enemy>     smartEnemies;
        private              int             enemiesSpawned;
        private              long            lastEnemySpawned;
        private              boolean         alternateWaveType;
        private              boolean         toggle;
        private              boolean         isRunning;


        public Wave(final WaveType waveType, final List<SpaceShip> spaceShips, final int noOfEnemies, final Image image, final boolean canFire, final boolean canBomb) {
            this(waveType, null, spaceShips, noOfEnemies, image, canFire, canBomb);
        }
        public Wave(final WaveType waveType1, final WaveType waveType2, final List<SpaceShip> spaceShips, final int noOfEnemies, final Image image, final boolean canFire, final boolean canBomb) {
            if (null == waveType1) { throw new IllegalArgumentException("You need at least define waveType1."); }
            this.waveType1         = waveType1;
            this.waveType2         = waveType2;
            this.spaceShips        = spaceShips;
            this.noOfEnemies       = noOfEnemies;
            this.noOfSmartEnemies  = level.getDifficulty().noOfSmartEnemies;
            this.image             = image;
            this.canFire           = canFire;
            this.canBomb           = canBomb;
            this.enemies           = new ArrayList<>(noOfEnemies);
            this.smartEnemies      = new ArrayList<>();
            this.enemiesSpawned    = 0;
            this.alternateWaveType = null == waveType2 ? false : true;
            this.toggle            = true;
            this.isRunning         = true;
        }


        public void update(final GraphicsContext ctx) {
            if (isRunning) {
                if (enemiesSpawned < noOfEnemies && System.nanoTime() - lastEnemySpawned > ENEMY_SPAWN_INTERVAL) {
                    Enemy enemy = spawnEnemy();
                    if (smartEnemies.size() < level.getDifficulty().noOfSmartEnemies && RND.nextBoolean()) {
                        smartEnemies.add(enemy);
                    }
                    lastEnemySpawned = System.nanoTime();
                }

                enemies.forEach(enemy -> {
                    if (level.getIndex() > 1 &&
                        !enemy.smart &&
                        enemy.frameCounter > waveType1.totalFrames * 0.35 &&
                        smartEnemies.contains(enemy)) {
                        enemy.smart = RND.nextBoolean();
                    }

                    enemy.update();

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
                        if (Helper.isHitCircleCircle(torpedo.x, torpedo.y, torpedo.radius, enemy.x, enemy.y, enemy.radius)) {
                            explosions.add(new Explosion(enemy.x - EXPLOSION_FRAME_WIDTH * 0.25, enemy.y - EXPLOSION_FRAME_HEIGHT * 0.25, enemy.vX, enemy.vY, 0.35));
                            torpedo.spaceShip.score += enemy.value;
                            torpedo.spaceShip.enemiesKilled++;
                            kills++;
                            levelKills++;
                            enemy.toBeRemoved = true;
                            torpedo.toBeRemoved = true;
                            playSound(spaceShipExplosionSound);
                        }
                    }

                    // Check for bigTorpedo hits
                    for (BigTorpedo bigTorpedo : bigTorpedos) {
                        if (Helper.isHitCircleCircle(bigTorpedo.x, bigTorpedo.y, bigTorpedo.radius, enemy.x, enemy.y, enemy.radius)) {
                            explosions.add(new Explosion(enemy.x - EXPLOSION_FRAME_WIDTH * 0.25, enemy.y - EXPLOSION_FRAME_HEIGHT * 0.25, enemy.vX, enemy.vY, 0.35));
                            bigTorpedo.spaceShip.score += enemy.value;
                            bigTorpedo.spaceShip.enemiesKilled++;
                            kills++;
                            levelKills++;
                            enemy.toBeRemoved = true;
                            bigTorpedo.toBeRemoved = true;
                            playSound(spaceShipExplosionSound);
                        }
                    }

                    // Check for rocket hits
                    for (Rocket rocket : rockets) {
                        if (Helper.isHitCircleCircle(rocket.x, rocket.y, rocket.radius, enemy.x, enemy.y, enemy.radius)) {
                            rocketExplosions.add(new RocketExplosion(enemy.x - ROCKET_EXPLOSION_FRAME_WIDTH * 0.25, enemy.y - ROCKET_EXPLOSION_FRAME_HEIGHT * 0.25, enemy.vX, enemy.vY, 0.5));
                            rocket.spaceShip.score += enemy.value;
                            rocket.spaceShip.enemiesKilled++;
                            kills++;
                            levelKills++;
                            enemy.toBeRemoved = true;
                            rocket.toBeRemoved = true;
                            playSound(rocketExplosionSound);
                        }
                    }

                    // Check for space ship hit
                    spaceShips.forEach(spaceShip -> {
                        if (spaceShip.isVulnerable && !spaceShip.hasBeenHit) {
                            boolean hit;
                            if (spaceShip.shield) {
                                hit = Helper.isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, enemy.x, enemy.y, enemy.radius);
                            } else {
                                hit = Helper.isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, enemy.x, enemy.y, enemy.radius);
                            }
                            if (hit) {
                                if (spaceShip.shield) {
                                    explosions.add(new Explosion(enemy.x - EXPLOSION_FRAME_WIDTH * 0.125, enemy.y - EXPLOSION_FRAME_HEIGHT * 0.125, enemy.vX, enemy.vY, 0.35));
                                    playSound(spaceShipExplosionSound);
                                } else {
                                    spaceShipExplosions.add(new SpaceShipExplosion(spaceShip,spaceShip.x - SPACESHIP_EXPLOSION_FRAME_WIDTH * 0.5, spaceShip.y - SPACESHIP_EXPLOSION_FRAME_HEIGHT * 0.5, spaceShip.vX, spaceShip.vY));
                                    playSound(spaceShipExplosionSound);
                                    spaceShip.hasBeenHit = true;
                                    spaceShip.noOfLifes--;
                                    if (0 == spaceShip.noOfLifes) {
                                        spaceShip.toBeRemoved = true;
                                        players.add(new Player(spaceShip.score, spaceShip.isPlayer1 ? SPACEFX_COLOR : SPACEFX_COLOR1));
                                    }
                                }
                                enemy.toBeRemoved = true;
                            }
                        }
                    });
                });

                enemies.removeIf(enemy -> enemy.toBeRemoved);
                if (enemies.isEmpty() && enemiesSpawned == noOfEnemies) { isRunning = false; }
            }
        }

        private Enemy spawnEnemy() {
            Enemy enemy;
            if (alternateWaveType) {
                enemy = new Enemy(toggle ? waveType1 : waveType2, spaceShips, image, canFire, canBomb);
            } else {
                enemy = new Enemy(waveType1, spaceShips, image, canFire, canBomb);
            }
            toggle = !toggle;
            enemies.add(enemy);
            enemiesSpawned++;
            return enemy;
        }
    }


    // ******************** Sprites *******************************************
    private class SpaceShip extends Sprite {
        private static final long      INVULNERABLE_TIME = 3_000_000_000l;
        private        final Image     imageUp;
        private        final Image     imageDown;
        public         final boolean   isPlayer1;
        private              long      born;
        private              boolean   shield;
        public               boolean   isVulnerable;
        public               boolean   hasBeenHit;
        public               int       noOfLifes;
        public               int       noOfShields;
        private              long      lastShieldActivated;
        private              long      lastTorpedoFired;
        public               boolean   bigTorpedosEnabled;
        public               boolean   starburstEnabled;
        public               long      score;
        public               long      torpedosFired;
        public               long      rocketsFired;
        public               long      shieldsUsed;
        public               long      bigTorpedosFired;
        public               long      starBurstFired;
        public               long      asteroidsDestroyed;
        public               long      enemiesKilled;
        public               long      enemyBossesKilled;
        public               long      levelBossedKilled;
        public               long      shieldsCollected;
        public               long      lifesCollected;
        public               long      bigTorpedosCollected;
        public               long      starburstsCollected;


        public SpaceShip(final Image image, final Image imageUp, final Image imageDown) {
            this(false, image, imageUp, imageDown);
        }
        public SpaceShip(final boolean isPlayer1, final Image image, final Image imageUp, final Image imageDown) {
            super(image);
            this.isPlayer1 = isPlayer1;
            this.imageUp   = imageUp;
            this.imageDown = imageDown;
            init();
        }


        @Override protected void init() {
            this.born                 = System.nanoTime();
            this.x                    = MULTI_PLAYER ? isPlayer1 ? WIDTH * 0.4 : WIDTH * 0.6 : WIDTH * 0.5;
            this.y                    = HEIGHT - 2 * image.getHeight();
            this.width                = image.getWidth();
            this.height               = image.getHeight();
            this.size                 = width > height ? width : height;
            this.radius               = size * 0.5;
            this.vX                   = 0;
            this.vY                   = 0;
            this.shield               = false;
            this.isVulnerable         = false;
            this.hasBeenHit           = false;
            this.noOfLifes            = NO_OF_LIFES;
            this.noOfShields          = NO_OF_SHIELDS;
            this.lastShieldActivated  = 0;
            this.lastTorpedoFired     = 0;
            this.bigTorpedosEnabled   = false;
            this.starburstEnabled     = false;
            this.score                = 0;
            this.torpedosFired        = 0;
            this.rocketsFired         = 0;
            this.shieldsUsed          = 0;
            this.bigTorpedosFired     = 0;
            this.starBurstFired       = 0;
            this.asteroidsDestroyed   = 0;
            this.enemiesKilled        = 0;
            this.enemyBossesKilled    = 0;
            this.levelBossedKilled    = 0;
            this.shieldsCollected     = 0;
            this.lifesCollected       = 0;
            this.bigTorpedosCollected = 0;
            this.starburstsCollected  = 0;
        }

        @Override public void respawn() {
            this.x            = MULTI_PLAYER ? isPlayer1 ? WIDTH * 0.4 : WIDTH * 0.6 : WIDTH * 0.5;
            this.y            = HEIGHT - 2 * this.height;
            this.vX           = 0;
            this.vY           = 0;
            this.shield       = false;
            this.born         = System.nanoTime();
            this.isVulnerable = false;
            this.hasBeenHit   = false;
        }

        @Override public void update() {
            if (!isVulnerable && System.nanoTime() - born > INVULNERABLE_TIME) {
                isVulnerable = true;
            }
            double oldX = x;
            double oldY = y;
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

            if (isPlayer1) {
                shipTouchArea.setCenterX(x);
                shipTouchArea.setCenterY(y);
            }
        }
    }

    private class Asteroid extends Sprite {
        private static final int     MAX_VALUE      = 10;
        private final        Random  rnd            = new Random();
        private final        double  xVariation     = 2;
        private final        double  minSpeedY      = 2;
        private final        double  minRotationR   = 0.1;
        private              double  imgCenterX;
        private              double  imgCenterY;
        private              double  radius;
        private              double  cX;
        private              double  cY;
        private              double  rot;
        private              boolean rotateRight;
        private              double  scale;
        private              double  vYVariation;
        private              int     value;
        private              int     hits;


        public Asteroid(final Image image) {
            super(image);
            init();
        }


        @Override protected void init() {
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

        @Override public void respawn() {
            this.image = asteroidImages[RND.nextInt(asteroidImages.length)];
            init();
        }

        @Override public void update() {
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

    private class Torpedo extends Sprite {
        public SpaceShip spaceShip;

        public Torpedo(final SpaceShip spaceShip, final Image image, final double x, final double y) {
            super(image, x, y - image.getHeight(), 0, TORPEDO_SPEED);
            this.spaceShip = spaceShip;
        }


        @Override public void update() {
            y -= vY;
            if (y < -size) {
                toBeRemoved = true;
            }
        }
    }

    private class BigTorpedo extends Sprite {
        public SpaceShip spaceShip;

        public BigTorpedo(final SpaceShip spaceShip, final Image image, final double x, final double y, final double vX, final double vY, final double r) {
            super(image, x, y, r, vX, vY);
            this.spaceShip = spaceShip;
        }


        @Override public void update() {
            x += vX;
            y += vY;
            if (x < -width || x > WIDTH + width || y < -height || y > HEIGHT + height) {
                toBeRemoved = true;
            }
        }
    }

    private class Rocket extends Sprite {
        public SpaceShip spaceShip;
        public double    halfWidth;
        public double    halfHeight;


        public Rocket(final SpaceShip spaceShip, final Image image, final double x, final double y) {
            super(image, x, y - image.getHeight(), 0, ROCKET_SPEED);
            this.spaceShip  = spaceShip;
            this.halfWidth  = width * 0.5;
            this.halfHeight = height * 0.5;
        }


        @Override public void update() {
            y -= vY;
            if (y < -size) {
                toBeRemoved = true;
            }
        }
    }

    private class Enemy extends Sprite {
        public static final  long            TIME_BETWEEN_SHOTS  = 500_000_000l;
        public static final  long            TIME_BETWEEN_BOMBS  = 1_000_000_000l;
        public static final  double          HALF_ANGLE_OF_SIGHT = 5;
        private static final double          BOMB_RANGE          = 10;
        private static final int             MAX_VALUE           = 50;
        private final        WaveType        waveType;
        public               int             frameCounter;
        private              List<SpaceShip> spaceShips;
        private              SpaceShip       spaceShipToAttack;
        public               boolean         canFire;
        public               boolean         canBomb;
        public               boolean         smart;
        private              int             noOfBombs;
        private              double          oldX;
        private              double          oldY;
        private              double          dX;
        private              double          dY;
        private              double          dist;
        private              double          factor;
        public               int             value;
        public               long            lastShot;
        public               long            lastBomb;
        public               boolean         toBeRemoved;


        public Enemy(final WaveType waveType, final List<SpaceShip> spaceShips, final Image image, final boolean canFire, final boolean canBomb) {
            this(waveType, spaceShips, image, canFire, canBomb, false);
        }
        public Enemy(final WaveType waveType, final List<SpaceShip> spaceShips, final Image image, final boolean canFire, final boolean canBomb, final boolean smart) {
            super(image);
            this.waveType          = waveType;
            this.frameCounter      = 0;
            this.spaceShips        = spaceShips;
            this.spaceShipToAttack = null;
            this.canFire           = canFire;
            this.canBomb           = canBomb;
            this.noOfBombs         = NO_OF_ENEMY_BOMBS;
            this.toBeRemoved       = false;
            this.smart             = smart;
            init();
        }


        @Override protected void init() {
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

        @Override public void update() {
            if (null == spaceShipToAttack) {
                Map<SpaceShip, Double> shipDistanceMap = new HashMap<>();
                spaceShips.forEach(spaceShip -> {
                    dX = spaceShip.x - x;
                    dY = spaceShip.y - y;
                    shipDistanceMap.put(spaceShip, Math.sqrt(dX * dX + dY * dY));
                });
                spaceShipToAttack = shipDistanceMap.entrySet()
                                                   .stream()
                                                   .min(Comparator.comparingDouble(Map.Entry::getValue))
                                                   .map(Map.Entry::getKey).orElse(null);
            }

            if (toBeRemoved) { return; }
            oldX = x;
            oldY = y;
            if (smart && null != spaceShipToAttack) {
                dX     = spaceShipToAttack.x - x;
                dY     = spaceShipToAttack.y - y;
                dist   = Math.sqrt(dX * dX + dY * dY);
                factor = ENEMY_SPEED / dist;
                if (spaceShipToAttack.isVulnerable && spaceShipToAttack.y > y && y < OUT_OF_SENSING_HEIGHT) {
                    vX = dX * factor;
                    vY = dY * factor;
                }
                x += vX;
                y += vY;
                r = Math.toDegrees(Math.atan2(vY, vX)) - 90;
            } else {
                x  = waveType.coordinates.get(frameCounter).x;
                y  = waveType.coordinates.get(frameCounter).y;
                r  = waveType.coordinates.get(frameCounter).r;
                vX = x - oldX;
                vY = y - oldY;
            }

            long now = System.nanoTime();

            spaceShips.forEach(spaceShip -> {
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
                                               lastBomb = now;
                                               lastBombDropped = now;
                                               noOfBombs--;
                                           }
                                       }
                                   }
                               });
            // Remove Enemy
            if (smart) {
                if(x < -size || x - radius > WIDTH || y - height > HEIGHT) {
                    toBeRemoved = true;
                }
            } else {
                frameCounter++;
                if (frameCounter >= waveType.totalFrames) {
                    toBeRemoved = true;
                }
            }
        }
    }

    private class EnemyTorpedo extends Sprite {
        private List<SpaceShip> spaceShips;


        public EnemyTorpedo(final List<SpaceShip> spaceShips, final Image image, final double x, final double y, final double vX, final double vY) {
            super(image, x - image.getWidth() / 2.0, y, vX, vY);
            this.spaceShips = spaceShips;
        }


        @Override public void update() {
            x += vX;
            y += vY;

            spaceShips.forEach(spaceShip -> {
                if (spaceShip.isVulnerable && !spaceShip.hasBeenHit) {
                    boolean hit;
                    if (spaceShip.shield) {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                    } else {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                    }
                    if (hit) {
                        toBeRemoved = true;
                        if (spaceShip.shield) {
                            playSound(shieldHitSound);
                        } else {
                            spaceShip.hasBeenHit = true;
                            playSound(spaceShipExplosionSound);
                            spaceShip.noOfLifes--;
                            if (0 == spaceShip.noOfLifes) {
                                spaceShip.toBeRemoved = true;
                                players.add(new Player(spaceShip.score, spaceShip.isPlayer1 ? SPACEFX_COLOR : SPACEFX_COLOR1));
                            }
                        }
                    }
                } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                    toBeRemoved = true;
                }
            });
        }
    }

    private class EnemyBomb extends Sprite {
        private List<SpaceShip> spaceShips;


        public EnemyBomb(final List<SpaceShip> spaceShips, final Image image, final double x, final double y, final double vX, final double vY) {
            super(image, x - image.getWidth() / 2.0, y, vX, vY);
            this.spaceShips = spaceShips;
        }


        @Override public void update() {
            x += vX;
            y += vY;

            spaceShips.forEach(spaceShip -> {
                if (spaceShip.isVulnerable && !spaceShip.hasBeenHit) {
                    boolean hit;
                    if (spaceShip.shield) {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                    } else {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                    }
                    if (hit) {
                        toBeRemoved = true;
                        if (spaceShip.shield) {
                            playSound(shieldHitSound);
                        } else {
                            spaceShip.hasBeenHit = true;
                            playSound(spaceShipExplosionSound);
                            spaceShip.noOfLifes--;
                            if (0 == spaceShip.noOfLifes) {
                                spaceShip.toBeRemoved = true;
                                players.add(new Player(spaceShip.score, spaceShip.isPlayer1 ? SPACEFX_COLOR : SPACEFX_COLOR1));
                            }
                        }
                    }
                } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                    toBeRemoved = true;
                }
            });
        }
    }

    private class EnemyBoss extends Sprite {
        private static final int             MAX_VALUE            = 100;
        private static final long            TIME_BETWEEN_SHOTS   = 500_000_000l;
        private static final long            TIME_BETWEEN_ROCKETS = 5_000_000_000l;
        private static final double          HALF_ANGLE_OF_SIGHT  = 10;
        private final        List<SpaceShip> spaceShips;
        private              SpaceShip       spaceShipToAttack;
        private              double          dX;
        private              double          dY;
        private              double          dist;
        private              double          factor;
        private              int             value;
        private              int             hits;
        private              long            lastShot;
        private              long            lastRocket;
        private              boolean         hasRockets;


        public EnemyBoss(final List<SpaceShip> spaceShips, final Image image, final boolean hasRockets) {
            super(image);
            this.spaceShips        = spaceShips;
            this.spaceShipToAttack = null;
            this.hasRockets        = hasRockets;
            init();
        }


        @Override protected void init() {
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

        @Override public void update() {
            if (null == spaceShipToAttack) {
                Map<SpaceShip, Double> shipDistanceMap = new HashMap<>();
                spaceShips.forEach(spaceShip -> {
                    dX = spaceShip.x - x;
                    dY = spaceShip.y - y;
                    shipDistanceMap.put(spaceShip, Math.sqrt(dX * dX + dY * dY));
                });
                spaceShipToAttack = shipDistanceMap.entrySet()
                                                   .stream()
                                                   .min(Comparator.comparingDouble(Map.Entry::getValue))
                                                   .map(Map.Entry::getKey).orElse(null);
            } else {
                dX = spaceShipToAttack.x - x;
                dY = spaceShipToAttack.y - y;
                dist = Math.sqrt(dX * dX + dY * dY);
                factor = ENEMY_BOSS_SPEED / dist;
                if (spaceShipToAttack.isVulnerable && y < OUT_OF_SENSING_HEIGHT) {
                    vX = dX * factor;
                    vY = dY * factor;
                }
            }

            x += vX;
            y += vY;

            r = Math.toDegrees(Math.atan2(vY, vX)) - 90;

            long now = System.nanoTime();
            spaceShips.forEach(spaceShip -> {


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
            });
            switch (hits) {
                case 5: image = level.getEnemyBossImg4();break;
                case 4: image = level.getEnemyBossImg3();break;
                case 3: image = level.getEnemyBossImg2();break;
                case 2: image = level.getEnemyBossImg1();break;
                case 1: image = level.getEnemyBossImg0();break;
            }

            // Remove enemy boss
            if(x < -size || x - radius > WIDTH || y - height > HEIGHT) {
                toBeRemoved = true;
            }
        }
    }

    private class EnemyBossTorpedo extends Sprite {
        private final List<SpaceShip> spaceShips;

        public EnemyBossTorpedo(final List<SpaceShip> spaceShips, final Image image, final double x, final double y, final double vX, final double vY) {
            super(image, x - image.getWidth() / 2.0, y, vX, vY);
            this.spaceShips = spaceShips;
        }


        @Override public void update() {
            x += vX;
            y += vY;

            spaceShips.forEach(spaceShip -> {
                if (spaceShip.isVulnerable && !spaceShip.hasBeenHit) {
                    boolean hit;
                    if (spaceShip.shield) {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                    } else {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                    }
                    if (hit) {
                        toBeRemoved = true;
                        if (spaceShip.shield) {
                            playSound(shieldHitSound);
                        } else {
                            spaceShip.hasBeenHit = true;
                            playSound(spaceShipExplosionSound);
                            spaceShip.noOfLifes--;
                            if (0 == spaceShip.noOfLifes) {
                                spaceShip.toBeRemoved = true;
                                players.add(new Player(spaceShip.score, spaceShip.isPlayer1 ? SPACEFX_COLOR : SPACEFX_COLOR1));
                            }
                        }
                    }
                } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                    toBeRemoved = true;
                }
            });
        }
    }

    private class EnemyBossRocket extends Sprite {
        private final long            rocketLifespan = 2_500_000_000l;
        private final SpaceShip       spaceShip;
        private final List<SpaceShip> spaceShips;
        private       long            born;
        private       double          dX;
        private       double          dY;
        private       double          dist;
        private       double          factor;


        public EnemyBossRocket(final SpaceShip spaceShip, final List<SpaceShip> spaceShips, final Image image, final double x, final double y) {
            super(image, x - image.getWidth() / 2.0, y, 0, 1);
            this.spaceShip  = spaceShip;
            this.spaceShips = spaceShips;
            this.born       = System.nanoTime();
        }


        @Override public void update() {
            dX     = spaceShip.x - x;
            dY     = spaceShip.y - y;
            dist   = Math.sqrt(dX * dX + dY * dY);
            factor = ENEMY_BOSS_ROCKET_SPEED / dist;
            if (spaceShip.y > y) {
                vX = dX * factor;
                vY = dY * factor;
            }

            x += vX;
            y += vY;

            r = Math.toDegrees(Math.atan2(vY, vX)) - 90;

            spaceShips.forEach(spaceShip -> {
                if (spaceShip.isVulnerable && !spaceShip.hasBeenHit) {
                    boolean hit;
                    if (spaceShip.shield) {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                    } else {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                    }
                    if (hit) {
                        toBeRemoved = true;
                        if (spaceShip.shield) {
                            playSound(shieldHitSound);
                        } else {
                            spaceShip.hasBeenHit = true;
                            playSound(spaceShipExplosionSound);
                            spaceShip.noOfLifes--;
                            if (0 == spaceShip.noOfLifes) {
                                spaceShip.toBeRemoved = true;
                                players.add(new Player(spaceShip.score, spaceShip.isPlayer1 ? SPACEFX_COLOR : SPACEFX_COLOR1));
                            }
                        }
                    }
                } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                    toBeRemoved = true;
                }
            });

            if (System.nanoTime() - born > rocketLifespan) {
                enemyRocketExplosions.add(new EnemyRocketExplosion(x - ENEMY_ROCKET_EXPLOSION_FRAME_WIDTH * 0.25, y - ENEMY_ROCKET_EXPLOSION_FRAME_HEIGHT * 0.25, vX, vY, 0.5));
                toBeRemoved = true;
            }
        }
    }

    private class LevelBoss extends Sprite {
        private static final int             MAX_VALUE            = 500;
        private static final long            TIME_BETWEEN_SHOTS   = 400_000_000l;
        private static final long            TIME_BETWEEN_ROCKETS = 3_500_000_000l;
        private static final long            TIME_BETWEEN_BOMBS   = 2_500_000_000l;
        private static final double          HALF_ANGLE_OF_SIGHT  = 22;
        private static final double          BOMB_RANGE           = 50;
        private static final long            WAITING_PHASE        = 10_000_000_000l;
        private final        List<SpaceShip> spaceShips;
        private              SpaceShip       spaceShipToAttack;
        private              double          dX;
        private              double          dY;
        private              double          dist;
        private              double          factor;
        private              double          weaponSpawnY;
        private              double          vpX;
        private              double          vpY;
        private              int             value;
        private              int             hits;
        private              long            lastShot;
        private              long            lastRocket;
        private              boolean         hasRockets;
        private              boolean         hasBombs;
        private              long            waitingStart;


        public LevelBoss(final List<SpaceShip> spaceShips, final Image image, final boolean hasRockets, final boolean hasBombs) {
            super(image);
            this.spaceShips        = spaceShips;
            this.spaceShipToAttack = null;
            this.hasRockets        = hasRockets;
            this.hasBombs          = hasBombs;
            init();
        }


        @Override protected void init() {
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

        @Override public void update() {
            if (y < height * 0.6) {
                // Approaching
                vY = LEVEL_BOSS_SPEED;
            } else {
                if (waitingStart == 0) {
                    waitingStart = System.nanoTime();
                }
                if (null == spaceShipToAttack) {
                    Map<SpaceShip, Double> shipDistanceMap = new HashMap<>();
                    spaceShips.forEach(spaceShip -> {
                        dX = spaceShip.x - x;
                        dY = spaceShip.y - y;
                        shipDistanceMap.put(spaceShip, Math.sqrt(dX * dX + dY * dY));
                    });
                    spaceShipToAttack = shipDistanceMap.entrySet()
                                                       .stream()
                                                       .min(Comparator.comparingDouble(Map.Entry::getValue))
                                                       .map(Map.Entry::getKey).orElse(null);
                } else {
                    dX     = spaceShipToAttack.x - x;
                    dY     = spaceShipToAttack.y - y;
                    dist   = Math.sqrt(dX * dX + dY * dY);
                    factor = LEVEL_BOSS_SPEED / dist;
                    vpX    = dX * factor;
                    vpY    = dY * factor;
                }
                if (System.nanoTime() < waitingStart + WAITING_PHASE) {
                    // Waiting
                    vX = dX * factor * 10;
                    vY = 0;
                } else if (y < OUT_OF_SENSING_HEIGHT) {
                    // Attacking
                    vX = vpX;
                    vY = vpY;
                    r  = Math.toDegrees(Math.atan2(vY, vX)) - 90;
                }
            }

            x += vX;
            y += vY;

            long now = System.nanoTime();
            spaceShips.forEach(spaceShip -> {
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
            });

            // Remove level boss
            if(x < -size || x - radius > WIDTH || y - height > HEIGHT) {
                toBeRemoved = true;
                nextLevel();
            }
        }
    }

    private class LevelBossTorpedo extends Sprite {
        private final List<SpaceShip> spaceShips;


        public LevelBossTorpedo(final List<SpaceShip> spaceShips, final Image image, final double x, final double y, final double vX, final double vY, final double r) {
            super(image, x - image.getWidth() / 2.0, y, r, vX, vY);
            this.spaceShips = spaceShips;
        }


        @Override public void update() {
            x += vX;
            y += vY;
            spaceShips.forEach(spaceShip -> {
                if (spaceShip.isVulnerable && !spaceShip.hasBeenHit) {
                    boolean hit;
                    if (spaceShip.shield) {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                    } else {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                    }
                    if (hit) {
                        toBeRemoved = true;
                        if (spaceShip.shield) {
                            playSound(shieldHitSound);
                        } else {
                            spaceShip.hasBeenHit = true;
                            playSound(spaceShipExplosionSound);
                            spaceShip.noOfLifes--;
                            if (0 == spaceShip.noOfLifes) {
                                spaceShip.toBeRemoved = true;
                                players.add(new Player(spaceShip.score, spaceShip.isPlayer1 ? SPACEFX_COLOR : SPACEFX_COLOR1));
                            }
                        }
                    }
                } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                    toBeRemoved = true;
                }
            });
        }
    }

    private class LevelBossRocket extends Sprite {
        private final long            rocketLifespan = 3_000_000_000l;
        private final SpaceShip       spaceShip;
        private final List<SpaceShip> spaceShips;
        private       long            born;
        private       double          dX;
        private       double          dY;
        private       double          dist;
        private       double          factor;


        public LevelBossRocket(final SpaceShip spaceShip, final List<SpaceShip> spaceShips, final Image image, final double x, final double y) {
            super(image, x - image.getWidth() / 2.0, y, 0, 1);
            this.spaceShip  = spaceShip;
            this.spaceShips = spaceShips;
            this.born       = System.nanoTime();
        }


        @Override public void update() {
            dX     = spaceShip.x - x;
            dY     = spaceShip.y - y;
            dist   = Math.sqrt(dX * dX + dY * dY);
            factor = ENEMY_BOSS_ROCKET_SPEED / dist;
            if (spaceShip.y > y) {
                vX = dX * factor;
                vY = dY * factor;
            }

            x += vX;
            y += vY;

            r = Math.toDegrees(Math.atan2(vY, vX)) - 90;

            spaceShips.forEach(spaceShip -> {
                if (spaceShip.isVulnerable && !spaceShip.hasBeenHit) {
                    boolean hit;
                    if (spaceShip.shield) {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                    } else {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                    }
                    if (hit) {
                        toBeRemoved = true;
                        if (spaceShip.shield) {
                            playSound(shieldHitSound);
                        } else {
                            spaceShip.hasBeenHit = true;
                            playSound(spaceShipExplosionSound);
                            spaceShip.noOfLifes--;
                            if (0 == spaceShip.noOfLifes) {
                                spaceShip.toBeRemoved = true;
                                players.add(new Player(spaceShip.score, spaceShip.isPlayer1 ? SPACEFX_COLOR : SPACEFX_COLOR1));
                            }
                        }
                    }
                } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                    toBeRemoved = true;
                }
            });
            if (System.nanoTime() - born > rocketLifespan) {
                enemyRocketExplosions.add(new EnemyRocketExplosion(x - ENEMY_ROCKET_EXPLOSION_FRAME_WIDTH * 0.25, y - ENEMY_ROCKET_EXPLOSION_FRAME_HEIGHT * 0.25, vX, vY, 0.5));
                toBeRemoved = true;
            }
        }
    }

    private class LevelBossBomb extends Sprite {
        private final List<SpaceShip> spaceShips;


        public LevelBossBomb(final List<SpaceShip> spaceShips, final Image image, final double x, final double y, final double vX, final double vY) {
            super(image, x - image.getWidth() / 2.0, y, vX, vY);
            this.spaceShips = spaceShips;
        }


        @Override public void update() {
            x += vX;
            y += vY;
            spaceShips.forEach(spaceShip -> {
                if (spaceShip.isVulnerable && !spaceShip.hasBeenHit) {
                    boolean hit;
                    if (spaceShip.shield) {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                    } else {
                        hit = Helper.isHitCircleCircle(x, y, radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                    }
                    if (hit) {
                        toBeRemoved = true;
                        if (spaceShip.shield) {
                            playSound(shieldHitSound);
                        } else {
                            spaceShip.hasBeenHit = true;
                            playSound(spaceShipExplosionSound);
                            spaceShip.noOfLifes--;
                            if (0 == spaceShip.noOfLifes) {
                                spaceShip.toBeRemoved  = true;
                                players.add(new Player(spaceShip.score, spaceShip.isPlayer1 ? SPACEFX_COLOR : SPACEFX_COLOR1));
                            }
                        }
                    }
                } else if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
                    toBeRemoved = true;
                }
            });
        }
    }


    // ******************** AnimatedSprites ***********************************
    private class EnemyRocketExplosion extends AnimatedSprite {

        public EnemyRocketExplosion(final double x, final double y, final double vX, final double vY, final double scale) {
            super(x, y, vX, vY, 4, 7, scale);
        }


        @Override public void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class AsteroidExplosion extends AnimatedSprite {
        public boolean player1Explosion;

        public AsteroidExplosion(final boolean player1Explosion, final double x, final double y, final double vX, final double vY, final double scale) {
            super(x, y, vX, vY, 8, 7, scale);
            this.player1Explosion = player1Explosion;
        }


        @Override public void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class Explosion extends AnimatedSprite {

        public Explosion(final double x, final double y, final double vX, final double vY, final double scale) {
            super(x, y, vX, vY, 8, 7, scale);
        }


        @Override public void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class EnemyBossExplosion extends AnimatedSprite {

        public EnemyBossExplosion(final double x, final double y, final double vX, final double vY, final double scale) {
            super(x, y, vX, vY, 4, 7, scale);
        }


        @Override public void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class LevelBossExplosion extends AnimatedSprite {

        public LevelBossExplosion(final double x, final double y, final double vX, final double vY, final double scale) {
            super(x, y, vX, vY, 8, 3, scale);
        }


        @Override public void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class UpExplosion extends AnimatedSprite {

        public UpExplosion(final double x, final double y, final double vX, final double vY, final double scale) {
            super(x, y, vX, vY, 4, 7, scale);
        }


        @Override public void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class SpaceShipExplosion extends AnimatedSprite {
        private final SpaceShip spaceShip;

        public SpaceShipExplosion(final SpaceShip spaceShip, final double x, final double y, final double vX, final double vY) {
            super(x, y, vX, vY, 8, 6, 1.0);
            this.spaceShip = spaceShip;
        }


        @Override public void update() {
            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class RocketExplosion extends AnimatedSprite {

        public RocketExplosion(final double x, final double y, final double vX, final double vY, final double scale) {
            super(x, y, vX, vY, 5, 4, scale);
        }


        @Override public void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class Hit extends AnimatedSprite {

        public Hit(final double x, final double y, final double vX, final double vY) {
            super(x, y, vX, vY, 5, 2, 1.0);
        }


        @Override public void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }

    private class EnemyHit extends AnimatedSprite {

        public EnemyHit(final double x, final double y, final double vX, final double vY) {
            super(x, y, vX, vY, 5, 2, 1.0);
        }


        @Override public void update() {
            x += vX;
            y += vY;

            countX++;
            if (countX == maxFrameX) {
                countY++;
                if (countX == maxFrameX && countY == maxFrameY) {
                    toBeRemoved = true;
                }
                countX = 0;
                if (countY == maxFrameY) {
                    countY = 0;
                }
            }
        }
    }


    // ******************** Bonuses *******************************************
    private class ShieldUp extends Bonus {

        public ShieldUp(final Image image) {
            super(image);
            init();
        }


        @Override protected void init() {
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

        @Override public void update() {
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
                toBeRemoved = true;
            }
        }
    }

    private class LifeUp extends Bonus {

        public LifeUp(final Image image) {
            super(image);
            init();
        }


        @Override protected void init() {
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

        @Override public void update() {
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
                toBeRemoved = true;
            }
        }
    }

    private class BigTorpedoBonus extends Bonus {

        public BigTorpedoBonus(final Image image) {
            super(image);
            init();
        }


        @Override protected void init() {
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

        @Override public void update() {
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
                toBeRemoved = true;
            }
        }
    }

    private class StarburstBonus extends Bonus {

        public StarburstBonus(final Image image) {
            super(image);
            init();
        }


        @Override protected void init() {
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

        @Override public void update() {
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
                toBeRemoved = true;
            }
        }
    }
}
