package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.princeton.cs.algs4.ST;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class NavigatorsFXMLController implements Initializable {

    // Queries
    private final String employee_query = "SELECT * FROM EMPLOYEES";
    private final String line_chart_query = "SELECT EXTRACT(YEAR FROM order_date), EXTRACT(MONTH FROM order_date), COUNT(*) FROM ORDERS GROUP BY EXTRACT(MONTH FROM order_date), EXTRACT(YEAR FROM order_date) ORDER BY EXTRACT(MONTH FROM order_date)";
    private final String first_pie_chart_query = "SELECT COUNT(*), PAYMENT_METHOD_NAME FROM PAYMENTS JOIN PAYMENT_METHODS ON PAYMENT_METHODS.PAYMENT_METHOD_CODE = PAYMENTS.PAYMENT_METHOD_CODE GROUP BY PAYMENT_METHOD_NAME";
    private final String second_pie_chart_query = "SELECT COUNT(*), PAYMENT_STATUS_DESCRIPTION FROM PAYMENTS JOIN PAYMENT_STATUS ON PAYMENT_STATUS.PAYMENT_STATUS_CODE = PAYMENTS.PAYMENT_STATUS_CODE GROUP BY PAYMENT_STATUS_DESCRIPTION";
    private final String third_pie_chart_query = "SELECT COUNT(*) as \"#\", COUNTRY_NAME FROM ORDERS JOIN CUSTOMER_DETAILS ON CUSTOMER_DETAILS.CUSTOMER_CODE = ORDERS.CUSTOMER_CODE JOIN ADDRESSES ON ADDRESSES.ADDRESS_ID = CUSTOMER_DETAILS.ADDRESS_ID JOIN COUNTRIES ON COUNTRIES.COUNTRY_CODE = ADDRESSES.COUNTRY_CODE GROUP BY COUNTRY_NAME ORDER BY \"#\" DESC FETCH FIRST 5 ROWS ONLY";
    private final String order_query = "SELECT orders.order_code, orders.customer_code, order_details.product_code, orders.employee_code, order_details.unit_price, order_details.quantity, order_details.discount, orders.order_date FROM ORDERS JOIN ORDER_DETAILS ON orders.order_code = order_details.order_code";

    private final String dsb_purchases = "SELECT SUM(amount_of_payment) FROM PAYMENTS WHERE payment_status_code = 1";
    private final String dsb_sales = "SELECT COUNT(*) FROM PAYMENTS WHERE payment_status_code = 1";
    private final String dsb_payments = "SELECT COUNT(*) FROM PAYMENTS";
    private final String dsb_returns= "SELECT COUNT(*) FROM DEFECTS";

    private final String dsb_last_returns = "SELECT order_code, defect_description FROM DEFECTS";
    private final String dsb_last_orders = "SELECT orders.order_code, customer_details.customer_name FROM ORDERS JOIN CUSTOMER_DETAILS ON orders.customer_code = customer_details.customer_code ORDER BY 1 DESC FETCH FIRST 10 ROWS ONLY";
    private final String dsb_best_employees = "SELECT  employees.last_name, employees.first_name, COUNT(*) FROM ORDERS JOIN EMPLOYEES ON orders.employee_code = employees.employee_code GROUP BY employees.last_name, employees.first_name ORDER BY 3 DESC FETCH NEXT 10 ROWS ONLY";

    // Main nodes
    @FXML private Pane pane1, pane2, pane3, pane4, pane5;
    @FXML private JFXButton btn1, btn2, btn3, btn4, btn5;
    @FXML private VBox pane;

    // Dashboard section
    @FXML private Text dsb_sales_label;
    @FXML private Text dsb_purchases_label;
    @FXML private Text dsb_payments_label;
    @FXML private Text dsb_returns_label;
    @FXML private VBox order_containers;
    @FXML private VBox returns_containers;
    @FXML private VBox employees_containers;

    // Stats section
    @FXML private LineChart<?, ?> line_chart;
    @FXML private PieChart pie_chart1, pie_chart2, pie_chart3;

    // Employees section
    @FXML private TableView<Employee> emp_table;
    @FXML private TableColumn<Employee, Integer> emp_col_id;
    @FXML private TableColumn<Employee, String> emp_col_firstName;
    @FXML private TableColumn<Employee, String> emp_col_lastName;
    @FXML private TableColumn<Employee, String> emp_col_job;
    @FXML private TableColumn<Employee, String> emp_col_phone;
    @FXML private TextField emp_search;
    @FXML private JFXComboBox<String> emp_comboBox;

    // Orders section
    @FXML private TableView<Order> ord_table;
    @FXML private TableColumn<Order, Integer> ord_col_order;
    @FXML private TableColumn<Order, Integer> ord_col_customer;
    @FXML private TableColumn<Order, Integer> ord_col_product;
    @FXML private TableColumn<Order, Integer> ord_col_employee;
    @FXML private TableColumn<Order, Integer> ord_col_price;
    @FXML private TableColumn<Order, Integer> ord_col_quantity;
    @FXML private TableColumn<Order, Float> ord_col_discount;
    @FXML private TableColumn<Order, String> ord_col_date;
    @FXML private TextField ord_search;
    @FXML private JFXComboBox<String> ord_comboBox;

    // Helper variables
    private Statement stmt;
    public Connection connection;

    private ObservableList<Employee> employees = FXCollections.observableArrayList();
    private FilteredList emp_filter = new FilteredList(employees);
    private ObservableList<Order> orders = FXCollections.observableArrayList();
    private FilteredList ord_filter = new FilteredList(orders);

    private String emp_search_by = "";
    private String ord_search_by = "";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Controller.addDraggableNode(this.pane);

        try {
            connection = new OracleConnection("\"Project\"", "87013345999b").getConnection();
            stmt = connection.createStatement();
            getLastOrders();
            getLastReturns();
            getBestEmployees();
            getSales();
            getPurchases();
            getPayments();
            getReturns();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        emp_comboBox.getItems().addAll("Last name", "First name", "Phone", "Job");
        ord_comboBox.getItems().addAll("Order", "Customer", "Product", "Employee");

        emp_col_id.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("id"));
        emp_col_firstName.setCellValueFactory(new PropertyValueFactory<Employee, String>("first_name"));
        emp_col_lastName.setCellValueFactory(new PropertyValueFactory<Employee, String>("last_name"));
        emp_col_job.setCellValueFactory(new PropertyValueFactory<Employee, String>("job"));
        emp_col_phone.setCellValueFactory(new PropertyValueFactory<Employee, String>("phone"));

        ord_col_order.setCellValueFactory(new PropertyValueFactory<Order, Integer>("order_code"));
        ord_col_customer.setCellValueFactory(new PropertyValueFactory<Order, Integer>("customer_code"));
        ord_col_product.setCellValueFactory(new PropertyValueFactory<Order, Integer>("product_code"));
        ord_col_employee.setCellValueFactory(new PropertyValueFactory<Order, Integer>("employee_code"));
        ord_col_price.setCellValueFactory(new PropertyValueFactory<Order, Integer>("unit_price"));
        ord_col_quantity.setCellValueFactory(new PropertyValueFactory<Order, Integer>("quantity"));
        ord_col_discount.setCellValueFactory(new PropertyValueFactory<Order, Float>("discount"));
        ord_col_date.setCellValueFactory(new PropertyValueFactory<Order, String>("order_date"));

        buildEmployeeTable();
        buildOrderTable();
    }

    @FXML
    private void handlerMenuButton(ActionEvent event) throws SQLException {
        if (event.getSource() == btn1) {
            pane1.toFront();
        } else if (event.getSource() == btn2) {
            pane2.toFront();
        } else if (event.getSource() == btn3) {
            loadOrderData();
            pane3.toFront();
        } else if (event.getSource() == btn4){
            loadEmployeeData();
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
            emp_filter.setPredicate((Predicate<? super Employee>) (Employee employee) ->{

                if (newValue.isEmpty() || newValue == null) return true;
                else if (emp_search_by.equals("Last name") && employee.getLast_name().contains(newValue)) return true;
                else if (emp_search_by.equals("First name") && employee.getFirst_name().contains(newValue)) return true;
                else if (emp_search_by.equals("Phone") && employee.getPhone().contains(newValue)) return true;
                else if (emp_search_by.equals("Job") && employee.getJob().contains(newValue)) return true;

                return false;
            });
        });

        emp_table.setItems(emp_filter);
    }

    @FXML
    private void employeeSearchBy(){
        emp_search_by = emp_comboBox.getValue();
        employeeSearch();
    }

    @FXML
    private void orderSearch(){
        ord_search.textProperty().addListener((observable, oldValue, newValue) -> {
            ord_filter.setPredicate((Predicate<? super Order>) (Order order) ->{

                if (newValue.isEmpty() || newValue == null) return true;
                else if (ord_search_by.equals("Order") && String.valueOf(order.getOrder_code()).contains(newValue)) return true;
                else if (ord_search_by.equals("Customer") && String.valueOf(order.getCustomer_code()).contains(newValue)) return true;
                else if (ord_search_by.equals("Product") && String.valueOf(order.getProduct_code()).contains(newValue)) return true;
                else if (ord_search_by.equals("Employee") && String.valueOf(order.getEmployee_code()).contains(newValue)) return true;

                return false;
            });
        });

        ord_table.setItems(ord_filter);
    }

    @FXML
    private void orderSearchBy(){
        ord_search_by = ord_comboBox.getValue();
        ord_search.setText(ord_search.getText());
    }

    @FXML
    public void clickItemOnEmployee(MouseEvent event) {
        if (event.getClickCount() == 2) {
            System.out.println(emp_table.getSelectionModel().getSelectedItem().getFirst_name());
        }
    }

    @FXML
    public void clickItemOnOrder(MouseEvent event) throws IOException, SQLException {
        if (event.getClickCount() == 2) {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/details.fxml"));
            Parent root = loader.load();
            DetailsController controller = (DetailsController)loader.getController();
            controller.run(connection, ord_table.getSelectionModel().getSelectedItem());
            Controller.addDraggableNode(root);

            Scene scene = new Scene(root);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void empButtonFire(){
        btn4.fire();
    }

    @FXML
    private void ordButtonFire(){
        btn3.fire();
    }

    @FXML
    private void addEmployee(ActionEvent event) throws IOException {
        Stage mainStage = (Stage)((Button)event.getSource()).getScene().getWindow();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/form.fxml"));
        Parent root = loader.load();
        FormController controller = loader.getController();
        controller.initConnection(connection);
        controller.setMainStage(mainStage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        mainStage.hide();
        stage.showAndWait();
    }

    public void getLastReturns() throws IOException, SQLException {
        ResultSet rs = stmt.executeQuery(dsb_last_returns);

        while (rs.next()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/item.fxml"));
            Pane root = loader.load();
            ItemController controller = loader.getController();
            controller.setText(rs.getString(1));
            controller.setSub_text(rs.getString(2));

            returns_containers.getChildren().add(root);
        }
    }

    public void getLastOrders() throws IOException, SQLException {
        ResultSet rs = stmt.executeQuery(dsb_last_orders);

        while (rs.next()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/item.fxml"));
            Pane root = loader.load();
            ItemController controller = loader.getController();
            controller.setText(rs.getString(1));
            controller.setSub_text(rs.getString(2));

            order_containers.getChildren().add(root);
        }
    }

    public void getBestEmployees() throws IOException, SQLException {
        ResultSet rs = stmt.executeQuery(dsb_best_employees);

        while (rs.next()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/item.fxml"));
            Pane root = loader.load();
            ItemController controller = loader.getController();
            controller.setText(rs.getString(1) +" "+ rs.getString(2));
            controller.setSub_text(String.valueOf(rs.getInt(3)));

            employees_containers.getChildren().add(root);
        }
    }

    public void getSales() throws SQLException {
        ResultSet rs = stmt.executeQuery(dsb_sales);

        while (rs.next()){
            dsb_sales_label.setText(String.valueOf(rs.getInt(1)));
        }
    }

    public void getPurchases() throws SQLException {
        ResultSet rs = stmt.executeQuery(dsb_purchases);

        while (rs.next()){
            dsb_purchases_label.setText("$"+String.valueOf(rs.getInt(1)));
        }
    }

    public void getPayments() throws SQLException {
        ResultSet rs = stmt.executeQuery(dsb_payments);

        while (rs.next()){
            dsb_payments_label.setText(String.valueOf(rs.getInt(1)));
        }
    }

    public void getReturns() throws SQLException {
        ResultSet rs = stmt.executeQuery(dsb_returns);

        while (rs.next()){
            dsb_returns_label.setText(String.valueOf(rs.getInt(1)));
        }
    }

    private void buildEmployeeTable(){
        try { loadEmployeeData(); } catch (SQLException e) { e.printStackTrace(); }

        emp_table.setItems(employees);
    }

    private void buildOrderTable()  {
        try { loadOrderData(); } catch (SQLException e) { e.printStackTrace(); }

        ord_table.setItems(orders);
    }

    private void loadOrderData() throws SQLException{
        orders.clear();
        ResultSet rs = stmt.executeQuery(order_query);

        while (rs.next()) orders.add(new Order(rs.getInt(1),
                                               rs.getInt(2),
                                               rs.getInt(3),
                                               rs.getInt(4),
                                               rs.getInt(5),
                                               rs.getInt(6),
                                               rs.getFloat(7),
                                               rs.getString(8)));

    }

    private void loadEmployeeData() throws SQLException{
        employees.clear();
        ResultSet rs = stmt.executeQuery(employee_query);

        while (rs.next()) employees.add(new Employee(rs.getInt(1),
                                                     rs.getString(2),
                                                     rs.getString(3),
                                                     rs.getString(4),
                                                     rs.getString(5)));

    }

    private void statsLineChart() throws SQLException {
        line_chart.getData().clear();
        ResultSet rs = stmt.executeQuery(line_chart_query);
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

        ResultSet rs = stmt.executeQuery(first_pie_chart_query);

        while (rs.next()) {
            PieChart.Data slice = new PieChart.Data(rs.getString(2), rs.getInt(1));
            pie_chart1.getData().add(slice);
        }

    }

    private void statsSecondPieChart() throws SQLException {
        pie_chart2.getData().clear();
        Statement stmt = connection.createStatement();

        ResultSet rs = stmt.executeQuery(second_pie_chart_query);

        while (rs.next()) {
            PieChart.Data slice = new PieChart.Data(rs.getString(2), rs.getInt(1));
            pie_chart2.getData().add(slice);
        }
    }

    private void statsThirdPieChart() throws SQLException {
        pie_chart3.getData().clear();
        ResultSet rs = stmt.executeQuery(third_pie_chart_query);

        while (rs.next()) {
            PieChart.Data slice = new PieChart.Data(rs.getString(2), rs.getInt(1));
            pie_chart3.getData().add(slice);
        }
    }

}
