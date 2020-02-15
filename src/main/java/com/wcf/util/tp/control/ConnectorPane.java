package com.wcf.util.tp.control;

import com.wcf.util.tp.common.page.TipsPage;
import com.wcf.util.tp.common.utils.ContainerUtils;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ConnectorPane extends Pane {

    /**
     * 存放历史地址信息
     */
    private final ComboBox<String> addressHistory;
    /**
     * 选中的地址
     */
    private String chooseAddress;
    /**
     * 选中的序号
     */
    private int chooseIndex;
    /**
     * 当前配置数量
     */
    private int currentPropertiesNum = 0;
    /**
     * 连接类型
     */
    private final ComboBox<String> connectType;
    /**
     * 用户名
     */
    private final TextField username;
    /**
     * 密码
     */
    private final TextField password;
    /**
     * 连接按钮
     */
    private final Button connectButton;
    /**
     * 是否需要输入用户信息
     */
    private final CheckBox authCheck;
    /**
     * 在文件中的配置信息
     */
    private OrderedProperties properties;

    private boolean removing = false;

    private final ExtendFunction extendFunction;

    private final File propertiesFile;

    /**
     * 客户段类型
     */
    private final ClientType clientType;

    {
        // 静态域里面设置好控件的位置和类型信息
        addressHistory = new ComboBox<>();
        connectType = new ComboBox<>();
        connectType.getItems().add("tcp");
        connectType.getItems().add("ssl");
        connectType.setPromptText("tcp");
        username = new TextField();
        username.setPromptText("请输入用户名");
        password = new PasswordField();
        password.setPromptText("请输入密码");
        connectButton = new Button("连 接");
        connectButton.setId("connectButton");
        authCheck = new CheckBox("用户名和密码");
        authCheck.setSelected(true);
    }

    /**
     * 界面打开初始化
     *
     * @param propertiesFile
     * @param type
     */
    public ConnectorPane(File propertiesFile, ClientType type, ExtendFunction function) {
        controlsAction();
        controlsPosition();
        controlStyle();
        this.extendFunction = function;
        this.clientType = type;
        this.propertiesFile = propertiesFile;
        try (FileInputStream fi = new FileInputStream(propertiesFile);
             BufferedReader bf = new BufferedReader(new InputStreamReader(fi, StandardCharsets.UTF_8))) {
            properties = new OrderedProperties();
            properties.load(bf);
            if (!ObjectUtils.isEmpty(properties)) {
                String num = properties.getProperty(clientType.toString().toLowerCase() + ConnectorConst.CONNECT_CLIENT_NUM);
                if (!ObjectUtils.isEmpty(num) && !"0".equals(num)) {
                    currentPropertiesNum = Integer.valueOf(num);
                    loadPropertiesHistory(currentPropertiesNum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void controlsAction() {
        authCheck.setOnAction(e -> checkBoxAction());
        connectButton.setOnAction(e -> connect());
    }

    private void controlsPosition() {
        this.setPrefWidth(600);
        this.setPrefHeight(400);
        Label addressLabel = new Label("连接地址");
        addressLabel.setLayoutX(98);
        addressLabel.setLayoutY(52);
        addressLabel.setPrefWidth(80);
        addressLabel.setPrefHeight(40);
        addressLabel.setStyle("-fx-font-size: 18px");
        addressLabel.setTextFill(Color.WHITE);
        this.getChildren().add(addressLabel);
        addressHistory.setEditable(true);
        addressHistory.setLayoutX(229);
        addressHistory.setLayoutY(52);
        addressHistory.setPrefWidth(207);
        addressHistory.setPrefHeight(41);
        addressHistory.setStyle("-fx-font-size: 15px");
        this.getChildren().add(addressHistory);
        Label connTypeLabel = new Label("连接方式");
        connTypeLabel.setLayoutX(98);
        connTypeLabel.setLayoutY(113);
        connTypeLabel.setPrefWidth(80);
        connTypeLabel.setPrefHeight(40);
        connTypeLabel.setStyle("-fx-font-size: 18px");
        connTypeLabel.setTextFill(Color.WHITE);
        this.getChildren().add(connTypeLabel);
        connectType.setLayoutY(117);
        connectType.setLayoutX(229);
        connectType.setPrefWidth(80);
        connectType.setPrefHeight(32);
        connectType.setStyle("-fx-font-size: 15px");
        this.getChildren().add(connectType);
        authCheck.setLayoutX(98);
        authCheck.setLayoutY(175);
        authCheck.setPrefWidth(120);
        authCheck.setPrefHeight(40);
        authCheck.setStyle("-fx-font-size: 15px");
        authCheck.setTextFill(Color.WHITE);
        this.getChildren().add(authCheck);
        Label usernameLabel = new Label("帐号名称");
        usernameLabel.setLayoutX(98);
        usernameLabel.setLayoutY(216);
        usernameLabel.setPrefWidth(80);
        usernameLabel.setPrefHeight(40);
        usernameLabel.setStyle("-fx-font-size: 18px");
        usernameLabel.setTextFill(Color.WHITE);
        this.getChildren().add(usernameLabel);
        username.setLayoutX(229);
        username.setLayoutY(216);
        username.setPrefWidth(160);
        username.setPrefHeight(40);
        username.setStyle("-fx-font-size: 15px");
        this.getChildren().add(username);

        Label passwordLabel = new Label("帐号密码");
        passwordLabel.setLayoutX(98);
        passwordLabel.setLayoutY(268);
        passwordLabel.setPrefHeight(40);
        passwordLabel.setPrefWidth(80);
        passwordLabel.setStyle("-fx-font-size: 18px");
        passwordLabel.setTextFill(Color.WHITE);
        this.getChildren().add(passwordLabel);
        password.setLayoutX(229);
        password.setLayoutY(268);
        password.setPrefHeight(40);
        password.setPrefWidth(160);
        password.setStyle("-fx-font-size: 15px");
        this.getChildren().add(password);
        connectButton.setLayoutX(226);
        connectButton.setLayoutY(334);
        connectButton.setPrefWidth(207);
        connectButton.setPrefHeight(41);
        this.getChildren().add(connectButton);

    }

    private void controlStyle() {
        this.setId("connectPane");
        AnchorPane.setBottomAnchor(this,-0d);
        AnchorPane.setLeftAnchor(this,-0d);
        AnchorPane.setRightAnchor(this,-0d);
        AnchorPane.setTopAnchor(this,-0d);
        setStyle("-fx-background-image: url(/image/cbk.jpg);-fx-background-size: cover;");
        connectButton.setTextFill(Color.WHITE);
        getStylesheets().add(this.getClass().getResource("/css/connectStyle.css").toExternalForm());
    }

    /**
     * 功能描述: 与broker建立连接
     *
     * @param
     * @return:void
     * @since: v1.0
     * @Author:wangcanfeng
     * @Date: 2019/6/4-16:47
     */
    public void connect() {
        connectButton.setDisable(true);
        // 使用旋转器进行旋转
        RotateTransition rotateTransition=new RotateTransition(Duration.seconds(1),connectButton);
        // 设置参数并进行旋转
        connectRotate(rotateTransition);
        String name = username.getText();
        String pwd = password.getText();
        String connType = connectType.getSelectionModel().selectedItemProperty().getValue();
        // 如果没有设置连接方式，就设置成默认的连接方式
        if (ObjectUtils.isEmpty(connType)) {
            connType = connectType.getPromptText();
        }
        // 判断一下输入的地址是否正常
        if (!ipCheck(chooseAddress)) {
            new TipsPage("IP地址格式错误", null);
            connectButton.setDisable(false);
            // 结束旋转
            rotateEnd(rotateTransition);
            return;
        }
        if (!extendFunction.tryConnect(chooseAddress, name, pwd, connType, authCheck.isSelected())) {
            new TipsPage("连接工厂创建失败", null);
            connectButton.setDisable(false);
            // 结束旋转
            rotateEnd(rotateTransition);
            return;
        } else {
            extendFunction.afterConnected(this);
        }
        // 记录一下配置信息
        saveProperties(name, connType, pwd);
        // 打印提示信息
        new TipsPage("连接工厂创建完毕", this);
        connectButton.setDisable(false);
        // 结束旋转
        rotateEnd(rotateTransition);
    }

    private void connectRotate(RotateTransition rotateTransition){
        connectButton.setLayoutX(286);
        connectButton.setMaxWidth(40);
        connectButton.setStyle("-fx-background-radius: 20px;");
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(360);
        rotateTransition.setCycleCount(Timeline.INDEFINITE);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
    }

    private void rotateEnd(RotateTransition rotateTransition){
        connectButton.setLayoutX(226);
        connectButton.setMinWidth(207);
        connectButton.setStyle("-fx-background-radius: 10px;");
        rotateTransition.stop();
    }



    /**
     * 功能描述: 加载历史配置
     *
     * @param num
     * @return:void
     * @since: v1.0
     * @Author:wangcanfeng
     * @Date: 2019/10/10-10:14
     */
    private void loadPropertiesHistory(int num) {
        addressHistory.getItems().clear();
        addressHistory.setOnAction(event -> {
            if (!removing) {
                chooseIndex = addressHistory.getSelectionModel().getSelectedIndex();
                chooseAddress = addressHistory.getSelectionModel().getSelectedItem();
                chooseProperties(chooseIndex);
            } else {
                removing = false;
            }
        });
        for (int i = 0; i < num; i++) {
            String address = properties.getProperty(ConnectorConst.CONNECT_ADDRESS + i);
            addressHistory.getItems().add(0, address);
        }
        // 默认选中最近一个配置
        addressHistory.getSelectionModel().select(0);
        chooseAddress = addressHistory.getSelectionModel().getSelectedItem();
        chooseProperties(0);
    }

    /**
     * 功能描述: 选择配置项
     *
     * @param choose
     * @return:void
     * @since: v1.0
     * @Author:wangcanfeng
     * @Date: 2019/10/10-11:02
     */
    private void chooseProperties(int choose) {
        choose = currentPropertiesNum - choose - 1;
        String type = properties.getProperty(ConnectorConst.CONNECT_TYPE + choose);
        if (!ObjectUtils.isEmpty(type)) {
            connectType.setPromptText(type);
        }
        String name = properties.getProperty(ConnectorConst.CONNECT_USERNAME + choose);
        if (!ObjectUtils.isEmpty(name)) {
            authCheck.selectedProperty().setValue(true);
            username.setDisable(false);
            password.setDisable(false);
            username.setText(name);
        } else {
            authCheck.selectedProperty().setValue(false);
            username.setDisable(true);
            password.setDisable(true);
            username.setText("");
        }
        String pwd = properties.getProperty(ConnectorConst.CONNECT_PASSWORD + choose);
        if (!ObjectUtils.isEmpty(pwd)) {
            password.setText(pwd);
        } else {
            password.setText("");
        }
    }

    /**
     * 功能描述: 保存配置信息
     *
     * @param name
     * @param type
     * @param pwd
     * @return:void
     * @since: v1.0
     * @Author:wangcanfeng
     * @Date: 2019/10/10-11:06
     */
    private void saveProperties(String name, String type, String pwd) {
        // 如果这个配置已经存在过，先删除，后面的配置上移，并将最新的配置加到最后。
        int current = chooseIndex;
        if (addressHistory.getItems().contains(chooseAddress)) {
            removing = true;
            addressHistory.getItems().remove(chooseAddress);
            moveProperties(current);
        } else {
            // 如果历史记录没有超过保存上限，就继续添加
            if (currentPropertiesNum < ConnectorConst.MAX_PROPERTIES) {
                properties.setProperty(ConnectorConst.CONNECT_CLIENT_NUM, String.valueOf(++currentPropertiesNum));
            } else {
                // 当前配置数量已经超限，所以需要替换掉历史事件最远的配置
                removing = true;
                addressHistory.getItems().remove(currentPropertiesNum - 1);
                moveProperties(currentPropertiesNum - 1);
            }
        }
        // 最后在最新的位置配置上最新的配置信息
        int num = currentPropertiesNum - 1;
        properties.setProperty(ConnectorConst.CONNECT_ADDRESS + num, chooseAddress);
        properties.setProperty(ConnectorConst.CONNECT_TYPE + num, type);
        properties.setProperty(ConnectorConst.CONNECT_USERNAME + num, name);
        properties.setProperty(ConnectorConst.CONNECT_PASSWORD + num, pwd);
        addressHistory.getItems().add(0, chooseAddress);
        // 重新设置选择的目标
        addressHistory.getSelectionModel().select(0);
        chooseIndex = 0;
        chooseAddress = addressHistory.getSelectionModel().getSelectedItem();
        extendFunction.recordPropertis(properties, propertiesFile);
    }

    /**
     * 功能描述: 移动配置信息
     *
     * @param choose
     * @return:void
     * @since: v1.0
     * @Author:wangcanfeng
     * @Date: 2019/10/10-16:54
     */
    private void moveProperties(int choose) {
        choose = currentPropertiesNum - choose - 1;
        // 再将后面的配置项顺序上移
        for (int i = choose; i < ConnectorConst.MAX_PROPERTIES; i++) {
            int j = i + 1;
            String nextAddress = properties.getProperty(ConnectorConst.CONNECT_ADDRESS + j);
            if (!ObjectUtils.isEmpty(nextAddress)) {
                properties.setProperty(ConnectorConst.CONNECT_ADDRESS + i, nextAddress);
            }
            String nextName = properties.getProperty(ConnectorConst.CONNECT_USERNAME + j);
            if (!ObjectUtils.isEmpty(nextName)) {
                properties.setProperty(ConnectorConst.CONNECT_USERNAME + i, nextName);
            }
            String nextType = properties.getProperty(ConnectorConst.CONNECT_TYPE + j);
            if (!ObjectUtils.isEmpty(nextType)) {
                properties.setProperty(ConnectorConst.CONNECT_TYPE + i, nextType);
            }
            String nextPwd = properties.getProperty(ConnectorConst.CONNECT_PASSWORD + j);
            if (!ObjectUtils.isEmpty(nextPwd)) {
                properties.setProperty(ConnectorConst.CONNECT_PASSWORD + i, nextPwd);
            }
        }
    }


    /**
     * 功能描述: 勾选是否需要输入用户信息
     *
     * @param
     * @return:void
     * @since: v1.0
     * @Author:wangcanfeng
     * @Date: 2019/10/15-11:59
     */
    private void checkBoxAction() {
        if (authCheck.isSelected()) {
            username.setDisable(false);
            password.setDisable(false);
        } else {
            username.setDisable(true);
            password.setDisable(true);
        }
    }

    private boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d):(\\d+)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }

    public interface ExtendFunction {

        boolean tryConnect(String address, String name, String pwd, String connType, boolean needAuth);

        void recordPropertis(Properties properties, File file);

        void afterConnected(Node current);

    }

    interface ConnectorConst {
        Integer MAX_PROPERTIES = 10;
        String CONNECT_CLIENT_NUM = ".client.num";
        String CONNECT_ADDRESS = ".address";
        String CONNECT_TYPE = ".connect.type";
        String CONNECT_USERNAME = ".username";
        String CONNECT_PASSWORD = ".password";
    }

    public enum ClientType {
        POSTGRES,
        AMQ
    }

    class OrderedProperties extends Properties {
        private final LinkedHashSet<Object> keys = new LinkedHashSet<>();

        @Override
        public Enumeration<Object> keys() {
            return Collections.enumeration(keys);
        }

        @Override
        public Set<Object> keySet() {
            return keys;
        }

        @Override
        public Set<String> stringPropertyNames() {
            Set<String> set = new LinkedHashSet<>();
            for (Object key : this.keys) {
                set.add((String) key);
            }
            return set;
        }

        @Override
        public String getProperty(String key) {
            return super.getProperty(clientType.toString().toLowerCase() + key);
        }

        @Override
        public synchronized Object setProperty(String key, String value) {
            return super.setProperty(clientType.toString().toLowerCase() + key, value);
        }
    }

}
