package com.wcf.util.tp;

import com.wcf.util.tp.common.page.MakeSureExitPage;
import com.wcf.util.tp.common.utils.ContainerUtils;
import com.wcf.util.tp.common.utils.ThreadPoolUtils;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.wcf.util.tp"},exclude = {DataSourceAutoConfiguration.class})
public class TablePartitionApplication extends AbstractJavaFxApplicationSupport {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("我能分表");
        primaryStage.setResizable(false);
        // 启动时将主窗口注入到容器中
        ContainerUtils.setPrimaryStage(primaryStage);
        // 初始化线程池
        ThreadPoolUtils.createThreadPool();
        // 这里是我自定义的退出确认提示窗口
        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            MakeSureExitPage sure = new MakeSureExitPage(windowEvent, primaryStage);
            sure.show();
        });
        super.start(primaryStage);
    }


    public static void main(String[] args) {
        launch(TablePartitionApplication.class, MainView.class, new InitializationView(), args);
    }
}
