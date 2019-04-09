package app;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class NavigatorsFXMLController implements Initializable {

    public Connection connection;
    @FXML private Pane pane1, pane2, pane3, pane4, pane5;
    @FXML private JFXButton btn1, btn2, btn3, btn4, btn5;
    @FXML private VBox pane;
    @FXML private LineChart<?, ?> line_chart;
    @FXML private PieChart pie_chart1, pie_chart2, pie_chart3;

    @FXML private TableView<Employee> emp_table;
    @FXML private TableColumn<Employee, Integer> col_id;
    @FXML private TableColumn<Employee, String> col_firstName;
    @FXML private TableColumn<Employee, String> col_lastName;
    @FXML private TableColumn<Employee, String> col_job;
    @FXML private TableColumn<Employee, String> col_phone;
    @FXML private TextField emp_search;

    private ObservableList<Employee> employees = FXCollections.observableArrayList();
    FilteredList filter = new FilteredList(employees);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Controller.addDraggableNode(this.pane);
        try {
            connection = new OracleConnection("\"Project\"", "87013345999b").getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handlerMenuButton(ActionEvent event) throws SQLException {
        if (event.getSource() == btn1) {
            pane1.toFront();
        } else if (event.getSource() == btn2) {
            pane2.toFront();
        } else if (event.getSource() == btn3) {
            pane3.toFront();
        } else if (event.getSource() == btn4){
            buildTable();
            pane4.toFront();
        } else if (event.getSource() == btn5){
            statsLineChart();
            statsFirstPieChart();
            statsSecondPieChart();
            statsThirdPieChart();
            pane5.toFront();
        }
    }

    @FXML
    private void close(ActionEvent event){
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void employeeSearch(){
        emp_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate((Predicate<? super Employee>) (Employee employee) ->{

                if (newValue.isEmpty() || newValue == null) return true;
                else if (employee.getFirst_name().contains(newValue) || employee.getLast_name().contains(newValue)) return true;

                return false;
            });
        });

        emp_table.setItems(filter);
    }

    private void buildTable() throws SQLException {
        Statement stmt = connection.createStatement();
        String query = "SELECT * FROM EMPLOYEES";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) employees.add(new Employee(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));

        col_id.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("id"));
        col_firstName.setCellValueFactory(new PropertyValueFactory<Employee, String>("first_name"));
        col_lastName.setCellValueFactory(new PropertyValueFactory<Employee, String>("last_name"));
        col_job.setCellValueFactory(new PropertyValueFactory<Employee, String>("job"));
        col_phone.setCellValueFactory(new PropertyValueFactory<Employee, String>("phone"));

        emp_table.setItems(employees);
    }

    private void statsLineChart() throws SQLException {
        line_chart.getData().clear();
        Statement stmt = connection.createStatement();
        String query = "SELECT EXTRACT(YEAR FROM order_date), EXTRACT(MONTH FROM order_date), COUNT(*) FROM ORDERS GROUP BY EXTRACT(MONTH FROM order_date), EXTRACT(YEAR FROM order_date) ORDER BY EXTRACT(MONTH FROM order_date)";
        ResultSet rs = stmt.executeQuery(query);
        HashMap<String, XYChart.Series> data = new HashMap<>();

        while (rs.next()) {
         if (!data.containsKey(rs.getString(1))){
             XYChart.Series series = new XYChart.Series();
             series.setName(rs.getString(1));
             series.getData().add(new XYChart.Data(rs.getString(2), rs.getInt(3)));
             data.put(rs.getString(1), series);
         } else {
             XYChart.Series series = data.get(rs.getString(1));
             series.getData().add(new XYChart.Data(rs.getString(2), rs.getInt(3)));
             data.put(rs.getString(1), series);
         }
        }

        for (XYChart.Series series: data.values()) {
            line_chart.getData().add(series);
        }
    }

    private void statsFirstPieChart() throws SQLException {
        pie_chart1.getData().clear();
        Statement stmt = connection.createStatement();
        String query = "SELECT COUNT(*), PAYMENT_METHOD_NAME FROM PAYMENTS " +
                "JOIN PAYMENT_METHODS ON PAYMENT_METHODS.PAYMENT_METHOD_CODE = PAYMENTS.PAYMENT_METHOD_CODE GROUP BY PAYMENT_METHOD_NAME";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            PieChart.Data slice = new PieChart.Data(rs.getString(2), rs.getInt(1));
            pie_chart1.getData().add(slice);
        }

    }

    private void statsSecondPieChart() throws SQLException {
        pie_chart2.getData().clear();
        Statement stmt = connection.createStatement();
        String query = "SELECT COUNT(*), PAYMENT_STATUS_DESCRIPTION FROM PAYMENTS " +
                "JOIN PAYMENT_STATUS ON PAYMENT_STATUS.PAYMENT_STATUS_CODE = PAYMENTS.PAYMENT_STATUS_CODE GROUP BY PAYMENT_STATUS_DESCRIPTION";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            PieChart.Data slice = new PieChart.Data(rs.getString(2), rs.getInt(1));
            pie_chart2.getData().add(slice);
        }
    }

    private void statsThirdPieChart() throws SQLException {
        pie_chart3.getData().clear();
        Statement stmt = connection.createStatement();
        String query = "SELECT COUNT(*) as \"#\", COUNTRY_NAME FROM ORDERS " +
                "JOIN CUSTOMER_DETAILS ON CUSTOMER_DETAILS.CUSTOMER_CODE = ORDERS.CUSTOMER_CODE " +
                "JOIN ADDRESSES ON ADDRESSES.ADDRESS_ID = CUSTOMER_DETAILS.ADDRESS_ID " +
                "JOIN COUNTRIES ON COUNTRIES.COUNTRY_CODE = ADDRESSES.COUNTRY_CODE " +
                "GROUP BY COUNTRY_NAME ORDER BY \"#\" DESC FETCH FIRST 5 ROWS ONLY";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            PieChart.Data slice = new PieChart.Data(rs.getString(2), rs.getInt(1));
            pie_chart3.getData().add(slice);
        }
    }
}
