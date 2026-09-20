package dps.jsf;

import dps.ejb.ImportationMaster;
import dps.ejb.ImportationApproval;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jsoup.Jsoup;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/** Stateless XLS builder; formatters and report data are local to each call. */
public final class SpecialImportationStatisticalExcelExporter {
    public StreamedContent export(List<ImportationMaster> allRecords) {
    if (allRecords == null) throw new IllegalArgumentException("Export records must not be null");
    try (HSSFWorkbook applicantExcel = new HSSFWorkbook()) {
    HSSFSheet sheet = applicantExcel.createSheet("Special Importation");
    sheet.createFreezePane(0, 1);
    
            HSSFFont boldFont = applicantExcel.createFont();
            boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            CellStyle style = applicantExcel.createCellStyle();
            style.setFont(boldFont);
            style.setWrapText(true);
            HSSFPalette palette = applicantExcel.getCustomPalette();
    
    // get the color which most closely matches the color you want to use
    HSSFColor myColor = palette.findSimilarColor(237, 203, 203);
    // get the palette index of that color
    short palIndex = myColor.getIndex();
    // code to get the style for the cell goes here
    style.setFillForegroundColor(palIndex);
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    style.setAlignment(CellStyle.ALIGN_CENTER);
    
            //Set Header Information 
            Header topHeader = sheet.getHeader();
            topHeader.setLeft("*** Special Importation Report ***");
            topHeader.setCenter(HSSFHeader.font("Calibri", "Bold")
                    + HSSFHeader.fontSize((short) 14) + "SAMPLE ORDER");
    
            List<String> headerList = new ArrayList<>();
    
            headerList.add("SN");
            headerList.add("Importation no.");
            headerList.add("Registration no. (if any)");
            headerList.add("Trade name");
            headerList.add("Generic name / ingredients");
            headerList.add("Current status");
            headerList.add("Applicant");
            headerList.add("Agent name");
            headerList.add("MAH");
            headerList.add("Manufacturer");
            headerList.add("Beneficiary / purchaser names");
            headerList.add("Importer Name");
            headerList.add("Exporter Name");
            headerList.add("Submission time");
            headerList.add("Submission date");
            headerList.add("Recieval time");
            headerList.add("Recieval date");
            headerList.add("Process time from submission till recieval");
    
            headerList.add("Assign date to assessor");
            headerList.add("Process time from recieval till assigning");
            headerList.add("Assessor 1 name");
            headerList.add("Assesor decision");
            headerList.add("Assesor Remarks");
            headerList.add("Date of taking assessor decision");
            headerList.add("Time of taking assessor decision");
            headerList.add("Process time from assigning till taking decision");
            headerList.add("Section head decision (approve / not approve / transfer to pricing / transfer to inspection)");
            headerList.add("Date of transfer to pricing / inspection");
            headerList.add("Time of transfer to pricing / inspection");
            headerList.add("Process time at section head till transfer");
            headerList.add("Assign date to pricing");
            headerList.add("Assign time to pricing");
            headerList.add("Date of taking pricing dcision");
            headerList.add("Time of taking pricing dcision");
            headerList.add("Process time from transfer to pricing till pricing decision (if any)");
    
            headerList.add("Assign date to inspection");
            headerList.add("Assign time to inspection");
            headerList.add("Date of taking inspection decision");
            headerList.add("Time of inspection pricing decision");
            headerList.add("Process time from transfer to inspection till inspection decision (if any)");
    
            headerList.add("Section head decision");
            headerList.add("Section head decision date");
            headerList.add("Section head decision time");
            headerList.add("Process time from transfer to section head till assign to director");
            headerList.add("Director decision");
            headerList.add("Director decision date");
            headerList.add("Director decision time");
            headerList.add("Process time from transfer to director till director decision");
            headerList.add("Final decision");
            headerList.add("Overall process time from time of submission");
            headerList.add("Overall process time from time of receival");
    
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
    
            int r = 1;
            SimpleDateFormat tdf = new SimpleDateFormat("HH:mm");
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat ydf = new SimpleDateFormat("yyyy");
            String[] allowedYears = new String[]{"2021", "2022", "2023", "2024", "2025"};
            List<String> years = Arrays.asList(allowedYears);
    
            for (ImportationMaster im : allRecords) {
    
                Date submittedOn = im.getSubmittedOn();
                if (submittedOn == null) {
                    continue;
                }
                String submittedYear = ydf.format(submittedOn);
    
                if (years.contains(submittedYear)) {
                    
                    Row row = sheet.createRow(r);
                    Cell serial = row.createCell(0);
                    serial.setCellValue(r);
    
                    Cell importationNo = row.createCell(1);
                    importationNo.setCellValue(im.getId());
    
                    Cell registrationNo = row.createCell(2);
                    registrationNo.setCellValue("");
    
                    Cell tradeName = row.createCell(3);
                    tradeName.setCellValue(im.getTradeName());
    
                    Cell genericName = row.createCell(4);
                    genericName.setCellValue(im.getGenericName());
    
                    Cell currentStatus = row.createCell(5);
                    currentStatus.setCellValue(im.getInternalStatus() != null ? im.getInternalStatus().getDescEn() : "");
    
                    Cell applicant = row.createCell(6);
                    applicant.setCellValue(im.getSubmittedBy() != null ? im.getSubmittedBy().getCompanyNameEn() : "");
    
                    Cell agentName = row.createCell(7);
                    agentName.setCellValue(im.getSubmittedBy() != null ? im.getSubmittedBy().getCompanyNameEn() : "");
    
                    Cell mah = row.createCell(8);
                    mah.setCellValue(im.getMah());
    
                    Cell manufacturer = row.createCell(9);
                    manufacturer.setCellValue(im.getManufacturerName());
    
                    Cell beneName = row.createCell(10);
                    beneName.setCellValue(im.getBeneficieryName());
    
                    Cell impName = row.createCell(11);
                    impName.setCellValue(im.getImporterName());
    
                    Cell expName = row.createCell(12);
                    expName.setCellValue(im.getExporterName());
    
                    Cell submissionTime = row.createCell(13);
                    submissionTime.setCellValue(tdf.format(submittedOn));
    
                    Cell submissionDate = row.createCell(14);
                    submissionDate.setCellValue(df.format(submittedOn));
                    submissionDate.setCellStyle(cs);
    
                    Cell receivalTime = row.createCell(15);
                    Date rDate = submittedOn;
    
                    receivalTime.setCellValue(tdf.format(rDate));
    
                    Cell receivalDate = row.createCell(16);
                    receivalDate.setCellValue(df.format(rDate));
    
                    Cell processTimeFrmSubToRec = row.createCell(17);
                    long diffInMillies = Math.abs(submittedOn.getTime() - rDate.getTime());
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    processTimeFrmSubToRec.setCellValue(diff + " Days");
                    processTimeFrmSubToRec.setCellStyle(cs);
    
                    Cell asrAsgnDate = row.createCell(18);
                    asrAsgnDate.setCellValue(im.getAssessorAssignedOn() != null ? df.format(im.getAssessorAssignedOn()) : "");
    
                    Cell processTimeFrmRecToAsrAsgnDate = row.createCell(19);
                    if (im.getAssessorAssignedOn() != null) {
                        long diffAInMillies = Math.abs(im.getAssessorAssignedOn().getTime() - rDate.getTime());
                        long diffA = TimeUnit.DAYS.convert(diffAInMillies, TimeUnit.MILLISECONDS);
                        processTimeFrmRecToAsrAsgnDate.setCellValue(diffA + " Days");
                    } else {
                        processTimeFrmRecToAsrAsgnDate.setCellValue("");
                    }
                    processTimeFrmRecToAsrAsgnDate.setCellStyle(cs);
    
                    Cell assrName = row.createCell(20);
                    assrName.setCellValue(im.getAssessor() != null ? im.getAssessor().getFirstNameEn() + " " + im.getAssessor().getLastNameEn() : "");
    
                    Cell assrDesn = row.createCell(21);
                    assrDesn.setCellValue(im.getAssessorDecision() != null ? im.getAssessorDecision().getDescEn() : "");
    
                    Date aDesnDate = null, trnsPrngAsnDate = null, trnsInsAsnDate = null, trnsPrngDate = null,
                            trnsInsDate = null, pAsnDate = null, pDesnDate = null,
                            iAsnDate = null, iDesnDate = null, headAsnDate = null, headDesnDate = null, dirAsnDate = null, dirDesnDate = null;
    
                    String headTnsferDesn = null, headDesn = null, dirDesn = null, assessorRemarks = null;
    
                    for (ImportationApproval app : (im.getApprovals() == null ? java.util.Collections.<ImportationApproval>emptyList() : im.getApprovals())) {
                        if (app == null) continue;
                        if (app.isSectionHead()) {
                            if (app.getDecision() != null) {
                                int id = app.getDecision().getId();
                                switch (id) {
                                    case 23:
                                        if (trnsPrngAsnDate == null || (app.getAssignedOn() != null && trnsPrngAsnDate.after(app.getAssignedOn()))) {
                                            trnsPrngAsnDate = app.getAssignedOn();
                                        }
                                        if (trnsPrngDate == null || (app.getDecisionOn() != null && trnsPrngDate.after(app.getDecisionOn()))) {
                                            trnsPrngDate = app.getDecisionOn();
                                            headTnsferDesn = app.getDecision().getDescEn();
                                        }
    
                                        break;
                                    case 43:
                                        if (trnsInsAsnDate == null || (app.getAssignedOn() != null && trnsInsAsnDate.after(app.getAssignedOn()))) {
                                            trnsInsAsnDate = app.getAssignedOn();
                                        }
                                        if (trnsInsDate == null || (app.getDecisionOn() != null && trnsInsDate.after(app.getDecisionOn()))) {
                                            trnsInsDate = app.getDecisionOn();
                                            headTnsferDesn = app.getDecision().getDescEn();
                                        }
                                        break;
                                    default:
                                        if (headAsnDate == null || (app.getAssignedOn() != null && headAsnDate.after(app.getAssignedOn()))) {
                                            headAsnDate = app.getAssignedOn();
                                        }
                                        if (headDesnDate == null || (app.getDecisionOn() != null && headDesnDate.after(app.getDecisionOn()))) {
                                            headDesnDate = app.getDecisionOn();
                                            headDesn = app.getDecision().getDescEn();
                                        }
                                        break;
                                }
                            }
                        } else if (app.isPricingAssessor()) {
                            if (pAsnDate == null || (app.getAssignedOn() != null && pAsnDate.after(app.getAssignedOn()))) {
                                pAsnDate = app.getAssignedOn();
                            }
                            if (pDesnDate == null || (app.getDecisionOn() != null && pDesnDate.after(app.getDecisionOn()))) {
                                pDesnDate = app.getDecisionOn();
                            }
                        } else if (app.isInspectionUser()) {
                            if (iAsnDate == null || (app.getAssignedOn() != null && iAsnDate.after(app.getAssignedOn()))) {
                                iAsnDate = app.getAssignedOn();
                            }
                            if (iDesnDate == null || (app.getDecisionOn() != null && iDesnDate.after(app.getDecisionOn()))) {
                                iDesnDate = app.getDecisionOn();
                            }
                        } else if (app.isDirector()) {
                            if (dirAsnDate == null || (app.getAssignedOn() != null && dirAsnDate.after(app.getAssignedOn()))) {
                                dirAsnDate = app.getAssignedOn();
                            }
                            if (dirDesnDate == null || (app.getDecisionOn() != null && dirDesnDate.after(app.getDecisionOn()))) {
                                dirDesnDate = app.getDecisionOn();
                                dirDesn = app.getDecision() != null ? app.getDecision().getDescEn() : null;
                            }
                        } else if (app.isAssessor()) {
                            if (app.getDecisionBy() != null && im.getAssessor() != null && app.getDecisionBy().equals(im.getAssessor())) {
                                assessorRemarks = app.getRemarks();
                            }
                        }
                    }
    
                    Cell assrRemarks = row.createCell(22);                    
                    assrRemarks.setCellValue(assessorRemarks != null ? Jsoup.parse(assessorRemarks).text() : "");
    
                    aDesnDate = im.getAssessorDecisionOn();
    
                    Cell asrDsnDate = row.createCell(23);
                    asrDsnDate.setCellValue(aDesnDate != null ? df.format(aDesnDate) : "");
    
                    Cell asrDsnTime = row.createCell(24);
                    asrDsnTime.setCellValue(aDesnDate != null ? tdf.format(aDesnDate) : "");
    
                    Cell processTimeFrmAsrAsgnToDesn = row.createCell(25);
                    if (im.getAssessorAssignedOn() != null && aDesnDate != null) {
                        long diffAInMillies = Math.abs(aDesnDate.getTime() - im.getAssessorAssignedOn().getTime());
                        long diffA = TimeUnit.DAYS.convert(diffAInMillies, TimeUnit.MILLISECONDS);
                        processTimeFrmAsrAsgnToDesn.setCellValue(diffA + " Days");
                    } else {
                        processTimeFrmAsrAsgnToDesn.setCellValue("");
                    }
                    processTimeFrmAsrAsgnToDesn.setCellStyle(cs);
    
                    Cell headDesnCell = row.createCell(26);
                    headDesnCell.setCellValue(headTnsferDesn != null ? headTnsferDesn : "");
    
                    Cell prcgInsDate = row.createCell(27);
                    Cell prcgInsTime = row.createCell(28);
                    Cell processTimeFrmHeadToTrnsf = row.createCell(29);
                    if (trnsPrngDate != null) {
                        prcgInsDate.setCellValue(df.format(trnsPrngDate));
                        prcgInsTime.setCellValue(tdf.format(trnsPrngDate));
                        if (trnsPrngAsnDate != null) {
                            long diffAInMillies = Math.abs(trnsPrngDate.getTime() - trnsPrngAsnDate.getTime());
                            long diffA = TimeUnit.DAYS.convert(diffAInMillies, TimeUnit.MILLISECONDS);
                            processTimeFrmHeadToTrnsf.setCellValue(diffA + " Days");
                        } else {
                            processTimeFrmHeadToTrnsf.setCellValue("");
                        }
                    } else if (trnsInsDate != null) {
                        prcgInsDate.setCellValue(df.format(trnsInsDate));
                        prcgInsTime.setCellValue(tdf.format(trnsInsDate));
                        if (trnsInsAsnDate != null) {
                            long diffAInMillies = Math.abs(trnsInsDate.getTime() - trnsInsAsnDate.getTime());
                            long diffA = TimeUnit.DAYS.convert(diffAInMillies, TimeUnit.MILLISECONDS);
                            processTimeFrmHeadToTrnsf.setCellValue(diffA + " Days");
                        } else {
                            processTimeFrmHeadToTrnsf.setCellValue("");
                        }
                    } else {
                        prcgInsDate.setCellValue("");
                        prcgInsTime.setCellValue("");
                        processTimeFrmHeadToTrnsf.setCellValue("");
                    }
    
                    Cell prcgADate = row.createCell(30);
                    prcgADate.setCellValue(pAsnDate != null ? df.format(pAsnDate) : "");
    
                    Cell prcgATime = row.createCell(31);
                    prcgATime.setCellValue(pAsnDate != null ? tdf.format(pAsnDate) : "");
    
                    Cell prcgDDate = row.createCell(32);
                    prcgDDate.setCellValue(pDesnDate != null ? df.format(pDesnDate) : "");
    
                    Cell prcgDTime = row.createCell(33);
                    prcgDTime.setCellValue(pDesnDate != null ? tdf.format(pDesnDate) : "");
    
                    Cell processTimeFrmPriTrfToDesn = row.createCell(34);
                    if (pAsnDate != null && pDesnDate != null) {
                        long diffAInMillies = Math.abs(pDesnDate.getTime() - pAsnDate.getTime());
                        long diffA = TimeUnit.DAYS.convert(diffAInMillies, TimeUnit.MILLISECONDS);
                        processTimeFrmPriTrfToDesn.setCellValue(diffA + " Days");
                    } else {
                        processTimeFrmPriTrfToDesn.setCellValue("");
                    }
    
                    Cell inspADate = row.createCell(35);
                    inspADate.setCellValue(iAsnDate != null ? df.format(iAsnDate) : "");
    
                    Cell inspATime = row.createCell(36);
                    inspATime.setCellValue(iAsnDate != null ? tdf.format(iAsnDate) : "");
    
                    Cell inspDDate = row.createCell(37);
                    inspDDate.setCellValue(iDesnDate != null ? df.format(iDesnDate) : "");
    
                    Cell inspDTime = row.createCell(38);
                    inspDTime.setCellValue(iDesnDate != null ? tdf.format(iDesnDate) : "");
    
                    Cell processTimeFrmInspTrfToDesn = row.createCell(39);
                    if (iAsnDate != null && iDesnDate != null) {
                        long diffAInMillies = Math.abs(iDesnDate.getTime() - iAsnDate.getTime());
                        long diffA = TimeUnit.DAYS.convert(diffAInMillies, TimeUnit.MILLISECONDS);
                        processTimeFrmInspTrfToDesn.setCellValue(diffA + " Days");
                    } else {
                        processTimeFrmInspTrfToDesn.setCellValue("");
                    }
    
                    Cell headFinalDesn = row.createCell(40);
                    headFinalDesn.setCellValue(headDesn != null ? headDesn : "");
    
                    Cell headFinalDesnDate = row.createCell(41);
                    headFinalDesnDate.setCellValue(headDesnDate != null ? df.format(headDesnDate) : "");
    
                    Cell headFinalDesnTime = row.createCell(42);
                    headFinalDesnTime.setCellValue(headDesnDate != null ? tdf.format(headDesnDate) : "");
    
                    Cell processTimeFrmHeadAssToDesn = row.createCell(43);
                    if (headAsnDate != null && headDesnDate != null) {
                        long diffAInMillies = Math.abs(headDesnDate.getTime() - headAsnDate.getTime());
                        long diffA = TimeUnit.DAYS.convert(diffAInMillies, TimeUnit.MILLISECONDS);
                        processTimeFrmHeadAssToDesn.setCellValue(diffA + " Days");
                    } else {
                        processTimeFrmHeadAssToDesn.setCellValue("");
                    }
    
                    Cell dirFinalDesn = row.createCell(44);
                    dirFinalDesn.setCellValue(dirDesn != null ? dirDesn : "");
    
                    Cell dirFinalDesnDate = row.createCell(45);
                    dirFinalDesnDate.setCellValue(dirDesnDate != null ? df.format(dirDesnDate) : "");
    
                    Cell dirFinalDesnTime = row.createCell(46);
                    dirFinalDesnTime.setCellValue(dirDesnDate != null ? tdf.format(dirDesnDate) : "");
    
                    Cell processTimeFrmDirAssToDesn = row.createCell(47);
                    if (dirAsnDate != null && dirDesnDate != null) {
                        long diffAInMillies = Math.abs(dirDesnDate.getTime() - dirAsnDate.getTime());
                        long diffA = TimeUnit.DAYS.convert(diffAInMillies, TimeUnit.MILLISECONDS);
                        processTimeFrmDirAssToDesn.setCellValue(diffA + " Days");
                    } else {
                        processTimeFrmDirAssToDesn.setCellValue("");
                    }
    
                    Cell finalDecision = row.createCell(48);
                    finalDecision.setCellValue(im.getInternalStatus() != null ? im.getInternalStatus().getDescEn() : "");
    
                    Cell processTimeFrmSubToDesn = row.createCell(49);
                    if (dirDesnDate != null) {
                        long diffAInMillies = Math.abs(dirDesnDate.getTime() - submittedOn.getTime());
                        long diffA = TimeUnit.DAYS.convert(diffAInMillies, TimeUnit.MILLISECONDS);
                        processTimeFrmSubToDesn.setCellValue(diffA + " Days");
                    } else {
                        processTimeFrmSubToDesn.setCellValue("");
                    }
    
                    Cell processTimeFrmRecToDesn = row.createCell(50);
                    if (dirDesnDate != null) {
                        long diffAInMillies = Math.abs(dirDesnDate.getTime() - submittedOn.getTime());
                        long diffA = TimeUnit.DAYS.convert(diffAInMillies, TimeUnit.MILLISECONDS);
                        processTimeFrmRecToDesn.setCellValue(diffA + " Days");
                    } else {
                        processTimeFrmRecToDesn.setCellValue("");
                    }
    
                    for (Cell cell : row) { cell.setCellStyle(cs); }
                    r++;
    
                }
            }
            sheet.setAutoFilter(new CellRangeAddress(0, r - 1, 0, headerList.size() - 1));
            // Fixed widths avoid measuring every cell in large reports.
            for (int column = 0; column < headerList.size(); column++) {
                String header = headerList.get(column).toLowerCase(java.util.Locale.ENGLISH);
                int width = 28;
                if (column == 0) width = 8;
                else if (header.contains("remarks")) width = 50;
                else if (header.contains("process time")) width = 38;
                else if (header.contains("decision") || header.contains("dcision")) width = 38;
                else if (header.contains("date")) width = 20;
                else if (header.contains("time")) width = 16;
                else if (header.contains("name") || header.contains("manufacturer")) width = 32;
                sheet.setColumnWidth(column, width * 256);
            }
            headerRow.setHeightInPoints(72);
            final byte[] excelBytes;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                applicantExcel.write(out);
                excelBytes = out.toByteArray();
            }
            return new DefaultStreamedContent(null, "application/vnd.ms-excel",
                    "Special Importation.xls", Integer.valueOf(excelBytes.length)) {
                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(excelBytes);
                }
            };
        } catch (Exception e) {
            throw new IllegalStateException("Could not build the statistical importation Excel report", e);
        }
    }
}
