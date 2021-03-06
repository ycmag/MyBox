/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mara.mybox.controller;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import mara.mybox.objects.AppVaribles;
import mara.mybox.objects.ImageFileInformation;

/**
 * @Author Mara
 * @CreateDate 2018-6-20
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageInformationController extends BaseController {

    @FXML
    private TextField FilesPath;
    @FXML
    private TextField FileName;
    @FXML
    private TextField FileSize;
    @FXML
    private TextField CreateTime;
    @FXML
    private TextField ModifyTime;
    @FXML
    private TextField ImageFormat;
    @FXML
    private TextField xPixels;
    @FXML
    private TextField yPixels;
    @FXML
    private TextField xDensity;
    @FXML
    private TextField yDensity;
    @FXML
    private TextField xSize;
    @FXML
    private TextField ySize;
    @FXML
    private TextField ColorSpace;
    @FXML
    private TextField ColorChannels;
    @FXML
    private TextField BitDepth;
    @FXML
    private TextField AlphaChannel;
    @FXML
    private TextField compressionType;
    @FXML
    private TextField lossless;
    @FXML
    private TextField ImageOrientation;

    @FXML
    private void closeStage(MouseEvent event) {
        try {
            closeStage();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public void loadInformation(ImageFileInformation info) {
        try {
            File file = info.getFile();
            FilesPath.setText(file.getParent());
            FileName.setText(file.getName());
            CreateTime.setText(info.getCreateTime());
            ModifyTime.setText(info.getModifyTime());
            FileSize.setText(info.getFileSize());
            xPixels.setText(info.getxPixels() + "");
            yPixels.setText(info.getyPixels() + "");
            if (info.getxDensity() > 0) {
                xDensity.setText(info.getxDensity() + " dpi");
                float xinch = info.getxPixels() / info.getxDensity();
                xSize.setText(xinch + " " + AppVaribles.getMessage("inches")
                        + " = " + (xinch * 2.54) + " " + AppVaribles.getMessage("centimetres"));
            }
            if (info.getyDensity() > 0) {
                yDensity.setText(info.getyDensity() + " dpi");
                float yinch = info.getyPixels() / info.getyDensity();
                ySize.setText(yinch + " " + AppVaribles.getMessage("inches")
                        + " = " + (yinch * 2.54) + " " + AppVaribles.getMessage("centimetres"));
            }
            ColorSpace.setText(info.getColorSpace());
            ColorChannels.setText(info.getColorChannels() + "");
            BitDepth.setText(info.getBitDepth() + "");
            AlphaChannel.setText(AppVaribles.getMessage(info.isHasAlpha() + ""));
            compressionType.setText(info.getCompressionType());
            lossless.setText(AppVaribles.getMessage(info.isIsLossless() + ""));
            ImageOrientation.setText(info.getImageRotation());
            ImageFormat.setText(info.getImageFormat() + " " + info.getExtraFormat());

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }
}
