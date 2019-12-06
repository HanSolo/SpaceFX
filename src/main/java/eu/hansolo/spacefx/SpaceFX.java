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
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.List;
import java.util.Random;


public class SpaceFX extends Application {
    private static final    Random             RND                     = new Random();
    private static final    double             WIDTH                   = 700;
    private static final    double             HEIGHT                  = 700;
    private static final    double             SHIELD_INDICATOR_X      = WIDTH * 0.73;
    private static final    double             SHIELD_INDICATOR_Y      = HEIGHT * 0.06;
    private static final    double             SHIELD_INDICATOR_WIDTH  = WIDTH * 0.26;
    private static final    double             SHIELD_INDICATOR_HEIGHT = HEIGHT * 0.01428571;
    private static final    long               FPS_60                  = 0_016_666_666l;
    private static final    long               FPS_30                  = 0_033_333_333l;
    private static final    long               FPS_10                  = 0_100_000_000l;
    private static final    int                NO_OF_STARS             = 20;
    private static final    int                NO_OF_ASTEROIDS         = 20;
    private static final    int                NO_OF_ENEMIES           = 3;
    private static final    double             VELOCITY_FACTOR_X       = 1.0;
    private static final    double             VELOCITY_FACTOR_Y       = 1.0;
    private static final    double             VELOCITY_FACTOR_R       = 1.0;
    private static final    int                LIFES                   = 5;
    private static final    int                SHIELDS                 = 10;
    private static final    int                DEFLECTOR_SHIELD_TIME   = 5000;
    private static final    Color              SCORE_COLOR             = Color.rgb(51, 210, 206);
    private static final    String             SPACE_BOY;
    private static          String             spaceBoyName;
    private static final    boolean            IS_BROWSER = WebAPI.isBrowser();
    private                 boolean            running;
    private                 boolean            gameOverScreen;
    private final           Image              startImg                = new Image(getClass().getResourceAsStream("startscreen.png"));
    private final           Image              gameOverImg             = new Image(getClass().getResourceAsStream("gameover.png"));
    private final           Image              nebularImg              = new Image(SpaceFX.class.getResourceAsStream("nebular.jpg"));
    private final           Image[]            asteroidImages          = { new Image(getClass().getResourceAsStream("asteroid1.png"), 150, 150, true, false),
                                                                           new Image(getClass().getResourceAsStream("asteroid2.png"), 150, 150, true, false),
                                                                           new Image(getClass().getResourceAsStream("asteroid3.png"), 150, 150, true, false),
                                                                           new Image(getClass().getResourceAsStream("asteroid4.png"), 120, 120, true, false),
                                                                           new Image(getClass().getResourceAsStream("asteroid5.png"), 100, 100, true, false),
                                                                           new Image(getClass().getResourceAsStream("asteroid6.png"), 130, 130, true, false),
                                                                           new Image(getClass().getResourceAsStream("asteroid7.png"), 120, 120, true, false),
                                                                           new Image(getClass().getResourceAsStream("asteroid8.png"), 110, 110, true, false),
                                                                           new Image(getClass().getResourceAsStream("asteroid9.png"), 140, 140, true, false),
                                                                           new Image(getClass().getResourceAsStream("asteroid10.png"), 130, 130, true, false),
                                                                           new Image(getClass().getResourceAsStream("asteroid11.png"), 150, 150, true, false) };
    private final           Image[]            enemyImages             = { new Image(getClass().getResourceAsStream("enemy1.png"), 48, 48, true, false),
                                                                           new Image(getClass().getResourceAsStream("enemy2.png"), 36, 36, true, false),
                                                                           new Image(getClass().getResourceAsStream("enemy3.png"), 68, 68, true, false) };
    private final           Image              spaceshipImg            = new Image(getClass().getResourceAsStream("fighter.png"), 48, 48, true, false);
    private final           Image              spaceshipThrustImg      = new Image(getClass().getResourceAsStream("fighterThrust.png"), 48, 48, true, false);
    private final           Image              miniSpaceshipImg        = new Image(getClass().getResourceAsStream("fighter.png"), 16, 16, true, false);
    private final           Image              deflectorShieldImg      = new Image(getClass().getResourceAsStream("deflectorshield.png"), 100, 100, true, false);
    private final           Image              miniDeflectorShieldImg  = new Image(getClass().getResourceAsStream("deflectorshield.png"), 16, 16, true, false);
    private final           Image              torpedoImg              = new Image(getClass().getResourceAsStream("torpedo.png"), 17, 20, true, false);
    private final           Image              enemyTorpedoImg         = new Image(getClass().getResourceAsStream("enemyTorpedo.png"), 21, 21, true, false);
    private final           Image              explosionImg            = new Image(getClass().getResourceAsStream("explosion.png"), 960, 768, true, false);
    private final           Image              spaceShipExplosionImg   = new Image(getClass().getResourceAsStream("spaceshipexplosion.png"), 800, 600, true, false);
    private final           AudioClip          laserSound              = new AudioClip(getClass().getResource("laserSound.wav").toExternalForm());
    private final           AudioClip          enemyLaserSound         = new AudioClip(getClass().getResource("enemyLaserSound.wav").toExternalForm());
    private final           AudioClip          explosionSound          = new AudioClip(getClass().getResource("explosionSound.wav").toExternalForm());
    private final           AudioClip          spaceShipExplosionSound = new AudioClip(getClass().getResource("spaceShipExplosionSound.wav").toExternalForm());
    private final           AudioClip          gameoverSound           = new AudioClip(getClass().getResource("gameover.wav").toExternalForm());
    private final           AudioClip          shieldHitSound          = new AudioClip(getClass().getResource("shieldhit.wav").toExternalForm());
    private final           AudioClip          deflectorShieldSound    = new AudioClip(getClass().getResource("deflectorshieldSound.wav").toExternalForm());
    private final           Media              gameSoundTheme          = new Media(getClass().getResource("RaceToMars.mp3").toExternalForm());
    private final           Media              soundTheme              = new Media(getClass().getResource("CityStomper.mp3").toExternalForm());
    private final           MediaPlayer        gameMediaPlayer         = new MediaPlayer(gameSoundTheme);
    private final           MediaPlayer        mediaPlayer             = new MediaPlayer(soundTheme);
    private final           Text               scoreText               = new Text("0");
    private                 Font               scoreFont;
    private                 Timeline           timeline;
    private                 ImageView          background;
    private                 Canvas             canvas;
    private                 GraphicsContext    ctx;
    private                 Star[]             stars;
    private                 Asteroid[]         asteroids;
    private                 Enemy[]            enemies;
    private                 SpaceShip          spaceShip;
    private                 SpaceShipExplosion spaceShipExplosion;
    private                 List<Torpedo>      torpedos;
    private                 List<Torpedo>      torpedosToRemove;
    private                 List<EnemyTorpedo> enemyTorpedos;
    private                 List<EnemyTorpedo> enemyTorpedosToRemove;
    private                 List<Explosion>    explosions;
    private                 List<Explosion>    explosionsToRemove;
    private                 long               score;
    private                 double             scorePosX;
    private                 double             scorePosY;
    private                 boolean            destroy;
    private                 int                noOfLifes;
    private                 int                noOfShields;
    private                 long               lastShieldActivated;
    private                 long               lastTimerCall;
    private                 AnimationTimer     timer;

    static {
        try {
            spaceBoyName = Font.loadFont(SpaceFX.class.getResourceAsStream("spaceboy.ttf"), 10).getName();
        } catch (Exception exception) { }
        SPACE_BOY = spaceBoyName;
    }


    // ******************** Methods *******************************************
    @Override public void init() {
        scoreFont = spaceBoy(30);
        running        = false;
        gameOverScreen = false;

        timeline = new Timeline();
        timeline.setOnFinished(e -> {
            gameOverScreen = false;
            scoreText.setVisible(false);
            destroy        = false;
            noOfLifes      = LIFES;
            noOfShields    = SHIELDS;
            score          = 0;
            mediaPlayer.play();
        });

        // Mediaplayer for background music
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.2);

        // Mediaplayer for game background music
        gameMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        gameMediaPlayer.setVolume(0.2);

        // Variable initialization
        background            = new ImageView(startImg);
        canvas                = new Canvas(WIDTH, HEIGHT);
        ctx                   = canvas.getGraphicsContext2D();
        stars                 = new Star[NO_OF_STARS];
        asteroids             = new Asteroid[NO_OF_ASTEROIDS];
        enemies               = new Enemy[NO_OF_ENEMIES];
        spaceShip             = new SpaceShip(spaceshipImg, spaceshipThrustImg);
        spaceShipExplosion    = new SpaceShipExplosion(0, 0);
        torpedos              = new ArrayList<>();
        torpedosToRemove      = new ArrayList<>();
        explosions            = new ArrayList<>();
        explosionsToRemove    = new ArrayList<>();
        enemyTorpedos         = new ArrayList<>();
        enemyTorpedosToRemove = new ArrayList<>();
        score                 = 0;
        destroy               = false;
        noOfLifes             = LIFES;
        noOfShields           = SHIELDS;
        lastShieldActivated   = 0;
        timer                 = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall) {
                    draw();
                    lastTimerCall = now + FPS_60;
                }
            }
        };
        for (int i = 0 ; i < NO_OF_STARS ; i++) { stars[i] = spawnStar(); }
        for (int i = 0 ; i < NO_OF_ASTEROIDS ; i++) { asteroids[i] = spawnAsteroid(); }
        for (int i = 0 ; i < NO_OF_ENEMIES ; i ++) { enemies[i] = spawnEnemy(); }

        scorePosX = WIDTH * 0.5;
        scorePosY = 30;

        // Preparing GraphicsContext
        ctx.setFont(scoreFont);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);

        // Score label (visible in game over screen)
        scoreText.setFont(spaceBoy(60));
        scoreText.setMouseTransparent(true);
        scoreText.setFill(Color.rgb(51, 210, 206));
        scoreText.setTextOrigin(VPos.CENTER);
        scoreText.setVisible(false);
        scoreText.setTranslateY(-HEIGHT * 0.25);
    }

    @Override public void start(final Stage stage) {
        StackPane pane = new StackPane(background, scoreText, canvas);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        // Setup key listener
        scene.setOnKeyPressed(e -> {
            if (running) {
                switch(e.getCode()) {
                    case UP:
                        spaceShip.dY = -5;
                        break;
                    case RIGHT:
                        spaceShip.dX = 5;
                        break;
                    case DOWN:
                        spaceShip.dY = 5;
                        break;
                    case LEFT:
                        spaceShip.dX = -5;
                        break;
                    case S:
                        if (noOfShields > 0 && !spaceShip.shield) {
                            lastShieldActivated = System.currentTimeMillis(); spaceShip.shield = true;
                            playSound(deflectorShieldSound);
                        }
                        break;
                    case SPACE:
                        spawnTorpedo(spaceShip.x + spaceShip.width / 2, spaceShip.y);
                        break;
                }
            } else if (e.getCode() == KeyCode.P && !gameOverScreen) {
                background.setImage(nebularImg);
                mediaPlayer.stop();
                gameMediaPlayer.play();
                running = true;
                timer.start();
            }
        });
        scene.setOnKeyReleased( e -> {
            if (running) {
                switch (e.getCode()) {
                    case UP   : spaceShip.dY = 0; break;
                    case RIGHT: spaceShip.dX = 0; break;
                    case DOWN : spaceShip.dY = 0; break;
                    case LEFT : spaceShip.dX = 0; break;
                }
            }
        });

        stage.setTitle("SpaceFX");
        stage.setScene(scene);
        stage.show();

        // Start playing background music
        mediaPlayer.play();
    }

    @Override public void stop() {
        if (!IS_BROWSER) {
            Platform.exit();
            System.exit(0);
        }
    }


    // Draw method which will be called by animation timer
    private void draw() {
        torpedosToRemove.clear();
        enemyTorpedosToRemove.clear();
        explosionsToRemove.clear();
        Rectangle2D spaceShipBounds = spaceShip.getBounds();
        ctx.clearRect(0, 0, WIDTH, HEIGHT);

        ctx.setFill(Color.rgb(255, 255, 255, 0.9));
        // Draw Stars and Asteroids
        for (int i = 0 ; i < NO_OF_ASTEROIDS ; i++) {
            // Draw Stars
            Star s = stars[i];
            ctx.fillOval(s.x, s.y, s.size, s.size);
            s.x += s.vX;
            s.y += s.vY;
            // Respawn star
            if(s.y > HEIGHT + s.size) {
                stars[i].x = RND.nextDouble() * WIDTH;
                stars[i].y = -s.size;
            }

            // Draw Asteroids
            Asteroid p = asteroids[i];

            ctx.save();
            ctx.translate(p.x, p.y);
            ctx.scale(p.scale, p.scale);
            ctx.translate(p.width * (-0.5), p.height * (-0.5));

            ctx.save();
            ctx.translate(p.width * 0.5, p.height * 0.5);
            ctx.rotate(p.rot);
            ctx.translate(-p.width * 0.5, -p.height * 0.5);
            ctx.drawImage(p.image, 0, 0);
            ctx.restore();
            ctx.restore();

            p.x += p.vX;
            p.y += p.vY;

            if (p.rotateRight) {
                p.rot += p.vR;
                if (p.rot > 360) { p.rot = 0; }
            } else {
                p.rot -= p.vR;
                if (p.rot < 0) { p.rot = 360; }
            }

            // Respawn Asteroid
            if(p.y > HEIGHT + p.height) {
                //asteroids[i].x = RND.nextDouble() * WIDTH;
                //asteroids[i].y = -p.image.getHeight();
                asteroids[i] = spawnAsteroid();
            }

            // Check for torpedo hits
            for (Torpedo torpedo : torpedos) {
                if (torpedo.intersects(p.getBounds())) {
                    explosions.add(new Explosion(p.x - Explosion.FRAME_WIDTH * 0.5 * p.scale, p.y - Explosion.FRAME_HEIGHT * 0.5 * p.scale, p.vX, p.vY, p.scale));
                    score += p.value;
                    asteroids[i] = spawnAsteroid();
                    torpedosToRemove.add(torpedo);
                    playSound(explosionSound);
                }
            }

            // Check for space ship hit
            if (!destroy && spaceShipBounds.intersects(p.getBounds())) {
                spaceShipExplosion.countX = 0;
                spaceShipExplosion.countY = 0;
                spaceShipExplosion.x      = spaceShip.x - SpaceShipExplosion.FRAME_WIDTH;
                spaceShipExplosion.y      = spaceShip.y - SpaceShipExplosion.FRAME_HEIGHT;
                asteroids[i]              = spawnAsteroid();
                if (spaceShip.shield) {
                    playSound(explosionSound);
                    explosions.add(new Explosion(p.x - Explosion.FRAME_WIDTH * 0.5 * p.scale, p.y - Explosion.FRAME_HEIGHT * 0.5 * p.scale, p.vX, p.vY, p.scale));
                } else {
                    playSound(spaceShipExplosionSound);
                    destroy = true;
                    noOfLifes--;
                    if (0 == noOfLifes) {
                        gameOver();
                    }
                }
            }
        }

        // Draw Enemies
        for (int i = 0 ; i < NO_OF_ENEMIES ; i++) {
            Enemy e = enemies[i];

            ctx.save();
            ctx.translate(e.x, e.y);
            ctx.drawImage(e.image, 0, 0);
            ctx.restore();

            // Fire if spaceship is below enemy
            if (e.x > spaceShip.x - 2 && e.x < spaceShip.x + 2) {
                if (e.y - e.lastShotY > 15) {
                    spawnEnemyTorpedo(e.x + e.width / 2, e.y);
                    e.lastShotY = e.y;
                }
            }

            e.x += e.vX;
            e.y += e.vY;

            // Respawn Enemy
            if(e.y > HEIGHT + e.height) {
                enemies[i] = spawnEnemy();
            }

            // Check for torpedo hits
            for (Torpedo torpedo : torpedos) {
                if (torpedo.intersects(e.getBounds())) {
                    explosions.add(new Explosion(e.x - Explosion.FRAME_WIDTH * 0.125, e.y - Explosion.FRAME_HEIGHT * 0.125, e.vX, e.vY, 0.5));
                    score += e.value;
                    enemies[i] = spawnEnemy();
                    torpedosToRemove.add(torpedo);
                    playSound(spaceShipExplosionSound);
                }
            }

            // Check for space ship hit
            if (!destroy && spaceShipBounds.intersects(e.getBounds())) {
                spaceShipExplosion.countX = 0;
                spaceShipExplosion.countY = 0;
                spaceShipExplosion.x      = spaceShip.x - SpaceShipExplosion.FRAME_WIDTH;
                spaceShipExplosion.y      = spaceShip.y - SpaceShipExplosion.FRAME_HEIGHT;
                enemies[i]                = spawnEnemy();
                playSound(spaceShipExplosionSound);
                if (spaceShip.shield) {
                    explosions.add(new Explosion(e.x - Explosion.FRAME_WIDTH * 0.125, e.y - Explosion.FRAME_HEIGHT * 0.125, e.vX, e.vY, 0.5));
                } else {
                    destroy = true;
                    noOfLifes--;
                    if (0 == noOfLifes) {
                        gameOver();
                    }
                }
            }
        }

        // Draw Torpedos
        for (Torpedo torpedo : torpedos) {
            torpedo.y -= torpedo.dY;
            if (torpedo.y < -torpedo.height) {
                torpedosToRemove.add(torpedo);
            }
            ctx.drawImage(torpedo.image, torpedo.x, torpedo.y);
        }
        torpedos.removeAll(torpedosToRemove);

        // Draw EnemyTorpedos
        for (EnemyTorpedo enemyTorpedo : enemyTorpedos) {
            enemyTorpedo.y += enemyTorpedo.dY;
            if (!destroy && enemyTorpedo.intersects(spaceShip.getBounds())) {
                enemyTorpedosToRemove.add(enemyTorpedo);
                if (spaceShip.shield) {
                    playSound(shieldHitSound);
                } else {
                    destroy = true;
                    playSound(spaceShipExplosionSound);
                    noOfLifes--;
                    if (0 == noOfLifes) {
                        gameOver();
                    }
                }
            } else if (enemyTorpedo.y > HEIGHT) {
                enemyTorpedosToRemove.add(enemyTorpedo);
            }
            ctx.drawImage(enemyTorpedo.image, enemyTorpedo.x, enemyTorpedo.y);
        }
        enemyTorpedos.removeAll(enemyTorpedosToRemove);

        // Draw Explosions
        for (Explosion e : explosions) {
            ctx.drawImage(explosionImg, e.countX * Explosion.FRAME_WIDTH, e.countY * Explosion.FRAME_HEIGHT, Explosion.FRAME_WIDTH, Explosion.FRAME_HEIGHT, e.x, e.y, Explosion.FRAME_WIDTH * e.scale, Explosion.FRAME_HEIGHT * e.scale);
            e.x += e.vX;
            e.y += e.vY;
            e.countX++;
            if (e.countX == 5) {
                e.countY++;
                if (e.countX == 5 && e.countY == 4) {
                    explosionsToRemove.add(e);
                }
                e.countX = 0;
                if (e.countY == 4) {
                    e.countY = 0;
                }
            }
        }
        explosions.removeAll(explosionsToRemove);

        if (noOfLifes > 0) {
            // Draw Spaceship or it's explosion
            if (destroy) {
                ctx.drawImage(spaceShipExplosionImg, spaceShipExplosion.countX * spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.countY * spaceShipExplosion.FRAME_HEIGHT,
                              spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.FRAME_HEIGHT, spaceShip.x - spaceShip.width * 0.5, spaceShip.y - spaceShip.height * 0.5,
                              spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.FRAME_HEIGHT);

                spaceShipExplosion.countX++;
                if (spaceShipExplosion.countX == 8) {
                    spaceShipExplosion.countX = 0;
                    spaceShipExplosion.countY++;
                    if (spaceShipExplosion.countY == 6) {
                        spaceShipExplosion.countY = 0;
                    }
                    if (spaceShipExplosion.countX == 0 && spaceShipExplosion.countY == 0) {
                        destroy = false;
                        spaceShip.x = WIDTH / 2.0 - spaceShip.width / 2.0;
                        spaceShip.y = HEIGHT - 2 * spaceShip.height;
                    }
                }
            } else {
                // Draw space ship
                spaceShip.x += spaceShip.dX;
                spaceShip.y += spaceShip.dY;

                if (spaceShip.x + spaceShip.width > WIDTH) {
                    spaceShip.x = WIDTH - spaceShip.width;
                }
                if (spaceShip.x < 0) {
                    spaceShip.x = 0;
                }
                if (spaceShip.y + spaceShip.height > HEIGHT) {
                    spaceShip.y = HEIGHT - spaceShip.height;
                }
                if (spaceShip.y < 0) {
                    spaceShip.y = 0;
                }
                ctx.drawImage((0 == spaceShip.dX && 0 == spaceShip.dY) ? spaceShip.image : spaceShip.imageThrust, spaceShip.x, spaceShip.y);

                if (spaceShip.shield) {
                    long delta = System.currentTimeMillis() - lastShieldActivated;
                    if (delta > DEFLECTOR_SHIELD_TIME) {
                        spaceShip.shield = false;
                        noOfShields--;
                    } else {
                        ctx.setStroke(SCORE_COLOR);
                        ctx.setFill(SCORE_COLOR);
                        ctx.strokeRect(SHIELD_INDICATOR_X, SHIELD_INDICATOR_Y, SHIELD_INDICATOR_WIDTH, SHIELD_INDICATOR_HEIGHT);
                        ctx.fillRect(SHIELD_INDICATOR_X, SHIELD_INDICATOR_Y, SHIELD_INDICATOR_WIDTH - SHIELD_INDICATOR_WIDTH * delta / DEFLECTOR_SHIELD_TIME, SHIELD_INDICATOR_HEIGHT);
                        ctx.setGlobalAlpha(RND.nextDouble() * 0.5 + 0.1);
                        ctx.drawImage(deflectorShieldImg, spaceShip.x - 26, spaceShip.y - 26);
                        ctx.setGlobalAlpha(1);
                    }
                }
            }

            // Draw score
            ctx.setFill(SCORE_COLOR);
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
    private Star spawnStar() {
        return new Star();
    }

    private Asteroid spawnAsteroid() {
        return new Asteroid(asteroidImages[RND.nextInt(asteroidImages.length - 1)]);
    }

    private Enemy spawnEnemy() {
        return new Enemy(enemyImages[RND.nextInt(enemyImages.length - 1)]);
    }

    private void spawnTorpedo(final double x, final double y) {
        torpedos.add(new Torpedo(torpedoImg, x, y));
        playSound(laserSound);
    }

    private void spawnEnemyTorpedo(final double x, final double y) {
        enemyTorpedos.add(new EnemyTorpedo(enemyTorpedoImg, x, y));
        playSound(enemyLaserSound);
    }


    // Game Over
    private void gameOver() {
        running        = false;
        gameOverScreen = true;
        timer.stop();
        scoreText.setText(Long.toString(score));
        scoreText.setVisible(true);
        ctx.clearRect(0, 0, WIDTH, HEIGHT);
        for (int i = 0 ; i < NO_OF_STARS ; i++) { stars[i] = spawnStar(); }
        for (int i = 0 ; i < NO_OF_ASTEROIDS ; i++) { asteroids[i] = spawnAsteroid(); }
        for (int i = 0 ; i < NO_OF_ENEMIES ; i ++) { enemies[i] = spawnEnemy(); }

        gameMediaPlayer.stop();

        KeyFrame kf0 = new KeyFrame(Duration.ZERO, new KeyValue(background.imageProperty(), gameOverImg));
        KeyFrame kf1 = new KeyFrame(Duration.seconds(5), new KeyValue(background.imageProperty(), startImg));
        timeline.getKeyFrames().setAll(kf0, kf1);
        timeline.play();
        playSound(gameoverSound);
    }


    // Play audio clips in a separate thread
    private void playSound(final AudioClip audioClip) {
        new Thread(() -> audioClip.play()).run();
    }


    // Font definition
    private static Font spaceBoy(final double size) { return new Font(SPACE_BOY, size); }


    // ******************** Misc **********************************************
    public static void main(String[] args) {
        launch(args);
    }


    // ******************** Space Object Classes ******************************
    private class Star {
        private final Random  rnd          = new Random();
        private final double  xVariation   = 0;
        private final double  minSpeedY    = 4;
        private       double  x;
        private       double  y;
        private       double  size;
        private       double  vX;
        private       double  vY;
        private       double  vYVariation;


        public Star() {
            // Random size
            size = (rnd.nextDouble() * 1.0) + 2;

            // Position
            x = rnd.nextDouble() * WIDTH;
            y = -size;

            // Random Speed
            vYVariation = (rnd.nextDouble() * 0.5) + 0.2;

            // Velocity
            vX = ((rnd.nextDouble() * xVariation) - xVariation * 0.5) * VELOCITY_FACTOR_X;
            vY = (((rnd.nextDouble() * 1.5) + minSpeedY) * vYVariation) * VELOCITY_FACTOR_Y;
        }
    }

    private class Asteroid {
        private static final int     MAX_VALUE    = 10;
        private final        Random  rnd          = new Random();
        private final        double  xVariation   = 2;
        private final        double  minSpeedY    = 2;
        private final        double  minRotationR = 0.1;
        private final        Image   image;
        private              double  x;
        private              double  y;
        private              double  width;
        private              double  height;
        private              double  radius;
        private              double  rot;
        private              double  vX;
        private              double  vY;
        private              double  vR;
        private              boolean rotateRight;
        private              double  scale;
        private              double  vYVariation;
        private              int     value;


        // ******************** Constructor ***********************************
        public Asteroid(final Image image) {
            // Image
            this.image = image;

            // Position
            x = rnd.nextDouble() * WIDTH;
            y = -image.getHeight();
            rot = 0;

            // Random Size
            scale = (rnd.nextDouble() * 0.6) + 0.2;

            // Value
            value = (int) (1 / scale * MAX_VALUE);

            // Random Speed
            vYVariation = (rnd.nextDouble() * 0.5) + 0.2;

            width  = image.getWidth() * scale;
            height = image.getHeight() * scale;
            radius = width * 0.5;

            // Velocity
            vX          = ((rnd.nextDouble() * xVariation) - xVariation * 0.5) * VELOCITY_FACTOR_X;
            vY          = (((rnd.nextDouble() * 1.5) + minSpeedY * 1/scale) * vYVariation) * VELOCITY_FACTOR_Y;
            vR          = (((rnd.nextDouble()) * 0.5) + minRotationR) * VELOCITY_FACTOR_R;
            rotateRight = rnd.nextBoolean();
        }

        public Rectangle2D getBounds() {
            return new Rectangle2D(x - width * 0.5, y - height * 0.5, width, height);
        }

        public boolean intersects(final Asteroid asteroid) {
            return intersects(asteroid.getBounds());
        }
        public boolean intersects(final Rectangle2D bounds) {
            return this.getBounds().intersects(bounds);
        }
    }

    private class SpaceShip {
        private final Image       image;
        private final Image       imageThrust;
        private       double      x;
        private       double      y;
        private       double      width;
        private       double      height;
        private       double      dX;
        private       double      dY;
        private       boolean     shield;


        public SpaceShip(final Image image, final Image imageThrust) {
            this.image       = image;
            this.imageThrust = imageThrust;
            this.x           = WIDTH / 2.0 - image.getWidth() / 2.0;
            this.y           = HEIGHT - 2 * image.getHeight();
            this.width       = image.getWidth();
            this.height      = image.getHeight();
            this.dX          = 0;
            this.dY          = 0;
            this.shield      = false;
        }

        public Rectangle2D getBounds() {
            return shield ? new Rectangle2D(x - 26, y - 26, width + 52, height + 52) : new Rectangle2D(x, y, width, height);
        }

        public boolean intersects(final Rectangle2D bounds) {
            return this.getBounds().intersects(bounds);
        }
    }

    private class Torpedo {
        private final Image  image;
        private       double x;
        private       double y;
        private       double width;
        private       double height;
        private       double dX;
        private       double dY;


        public Torpedo(final Image image) {
            this(image, 0, 0);
        }
        public Torpedo(final Image image, final double x, final double y) {
            this.image  = image;
            this.x      = x - image.getWidth() / 2.0;
            this.y      = y - image.getHeight();
            this.width  = image.getWidth();
            this.height = image.getHeight();
            this.dX     = 0;
            this.dY     = 5;
        }

        public Rectangle2D getBounds() { return new Rectangle2D(x - width * 0.5, y - height * 0.5, width, height); }

        public boolean intersects(final Rectangle2D bounds) {
            return this.getBounds().intersects(bounds);
        }
    }

    private class Explosion {
        private static final double FRAME_WIDTH  = 192;
        private static final double FRAME_HEIGHT = 192;
        private              double x;
        private              double y;
        private              double vX;
        private              double vY;
        private              double scale;
        private              int    countX;
        private              int    countY;


        public Explosion(final double x, final double y, final double vX, final double vY, final double scale) {
            this.x      = x;
            this.y      = y;
            this.vX     = vX;
            this.vY     = vY;
            this.scale  = scale;
            this.countX = 0;
            this.countY = 0;
        }
    }

    private class SpaceShipExplosion {
        private static final double FRAME_WIDTH  = 100;
        private static final double FRAME_HEIGHT = 100;
        private              double x;
        private              double y;
        private              int    countX;
        private              int    countY;


        public SpaceShipExplosion(final double x, final double y) {
            this.x      = x;
            this.y      = y;
            this.countX = 0;
            this.countY = 0;
        }
    }

    private class Enemy {
        public  static final int     MAX_NO_OF_SHOTS = 5;
        private static final int     MAX_VALUE       = 49;
        private final        Random  rnd             = new Random();
        private final        double  xVariation      = 1;
        private final        double  minSpeedY       = 3;
        private final        Image   image;
        private              double  x;
        private              double  y;
        private              double  width;
        private              double  height;
        private              double  size;
        private              double  vX;
        private              double  vY;
        private              double  vYVariation;
        private              int     value;
        private              double  lastShotY;


        // ******************** Constructor ***********************************
        public Enemy(final Image image) {
            // Image
            this.image = image;

            // Position
            x = rnd.nextDouble() * WIDTH;
            y = -image.getHeight();

            // Value
            value = rnd.nextInt(MAX_VALUE) + 1;

            // Random Speed
            vYVariation = (rnd.nextDouble() * 0.5) + 0.2;

            width  = image.getWidth();
            height = image.getHeight();
            size   = width < height ? height : width;

            // Velocity
            vX = ((rnd.nextDouble() * xVariation) - xVariation * 0.5) * VELOCITY_FACTOR_X;
            vY = (((rnd.nextDouble() * 1.5) + minSpeedY) * vYVariation) * VELOCITY_FACTOR_Y;

            // Related to laser fire
            lastShotY = 0;
        }

        public Rectangle2D getBounds() {
            return new Rectangle2D(x - width * 0.5, y - height * 0.5, width, height);
        }

        public boolean intersects(final Asteroid asteroid) {
            return intersects(asteroid.getBounds());
        }
        public boolean intersects(final Rectangle2D bounds) {
            return this.getBounds().intersects(bounds);
        }
    }

    private class EnemyTorpedo {
        private final Image  image;
        private       double x;
        private       double y;
        private       double width;
        private       double height;
        private       double dX;
        private       double dY;


        public EnemyTorpedo(final Image image, final double x, final double y) {
            this.image  = image;
            this.x      = x - image.getWidth() / 2.0;
            this.y      = y;
            this.width  = image.getWidth();
            this.height = image.getHeight();
            this.dX     = 0;
            this.dY     = 5;
        }

        public Rectangle2D getBounds() { return new Rectangle2D(x - width * 0.5, y - height * 0.5, width, height); }

        public boolean intersects(final Rectangle2D bounds) {
            return this.getBounds().intersects(bounds);
        }
    }
}