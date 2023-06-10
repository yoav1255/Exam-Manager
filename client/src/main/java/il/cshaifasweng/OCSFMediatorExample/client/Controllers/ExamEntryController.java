package il.cshaifasweng.OCSFMediatorExample.client.Controllers;

import il.cshaifasweng.OCSFMediatorExample.client.App;
import il.cshaifasweng.OCSFMediatorExample.client.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.entities.CustomMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.ScheduledTest;
import il.cshaifasweng.OCSFMediatorExample.server.Events.MoveIdToNextPageEvent;
import il.cshaifasweng.OCSFMediatorExample.server.Events.MoveObjectToNextPageEvent;
import il.cshaifasweng.OCSFMediatorExample.server.Events.ShowScheduleTestEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExamEntryController {
    @FXML
    private Label msg;

    @FXML
    private TextField text_id;

    @FXML
    private TextField text_testCode;
    private String id;
    private List<String> scheduleTestIds;
    private List<ScheduledTest> scheduledTests;
    public ExamEntryController() {
        EventBus.getDefault().register(this);
        scheduleTestIds = new ArrayList<>();
        Platform.runLater(()->{
            msg.setVisible(false);
        });
    }
    public void cleanup(){
        EventBus.getDefault().unregister(this);
    }

@Subscribe
    public void onMoveIdToNextPageEvent(MoveIdToNextPageEvent event) throws IOException {
        id = event.getId();
    }


@Subscribe
    public void onShowScheduleTestEvent(ShowScheduleTestEvent event){
        scheduledTests = event.getScheduledTestList();
        for(ScheduledTest scheduledTest:scheduledTests){
            scheduleTestIds.add(scheduledTest.getId());
        }
        Platform.runLater(this::enterTest);
}

@FXML
    public void EnterTest_btn(ActionEvent event) throws IOException {
        SimpleClient.getClient().sendToServer(new CustomMessage("#showScheduleTest",""));
    }
    public void enterTest(){
        String idInput = text_id.getText();
        String codeInput = text_testCode.getText();
        if(!idInput.equals(id) || (!scheduleTestIds.contains(codeInput))){
            //if the id doesn't match with the user id
            // or the testId doesn't exist
            Platform.runLater(()->{
                msg.setVisible(true);
                msg.setText("You dont have access!");
            });
            //TODO add more validation checks
        }
        else { // valid id and valid schedule test id
            //TODO check if status == 1 .
            //if status is 0 write the time and date that test starts
            //if status is 2 write that the test isnt available anymore
            int index = scheduleTestIds.indexOf(codeInput);
            System.out.println(codeInput);
            ScheduledTest scheduledTest = scheduledTests.get(index);
            int status = scheduledTest.getStatus();
            if (status == 0) { // test is yet to start
                Platform.runLater(() -> {
                    msg.setVisible(true);
                    msg.setText("the test will be available at : " + scheduledTest.getDate() + " in " + scheduledTest.getTime());
                });
            } else if (status == 2) { // test time has passed
                msg.setVisible(true);
                msg.setText("the test is not available anymore");
            } else { // test is available
                cleanup();
                try {
                    if(scheduledTest.getIsComputerTest()){App.switchScreen("studentExecuteExam");}
                    else{App.switchScreen("studentExecuteExamLOCAL");}
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    try {
                        SimpleClient.getClient().sendToServer(new CustomMessage("#getStudent", id));
                        SimpleClient.getClient().sendToServer(new CustomMessage("#getScheduleTestWithInfo", codeInput));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }


    //TODO add buttons for home and back (matan)

    @FXML
    public void goBackButton() throws IOException {
        cleanup();
        App.switchScreen("studentHome");
        Platform.runLater(() -> {
            try {
                SimpleClient.getClient().sendToServer(new CustomMessage("#studentHome", id));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
