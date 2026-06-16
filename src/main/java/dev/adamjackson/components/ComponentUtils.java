package dev.adamjackson.components;

import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class ComponentUtils {
    public static FXMLLoader getLoader(Class<?> fxml) {
        return new FXMLLoader(fxml.getResource(fxml.getSimpleName()+".fxml"));
    }

    public static <T> Parent getParent(Class<T> fxml) {
        FXMLLoader loader = getLoader(fxml);
        Parent retVal;

        try {
            retVal = loader.load();
        }
        catch (IOException ex) {
            retVal = null;
        }

        return retVal;
    }

    public static void zeroAnchor(Parent node) {
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
    }

    public static String getErrorMsg(Object obj) {
        return obj.getClass().getSimpleName()+" was not found in the resources";
    }

    /**
     * Initializes a spinner and binds its double value to a specified property.
     * 
     * @param spinner The spinner to initialize
     * @param min min value of the spinner
     * @param max max value of the spinner
     * @param stepSize amount to increment/decrement by when clicking the arrows
     * @param bindValue the property to bind the value of the spinner to
     */
    public static void initSpinner(Spinner<Double> spinner, 
                                   double min, 
                                   double max, 
                                   double stepSize,
                                   ObjectProperty<Double> bindValue) 
    {
        DoubleSpinnerValueFactory factory = 
            new DoubleSpinnerValueFactory(min, max);
        factory.setAmountToStepBy(stepSize);
        factory.valueProperty().bindBidirectional(
            bindValue
        );

        spinner.setValueFactory(factory);
    }  

    /**
     * Initializes a spinner and binds its integer value to a specified 
     * property.
     * 
     * @param spinner The spinner to initialize
     * @param min min value of the spinner
     * @param max max value of the spinner
     * @param stepSize amount to increment/decrement by when clicking the arrows
     * @param bindValue the property to bind the value of the spinner to
     */
    public static void initSpinner(Spinner<Integer> spinner, 
                                   int min, 
                                   int max, 
                                   int stepSize,
                                   ObjectProperty<Integer> bindValue) 
    {
        IntegerSpinnerValueFactory factory = 
            new IntegerSpinnerValueFactory(min, max);
        factory.setAmountToStepBy(stepSize);
        factory.valueProperty().bindBidirectional(
            bindValue
        );

        spinner.setValueFactory(factory);
    }  

    /**
     * Initializes a spinner and binds its double value to a specified property. 
     * This differs from the method that accepts regular integers in the fact 
     * that it accepts properties as its min, max, and stepSize values. This 
     * means that if those properties' values change, it will take effect on the 
     * spinner immediately. This allows for configurable values.
     * 
     * @param spinner The spinner to initialize
     * @param min min value of the spinner
     * @param max max value of the spinner
     * @param stepSize amount to increment/decrement by when clicking the arrows
     * @param bindValue the property to bind the value of the spinner to
     */
    public static void initSpinner(Spinner<Double> spinner,
                                   DoubleProperty min,
                                   DoubleProperty max,
                                   DoubleProperty stepSize,
                                   ObjectProperty<Double> bindValue)
    {
        DoubleSpinnerValueFactory factory =
            new DoubleSpinnerValueFactory(min.get(), max.get());
        factory.minProperty().bind(min);
        factory.maxProperty().bind(max);
        factory.amountToStepByProperty().bind(stepSize);
        factory.valueProperty().bindBidirectional(
            bindValue
        );

        spinner.setValueFactory(factory);
    }

    /**
     * Initializes a spinner and binds its integer value to a specified 
     * property. This differs from the method that accepts regular integers in
     * the fact that it accepts properties as its min, max, and stepSize values.
     * This means that if those properties' values change, it will take effect
     * on the spinner immediately. This allows for configurable values.
     * 
     * @param spinner The spinner to initialize
     * @param min min value of the spinner
     * @param max max value of the spinner
     * @param stepSize amount to increment/decrement by when clicking the arrows
     * @param bindValue the property to bind the value of the spinner to
     */
    public static void initSpinner(Spinner<Integer> spinner,
                                   IntegerProperty min,
                                   IntegerProperty max,
                                   IntegerProperty stepSize,
                                   ObjectProperty<Integer> bindValue)
    {
        IntegerSpinnerValueFactory factory =
            new IntegerSpinnerValueFactory(min.get(), max.get());
        factory.minProperty().bind(min);
        factory.maxProperty().bind(max);
        factory.amountToStepByProperty().bind(stepSize);
        factory.valueProperty().bindBidirectional(
            bindValue
        );

        spinner.setValueFactory(factory);
    }

    
    public static void addPressAndHoldHandler(Node node, 
                                              Duration holdTime, 
                                              EventHandler<MouseEvent> handler) 
    {
        class Wrapper<T> { T content; }
        Wrapper<MouseEvent> eventWrapper = new Wrapper<>();

        PauseTransition holdTimer = new PauseTransition(holdTime);
        holdTimer.setOnFinished(event -> {
            handler.handle(eventWrapper.content);
            holdTimer.playFromStart();
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            eventWrapper.content = event;
            holdTimer.playFromStart();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());
        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }
}
