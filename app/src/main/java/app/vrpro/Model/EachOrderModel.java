package app.vrpro.Model;

import java.util.ArrayList;

/**
 * Created by Plooer on 6/25/2017 AD.
 */

public class EachOrderModel {
    private Integer ID;
    private String quotationNo;
    private String floor;
    private String position;
    private String dw;
    private String typeOfM;
    private String specialWord;
    private ArrayList<String> specialReq;
    private Double totalPrice;
    private Double width;
    private Double height;
    private Double pricePer1mm;
    private String SpecialWordReport;


    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getQuotationNo() {
        return quotationNo;
    }

    public void setQuotationNo(String quotationNo) {
        this.quotationNo = quotationNo;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDw() {
        return dw;
    }

    public void setDw(String dw) {
        this.dw = dw;
    }

    public String getTypeOfM() {
        return typeOfM;
    }

    public void setTypeOfM(String typeOfM) {
        this.typeOfM = typeOfM;
    }

    public String getSpecialWord() {
        return specialWord;
    }

    public void setSpecialWord(String specialWord) {
        this.specialWord = specialWord;
    }

    public ArrayList<String> getSpecialReq() {
        return specialReq;
    }

    public void setSpecialReq(ArrayList<String> specialReq) {
        this.specialReq = specialReq;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getPricePer1mm() {
        return pricePer1mm;
    }

    public void setPricePer1mm(Double pricePer1mm) {
        this.pricePer1mm = pricePer1mm;
    }

    public String getSpecialWordReport() {
        return SpecialWordReport;
    }

    public void setSpecialWordReport(String specialWordReport) {
        SpecialWordReport = specialWordReport;
    }
}
