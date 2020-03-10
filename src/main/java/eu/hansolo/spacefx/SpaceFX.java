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

//import com.jpro.webapi.WebAPI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import static com.gluonhq.attach.util.Platform.isDesktop;
import static eu.hansolo.spacefx.Config.HEIGHT;
import static eu.hansolo.spacefx.Config.WIDTH;


public class SpaceFX extends Application {
    //private static final boolean IS_BROWSER = WebAPI.isBrowser();
    private              boolean torpedoArmed;
    private              boolean rocketArmed;
    private              boolean shieldArmed;
    private              SpaceFXView view;

    @Override public void init() {
        torpedoArmed = true;
        rocketArmed  = true;
        shieldArmed  = true;
        view = new SpaceFXView();
    }

    @Override public void start(Stage stage) {
        Scene scene = new Scene(view, WIDTH, HEIGHT);
        scene.getStylesheets().add(SpaceFX.class.getResource(isDesktop() ? "spacefx.css" : "spacefx-mobile.css").toExternalForm());
        //scene.getStylesheets().add(SpaceFX.class.getResource("spacefx.css").toExternalForm());


        // Setup key listener
        if (isDesktop()) {
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
                            if (shieldArmed) {
                                view.activateSpaceShipShield();
                                shieldArmed = false;
                            }
                            break;
                        case R:
                            if (rocketArmed) {
                                view.fireSpaceShipRocket();
                                rocketArmed = false;
                            }
                            break;
                        case SPACE:
                            if (torpedoArmed) {
                                view.fireSpaceShipWeapon();
                                torpedoArmed = false;
                            }
                            break;
                    }
                } else if (view.isHallOfFameScreen()) {
                    switch (e.getCode()) {
                        case UP:
                            if (view.getDigit1().isSelected()) { view.getDigit1().up(); }
                            if (view.getDigit2().isSelected()) { view.getDigit2().up(); }
                            break;
                        case RIGHT:
                            if (view.getDigit1().isSelected()) {
                                view.getDigit2().setSelected(true);
                            }
                            break;
                        case DOWN:
                            if (view.getDigit1().isSelected()) { view.getDigit1().down(); }
                            if (view.getDigit2().isSelected()) { view.getDigit2().down(); }
                            break;
                        case LEFT:
                            if (view.getDigit2().isSelected()) {
                                view.getDigit1().setSelected(true);
                            }
                            break;
                        case SPACE:
                            view.storePlayer();
                            break;
                    }
                } else if (e.getCode() == KeyCode.P && view.isReadyToStart()) {
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
                        case S:
                            shieldArmed = true;
                            break;
                        case R:
                            rocketArmed = true;
                            break;
                        case SPACE:
                            torpedoArmed = true;
                            break;
                    }
                }
            });
        } else {
            scene.setOnMousePressed(e -> {
                if (!view.isRunning() && view.isReadyToStart()) {
                    view.startGame();
                }
            });
        }

        stage.setMaximized(!isDesktop());
        stage.setScene(scene);
        if (isDesktop()) { stage.setResizable(false); }
        // stage.setResizable(false);
        stage.show();
    }

    @Override public void stop() {
        //if (!IS_BROWSER) {
        Platform.exit();
        System.exit(0);
        //}
    }

    public static void main(String[] args) {
        launch(args);
    }
}
