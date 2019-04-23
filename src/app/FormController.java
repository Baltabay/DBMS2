package app;

import com.jfoenix.controls.JFXTextField;
import edu.princeton.cs.algs4.ST;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class FormController implements Initializable {

    private Stage mainStage;

    public Connection connection;
    private Statement stmt;

    @FXML
    private Pane pane;

    @FXML
    private JFXTextField id_field;

    @FXML
    private JFXTextField first_name_field;

    @FXML
    private JFXTextField last_name_field;

    @FXML
    private JFXTextField job_field;

    @FXML
    private JFXTextField phone_field1;

    @FXML
    private JFXTextField phone_field2;

    @FXML
    private JFXTextField phone_field3;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addTextLimiter(phone_field1, 3);
        addTextLimiter(phone_field2, 3);
        addTextLimiter(phone_field3, 4);
        Controller.addDraggableNode(pane);
    }

    public void initConnection(Connection connection){
        this.connection = connection;
        try {
            stmt = this.connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void add(ActionEvent event) {
        String phone = "8-" + phone_field1.getText() + "-" + phone_field2.getText() + "-" + phone_field3.getText();

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO EMPLOYEES(EMPLOYEE_CODE, FIRST_NAME, LAST_NAME, JOB_DESCRIPTION, PHONE_NUMBER) VALUES (?, ?, ?, ?, ?)");
            statement.setInt(1, Integer.parseInt(id_field.getText()));

            statement.setString(2, first_name_field.getText());
            statement.setString(3, last_name_field.getText());
            statement.setString(4, job_field.getText());
            statement.setString(5, phone);

            statement.executeUpdate();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }


        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        mainStage.show();
        stage.close();
    }

    @FXML
    private void handleCloseButton(ActionEvent event){
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }



    public static void addTextLimiter(final JFXTextField tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
}
