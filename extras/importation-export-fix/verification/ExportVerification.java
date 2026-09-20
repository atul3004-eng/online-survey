package dps.jsf;

import java.io.*;
import java.util.Arrays;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/** Test fixtures only: do not deploy these stub DPS controllers. */
public class ExportVerification {
    private static final byte[] PAYLOAD = {80, 75, 3, 4, 10, 20, 30};
    private static int checks;

    public static void main(String[] args) throws Exception {
        ImportationMasterController filters = new ImportationMasterController();
        DetailedReportController statistics = new DetailedReportController();
        ImportationExportController controller = new ImportationExportController();
        controller.setImportationMasterController(filters);
        controller.setDetailedReportController(statistics);
        check(!controller.isExportReady() && controller.getFile() == null, "initial state");

        for (int status = 0; status < 3; status++) {
            filters.approved = status == 0;
            filters.pending = status == 1;
            ReportsController.next = content("report.xlsx");
            controller.prepareImportation();
            check(new String[]{"Approved", "In-Progress", "All"}[status].equals(ReportsController.lastStatus), "status snapshot");
            check(controller.isExportReady() && !controller.isExportInProgress(), "ready after bytes copied");
            StreamedContent first = controller.getFile();
            StreamedContent second = controller.getFile();
            check(first != second && first.getStream() != second.getStream(), "fresh streams");
            check(Arrays.equals(PAYLOAD, read(first)), "first download bytes");
            check(Arrays.equals(PAYLOAD, read(second)), "second download bytes");
            check("report.xlsx".equals(second.getName()) && second.getContentLength() == PAYLOAD.length, "metadata");
        }

        ReportsController.next = null;
        controller.prepareImportation();
        failed(controller, "null report clears old result");

        ReportsController.next = new DefaultStreamedContent(new ByteArrayInputStream(new byte[0]), "application/vnd.ms-excel", "empty.xls");
        controller.prepareImportation();
        failed(controller, "empty report");

        ReportsController.next = new DefaultStreamedContent(null, "application/vnd.ms-excel", "missing.xls");
        controller.prepareImportation();
        failed(controller, "null stream");

        ReportsController.next = new DefaultStreamedContent(new InputStream() {
            @Override public int read() throws IOException { throw new IOException("closed stream fixture"); }
        }, "application/vnd.ms-excel", "closed.xls");
        controller.prepareImportation();
        failed(controller, "unreadable stream");

        ReportsController.fail = true;
        controller.prepareImportation();
        failed(controller, "generator exception");
        ReportsController.fail = false;

        statistics.next = content("statistics.xlsx");
        controller.prepareStatisticalImportation();
        check(statistics.argument == 3 && controller.isExportReady(), "statistical generation");
        check("Download Statistical Excel".equals(controller.getDownloadLabel()), "statistical label");
        check(controller.getExportError() == null, "retry clears error");
        check(Arrays.equals(PAYLOAD, read(controller.getFile())), "statistical download");
        System.out.println("PASS: " + checks + " controller checks against PrimeFaces 6.1 (stub report generators).");
    }

    private static StreamedContent content(String filename) {
        return new DefaultStreamedContent(new ByteArrayInputStream(PAYLOAD),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", filename);
    }
    private static byte[] read(StreamedContent file) throws IOException {
        try (InputStream input = file.getStream(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            int next;
            while ((next = input.read()) != -1) output.write(next);
            return output.toByteArray();
        }
    }
    private static void failed(ImportationExportController controller, String name) {
        check(!controller.isExportReady() && !controller.isExportInProgress()
                && controller.getFile() == null && controller.getExportError() != null, name);
    }
    private static void check(boolean result, String name) {
        if (!result) throw new AssertionError(name);
        checks++;
    }
}

class ReportsController implements Serializable {
    static StreamedContent next;
    static String lastStatus;
    static boolean fail;
    public StreamedContent getSpecialImportation(String status) {
        lastStatus = status;
        if (fail) throw new IllegalStateException("report generation failure fixture");
        return next;
    }
}
class ImportationMasterController {
    boolean approved;
    boolean pending;
    public boolean isApprovedItems() { return approved; }
    public boolean isPendingItems() { return pending; }
}
class DetailedReportController {
    StreamedContent next;
    int argument;
    public StreamedContent generateSpecialImportationReport(int value) {
        argument = value;
        return next;
    }
}
