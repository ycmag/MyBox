package mara.mybox.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import static mara.mybox.controller.BaseController.logger;
import mara.mybox.objects.AppVaribles;
import mara.mybox.objects.CommonValues;
import static mara.mybox.fxml.FxmlTools.badStyle;

/**
 * @Author Mara
 * @CreateDate 2018-6-24
 * @Description
 * @License Apache License Version 2.0
 */
public class PdfExtractImagesBatchController extends PdfExtractImagesController {

    @FXML
    private ScrollPane scrollPane;

    @Override
    protected void initializeNext2() {
        try {

            appendPageNumber.setSelected(AppVaribles.getConfigBoolean("pei_appendPageNumber"));
            appendIndex.setSelected(AppVaribles.getConfigBoolean("pei_appendIndex"));

            operationBarController.startButton.disableProperty().bind(
                    Bindings.isEmpty(filesTableController.getFilesTableView().getItems())
                            .or(Bindings.isEmpty(targetSelectionController.targetPathInput.textProperty()))
                            .or(targetSelectionController.targetPathInput.styleProperty().isEqualTo(badStyle))
            );

            previewButton.disableProperty().bind(
                    operationBarController.startButton.disableProperty()
                            .or(operationBarController.startButton.textProperty().isNotEqualTo(AppVaribles.getMessage("Start")))
            );

            if (targetSelectionController.targetPathInput != null && targetSelectionController.targetPathInput.getText().isEmpty()) {
                targetSelectionController.targetPathInput.setText(AppVaribles.getConfigValue("pdfTargetPath", CommonValues.UserFilePath));
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    protected void makeMoreParameters() {
        makeBatchParameters();
    }

}
