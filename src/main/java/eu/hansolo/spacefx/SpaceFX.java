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
    private static final    int                NO_OF_STARS             = 15;
    private static final    int                NO_OF_ASTEROIDS         = 15;
    private static final    int                NO_OF_ENEMIES           = 3;
    private static final    double             VELOCITY_FACTOR_X       = 1.0;
    private static final    double             VELOCITY_FACTOR_Y       = 1.0;
    private static final    double             VELOCITY_FACTOR_R       = 1.0;
    private static final    int                LIFES                   = 5;
    private static final    int                SHIELDS                 = 10;
    private static final    int                DEFLECTOR_SHIELD_TIME   = 5000;
    private static final    Color              SCORE_COLOR             = Color.rgb(51, 210, 206);
    private static final    double             TORPEDO_SPEED           = 6;
    private static final    double             ENEMY_TORPEDO_SPEED     = 5;
    private static final    String             SPACE_BOY;
    private static          String             spaceBoyName;
    private static final    boolean            IS_BROWSER = WebAPI.isBrowser();
    private                 boolean            running;
    private                 boolean            gameOverScreen;
    private final           Image              startImg                = new Image(getClass().getResourceAsStream("startscreen.png"));
    private final           Image              gameOverImg             = new Image(getClass().getResourceAsStream("gameover.png"));
    private final           Image              nebularImg              = new Image(SpaceFX.class.getResourceAsStream("nebular.jpg"));
    private final           Image[]            asteroidImages          = { new Image(getClass().getResourceAsStream("asteroid1.png"), 140, 140, true, false),
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
    private final           Image              hitImg                  = new Image(getClass().getResourceAsStream("torpedoHit1.png"), 400, 160, true, false);
    private final           AudioClip          laserSound              = new AudioClip(getClass().getResource("laserSound.wav").toExternalForm());
    private final           AudioClip          enemyLaserSound         = new AudioClip(getClass().getResource("enemyLaserSound.wav").toExternalForm());
    private final           AudioClip          explosionSound          = new AudioClip(getClass().getResource("explosionSound.wav").toExternalForm());
    private final           AudioClip          torpedoHitSound         = new AudioClip(getClass().getResource("hit.wav").toExternalForm());
    private final           AudioClip          spaceShipExplosionSound = new AudioClip(getClass().getResource("spaceShipExplosionSound.wav").toExternalForm());
    private final           AudioClip          gameoverSound           = new AudioClip(getClass().getResource("gameover.wav").toExternalForm());
    private final           AudioClip          shieldHitSound          = new AudioClip(getClass().getResource("shieldhit.wav").toExternalForm());
    private final           AudioClip          deflectorShieldSound    = new AudioClip(getClass().getResource("deflectorshieldSound.wav").toExternalForm());
    private final           Media              gameSoundTheme          = new Media(getClass().getResource("RaceToMars.mp3").toExternalForm());
    private final           Media              soundTheme              = new Media(getClass().getResource("CityStomper.mp3").toExternalForm());
    private final           MediaPlayer        gameMediaPlayer         = new MediaPlayer(gameSoundTheme);
    private final           MediaPlayer        mediaPlayer             = new MediaPlayer(soundTheme);
    private final           Text               scoreText               = new Text("0");
    private final           double             deflectorShieldRadius   = deflectorShieldImg.getRequestedWidth() * 0.5;
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
    private                 List<Hit>          hits;
    private                 List<Hit>          hitsToRemove;
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
        scoreFont      = spaceBoy(30);
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

        // Adjust audio clip volumes
        explosionSound.setVolume(0.5);
        torpedoHitSound.setVolume(0.5);
        
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
        hits                  = new ArrayList<>();
        hitsToRemove          = new ArrayList<>();
        score                 = 0;
        destroy               = false;
        noOfLifes             = LIFES;
        noOfShields           = SHIELDS;
        lastShieldActivated   = 0;
        timer                 = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall) {
                    draw();
                    lastTimerCall = now + FPS_10;
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
                            lastShieldActivated = System.currentTimeMillis();
                            spaceShip.shield    = true;
                            playSound(deflectorShieldSound);
                        }
                        break;
                    case SPACE:
                        spawnTorpedo(spaceShip.x, spaceShip.y);
                        break;
                }
            } else if (e.getCode() == KeyCode.P && !gameOverScreen) {
                background.setImage(nebularImg);
                mediaPlayer.pause();
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
        hitsToRemove.clear();
        ctx.clearRect(0, 0, WIDTH, HEIGHT);

        ctx.setFill(Color.rgb(255, 255, 255, 0.9));

        // Draw Stars and Asteroids
        for (int i = 0 ; i < NO_OF_ASTEROIDS ; i++) {
            // Draw Stars
            Star star = stars[i];
            ctx.fillOval(star.x, star.y, star.size, star.size);
            star.x += star.vX;
            star.y += star.vY;

            // Respawn star
            if(star.y > HEIGHT + star.size) {
                stars[i].x = RND.nextDouble() * WIDTH;
                stars[i].y = -star.size;
            }

            // Draw Asteroids
            Asteroid asteroid = asteroids[i];

            ctx.save();
            ctx.translate(asteroid.x + asteroid.centerX,asteroid.y + asteroid.centerY);
            ctx.rotate(asteroid.rot);
            ctx.scale(asteroid.scale, asteroid.scale);
            ctx.translate(-asteroid.centerX, -asteroid.centerY);
            ctx.drawImage(asteroid.image, 0, 0);
            ctx.restore();

            asteroid.x += asteroid.vX;
            asteroid.y += asteroid.vY;

            if (asteroid.rotateRight) {
                asteroid.rot += asteroid.vR;
                if (asteroid.rot > 360) { asteroid.rot = 0; }
            } else {
                asteroid.rot -= asteroid.vR;
                if (asteroid.rot < 0) { asteroid.rot = 360; }
            }

            // Respawn Asteroid
            if(asteroid.x < 0 - asteroid.size || asteroid.x + asteroid.size > WIDTH + asteroid.size || asteroid.y > HEIGHT + asteroid.size) {
                asteroids[i] = spawnAsteroid();
            }

            // Check for torpedo hits
            for (Torpedo torpedo : torpedos) {
                if (isHitCircleCircle(torpedo.x, torpedo.y, torpedo.radius, asteroid.x + asteroid.centerX, asteroid.y + asteroid.centerY, asteroid.radius)) {
                    asteroid.hits--;
                    if (asteroid.hits == 0) {
                        explosions.add(new Explosion(asteroid.x + asteroid.centerX - Explosion.FRAME_CENTER * asteroid.scale, asteroid.y + asteroid.centerY - Explosion.FRAME_CENTER * asteroid.scale, asteroid.vX, asteroid.vY, asteroid.scale));
                        score += asteroid.value;
                        asteroids[i] = spawnAsteroid();
                        torpedosToRemove.add(torpedo);
                        playSound(explosionSound);
                    } else {
                        hits.add(new Hit(torpedo.x - Hit.FRAME_CENTER, torpedo.y - Hit.FRAME_HEIGHT, asteroid.vX, asteroid.vY));
                        torpedosToRemove.add(torpedo);
                        playSound(torpedoHitSound);
                    }
                }
            }

            // Check for space ship hit
            if (!destroy) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, asteroid.x + asteroid.centerX, asteroid.y + asteroid.centerY, asteroid.radius);
                } else {
                    hit = isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, asteroid.x + asteroid.centerX, asteroid.y + asteroid.centerY, asteroid.radius);
                }
                if (hit) {
                    spaceShipExplosion.countX = 0;
                    spaceShipExplosion.countY = 0;
                    spaceShipExplosion.x = spaceShip.x - SpaceShipExplosion.FRAME_WIDTH;
                    spaceShipExplosion.y = spaceShip.y - SpaceShipExplosion.FRAME_HEIGHT;
                    asteroids[i] = spawnAsteroid();
                    if (spaceShip.shield) {
                        playSound(explosionSound);
                        explosions.add(
                            new Explosion(asteroid.x + asteroid.centerX - Explosion.FRAME_CENTER * asteroid.scale, asteroid.y + asteroid.centerY - Explosion.FRAME_CENTER * asteroid.scale, asteroid.vX,
                                          asteroid.vY, asteroid.scale));
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
        }

        // Draw Enemies
        for (int i = 0 ; i < NO_OF_ENEMIES ; i++) {
            Enemy enemy = enemies[i];

            ctx.save();
            ctx.translate(enemy.x - enemy.radius, enemy.y - enemy.radius);
            ctx.save();
            ctx.translate(enemy.radius, enemy.radius);
            ctx.rotate(enemy.rot);
            ctx.translate(-enemy.radius, -enemy.radius);
            ctx.drawImage(enemy.image, 0, 0);
            ctx.restore();
            ctx.restore();

            // Fire if spaceship is below enemy
            if (enemy.x > spaceShip.x - 2 && enemy.x < spaceShip.x + 2) {
                if (enemy.y - enemy.lastShotY > 15) {
                    spawnEnemyTorpedo(enemy.x, enemy.y, enemy.vX, enemy.vY);
                    enemy.lastShotY = enemy.y;
                }
            }

            enemy.x += enemy.vX;
            enemy.y += enemy.vY;

            // Respawn Enemy
            if (enemy.x < -enemy.size || enemy.x > WIDTH + enemy.size || enemy.y > HEIGHT + enemy.size) {
                enemies[i] = spawnEnemy();
            }

            // Check for torpedo hits
            for (Torpedo torpedo : torpedos) {
                if (isHitCircleCircle(torpedo.x, torpedo.y, torpedo.radius, enemy.x, enemy.y, enemy.radius)) {
                    explosions.add(new Explosion(enemy.x - Explosion.FRAME_WIDTH * 0.25, enemy.y - Explosion.FRAME_HEIGHT * 0.25, enemy.vX, enemy.vY, 0.5));
                    score += enemy.value;
                    enemies[i] = spawnEnemy();
                    torpedosToRemove.add(torpedo);
                    playSound(spaceShipExplosionSound);
                }
            }

            // Check for space ship hit
            if (!destroy) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(spaceShip.x, spaceShip.y, deflectorShieldRadius, enemy.x, enemy.y, enemy.radius);
                } else {
                    hit = isHitCircleCircle(spaceShip.x, spaceShip.y, spaceShip.radius, enemy.x, enemy.y, enemy.radius);
                }
                if (hit) {
                    spaceShipExplosion.countX = 0;
                    spaceShipExplosion.countY = 0;
                    spaceShipExplosion.x = spaceShip.x - SpaceShipExplosion.FRAME_WIDTH;
                    spaceShipExplosion.y = spaceShip.y - SpaceShipExplosion.FRAME_HEIGHT;
                    enemies[i] = spawnEnemy();
                    playSound(spaceShipExplosionSound);
                    if (spaceShip.shield) {
                        explosions.add(new Explosion(enemy.x - Explosion.FRAME_WIDTH * 0.125, enemy.y - Explosion.FRAME_HEIGHT * 0.125, enemy.vX, enemy.vY, 0.5));
                    } else {
                        destroy = true;
                        noOfLifes--;
                        if (0 == noOfLifes) {
                            gameOver();
                        }
                    }
                }
            }
        }

        // Draw Torpedos
        for (Torpedo torpedo : torpedos) {
            torpedo.y -= torpedo.vY;
            if (torpedo.y < -torpedo.size) {
                torpedosToRemove.add(torpedo);
            }
            ctx.drawImage(torpedo.image, torpedo.x - torpedo.radius, torpedo.y - torpedo.radius);
        }
        torpedos.removeAll(torpedosToRemove);

        // Draw EnemyTorpedos
        for (EnemyTorpedo enemyTorpedo : enemyTorpedos) {
            enemyTorpedo.x += enemyTorpedo.vX;
            enemyTorpedo.y += enemyTorpedo.vY;
            if (!destroy) {
                boolean hit;
                if (spaceShip.shield) {
                    hit = isHitCircleCircle(enemyTorpedo.x, enemyTorpedo.y, enemyTorpedo.radius, spaceShip.x, spaceShip.y, deflectorShieldRadius);
                } else {
                    hit = isHitCircleCircle(enemyTorpedo.x, enemyTorpedo.y, enemyTorpedo.radius, spaceShip.x, spaceShip.y, spaceShip.radius);
                }
                if (hit) {
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
                }
            } else if (enemyTorpedo.y > HEIGHT) {
                enemyTorpedosToRemove.add(enemyTorpedo);
            }
            ctx.drawImage(enemyTorpedo.image, enemyTorpedo.x, enemyTorpedo.y);
        }
        enemyTorpedos.removeAll(enemyTorpedosToRemove);

        // Draw Explosions
        for (Explosion explosion : explosions) {
            ctx.drawImage(explosionImg, explosion.countX * Explosion.FRAME_WIDTH, explosion.countY * Explosion.FRAME_HEIGHT, Explosion.FRAME_WIDTH, Explosion.FRAME_HEIGHT, explosion.x, explosion.y, Explosion.FRAME_WIDTH * explosion.scale, Explosion.FRAME_HEIGHT * explosion.scale);
            explosion.x += explosion.vX;
            explosion.y += explosion.vY;
            explosion.countX++;
            if (explosion.countX == Explosion.MAX_FRAME_X) {
                explosion.countY++;
                if (explosion.countX == Explosion.MAX_FRAME_X && explosion.countY == Explosion.MAX_FRAME_Y) {
                    explosionsToRemove.add(explosion);
                }
                explosion.countX = 0;
                if (explosion.countY == 4) {
                    explosion.countY = 0;
                }
            }
        }
        explosions.removeAll(explosionsToRemove);

        // Draw Hits
        for (Hit hit : hits) {
            ctx.drawImage(hitImg, hit.countX * Hit.FRAME_WIDTH, hit.countY * Hit.FRAME_HEIGHT, Hit.FRAME_WIDTH, Hit.FRAME_HEIGHT, hit.x, hit.y, Hit.FRAME_WIDTH, Hit.FRAME_HEIGHT);
            hit.x += hit.vX;
            hit.y += hit.vY;
            hit.countX++;
            if (hit.countX == Hit.MAX_FRAME_X) {
                hit.countY++;
                if (hit.countX == Hit.MAX_FRAME_X && hit.countY == Hit.MAX_FRAME_Y) {
                    hitsToRemove.add(hit);
                }
                hit.countX = 0;
                if (hit.countY == Hit.MAX_FRAME_Y) {
                    hit.countY = 0;
                }
            }
        }
        hits.removeAll(hitsToRemove);

        // Draw Spaceship, score, lifes and shields
        if (noOfLifes > 0) {
            // Draw Spaceship or it's explosion
            if (destroy) {
                ctx.drawImage(spaceShipExplosionImg, spaceShipExplosion.countX * spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.countY * spaceShipExplosion.FRAME_HEIGHT,
                              spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.FRAME_HEIGHT, spaceShip.x - spaceShipExplosion.FRAME_CENTER, spaceShip.y - spaceShipExplosion.FRAME_CENTER,
                              spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.FRAME_HEIGHT);

                spaceShipExplosion.countX++;
                if (spaceShipExplosion.countX == SpaceShipExplosion.MAX_FRAME_X) {
                    spaceShipExplosion.countX = 0;
                    spaceShipExplosion.countY++;
                    if (spaceShipExplosion.countY == SpaceShipExplosion.MAX_FRAME_Y) {
                        spaceShipExplosion.countY = 0;
                    }
                    if (spaceShipExplosion.countX == 0 && spaceShipExplosion.countY == 0) {
                        destroy = false;
                        spaceShip.x = WIDTH / 2.0;
                        spaceShip.y = HEIGHT - 2 * spaceShip.size;
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
                ctx.drawImage((0 == spaceShip.dX && 0 == spaceShip.dY) ? spaceShip.image : spaceShip.imageThrust, spaceShip.x - spaceShip.radius, spaceShip.y - spaceShip.radius);

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
                        ctx.drawImage(deflectorShieldImg, spaceShip.x - deflectorShieldRadius, spaceShip.y - deflectorShieldRadius);
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

    private void spawnEnemyTorpedo(final double x, final double y, final double vX, final double vY) {
        double vFactor = ENEMY_TORPEDO_SPEED / vY; // make sure the speed is always the defined one
        enemyTorpedos.add(new EnemyTorpedo(enemyTorpedoImg, x, y, vFactor * vX, vFactor * vY));
        playSound(enemyLaserSound);
    }


    // Hit test
    private boolean isHitCircleCircle(final double c1X, final double c1Y, final double c1R, final double c2X, final double c2Y, final double c2R) {
        double distX    = c1X - c2X;
        double distY    = c1Y - c2Y;
        double distance = Math.sqrt((distX * distX) + (distY * distY));
        return (distance <= c1R+c2R);
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

        gameMediaPlayer.pause();

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
        private static final int     MAX_VALUE      = 10;
        private final        Random  rnd            = new Random();
        private final        double  xVariation     = 2;
        private final        double  minSpeedY      = 2;
        private final        double  minRotationR   = 0.1;
        private final        Image   image;
        private              double  x;
        private              double  y;
        private              double  width;
        private              double  height;
        private              double  size;
        private              double  centerX;
        private              double  centerY;
        private              double  radius;
        private              double  rot;
        private              double  vX;
        private              double  vY;
        private              double  vR;
        private              boolean rotateRight;
        private              double  scale;
        private              double  vYVariation;
        private              int     value;
        private              int     hits;


        // ******************** Constructor ***********************************
        public Asteroid(final Image image) {
            // Image
            this.image = image;

            // Position
            x   = rnd.nextDouble() * WIDTH;
            y   = -image.getHeight();
            rot = 0;

            // Random Size
            scale = (rnd.nextDouble() * 0.6) + 0.2;

            // No of hits (0.2 - 0.8)
            hits = (int) (scale * 5.0);

            // Value
            value = (int) (1 / scale * MAX_VALUE);

            // Random Speed
            vYVariation = (rnd.nextDouble() * 0.5) + 0.2;

            width   = image.getWidth() * scale;
            height  = image.getHeight() * scale;
            size    = width > height ? width : height;
            radius  = size * 0.5;
            centerX = image.getWidth() * 0.5;
            centerY = image.getHeight() * 0.5;

            // Velocity
            vX          = ((rnd.nextDouble() * xVariation) - xVariation * 0.5) * VELOCITY_FACTOR_X;
            vY          = (((rnd.nextDouble() * 1.5) + minSpeedY * 1/scale) * vYVariation) * VELOCITY_FACTOR_Y;
            vR          = (((rnd.nextDouble()) * 0.5) + minRotationR) * VELOCITY_FACTOR_R;
            rotateRight = rnd.nextBoolean();
        }
    }

    private class SpaceShip {
        private final Image       image;
        private final Image       imageThrust;
        private       double      x;
        private       double      y;
        private       double      size;
        private       double      radius;
        private       double      width;
        private       double      height;
        private       double      dX;
        private       double      dY;
        private       boolean     shield;


        public SpaceShip(final Image image, final Image imageThrust) {
            this.image       = image;
            this.imageThrust = imageThrust;
            this.x           = WIDTH / 2.0;
            this.y           = HEIGHT - 2 * image.getHeight();
            this.width       = image.getWidth();
            this.height      = image.getHeight();
            this.size        = width > height ? width : height;
            this.radius      = size * 0.5;
            this.dX          = 0;
            this.dY          = 0;
            this.shield      = false;
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
        private static final double FRAME_CENTER = 50;
        private static final int    MAX_FRAME_X  = 8;
        private static final int    MAX_FRAME_Y  = 6;
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
            this.x      = x;
            this.y      = y;
            this.vX     = vX;
            this.vY     = vY;
            this.countX = 0;
            this.countY = 0;
        }
    }

    private class Enemy {
        private static final int     MAX_VALUE  = 49;
        private final        Random  rnd        = new Random();
        private final        double  xVariation = 1;
        private final        double  minSpeedY  = 3;
        private final        Image   image;
        private              double  x;
        private              double  y;
        private              double  rot;
        private              double  width;
        private              double  height;
        private              double  size;
        private              double  radius;
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
            size   = width > height ? width : height;
            radius = size * 0.5;

            // Velocity
            vX = ((rnd.nextDouble() * xVariation) - xVariation * 0.5) * VELOCITY_FACTOR_X;
            vY = (((rnd.nextDouble() * 1.5) + minSpeedY) * vYVariation) * VELOCITY_FACTOR_Y;

            // Rotation
            rot = Math.toDegrees(Math.atan2(vY, vX)) - 90;

            // Related to laser fire
            lastShotY = 0;
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
    }
}