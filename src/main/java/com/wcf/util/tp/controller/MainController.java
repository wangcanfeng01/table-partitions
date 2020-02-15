package com.wcf.util.tp.controller;

import com.wcf.util.tp.common.page.TipsPage;
import com.wcf.util.tp.common.utils.JdbcUtils;
import com.wcf.util.tp.control.ConnectorPane;
import com.wcf.util.tp.sql.PartitionSQL;
import com.wcf.util.tp.sql.PgHashPartitionSQL;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author wangcanfeng
 * @time 2019/5/10
 * @function
 **/
@FXMLController
public class MainController implements Initializable {

    @FXML
    private AnchorPane bottom;

    @FXML
    private TextField tableName;

    @FXML
    private TextField partitions;

    @FXML
    private TextField colName;

    @FXML
    private TextField selectAppend;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("成功启动主窗口");
        bottom.getChildren().add(new ConnectorPane(getProperties(), ConnectorPane.ClientType.POSTGRES, getFunc()));
    }

    private File getProperties() {
        return null;
    }

    private ConnectorPane.ExtendFunction getFunc() {
        return null;
    }

    /**
     * 扩展功能函数
     */
    class ConnectImpl implements ConnectorPane.ExtendFunction {

        @Override
        public boolean tryConnect(String address, String name, String pwd, String connType, boolean needAuth) {
            return false;
        }

        @Override
        public void recordPropertis(Properties properties, File file) {

        }

        @Override
        public void afterConnected(Node current) {

        }
    }

    public void executePartitions() {
        PartitionSQL partition = new PgHashPartitionSQL();
        String tabName = tableName.getText();
        if (ObjectUtils.isEmpty(tabName)) {
            new TipsPage("表名不能为空", null);
            return;
        }
        // 至少有一个分表吧
        Integer partNum = 1;
        if (numCheck(partitions.getText())) {
            partNum = Integer.valueOf(partitions.getText());
        } else {
            new TipsPage("请输入1-99的数字", null);
            return;
        }
        String cName = colName.getText();
        if (ObjectUtils.isEmpty(cName)) {
            new TipsPage("列名不能为空", null);
            return;
        }
        String pSQL = partition.partitionSQL(tabName, cName, partNum);
        String iFunc = partition.insertFunction(tabName, cName, partNum);
        String pNull = partition.protectNull(tabName, cName);
        try {
            // 在数据库中执行分表语句
            JdbcUtils.executeBatch(pSQL, iFunc, pNull);
        } catch (Exception e) {
            new TipsPage("分表执行失败", null);
            return;
        }
        selectAppend.setText(partition.selectAppend(cName, partNum));
    }

    private boolean numCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^\\d{2}$";
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

}
