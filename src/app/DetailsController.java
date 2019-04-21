package app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DetailsController {
    private Order record;

    @FXML private Text cus_name;
    @FXML private Text com_name;
    @FXML private Text cus_email;
    @FXML private Text cus_phone;
    @FXML private Text prod_name;
    @FXML private Text supp;
    @FXML private Text prod_cat;
    @FXML private Text payed;

    private Connection con;

    public void setCus_name(String cus_name) {
        this.cus_name.setText(cus_name);
    }

    public void setCom_name(String com_name) {
        this.com_name.setText(com_name);
    }

    public void setCus_email(String cus_email) {
        this.cus_email.setText(cus_email);
    }

    public void setCus_phone(String cus_phone) {
        this.cus_phone.setText(cus_phone);
    }

    public void setProd_name(String prod_name) {
        this.prod_name.setText(prod_name);
    }

    public void setSupp(String supp) {
        this.supp.setText(supp);
    }

    public void setProd_cat(String prod_cat) {
        this.prod_cat.setText(prod_cat);
    }

    public void setPayed(String payed) {
        this.payed.setText(payed);
    }

    public void run(Connection con, Order order) throws SQLException {
        this.con = con;
        this.record = order;
        display();
    }

    private void display() throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT CUSTOMER_NAME, EMAIL, PHONE_NUMBER, COMPANY_NAME FROM CUSTOMER_DETAILS WHERE customer_code = ?");
        statement.setInt(1, record.getCustomer_code());
        ResultSet rs = statement.executeQuery();
        while (rs.next()){
            setCus_name(rs.getString(1));
            setCus_email(rs.getString(2));
            setCus_phone(rs.getString(3));
            setCom_name(rs.getString(4));
        }

        statement = con.prepareStatement("SELECT products.product_name, categories.category_name, suppliers.supplier_name FROM PRODUCTS JOIN CATEGORIES ON categories.category_id = products.category_id JOIN SUPPLIERS ON suppliers.supplier_id = products.supplier_id WHERE products.product_code = ?");
        statement.setInt(1, record.getProduct_code());
        rs = statement.executeQuery();
        while (rs.next()){
            setProd_name(rs.getString(1));
            setProd_cat(rs.getString(2));
            setSupp(rs.getString(3));
        }

        setPayed("True");
    }

    @FXML
    private void handleCloseButton(ActionEvent event){
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
