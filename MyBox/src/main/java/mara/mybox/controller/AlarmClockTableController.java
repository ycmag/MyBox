package mara.mybox.controller;

import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import mara.mybox.objects.AlarmClock;
import mara.mybox.objects.AppVaribles;
import mara.mybox.tools.DateTools;

/**
 * @Author Mara
 * @CreateDate 2018-7-13
 * @Description
 * @License Apache License Version 2.0
 */
public class AlarmClockTableController extends BaseController {

    protected AlarmClockController alarmClockController;
    protected ObservableList<AlarmClock> tableData = FXCollections.observableArrayList();

    @FXML
    private HBox alarmClocksPane;
    @FXML
    private Button clearButton, deleteButton, editButton, activeButton, inactiveButton;
    @FXML
    private TableView<AlarmClock> alarmClocksView;
    @FXML
    private TableColumn<AlarmClock, String> statusColumn, descriptionColumn, repeatColumn;
    @FXML
    private TableColumn<AlarmClock, String> nextTimeColumn, soundColumn, lastTimeColumn, startTimeColumn;

    @Override
    protected void initializeNext() {
        try {
            statusColumn.setCellValueFactory(new PropertyValueFactory<AlarmClock, String>("status"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<AlarmClock, String>("description"));
            repeatColumn.setCellValueFactory(new PropertyValueFactory<AlarmClock, String>("repeat"));
            nextTimeColumn.setCellValueFactory(new PropertyValueFactory<AlarmClock, String>("next"));
            soundColumn.setCellValueFactory(new PropertyValueFactory<AlarmClock, String>("sound"));
            lastTimeColumn.setCellValueFactory(new PropertyValueFactory<AlarmClock, String>("last"));
            startTimeColumn.setCellValueFactory(new PropertyValueFactory<AlarmClock, String>("start"));

            alarmClocksView.setItems(tableData);
            alarmClocksView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            tableData.addAll(AlarmClock.readAlarmClocks());

            tableData.addListener(new ListChangeListener() {
                @Override
                public void onChanged(ListChangeListener.Change change) {
                    if (tableData.size() > 0) {
                        clearButton.setDisable(false);
                    } else {
                        clearButton.setDisable(true);
                    }
                }
            });
            if (tableData.size() > 0) {
                clearButton.setDisable(false);
            } else {
                clearButton.setDisable(true);
            }

            alarmClocksView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    ObservableList<AlarmClock> selected = alarmClocksView.getSelectionModel().getSelectedItems();
                    if (selected != null && selected.size() > 0) {
                        editButton.setDisable(false);
                        deleteButton.setDisable(false);
                        activeButton.setDisable(true);
                        inactiveButton.setDisable(true);
                        for (AlarmClock alarm : selected) {
                            if (alarm.isIsActive()) {
                                inactiveButton.setDisable(false);
                            } else {
                                activeButton.setDisable(false);
                            }
                        }
                    } else {
                        editButton.setDisable(true);
                        deleteButton.setDisable(true);
                        activeButton.setDisable(true);
                        inactiveButton.setDisable(true);
                    }
                }
            });

            alarmClocksView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() > 1) {
                        ObservableList<AlarmClock> selected = alarmClocksView.getSelectionModel().getSelectedItems();
                        if (selected != null && selected.size() > 0) {
                            alarmClockController.edit(selected.get(0));
                        }
                    }
                }
            });

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    private void clearAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(getBaseTitle());
        alert.setContentText(AppVaribles.getMessage("SureClearAlarmClocks"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK) {
            return;
        }

        tableData.clear();
        deleteButton.setDisable(true);
        editButton.setDisable(true);
        activeButton.setDisable(true);
        inactiveButton.setDisable(true);
        AlarmClock.clearAllAlarmClocks();
    }

    @FXML
    private void deleteAction(ActionEvent event) {
        ObservableList<AlarmClock> selected = alarmClocksView.getSelectionModel().getSelectedItems();
        if (selected == null || selected.isEmpty()) {
            return;
        }
        AlarmClock.deleteAlarmClocks(selected);
        tableData.removeAll(selected);
    }

    @FXML
    private void editAction(ActionEvent event) {
        ObservableList<AlarmClock> selected = alarmClocksView.getSelectionModel().getSelectedItems();
        if (selected == null || selected.isEmpty()) {
            return;
        }
        alarmClockController.edit(selected.get(0));
    }

    @FXML
    private void activeAction(ActionEvent event) {
        ObservableList<AlarmClock> selected = alarmClocksView.getSelectionModel().getSelectedItems();
        if (selected == null || selected.isEmpty()) {
            return;
        }
        for (AlarmClock alarm : selected) {
            alarm.setIsActive(true);
            alarm.setStatus(AppVaribles.getMessage("Active"));
            AlarmClock.calculateNextTime(alarm);
            alarm.setNext(DateTools.datetimeToString(alarm.getNextTime()));
            AlarmClock.scehduleAlarmClock(alarm);

        }
        AlarmClock.writeAlarmClocks(selected);
        alarmClocksView.refresh();
        activeButton.setDisable(true);
        inactiveButton.setDisable(false);

    }

    @FXML
    private void inactiveAction(ActionEvent event) {
        ObservableList<AlarmClock> selected = alarmClocksView.getSelectionModel().getSelectedItems();
        if (selected == null || selected.isEmpty()) {
            return;
        }
        for (AlarmClock alarm : selected) {
            alarm.setIsActive(false);
            alarm.setStatus(AppVaribles.getMessage("Inactive"));
            alarm.setNextTime(-1);
            alarm.setNext("");
            AlarmClock.scehduleAlarmClock(alarm);
        }
        AlarmClock.writeAlarmClocks(selected);
        alarmClocksView.refresh();
        inactiveButton.setDisable(true);
        activeButton.setDisable(false);
    }

    @FXML
    public void refreshAction() {
        tableData.clear();
        tableData.addAll(AlarmClock.readAlarmClocks());

        if (tableData.size() > 0) {
            clearButton.setDisable(false);
        } else {
            clearButton.setDisable(true);
        }

    }

    public AlarmClockController getAlarmClockController() {
        return alarmClockController;
    }

    public void setAlarmClockController(AlarmClockController alarmClockController) {
        this.alarmClockController = alarmClockController;
    }

    public void saveAlarm(AlarmClock alarm, boolean isNew) {
        AlarmClock.setExtraValues(alarm);
        AlarmClock.writeAlarmClock(alarm);
        AlarmClock.scehduleAlarmClock(alarm);
        if (isNew) {
            tableData.add(alarm);
        } else {
            int i = AlarmClock.findAlarmIndex(tableData, alarm.getKey());
            if (i >= 0) {
                tableData.set(i, alarm);
//                alarmClocksView.refresh();
            } else {
                tableData.add(alarm);
            }
        }
    }

}
