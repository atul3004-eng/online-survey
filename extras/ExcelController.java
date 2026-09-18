package JSF;

import ejb.EventMaster;
import ejb.RegisterationMaster;
import ejb.RegisterationMasterValues;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Java EE 7/8; retains the facade fields inherited by the supplied snippet. */
@ManagedBean(name = "excelController")
@ViewScoped
public class ExcelController extends EventMasterController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ExcelController.class.getName());

    @Resource(lookup = "java:comp/DefaultManagedExecutorService")
    private transient ManagedExecutorService executor;

    @ManagedProperty("#{eventMasterController}")
    private EventMasterController eventMasterController;

    private volatile boolean generationRequested;
    private volatile boolean exportInProgress;
    private volatile byte[] fileBytes;
    private volatile int progress;
    private volatile long startedAt;
    private volatile long finishedAt;
    private volatile String status = "Select an event and generate Excel.";

    public EventMasterController getEventMasterController() { return eventMasterController; }
    public void setEventMasterController(EventMasterController controller) {
        eventMasterController = controller;
    }

    public synchronized void startEventExcelThread() {
        if (exportInProgress) { return; }
        generationRequested = true;
        fileBytes = null;
        progress = 0;
        startedAt = 0;
        finishedAt = 0;

        // Capture request-dependent state BEFORE submitting background work.
        final EventMaster selectedEvent = eventMasterController == null
                ? null : eventMasterController.getEventid();
        if (selectedEvent == null) {
            status = "Please select an event first.";
            return;
        }
        final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        if (executor == null) {
            status = "The server managed executor is unavailable.";
            return;
        }
        exportInProgress = true;
        startedAt = System.currentTimeMillis();
        status = "Loading applicants...";
        try {
            executor.submit(() -> {
                try {
                    List<Map<String, String>> rows = loadApplicants(selectedEvent, locale);
                    status = "Writing Excel...";
                    byte[] generated = generateExcel(rows);
                    fileBytes = generated;
                    progress = 100;
                    status = "Excel ready: " + rows.size() + " applicant(s).";
                } catch (Exception ex) {
                    fileBytes = null;
                    status = "Export failed. Please try again or check the server log.";
                    LOG.log(Level.SEVERE, "Applicant Excel export failed", ex);
                } finally {
                    finishedAt = System.currentTimeMillis();
                    exportInProgress = false;
                }
            });
        } catch (RejectedExecutionException ex) {
            finishedAt = System.currentTimeMillis();
            exportInProgress = false;
            status = "Export service is busy. Please try again shortly.";
            LOG.log(Level.WARNING, "Applicant export rejected", ex);
        } catch (RuntimeException ex) {
            finishedAt = System.currentTimeMillis();
            exportInProgress = false;
            status = "Unable to start the export. Check the server log.";
            LOG.log(Level.SEVERE, "Unable to submit applicant export", ex);
        }
    }

    private List<Map<String, String>> loadApplicants(EventMaster event, Locale locale)
            throws IOException {
        // These must be container-managed, thread-safe EJB facade proxies.
        // No FacesContext or shared inherited applicantDetails/regdetails here.
        List<RegisterationMaster> registrations = registerationMasterFacade.getRecordByEventId(event);
        List<Map<String, String>> rows = new ArrayList<>();
        SimpleDateFormat dates = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        boolean arabic = locale != null && "ar".equals(locale.getLanguage());
        int completed = 0;
        for (RegisterationMaster registration : registrations) {
            checkInterrupted();
            Map<String, String> row = new LinkedHashMap<>();
            row.put("ApplicantId", String.valueOf(registration.getRegisterationId()));
            String registeredOn = "";
            List<RegisterationMasterValues> valuesList =
                    registerationMasterValuesFacade.getrecordByRegId(registration);
            for (RegisterationMasterValues values : valuesList) {
                checkInterrupted();
                if (values == null) { continue; }
                if (registeredOn.isEmpty() && values.getReg_date() != null) {
                    registeredOn = dates.format(values.getReg_date());
                }
                if (values.getFieldId() == null) {
                    throw new IOException("Missing field definition for applicant "
                            + registration.getRegisterationId());
                }
                String name = values.getFieldId().getFieldName();
                String arabicName = values.getFieldId().getFieldname_ar();
                if (arabic && arabicName != null && !arabicName.isEmpty()) { name = arabicName; }
                if (name == null || name.trim().isEmpty()) {
                    throw new IOException("An export field has no name");
                }
                String value = values.getFieldValues();
                if (value == null || value.isEmpty()) { value = ""; }
                else if (values.isIsaction()) {
                    value = actionFacade.getRecordValueById(Integer.parseInt(value));
                }
                // Avoid silently overwriting another field with the same label.
                if (row.containsKey(name) || "Date to Registration".equals(name)
                        || "Attended".equals(name) || "Survey Sent".equals(name)) {
                    throw new IOException("Duplicate or reserved export field label: " + name);
                }
                row.put(name, value == null ? "" : value);
            }
            row.put("Date to Registration", registeredOn);
            row.put("Attended", Boolean.TRUE.equals(registration.getAttended()) ? "Yes" : "No");
            row.put("Survey Sent", Boolean.TRUE.equals(registration.getSurvey_sent()) ? "Yes" : "No");
            rows.add(row);
            progress = (int) (60L * ++completed / registrations.size());
        }
        progress = 60;
        return rows;
    }

    private byte[] generateExcel(List<Map<String, String>> rows) throws IOException {
        Set<String> keys = new LinkedHashSet<>();
        keys.add("ApplicantId");
        for (Map<String, String> row : rows) {
            checkInterrupted();
            keys.addAll(row.keySet());
        }
        // Keep summary columns at the end, including for an empty export.
        keys.remove("Date to Registration");
        keys.remove("Attended");
        keys.remove("Survey Sent");
        keys.add("Date to Registration");
        keys.add("Attended");
        keys.add("Survey Sent");
        if (keys.size() > 16384) { throw new IOException("Too many columns for Excel"); }

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        try {
            workbook.setCompressTempFiles(true);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.BROWN.getIndex());
            headerStyle.setFont(font);
            Sheet sheet = createSheet(workbook, keys, headerStyle);
            int rowIndex = 1;
            int completed = 0;
            for (Map<String, String> data : rows) {
                checkInterrupted();
                if (rowIndex >= 1048576) {
                    sheet = createSheet(workbook, keys, headerStyle);
                    rowIndex = 1;
                }
                Row row = sheet.createRow(rowIndex++);
                int column = 0;
                for (String key : keys) {
                    String value = data.get(key);
                    if (value != null && value.length() > 32767) {
                        throw new IOException("Excel cell exceeds 32767 characters: " + key);
                    }
                    row.createCell(column++).setCellValue(value == null ? "" : value);
                }
                progress = 60 + (int) (35L * ++completed / rows.size());
            }
            progress = 95;
            checkInterrupted();
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                workbook.write(output);
                return output.toByteArray();
            }
        } finally {
            try { workbook.close(); }
            finally { workbook.dispose(); }
        }
    }

    private Sheet createSheet(SXSSFWorkbook workbook, Set<String> keys, CellStyle style) {
        Sheet sheet = workbook.createSheet("Applicants " + (workbook.getNumberOfSheets() + 1));
        sheet.createFreezePane(0, 1);
        Row header = sheet.createRow(0);
        int column = 0;
        for (String key : keys) {
            Cell cell = header.createCell(column);
            cell.setCellValue(key);
            cell.setCellStyle(style);
            sheet.setColumnWidth(column++, 24 * 256);
        }
        return sheet;
    }

    private static void checkInterrupted() throws IOException {
        if (Thread.currentThread().isInterrupted()) { throw new IOException("Export cancelled"); }
    }

    // Called on a normal, non-AJAX JSF request after the worker finishes.
    public void download() throws IOException {
        if (exportInProgress) { return; }
        byte[] bytes = fileBytes;
        if (bytes == null) { return; }
        FacesContext faces = FacesContext.getCurrentInstance();
        ExternalContext response = faces.getExternalContext();
        response.responseReset();
        response.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setResponseHeader("Content-Disposition", "attachment; filename=\"Applicants.xlsx\"");
        response.setResponseHeader("Cache-Control", "no-store");
        response.setResponseContentLength(bytes.length);
        response.getResponseOutputStream().write(bytes);
        faces.responseComplete();
    }

    public void checkExportReady() { /* Poll renders the current worker state. */ }
    public boolean isGenerationRequested() { return generationRequested; }
    public boolean isExportInProgress() { return exportInProgress; }
    public boolean isExportReady() { return !exportInProgress && fileBytes != null; }
    public int getProgress() { return progress; }
    public String getStatus() { return status; }
    public String getFormattedTime() {
        long start = startedAt;
        long end = finishedAt;
        long seconds = start == 0 ? 0 : Math.max(0, ((end == 0 ? System.currentTimeMillis() : end) - start) / 1000);
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }
}
