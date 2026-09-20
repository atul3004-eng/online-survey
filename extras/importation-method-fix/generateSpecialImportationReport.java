public StreamedContent generateSpecialImportationReport(int status) {
    // Kept for existing callers. The original implementation did not filter by status.
    try {
        List<ImportationMaster> allRecords = importationMasterFacade.getAllRecords();
        return new SpecialImportationStatisticalExcelExporter().export(allRecords);
    } catch (Exception e) {
        java.util.logging.Logger.getLogger(getClass().getName()).log(
                java.util.logging.Level.SEVERE,
                "Failed to generate statistical special importation report", e);
        throw new IllegalStateException("Could not generate statistical special importation report", e);
    }
}