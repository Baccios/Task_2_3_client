package com.task2_3.client;

import javafx.beans.NamedArg;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.util.ArrayList;

public class AutoCompleteComboBox<T> extends ComboBox<T>{
    ObservableList<T> objectChoices = FXCollections.observableArrayList();
    private T selectedObject;
    private boolean selected;
    private int caretPos;

    public AutoCompleteComboBox() {
        this.selectedObject = null;
        this.selected = false;
        this.setItems(objectChoices);
        this.setEditable(true);

        /*this.valueProperty().addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observableValue, T t, T t1) {
                System.out.println(t1);
                selectedObject = null;
                selected = false;
                for(T tmp: objectChoices){
                    if(t1.equals(tmp)){
                        selected = true;
                        selectedObject = t1;
                    }
                }
                System.out.println(selected);
            }
        });*/

        this.setOnKeyPressed(t -> this.hide());
        this.getEditor().addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.UP) {
                    caretPos = -1;
                    moveCaret(getEditor().getText().length());
                    event.consume();
                    return;
                } else if(event.getCode() == KeyCode.DOWN) {
                    if(!isShowing()) {
                        show();
                    }
                    caretPos = -1;
                    moveCaret(getEditor().getText().length());
                    event.consume();
                    return;
                } /*else if(event.getCode() == KeyCode.LEFT){
                    getEditor().positionCaret(getEditor().getCaretPosition() - 1);
                    event.consume();
                } else if(event.getCode() == KeyCode.RIGHT){
                    if(getEditor().getCaretPosition() < getEditor().getText().length()) {
                        getEditor().positionCaret(getEditor().getCaretPosition() + 1);
                    }
                    event.consume();
                }*/

            }
        });
    }

    private void moveCaret(int textLength) {
        if(caretPos == -1) {
            this.getEditor().positionCaret(textLength);
        } else {
            this.getEditor().positionCaret(caretPos);
        }
    }

    public boolean isValid(){
        selectedObject = null;
        selected = false;
        for(T tmp: objectChoices){
            if(getEditor().getText().equals(tmp.toString())){
                selected = true;
                selectedObject = tmp;
            }
        }
        return selectedObject != null;
    }

    public T getSelectedObject() {
        if(selected || isValid()){
            return selectedObject;
        }
        else {
            System.out.println("Input is not a valid object");
            return null;
        }
    }
}
