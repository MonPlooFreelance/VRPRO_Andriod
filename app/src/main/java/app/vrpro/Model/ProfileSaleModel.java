package app.vrpro.Model;

/**
 * Created by Plooer on 6/27/2017 AD.
 */

public class ProfileSaleModel {
    private Integer ID;
    private String saleName;
    private String salePhone;
    private String quotationNo;
    private Integer quotationRunningNo;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName;
    }

    public String getSalePhone() {
        return salePhone;
    }

    public void setSalePhone(String salePhone) {
        this.salePhone = salePhone;
    }

    public String getQuotationNo() {
        return quotationNo;
    }

    public void setQuotationNo(String quotationNo) {
        this.quotationNo = quotationNo;
    }

    public Integer getQuotationRunningNo() {
        return quotationRunningNo;
    }

    public void setQuotationRunningNo(Integer quotationRunningNo) {
        this.quotationRunningNo = quotationRunningNo;
    }
}
