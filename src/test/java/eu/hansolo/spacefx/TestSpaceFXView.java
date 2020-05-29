package eu.hansolo.spacefx;

import de.sandec.jmemorybuddy.JMemoryBuddy;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class TestSpaceFXView {

    @BeforeClass
    public static void startFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            latch.countDown();
        });

    }

    public void workaround() {
        Stage stage = new Stage();
        Scene scene = new Scene(new StackPane());
        stage.setScene(scene);
        stage.show();
        stage.close();
    }

    @Test
    public void simpleLeaktest() throws InterruptedException {

        JMemoryBuddy.memoryTest(checker -> {
            CountDownLatch latch = new CountDownLatch(1);
            SpaceFXView[] view = new SpaceFXView[1];
            Platform.runLater(() -> {
                try {

                    Stage stage = new Stage();

                    view[0] = new SpaceFXView(stage);

                    Scene scene = new Scene(view[0]);
                    stage.setScene(scene);
                    stage.show();
                    Platform.runLater(() -> {
                        stage.close();
                        workaround();
                    });

                    checker.assertCollectable(view[0]);
                    checker.assertCollectable(stage);
                    checker.assertCollectable(scene);

                    latch.countDown();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                latch.await();
                while(!view[0].isReadyToStart()) {
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void simpleLeaktest2() throws InterruptedException {

        JMemoryBuddy.memoryTest(checker -> {
            CountDownLatch latch = new CountDownLatch(1);
            SpaceFXView[] view = new SpaceFXView[1];
            Platform.runLater(() -> {
                try {

                    Stage stage = new Stage();

                    view[0] = new SpaceFXView(stage);

                    Scene scene = new Scene(view[0]);
                    stage.setScene(scene);
                    stage.show();

                    checker.assertCollectable(view[0]);
                    checker.assertCollectable(stage);
                    checker.assertCollectable(scene);

                    latch.countDown();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                latch.await();
                while(!view[0].isReadyToStart()) {
                    Thread.sleep(10);
                }
                Platform.runLater(() -> {
                    view[0].startGame();
                    ((Stage) view[0].getScene().getWindow()).close();
                    workaround();
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
