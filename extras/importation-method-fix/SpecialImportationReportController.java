package dps.jsf;

import dps.ejb.ImportationMaster;
import dps.sb.ImportationMasterFacade;
import dps.sb.ExternalStatusFacade;
import javax.ejb.EJB;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.StreamedContent;

import dps.ejb.ExternalStatus;
import java.util.ArrayList;

@ManagedBean(name = "specialImportationReportController")
@SessionScoped
public class SpecialImportationReportController implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private ImportationMasterFacade importationFacade;

    @EJB
    private ExternalStatusFacade extStatusFacade;

public StreamedContent getSpecialImportation(String status) {
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

public StreamedContent generateSpecialImportationReport(int status) {
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
}

