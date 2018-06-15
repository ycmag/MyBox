package mara.mybox.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mara.mybox.objects.AppVaribles;
import mara.mybox.objects.CommonValues;

/**
 * @Author Mara
 * @CreateDate 2018-6-15
 * @Description
 * @License Apache License Version 2.0
 */
public class CommonBarController extends BaseController {

    @FXML
    protected Pane commonBarPane;

    @Override
    public Stage getThisStage() {
        if (thisStage == null) {
            if (commonBarPane != null && commonBarPane.getScene() != null) {
                thisStage = (Stage) commonBarPane.getScene().getWindow();
            }
        }
        return thisStage;
    }

    @FXML
    private void setEnglish(MouseEvent event) {
        AppVaribles.CurrentBundle = CommonValues.BundleEnUS;
        reloadInterface(parentFxml);
    }

    @FXML
    private void setChinese(MouseEvent event) {
        AppVaribles.CurrentBundle = CommonValues.BundleZhCN;
        reloadInterface(parentFxml);
    }

    @FXML
    private void about(MouseEvent event) {
        try {
            Pane aboutPane = FXMLLoader.load(getClass().getResource(CommonValues.AboutInterface), AppVaribles.CurrentBundle);
            Scene scene = new Scene(aboutPane);

            final Stage stage = new Stage();
            stage.initModality(Modality.NONE);
            stage.initOwner(getThisStage());
            stage.setTitle(AppVaribles.getMessage("AppTitle"));
            stage.getIcons().add(new Image("img/mybox.png"));
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

}