package com.kiptoo2000.surveyadmin.bean;

import com.kiptoo2000.surveyadmin.service.ExportExecutor;
import com.kiptoo2000.surveyadmin.service.SchoolHealthExcelExport;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean(name = "schoolHealthExport")
@ViewScoped
public class SchoolHealthExportController implements Serializable {
    private static final long serialVersionUID = 1L;
    // TEMPORARY: set to 0 after checking polling/progress in the browser.
    private static final long PROGRESS_PREVIEW_DELAY_MS = 5000L;
    private volatile boolean running;
    private volatile int progress;
    private volatile byte[] file;
    private volatile String status = "Export all submitted school health responses (drafts excluded).";

    public synchronized void start() {
        if (running) { return; }
        file = null;
        progress = 0;
        running = true;
        status = "Preparing Excel file...";
        ExecutorService executor = (ExecutorService) FacesContext.getCurrentInstance()
                .getExternalContext().getApplicationMap().get(ExportExecutor.KEY);
        try {
            if (executor == null) { throw new RejectedExecutionException("Export workers unavailable"); }
            executor.submit(() -> {
                try {
                    file = new SchoolHealthExcelExport().generate(value -> {
                        progress = value;
                        pauseForProgressPreview();
                    });
                    progress = 100;
                    status = "Excel file is ready to download.";
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "School health export failed", ex);
                    status = "Export failed. Please try again.";
                } finally { running = false; }
            });
        } catch (RejectedExecutionException ex) {
            running = false;
            status = "Export service is busy. Please try again shortly.";
        }
    }

    private void pauseForProgressPreview() {
        if (PROGRESS_PREVIEW_DELAY_MS <= 0) { return; }
        try {
            // Runs only on the background export worker; polling requests stay responsive.
            Thread.sleep(PROGRESS_PREVIEW_DELAY_MS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new java.util.concurrent.CancellationException("Export interrupted");
        }
    }

    public void download() throws IOException {
        byte[] bytes = file;
        if (running || bytes == null) { return; }
        FacesContext faces = FacesContext.getCurrentInstance();
        ExternalContext context = faces.getExternalContext();
        context.responseReset();
        context.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        context.setResponseHeader("Content-Disposition", "attachment; filename=\"school-health-responses.xlsx\"");
        context.setResponseHeader("Cache-Control", "no-store");
        context.setResponseContentLength(bytes.length);
        context.getResponseOutputStream().write(bytes);
        faces.responseComplete();
    }

    public boolean isRunning() { return running; }
    public boolean isReady() { return !running && file != null; }
    public int getProgress() { return progress; }
    public String getStatus() { return status; }
}
