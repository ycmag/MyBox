package mara.mybox.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import static mara.mybox.controller.BaseController.logger;
import mara.mybox.fxml.FxmlFilterTools;
import mara.mybox.fxml.FxmlFilterTools.FiltersOperationType;
import mara.mybox.image.ImageGrayTools;
import static mara.mybox.objects.AppVaribles.getMessage;
import mara.mybox.objects.ImageScope.OperationType;
import mara.mybox.fxml.FxmlTools;
import static mara.mybox.fxml.FxmlTools.badStyle;
import mara.mybox.objects.ImageScope.ScopeType;

/**
 * @Author Mara
 * @CreateDate 2018-10-11
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageManufactureFiltersController extends ImageManufactureController {

    protected int scaleValue;
    private FiltersOperationType filtersOperationType;

    @FXML
    protected ToggleGroup filtersGroup;
    @FXML
    protected Slider scaleSlider;
    @FXML
    protected Button binaryCalculateButton, okButton;
    @FXML
    protected RadioButton grayRadio, bwRadio;
    @FXML
    protected HBox bwBox;
    @FXML
    protected TextField scaleInput;
    @FXML
    protected Label scaleLabel, unitLabel;

    public ImageManufactureFiltersController() {
    }

    @Override
    protected void initializeNext2() {
        try {
            initCommon();
            initFiltersTab();
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
            values.getScope().setOperationType(OperationType.Filters);

            isSettingValues = true;

            isSettingValues = false;
        } catch (Exception e) {
            logger.debug(e.toString());
        }

    }

    protected void initFiltersTab() {
        try {

            filtersGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkFiltersOperationType();
                }
            });
            checkFiltersOperationType();

            scaleSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    scaleValue = newValue.intValue();
                    scaleInput.setText(scaleValue + "");
                }
            });

            scaleInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkValueInput();
                }
            });

            Tooltip stips = new Tooltip(getMessage("GrayBinaryComments"));
            stips.setFont(new Font(16));
            FxmlTools.setComments(grayRadio, stips);
            FxmlTools.setComments(bwRadio, stips);

            Tooltip tips = new Tooltip(getMessage("CTRL+a"));
            tips.setFont(new Font(16));
            FxmlTools.quickTooltip(okButton, tips);

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkFiltersOperationType() {
        binaryCalculateButton.setDisable(true);
        scaleLabel.setText("");
        scaleSlider.setDisable(true);
        scaleInput.setDisable(true);
        scaleInput.setStyle(null);
        unitLabel.setText("");
        RadioButton selected = (RadioButton) filtersGroup.getSelectedToggle();
        if (getMessage("BlackOrWhite").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.BlackOrWhite;
            binaryCalculateButton.setDisable(false);
            scaleLabel.setText(getMessage("Threshold"));
            scaleSlider.setDisable(false);
            scaleSlider.setMax(99);
            scaleSlider.setMin(1);
            scaleSlider.setBlockIncrement(1);
            scaleSlider.setValue(50);
            scaleInput.setDisable(false);
            unitLabel.setText("%");
            checkValueInput();
        } else if (getMessage("Gray").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.Gray;
        } else if (getMessage("Invert").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.Invert;
        } else if (getMessage("Red").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.Red;
        } else if (getMessage("Green").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.Green;
        } else if (getMessage("Blue").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.Blue;
        } else if (getMessage("Yellow").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.Yellow;
        } else if (getMessage("Cyan").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.Cyan;
        } else if (getMessage("Magenta").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.Magenta;
        } else if (getMessage("RedInvert").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.RedInvert;
        } else if (getMessage("GreenInvert").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.GreenInvert;
        } else if (getMessage("BlueInvert").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.BlueInvert;
        } else if (getMessage("Sepia").equals(selected.getText())) {
            filtersOperationType = FiltersOperationType.Sepia;
            scaleLabel.setText(getMessage("Intensity"));
            scaleSlider.setDisable(false);
            scaleSlider.setMax(255);
            scaleSlider.setMin(0);
            scaleSlider.setBlockIncrement(1);
            scaleSlider.setValue(80);
            scaleInput.setDisable(false);
            checkValueInput();
        }

    }

    private void checkValueInput() {
        try {
            scaleValue = Integer.valueOf(scaleInput.getText());
            if (scaleValue >= 0 && scaleValue <= scaleSlider.getMax()) {
                scaleInput.setStyle(null);
                scaleSlider.setValue(scaleValue);
            } else {
                scaleInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            scaleInput.setStyle(badStyle);
        }
    }

    @FXML
    public void calculateThreshold() {
        scaleValue = ImageGrayTools.calculateThreshold(values.getSourceFile());
        scaleValue = scaleValue * 100 / 256;
        scaleSlider.setValue(scaleValue);
    }

    @Override
    protected void keyEventsHandler(KeyEvent event) {
        super.keyEventsHandler(event);
        String key = event.getText();
        if (key == null || key.isEmpty()) {
            return;
        }
        if (event.isControlDown()) {
            switch (key) {
                case "a":
                case "A":
                    filtersAction();
                    break;
            }
        }
    }

    @FXML
    public void filtersAction() {
        if (null == filtersOperationType || scope == null) {
            return;
        }
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final Image newImage;
                if (scope.getScopeType() == ScopeType.Matting) {
                    newImage = FxmlFilterTools.filterColorByMatting(values.getCurrentImage(),
                            filtersOperationType, scaleValue, scope.getPoints(), scope.getColorDistance());
                } else {
                    newImage = FxmlFilterTools.filterColorByScope(values.getCurrentImage(),
                            filtersOperationType, scaleValue, scope);
                }
                recordImageHistory(ImageOperationType.Filters, newImage);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        values.setUndoImage(values.getCurrentImage());
                        values.setCurrentImage(newImage);
                        imageView.setImage(newImage);
                        setImageChanged(true);
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
