package dps.jsf;

import dps.ejb.ExternalStatus;
import dps.ejb.ImportationApproval;
import dps.ejb.ImportationMaster;
import dps.ejb.UserMaster;
import dps.sb.ExternalStatusFacade;
import dps.sb.ImportationMasterFacade;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
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
import javax.annotation.Resource;
import javax.annotation.PreDestroy;
import javax.enterprise.concurrent.ManagedExecutorService;
import java.util.concurrent.Future;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

/** PrimeFaces 6.1: managed background preparation, polling, then non-AJAX download. */
@ManagedBean(name = "importationExportController")
@SessionScoped
public class ImportationExportController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ImportationExportController.class.getName());

    private volatile boolean exportRequested;
    private volatile boolean exportInProgress;
    private volatile String exportError;
    private volatile ExportFile preparedFile;

    @ManagedProperty(value = "#{importationMasterController}")
    private ImportationMasterController importationMasterController;

    @Resource(lookup = "java:comp/DefaultManagedExecutorService")
    private transient ManagedExecutorService executor;
    @EJB
    private ImportationMasterFacade importationFacade;
    @EJB
    private ExternalStatusFacade extStatusFacade;
    private transient Future<?> job;
    private boolean destroyed;
    private long generation;

    protected void authorize(boolean statistical) {
        FacesContext faces = FacesContext.getCurrentInstance();
        if (faces == null) throw new SecurityException("An authenticated request is required");
        boolean allowed;
        if (statistical) {
            allowed = Boolean.TRUE.equals(faces.getApplication().evaluateExpressionGet(faces,
                    "#{usersController.current.userName eq 'admin'}", Boolean.class));
        } else {
            javax.faces.context.ExternalContext context = faces.getExternalContext();
            allowed = !context.isUserInRole("Vendor")
                    && (context.isUserInRole("Admin") || context.isUserInRole("ImportationAdmin"));
        }
        if (!allowed) throw new SecurityException("Importation export is not permitted");
    }

    protected ManagedExecutorService executor() throws Exception {
        if (executor == null) executor = (ManagedExecutorService)
                new InitialContext().lookup("java:comp/DefaultManagedExecutorService");
        return executor;
    }

    public void prepareImportation() {
        prepare(false);
    }

    public void prepareStatisticalImportation() {
        prepare(true);
    }

    public void pollExport() {
        if (exportInProgress) return;
        // Leave the DOM untouched while preparing; repaint only the final result.
        FacesContext faces = FacesContext.getCurrentInstance();
        String source = faces.getExternalContext().getRequestParameterMap().get("javax.faces.source");
        javax.faces.component.UIComponent poll = faces.getViewRoot().findComponent(source);
        javax.faces.component.UIComponent controls = poll.getParent().findComponent("exportControls");
        org.primefaces.context.RequestContext context = org.primefaces.context.RequestContext.getCurrentInstance();
        context.update(controls.getClientId(faces));
        context.execute("PF('importationExportPoll').stop();");
    }

    private synchronized void prepare(final boolean statistical) {
        authorize(statistical);
        if (destroyed || exportInProgress) return;
        // Resolve all JSF/session filters before leaving the request thread.
        final String status = statistical ? "All"
                : importationMasterController.isApprovedItems() ? "Approved"
                : importationMasterController.isPendingItems() ? "In-Progress" : "All";
        final long ticket = ++generation;
        exportRequested = true;
        exportInProgress = true;
        preparedFile = null;
        exportError = null;
        try {
            job = executor().submit(new Runnable() {
                @Override public void run() {
                    ExportFile result = null;
                    Exception failure = null;
                    try {
                        result = readFile(buildReport(status, statistical), statistical);
                    } catch (Exception ex) {
                        failure = ex;
                        LOG.log(Level.SEVERE, "Importation Excel generation failed", ex);
                    } finally {
                        synchronized (ImportationExportController.this) {
                            if (!destroyed && generation == ticket) {
                                preparedFile = result;
                                exportError = failure == null && result != null ? null
                                        : "Excel generation failed. Please try again or contact support.";
                                exportInProgress = false;
                            }
                        }
                    }
                }
            });
        } catch (Exception ex) {
            exportInProgress = false;
            exportError = "Could not start Excel generation. Please try again or contact support.";
            LOG.log(Level.SEVERE, "Could not submit importation export", ex);
        }
    }

    /** Runs exclusively on the managed worker. JTA keeps lazy relationships available
     * when the supplied EJB facades use the standard REQUIRED transaction policy. */
    protected StreamedContent buildReport(String status, boolean statistical) throws Exception {
        UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        transaction.begin();
        try {
            StreamedContent result = statistical ? generateSpecialImportationReport(3)
                    : getSpecialImportation(status);
            transaction.commit();
            return result;
        } catch (Exception ex) {
            try { transaction.rollback(); } catch (Exception rollback) { ex.addSuppressed(rollback); }
            throw ex;
        }
    }

    @PreDestroy
    public synchronized void destroy() {
        destroyed = true;
        generation++;
        if (job != null) job.cancel(true);
        preparedFile = null;
        exportInProgress = false;
    }

    private void readObject(java.io.ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
        if (exportInProgress) {
            exportInProgress = false;
            preparedFile = null;
            exportError = "Excel generation was interrupted. Please generate the report again.";
        }
    }

    private ExportFile readFile(StreamedContent generated, boolean statistical) throws IOException {
        if (generated == null) throw new IOException("Report generator returned null");
        InputStream stream = generated.getStream();
        if (stream == null) throw new IOException("Report generator returned no stream");
        byte[] bytes;
        try (InputStream input = stream; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
            bytes = output.toByteArray();
        }
        if (bytes.length == 0) throw new IOException("Report stream is empty or already consumed");
        // Preserve the generator's extension and MIME type; do not relabel XLS as XLSX.
        String name = generated.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new IOException("Report generator did not supply a filename");
        }
        String contentType = generated.getContentType();
        if (contentType == null || contentType.trim().isEmpty()) {
            contentType = "application/octet-stream";
        }
        return new ExportFile(bytes, name, contentType, statistical);
    }

    public synchronized StreamedContent getFile() {
        ExportFile snapshot = preparedFile;
        if (exportInProgress || snapshot == null) return null;
        authorize(snapshot.statistical);
        // PF 6.1 consumes and closes this stream. Never reuse a stored stream.
        return new DefaultStreamedContent(new ByteArrayInputStream(snapshot.bytes),
                snapshot.contentType, snapshot.name, Integer.valueOf(snapshot.bytes.length));
    }

    public boolean isExportRequested() { return exportRequested; }
    public boolean isExportInProgress() { return exportInProgress; }
    public boolean isExportReady() { return !exportInProgress && preparedFile != null; }
    public String getExportError() { return exportError; }
    public String getDownloadLabel() {
        ExportFile snapshot = preparedFile;
        return snapshot != null && snapshot.statistical ? "Download Statistical Excel" : "Download Excel";
    }

    public ImportationMasterController getImportationMasterController() { return importationMasterController; }
    public void setImportationMasterController(ImportationMasterController controller) {
        importationMasterController = controller;
    }

    private static final class ExportFile implements Serializable {
        private static final long serialVersionUID = 1L;
        private final byte[] bytes;
        private final String name;
        private final String contentType;
        private final boolean statistical;
        private ExportFile(byte[] bytes, String name, String contentType, boolean statistical) {
            this.bytes = bytes;
            this.name = name;
            this.contentType = contentType;
            this.statistical = statistical;
        }
    }

    private StreamedContent getSpecialImportation(String status) {
        if (!"Approved".equals(status) && !"In-Progress".equals(status) && !"All".equals(status)) {
            throw new IllegalArgumentException("Unsupported importation status: " + status);
        }
        try {
            ExternalStatus extStatus = null;
            List<ImportationMaster> exportItems = new ArrayList<>();

            if (status.equals("Approved")) {
                extStatus = extStatusFacade.find(3);
                int totalCounts = importationFacade.countRecordsByExtStatus(extStatus);
                exportItems = importationFacade.getAllRecordsByExtStatusAndAssessorWithFilter(extStatus, null, null, null, null,
                        null, null, null, null, null, null, null, 0, totalCounts);
            } else if (status.equals("In-Progress")) {
                extStatus = extStatusFacade.find(2);
                int totalCounts = importationFacade.countRecordsByExtStatus(extStatus);
                exportItems = importationFacade.getAllRecordsByExtStatusAndAssessorWithFilter(extStatus, null, null, null, null,
                        null, null, null, null, null, null, null, 0, totalCounts);
            } else if (status.equals("All")) {
                int totalCounts = importationFacade.countAllRecordsWithFilter("", "", "", "", "", "", "", "", "");
                exportItems = importationFacade.getAllRecordsWithFilter("", "", "", "", "", "", "", "", "", "", 0, totalCounts);
            }

            return new SpecialImportationExcelExporter().export(status, exportItems);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE,
                    "Failed to export special importations for status " + status, e);
            throw new IllegalStateException("Could not export special importations", e);
        }
    }

    private StreamedContent generateSpecialImportationReport(int status) {
        // Kept for existing callers. The original implementation did not filter by status.
        try {
            List<ImportationMaster> allRecords = importationFacade.getAllRecords();
            return new SpecialImportationStatisticalExcelExporter().export(allRecords);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(
                    java.util.logging.Level.SEVERE,
                    "Failed to generate statistical special importation report", e);
            throw new IllegalStateException("Could not generate statistical special importation report", e);
        }
    }

    private static final class SpecialImportationExcelExporter {
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
                    if (Thread.currentThread().isInterrupted()) throw new IllegalStateException("Export cancelled");
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

    private static final class SpecialImportationStatisticalExcelExporter {
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
                    if (Thread.currentThread().isInterrupted()) throw new IllegalStateException("Export cancelled");

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

}
