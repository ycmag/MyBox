package mara.mybox.controller;

import java.util.Arrays;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import static mara.mybox.controller.BaseController.logger;
import mara.mybox.objects.AppVaribles;
import mara.mybox.objects.CommonValues;
import mara.mybox.fxml.FxmlImageTools;
import static mara.mybox.fxml.FxmlTools.badStyle;

/**
 * @Author Mara
 * @CreateDate 2018-10-11
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageManufactureShadowController extends ImageManufactureController {

    final protected String ImageShadowKey;
    protected int shadow;

    @FXML
    protected ColorPicker shadowColorPicker;
    @FXML
    protected Button transShadowButton;
    @FXML
    protected ComboBox shadowBox;

    public ImageManufactureShadowController() {
        ImageShadowKey = "ImageShadowKey";
    }

    @Override
    protected void initializeNext2() {
        try {
            initCommon();
            initShadowTab();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    protected void initInterface() {
        try {
            if (values == null || values.getImage() == null) {
                return;
            }
            super.initInterface();

            isSettingValues = true;

            if (CommonValues.NoAlphaImages.contains(values.getImageInfo().getImageFormat())) {
                transShadowButton.setDisable(true);
            } else {
                transShadowButton.setDisable(false);
            }

            shadowBox.getItems().clear();
            shadowBox.getItems().addAll(Arrays.asList(values.getImageInfo().getxPixels() / 100 + "",
                    values.getImageInfo().getxPixels() / 50 + "",
                    values.getImageInfo().getxPixels() / 200 + "",
                    values.getImageInfo().getxPixels() / 30 + "",
                    "0", "4", "5", "3", "2", "1", "6"));
            shadowBox.getSelectionModel().select(0);

            isSettingValues = false;
        } catch (Exception e) {
            logger.debug(e.toString());
        }

    }

    // Shadow Methods
    protected void initShadowTab() {
        try {
            shadowBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        shadow = Integer.valueOf(newValue);
                        if (shadow >= 0) {
                            shadowBox.getEditor().setStyle(null);
                            AppVaribles.setConfigValue(ImageShadowKey, newValue);
                        } else {
                            shadow = 0;
                            shadowBox.getEditor().setStyle(badStyle);
                        }
                    } catch (Exception e) {
                        shadow = 0;
                        shadowBox.getEditor().setStyle(badStyle);
                    }
                }
            });

            shadowColorPicker.setValue(Color.BLACK);

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    public void shadowTransparentAction() {
        shadowColorPicker.setValue(Color.TRANSPARENT);
    }

    @FXML
    public void shadowWhiteAction() {
        shadowColorPicker.setValue(Color.WHITE);
    }

    @FXML
    public void shadowBlackAction() {
        shadowColorPicker.setValue(Color.BLACK);
    }

    @FXML
    public void shadowAction() {
        if (shadow <= 0) {
            return;
        }
        try {
            Image newImage = FxmlImageTools.addShadowFx(values.getCurrentImage(), shadow, shadowColorPicker.getValue());
            if (newImage != null) {
                recordImageHistory(ImageOperationType.Shadow, newImage);
                values.setUndoImage(values.getCurrentImage());
                values.setCurrentImage(newImage);
                imageView.setImage(newImage);
                setImageChanged(true);
                setBottomLabel();
                return;
            }
        } catch (Exception e) {

        }

        Image newImage = FxmlImageTools.addShadowBigFx(values.getCurrentImage(), shadow, shadowColorPicker.getValue());
        if (newImage != null) {
            recordImageHistory(ImageOperationType.Shadow, newImage);
            values.setUndoImage(values.getCurrentImage());
            values.setCurrentImage(newImage);
            imageView.setImage(newImage);
            setImageChanged(true);
            setBottomLabel();
            return;
        }

        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage = FxmlImageTools.addShadow(values.getCurrentImage(), shadow, shadowColorPicker.getValue());
                recordImageHistory(ImageOperationType.Shadow, newImage);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        values.setUndoImage(values.getCurrentImage());
                        values.setCurrentImage(newImage);
                        imageView.setImage(newImage);
                        setImageChanged(true);
                        setBottomLabel();
                    }
                });
                return null;
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

}
