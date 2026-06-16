package dev.adamjackson.components.PrimaryComponent;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class VirtualLCD extends GridPane {
    
    private LCDCharacter[][] frame = new LCDCharacter[4][40];
    private int row = 0;
    private int col = 0;
    
    public VirtualLCD() {
        for (int i = 0; i < 40; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(-1);
            this.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < 4; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(-1);
            this.getRowConstraints().add(rowConst);
        }
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 40; j++) {
                frame[i][j] = new LCDCharacter(" ");
                this.add(frame[i][j], j, i);
            }
        }

        this.setHgap(3);
        this.setVgap(3);
    }

    public void write(char c) {
        frame[this.row][this.col].setUnderline(false);

        if (c == '\r') {
            this.row++;
            this.col = 0;
            if (this.row == 4) {
                this.row = 0;
            }
            return;
        }
        
        frame[this.row][this.col].setText(String.valueOf(c));

        this.col++;
        if (this.col == 40) {
            this.col = 0;
            this.row++;
            if (this.row == 4) {
                this.row = 0;
            }
        }
        
        frame[this.row][this.col].setUnderline(true);
    }

    public void draw()
    {
    }

    public void setCursor(int col, int row) {
        frame[this.row][this.col].setUnderline(false);
        this.row = row;
        this.col = col;
        frame[this.row][this.col].setUnderline(true);
    }
}
