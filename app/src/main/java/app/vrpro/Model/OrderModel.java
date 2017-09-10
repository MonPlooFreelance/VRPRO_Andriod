package app.vrpro.Model;

/**
 * Created by Plooer on 6/25/2017 AD.
 */

public class OrderModel {

    private Integer ID;
    private String quotationNo;
    private String quotationDate;
    private String projectName;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String customerTypeOfWongKob;
    private Double discount;
    private Double totalPrice;
    private String remarks;
    private Double realTotalPrice;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getQuotationDate() {
        return quotationDate;
    }

    public void setQuotationDate(String quotationDate) {
        this.quotationDate = quotationDate;
    }

    public String getQuotationNo() {
        return quotationNo;
    }

    public void setQuotationNo(String quotationNo) {
        this.quotationNo = quotationNo;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerTypeOfWongKob() {
        return customerTypeOfWongKob;
    }

    public void setCustomerTypeOfWongKob(String customerTypeOfWongKob) {
        this.customerTypeOfWongKob = customerTypeOfWongKob;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Double getRealTotalPrice() {
        return realTotalPrice;
    }

    public void setRealTotalPrice(Double realTotalPrice) {
        this.realTotalPrice = realTotalPrice;
    }
}
