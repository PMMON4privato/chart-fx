package de.gsi.acc.ui.samples;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gsi.chart.utils.PeriodicScreenCapture;

/**
 * @author rstein
 */
public class RunUiSamples extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunUiSamples.class);
    private static final int DEFAULT_DELAY = 2;
    private static final int DEFAULT_PERIOD = 5;
    private final String userHome = System.getProperty("user.home");
    private final Path path = Paths.get(userHome + "/ChartMathSamples");

    Stage stage = new Stage();
    CheckBox makeScreenShot = new CheckBox("make screenshot to home directory");

    @Override
    public void start(final Stage primaryStage) {
        final BorderPane root = new BorderPane();

        final FlowPane buttons = new FlowPane();
        buttons.setAlignment(Pos.CENTER_LEFT);
        root.setCenter(buttons);
        root.setBottom(makeScreenShot);

        buttons.getChildren().add(new MyButton("AcqButtonTests", new AcqButtonTests()));

        final Scene scene = new Scene(root);

        primaryStage.setTitle(this.getClass().getSimpleName());
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(evt -> System.exit(0));
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        Application.launch(args);
    }

    protected class MyButton extends Button {
        public MyButton(final String buttonText, final Application run) {
            super(buttonText);
            setOnAction(e -> {
                try {
                    run.start(stage);
                    stage.getScene().getRoot().layout();
                    stage.show();

                    if (makeScreenShot.isSelected()) {
                        new Thread(() -> {
                            try {
                                Thread.sleep(2000);
                                Platform.runLater(() -> {
                                    LOGGER.atInfo()
                                            .log("make screen shot to file of " + run.getClass().getSimpleName());
                                    final PeriodicScreenCapture screenCapture = new PeriodicScreenCapture(path,
                                            run.getClass().getSimpleName(), stage.getScene(), DEFAULT_DELAY,
                                            DEFAULT_PERIOD, false);
                                    screenCapture.performScreenCapture();
                                });
                            } catch (final InterruptedException e12) {
                                if (LOGGER.isErrorEnabled()) {
                                    LOGGER.atError().setCause(e12).log("InterruptedException");
                                }
                            }
                        }).start();
                    }
                } catch (final Exception e1) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.atError().setCause(e1).log("InterruptedException");
                    }
                }
            });
        }
    }
}
