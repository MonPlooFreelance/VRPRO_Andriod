package vrpro.vrpro.util;

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
import java.util.ArrayList;
import java.util.List;

import vrpro.vrpro.Model.EachOrderModel;
import vrpro.vrpro.Model.OrderModel;
import vrpro.vrpro.Model.ProfileSaleModel;

/**
 * Created by manitkannika on 7/4/2017 AD.
 */

public class PDFTemplateUtils {

    private static final String LOG_TAG = "PDFTemplateUtils";
    private String fontFamily = "assets/THSarabun.ttf";
    private Font bodyTile = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.BOLD);
    private Font bodyFontBold = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.BOLD);
    private Font bodyFont = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);
    private Font bodyFontBoldRed = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.BOLD, BaseColor.RED);
    private Font itemTableHeader = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.BOLD, BaseColor.WHITE);
    private Font itemTableFooter = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.BOLD);
    private Font tail = FontFactory.getFont(fontFamily,  BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.BOLD, BaseColor.RED);
    private Context context;
    private OrderModel orderModel;
    private List<EachOrderModel> eachOrderModelList;
    private ProfileSaleModel profileSaleModel;

    public PDFTemplateUtils(Context context, OrderModel orderModel, List<EachOrderModel> eachOrderModelList, ProfileSaleModel profileSaleModel){
        this.context = context;
        this.orderModel = orderModel;
        this.eachOrderModelList = eachOrderModelList;
        this.profileSaleModel = profileSaleModel;
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
            document.add(getItemDetail(eachOrderModelList, orderModel.getTotalPrice()));
            document.add(getTotalPriceDetail(orderModel));
            phrase = new Phrase("กำหนดยืนราคา (Price Validity) 30 วัน                   กำหนดส่งงาน (Term of Delivery)", bodyFontBold);
            document.add(new Paragraph(phrase));
            document.add(new Paragraph("\n"));
            document.add(getPaymentDetail(orderModel));
            document.add(new Paragraph("\n"));document.add(new Paragraph("\n"));
            document.add(getSignatureDetail(orderModel, profileSaleModel));

//            document.add(itemTable);
            document.close();
            return true;
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        } catch (DocumentException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        }
    }

    private Element getSignatureDetail(OrderModel orderModel, ProfileSaleModel profileSaleModel) throws DocumentException {
        PdfPTable itemTable = new PdfPTable(5);
        itemTable.setWidths(new float[] {25, 25, 25, 25, 25});
        itemTable.setTotalWidth(523);
        itemTable.setLockedWidth(true);
        PdfPCell cell = new PdfPCell(new Phrase("เจ้าหน้าที่ฝ่ายขาย\n(Sales Representative)", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase(profileSaleModel.getSaleName()+"\n"+profileSaleModel.getSalePhone()+"\n....../...../.....", bodyFontBoldRed));
        cell.setBorder(0);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("ผู้สั่งซื้อ..................\n      (Buyer)\n"+"....../...../.....", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("ผู้อนุมัติ\n(Authorized by)", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase(".......................\n นาย "+profileSaleModel.getSaleName()+"\n....../...../.....", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);
        return itemTable;
    }

    private Element getTotalPriceDetail(OrderModel orderModel) throws DocumentException {
        PdfPTable itemTable = new PdfPTable(3);
        itemTable.setWidths(new float[] {50, 20, 30});
        itemTable.setTotalWidth(523);
        itemTable.setLockedWidth(true);
        itemTable.addCell(createCellFooterItemTable("หนึ่งหมื่นเจ็ดพันสองร้อยเก้าสิบหกบาทถ้วน"));
        itemTable.addCell(createCellFooterItemTable("รวมเงิน\nTOTAL"));
        itemTable.addCell(createCellFooterItemTable(String.valueOf(orderModel.getTotalPrice())));
        return itemTable;
    }

    private Element getPaymentDetail(OrderModel orderModel) throws DocumentException {
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

        cell = new PdfPCell(new Phrase("มัดจำ                 7,000       บาท", bodyFontBold));
        cell.setBorder(0);
        itemTable.addCell(cell);

        cell = new PdfPCell(new Phrase("ติดตั้ง                 10,296       บาท", bodyFontBold));
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

    private Element getItemDetail(List<EachOrderModel> eachOrderModelList, double totalPrice) throws DocumentException {
        PdfPTable itemTable = createHeaderItemTable();

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

        String currentFloor = "";
        for (EachOrderModel eachOrderModel : eachOrderModelList) {
            String floor = eachOrderModel.getFloor();
            if(!currentFloor.equals(floor)){
                cell = new PdfPCell(new Phrase("ชั้น "+floor, bodyFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.LEFT);
                itemTable.addCell(cell);
                cell = new PdfPCell();
                cell.setBackgroundColor(new BaseColor(176, 182, 188));
                itemTable.addCell(cell);
                itemTable.addCell("");
                itemTable.addCell("");
                itemTable.addCell("");
                currentFloor = floor;
            }
            cell = new PdfPCell(new Phrase(eachOrderModel.getPosition(), bodyFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.LEFT);
            itemTable.addCell(cell);
            cell = new PdfPCell(new Phrase(createDetailString(eachOrderModel), bodyFont));
            cell.setBorder(Rectangle.LEFT);
            itemTable.addCell(cell);
            cell = new PdfPCell(new Phrase(String.valueOf(eachOrderModel.getPricePer1mm()), bodyFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemTable.addCell(cell);
            cell = new PdfPCell(new Phrase("1.00", bodyFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemTable.addCell(cell);
            cell = new PdfPCell(new Phrase(String.valueOf(eachOrderModel.getTotolPrice()), bodyFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemTable.addCell(cell);
        }

//        cell = new PdfPCell(new Phrase("ประตูหน้า", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("D1 (ก 1.10 x ส 2.05)    สีขาว / มุ้งกรอบเหล็กเลื่อน", bodyFont));
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("4,500", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1.00", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("4,500", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);

//        cell = new PdfPCell(new Phrase("ประตูหลัง", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("D2 (ก 1.10 x ส 2.05)    สีขาว / มุ้งกรอบเหล็กเลื่อน", bodyFont));
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("4,500", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1.00", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("4,500", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//
//        cell = new PdfPCell(new Phrase("หน้าต่างหลัง", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("W3 (ก 0.80 x ส 1.20)    # / มุ้งเลื่อน 1 บาน", bodyFont));
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1,000", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("0,96", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1,000", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//
//        cell = new PdfPCell(new Phrase("ช่องบันได", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("W4 (ก 0.70 x ส 1.20)    # / มุ้งเปิด 1 บาน", bodyFont));
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("7,00", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("0,84", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("700", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//
//        cell = new PdfPCell(new Phrase("ชั้น 2", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell();
//        cell.setBackgroundColor(new BaseColor(176, 182, 188));
//        itemTable.addCell(cell);
//        itemTable.addCell("");
//        itemTable.addCell("");
//        itemTable.addCell("");
//
//        cell = new PdfPCell(new Phrase("ประตูระเบียง", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("D5 (ก 0.84 x ส 1.90)    # / มุ้งเลื่อน 1 บาน", bodyFont));
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1,000", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1.60", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1,596", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//
//        cell = new PdfPCell(new Phrase("หน้าต่าง", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("W6 (ก 0.60 x ส 1.55)    # / มุ้งเลื่อน 1 บาน", bodyFont));
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1,000", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("0.93", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1,000", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//
//        cell = new PdfPCell(new Phrase("หน้าต่าง", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("W7 (ก 0.80 x ส 1.20)    # / มุ้งเลื่อน 1 บาน", bodyFont));
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1,000", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("0.96", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1,000", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//
//        cell = new PdfPCell(new Phrase("หน้าต่าง", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("W8 (ก 0.80 x ส 1.20)    # / มุ้งเลื่อน 1 บาน", bodyFont));
//        cell.setBorder(Rectangle.LEFT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1,000", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("0.96", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        itemTable.addCell(cell);
//        cell = new PdfPCell(new Phrase("1,000", bodyFont));
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        itemTable.addCell(cell);

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
        cell = new PdfPCell(new Phrase("กรณีพื้นที่มุ้งไม่ถึง 0.50 ตร.ม. คิดเป็น 0.50 ตร.ม.", bodyFont));
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        itemTable.addCell("");
        itemTable.addCell("");
        itemTable.addCell("");

        cell = new PdfPCell();
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("กรณีพื้นที่มุ้งที่อยู่ในช่วงระหว่าง 0.51-0.99 ตร.ม. คิดเป็น 1.00 ตร.ม.", bodyFont));
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        itemTable.addCell("");
        itemTable.addCell("");
        itemTable.addCell("");

        cell = new PdfPCell();
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        cell = new PdfPCell();
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        itemTable.addCell("");
        itemTable.addCell("");
        itemTable.addCell("");

        cell = new PdfPCell(new Phrase("2.", bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.LEFT);
        cell.setUseVariableBorders(true);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("ค่าติดตั้ง", bodyFont));
        cell.setBorder(Rectangle.LEFT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("2,000", bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("1", bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("2,000", bodyFont));
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
        cell = new PdfPCell(new Phrase("300", bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("0", bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase("300", bodyFont));
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
        cell.setColspan(2);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase(String.valueOf(totalPrice), bodyFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemTable.addCell(cell);
        return itemTable;
    }

    private String createDetailString(EachOrderModel eachOrderModel) {
        return eachOrderModel.getDw() +" (ก "+eachOrderModel.getWidth()+" x ส "+eachOrderModel.getHeight()+"        "
                +eachOrderModel.getTypeOfM()+" "+eachOrderModel.getSpecialWord()+" / "+getSpacialReqString(eachOrderModel.getSpecialReq());
//        "D1 (ก 1.10 x ส 2.05)    สีขาว / มุ้งกรอบเหล็กเลื่อน"
    }

    private String getSpacialReqString(ArrayList<String> specialReq) {
        String result = "";
        for (String req: specialReq) {
            result += req;
            result += " ";
        }
        return result;
    }

    private PdfPTable createHeaderItemTable() throws DocumentException {
        PdfPTable itemTable = new PdfPTable(5);
        itemTable.setWidths(new float[] {20, 50, 10, 10, 10});
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

        cell = new PdfPCell(new Phrase(": "+orderModel.getCustomerAdress(), bodyFont));
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

        divTable.addCell(new Phrase(": วงกบสีขาว / ยังไม่สามารถระบุเรื่องรางมุ้ง", bodyFont));
        return divTable;
    }

    private PdfPCell createCellHeaderItemTable(String title){
        PdfPCell cell = new PdfPCell(new Phrase(title, itemTableHeader));
        cell.setFixedHeight(40f);
        cell.setBackgroundColor(BaseColor.RED);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private PdfPCell createCellFooterItemTable(String title){
        PdfPCell cell = new PdfPCell(new Phrase(title, itemTableFooter));
        cell.setFixedHeight(40f);
        if(isPriceText(title)){
            cell.setBackgroundColor(new BaseColor(176, 182, 188));
        } else {
            cell.setBackgroundColor(new BaseColor(192, 199, 205));
        }
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private boolean isPriceText(String printString){
        String replaceString = printString.replace(",", "");
        try {
            Integer.parseInt(replaceString);
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

}
