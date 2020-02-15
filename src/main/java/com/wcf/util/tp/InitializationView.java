package com.wcf.util.tp;

import de.felixroske.jfxsupport.SplashScreen;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

/**
 * @author wangcanfeng
 * @time 2019/5/17
 * @function
 **/
public class InitializationView  extends SplashScreen {
    @Override
    public String getImagePath() {
        return "/image/init.jpg";
    }

    @Override
    public Parent getParent() {
        Group gp = new Group();
        ImageView imageView = new ImageView(this.getClass().getResource(this.getImagePath()).toExternalForm());
        gp.getChildren().add(imageView);
        return gp;
    }
}
