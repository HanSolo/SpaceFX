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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

//import static com.gluonhq.attach.util.Platform.isDesktop;
import static eu.hansolo.spacefx.Config.HEIGHT;
import static eu.hansolo.spacefx.Config.WIDTH;


public class SpaceFX extends Application {
    private static final boolean     IS_BROWSER = WebAPI.isBrowser();
    private              SpaceFXView view;

    @Override public void init() {
        view = new SpaceFXView();
    }

    @Override public void start(Stage stage) {
        Scene scene = new Scene(view, WIDTH, HEIGHT);
        scene.getStylesheets().add(SpaceFX.class.getResource("spacefx.css").toExternalForm());

        // Setup key listener
        //if (isDesktop()) {
            scene.setOnKeyPressed(e -> {
                if (view.isRunning()) {
                    switch (e.getCode()) {
                        case UP:
                            view.decreaseSpaceShipVy();
                            break;
                        case RIGHT:
                            view.increaseSpaceShipVx();
                            break;
                        case DOWN:
                            view.increaseSpaceShipVy();
                            break;
                        case LEFT:
                            view.decreaseSpaceShipVx();
                            break;
                        case S:
                            view.spaceShipShield();
                            break;
                        case R:
                            view.spaceShipRocket();
                            break;
                        case SPACE:
                            view.spaceShipTorpedo();
                            break;
                    }
                } else if (e.getCode() == KeyCode.P) {
                    view.startGame();
                }
            });
            scene.setOnKeyReleased(e -> {
                if (view.isRunning()) {
                    switch (e.getCode()) {
                        case UP:
                            view.stopSpaceShipVy();
                            break;
                        case RIGHT:
                            view.stopSpaceShipVx();
                            break;
                        case DOWN:
                            view.stopSpaceShipVy();
                            break;
                        case LEFT:
                            view.stopSpaceShipVx();
                            break;
                    }
                }
            });
        //}  else {
        //    scene.setOnMousePressed(e -> {
        //        if (!view.isRunning()) {
        //            view.startGame();
        //        }
        //    });
        //}       

        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        if (!IS_BROWSER) {
            Platform.exit();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
