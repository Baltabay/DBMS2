package app;

public class Order {
    private int order_code;
    private int customer_code;
    private int product_code;
    private int employee_code;
    private int unit_price;
    private int quantity;
    private float discount;
    private String order_date;

    public Order(int order_code, int customer_code, int product_code, int employee_code, int unit_price, int quantity, float discount, String order_date) {
        this.order_code = order_code;
        this.customer_code = customer_code;
        this.product_code = product_code;
        this.employee_code = employee_code;
        this.unit_price = unit_price;
        this.quantity = quantity;
        this.discount = discount;
        this.order_date = order_date;
    }

    public int getOrder_code() {
        return order_code;
    }

    public void setOrder_code(int order_code) {
        this.order_code = order_code;
    }

    public int getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(int customer_code) {
        this.customer_code = customer_code;
    }

    public int getProduct_code() {
        return product_code;
    }

    public void setProduct_code(int product_code) {
        this.product_code = product_code;
    }

    public int getEmployee_code() {
        return employee_code;
    }

    public void setEmployee_code(int employee_code) {
        this.employee_code = employee_code;
    }

    public int getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(int unit_price) {
        this.unit_price = unit_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }
}
