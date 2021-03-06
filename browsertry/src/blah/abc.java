package blah;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.*;
import javafx.stage.Stage;

/** Reports load times for pages loaded in a WebView */
public class abc extends Application {

    public static void main(String[] args) { launch(args); }

    @Override public void start(final Stage stage) {
        final WebView webview  = new WebView();

        VBox layout = new VBox();
        layout.getChildren().setAll(
                createProgressReport(webview.getEngine()),
                webview
        );

        stage.setScene(new Scene(layout));
        stage.show();

        webview.getEngine().load("http://www.fxexperience.com");
    }

    /** @return a HBox containing a ProgressBar bound to engine load progress and a Label showing load times */
    private HBox createProgressReport(WebEngine engine) {
        final LongProperty startTime   = new SimpleLongProperty();
        final LongProperty endTime     = new SimpleLongProperty();
        final LongProperty elapsedTime = new SimpleLongProperty();

        final ProgressBar loadProgress = new ProgressBar();
        loadProgress.progressProperty().bind(engine.getLoadWorker().progressProperty());

        final Label loadTimeLabel = new Label();
        loadTimeLabel.textProperty().bind(
                Bindings.when(
                        elapsedTime.greaterThan(0))
                        .then(
                                Bindings.concat("Loaded page in ", elapsedTime.divide(1_000_000), "ms")
                        )
                        .otherwise(
                                "Loading..."
                        )
        );

        elapsedTime.bind(Bindings.subtract(endTime, startTime));

        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State state) {
                switch (state) {
                    case RUNNING:
                        startTime.set(System.nanoTime());
                        break;

                    case SUCCEEDED:
                        endTime.set(System.nanoTime());
                        break;
                }
            }
        });

        HBox progressReport = new HBox(10);
        progressReport.getChildren().setAll(
                loadProgress,
                loadTimeLabel
        );
        progressReport.setPadding(new Insets(5));
        progressReport.setAlignment(Pos.CENTER_LEFT);

        return progressReport;
    }
}