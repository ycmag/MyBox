package mara.mybox.controller;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javax.imageio.ImageIO;
import static mara.mybox.controller.BaseController.logger;
import mara.mybox.fxml.FxmlImageTools;
import mara.mybox.image.ImageBlendTools.ImagesBlendMode;
import mara.mybox.image.ImageBlendTools.ImagesRelativeLocation;
import mara.mybox.imagefile.ImageFileReaders;
import mara.mybox.imagefile.ImageFileWriters;
import mara.mybox.objects.AppVaribles;
import mara.mybox.objects.CommonValues;
import mara.mybox.objects.ImageFileInformation;
import mara.mybox.tools.FileTools;
import static mara.mybox.fxml.FxmlTools.badStyle;

/**
 * @Author Mara
 * @CreateDate 2018-10-31
 * @Description
 * @License Apache License Version 2.0
 */
public class ImagesBlendController extends ImageViewerController {

    protected String ImageBlendFileTypeKey;
    private ImagesRelativeLocation location;
    private ImagesBlendMode blendMode;
    private int x, y;
    private boolean isAreaValid;
    private float opacity;

    private File foreFile, backFile, targetFile;
    private Image foreImage, backImage;
    private ImageFileInformation foreInfo, backInfo;

    @FXML
    private VBox mainPane, targetBox;
    @FXML
    private SplitPane splitPane;
    @FXML
    private ScrollPane foreScroll, backScroll;
    @FXML
    private ImageView foreView, backView;
    @FXML
    private HBox foreBox, backBox, opacityHBox, toolBox;
    @FXML
    private ToggleGroup locationGroup;
    @FXML
    private Button openTargetButton;
    @FXML
    private ComboBox<String> targetTypeBox, blendModeBox, opacityBox;
    @FXML
    private Label foreTitle, foreLabel, backTitle, backLabel;
    @FXML
    private Label pointLabel;
    @FXML
    private Button saveButton, imageSizeButton, paneSizeButton, zoomInButton, zoomOutButton, newWindowButton;
    @FXML
    private TextField pointX, pointY;
    @FXML
    private CheckBox intersectOnlyCheck;

    public ImagesBlendController() {
        ImageBlendFileTypeKey = "ImageBlendFileType";
    }

    @Override
    protected void initializeNext() {
        try {
            initSourcesSection();
            initOptionsSection();
            initTargetSection();

            targetBox.setDisable(true);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void initSourcesSection() {
        try {

            foreBox.setDisable(true);
            backBox.setDisable(true);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void initOptionsSection() {
        try {
            locationGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkLocation();
                }
            });
            location = ImagesRelativeLocation.Foreground_In_Background;
            pointLabel.setText(AppVaribles.getMessage("ClickOnBackgournd"));

            blendModeBox.getItems().addAll(Arrays.asList(AppVaribles.getMessage("NormalMode"),
                    AppVaribles.getMessage("DissolveMode"), AppVaribles.getMessage("MultiplyMode"), AppVaribles.getMessage("ScreenMode"),
                    AppVaribles.getMessage("OverlayMode"), AppVaribles.getMessage("HardLightMode"), AppVaribles.getMessage("SoftLightMode"),
                    AppVaribles.getMessage("ColorDodgeMode"), AppVaribles.getMessage("LinearDodgeMode"), AppVaribles.getMessage("DivideMode"),
                    AppVaribles.getMessage("ColorBurnMode"), AppVaribles.getMessage("LinearBurnMode"), AppVaribles.getMessage("VividLightMode"),
                    AppVaribles.getMessage("LinearLightMode"), AppVaribles.getMessage("SubtractMode"), AppVaribles.getMessage("DifferenceMode"),
                    AppVaribles.getMessage("ExclusionMode"), AppVaribles.getMessage("DarkenMode"), AppVaribles.getMessage("LightenMode"),
                    AppVaribles.getMessage("HueMode"), AppVaribles.getMessage("SaturationMode"), AppVaribles.getMessage("ColorMode"),
                    AppVaribles.getMessage("LuminosityMode")
            ));
            blendModeBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov,
                        String oldValue, String newValue) {
                    checkBlendMode(newValue);
                }
            });
            blendMode = null;

            pointX.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkPoint();
                }
            });
            pointY.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkPoint();
                }
            });
            isAreaValid = true;

            opacityBox.getItems().addAll(Arrays.asList("0.5", "1.0", "0.3", "0.1", "0.8", "0.2", "0.9", "0.0"));
            opacityBox.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        opacity = Float.valueOf(newValue);
                        if (opacity >= 0.0f && opacity <= 1.0f) {
                            opacityBox.getEditor().setStyle(null);
                            blendImages();
                        } else {
                            opacity = 0.5f;
                            opacityBox.getEditor().setStyle(badStyle);
                        }
                    } catch (Exception e) {
                        opacity = 0.5f;
                        opacityBox.getEditor().setStyle(badStyle);
                    }
                }
            });
            opacityBox.getSelectionModel().select(0);
            opacityHBox.setDisable(true);

            intersectOnlyCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable,
                        Boolean oldValue, Boolean newValue) {
                    blendImages();
                }
            });
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkLocation() {
        if (foreImage == null || backImage == null) {
            return;
        }
        RadioButton selected = (RadioButton) locationGroup.getSelectedToggle();
        if (AppVaribles.getMessage("FinB").equals(selected.getText())) {
            location = ImagesRelativeLocation.Foreground_In_Background;
            pointLabel.setText(AppVaribles.getMessage("ClickOnBackgournd"));
            bottomLabel.setText(AppVaribles.getMessage("BlendedSize") + ": "
                    + (int) backImage.getWidth() + "*" + (int) backImage.getHeight());

        } else if (AppVaribles.getMessage("BinF").equals(selected.getText())) {
            location = ImagesRelativeLocation.Background_In_Foreground;
            pointLabel.setText(AppVaribles.getMessage("ClickOnForegournd"));
            bottomLabel.setText(AppVaribles.getMessage("BlendedSize") + ": "
                    + (int) foreImage.getWidth() + "*" + (int) foreImage.getHeight());

        } else {
            return;
        }
        pointX.setText("0");
        pointY.setText("0");
    }

    private void checkBlendMode(String mode) {
        opacityHBox.setDisable(true);
        if (AppVaribles.getMessage("NormalMode").equals(mode)) {
            blendMode = ImagesBlendMode.NORMAL;
            opacityHBox.setDisable(false);

        } else if (AppVaribles.getMessage("DissolveMode").equals(mode)) {
            blendMode = ImagesBlendMode.DISSOLVE;

        } else if (AppVaribles.getMessage("MultiplyMode").equals(mode)) {
            blendMode = ImagesBlendMode.MULTIPLY;

        } else if (AppVaribles.getMessage("ScreenMode").equals(mode)) {
            blendMode = ImagesBlendMode.SCREEN;

        } else if (AppVaribles.getMessage("OverlayMode").equals(mode)) {
            blendMode = ImagesBlendMode.OVERLAY;

        } else if (AppVaribles.getMessage("HardLightMode").equals(mode)) {
            blendMode = ImagesBlendMode.HARD_LIGHT;

        } else if (AppVaribles.getMessage("SoftLightMode").equals(mode)) {
            blendMode = ImagesBlendMode.SOFT_LIGHT;

        } else if (AppVaribles.getMessage("ColorDodgeMode").equals(mode)) {
            blendMode = ImagesBlendMode.COLOR_DODGE;

        } else if (AppVaribles.getMessage("LinearDodgeMode").equals(mode)) {
            blendMode = ImagesBlendMode.LINEAR_DODGE;

        } else if (AppVaribles.getMessage("DivideMode").equals(mode)) {
            blendMode = ImagesBlendMode.DIVIDE;

        } else if (AppVaribles.getMessage("ColorBurnMode").equals(mode)) {
            blendMode = ImagesBlendMode.COLOR_BURN;

        } else if (AppVaribles.getMessage("LinearBurnMode").equals(mode)) {
            blendMode = ImagesBlendMode.LINEAR_BURN;

        } else if (AppVaribles.getMessage("VividLightMode").equals(mode)) {
            blendMode = ImagesBlendMode.VIVID_LIGHT;

        } else if (AppVaribles.getMessage("LinearLightMode").equals(mode)) {
            blendMode = ImagesBlendMode.LINEAR_LIGHT;

        } else if (AppVaribles.getMessage("SubtractMode").equals(mode)) {
            blendMode = ImagesBlendMode.SUBTRACT;

        } else if (AppVaribles.getMessage("DifferenceMode").equals(mode)) {
            blendMode = ImagesBlendMode.DIFFERENCE;

        } else if (AppVaribles.getMessage("ExclusionMode").equals(mode)) {
            blendMode = ImagesBlendMode.EXCLUSION;

        } else if (AppVaribles.getMessage("DarkenMode").equals(mode)) {
            blendMode = ImagesBlendMode.DARKEN;

        } else if (AppVaribles.getMessage("LightenMode").equals(mode)) {
            blendMode = ImagesBlendMode.LIGHTEN;

        } else if (AppVaribles.getMessage("HueMode").equals(mode)) {
            blendMode = ImagesBlendMode.HUE;

        } else if (AppVaribles.getMessage("SaturationMode").equals(mode)) {
            blendMode = ImagesBlendMode.SATURATION;

        } else if (AppVaribles.getMessage("ColorMode").equals(mode)) {
            blendMode = ImagesBlendMode.COLOR;

        } else if (AppVaribles.getMessage("LuminosityMode").equals(mode)) {
            blendMode = ImagesBlendMode.LUMINOSITY;

        }
        blendImages();
    }

    private void checkPoint() {
        try {
            isAreaValid = true;
            x = Integer.valueOf(pointX.getText());
            if (x >= 0 && x < backImage.getWidth()) {
                pointX.setStyle(null);
                isAreaValid = true;
            } else {
                pointX.setStyle(badStyle);
                x = 0;
                isAreaValid = false;
            }
        } catch (Exception e) {
            pointX.setStyle(badStyle);
            x = 0;
            isAreaValid = false;
        }
        try {
            y = Integer.valueOf(pointY.getText());
            if (y >= 0 && y < backImage.getHeight()) {
                pointY.setStyle(null);
            } else {
                pointY.setStyle(badStyle);
                y = 0;
                isAreaValid = false;
            }
        } catch (Exception e) {
            pointY.setStyle(badStyle);
            y = 0;
            isAreaValid = false;
        }

        blendImages();
    }

    private void initTargetSection() {
        try {
            toolBox.disableProperty().bind(
                    foreBox.disableProperty()
                            .or(backBox.disableProperty())
                            .or(blendModeBox.selectionModelProperty().getValue().selectedItemProperty().isNull())
                            .or(pointX.styleProperty().isEqualTo(badStyle))
                            .or(pointY.styleProperty().isEqualTo(badStyle))
            );

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    private void selectForegroundImage(ActionEvent event) {
        try {
            final FileChooser fileChooser = new FileChooser();
            File path = new File(AppVaribles.getConfigValue(sourcePathKey, CommonValues.UserFilePath));
            if (!path.isDirectory()) {
                path = new File(CommonValues.UserFilePath);
            }
            fileChooser.setInitialDirectory(path);
            fileChooser.getExtensionFilters().addAll(fileExtensionFilter);
            final File file = fileChooser.showOpenDialog(getMyStage());
            if (file == null) {
                return;
            }
            foreFile = file;
            AppVaribles.setConfigValue(LastPathKey, foreFile.getParent());
            AppVaribles.setConfigValue(sourcePathKey, foreFile.getParent());

            final String fileName = file.getPath();
            Task loadTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        BufferedImage bufferImage = ImageIO.read(file);
                        foreImage = SwingFXUtils.toFXImage(bufferImage, null);
                        foreInfo = ImageFileReaders.readImageMetaData(fileName);
                        foreInfo.setImage(foreImage);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                foreView.setPreserveRatio(true);
                                foreView.setImage(foreImage);
                                if (foreScroll.getHeight() < foreImage.getHeight()) {
                                    foreView.setFitHeight(foreScroll.getHeight() - 5);
                                    foreView.setFitWidth(foreScroll.getWidth() - 1);
                                } else {
                                    foreView.setFitHeight(-1);
                                    foreView.setFitWidth(-1);
                                }
                                foreTitle.setText(AppVaribles.getMessage("ForegroundImage") + " "
                                        + (int) foreImage.getWidth() + "*" + (int) foreImage.getHeight());
                                foreLabel.setText(fileName);
                                foreBox.setDisable(false);
                                afterImagesOpened();
                            }
                        });
                    } catch (Exception e) {
                        logger.error(e.toString());
                    }
                    return null;
                }
            };
            openHandlingStage(loadTask, Modality.WINDOW_MODAL);
            Thread thread = new Thread(loadTask);
            thread.setDaemon(true);
            thread.start();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    private void openForegroundImage(ActionEvent event) {
        if (foreFile != null) {
            openImageManufactureInNew(foreFile.getAbsolutePath());
        }
    }

    @FXML
    private void setForegroundPaneSize(ActionEvent event) {
        foreView.setFitHeight(foreScroll.getHeight() - 5);
        foreView.setFitWidth(foreScroll.getWidth() - 1);
    }

    @FXML
    private void setForegroundImageSize(ActionEvent event) {
        foreView.setFitHeight(-1);
        foreView.setFitWidth(-1);
    }

    @FXML
    private void selectBackgroundImage(ActionEvent event) {
        try {
            final FileChooser fileChooser = new FileChooser();
            File path = new File(AppVaribles.getConfigValue(sourcePathKey, CommonValues.UserFilePath));
            if (!path.isDirectory()) {
                path = new File(CommonValues.UserFilePath);
            }
            fileChooser.setInitialDirectory(path);
            fileChooser.getExtensionFilters().addAll(fileExtensionFilter);
            final File file = fileChooser.showOpenDialog(getMyStage());
            if (file == null) {
                return;
            }
            backFile = file;
            AppVaribles.setConfigValue(LastPathKey, backFile.getParent());
            AppVaribles.setConfigValue(sourcePathKey, backFile.getParent());

            final String fileName = file.getPath();
            Task loadTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        BufferedImage bufferImage = ImageIO.read(file);
                        backImage = SwingFXUtils.toFXImage(bufferImage, null);
                        backInfo = ImageFileReaders.readImageMetaData(fileName);
                        backInfo.setImage(backImage);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                backView.setPreserveRatio(true);
                                backView.setImage(backImage);
                                if (backScroll.getHeight() < backImage.getHeight()) {
                                    backView.setFitHeight(backScroll.getHeight() - 5);
                                    backView.setFitWidth(backScroll.getWidth() - 1);
                                } else {
                                    backView.setFitHeight(-1);
                                    backView.setFitWidth(-1);
                                }
                                backTitle.setText(AppVaribles.getMessage("BackgroundImage") + " "
                                        + (int) backImage.getWidth() + "*" + (int) backImage.getHeight());
                                backLabel.setText(fileName);
                                backBox.setDisable(false);
                                afterImagesOpened();
                            }
                        });
                    } catch (Exception e) {
                        logger.error(e.toString());
                    }
                    return null;
                }
            };
            openHandlingStage(loadTask, Modality.WINDOW_MODAL);
            Thread thread = new Thread(loadTask);
            thread.setDaemon(true);
            thread.start();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void afterImagesOpened() {
        if (foreImage != null && backImage != null) {
            targetBox.setDisable(false);
            blendImages();
        } else {
            targetBox.setDisable(true);
        }
    }

    @FXML
    private void openBackgroundImage(ActionEvent event) {
        if (backFile != null) {
            openImageManufactureInNew(backFile.getAbsolutePath());
        }
    }

    @FXML
    private void setBackgroundPaneSize(ActionEvent event) {
        backView.setFitHeight(backScroll.getHeight() - 5);
        backView.setFitWidth(backScroll.getWidth() - 1);
    }

    @FXML
    private void setBackgroundImageSize(ActionEvent event) {
        backView.setFitHeight(-1);
        backView.setFitWidth(-1);
    }

    @FXML
    private void saveAction(ActionEvent event) {
        if (image == null) {
            return;
        }
        try {
            final FileChooser fileChooser = new FileChooser();
            File path = new File(AppVaribles.getConfigValue(targetPathKey, CommonValues.UserFilePath));
            if (!path.isDirectory()) {
                path = new File(CommonValues.UserFilePath);
            }
            fileChooser.setInitialDirectory(path);
            fileChooser.getExtensionFilters().addAll(fileExtensionFilter);
            final File file = fileChooser.showSaveDialog(getMyStage());
            if (file == null) {
                return;
            }
            AppVaribles.setConfigValue(targetPathKey, file.getParent());
            targetFile = file;

            Task saveTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        String filename = targetFile.getAbsolutePath();
                        String format = FileTools.getFileSuffix(filename);
                        final BufferedImage bufferedImage = FxmlImageTools.getBufferedImage(image);
                        ImageFileWriters.writeImageFile(bufferedImage, format, filename);
                    } catch (Exception e) {
                        logger.error(e.toString());
                    }
                    return null;
                }
            };
            openHandlingStage(saveTask, Modality.WINDOW_MODAL);
            Thread thread = new Thread(saveTask);
            thread.setDaemon(true);
            thread.start();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    private void newWindow(ActionEvent event) {
        showImageView(image);
    }

    @FXML
    private void openTargetPath(ActionEvent event) {
        try {
            Desktop.getDesktop().browse(targetPath.toURI());
        } catch (Exception e) {

        }
    }

    @FXML
    private void foreClicked(MouseEvent event) {
        if (foreImage == null || location != ImagesRelativeLocation.Background_In_Foreground) {
            return;
        }
        foreView.setCursor(Cursor.HAND);

        int xv = (int) Math.round(event.getX() * foreImage.getWidth() / foreView.getBoundsInLocal().getWidth());
        int yv = (int) Math.round(event.getY() * foreImage.getHeight() / foreView.getBoundsInLocal().getHeight());

        pointX.setText(xv + "");
        pointY.setText(yv + "");
    }

    @FXML
    private void backClicked(MouseEvent event) {
        if (backImage == null || location != ImagesRelativeLocation.Foreground_In_Background) {
            return;
        }
        backView.setCursor(Cursor.HAND);

        int xv = (int) Math.round(event.getX() * backImage.getWidth() / backView.getBoundsInLocal().getWidth());
        int yv = (int) Math.round(event.getY() * backImage.getHeight() / backView.getBoundsInLocal().getHeight());

        pointX.setText(xv + "");
        pointY.setText(yv + "");
    }

    @FXML
    protected void imageClicked(MouseEvent event) {
        if (image == null) {
            return;
        }
        imageView.setCursor(Cursor.HAND);

        int xv = (int) Math.round(event.getX() * image.getWidth() / imageView.getBoundsInLocal().getWidth());
        int yv = (int) Math.round(event.getY() * image.getHeight() / imageView.getBoundsInLocal().getHeight());

        pointX.setText(xv + "");
        pointY.setText(yv + "");

    }

    private void blendImages() {
        if (!isAreaValid || foreImage == null || backImage == null
                || blendMode == null) {
            image = null;
            imageView.setImage(null);
            bottomLabel.setText("");
            return;
        }

        bottomLabel.setText(AppVaribles.getMessage("Loading..."));

        image = FxmlImageTools.blendImages(foreImage, backImage,
                location, x, y, intersectOnlyCheck.isSelected(), blendMode, opacity);
        if (image == null) {
            bottomLabel.setText("");
            return;
        }
        imageView.setImage(image);
        fitSize();
        bottomLabel.setText(AppVaribles.getMessage("BlendedSize") + ": "
                + (int) image.getWidth() + "*" + (int) image.getHeight());
    }

}
