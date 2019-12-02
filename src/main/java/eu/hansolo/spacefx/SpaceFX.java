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

import javafx.animation.AnimationTimer;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SpaceFX extends Application {
    private static final Random             RND                     = new Random();
    private static final double             WIDTH                   = 700;
    private static final double             HEIGHT                  = 700;
    private static final int                NO_OF_STARS             = 20;
    private static final int                NO_OF_ASTEROIDS         = 20;
    private static final double             VELOCITY_FACTOR_X       = 1.0;
    private static final double             VELOCITY_FACTOR_Y       = 1.0;
    private static final double             VELOCITY_FACTOR_R       = 1.0;
    private static final String             SPACE_BOY;
    private static       String             spaceBoyName;
    private final        Image              nebular                 = new Image(SpaceFX.class.getResourceAsStream("nebular.jpg"));
    private final        Image[]            asteroidImages          = { new Image(getClass().getResourceAsStream("asteroid1.png"), 150, 150, true, false),
                                                                        new Image(getClass().getResourceAsStream("asteroid2.png"), 150, 150, true, false),
                                                                        new Image(getClass().getResourceAsStream("asteroid3.png"), 150, 150, true, false),
                                                                        new Image(getClass().getResourceAsStream("asteroid4.png"), 150, 150, true, false),
                                                                        new Image(getClass().getResourceAsStream("asteroid5.png"), 150, 150, true, false),
                                                                        new Image(getClass().getResourceAsStream("asteroid6.png"), 150, 150, true, false),
                                                                        new Image(getClass().getResourceAsStream("asteroid7.png"), 150, 150, true, false),
                                                                        new Image(getClass().getResourceAsStream("asteroid8.png"), 150, 150, true, false),
                                                                        new Image(getClass().getResourceAsStream("asteroid9.png"), 150, 150, true, false),
                                                                        new Image(getClass().getResourceAsStream("asteroid10.png"), 150, 150, true, false),
                                                                        new Image(getClass().getResourceAsStream("asteroid11.png"), 150, 150, true, false),
                                                                        };
    private final        Image              spaceshipImg            = new Image(getClass().getResourceAsStream("fighter.png"), 48, 48, true, false);
    private final        Image              torpedoImg              = new Image(getClass().getResourceAsStream("torpedo.png"), 17, 20, true, false);
    private final        Image              explosionImg            = new Image(getClass().getResourceAsStream("explosion.png"), 960, 768, true, false);
    private final        Image              spaceShipExplosionImg   = new Image(getClass().getResourceAsStream("spaceshipexplosion.png"), 800, 600, true, false);
    private final        AudioClip          laserSound              = new AudioClip(getClass().getResource("laserSound.wav").toExternalForm());
    private final        AudioClip          explosionSound          = new AudioClip(getClass().getResource("explosionSound.wav").toExternalForm());
    private final        AudioClip          spaceShipExplosionSound = new AudioClip(getClass().getResource("spaceShipExplosionSound.wav").toExternalForm());
    private final        Media              soundTheme              = new Media(getClass().getResource("IntergalacticOdyssey.mp3").toExternalForm());
    private final        MediaPlayer        mediaPlayer             = new MediaPlayer(soundTheme);
    private              ImageView          background;
    private              Canvas             canvas;
    private              GraphicsContext    ctx;
    private              Star[]             stars;
    private              Asteroid[]         asteroids;
    private              SpaceShip          spaceShip;
    private              SpaceShipExplosion spaceShipExplosion;
    private              List<Torpedo>      torpedos;
    private              List<Torpedo>      torpedosToRemove;
    private              List<Explosion>    explosions;
    private              List<Explosion>    explosionsToRemove;
    private              long               score;
    private              double             scoreX;
    private              double             scoreY;
    private              boolean            destroy;
    private              AnimationTimer     timer;

    static {
        try {
            spaceBoyName = Font.loadFont(SpaceFX.class.getResourceAsStream("spaceboy.ttf"), 10).getName();
        } catch (Exception exception) { }
        SPACE_BOY = spaceBoyName;
    }


    // ******************** Methods *******************************************
    @Override public void init() {
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.1);
        background         = new ImageView(nebular);
        canvas             = new Canvas(WIDTH, HEIGHT);
        ctx                = canvas.getGraphicsContext2D();
        stars              = new Star[NO_OF_STARS];
        asteroids          = new Asteroid[NO_OF_ASTEROIDS];
        spaceShip          = new SpaceShip(spaceshipImg);
        spaceShipExplosion = new SpaceShipExplosion(0, 0);
        torpedos           = new ArrayList<>();
        torpedosToRemove   = new ArrayList<>();
        explosions         = new ArrayList<>();
        explosionsToRemove = new ArrayList<>();
        score              = 0;
        destroy            = false;
        timer              = new AnimationTimer() {
            @Override public void handle(final long NOW) {
                draw();
            }
        };
        for (int i = 0 ; i < NO_OF_STARS ; i++) {
            stars[i] = spawnStar();
        }
        for (int i = 0; i < NO_OF_ASTEROIDS; i++) {
            asteroids[i] = spawnAsteroid();
        }

        scoreX = WIDTH * 0.5;
        scoreY = 30;

        // Prepare GraphicsContext
        ctx.setFont(spaceBoy(30));
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);
    }

    @Override public void start(final Stage stage) {
        StackPane pane = new StackPane(background, canvas);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);
        scene.setOnKeyPressed(e -> {
            switch(e.getCode()) {
                case UP   : spaceShip.dY = -5; break;
                case RIGHT: spaceShip.dX = 5; break;
                case DOWN : spaceShip.dY = 5; break;
                case LEFT : spaceShip.dX = -5; break;
                case SPACE: spawnTorpedo(spaceShip.x + spaceShip.width / 2, spaceShip.y);
            }
        });
        scene.setOnKeyReleased( e -> {
            switch(e.getCode()) {
                case UP   : spaceShip.dY = 0; break;
                case RIGHT: spaceShip.dX = 0; break;
                case DOWN : spaceShip.dY = 0; break;
                case LEFT : spaceShip.dX = 0; break;
            }
        });

        stage.setTitle("Asteroids");
        stage.setScene(scene);
        stage.show();

        mediaPlayer.play();

        timer.start();
    }

    @Override public void stop() {
        Platform.exit();
        System.exit(0);
    }

    private void draw() {
        torpedosToRemove.clear();
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
            ctx.translate(p.image.getWidth() * (-0.5), p.image.getHeight() * (-0.5));

            ctx.save();
            ctx.translate(p.image.getWidth() * 0.5, p.image.getHeight() * 0.5);
            ctx.rotate(p.r);
            ctx.translate(-p.image.getWidth() * 0.5, -p.image.getHeight() * 0.5);
            ctx.drawImage(p.image, 0, 0);
            ctx.restore();
            ctx.restore();

            p.x += p.vX;
            p.y += p.vY;

            if (p.rotateRight) {
                p.r += p.vR;
                if (p.r > 360) { p.r = 0; }
            } else {
                p.r -= p.vR;
                if (p.r < 0) { p.r = 360; }
            }

            // Respawn Asteroid
            if(p.y > HEIGHT + p.image.getHeight()) {
                //particles[i].x = RND.nextDouble() * WIDTH;
                //particles[i].y = -p.image.getHeight();
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
                destroy                   = true;
                score                     = 0;
                spaceShipExplosion.countX = 0;
                spaceShipExplosion.countY = 0;
                spaceShipExplosion.x      = spaceShip.x - SpaceShipExplosion.FRAME_WIDTH;
                spaceShipExplosion.y      = spaceShip.y - SpaceShipExplosion.FRAME_HEIGHT;
                asteroids[i] = spawnAsteroid();
                playSound(spaceShipExplosionSound);
            }
        }

        // Draw Torpedos
        for (Torpedo torpedo : torpedos) {
            torpedo.y -= torpedo.dY;
            if (torpedo.y < -torpedo.image.getHeight()) {
                torpedosToRemove.add(torpedo);
            }
            ctx.drawImage(torpedo.image, torpedo.x, torpedo.y);
        }
        torpedos.removeAll(torpedosToRemove);

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

        if (destroy) {
            ctx.drawImage(spaceShipExplosionImg, spaceShipExplosion.countX * spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.countY * spaceShipExplosion.FRAME_HEIGHT, spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.FRAME_HEIGHT, spaceShip.x - spaceShip.width * 0.5, spaceShip.y - spaceShip.height * 0.5, spaceShipExplosion.FRAME_WIDTH, spaceShipExplosion.FRAME_HEIGHT);

            spaceShipExplosion.countX++;
            if (spaceShipExplosion.countX == 8) {
                spaceShipExplosion.countX = 0;
                spaceShipExplosion.countY++;
                if (spaceShipExplosion.countY == 6) {
                    spaceShipExplosion.countY = 0;
                }
                if (spaceShipExplosion.countX == 0 && spaceShipExplosion.countY == 0) {
                    destroy = false;
                }
            }
        } else {
            // Draw space ship
            spaceShip.x += spaceShip.dX;
            spaceShip.y += spaceShip.dY;

            if (spaceShip.x + spaceShip.image.getWidth() > WIDTH) {
                spaceShip.x = WIDTH - spaceShip.image.getWidth();
            }
            if (spaceShip.x < 0) {
                spaceShip.x = 0;
            }
            if (spaceShip.y + spaceShip.image.getHeight() > HEIGHT) {
                spaceShip.y = HEIGHT - spaceShip.image.getHeight();
            }
            if (spaceShip.y < 0) {
                spaceShip.y = 0;
            }
            ctx.drawImage(spaceShip.image, spaceShip.x, spaceShip.y);
        }

        // Draw score
        ctx.setFill(Color.rgb(51, 210, 206));
        ctx.fillText(Long.toString(score), scoreX, scoreY);
    }

    private Star spawnStar() {
        return new Star();
    }

    private Asteroid spawnAsteroid() {
        return new Asteroid(asteroidImages[RND.nextInt(asteroidImages.length - 1)]);
    }

    private void spawnTorpedo(final double x, final double y) {
        torpedos.add(new Torpedo(torpedoImg, x, y));
        playSound(laserSound);
    }

    private void playSound(final AudioClip audioClip) {
        new Thread(() -> audioClip.play()).run();
    }

    private static Font spaceBoy(final double SIZE) { return new Font(SPACE_BOY, SIZE); }


    // ******************** Misc **********************************************
    public static void main(String[] args) {
        launch(args);
    }


    // ******************** InnerClasses **************************************
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
        private              double  r;
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
            r = 0;

            // Random Size
            scale = (rnd.nextDouble() * 0.6) + 0.1;

            // Value
            value = (int) (1 / scale * MAX_VALUE);

            // Random Speed
            vYVariation = (rnd.nextDouble() * 0.5) + 0.2;

            width  = image.getWidth() * scale;
            height = image.getHeight() * scale;

            // Velocity
            vX          = ((rnd.nextDouble() * xVariation) - xVariation * 0.5) * VELOCITY_FACTOR_X;
            vY          = (((rnd.nextDouble() * 1.5) + minSpeedY * 1/scale) * vYVariation) * VELOCITY_FACTOR_Y;
            vR          = (((rnd.nextDouble()) * 0.5) + minRotationR) * VELOCITY_FACTOR_R;
            rotateRight = rnd.nextBoolean();
        }

        public Rectangle2D getBounds() {
            return new Rectangle2D(x - width * 0.5, y - height * 0.5, width, height);
        }

        public boolean intersects(final Asteroid particle) {
            return intersects(particle.getBounds());
        }
        public boolean intersects(final Rectangle2D bounds) {
            return this.getBounds().intersects(bounds);
        }
    }

    private class SpaceShip {
        private final Image       image;
        private       double      x;
        private       double      y;
        private       double      width;
        private       double      height;
        private       double      dX;
        private       double      dY;


        public SpaceShip(final Image image) {
            this.image     = image;
            this.x         = WIDTH / 2.0 - image.getWidth() / 2.0;
            this.y         = HEIGHT - 2 * image.getHeight();
            this.width     = image.getWidth();
            this.height    = image.getHeight();
            this.dX        = 0;
            this.dY        = 0;
        }

        public Rectangle2D getBounds() {
            return new Rectangle2D(x, y, width, height);
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
}