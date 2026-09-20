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