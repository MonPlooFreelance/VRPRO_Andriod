package vrpro.vrpro.Model;

/**
 * Created by Plooer on 6/27/2017 AD.
 */

public class ProfileSaleModel {
    private Integer ID;
    private String saleName;
    private String salePhone;
    private String quatationNo;
    private Integer quatationRunningNo;

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

    public String getQuatationNo() {
        return quatationNo;
    }

    public void setQuatationNo(String quatationNo) {
        this.quatationNo = quatationNo;
    }

    public Integer getQuatationRunningNo() {
        return quatationRunningNo;
    }

    public void setQuatationRunningNo(Integer quatationRunningNo) {
        this.quatationRunningNo = quatationRunningNo;
    }
}
