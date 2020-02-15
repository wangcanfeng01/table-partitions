package com.wcf.util.tp.common.page;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TipsPage {

    /**
     * 默认的提示框
     *
     * @param tip 提示信息
     */
    public TipsPage(String tip, Node pre) {
        Stage stage = new Stage();
        BorderPane borderPane = new BorderPane();
        stage.setTitle("提示");
        HBox text = new HBox(5);
        text.setPadding(new Insets(25, 0, 0, 70));
        Label label = new Label(tip);
        text.getChildren().addAll(label);
        HBox hBox = new HBox(5);
        hBox.setPadding(new Insets(25, 0, 0, 100));
        //注入两个按钮，分别是确认退出和取消退出
        Button btn1 = new Button("知道了");
        btn1.setPrefHeight(25);
        btn1.setPrefWidth(80);
        btn1.setOnAction(event -> {
            if (pre != null) {
                pre.setVisible(false);
            }
            stage.close();
        });
        hBox.getChildren().add(btn1);
        borderPane.setTop(text);
        borderPane.setCenter(hBox);
        stage.setScene(new Scene(borderPane, 300, 100));
        stage.setAlwaysOnTop(true);
        // 提示信息页面直接展示就行了，尽量使得代码精炼
        stage.show();
    }
}
