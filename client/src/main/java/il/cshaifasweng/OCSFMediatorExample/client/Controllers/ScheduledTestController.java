/**
 * Sample Skeleton for 'scheduledTest.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client.Controllers;

import il.cshaifasweng.OCSFMediatorExample.client.App;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.Events.*;
import il.cshaifasweng.OCSFMediatorExample.client.SimpleClient;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalDate;
import java.util.regex.*;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Date;

public class ScheduledTestController {
    private String id;

    private ExamForm examForm;

    private Teacher teacher;
    private ScheduledTest selectedTest;
    @FXML // fx:id="allStudentsBN"
    private Button allStudentsBN; // Value injected by FXMLLoader

    @FXML // fx:id="buttonScheduleTest"
    private Button buttonScheduleTest; // Value injected by FXMLLoader

    @FXML // fx:id="comboBoxExamForm"
    private ComboBox<String> comboBoxExamForm; // Value injected by FXMLLoader

    @FXML // fx:id="dataTimescheduleDate"
    private DatePicker dataTimescheduleDate; // Value injected by FXMLLoader

    @FXML // fx:id="labelTeacher"
    private Label labelTeacher; // Value injected by FXMLLoader

    @FXML // fx:id="scheduleTime"
    private TextField scheduleTime; // Value injected by FXMLLoader

    @FXML // fx:id="homeBN"
    private Button homeBN; // Value injected by FXMLLoader

    @FXML // fx:id="sendSchedule"
    private Button sendSchedule; // Value injected by FXMLLoader

    @FXML // fx:id="statusLB"
    private Label statusLB; // Value injected by FXMLLoader

    @FXML
    private TextField textFieldsubmission;

    @FXML
    private TextField scheduleCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //    public ScheduledTest getSelectedTest(){return selectedTest;}
    public void setSelectedTest(ScheduledTest selectedTest) {
        this.selectedTest = selectedTest;
    }

    public ScheduledTestController() {
        EventBus.getDefault().register(this);

    }

    public void cleanup() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onTeacherFromIdEvent(TeacherFromIdEvent event){
        teacher=event.getTeacherFromId();
    }
    @Subscribe
    public void onExamFormEvent(ExamFormEvent event) {
        List<String> examFormList = event.getExamFormEventCode();
        comboBoxExamForm.setItems(FXCollections.observableArrayList(examFormList));
    }
    @Subscribe
    public void onMoveIdToNextPageEvent(MoveIdToNextPageEvent event) throws IOException {
        setId(event.getId());
        scheduleTime.setText("12:00");
    }

    @Subscribe
    public void onSelectedTestEvent(SelectedTestEvent event) throws IOException {
        setSelectedTest(event.getSelectedTestEvent());
        scheduleCode.setText(selectedTest.getId());
        scheduleCode.setEditable(false);
        scheduleCode.setStyle("-fx-background-color: grey;");
        labelTeacher.setText(selectedTest.getTeacher().getId());
        scheduleTime.setText(selectedTest.getTime().toString().substring(0, 5));
        textFieldsubmission.setText(String.valueOf(selectedTest.getSubmissions()));
        dataTimescheduleDate.setValue(selectedTest.getDate());
        try {
            comboBoxExamForm.setValue(selectedTest.getExamForm().getCode());
        } catch (NullPointerException e) {
            // Handle the exception here (e.g., set a default value)
            comboBoxExamForm.setValue("");
        }
    }


    @FXML
    void comboAction(ActionEvent event) {
        if (comboBoxExamForm.getValue() != null) {
            Platform.runLater(()->{
                try {
                    SimpleClient.getClient().sendToServer(new CustomMessage("#sendExamFormId", comboBoxExamForm.getValue()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Subscribe
    public void onScheduledTestEvent(ScheduledTestEvent event) {
        this.examForm = (ExamForm) event.getScheduledTestEvent();

    }

    public boolean validateDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy0MM0dd");
        LocalDateTime now = LocalDateTime.now();
        if (dataTimescheduleDate.getValue() != null) {
            String currentDate = dataTimescheduleDate.getValue().toString().replace('-', '0');
            String today = dtf.format(now);
            if (Integer.parseInt(currentDate) > Integer.parseInt(today))
                return true;
        }
        dataTimescheduleDate.setStyle("-fx-border-color: #cc0000;");
        return false;
    }

    public boolean validateTime() {
        String pattern = "^([01]\\d|2[0-3]):[0-5]\\d$";
        // Check if the time matches the pattern
        if (scheduleTime != null) {
            if (Pattern.matches(pattern, scheduleTime.getText())) {
                if (Integer.parseInt(scheduleTime.getText().split(":")[0]) < 24 && Integer.parseInt(scheduleTime.getText().split(":")[1]) < 60) {
                    return true;
                }
            }

        }
        scheduleTime.setStyle("-fx-border-color:#cc0000;");
        return false;
    }

    public boolean validatesubmission() {
        String sumbissionPattern = "\\d+";
        if (textFieldsubmission != null) {
            if (Pattern.matches(sumbissionPattern, textFieldsubmission.getText())) {
                return true;
            }
        }
        textFieldsubmission.setStyle("-fx-border-color:#cc0000;");
        return false;
    }

    public boolean validateCode() {
        String patternCode = "[a-zA-Z0-9]{4}";
        if (scheduleCode != null) {
            if (Pattern.matches(patternCode, scheduleCode.getText())) {
                return true;
            }
        }
        scheduleCode.setStyle("-fx-border-color: #cc0000;");
        return false;
    }

    public boolean validateSchesuledForm() {
        boolean valid;
        dataTimescheduleDate.setStyle("-fx-border-color:default;");
        scheduleTime.setStyle("-fx-border-color:default;");
        textFieldsubmission.setStyle("-fx-border-color:default;");
        comboBoxExamForm.setStyle("-fx-border-color:default;");
        scheduleCode.setStyle("-fx-border-color:default;");
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("ERROR");
        valid = validateDate() & validateTime() & validatesubmission() & validateCode();
        if (comboBoxExamForm.getValue() == null) {
            comboBoxExamForm.setStyle("-fx-border-color:#cc0000;");
            valid = false;
        }
        if (!valid) {
            errorAlert.setContentText("input not valid!");
            errorAlert.show();
            return false;
        }
        return true;
    }


    @FXML
    void sendSchedule(ActionEvent event) {
        boolean valid = validateSchesuledForm();
        int day;
        int month;
        int year;
        String time;
        if (valid) {
            day = dataTimescheduleDate.getValue().getDayOfMonth();
            month = dataTimescheduleDate.getValue().getMonthValue();
            year = dataTimescheduleDate.getValue().getYear();
            time = scheduleTime.getText();
            if (selectedTest != null) {
                selectedTest.setDate(LocalDate.of(year, month, day));
                selectedTest.setTime(new Time(Integer.parseInt(time.substring(0, 2)), Integer.parseInt(time.substring(3, 5)), 0));
                selectedTest.setSubmissions(Integer.parseInt(textFieldsubmission.getText()));
                selectedTest.setExamForm(examForm);
                selectedTest.setTeacher(teacher);
                ScheduledTest scheduledTest1 = selectedTest;
                try {
                    SimpleClient.getClient().sendToServer(new CustomMessage("#updateScheduleTest", scheduledTest1));
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setHeaderText("Success");
                    success.setContentText("update schedule test Succeed");
                    success.show();
                    App.switchScreen("showScheduleTest");
                    Platform.runLater(()->{
                        try {
                            EventBus.getDefault().post(new MoveIdToNextPageEvent(id));
                            SimpleClient.getClient().sendToServer(new CustomMessage("#showScheduleTest",""));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    cleanup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ScheduledTest scheduledTest = new ScheduledTest(scheduleCode.getText(), LocalDate.of(year, month, day), new Time(Integer.parseInt(time.substring(0, 2)), Integer.parseInt(time.substring(3, 5)), 0), Integer.parseInt(textFieldsubmission.getText()));
                scheduledTest.setExamForm(examForm);
                scheduledTest.setTeacher(teacher);

                try {
                    SimpleClient.getClient().sendToServer(new CustomMessage("#addScheduleTest", scheduledTest));
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setHeaderText("Success");
                    success.setContentText("added new schedule test Succeed");
                    success.show();
                    App.switchScreen("showScheduleTest");
                    Platform.runLater(()->{
                        try {
                            EventBus.getDefault().post(new MoveIdToNextPageEvent(id));
                            SimpleClient.getClient().sendToServer(new CustomMessage("#showScheduleTest",""));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    cleanup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @FXML
    void handleGoHomeButtonClick(ActionEvent event) throws IOException {
            il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen("teacherHome");
            Platform.runLater(()->{
                EventBus.getDefault().post(new MoveIdToNextPageEvent(id));

            });
            cleanup();
    }

    @FXML
    void handleGoToAllStudentsButtonClick(ActionEvent event) throws IOException {
        App.switchScreen("allStudents");
            Platform.runLater(()->{
                try {
                    SimpleClient.getClient().sendToServer("#showAllStudents");
                    EventBus.getDefault().post(new MoveIdToNextPageEvent(id));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
            cleanup();

    }

}
