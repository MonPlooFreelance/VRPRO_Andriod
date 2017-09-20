package app.vrpro.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.vrpro.Model.EachOrderModel;
import app.vrpro.Model.OrderModel;
import app.vrpro.Model.ProfileSaleModel;

/**
 * Created by manitkannika on 7/4/2017 AD.
 */

public class PDFTemplateUtils {

    private static List<String> fixAreaCostTypeOfMList = createFixAreaCostTypeOfMList();

    private static final String LOG_TAG = "PDFTemplateUtils";
    private static final SimpleDateFormat longDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String fontFamily = "assets/THSarabun.ttf";
    private Font bodyTile = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.BOLD);
    private Font bodyFontBold = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.BOLD);
    private Font bodyFont = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);
    private Font bodyFontBoldRed = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.BOLD, BaseColor.RED);
    private Font itemTableHeader = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14, Font.BOLD, BaseColor.WHITE);
    private Font itemTableFooter = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14, Font.BOLD);
    private Font tail = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.BOLD, BaseColor.RED);
    private DecimalFormat priceFormat = new DecimalFormat("#,###.##");
    private DecimalFormat areaFormat=new DecimalFormat("0.00");
    private BaseColor lightGrayColor = new BaseColor(209, 211, 214);
    private BaseColor darkGrayColor = new BaseColor(152, 153, 155);
    private BaseColor headerTableColor = new BaseColor(204, 6, 9);
    private Context context;
    private OrderModel orderModel;
    private List<EachOrderModel> eachOrderModelList;
    private ProfileSaleModel profileSaleModel;
    private double subTotalPrice;
    private double lastTotalPrice;
    private Map<Integer, List<EachOrderModel>> itemByFloorMap;
    private int countWindow;
    private int countDoor;

    public PDFTemplateUtils(Context context, OrderModel orderModel, List<EachOrderModel> eachOrderModelList, ProfileSaleModel profileSaleModel){
        this.itemByFloorMap = new HashMap<>();
        this.countWindow = 0;
        this.countDoor = 0;
        this.context = context;
        this.orderModel = orderModel;
        this.eachOrderModelList = eachOrderModelList;
        this.profileSaleModel = profileSaleModel;
        Log.i(LOG_TAG, "totalPrice : "+ orderModel.getTotalPrice());
        subTotalPrice = orderModel.getTotalPrice() + orderModel.getDiscount() + 2000d;
        lastTotalPrice = orderModel.getTotalPrice() + 2000d;
    }

    public Boolean write(File file) {
        try {

            if (!file.exists()) {
                file.createNewFile();
                Log.i(LOG_TAG, "createNewFile");
            }

            PDFTemplateUtils.HeaderTable event = new PDFTemplateUtils.HeaderTable();
            Document document = new Document(PageSize.A4, 36, 36, 20 + event.getTableHeight(), 36);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
            writer.setPageEvent(event);
            document.open();
            Phrase phrase = new Phrase("ใบเสนอราคา/ใบสั่งซื้อ", bodyTile);
            Paragraph paragraph = new Paragraph(phrase);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add(new Paragraph(new Phrase("เรียน/Attention", bodyFontBold)));
            document.add(getCustomerDetail(orderModel));
            document.add(new Paragraph("\n"));

            document.add(getItemDetail(eachOrderModelList));
            document.add(getTotalPriceDetail());

            phrase = new Phrase("กำหนดยืนราคา (Price Validity) 30 วัน                   กำหนดส่งงาน (Term of Delivery)", bodyFontBold);
            document.add(new Paragraph(phrase));
            document.add(new Paragraph("\n"));
            document.add(getPaymentDetail());
            document.add(new Paragraph("\n"));
            document.add(getSignatureDetail(orderModel, profileSaleModel));
            document.close();
            return true;
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        } catch (DocumentException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        }
    }

    private Element getSignatureDetail(OrderModel orderModel, ProfileSaleModel profileSaleModel) throws DocumentException, ParseException {
        PdfPTable itemTable = new PdfPTable(5);
        itemTable.setWidths(new float[] {25, 25, 25, 25, 25});
        itemTable.setTotalWidth(523);
        itemTable.setLockedWidth(true);
        PdfPCell cell = new PdfPCell(new Phrase("เจ้าหน้าที่ฝ่ายขาย\n(Sales Representative)", bodyFontBold));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);
        Date date = longDateTimeFormat.parse(orderModel.getQuotationDate());
        cell = new PdfPCell(new Phrase(profileSaleModel.getSaleName()+"\n"+profileSaleModel.getSalePhone()+"\n"+dateFormat.format(date), bodyFontBoldRed));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("ผู้สั่งซื้อ....................................\n(Buyer)\n"+".........../.........../...........", bodyFontBold));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("ผู้อนุมัติ\n(Authorized by)", bodyFontBold));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("....................................\n นาย ศุภกฤต  นิลพันธ์ \n.........../.........../...........", bodyFontBold));
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);
        return itemTable;
    }

    private Element getTotalPriceDetail() throws DocumentException {
        PdfPTable itemTable = new PdfPTable(3);
        itemTable.setWidths(new float[] {50, 20, 30});
        itemTable.setTotalWidth(523);
        itemTable.setLockedWidth(true);
        itemTable.addCell(createCellFooterItemTable(generateThaiPrice(lastTotalPrice)));
        itemTable.addCell(createCellFooterItemTable("รวมเงิน\nTOTAL"));
        itemTable.addCell(createCellFooterItemTable(priceFormat.format(lastTotalPrice)));
        return itemTable;
    }

    private String generateThaiPrice(double lastTotalPrice) {
        return new ThaiBahtUtils().getText(lastTotalPrice);
    }

    private Element getPaymentDetail() throws DocumentException {
        PdfPTable itemTable = new PdfPTable(2);
        itemTable.setWidths(new float[] {45, 55});
        itemTable.setTotalWidth(523);
        itemTable.setLockedWidth(true);
        itemTable.setPaddingTop(5f);
        PdfPCell cell = new PdfPCell(new Phrase("รายการชาระเงิน (Terms of Payment)", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);

        cell = new PdfPCell(new Phrase("ชื่อบัญชี นางวชิราวรรณ ทัตตมนัส\n" +
                "บัญชีธนาคารกรุงเทพ เลขที่บัญชี 019-033-2999\n" +
                " ประเภทบัญชีออมทรพัย์", tail));
        cell.setRowspan(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        itemTable.addCell(cell);

        double depositPrice = calculateDepositPrice(lastTotalPrice);

        cell = new PdfPCell(new Phrase("มัดจำ                 "+priceFormat.format(depositPrice)+"       บาท", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);

        cell = new PdfPCell(new Phrase("ติดตั้ง                 "+priceFormat.format(lastTotalPrice-depositPrice)+"       บาท", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);

        cell = new PdfPCell(new Phrase("\n", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);

        cell = new PdfPCell(new Phrase("หมายเหตุ", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);

        cell = new PdfPCell(new Phrase("รับประกันสินค้า 1 ปีเต็ม ในกรณีมีการใช้งานปกติ", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);

        cell = new PdfPCell(new Phrase("การรับประกันไม่รวมกรณีแผ่นมั้งขาด และมั้งลวดตกราง", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);

        cell = new PdfPCell(new Phrase("ราคาดังกล่าวยังไม่รวมภาษีมูลค่าเพิ่ม 7%", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);
        return itemTable;
    }

    private double calculateDepositPrice(double lastTotalPrice) {
        int temp = (int) ((lastTotalPrice*0.4)/1000);
        Log.i(LOG_TAG, "price temp : "+temp);
        double depositPrice = (temp+1)*1000;
        Log.i(LOG_TAG, "depositPrice : "+depositPrice);
        return depositPrice;
    }

    private Element getItemDetail(List<EachOrderModel> eachOrderModelList) throws DocumentException {
        PdfPTable itemTable = createHeaderItemTable();
        createRuleOneItem(itemTable);

        separateItemByFloor(eachOrderModelList);

        for (int i=1; i<= itemByFloorMap.size(); i++) {
            insertItemByFloor(itemTable, i);
        }

        insertRemarkPart(itemTable);
        PdfPCell cell;

        cell = new PdfPCell(new Phrase("2.", bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.LEFT);
        cell.setUseVariableBorders(true);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("ค่าติดตั้ง", bodyFont));
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("2,000", bodyFontBold));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("1", bodyFontBold));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("2,000", bodyFontBold));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemTable.addCell(cell);
        
        cell = new PdfPCell(new Phrase("3.", bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.LEFT);
        cell.setUseVariableBorders(true);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("ชุดทำความสะอาดมุ้งลวด หน้าต่างบานเลื่อน 300 บ./ชุด", bodyFont));
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("300", bodyFontBold));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("0", bodyFontBold));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("0", bodyFontBold));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemTable.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(Rectangle.LEFT);
        cell.setUseVariableBorders(true);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("มุ้งลวด - เฟรมอลูมิเนียมสีขาว/มุ้งไฟเบอร์สีเทา", bodyFont));
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("TOTAL", bodyFontBold));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(lightGrayColor);
        cell.setColspan(2);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase(priceFormat.format(subTotalPrice), bodyFontBold));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBackgroundColor(lightGrayColor);
        itemTable.addCell(cell);

        addDiscountPart(itemTable);

        return itemTable;
    }

    private void insertRemarkPart(PdfPTable itemTable) {
        PdfPCell cell;
        cell = new PdfPCell();
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("Remark : ", bodyFontBold));
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        itemTable.addCell("");
        itemTable.addCell("");
        itemTable.addCell("");

        cell = new PdfPCell();
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("-กรณีพื้นที่มุ้งไม่ถึง 0.50 ตร.ม. คิดเป็น 0.50 ตร.ม.", bodyFont));
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        itemTable.addCell("");
        itemTable.addCell("");
        itemTable.addCell("");

        cell = new PdfPCell();
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("-กรณีพื้นที่มุ้งที่อยู่ในช่วงระหว่าง 0.51-0.99 ตร.ม. คิดเป็น 1.00 ตร.ม.", bodyFont));
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        itemTable.addCell("");
        itemTable.addCell("");
        itemTable.addCell("");
        addCustomRemark(itemTable);
    }

    private void insertItemByFloor(PdfPTable itemTable, int i) throws DocumentException {
        List<EachOrderModel> windowList = new ArrayList<>();
        List<EachOrderModel> doorList = new ArrayList<>();
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("ชั้น "+i, bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        cell = new PdfPCell();
        cell.setBackgroundColor(darkGrayColor);
        itemTable.addCell(cell);
        itemTable.addCell("");
        itemTable.addCell("");
        itemTable.addCell("");
        List<EachOrderModel> eachFloorItems = itemByFloorMap.get(i);

        for (EachOrderModel eachOrderModel : eachFloorItems) {
            if ("หน้าต่าง".equals(eachOrderModel.getDw())){
                windowList.add(eachOrderModel);
            } else if ("ประตู".equals(eachOrderModel.getDw())) {
                doorList.add(eachOrderModel);
            }
        }

        this.countWindow = 0;
        for (EachOrderModel eachOrderModel : windowList) {
            countWindow++;
            insertEachItem(itemTable, eachOrderModel);
        }

        this.countDoor = 0;
        for (EachOrderModel eachOrderModel : doorList) {
            countDoor++;
            insertEachItem(itemTable, eachOrderModel);
        }
    }

    private void insertEachItem(PdfPTable itemTable, EachOrderModel eachOrderModel) throws DocumentException {
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(eachOrderModel.getPosition(), bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);

        cell = new PdfPCell(createDetailString(eachOrderModel));
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);

        boolean fixType = isFixAreaCostTypeOfM(eachOrderModel);
        cell = new PdfPCell(new Phrase(priceFormat.format(eachOrderModel.getPricePer1mm()), bodyFontBold));
        if (fixType) {
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        } else {
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        }
        itemTable.addCell(cell);

        if (fixType) {
            cell = new PdfPCell(new Phrase("1.00", bodyFontBold));
        } else {
            cell = new PdfPCell(new Phrase(calculateArea(eachOrderModel.getWidth(), eachOrderModel.getHeight()), bodyFontBold));
        }
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);


        Log.i(LOG_TAG, "each order model : "+eachOrderModel.getTotalPrice());
        cell = new PdfPCell(new Phrase(priceFormat.format(eachOrderModel.getTotalPrice()), bodyFontBold));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemTable.addCell(cell);
    }

    private boolean isFixAreaCostTypeOfM(EachOrderModel eachOrderModel) {
        for (String typeName: fixAreaCostTypeOfMList) {
            if(typeName.equals(eachOrderModel.getTypeOfM())){
                return true;
            }
        }
        return false;
    }

    private void createRuleOneItem(PdfPTable itemTable) {
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("1.", bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.LEFT);
        cell.setUseVariableBorders(true);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("มุ้งลวด VP-PRO (ราคาโรงงาน)", bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);
        itemTable.addCell("");
        itemTable.addCell("");
        itemTable.addCell("");
    }

    private void separateItemByFloor(List<EachOrderModel> eachOrderModelList){
        for (EachOrderModel eachOrderModel : eachOrderModelList) {
            if(itemByFloorMap.containsKey(Integer.parseInt(eachOrderModel.getFloor()))){
                List<EachOrderModel> list = itemByFloorMap.get(Integer.parseInt(eachOrderModel.getFloor()));
                list.add(eachOrderModel);
                itemByFloorMap.put(Integer.parseInt(eachOrderModel.getFloor()), list);
            } else {
                List<EachOrderModel> list = new ArrayList<>();
                list.add(eachOrderModel);
                itemByFloorMap.put(Integer.parseInt(eachOrderModel.getFloor()), list);
            }
        }
    }

    private void addCustomRemark(PdfPTable itemTable) {
        PdfPCell cell;
        if(this.orderModel.getRemarks() != null && !("".equals(this.orderModel.getRemarks()))) {
            cell = new PdfPCell();
            cell.setBorder(Rectangle.LEFT);
            itemTable.addCell(cell);
            cell = new PdfPCell(new Phrase("-"+this.orderModel.getRemarks(), bodyFont));
            cell.setBorder(Rectangle.LEFT);
            itemTable.addCell(cell);
            itemTable.addCell("");
            itemTable.addCell("");
            itemTable.addCell("");
        }
    }

    private void addDiscountPart(PdfPTable itemTable) {
        PdfPCell cell;
        if(this.orderModel.getDiscount() != null && this.orderModel.getDiscount() != 0.0d) {
            Log.i(LOG_TAG, "have discount : "+this.orderModel.getDiscount());
            cell = new PdfPCell(new Phrase(""));
            cell.setBorder(Rectangle.LEFT);
            itemTable.addCell(cell);
            itemTable.addCell(cell);
            cell = new PdfPCell(new Phrase("Discount", bodyFontBoldRed));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGrayColor);
            cell.setColspan(2);
            itemTable.addCell(cell);
            cell = new PdfPCell(new Phrase(priceFormat.format(this.orderModel.getDiscount()), bodyFontBold));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBackgroundColor(lightGrayColor);
            itemTable.addCell(cell);
            Log.i(LOG_TAG, "finish get discount");
        }
    }

    private String calculateArea(double centimetreWidth, double centimetreHeight) {
        double multipliedResult = centimetreWidth/100.00d * centimetreHeight/100.00d;
        double roundUp = Math.round(multipliedResult*100)/100d;
        return areaFormat.format(roundUp);
    }

    private PdfPTable createDetailString(EachOrderModel eachOrderModel) throws DocumentException {

        PdfPTable innerTable = new PdfPTable(4);
        innerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        innerTable.setWidths(new float[] {28, 45, 2, 25});
        PdfPCell pdfPCell = new PdfPCell(new Phrase(parseToShortTypeAndAreaString(eachOrderModel), bodyFont));
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        innerTable.addCell(pdfPCell);

        String specialString = getSpecialString(eachOrderModel.getSpecialWordReport());
        pdfPCell = new PdfPCell(new Phrase(specialString, bodyFont));
        pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        innerTable.addCell(pdfPCell);

        pdfPCell = new PdfPCell(new Phrase("/", bodyFont));
        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        innerTable.addCell(pdfPCell);

        pdfPCell = new PdfPCell(new Phrase(eachOrderModel.getTypeOfM(), bodyFont));
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        innerTable.addCell(pdfPCell);
        return innerTable;
    }

    private String getSpecialString(String specialWordReport) {
        String resultString;
        if(specialWordReport == null || "".equals(specialWordReport)){
            resultString = "#";
        } else {
            resultString = specialWordReport;
        }
        return resultString;
    }

    private String parseToShortTypeAndAreaString(EachOrderModel eachOrderModel){
        String shortString = "";
        if("หน้าต่าง".equals(eachOrderModel.getDw())){
            shortString = "W"+this.countWindow+" (ก "+parseCentimetreToMetre(eachOrderModel.getWidth())+" x ส "+parseCentimetreToMetre(eachOrderModel.getHeight())+")";
        } else if ("ประตู".equals(eachOrderModel.getDw())){
            shortString = "D"+this.countDoor+" (ก "+parseCentimetreToMetre(eachOrderModel.getWidth())+" x ส "+parseCentimetreToMetre(eachOrderModel.getHeight())+")";
        }
        return shortString;
    }

    private String parseCentimetreToMetre(Double centimetre){
        return areaFormat.format(centimetre/100.00D);
    }

    private PdfPTable createHeaderItemTable() throws DocumentException {
        PdfPTable itemTable = new PdfPTable(5);
        itemTable.setWidths(new float[] {12, 58, 10, 10, 10});
        itemTable.setTotalWidth(523);
        itemTable.setLockedWidth(true);
        itemTable.addCell(createCellHeaderItemTable("รายการ\nITEM"));
        itemTable.addCell(createCellHeaderItemTable("รายละเอียด\nDESCRIPTION"));
        itemTable.addCell(createCellHeaderItemTable("ราคา/ตร.ม\nPRICE"));
        itemTable.addCell(createCellHeaderItemTable("ตร.ม\nขั้นต่ำ 1"));
        itemTable.addCell(createCellHeaderItemTable("ราคารวม\nAMOUNT"));
        return itemTable;
    }

    private PdfPTable getCustomerDetail(OrderModel orderModel) throws DocumentException {
        PdfPTable divTable = new PdfPTable(2);
        divTable.setWidths(new float[] {40, 60});
        divTable.setTotalWidth(523);
        divTable.setLockedWidth(true);
        divTable.getDefaultCell().setBorder(0);

        PdfPCell cell = new PdfPCell(new Phrase(": คุณ "+orderModel.getCustomerName(), bodyFont));
        cell.setColspan(2);
        cell.setBorder(0);
        divTable.addCell(cell);

        cell = new PdfPCell(new Phrase(": "+orderModel.getCustomerAddress(), bodyFont));
        cell.setColspan(2);
        cell.setBorder(0);
        divTable.addCell(cell);

        divTable.addCell(new Phrase(": "+orderModel.getCustomerPhone(), bodyFont));

        PdfPTable subTable = new PdfPTable(2);
        subTable.setWidths(new float[] {30, 70});
        subTable.addCell(new Phrase("เลขที่/No : ", bodyFontBold));
        subTable.addCell(new Phrase(orderModel.getQuotationNo(), bodyFont));
        subTable.addCell(new Phrase("วันที่/Date : ", bodyFontBold));
        subTable.addCell(new Phrase(orderModel.getQuotationDate(), bodyFont));
        subTable.addCell(new Phrase("โครงการ/Project : ", bodyFontBold));
        subTable.addCell(new Phrase(orderModel.getProjectName(), bodyFont));
        cell = new PdfPCell(subTable);
        cell.setRowspan(2);
        divTable.addCell(cell);

        divTable.addCell(new Phrase(": "+orderModel.getCustomerTypeOfWongKob(), bodyFont));
        return divTable;
    }

    private PdfPCell createCellHeaderItemTable(String title){
        PdfPCell cell = new PdfPCell(new Phrase(title, itemTableHeader));
        cell.setFixedHeight(35f);
        cell.setBackgroundColor(headerTableColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(0);
        return cell;

    }

    private PdfPCell createCellFooterItemTable(String title){
        PdfPCell cell = new PdfPCell(new Phrase(title, itemTableFooter));
        cell.setFixedHeight(35f);
        if(isPriceText(title)){
            cell.setBackgroundColor(darkGrayColor);
        } else {
            cell.setBackgroundColor(lightGrayColor);
        }
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        if(isNotTotalString(title)){
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        }
        cell.setPadding(0);
        return cell;
    }

    private boolean isNotTotalString(String title) {
        boolean result = !title.equals("รวมเงิน\nTOTAL");
        Log.i(LOG_TAG, "isNotTotalString : "+result);
        return result;
    }

    private boolean isPriceText(String printString){
        String replaceString = printString.replace(",", "");
        try {
            Double.parseDouble(replaceString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private ByteArrayOutputStream getResourceImage(String resourceName){
        try {
            InputStream ims = context.getAssets().open(resourceName);
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream;
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private class HeaderTable extends PdfPageEventHelper {
        private PdfPTable table;
        private float tableHeight;

        private HeaderTable() {
            try {
                Font headerTitle = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.BOLD, BaseColor.RED);
                Font headerFont = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 10, Font.BOLD);
                table = new PdfPTable(2);
                table.setWidths(new float[] {1, 2});
                table.setTotalWidth(523);
                table.setLockedWidth(true);
                table.getDefaultCell().setBorder(0);
                Image image = Image.getInstance(getResourceImage("vrpro_logo.jpg").toByteArray());
                image.scaleAbsolute(188, 63);
                table.addCell(image);

                PdfPTable subTable = new PdfPTable(1);
                subTable.getDefaultCell().setBorder(0);
                Phrase phrase = new Phrase("วีอาโปร SINCE 2008", headerTitle);
                PdfPCell cell = new PdfPCell(phrase);
                cell.setFixedHeight(30f);
                cell.setBorder(0);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                subTable.addCell(cell);
                subTable.addCell(new Phrase("4 ซอยสามวา 6 ถนนสามวา แขวงบางชัน เขตคลองสามวา กรงุเทพมหานคร", headerFont));
                subTable.addCell(new Phrase("Tel.087-659-2449 Fax.02-907-4498   E-mail.vrprosince2008@gmail.com", headerFont));

                table.addCell(subTable);
                PdfPCell lastRowCell = new PdfPCell();
                lastRowCell.setBorder(Rectangle.BOTTOM);
                lastRowCell.setUseVariableBorders(true);
                lastRowCell.setBorderWidthBottom(1f);
                lastRowCell.setBorderColorBottom(BaseColor.RED);
                lastRowCell.setFixedHeight(3f);
                table.addCell(lastRowCell);
                table.addCell(lastRowCell);
                tableHeight = table.getTotalHeight();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        private float getTableHeight() {
            return tableHeight;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            table.writeSelectedRows(0, -1,
                    document.left(),
                    document.top() + ((document.topMargin() + tableHeight) / 2),
                    writer.getDirectContent());
        }
    }

    private static List<String> createFixAreaCostTypeOfMList() {
        List<String> resultList = new ArrayList<>();
        resultList.add("มุ้งกรอบเหล็กเปิด");
        resultList.add("มุ้งกรอบเหล็กเลื่อน");
        resultList.add("มุ้งประตูเปิด");
        return resultList;
    }
}
