package app;

import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginFXMLController implements Initializable  {
    @FXML private AnchorPane pane;
    @FXML private Text message;
    @FXML private TextField user_id;
    @FXML private PasswordField password;

    private boolean right = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Controller.addDraggableNode(this.pane);
    }

    @FXML
    public void connects(ActionEvent event){
        if (verifyFields()){
            message.setText("Complete all fields");
            return;
        }

        String user = "\"" + user_id.getText() + "\"";

        try {
            Connection connection = new OracleConnection(user, password.getText()).getConnection();
            message.setText("Connection is success!");
            connection.close();
            Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("resources/navigators.fxml"))));
            stage.centerOnScreen();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
            message.setText("Invalid username/password");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    private boolean verifyFields(){
        return user_id.getText().trim().isEmpty() || password.getText().trim().isEmpty();
    }

    @FXML
    private void handleCloseButton(ActionEvent event){
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMoveButton(ActionEvent event){
        if (right) {
            TranslateTransition t1 = new TranslateTransition();
            TranslateTransition t2 = new TranslateTransition();
            TranslateTransition t3 = new TranslateTransition();

            t1.setDuration(new Duration(200));
            t2.setDuration(new Duration(300));
            t3.setDuration(new Duration(300));

            t1.setNode(pane);
            t2.setNode(pane);
            t3.setNode(pane);

            t1.setByX(-510);
            t2.setByX(40);
            t3.setByX(-10);

            SequentialTransition transition = new SequentialTransition(t1, t2, t3);
            transition.play();
            right = false;
        } else {
            TranslateTransition t1 = new TranslateTransition();
            TranslateTransition t2 = new TranslateTransition();
            TranslateTransition t3 = new TranslateTransition();

            t1.setDuration(new Duration(200));
            t2.setDuration(new Duration(300));
            t3.setDuration(new Duration(300));

            t1.setNode(pane);
            t2.setNode(pane);
            t3.setNode(pane);

            t1.setByX(510);
            t2.setByX(-40);
            t3.setByX(10);

            SequentialTransition transition = new SequentialTransition(t1, t2, t3);
            transition.play();
            right = true;
        }

    }
}
