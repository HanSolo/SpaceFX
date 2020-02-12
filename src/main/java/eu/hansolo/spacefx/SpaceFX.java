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
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static eu.hansolo.spacefx.Config.*;


public class SpaceFX extends Application {
    private static final Random                     RND                        = new Random();
    private static final double                     FIRST_QUARTER_WIDTH        = WIDTH * 0.25;
    private static final double                     LAST_QUARTER_WIDTH         = WIDTH * 0.75;
    private static final double                     SHIELD_INDICATOR_X         = WIDTH * 0.73;
    private static final double                     SHIELD_INDICATOR_Y         = HEIGHT * 0.06;
    private static final double                     SHIELD_INDICATOR_WIDTH     = WIDTH * 0.26;
    private static final double                     SHIELD_INDICATOR_HEIGHT    = HEIGHT * 0.01428571;
    private static final long                       FPS_60                     = 0_016_666_666l;
    private static final long                       FPS_30                     = 0_033_333_333l;
    private static final long                       FPS_10                     = 0_100_000_000l;
    private static final long                       FPS_2                      = 0_500_000_000l;
    private static final Color                      SCORE_COLOR                = Color.rgb(51, 210, 206);
    private static final String                     SPACE_BOY;
    private static       String                     spaceBoyName;
    private static final boolean                    IS_BROWSER                 = WebAPI.isBrowser();
    private static final WaveType[]                 waveTypes                  = { WaveType.TYPE_1, WaveType.TYPE_2, WaveType.TYPE_3, WaveType.TYPE_4, WaveType.TYPE_5 };
    private              boolean                    running;
    private              boolean                    gameOverScreen;
    private              boolean                    hallOfFameScreen;
    private              List<Player>               hallOfFame;
    private              boolean                    inputAllowed;
    private              Text                       userName;
    private final        Image                      startImg                   = new Image(getClass().getResourceAsStream("startscreen.png"));
    private final        Image                      gameOverImg                = new Image(getClass().getResourceAsStream("gameover.png"));
    private final        Image                      backgroundImg              = new Image(SpaceFX.class.getResourceAsStream("background.jpg"));
    private final        Image[]                    asteroidImages             = { new Image(getClass().getResourceAsStream("asteroid1.png"), 140, 140, true, false),
                                                                                   new Image(getClass().getResourceAsStream("asteroid2.png"), 140, 140, true, false),
                                                                                   new Image(getClass().getResourceAsStream("asteroid3.png"), 140, 140, true, false),
                                                                                   new Image(getClass().getResourceAsStream("asteroid4.png"), 110, 110, true, false),
                                                                                   new Image(getClass().getResourceAsStream("asteroid5.png"), 100, 100, true, false),
                                                                                   new Image(getClass().getResourceAsStream("asteroid6.png"), 120, 120, true, false),
                                                                                   new Image(getClass().getResourceAsStream("asteroid7.png"), 110, 110, true, false),
                                                                                   new Image(getClass().getResourceAsStream("asteroid8.png"), 100, 100, true, false),
                                                                                   new Image(getClass().getResourceAsStream("asteroid9.png"), 130, 130, true, false),
                                                                                   new Image(getClass().getResourceAsStream("asteroid10.png"), 120, 120, true, false),
                                                                                   new Image(getClass().getResourceAsStream("asteroid11.png"), 140, 140, true, false) };
    private final        Image[]                    enemyImages                = { new Image(getClass().getResourceAsStream("enemy1.png"), 56, 56, true, false),
                                                                                   new Image(getClass().getResourceAsStream("enemy2.png"), 50, 50, true, false),
                                                                                   new Image(getClass().getResourceAsStream("enemy3.png"), 68, 68, true, false) };
    private final        Image                      enemyBossImg0              = new Image(getClass().getResourceAsStream("enemyBoss0.png"), 100, 100, true, false);
    private final        Image                      enemyBossImg1              = new Image(getClass().getResourceAsStream("enemyBoss1.png"), 100, 100, true, false);
    private final        Image                      enemyBossImg2              = new Image(getClass().getResourceAsStream("enemyBoss2.png"), 100, 100, true, false);
    private final        Image                      enemyBossImg3              = new Image(getClass().getResourceAsStream("enemyBoss3.png"), 100, 100, true, false);
    private final        Image                      enemyBossImg4              = new Image(getClass().getResourceAsStream("enemyBoss4.png"), 100, 100, true, false);
    private final        Image                      spaceshipImg               = new Image(getClass().getResourceAsStream("fighter.png"), 48, 48, true, false);
    private final        Image                      spaceshipThrustImg         = new Image(getClass().getResourceAsStream("fighterThrust.png"), 48, 48, true, false);
    private final        Image                      miniSpaceshipImg           = new Image(getClass().getResourceAsStream("fighter.png"), 16, 16, true, false);
    private final        Image                      deflectorShieldImg         = new Image(getClass().getResourceAsStream("deflectorshield.png"), 100, 100, true, false);
    private final        Image                      miniDeflectorShieldImg     = new Image(getClass().getResourceAsStream("deflectorshield.png"), 16, 16, true, false);
    private final        Image                      torpedoImg                 = new Image(getClass().getResourceAsStream("torpedo.png"), 17, 20, true, false);
    private final        Image                      enemyBombImg               = new Image(getClass().getResourceAsStream("enemyBomb.png"), 20, 20, true, false);
    private final        Image                      enemyTorpedoImg            = new Image(getClass().getResourceAsStream("enemyTorpedo.png"), 21, 21, true, false);
    private final        Image                      enemyBossTorpedoImg        = new Image(getClass().getResourceAsStream("enemyBossTorpedo.png"), 26, 26, true, false);
    private final        Image                      enemyBossRocketImg         = new Image(getClass().getResourceAsStream("enemyBossRocket.png"), 17, 42, true, false);
    private final        Image                      explosionImg               = new Image(getClass().getResourceAsStream("explosion1.png"), 960, 768, true, false);
    private final        Image                      asteroidExplosionImg       = new Image(getClass().getResourceAsStream("asteroidExplosion.png"), 2048, 1792, true, false);
    private final        Image                      spaceShipExplosionImg      = new Image(getClass().getResourceAsStream("spaceshipexplosion.png"), 800, 600, true, false);
    private final        Image                      hitImg                     = new Image(getClass().getResourceAsStream("torpedoHit2.png"), 400, 160, true, false);
    private final        Image                      enemyBossHitImg            = new Image(getClass().getResourceAsStream("torpedoHit.png"), 400, 160, true, false);
    private final        Image                      enemyBossExplosionImg      = new Image(getClass().getResourceAsStream("enemyBossExplosion.png"), 800, 1400, true, false);
    private final        Image                      shieldUpImg                = new Image(getClass().getResourceAsStream("shieldUp.png"), 50, 50, true, false);
    private final        Image                      lifeUpImg                  = new Image(getClass().getResourceAsStream("lifeUp.png"), 50, 50, true, false);
    private final        Image                      upExplosionImg             = new Image(getClass().getResourceAsStream("upExplosion.png"), 400, 700, true, false);
    private final        Image                      rocketExplosionImg         = new Image(getClass().getResourceAsStream("rocketExplosion.png"), 960, 768, true, false);
    private final        Image                      rocketImg                  = new Image(getClass().getResourceAsStream("rocket.png"), 17, 50, true, false);
    private final        Image                      enemyRocketExplosionImg    = new Image(getClass().getResourceAsStream("enemyRocketExplosion.png"), 512, 896, true, false);
    private final        AudioClip                  laserSound                 = new AudioClip(getClass().getResource("laserSound.wav").toExternalForm());
    private final        AudioClip                  rocketLaunchSound          = new AudioClip(getClass().getResource("rocketLaunch.wav").toExternalForm());
    private final        AudioClip                  rocketExplosionSound       = new AudioClip(getClass().getResource("rocketExplosion.wav").toExternalForm());
    private final        AudioClip                  enemyLaserSound            = new AudioClip(getClass().getResource("enemyLaserSound.wav").toExternalForm());
    private final        AudioClip                  enemyBombSound             = new AudioClip(getClass().getResource("enemyBomb.wav").toExternalForm());
    private final        AudioClip                  explosionSound             = new AudioClip(getClass().getResource("explosionSound.wav").toExternalForm());
    private final        AudioClip                  asteroidExplosionSound     = new AudioClip(getClass().getResource("asteroidExplosion.wav").toExternalForm());
    private final        AudioClip                  torpedoHitSound            = new AudioClip(getClass().getResource("hit.wav").toExternalForm());
    private final        AudioClip                  spaceShipExplosionSound    = new AudioClip(getClass().getResource("spaceShipExplosionSound.wav").toExternalForm());
    private final        AudioClip                  enemyBossExplosionSound    = new AudioClip(getClass().getResource("enemyBossExplosion.wav").toExternalForm());
    private final        AudioClip                  gameoverSound              = new AudioClip(getClass().getResource("gameover.wav").toExternalForm());
    private final        AudioClip                  shieldHitSound             = new AudioClip(getClass().getResource("shieldhit.wav").toExternalForm());
    private final        AudioClip                  enemyBossShieldHitSound    = new AudioClip(getClass().getResource("enemyBossShieldHit.wav").toExternalForm());
    private final        AudioClip                  deflectorShieldSound       = new AudioClip(getClass().getResource("deflectorshieldSound.wav").toExternalForm());
    private final        AudioClip                  shieldUpSound              = new AudioClip(getClass().getResource("shieldUp.wav").toExternalForm());
    private final        AudioClip                  lifeUpSound                = new AudioClip(getClass().getResource("lifeUp.wav").toExternalForm());
    private final        Media                      gameSoundTheme             = new Media(getClass().getResource("RaceToMars.mp3").toExternalForm());
    private final        Media                      soundTheme                 = new Media(getClass().getResource("CityStomper.mp3").toExternalForm());
    private final        MediaPlayer                gameMediaPlayer            = new MediaPlayer(gameSoundTheme);
    private final        MediaPlayer                mediaPlayer                = new MediaPlayer(soundTheme);
    private final        double                     deflectorShieldRadius      = deflectorShieldImg.getRequestedWidth() * 0.5;
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
    private              List<EnemyBossHit>         enemyBossHits;
    private              List<EnemyBossHit>         enemyBossHitsToRemove;
    private              long                       score;
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
    private              long                       lastTimerCall;
    private              AnimationTimer             timer;

    static {
        try {
            spaceBoyName = Font.loadFont(SpaceFX.class.getResourceAsStream("spaceboy.ttf"), 10).getName();
        } catch (Exception exception) { }
        SPACE_BOY = spaceBoyName;
    }


    // ******************** Methods *******************************************
    @Override public void init() {
        scoreFont        = spaceBoy(60);
        running          = false;
        gameOverScreen   = false;
        hallOfFameScreen = false;

        // PreFill hall of fame
        Player p1 = new Player("--", 0l);
        Player p2 = new Player("--", 0l);
        Player p3 = new Player("--", 0l);
        hallOfFame = new ArrayList<>(3);
        hallOfFame.add(p1);
        hallOfFame.add(p2);
        hallOfFame.add(p3);
        inputAllowed = false;
        userName = new Text("--");
        userName.setFont(spaceBoy(30));
        userName.setTextOrigin(VPos.CENTER);
        userName.setTextAlignment(TextAlignment.CENTER);
        userName.setManaged(false);
        userName.setVisible(false);

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
        backgroundViewportY         = 2079; //backgroundImg.getHeight() - HEIGHT;
        canvas                      = new Canvas(WIDTH, HEIGHT);
        ctx                         = canvas.getGraphicsContext2D();
        stars                       = new Star[NO_OF_STARS];
        asteroids                   = new Asteroid[NO_OF_ASTEROIDS];
        spaceShip                   = new SpaceShip(spaceshipImg, spaceshipThrustImg);
        spaceShipExplosion          = new SpaceShipExplosion(0, 0);
        waves                       = new ArrayList<>();
        wavesToRemove               = new ArrayList<>();
        enemyBosses                 = new ArrayList<>();
        enemyBossesToRemove         = new ArrayList<>();
        shieldUps                   = new ArrayList<>();
        shieldUpsToRemove           = new ArrayList<>();
        lifeUps                     = new ArrayList<>();
        lifeUpsToRemove             = new ArrayList<>();
        rockets                     = new ArrayList<>();
        rocketsToRemove             = new ArrayList<>();
        torpedos                    = new ArrayList<>();
        torpedosToRemove            = new ArrayList<>();
        enemyRocketExplosions = new ArrayList<>();
        enemyRocketExplosionsToRemove = new ArrayList<>();
        explosions                  = new ArrayList<>();
        explosionsToRemove          = new ArrayList<>();
        asteroidExplosions          = new ArrayList<>();
        asteroidExplosionsToRemove  = new ArrayList<>();
        upExplosions                = new ArrayList<>();
        upExplosionsToRemove        = new ArrayList<>();
        enemyTorpedos               = new ArrayList<>();
        enemyTorpedosToRemove       = new ArrayList<>();
        enemyBombs                  = new ArrayList<>();
        enemyBombsToRemove          = new ArrayList<>();
        enemyBossTorpedos           = new ArrayList<>();
        enemyBossTorpedosToRemove   = new ArrayList<>();
        enemyBossRockets            = new ArrayList<>();
        enemyBossRocketsToRemove    = new ArrayList<>();
        enemyBossExplosions         = new ArrayList<>();
        enemyBossExplosionsToRemove = new ArrayList<>();
        rocketExplosions = new ArrayList<>();
        rocketExplosionsToRemove = new ArrayList<>();
        hits                        = new ArrayList<>();
        hitsToRemove                = new ArrayList<>();
        enemyBossHits               = new ArrayList<>();
        enemyBossHitsToRemove       = new ArrayList<>();
        score                       = 0;
        hasBeenHit                  = false;
        noOfLifes                   = LIFES;
        noOfShields                 = SHIELDS;
        lastShieldActivated         = 0;
        lastEnemyBossAttack         = System.nanoTime();
        lastShieldUp                = System.nanoTime();
        lastLifeUp                  = System.nanoTime();
        lastWave                    = System.nanoTime();
        long deltaTime              = IS_BROWSER ? FPS_30 : FPS_60;
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

        initStars();
        initAsteroids();

        scorePosX = WIDTH * 0.5;
        scorePosY = 40;

        // Preparing GraphicsContext
        ctx.setFont(scoreFont);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.drawImage(startImg, 0, 0);
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
        StackPane pane = new StackPane(canvas);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

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
                    ctx.drawImage(backgroundImg, 0, 0);
                }
                if (PLAY_MUSIC) {
                    mediaPlayer.pause();
                    gameMediaPlayer.play();
                }
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
        scene.setOnKeyTyped(e -> {
            if (inputAllowed) {
                if (userName.getText().startsWith("_")) {
                    userName.setText(e.getCharacter().toUpperCase() + "_");
                } else if (userName.getText().endsWith("_")) {
                    userName.setText(userName.getText().substring(0, 1) + e.getCharacter().toUpperCase());
                    inputAllowed = false;
                }
            }
        });

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
        enemyTorpedosToRemove.clear();
        enemyBombsToRemove.clear();
        explosionsToRemove.clear();
        hitsToRemove.clear();
        asteroidExplosionsToRemove.clear();
        rocketExplosionsToRemove.clear();
        enemyRocketExplosionsToRemove.clear();
        upExplosionsToRemove.clear();
        shieldUpsToRemove.clear();
        lifeUpsToRemove.clear();
        wavesToRemove.clear();
        enemyBossesToRemove.clear();
        enemyBossHitsToRemove.clear();
        enemyBossExplosionsToRemove.clear();
        enemyBossTorpedosToRemove.clear();
        enemyBossRocketsToRemove.clear();
        ctx.clearRect(0, 0, WIDTH, HEIGHT);

        // Draw background
        if (SHOW_BACKGROUND) {
            backgroundViewportY -= 0.5;
            if (backgroundViewportY <= 0) {
                backgroundViewportY = 2079; //backgroundImg.getHeight() - HEIGHT;
            }
            ctx.drawImage(backgroundImg, 0, backgroundViewportY, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT);
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
                        enemyBossesToRemove.add(enemyBoss);
                        torpedosToRemove.add(torpedo);
                        playSound(enemyBossExplosionSound);
                    } else {
                        enemyBossHits.add(new EnemyBossHit(torpedo.x - Hit.FRAME_CENTER, torpedo.y - Hit.FRAME_HEIGHT, enemyBoss.vX, enemyBoss.vY));
                        torpedosToRemove.add(torpedo);
                        playSound(enemyBossShieldHitSound);
                    }
                }
            }

            // Check for rocket hits with enemy boss
            for (Rocket rocket : rockets) {
                if (isHitCircleCircle(rocket.x, rocket.y, rocket.radius, enemyBoss.x, enemyBoss.y, enemyBoss.radius)) {
                    enemyBossExplosions.add(
                        new EnemyBossExplosion(enemyBoss.x - EnemyBossExplosion.FRAME_WIDTH * 0.25, enemyBoss.y - EnemyBossExplosion.FRAME_HEIGHT * 0.25, enemyBoss.vX, enemyBoss.vY, 0.5));
                    score += enemyBoss.value;
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
                        explosions.add(new Explosion(enemyBoss.x - Explosion.FRAME_WIDTH * 0.125, enemyBoss.y - Explosion.FRAME_HEIGHT * 0.125, enemyBoss.vX, enemyBoss.vY, 0.5));
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
                    enemyBossesToRemove.add(enemyBoss);
                }
            }
        }
        enemyBosses.removeAll(enemyBossesToRemove);

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

        // Draw Explosions
        for (Explosion explosion : explosions) {
            explosion.update();
            ctx.drawImage(explosionImg, explosion.countX * Explosion.FRAME_WIDTH, explosion.countY * Explosion.FRAME_HEIGHT, Explosion.FRAME_WIDTH, Explosion.FRAME_HEIGHT,
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
            ctx.drawImage(enemyRocketExplosionImg, enemyRocketExplosion.countX * EnemyRocketExplosion.FRAME_WIDTH, enemyRocketExplosion.countY * EnemyRocketExplosion.FRAME_HEIGHT, EnemyRocketExplosion.FRAME_WIDTH, EnemyRocketExplosion.FRAME_HEIGHT, enemyRocketExplosion.x, enemyRocketExplosion.y, EnemyRocketExplosion.FRAME_WIDTH * enemyRocketExplosion.scale,
                          EnemyRocketExplosion.FRAME_HEIGHT * enemyRocketExplosion.scale);
        }
        enemyRocketExplosions.removeAll(enemyRocketExplosionsToRemove);

        // Draw EnemyBossExplosions
        for (EnemyBossExplosion enemyBossExplosion : enemyBossExplosions) {
            enemyBossExplosion.update();
            ctx.drawImage(enemyBossExplosionImg, enemyBossExplosion.countX * EnemyBossExplosion.FRAME_WIDTH, enemyBossExplosion.countY * EnemyBossExplosion.FRAME_HEIGHT,
                          EnemyBossExplosion.FRAME_WIDTH, EnemyBossExplosion.FRAME_HEIGHT, enemyBossExplosion.x, enemyBossExplosion.y,
                          EnemyBossExplosion.FRAME_WIDTH * enemyBossExplosion.scale, EnemyBossExplosion.FRAME_HEIGHT * enemyBossExplosion.scale);
        }
        enemyBossExplosions.removeAll(enemyBossExplosionsToRemove);

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
        for (EnemyBossHit hit : enemyBossHits) {
            hit.update();
            ctx.drawImage(enemyBossHitImg, hit.countX * Hit.FRAME_WIDTH, hit.countY * Hit.FRAME_HEIGHT, Hit.FRAME_WIDTH, Hit.FRAME_HEIGHT, hit.x, hit.y, Hit.FRAME_WIDTH, Hit.FRAME_HEIGHT);
        }
        enemyBossHits.removeAll(enemyBossHitsToRemove);

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
                ctx.drawImage((0 == spaceShip.vX && 0 == spaceShip.vY) ? spaceShip.image : spaceShip.imageThrust, spaceShip.x - spaceShip.radius, spaceShip.y - spaceShip.radius);
                ctx.restore();

                if (spaceShip.shield) {
                    long delta = System.currentTimeMillis() - lastShieldActivated;
                    if (delta > DEFLECTOR_SHIELD_TIME) {
                        spaceShip.shield = false;
                        noOfShields--;
                    } else {
                        ctx.setStroke(SCORE_COLOR);
                        ctx.setFill(SCORE_COLOR);
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
            ctx.setFill(SCORE_COLOR);
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
        enemyTorpedos.add(new EnemyTorpedo(enemyTorpedoImg, x, y, vFactor * vX, vFactor * vY));
        playSound(enemyLaserSound);
    }

    private void spawnEnemyBomb(final double x, final double y) {
        enemyBombs.add(new EnemyBomb(enemyBombImg, x, y, 0, ENEMY_BOMB_SPEED));
        playSound(enemyBombSound);
    }

    private void spawnEnemyBoss(final SpaceShip spaceShip) {
        enemyBosses.add(new EnemyBoss(spaceShip, enemyBossImg4, RND.nextBoolean()));
    }

    private void spawnShieldUp() {
        shieldUps.add(new ShieldUp(shieldUpImg));
    }

    private void spawnLifeUp() {
        lifeUps.add(new LifeUp(lifeUpImg));
    }

    private void spawnWave() {
        waves.add(new Wave(waveTypes[RND.nextInt(5)], spaceShip, 10, enemyImages[RND.nextInt(3)], RND.nextBoolean(), RND.nextBoolean()));
    }

    private void spawnEnemyBossTorpedo(final double x, final double y, final double vX, final double vY) {
        double vFactor = ENEMY_BOSS_TORPEDO_SPEED / Math.abs(vY); // make sure the speed is always the defined one
        enemyBossTorpedos.add(new EnemyBossTorpedo(enemyBossTorpedoImg, x, y, vFactor * vX, vFactor * vY));
        playSound(enemyLaserSound);
    }

    private void spawnEnemyBossRocket(final double x, final double y) {
        enemyBossRockets.add(new EnemyBossRocket(spaceShip, enemyBossRocketImg, x, y));
        playSound(rocketLaunchSound);
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

        PauseTransition pauseBeforeGameOverScreen = new PauseTransition(Duration.millis(1000));
        pauseBeforeGameOverScreen.setOnFinished(e -> {
            checkForHighScore(new Player("", score));
            ctx.clearRect(0, 0, WIDTH, HEIGHT);
            ctx.drawImage(gameOverImg, 0, 0, WIDTH, HEIGHT);
            ctx.setFill(SCORE_COLOR);
            ctx.setFont(spaceBoy(60));
            ctx.fillText(Long.toString(score), scorePosX, HEIGHT * 0.25);
            playSound(gameoverSound);
        });
        pauseBeforeGameOverScreen.play();

        PauseTransition pauseInGameOverScreen = new PauseTransition(Duration.millis(5000));
        pauseInGameOverScreen.setOnFinished(e -> {
            ctx.clearRect(0, 0, WIDTH, HEIGHT);
            ctx.drawImage(startImg, 0, 0);

            gameOverScreen = false;
            explosions.clear();
            torpedos.clear();
            enemyTorpedos.clear();
            enemyBossRockets.clear();
            enemyBosses.clear();
            shieldUps.clear();
            waves.clear();
            initAsteroids();
            spaceShip.init();
            hasBeenHit   = false;
            noOfLifes    = LIFES;
            noOfShields  = SHIELDS;
            score        = 0;
            if (PLAY_MUSIC) {
                mediaPlayer.play();
            }
        });
        pauseInGameOverScreen.play();
    }


    // Check for highscore
    private void checkForHighScore(final Player player) {
        if (player.score < hallOfFame.get(2).score) {
            return;
        }

        // Ask for player name
        inputAllowed = true;
        userName.setVisible(true);
        userName.setManaged(true);
        userName.setX(WIDTH * 0.5);
        userName.setY(HEIGHT * 0.75);

        hallOfFame.add(player);
        Collections.sort(hallOfFame);
        hallOfFame = hallOfFame.stream().limit(3).collect(Collectors.toList());

        // Show hall of fame

    }


    // Play audio clips in a separate thread
    private void playSound(final AudioClip audioClip) {
        if (PLAY_SOUND) {
            audioClip.play();
        }
    }


    // Font definition
    private static Font spaceBoy(final double size) { return new Font(SPACE_BOY, size); }


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
        private final Image   image;
        private final Image   imageThrust;
        private       long    born;
        private       double  x;
        private       double  y;
        private       double  size;
        private       double  radius;
        private       double  width;
        private       double  height;
        private       double  vX;
        private       double  vY;
        private       boolean shield;
        public        boolean isVulnerable;


        public SpaceShip(final Image image, final Image imageThrust) {
            this.image       = image;
            this.imageThrust = imageThrust;
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
        private static final double FRAME_WIDTH  = 128;
        private static final double FRAME_HEIGHT = 128;
        private static final double FRAME_CENTER = 64;
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
        private static final double FRAME_WIDTH  = 256;
        private static final double FRAME_HEIGHT = 256;
        private static final double FRAME_CENTER = 128;
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
        private static final double FRAME_WIDTH  = 192;
        private static final double FRAME_HEIGHT = 192;
        private static final double FRAME_CENTER = 96;
        private static final int    MAX_FRAME_X  = 5;
        private static final int    MAX_FRAME_Y  = 4;
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
        private static final double FRAME_WIDTH  = 100;
        private static final double FRAME_HEIGHT = 100;
        private static final double FRAME_CENTER = 50;
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
        private static final double FRAME_WIDTH  = 100;
        private static final double FRAME_HEIGHT = 100;
        private static final double FRAME_CENTER = 50;
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

    private class Hit {
        private static final double FRAME_WIDTH  = 80;
        private static final double FRAME_HEIGHT = 80;
        private static final double FRAME_CENTER = 40;
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

    private class EnemyBossHit {
        private static final double FRAME_WIDTH  = 80;
        private static final double FRAME_HEIGHT = 80;
        private static final double FRAME_CENTER = 40;
        private static final int    MAX_FRAME_X  = 5;
        private static final int    MAX_FRAME_Y  = 2;
        private              double x;
        private              double y;
        private              double vX;
        private              double vY;
        private              int    countX;
        private              int    countY;


        public EnemyBossHit(final double x, final double y, final double vX, final double vY) {
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
                    enemyBossHitsToRemove.add(EnemyBossHit.this);
                }
                countX = 0;
                if (countY == MAX_FRAME_Y) {
                    countY = 0;
                }
            }
        }
    }

    private class Enemy {
        public  static final long      TIME_BETWEEN_SHOTS  = 500_000_000l;
        public  static final long      TIME_BETWEEN_BOMBS  = 2_500_000_000l;
        public  static final double    HALF_ANGLE_OF_SIGHT = 5;
        private static final double    BOMB_RANGE          = 10;
        private static final int       MAX_VALUE           = 49;
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

            if (canFire) {
                if (System.nanoTime() - lastShot > TIME_BETWEEN_SHOTS) {
                    double[] p0 = { x, y };
                    double[] p1 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, -HALF_ANGLE_OF_SIGHT);
                    double[] p2 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, HALF_ANGLE_OF_SIGHT);

                    double area = 0.5 * (-p1[1] * p2[0] + p0[1] * (-p1[0] + p2[0]) + p0[0] * (p1[1] - p2[1]) + p1[0] * p2[1]);
                    double s    = 1 / (2 * area) * (p0[1] * p2[0] - p0[0] * p2[1] + (p2[1] - p0[1]) * spaceShip.x + (p0[0] - p2[0]) * spaceShip.y);
                    double t    = 1 / (2 * area) * (p0[0] * p1[1] - p0[1] * p1[0] + (p0[1] - p1[1]) * spaceShip.x + (p1[0] - p0[0]) * spaceShip.y);
                    if (s > 0 && t > 0 && 1 - s - t > 0) {
                        spawnEnemyTorpedo(x, y, vX * 2, vY * 2);
                        lastShot = System.nanoTime();
                    }
                }
            }

            if (canBomb && noOfBombs > 0) {
                if (System.nanoTime() - lastBomb > TIME_BETWEEN_BOMBS) {
                    if (spaceShip.x > x - BOMB_RANGE && spaceShip.x < x + BOMB_RANGE) {
                        spawnEnemyBomb(x, y);
                        lastBomb = System.nanoTime();
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

    private class EnemyBoss {
        private static final int       MAX_VALUE            = 99;
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

            if (hasRockets) {
                if (System.nanoTime() - lastRocket > TIME_BETWEEN_ROCKETS) {
                    double[] p0 = { x, y };
                    double[] p1 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, -HALF_ANGLE_OF_SIGHT);
                    double[] p2 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, HALF_ANGLE_OF_SIGHT);

                    double area = 0.5 * (-p1[1] * p2[0] + p0[1] * (-p1[0] + p2[0]) + p0[0] * (p1[1] - p2[1]) + p1[0] * p2[1]);
                    double s    = 1 / (2 * area) * (p0[1] * p2[0] - p0[0] * p2[1] + (p2[1] - p0[1]) * spaceShip.x + (p0[0] - p2[0]) * spaceShip.y);
                    double t    = 1 / (2 * area) * (p0[0] * p1[1] - p0[1] * p1[0] + (p0[1] - p1[1]) * spaceShip.x + (p1[0] - p0[0]) * spaceShip.y);
                    if (s > 0 && t > 0 && 1 - s - t > 0) {
                        spawnEnemyBossRocket(x, y);
                        lastRocket = System.nanoTime();
                    }
                }
            } else {
                if (System.nanoTime() - lastShot > TIME_BETWEEN_SHOTS) {
                    double[] p0 = { x, y };
                    double[] p1 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, -HALF_ANGLE_OF_SIGHT);
                    double[] p2 = Helper.rotatePointAroundRotationCenter(x + HEIGHT * vX, y + HEIGHT * vY, x, y, HALF_ANGLE_OF_SIGHT);

                    double area = 0.5 * (-p1[1] * p2[0] + p0[1] * (-p1[0] + p2[0]) + p0[0] * (p1[1] - p2[1]) + p1[0] * p2[1]);
                    double s    = 1 / (2 * area) * (p0[1] * p2[0] - p0[0] * p2[1] + (p2[1] - p0[1]) * spaceShip.x + (p0[0] - p2[0]) * spaceShip.y);
                    double t    = 1 / (2 * area) * (p0[0] * p1[1] - p0[1] * p1[0] + (p0[1] - p1[1]) * spaceShip.x + (p1[0] - p0[0]) * spaceShip.y);
                    if (s > 0 && t > 0 && 1 - s - t > 0) {
                        spawnEnemyBossTorpedo(x, y, vX, vY);
                        lastShot = System.nanoTime();
                    }
                }
            }

            switch (hits) {
                case 5:
                    image = enemyBossImg4;
                    break;
                case 4:
                    image = enemyBossImg3;
                    break;
                case 3:
                    image = enemyBossImg2;
                    break;
                case 2:
                    image = enemyBossImg1;
                    break;
                case 1:
                    image = enemyBossImg0;
                    break;
            }

            // Respawn Enemy
            if (x < -size || x > WIDTH + size || y > HEIGHT + size) {
                enemyBossesToRemove.add(EnemyBoss.this);
            }
        }
    }

    private class EnemyBossExplosion {
        private static final double FRAME_WIDTH  = 200;
        private static final double FRAME_HEIGHT = 200;
        private static final double FRAME_CENTER = 100;
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

    private class RocketExplosion {
        private static final double FRAME_WIDTH  = 192;
        private static final double FRAME_HEIGHT = 192;
        private static final double FRAME_CENTER = 96;
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

    private class Player implements Comparable<Player> {
        private final String id;
        private       String name;
        private       Long   score;


        public Player(final String name, final Long score) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.score = score;
        }


        @Override public int compareTo(final Player player) {
            return Long.compare(player.score, this.score);
        }
    }

    private class Wave {
        private static final long        ENEMY_SPAWN_INTERVAL = 250_000_000l;
        private        final WaveType    waveType;
        private        final SpaceShip   spaceShip;
        private        final int         noOfEnemies;
        private        final Image       image;
        private        final boolean     canFire;
        private        final boolean     canBomb;
        private        final List<Enemy> enemies;
        private        final List<Enemy> enemiesToRemove;
        private              int         enemiesSpawned;
        private              long        lastEnemySpawned;
        private              boolean     isRunning;


        public Wave(final WaveType waveType, final SpaceShip spaceShip, final int noOfEnemies, final Image image, final boolean canFire, final boolean canBomb) {
            this.waveType        = waveType;
            this.spaceShip       = spaceShip;
            this.noOfEnemies     = noOfEnemies;
            this.image           = image;
            this.canFire         = canFire;
            this.canBomb         = canBomb;
            this.enemies         = new ArrayList<>(noOfEnemies);
            this.enemiesToRemove = new ArrayList<>();
            this.enemiesSpawned  = 0;
            this.isRunning       = true;
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
                            explosions.add(new Explosion(enemy.x - Explosion.FRAME_WIDTH * 0.25, enemy.y - Explosion.FRAME_HEIGHT * 0.25, enemy.vX, enemy.vY, 0.5));
                            score += enemy.value;
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
                            enemiesToRemove.add(enemy);
                            rocketsToRemove.add(rocket);
                            playSound(rocketExplosionSound);
                        }
                    }

                    // Check for space ship hit
                    if (!hasBeenHit) {
                        boolean hit;
                        if (spaceShip.shield) {
                            hit = isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, enemy.x, enemy.y, enemy.radius);
                        } else {
                            hit = isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, enemy.x, enemy.y, enemy.radius);
                        }
                        if (hit) {
                            if (spaceShip.shield) {
                                explosions.add(new Explosion(enemy.x - Explosion.FRAME_WIDTH * 0.125, enemy.y - Explosion.FRAME_HEIGHT * 0.125, enemy.vX, enemy.vY, 0.5));
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
            Enemy enemy = new Enemy(waveType, spaceShip, image, canFire, canBomb);
            enemies.add(enemy);
            enemiesSpawned++;
        }
    }
}