package dps.jsf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/** PrimeFaces 6.1: prepare in an AJAX request, download in a non-AJAX request. */
@ManagedBean(name = "importationExportController")
@SessionScoped
public class ImportationExportController extends ReportsController implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ImportationExportController.class.getName());

    private volatile boolean exportRequested;
    private volatile boolean exportInProgress;
    private volatile String exportError;
    private volatile ExportFile preparedFile;

    @ManagedProperty(value = "#{importationMasterController}")
    private ImportationMasterController importationMasterController;

    @ManagedProperty(value = "#{detailedReportController}")
    private DetailedReportController detailedReportController;

    public void prepareImportation() {
        prepare(false);
    }

    public void prepareStatisticalImportation() {
        prepare(true);
    }

    private synchronized boolean beginExport() {
        if (exportInProgress) return false;
        exportRequested = true;
        exportInProgress = true;
        preparedFile = null;
        exportError = null;
        return true;
    }

    private void prepare(boolean statistical) {
        if (!beginExport()) return;
        try {
            // Keep existing report code in the JSF request thread. It may depend
            // on FacesContext, the logged-in user, or request-bound persistence.
            StreamedContent generated;
            if (statistical) {
                generated = detailedReportController.generateSpecialImportationReport(3);
            } else {
                String status = importationMasterController.isApprovedItems() ? "Approved"
                        : importationMasterController.isPendingItems() ? "In-Progress" : "All";
                generated = getSpecialImportation(status);
            }
            preparedFile = readFile(generated, statistical);
        } catch (Exception ex) {
            preparedFile = null;
            exportError = "Excel generation failed. Please try again or contact support.";
            LOG.log(Level.SEVERE, "Importation Excel generation failed", ex);
        } finally {
            // Finishing is not the same as successfully preparing a file.
            exportInProgress = false;
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

    public StreamedContent getFile() {
        ExportFile snapshot = preparedFile;
        if (exportInProgress || snapshot == null) return null;
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
    public DetailedReportController getDetailedReportController() { return detailedReportController; }
    public void setDetailedReportController(DetailedReportController controller) {
        detailedReportController = controller;
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
}
