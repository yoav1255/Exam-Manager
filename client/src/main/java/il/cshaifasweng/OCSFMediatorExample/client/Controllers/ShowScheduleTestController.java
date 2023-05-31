package il.cshaifasweng.OCSFMediatorExample.client.Controllers;

import il.cshaifasweng.OCSFMediatorExample.client.App;
import il.cshaifasweng.OCSFMediatorExample.client.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.entities.CustomMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.ScheduledTest;
import il.cshaifasweng.OCSFMediatorExample.server.Events.MoveIdToNextPageEvent;
import il.cshaifasweng.OCSFMediatorExample.server.Events.MoveManagerIdEvent;
import il.cshaifasweng.OCSFMediatorExample.server.Events.SelectedTestEvent;
import il.cshaifasweng.OCSFMediatorExample.server.Events.ShowScheduleTestEvent;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShowScheduleTestController {
    private String idTeacher;

    @FXML // fx:id="allStudentsBN"
    private Button allStudentsBN; // Value injected by FXMLLoader

    @FXML // fx:id="date"
    private TableColumn<ScheduledTest, String> date; // Value injected by FXMLLoader

    @FXML // fx:id="examFormId"
    private TableColumn<ScheduledTest, String> examFormId; // Value injected by FXMLLoader

    @FXML // fx:id="homeBN"
    private Button homeBN; // Value injected by FXMLLoader

    @FXML // fx:id="id"
    private TableColumn<ScheduledTest, String> id; // Value injected by FXMLLoader

    @FXML // fx:id="scheduleTestGP"
    private GridPane scheduleTestGP; // Value injected by FXMLLoader

    @FXML // fx:id="statusLB"
    private Label statusLB; // Value injected by FXMLLoader

    @FXML // fx:id="statusLB1"
    private Label statusLB1; // Value injected by FXMLLoader
    @FXML
    private Button btnNewTest;
    @FXML
    private CheckBox onlyMyTestCheckBox;

    private boolean onlyMyTest=false;
    @FXML // fx:id="students_table_view"
    private TableView<ScheduledTest> scheduleTest_table_view; // Value injected by FXMLLoader

    @FXML // fx:id="submission"
    private TableColumn<ScheduledTest, String> submission; // Value injected by FXMLLoader

    @FXML // fx:id="teacherId"
    private TableColumn<ScheduledTest, String> teacherId; // Value injected by FXMLLoader

    @FXML // fx:id="time"
    private TableColumn<ScheduledTest, String> time; // Value injected by FXMLLoader

    private List<ScheduledTest> scheduledTests;
    private boolean edit = false;
    private boolean showGrades = false;

    private boolean isManager;
    private String managerId;


    public String getId() {
        return this.idTeacher;
    }

    public void setId(String id) {
        this.idTeacher = id;
    }


    public ShowScheduleTestController() {
        EventBus.getDefault().register(this);
    }

    public void cleanup() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMoveIdToNextPageEvent(MoveIdToNextPageEvent event) {
        this.idTeacher = event.getId();
        Platform.runLater(()->{
            btnNewTest.setDisable(false);
            btnNewTest.setVisible(true);
        });
    }

    @Subscribe
    public void onMoveManagerIdEvent(MoveManagerIdEvent event) {
        isManager = true;
        managerId = event.getId();
        Platform.runLater(()->{
            btnNewTest.setDisable(true);
            btnNewTest.setVisible(false);
        });

    }

    public void setScheduledTests(List<ScheduledTest> scheduledTests) {
        this.scheduledTests = scheduledTests;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowScheduleTestEvent(ShowScheduleTestEvent event) {
        try {
            setScheduledTests(event.getScheduledTestList());
            id.setCellValueFactory(new PropertyValueFactory<ScheduledTest, String>("id"));
            date.setCellValueFactory(new PropertyValueFactory<ScheduledTest, String>("date"));
            time.setCellValueFactory(cellData -> {
                String formattedTime = cellData.getValue().getTime().toString();
                formattedTime = formattedTime.substring(0, 5);
                return new SimpleStringProperty(formattedTime);
            });
            submission.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduledTest, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduledTest, String> param) {

                        return new SimpleStringProperty(String.valueOf(param.getValue().getCheckedSubmissions())+"/"+String.valueOf(param.getValue().getSubmissions()));
                }
            });
            examFormId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduledTest, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduledTest, String> param) {
                    try {

                        return new SimpleStringProperty(param.getValue().getExamForm().getCode());
                    } catch (NullPointerException e) {
                        // Handle the exception here (e.g., set a default value)
                        return new SimpleStringProperty("N/A");
                    }
                }
            });
            teacherId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ScheduledTest, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ScheduledTest, String> param) {
                    try {
                        return new SimpleStringProperty(param.getValue().getTeacher().getId());
                    } catch (NullPointerException e) {
                        // Handle the exception here (e.g., set a default value)
                        return new SimpleStringProperty("N/A");
                    }
                }
            });
            ShowScheduleTest("ShowAllTests");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void ShowScheduleTest(String show) {
        ObservableList<ScheduledTest> scheduledTestObservableList = FXCollections.observableArrayList();

        if (show.equals("ShowAllTests")) {
            if (!onlyMyTest)
                scheduledTestObservableList = FXCollections.observableList(scheduledTests);
            else {
                for (ScheduledTest scheduledTest : scheduledTests) {
                    if (this.idTeacher.equals(scheduledTest.getTeacher().getId()))
                        scheduledTestObservableList.add(scheduledTest);
                }
            }
            this.edit = false;
            this.showGrades = false;
            scheduleTest_table_view.setItems(scheduledTestObservableList);
        } else {
            scheduledTestObservableList = FXCollections.observableArrayList();

            for (ScheduledTest scheduledTest : scheduledTests) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy0MM0dd");
                LocalDateTime now = LocalDateTime.now();
                String currentDate = scheduledTest.getDate().toString().replace('-', '0');
                String today = dtf.format(now);
                if (show.equals("ShowTestHasntPerformed")) {
                    if (Integer.parseInt(currentDate) > Integer.parseInt(today)) {
                        if (!onlyMyTest)
                            scheduledTestObservableList.add(scheduledTest);
                        else {
                            if (this.idTeacher.equals(scheduledTest.getTeacher().getId()))
                                scheduledTestObservableList.add(scheduledTest);
                        }
                    }
                    this.showGrades = false;
                    this.edit = true;
                } else if (show.equals("ShowTestPerformed")) {
                    if (Integer.parseInt(currentDate) <= Integer.parseInt(today)) {
                        if (!onlyMyTest)
                            scheduledTestObservableList.add(scheduledTest);
                        else {
                            if (this.idTeacher.equals(scheduledTest.getTeacher().getId()))
                                scheduledTestObservableList.add(scheduledTest);
                        }
                    }
                    this.showGrades = true;
                    this.edit = false;
                }
                scheduleTest_table_view.setItems(scheduledTestObservableList);

            }
        }
    }

    @FXML
    void showTestHasntPerformed(ActionEvent event) {
        ShowScheduleTest("ShowTestHasntPerformed");
    }

    @FXML
    void showTestPerformed(ActionEvent event) {
        ShowScheduleTest("ShowTestPerformed");
    }

    @FXML
    void showAllTest(ActionEvent event) {
        ShowScheduleTest("ShowAllTests");
    }
    @FXML
    void handleOnlyMyTest(ActionEvent event) {
    if (onlyMyTestCheckBox.isSelected())
        onlyMyTest=true;
    else
        onlyMyTest=false;
    }
    @FXML
    public void handleRowClick(MouseEvent event) {
        if (!isManager) {
            try {
                if (event.getClickCount() == 2 && scheduleTest_table_view.getSelectionModel().getSelectedItem() != null) { // Check if the user double-clicked the row
                    ScheduledTest selectedTest = scheduleTest_table_view.getSelectionModel().getSelectedItem();
                    if (this.idTeacher != null && this.idTeacher.equals(selectedTest.getTeacher().getId()) && edit == true) {
                        App.switchScreen("scheduledTest");
                        Platform.runLater(() -> {
                            try {
                                SimpleClient.getClient().sendToServer(new CustomMessage("#fillComboBox", idTeacher));
                                SimpleClient.getClient().sendToServer(new CustomMessage("#getTeacher", idTeacher));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            EventBus.getDefault().post(new MoveIdToNextPageEvent(idTeacher));
                            EventBus.getDefault().post(new SelectedTestEvent(selectedTest));
                        });

                    } else if (this.idTeacher != null && this.idTeacher.equals(selectedTest.getTeacher().getId()) && showGrades == true) {
                        App.switchScreen("testGrade");
                        Platform.runLater(() -> {
                            try {
                                SimpleClient.getClient().sendToServer(new CustomMessage("#getStudentTestsFromSchedule", selectedTest));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            EventBus.getDefault().post(new MoveIdToNextPageEvent(idTeacher));
                            EventBus.getDefault().post(new SelectedTestEvent(selectedTest));
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleGoToAllStudentsButtonClick(ActionEvent event) throws IOException {
        App.switchScreen("allStudents");
        Platform.runLater(() -> {
            try {
                SimpleClient.getClient().sendToServer(new CustomMessage("#showAllStudents", ""));
                EventBus.getDefault().post(new MoveIdToNextPageEvent(idTeacher));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void handleGoHomeButtonClick(ActionEvent event) throws IOException {
        if (!isManager) {
            App.switchScreen("teacherHome");
            Platform.runLater(() -> {
                try {
                    EventBus.getDefault().post(new MoveIdToNextPageEvent(idTeacher));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        else {
            App.switchScreen("managerHome");
            Platform.runLater(() -> {
                try {
                    EventBus.getDefault().post(new MoveManagerIdEvent(managerId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    public void goToScheduleNewTest(ActionEvent event) throws IOException {
        App.switchScreen("scheduledTest");
        Platform.runLater(() -> {
            try {
                SimpleClient.getClient().sendToServer(new CustomMessage("#scheduledTest", ""));
                SimpleClient.getClient().sendToServer(new CustomMessage("#fillComboBox", idTeacher));
                SimpleClient.getClient().sendToServer(new CustomMessage("#getTeacher", idTeacher));
                EventBus.getDefault().post(new MoveIdToNextPageEvent(idTeacher));
            } catch (Exception e) {

            }
        });
    }
}



