package mara.mybox.controller;

import java.awt.image.BufferedImage;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import static mara.mybox.controller.BaseController.logger;
import mara.mybox.image.ImageConvertTools;
import mara.mybox.objects.AppVaribles;
import static mara.mybox.fxml.FxmlTools.badStyle;

/**
 * @Author Mara
 * @CreateDate 2018-9-22
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageManufactureBatchCropController extends ImageManufactureBatchController {

    private boolean isCenter;
    private int centerWidth, centerHeight, leftX, leftY, rightX, rightY;

    @FXML
    private RadioButton centerRadio, customRadio;
    @FXML
    private ToggleGroup cropGroup;
    @FXML
    private TextField centerWidthInput, centerHeightInput, leftXInput, leftYInput, rightXInput, rightYInput;

    public ImageManufactureBatchCropController() {

    }

    @Override
    protected void initializeNext2() {
        try {

            operationBarController.startButton.disableProperty().bind(Bindings.isEmpty(targetPathInput.textProperty())
                    .or(targetPathInput.styleProperty().isEqualTo(badStyle))
                    .or(Bindings.isEmpty(sourceFilesInformation))
                    .or(leftXInput.styleProperty().isEqualTo(badStyle))
                    .or(leftYInput.styleProperty().isEqualTo(badStyle))
                    .or(rightXInput.styleProperty().isEqualTo(badStyle))
                    .or(rightYInput.styleProperty().isEqualTo(badStyle))
                    .or(centerWidthInput.styleProperty().isEqualTo(badStyle))
                    .or(centerHeightInput.styleProperty().isEqualTo(badStyle))
            );

        } catch (Exception e) {
            logger.debug(e.toString());
        }
    }

    @Override
    protected void initOptionsSection() {
        try {

            centerWidthInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkCenterWidth();
                }
            });

            centerHeightInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkCenterHeight();
                }
            });

            leftXInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkLeftX();
                }
            });

            leftYInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkLeftY();
                }
            });

            rightXInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkRightX();
                }
            });

            rightYInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkRightY();
                }
            });

            cropGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkType();
                }
            });
            checkType();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkType() {
        centerWidthInput.setDisable(true);
        centerWidthInput.setStyle(null);
        centerHeightInput.setDisable(true);
        centerHeightInput.setStyle(null);
        leftXInput.setDisable(true);
        leftXInput.setStyle(null);
        leftYInput.setDisable(true);
        leftYInput.setStyle(null);
        rightXInput.setDisable(true);
        rightXInput.setStyle(null);
        rightYInput.setDisable(true);
        rightYInput.setStyle(null);

        RadioButton selected = (RadioButton) cropGroup.getSelectedToggle();
        if (selected.equals(centerRadio)) {
            isCenter = true;
            centerWidthInput.setDisable(false);
            centerHeightInput.setDisable(false);
            checkCenterWidth();
            checkCenterHeight();

        } else if (selected.equals(customRadio)) {
            isCenter = false;
            leftXInput.setDisable(false);
            leftYInput.setDisable(false);
            rightXInput.setDisable(false);
            rightYInput.setDisable(false);
            checkLeftX();
            checkLeftY();
            checkRightX();
            checkRightY();

        }
    }

    private void checkCenterWidth() {
        try {
            centerWidth = Integer.valueOf(centerWidthInput.getText());
            if (centerWidth > 0) {
                centerWidthInput.setStyle(null);
            } else {
                centerWidthInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            centerWidthInput.setStyle(badStyle);
        }
    }

    private void checkCenterHeight() {
        try {
            centerHeight = Integer.valueOf(centerHeightInput.getText());
            if (centerHeight > 0) {
                centerHeightInput.setStyle(null);
            } else {
                centerHeightInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            centerHeightInput.setStyle(badStyle);
        }
    }

    private void checkLeftX() {
        try {
            leftX = Integer.valueOf(leftXInput.getText());
            if (leftX > 0) {
                leftXInput.setStyle(null);
            } else {
                leftXInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            leftXInput.setStyle(badStyle);
        }
    }

    private void checkLeftY() {
        try {
            leftY = Integer.valueOf(leftYInput.getText());
            if (leftX > 0) {
                leftXInput.setStyle(null);
            } else {
                leftXInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            leftXInput.setStyle(badStyle);
        }
    }

    private void checkRightX() {
        try {
            rightX = Integer.valueOf(rightXInput.getText());
            if (rightX > 0 && rightX > leftX) {
                rightXInput.setStyle(null);
            } else {
                rightXInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            rightXInput.setStyle(badStyle);
        }
    }

    private void checkRightY() {
        try {
            rightY = Integer.valueOf(rightYInput.getText());
            if (rightY > 0 && rightY > leftY) {
                rightYInput.setStyle(null);
            } else {
                rightYInput.setStyle(badStyle);
            }
        } catch (Exception e) {
            rightYInput.setStyle(badStyle);
        }
    }

    @Override
    protected BufferedImage handleImage(BufferedImage source) {
        try {
            int width = source.getWidth();
            int height = source.getHeight();
            int x1, y1, x2, y2;
            if (isCenter) {
                x1 = (source.getWidth() - centerWidth) / 2;
                y1 = (source.getHeight() - centerHeight) / 2;
                x2 = (source.getWidth() + centerWidth) / 2 - 1;
                y2 = (source.getHeight() + centerHeight) / 2 - 1;
            } else {
                x1 = leftX;
                y1 = leftY;
                x2 = rightX;
                y2 = rightY;
            }
            if (x1 >= x2 || y1 >= y2
                    || x1 < 0 || x2 < 0 || y1 < 0 || y2 < 0
                    || x1 > width - 1 || y1 > height - 1
                    || x2 > width - 1 || y2 > height - 1) {
                errorString = AppVaribles.getMessage("BeyondSize");
                return null;
            }
            return ImageConvertTools.cropImage(source, x1, y1, x2, y2);
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }

    }

}
