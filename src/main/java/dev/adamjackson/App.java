package dev.adamjackson;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import dev.adamjackson.components.PrimaryComponent.PrimaryComponent;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Scene scene;
    public static Stage stage;
    private static BooleanProperty isRunning = new SimpleBooleanProperty(false);
    public static final GapBuffer buffer = new GapBuffer(16);
    
    @Override
    public void start(Stage stage) throws IOException {
        App.isRunning.set(true);
        Scene primary = new Scene(PrimaryComponent.getRoot());
        primary.setOnKeyPressed((ev) -> {
            boolean consumed = true;
            switch (ev.getCode()) {
                case LEFT:
                    if (ev.isControlDown()) {
                        buffer.moveWordLeft();
                    }
                    else {
                        buffer.moveLeft();
                    }
                    break;

                case RIGHT:
                    if (ev.isControlDown()) {
                        buffer.moveWordRight();
                    }                        
                    else {
                        buffer.moveRight();
                    }
                    break;

                case UP:
                    if (ev.isControlDown()) {
                        buffer.moveToFrameStart();
                    }
                    else {
                        buffer.moveUpOneLine();
                    }
                    break;

                case DOWN:
                    if (ev.isControlDown()) {
                        buffer.moveToFrameEnd();
                    }
                    else {
                        buffer.moveDownOneLine();
                    }
                    break;

                case BACK_SPACE:
                    if (ev.isControlDown()) {
                        buffer.backSpaceWord();
                    }
                    else {
                        buffer.backSpace();
                    }
                    break;
                    
                case DELETE:
                    if (ev.isControlDown()) {
                        buffer.deleteWord();
                    }
                    else {
                        buffer.deleteChar();
                    }
                    break;

                case HOME:
                    if (ev.isControlDown()) {
                        buffer.moveToStart();
                    }
                    else {
                        buffer.moveToLineStart();
                    }
                    break;

                case END:
                    if (ev.isControlDown()) {
                        buffer.moveToEnd();
                    }
                    else {
                        buffer.moveToLineEnd();
                    }
                    break;

                case ENTER:
                    buffer.insert('\n');
                    break;

                case SPACE:
                    buffer.insert(' ');
                    break;

                // these are just here so we don't try to insert them as characters
                case SHIFT:
                case CONTROL:
                    break;
                default:
                    consumed = false;
                    break;
            }

            if (!consumed) {
                if (ev.isShiftDown()) {
                    buffer.insert(ev.getCode().getChar().charAt(0));
                }
                else {
                    buffer.insert(ev.getCode().getChar().toLowerCase().charAt(0));
                }
            }
            
            App.buffer.refreshFrameBuffer();
        });

        stage.setScene(primary);
        stage.show();
        stage.setHeight(200);
        stage.setOnCloseRequest((evt) -> {
            isRunning.set(false);
            Platform.exit();
            System.exit(0);
        });
        App.stage = stage;
        App.isRunning.set(true);

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static boolean isRunning() {
        synchronized(isRunning) {
            return isRunning.get();
        }
    }

    public static BooleanProperty isRunningProperty() {
        return isRunning;
    }

    private static void loadTestDoc()
    {
        try {
            Path path = Paths.get("test-doc.txt");
            byte[] data = Files.readAllBytes(path);
            for (byte b : data) {
                buffer.insert((char)b);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equals("--help") || arg.equals("-h")) {
                System.out.println("Usage: java -jar MyApp.jar [options]");
                System.out.println("Options:");
                System.out.println("  --help, -h       Show this help message and exit");
                System.out.println("  --load-test      Load test data from test-doc.txt into the buffer");
                System.exit(0);
            }
            else if (arg.equals("--load-test")) {
                // we'll handle this after the loop
                loadTestDoc();
            }
        }

        launch();
    }
}