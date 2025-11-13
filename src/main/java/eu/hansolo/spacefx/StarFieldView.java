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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class StarFieldView extends Application {
    private StarField starField;

    @Override public void init() {
        this.starField = new StarField();
    }

    @Override public void start(final Stage stage) {
        StackPane pane  = new StackPane(starField);
        Scene     scene = new Scene(pane);

        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();

        starField.start();
    }

    @Override public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
