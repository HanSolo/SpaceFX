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
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 23.02.20
 * Time: 06:47
 */
public class SpaceFX extends Application {
    private static final boolean     IS_BROWSER = WebAPI.isBrowser();
    private static final Rectangle2D BOUNDS     = Screen.getPrimary().getVisualBounds();
    private              SpaceFXView view;

    @Override public void init() {
        view = new SpaceFXView();
    }

    @Override public void start(Stage stage) {
        Scene scene = new Scene(view, BOUNDS.getWidth(), BOUNDS.getHeight());
        scene.getStylesheets().add(SpaceFX.class.getResource("spacefx.css").toExternalForm());

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
