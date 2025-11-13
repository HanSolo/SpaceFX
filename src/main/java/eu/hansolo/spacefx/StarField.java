 /*
  * Copyright (c) 2025 by Gerrit Grunwald
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
 import javafx.beans.DefaultProperty;
 import javafx.geometry.Insets;
 import javafx.scene.canvas.Canvas;
 import javafx.scene.canvas.GraphicsContext;
 import javafx.scene.layout.Background;
 import javafx.scene.layout.BackgroundFill;
 import javafx.scene.layout.CornerRadii;
 import javafx.scene.layout.Pane;
 import javafx.scene.layout.Region;
 import javafx.scene.paint.Color;

 import java.util.Arrays;
 import java.util.Random;


 @DefaultProperty("children")
 public class StarField extends Region {
     private static final double          WIDTH       = 1920;
     private static final double          HEIGHT      = 1080;
     private static final Random          RND         = new Random();
     public static final  int             NO_OF_STARS = 200;
     public static final  long            FPS_60      = 0_016_666_666l;
     private              Canvas          canvas;
     private              GraphicsContext ctx;
     private              Pane            pane;
     private              Star[]          stars       = new Star[NO_OF_STARS];
     private              long            deltaTime   = FPS_60;
     private              long            lastTimerCall;
     private              AnimationTimer  timer;


     // ******************** Constructors **************************************
     public StarField() {
         for (int i = 0; i < NO_OF_STARS; i++) {
             Star star = new Star();
             star.x   = RND.nextDouble() * WIDTH;
             stars[i] = star;
         }

         timer = new AnimationTimer() {
             @Override public void handle(final long now) {
                 if (now > lastTimerCall) {
                     lastTimerCall = now + deltaTime;
                     redraw();
                 }
             }
         };

         initGraphics();
         registerListeners();
     }


     // ******************** Initialization ************************************
     private void initGraphics() {
         canvas = new Canvas(WIDTH, HEIGHT);
         ctx    = canvas.getGraphicsContext2D();
         pane   = new Pane(canvas);
         pane.setPrefSize(WIDTH, HEIGHT);
         pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

         getChildren().setAll(pane);

         setPrefSize(WIDTH, HEIGHT);
     }

     private void registerListeners() {
         //widthProperty().addListener(o -> resize());
         //heightProperty().addListener(o -> resize());
     }


     // ******************** Methods *******************************************
     public void start() { this.timer.start(); }
     public void stop() { this.timer.stop(); }

     private void redraw() {
         ctx.clearRect(0, 0, WIDTH, HEIGHT);
         ctx.setFill(Color.rgb(255, 255, 255, 0.9));
         Arrays.stream(stars).forEach(star -> {
             star.update();
             ctx.fillOval(star.x, star.y, star.size, star.size);
         });
     }


     // ******************** Inner Classes ************************************
     private class Star {
         private final Random rnd        = new Random();
         private final double yVariation = 0;
         private final double minSpeedX  = 4;
         private       double x;
         private       double y;
         private       double size;
         private       double vX;
         private       double vY;
         private       double vXVariation;


         public Star() {
             // Random size
             size = rnd.nextInt(2) + 1;

             // Position
             x = -size;
             y = (int)(rnd.nextDouble() * HEIGHT);

             // Random Speed
             vXVariation = (rnd.nextDouble() * 0.5) + 0.2;

             // Velocity
             vX = (int) (Math.round(((rnd.nextDouble() * 1.5) + minSpeedX) * vXVariation));
             vY = (int) (Math.round((rnd.nextDouble() * yVariation) - yVariation * 0.5));
         }


         private void respawn() {
             x = -size;
             y = (int) (RND.nextDouble() * HEIGHT);
         }

         private void update() {
             x += vX;
             y += vY;

             // Respawn star
             if (x > WIDTH + size) {
                 respawn();
             }
         }
     }
 }