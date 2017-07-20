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
import java.text.DecimalFormat;
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
    private DecimalFormat priceFormat = new DecimalFormat("#,###.##");
    private DecimalFormat areaFormat=new DecimalFormat("0.00");
    private Context context;
    private OrderModel orderModel;
    private List<EachOrderModel> eachOrderModelList;
    private ProfileSaleModel profileSaleModel;
    private double lastTotalPrice;

    public PDFTemplateUtils(Context context, OrderModel orderModel, List<EachOrderModel> eachOrderModelList, ProfileSaleModel profileSaleModel){
        this.context = context;
        this.orderModel = orderModel;
        this.eachOrderModelList = eachOrderModelList;
        this.profileSaleModel = profileSaleModel;
        Log.i(LOG_TAG, "totoalPrice : "+ orderModel.getTotalPrice());
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
            document.add(new Paragraph("\n"));document.add(new Paragraph("\n"));
            document.add(getSignatureDetail(orderModel, profileSaleModel));
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
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase(profileSaleModel.getSaleName()+"\n"+profileSaleModel.getSalePhone()+"\n.........../.........../...........", bodyFontBoldRed));
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
        cell = new PdfPCell(new Phrase("....................................\n "+profileSaleModel.getSaleName()+"\n.........../.........../...........", bodyFontBold));
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
            cell = new PdfPCell(new Phrase(priceFormat.format(eachOrderModel.getPricePer1mm()), bodyFontBold));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemTable.addCell(cell);
            cell = new PdfPCell(new Phrase(calculateArea(eachOrderModel.getWidth(), eachOrderModel.getHeight()), bodyFontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemTable.addCell(cell);
            Log.i(LOG_TAG, "each order model : "+eachOrderModel.getTotolPrice());
            cell = new PdfPCell(new Phrase(priceFormat.format(eachOrderModel.getTotolPrice()), bodyFontBold));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemTable.addCell(cell);
        }

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
        cell.setColspan(2);
        itemTable.addCell(cell);
        cell = new PdfPCell(new Phrase(priceFormat.format(lastTotalPrice), bodyFontBold));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemTable.addCell(cell);
        return itemTable;
    }

    private String calculateArea(double width, double height) {
        double multipliedResult = width * height;
        double roundUp = Math.round(multipliedResult*100)/100d;
        return areaFormat.format(roundUp);
    }

    private String createDetailString(EachOrderModel eachOrderModel) {
        return eachOrderModel.getDw() +" (ก "+eachOrderModel.getWidth()+" x ส "+eachOrderModel.getHeight()+")        "
                +eachOrderModel.getTypeOfM()+" "+eachOrderModel.getSpecialWord()+" / "+getSpacialReqString(eachOrderModel.getSpecialReq());
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
        cell.setFixedHeight(30f);
        cell.setBackgroundColor(BaseColor.RED);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private PdfPCell createCellFooterItemTable(String title){
        PdfPCell cell = new PdfPCell(new Phrase(title, itemTableFooter));
        cell.setFixedHeight(30f);
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
