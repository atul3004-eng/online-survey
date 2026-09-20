package dps.jsf;

import dps.ejb.ImportationMaster;
import dps.ejb.ImportationApproval;
import dps.ejb.UserMaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jsoup.Jsoup;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/** Builds XLS files from supplied records. No JSF context, facades, or shared state. */
public final class SpecialImportationExcelExporter {
    public StreamedContent export(String status, List<ImportationMaster> exportItems) {
    
        if (!"Approved".equals(status) && !"In-Progress".equals(status) && !"All".equals(status)) {
            throw new IllegalArgumentException("Unsupported importation status: " + status);
        }
    
        if (exportItems == null) {
            throw new IllegalArgumentException("Export records must not be null");
        }
    
        try (HSSFWorkbook applicantExcel = new HSSFWorkbook()) {
            HSSFSheet sheet = applicantExcel.createSheet(org.apache.poi.ss.util.WorkbookUtil.createSafeSheetName(status + " Special Importation Report"));
            sheet.createFreezePane(0, 1);
    
            Font boldFont = applicantExcel.createFont();
            boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            boldFont.setFontHeightInPoints((short) 12);
            boldFont.setFontName("Cambria");
    
            HSSFFont font = applicantExcel.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setFontName("Cambria");
    
            CellStyle style = applicantExcel.createCellStyle();
            style.setFont(boldFont);
            style.setWrapText(true);
            HSSFPalette palette = applicantExcel.getCustomPalette();
    
    // get the color which most closely matches the color you want to use
    HSSFColor myColor = palette.findSimilarColor((byte) 242, (byte) 220, (byte) 219);
    //HSSFColor myColor = palette.getColor(45);
    // get the palette index of that color
    short palIndex = myColor.getIndex();
    // code to get the style for the cell goes here
    style.setFillForegroundColor(palIndex);
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    style.setAlignment(CellStyle.ALIGN_CENTER);
    
            //Set Header Information 
            Header topHeader = sheet.getHeader();
            topHeader.setLeft("*** Special Importation Report ***");
            topHeader.setCenter(HSSFHeader.font("Arial", "Bold")
                    + HSSFHeader.fontSize((short) 14) + "SAMPLE ORDER");
    
            List<String> headerList = new ArrayList<String>();
            headerList.add("S.No.");
            headerList.add("Application No.");
            headerList.add("Trade Name");
            headerList.add("Generic Name");
            headerList.add("Manufaturer");
            headerList.add("Manufaturer Country");
            headerList.add("MAH");
            headerList.add("MAH Country");
            headerList.add("Exporter Name");
            headerList.add("Exporter Address");
            headerList.add("Importer Name");
            headerList.add("Beneficiary name");
            headerList.add("Submission Date");
            headerList.add("Approval Date");
            headerList.add("Quantity");
            headerList.add("Concentration");
            headerList.add("Dosage Form");
            headerList.add("Pack Size");
            headerList.add("Batch No");
            headerList.add("Assessor Name");
            headerList.add("Assessor Remarks");
            headerList.add("Section Head Remarks");
            headerList.add("Director Remarks");
            headerList.add("Status");
            headerList.add("Assessed-On date (Assessor Decision)");
    
            int i = 0;
            Row headerRow = sheet.createRow(0);
            for (String header : headerList) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header);
                cell.setCellStyle(style);
                i++;
            }
    
            CellStyle cs = applicantExcel.createCellStyle();
            cs.setWrapText(true);
    
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    
            int r = 1;
    
            for (ImportationMaster imp : exportItems) {
                Row row = sheet.createRow(r);
                Cell serial = row.createCell(0);
                serial.setCellValue(r);
    
                Cell applicationNo = row.createCell(1);
                applicationNo.setCellValue(imp.getId());
                applicationNo.setCellStyle(cs);
    
                Cell tradeName = row.createCell(2);
                tradeName.setCellValue(imp.getTradeName());
                tradeName.setCellStyle(cs);
    
                Cell genericaName = row.createCell(3);
                genericaName.setCellValue(imp.getGenericName());
                genericaName.setCellStyle(cs);
    
                Cell manufacturerImp = row.createCell(4);
                manufacturerImp.setCellValue(imp.getManufacturerName());
                manufacturerImp.setCellStyle(cs);
    
                Cell manufacturerCont = row.createCell(5);
                manufacturerCont.setCellValue(imp.getManufacturerCountry() != null ? imp.getManufacturerCountry().getDescEn() : "");
                manufacturerCont.setCellStyle(cs);
    
                Cell mahImp = row.createCell(6);
                mahImp.setCellValue(imp.getMah());
                mahImp.setCellStyle(cs);
    
                Cell mahCont = row.createCell(7);
                mahCont.setCellValue(imp.getMahCountry() != null ? imp.getMahCountry().getDescEn() : "");
                mahCont.setCellStyle(cs);
    
                Cell exporterName = row.createCell(8);
                exporterName.setCellValue(imp.getExporterName());
                exporterName.setCellStyle(cs);
    
                Cell exporterAddress = row.createCell(9);
                exporterAddress.setCellValue(imp.getExporterAddress());
                exporterAddress.setCellStyle(cs);
    
                Cell importerName = row.createCell(10);
                importerName.setCellValue(imp.getImporterName());
                importerName.setCellStyle(cs);
    
                Cell benificieryName = row.createCell(11);
                benificieryName.setCellValue(imp.getBeneficieryName());
                benificieryName.setCellStyle(cs);
    
                Cell submissionDate = row.createCell(12);
                submissionDate.setCellValue(imp.getSubmittedOn() != null ? df.format(imp.getSubmittedOn()) : "");
                submissionDate.setCellStyle(cs);
    
                Cell approvalDate = row.createCell(13);
                approvalDate.setCellValue(imp.getApprovedOn() != null ? df.format(imp.getApprovedOn()) : "");
                approvalDate.setCellStyle(cs);
    
                Cell quantity = row.createCell(14);
                quantity.setCellValue(imp.getQuantities());
                quantity.setCellStyle(cs);
    
                Cell conc = row.createCell(15);
                conc.setCellValue(imp.getConcentration());
                conc.setCellStyle(cs);
    
                Cell dosageFormImp = row.createCell(16);
                dosageFormImp.setCellValue(imp.getDosageForm() != null ? imp.getDosageForm().getDescEn() : "");
                dosageFormImp.setCellStyle(cs);
    
                Cell packSize = row.createCell(17);
                packSize.setCellValue(imp.getPackageSize());
                packSize.setCellStyle(cs);
    
                Cell batchNo = row.createCell(18);
                batchNo.setCellValue(imp.getBatchNo());
                batchNo.setCellStyle(cs);
    
                Cell assessorName = row.createCell(19);
    
                UserMaster assignedAssessor = imp.getAssessor();
                String assessorFullName = "";
    
                if (assignedAssessor != null) {
                    String firstName = (assignedAssessor.getFirstNameEn() == null ? "" : assignedAssessor.getFirstNameEn());
                    String lastName = (assignedAssessor.getLastNameEn() == null ? "" : assignedAssessor.getLastNameEn());
                    assessorFullName = (firstName + " " + lastName).trim();
                }
    
                assessorName.setCellValue(assessorFullName);
                assessorName.setCellStyle(cs);
    
                Cell assessorRemarks = row.createCell(20);
                String assessorRemrk = null;
                for (ImportationApproval app : (imp.getApprovals() == null ? java.util.Collections.<ImportationApproval>emptyList() : imp.getApprovals())) {
                    if (app.isAssessor() && imp.getAssessorDecisionBy() != null && app.getDecisionBy() != null
                            && app.getDecisionBy().equals(imp.getAssessorDecisionBy())) {
                        assessorRemrk = assessorRemrk != null ? assessorRemrk + "\n" + app.getRemarks() : app.getRemarks();
                    }
                }
                assessorRemarks.setCellValue(assessorRemrk != null ? Jsoup.parse(assessorRemrk).text() : "");
                assessorRemarks.setCellStyle(cs);
    
                Cell headRemarks = row.createCell(21);
                String headRemrk = null;
                for (ImportationApproval app : (imp.getApprovals() == null ? java.util.Collections.<ImportationApproval>emptyList() : imp.getApprovals())) {
                    if (app.isSectionHead() && app.getDecision() != null && app.getDecision().getId() == 12) {
                        headRemrk = headRemrk != null ? headRemrk + "\n" + app.getRemarks() : app.getRemarks();
                    }
                }
                headRemarks.setCellValue(headRemrk != null ? headRemrk : "");
                headRemarks.setCellStyle(cs);
    
                Cell directorRemarks = row.createCell(22);
                String dirRemrk = null;
                for (ImportationApproval app : (imp.getApprovals() == null ? java.util.Collections.<ImportationApproval>emptyList() : imp.getApprovals())) {
                    if (app.isDirector() && app.getDecision() != null && app.getDecision().getId() == 12) {
                        dirRemrk = dirRemrk != null ? dirRemrk + "\n" + app.getRemarks() : app.getRemarks();
                    }
                }
                directorRemarks.setCellValue(dirRemrk != null ? dirRemrk : "");
                directorRemarks.setCellStyle(cs);
    
                Cell statusImp = row.createCell(23);
                statusImp.setCellValue(imp.getInternalStatus() != null ? imp.getInternalStatus().getDescEn() : "");
                statusImp.setCellStyle(cs);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
               Cell assessorCell = row.createCell(24);
    
                String assessorValue = "";
                if (imp.getAssessorDecisionOn() != null) {
                    assessorValue = dateFormat.format(imp.getAssessorDecisionOn());
                    if (imp.getAssessorDecision() != null) {
                        assessorValue += " (" + imp.getAssessorDecision().getDescEn() + ")";
                    }                }
                assessorCell.setCellValue(assessorValue);
                assessorCell.setCellStyle(cs);
    
                r++;
            }
    
            sheet.setAutoFilter(new CellRangeAddress(0, r - 1, 0, headerList.size() - 1));
            // Widths in characters, matching the 25 headers above. Avoid scanning
            // every cell with font metrics after generating a large report.
            int[] columnWidths = {
                8, 18, 32, 32, 32, 24, 30, 24, 30, 45,
                30, 30, 18, 18, 14, 22, 24, 20, 20, 28,
                50, 50, 50, 24, 40
            };
            for (int colNum = 0; colNum < columnWidths.length; colNum++) {
                sheet.setColumnWidth(colNum, columnWidths[colNum] * 256);
            }
            headerRow.setHeightInPoints(36);
    
            final byte[] excelBytes;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                applicantExcel.write(out);
                excelBytes = out.toByteArray();
            }
    
            // HSSFWorkbook produces XLS, not XLSX. Each download needs a fresh stream.
            return new DefaultStreamedContent(null, "application/vnd.ms-excel",
                    "SpecialImportation_report.xls", Integer.valueOf(excelBytes.length)) {
                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(excelBytes);
                }
            };
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE,
                    "Failed to generate special importation Excel report for status " + status, e);
            throw new IllegalStateException("Could not generate the special importation Excel report", e);
        }
    }
}
