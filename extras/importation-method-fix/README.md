# Complete importation Excel download fix

These files belong to the DPS application (GlassFish 4.1, PrimeFaces 6.1 and legacy
Apache POI), not the survey application in this repository.

1. Add both SpecialImportationExcelExporter.java and
   SpecialImportationStatisticalExcelExporter.java to package dps.jsf.
2. Replace ReportsController.getSpecialImportation(String) with the method snippet
   in getSpecialImportation.java.
3. Replace DetailedReportController.generateSpecialImportationReport(int) with
   the method snippet in generateSpecialImportationReport.java. Keep the existing
   facade injection and entity/List/StreamedContent imports. Paste this method
   inside the controller class; it is not a standalone Java class.
4. Replace dps.jsf.ImportationExportController with the included controller.
5. Replace the importation list page with importationList.xhtml, keeping its
   existing filename. For a customized page, replace its export controls and
   remove the old export thread/poll controls.
6. Rebuild and deploy the DPS application, then start a fresh session.

The second method delegates all 51 statistical columns to its stateless exporter.
It uses local formatters, fixed column widths, the XLS MIME type, an all-column
filter, and HH:mm for assessor decision time. Workbook resources are closed after
writing, and every stream request reads the completed bytes afresh. Failures
propagate instead of returning an old shared file.

Original statistical rules remain: submitted years 2021–2025, receipt date equal
 to submission date, and no status filtering. The int argument remains for existing
callers. XLS supports 65,535 data rows plus a header per sheet. Queries and approval
selection rules remain unchanged; lazy relationships must be accessible during export.

The XHTML prepares via AJAX, then displays a separate non-AJAX download button.
Preparation runs in the JSF request without raw threads or polling. Ready is set
only after a non-empty stream is read successfully. New exports clear the previous
result; failures display an error. Existing visibility rules are preserved; retain
existing report-service authorization too.

Run verification/verify.ps1 to check the preparation/download controller against
PrimeFaces 6.1 using stub DPS controllers and parse the XHTML. This does not compile
the real DPS entities/exporters or verify GlassFish/database integration. In the
actual application, test Approved, In-Progress, All and statistical exports, download
each twice, and inspect the workbook contents. Large reports still occupy a server
request and retain bytes in the session. Real database performance is unmeasured.
