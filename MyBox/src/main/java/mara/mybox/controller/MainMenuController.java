package mara.mybox.controller;

import java.io.File;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mara.mybox.objects.AppVaribles;
import mara.mybox.objects.CommonValues;
import static mara.mybox.controller.BaseController.logger;
import mara.mybox.db.DerbyBase;
import static mara.mybox.objects.AppVaribles.getConfigValue;
import mara.mybox.fxml.FxmlTools;
import static mara.mybox.objects.CommonValues.UserFilePath;

/**
 * @Author Mara
 * @CreateDate 2018-6-20
 * @Description
 * @License Apache License Version 2.0
 */
public class MainMenuController extends BaseController {

    @FXML
    private Pane mainMenuPane;
    @FXML
    private RadioMenuItem chineseMenuItem, englishMenuItem;
    @FXML
    private RadioMenuItem replaceWhiteMenu, replaceBlackMenu, pdf500mbRadio, pdf1gbRadio, pdf2gbRadio, pdfUnlimitRadio;
    @FXML
    private CheckMenuItem showCommentsCheck, stopAlarmCheck;
    @FXML
    private Menu settingsMenu;

    @Override
    protected void initializeNext() {
        try {

            settingsMenu.setOnShowing(new EventHandler<Event>() {
                @Override
                public void handle(Event e) {
                    checkSettings();
                }
            });
            checkSettings();

        } catch (Exception e) {
            logger.debug(e.toString());
        }
    }

    private void checkSettings() {
        checkLanguage();
        checkAlpha();
        checkPdfMem();
        stopAlarmCheck.setSelected(AppVaribles.getConfigBoolean("StopAlarmsWhenExit"));
        showCommentsCheck.setSelected(AppVaribles.isShowComments());
    }

    protected void checkLanguage() {
        if (AppVaribles.CurrentBundle == CommonValues.BundleZhCN) {
            chineseMenuItem.setSelected(true);
        } else {
            englishMenuItem.setSelected(true);
        }
    }

    protected void checkAlpha() {
        if (AppVaribles.isAlphaAsBlack()) {
            replaceBlackMenu.setSelected(true);
        } else {
            replaceWhiteMenu.setSelected(true);
        }
    }

    protected void checkPdfMem() {
        String pm = getConfigValue("PdfMemDefault", "1GB");
        switch (pm) {
            case "1GB":
                pdf1gbRadio.setSelected(true);
                break;
            case "2GB":
                pdf2gbRadio.setSelected(true);
                break;
            case "Unlimit":
                pdfUnlimitRadio.setSelected(true);
                break;
            case "500MB":
            default:
                pdf500mbRadio.setSelected(true);
        }
    }

    @FXML
    protected void showHome(ActionEvent event) {
        openStage(CommonValues.MyboxFxml, false, true);
    }

    @FXML
    protected void setChinese(ActionEvent event) {
        AppVaribles.setLanguage("zh");
        if (parentFxml.contains("ImageManufacture") && !parentFxml.contains("ImageManufactureBatch")) {
            reloadStage(CommonValues.ImageManufactureFileFxml, getParentController().getMyStage().getTitle());
        } else {
            reloadStage(parentFxml, getParentController().getMyStage().getTitle());
        }
    }

    @FXML
    protected void setEnglish(ActionEvent event) {
        AppVaribles.setLanguage("en");
        if (parentFxml.contains("ImageManufacture") && !parentFxml.contains("ImageManufactureBatch")) {
            reloadStage(CommonValues.ImageManufactureFileFxml, getParentController().getMyStage().getTitle());
        } else {
            reloadStage(parentFxml, getParentController().getMyStage().getTitle());
        }
    }

    @FXML
    protected void setStopAlarm(ActionEvent event) {
        AppVaribles.setConfigValue("StopAlarmsWhenExit", stopAlarmCheck.isSelected());
    }

    @FXML
    protected void showCommentsAction() {
        checkShowComments();
    }

    public void checkShowComments() {
        boolean v = showCommentsCheck.isSelected();
        AppVaribles.setConfigValue("ShowComments", v);
        if (v) {
            popInformation(AppVaribles.getMessage("CommentsShown"));
        } else {
            popInformation(AppVaribles.getMessage("CommentsHidden"));
        }
    }

    @FXML
    protected void replaceWhiteAction(ActionEvent event) {
        AppVaribles.setConfigValue("AlphaAsBlack", false);
    }

    @FXML
    protected void replaceBlackAction(ActionEvent event) {
        AppVaribles.setConfigValue("AlphaAsBlack", true);
    }

    @FXML
    protected void PdfMem500MB(ActionEvent event) {
        AppVaribles.setPdfMem("500MB");
    }

    @FXML
    protected void PdfMem1GB(ActionEvent event) {
        AppVaribles.setPdfMem("1GB");
    }

    @FXML
    protected void PdfMem2GB(ActionEvent event) {
        AppVaribles.setPdfMem("2GB");
    }

    @FXML
    protected void pdfMemUnlimit(ActionEvent event) {
        AppVaribles.setPdfMem("Unlimit");
    }

    @FXML
    protected void clearSettings(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(getBaseTitle());
        alert.setContentText(AppVaribles.getMessage("SureClear"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK) {
            return;
        }
        DerbyBase.dropTables();
        DerbyBase.initTables();
        String f = getParentController().getMyFxml();
        if (f.contains("ImageManufacture") && !f.contains("ImageManufactureBatch")) {
            f = CommonValues.ImageManufactureFileFxml;
        }
        BaseController c = reloadStage(f, getParentController().getMyStage().getTitle());
        popInformation(AppVaribles.getMessage("Successful"));
    }

    @FXML
    protected void setDefaultStyle(ActionEvent event) {
        setInterfaceStyle(CommonValues.DefaultStyle);
    }

    @FXML
    protected void setWhiteOnBlackStyle(ActionEvent event) {
        setInterfaceStyle(CommonValues.WhiteOnBlackStyle);
    }

    @FXML
    protected void setYellowOnBlackStyle(ActionEvent event) {
        setInterfaceStyle(CommonValues.YellowOnBlackStyle);
    }

    @FXML
    protected void setWhiteOnGreenStyle(ActionEvent event) {
        setInterfaceStyle(CommonValues.WhiteOnGreenStyle);
    }

    @FXML
    protected void setCaspianStyle(ActionEvent event) {
        setInterfaceStyle(CommonValues.caspianStyle);
    }

    @FXML
    protected void setGreenOnBlackStyle(ActionEvent event) {
        setInterfaceStyle(CommonValues.GreenOnBlackStyle);
    }

    @FXML
    protected void setPinkOnBlackStyle(ActionEvent event) {
        setInterfaceStyle(CommonValues.PinkOnBlackStyle);
    }

    @FXML
    protected void setBlackOnYellowStyle(ActionEvent event) {
        setInterfaceStyle(CommonValues.BlackOnYellowStyle);
    }

    @FXML
    protected void setWhiteOnPurpleStyle(ActionEvent event) {
        setInterfaceStyle(CommonValues.WhiteOnPurpleStyle);
    }

    @FXML
    protected void setWhiteOnBlueStyle(ActionEvent event) {
        setInterfaceStyle(CommonValues.WhiteOnBlueStyle);
    }

    @Override
    public void setInterfaceStyle(String style) {
        try {
            AppVaribles.setConfigValue("InterfaceStyle", style);
            if (parentController != null) {
                parentController.setInterfaceStyle(style);
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    private void exit(ActionEvent event) {
        // This statement is internel call to close the stage, so itself can not tigger stageClosing()
        closeStage();
    }

    @FXML
    private void openPdfConvertImages(ActionEvent event) {
        reloadStage(CommonValues.PdfConvertImagesFxml, AppVaribles.getMessage("PdfConvertImages"));
    }

    @FXML
    private void openPdfConvertImagesBatch(ActionEvent event) {
        reloadStage(CommonValues.PdfConvertImagesBatchFxml, AppVaribles.getMessage("PdfConvertImagesBatch"));
    }

    @FXML
    private void openImagesCombinePdf(ActionEvent event) {
        reloadStage(CommonValues.ImagesCombinePdfFxml, AppVaribles.getMessage("ImagesCombinePdf"));
    }

    @FXML
    private void openPdfExtractImages(ActionEvent event) {
        reloadStage(CommonValues.PdfExtractImagesFxml, AppVaribles.getMessage("PdfExtractImages"));
    }

    @FXML
    private void openPdfExtractTexts(ActionEvent event) {
        reloadStage(CommonValues.PdfExtractTextsFxml, AppVaribles.getMessage("PdfExtractTexts"));
    }

    @FXML
    private void openPdfExtractTextsBatch(ActionEvent event) {
        reloadStage(CommonValues.PdfExtractTextsBatchFxml, AppVaribles.getMessage("PdfExtractTextsBatch"));
    }

    @FXML
    private void openPdfExtractImagesBatch(ActionEvent event) {
        reloadStage(CommonValues.PdfExtractImagesBatchFxml, AppVaribles.getMessage("PdfExtractImagesBatch"));
    }

    @FXML
    private void openMergePdf(ActionEvent event) {
        reloadStage(CommonValues.PdfMergeFxml, AppVaribles.getMessage("MergePdf"));
    }

    @FXML
    private void openSplitPdf(ActionEvent event) {
        reloadStage(CommonValues.PdfSplitFxml, AppVaribles.getMessage("SplitPdf"));
    }

    @FXML
    private void openCompressPdfImages(ActionEvent event) {
        reloadStage(CommonValues.PdfCompressImagesFxml, AppVaribles.getMessage("CompressPdfImages"));
    }

    @FXML
    private void openCompressPdfImagesBatch(ActionEvent event) {
        reloadStage(CommonValues.PdfCompressImagesBatchFxml, AppVaribles.getMessage("CompressPdfImagesBatch"));
    }

    @FXML
    private void openImageViewer(ActionEvent event) {
        reloadStage(CommonValues.ImageViewerFxml, AppVaribles.getMessage("ImageViewer"));
    }

    @FXML
    private void openMultipleImagesViewer(ActionEvent event) {
        reloadStage(CommonValues.ImagesViewerFxml, AppVaribles.getMessage("MultipleImagesViewer"));
    }

    @FXML
    private void openImageConverter(ActionEvent event) {
        reloadStage(CommonValues.ImageConverterFxml, AppVaribles.getMessage("ImageConverter"));
    }

    @FXML
    private void openImageConverterBatch(ActionEvent event) {
        reloadStage(CommonValues.ImageConverterBatchFxml, AppVaribles.getMessage("ImageConverterBatch"));
    }

    @FXML
    private void openImageManufacture(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
    }

    @FXML
    private void openImageManufactureSize(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("size");
    }

    @FXML
    private void openImageManufactureCrop(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("crop");
    }

    @FXML
    private void openImageManufactureColor(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("color");
    }

    @FXML
    private void openImageManufactureEffects(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("effects");
    }

    @FXML
    private void openImageManufactureFilters(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("filters");
    }

    @FXML
    private void openImageManufactureConvolution(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("convolution");
    }

    @FXML
    private void openImageManufactureReplaceColor(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("replaceColor");
    }

    @FXML
    private void openImageManufactureText(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("text");
    }

    @FXML
    private void openImageManufactureCover(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("cover");
    }

    @FXML
    private void openImageManufactureArc(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("arc");
    }

    @FXML
    private void openImageManufactureShadow(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("shadow");
    }

    @FXML
    private void openImageManufactureTransform(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("transform");
    }

    @FXML
    private void openImageManufactureMargins(ActionEvent event) {
        ImageManufactureFileController controller
                = (ImageManufactureFileController) reloadStage(CommonValues.ImageManufactureFileFxml, AppVaribles.getMessage("ImageManufacture"));
        controller.setInitTab("margins");
    }

    @FXML
    private void openImageManufactureBatch(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchFxml, AppVaribles.getMessage("ImageManufactureBatch"));
    }

    @FXML
    private void openImageManufactureBatchSize(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchSizeFxml, AppVaribles.getMessage("ImageManufactureBatchSize"));
    }

    @FXML
    private void openImageManufactureBatchCrop(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchCropFxml, AppVaribles.getMessage("ImageManufactureBatchCrop"));
    }

    @FXML
    private void openImageManufactureBatchColor(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchColorFxml, AppVaribles.getMessage("ImageManufactureBatchColor"));
    }

    @FXML
    private void openImageManufactureBatchEffects(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchEffectsFxml, AppVaribles.getMessage("ImageManufactureBatchEffects"));
    }

    @FXML
    private void openImageManufactureBatchFilters(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchFiltersFxml, AppVaribles.getMessage("ImageManufactureBatchFilters"));
    }

    @FXML
    private void openImageManufactureBatchConvolution(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchConvolutionFxml, AppVaribles.getMessage("ImageManufactureBatchConvolution"));
    }

    @FXML
    private void openImageManufactureBatchReplaceColor(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchReplaceColorFxml, AppVaribles.getMessage("ImageManufactureBatchReplaceColor"));
    }

    @FXML
    private void openImageManufactureBatchText(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchTextFxml, AppVaribles.getMessage("ImageManufactureBatchText"));
    }

    @FXML
    private void openImageManufactureBatchArc(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchArcFxml, AppVaribles.getMessage("ImageManufactureBatchArc"));
    }

    @FXML
    private void openImageManufactureBatchShadow(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchShadowFxml, AppVaribles.getMessage("ImageManufactureBatchShadow"));
    }

    @FXML
    private void openImageManufactureBatchTransform(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchTransformFxml, AppVaribles.getMessage("ImageManufactureBatchTransform"));
    }

    @FXML
    private void openImageManufactureBatchAddMargins(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchAddMarginsFxml, AppVaribles.getMessage("ImageManufactureBatchAddMargins"));
    }

    @FXML
    private void openImageManufactureBatchCutMargins(ActionEvent event) {
        reloadStage(CommonValues.ImageManufactureBatchCutMarginsFxml, AppVaribles.getMessage("ImageManufactureBatchCutMargins"));
    }

    @FXML
    private void openImageSplit(ActionEvent event) {
        reloadStage(CommonValues.ImageSplitFxml, AppVaribles.getMessage("ImageSplit"));
    }

    @FXML
    private void openImagesCombine(ActionEvent event) {
        reloadStage(CommonValues.ImagesCombineFxml, AppVaribles.getMessage("ImageCombine"));
    }

    @FXML
    private void openImagesBlend(ActionEvent event) {
        reloadStage(CommonValues.ImagesBlendFxml, AppVaribles.getMessage("ImagesBlend"));
    }

    @FXML
    private void openConvolutionKernelManager(ActionEvent event) {
        reloadStage(CommonValues.ConvolutionKernelManagerFxml, AppVaribles.getMessage("ConvolutionKernelManager"));
    }

    @FXML
    private void openColorPalette(ActionEvent event) {
        openStage(CommonValues.ColorPaletteFxml, AppVaribles.getMessage("ColorPalette"), false, false);
    }

    @FXML
    private void openPixelsCalculator(ActionEvent event) {
        openStage(CommonValues.PixelsCalculatorFxml, AppVaribles.getMessage("PixelsCalculator"), false, false);
    }

    @FXML
    private void openFilesRename(ActionEvent event) {
        reloadStage(CommonValues.FilesRenameFxml, AppVaribles.getMessage("FilesRename"));
    }

    @FXML
    private void openDirsRename(ActionEvent event) {
        reloadStage(CommonValues.DirectoriesRenameFxml, AppVaribles.getMessage("DirectoriesRename"));
    }

    @FXML
    private void openDirectorySynchronize(ActionEvent event) {
        reloadStage(CommonValues.DirectorySynchronizeFxml, AppVaribles.getMessage("DirectorySynchronize"));
    }

    @FXML
    private void openFilesArrangement(ActionEvent event) {
        reloadStage(CommonValues.FilesArrangementFxml, AppVaribles.getMessage("FilesArrangement"));
    }

    @FXML
    private void openAlarmClock(ActionEvent event) {
        reloadStage(CommonValues.AlarmClockFxml, AppVaribles.getMessage("AlarmClock"));
    }

    @FXML
    private void openHtmlEditor(ActionEvent event) {
        reloadStage(CommonValues.HtmlEditorFxml, AppVaribles.getMessage("HtmlEditor"));
    }

    @FXML
    private void openHtmlEditor2(ActionEvent event) {
        HtmlEditorController controller
                = (HtmlEditorController) reloadStage(CommonValues.HtmlEditorFxml, AppVaribles.getMessage("HtmlEditor"));
//        controller.switchBroswerTab();
    }

    @FXML
    private void openTextEditor(ActionEvent event) {
        reloadStage(CommonValues.TextEditorFxml, AppVaribles.getMessage("TextEditor"));
    }

    @FXML
    private void openWeiboSnap(ActionEvent event) {
        WeiboSnapController controller
                = (WeiboSnapController) reloadStage(CommonValues.WeiboSnapFxml, AppVaribles.getMessage("WeiboSnap"));
    }

    @FXML
    private void showHelp(ActionEvent event) {
        try {
            String lang = AppVaribles.getLanguage();
            Boolean updated = AppVaribles.getConfigBoolean("UpdatedHelps4.2", false);
            if (!updated) {
                logger.debug("Updating Helps 4.2");
                File file = new File(UserFilePath);
                if (file.exists()) {
                    File[] files = file.listFiles();
                    for (File f : files) {
                        if (f.getAbsolutePath().endsWith(".html")) {
                            f.delete();
                        }
                    }
                }
                AppVaribles.setConfigValue("UpdatedHelps4.2", true);
            }
            File mybox_help = FxmlTools.getUserFile(getClass(),
                    "/docs/mybox_help_" + lang + ".html", "mybox_help_" + lang + ".html", !updated);
            FxmlTools.getUserFile(getClass(),
                    "/docs/mybox_help_nav_" + lang + ".html", "mybox_help_nav_" + lang + ".html", !updated);
            FxmlTools.getUserFile(getClass(),
                    "/docs/mybox_help_main_" + lang + ".html", "mybox_help_main_" + lang + ".html", !updated);
            HtmlEditorController controller
                    = (HtmlEditorController) openStage(CommonValues.HtmlEditorFxml, false);
            controller.switchBroswerTab();
            controller.loadHtml(mybox_help);
//            Desktop.getDesktop().browse(mybox_help.toURI());
        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    @FXML
    private void showAbout(ActionEvent event) {
        openStage(CommonValues.AboutFxml, true);
    }

    @FXML
    private void settingsAction(ActionEvent event) {
        BaseController c = openStage(CommonValues.SettingsFxml, true);
        c.setParentController(parentController);
        c.setParentFxml(parentFxml);
    }

    @Override
    public Stage getMyStage() {
        if (myStage == null) {
            if (mainMenuPane != null && mainMenuPane.getScene() != null) {
                myStage = (Stage) mainMenuPane.getScene().getWindow();
            }
        }
        return myStage;
    }

    @Override
    public boolean stageReloading() {
        try {
//            logger.debug("stageReloading");

            return parentController.stageClosing();

        } catch (Exception e) {
            return false;
        }

    }

    public CheckMenuItem getShowCommentsCheck() {
        return showCommentsCheck;
    }

    public void setShowCommentsCheck(CheckMenuItem showCommentsCheck) {
        this.showCommentsCheck = showCommentsCheck;
    }

}
