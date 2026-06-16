package dev.adamjackson.components.PrimaryComponent;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LCDCharacter extends Label {
    public LCDCharacter() {
        this(" ");
    }

    public LCDCharacter(String str) {
        super(str);
        this.setFont(Font.font("Monospace", FontWeight.BOLD, 20));

        this.setPrefWidth(10);
        this.setPrefHeight(25);
    }
}
