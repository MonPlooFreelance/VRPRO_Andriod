package vrpro.vrpro.Model;

import java.util.ArrayList;

/**
 * Created by Plooer on 6/25/2017 AD.
 */

public class EachOrderModel {
    private Integer ID;
    private String quatationNo;
    private String floor;
    private String position;
    private String dw;
    private String typeOfM;
    private String specialWord;
    private ArrayList<String> specialReq;
    private Double totolPrice;
    private Double width;
    private Double height;


    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getQuatationNo() {
        return quatationNo;
    }

    public void setQuatationNo(String quatationNo) {
        this.quatationNo = quatationNo;
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

    public Double getTotolPrice() {
        return totolPrice;
    }

    public void setTotolPrice(Double totolPrice) {
        this.totolPrice = totolPrice;
    }
}
