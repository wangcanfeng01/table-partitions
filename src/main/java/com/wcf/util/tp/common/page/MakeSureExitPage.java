package com.wcf.util.tp.common.page;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author wangcanfeng
 * @time 2019/5/17
 * @function
 **/
public class MakeSureExitPage {
    /**
     * 按钮的高度
     */
    public static final int BUTTON_HEIGHT=25;
    /**
     * 按钮的长度
     */
    public static final int BUTTON_WIDTH=80;

    /**
     * 盒子中各个空间的间距
     */
    public static final int BOX_INTERVAL=5;
    private Stage stage;

    /**
     * 构造器，初始化各个参数
     * @param wEvent
     * @param main
     */
    public MakeSureExitPage(WindowEvent wEvent, Stage main) {
        this.stage = new Stage();
        BorderPane borderPane = new BorderPane();
        stage.setTitle("警告");
        HBox text = new HBox(BOX_INTERVAL);
        text.setPadding(new Insets(25,0,0,70));
        Label label = new Label("是否退出分表工具");
        text.getChildren(). addAll(label);
        HBox hBox = new HBox(BOX_INTERVAL);
        hBox.setPadding(new Insets(25,0,0,100));
        //注入两个按钮，分别是确认退出和取消退出
        Button btn1 = new Button("退出");
        btn1.setPrefHeight(BUTTON_HEIGHT);
        btn1.setPrefWidth(BUTTON_WIDTH);
        btn1.setOnAction(event -> {
            main.close();
            stage.close();
        });

        Button btn2 = new Button("取消");
        btn2.setPrefHeight(BUTTON_HEIGHT);
        btn2.setPrefWidth(BUTTON_WIDTH);
        btn2.setOnAction(event -> stage.close());
        hBox.getChildren().addAll(btn1, btn2);
        borderPane.setTop(text);
        borderPane.setCenter(hBox);
        stage.setScene(new Scene(borderPane, 300,100));
    }

    /**
     * 展示页面
     */
    public void show() {
        stage.show();
    }
}
