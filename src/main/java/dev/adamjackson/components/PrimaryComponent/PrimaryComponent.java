package dev.adamjackson.components.PrimaryComponent;

import java.io.IOException;

import dev.adamjackson.App;
import dev.adamjackson.GapBuffer;
import dev.adamjackson.components.ComponentUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

public class PrimaryComponent {
    @FXML private VBox root;
    private VirtualLCD lcd;

    @FXML private void initialize() {

        this.lcd = new VirtualLCD();

        this.root.getChildren().add(lcd);

        App.buffer.isStale().addListener((obs, oldV, newV) -> {
            if (newV) {
                
                char[][] b = App.buffer.getVisibleFrame();
                lcd.setCursor(0,0);
                for (int i = 0; i < GapBuffer.ROW_COUNT; i++) {
                    for (int j = 0; j < GapBuffer.COL_COUNT; j++) {
                        char c = b[i][j];

                        switch (c) {
                            case '\n':
                            case '\r':
                            case '\0':
                                lcd.write(' ');
                                break;
                            default:
                                lcd.write(c);
                                break;
                        }
                    }
                }
            }

            int row = App.buffer.getFrameRow();
            int col = App.buffer.getFrameCol();
            lcd.setCursor(col,row);
            App.buffer.isStale().set(false);
        });
        
        if (App.buffer.totalChars() == 0) {
            lcd.setCursor(0,0); // set the initial cursor position
        }
        else {
            // toggle the stale property to trigger the listener and update
            // the virtual LCD with the current buffer contents and cursor position
            App.buffer.isStale().set(false); 
            App.buffer.isStale().set(true); 
        }
    }

    public static VBox getRoot() {
        try {
            FXMLLoader loader = ComponentUtils.getLoader(PrimaryComponent.class);
            VBox root = new VBox();
            loader.setRoot(root);
            loader.load();
            return root;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
