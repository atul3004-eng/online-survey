# Independent importation Excel reports

Leave ReportsController and DetailedReportController unchanged.

Add these three classes to the DPS application's dps.jsf package:
- SpecialImportationReportController.java: contains both getSpecialImportation(String status)
  and generateSpecialImportationReport(int status)
- SpecialImportationExcelExporter.java
- SpecialImportationStatisticalExcelExporter.java

Replace ImportationExportController.java with the included class. It now injects
the combined specialImportationReportController bean; it neither extends nor calls the existing report controllers.
Use the included importationList.xhtml (keep your existing page filename), or copy
its export/download controls into your customized page and remove old export polling.
Do not apply getSpecialImportation.java or generateSpecialImportationReport.java:
those standalone method snippets are from the previous approach and are not needed.

Facade imports use the supplied dps.sb package. The class names assumed from the
existing variables are ImportationMasterFacade and ExternalStatusFacade. Confirm
these against your application; the actual facade sources are absent here. Entity
imports use dps.ejb. Retain any application-specific service authorization checks.
Rebuild, deploy, and start a fresh session after installing the classes and page.

Regular reports retain the Approved, In-Progress and All queries. Statistical reports
retain all 51 columns and the original 2021–2025 submission-year filter. The int status
argument remains unused, as in the supplied method. Receipt date still equals submission
date. Exporters use fixed widths, local formatters, correct XLS MIME type, all-column
filtering, and fresh streams. Statistical assessor decision time uses HH:mm.

Preparation uses an AJAX request; downloading uses a separate non-AJAX request.
Only successful preparation enables downloading. Failures clear the previous result.
There are no raw threads or polling. XLS supports 65,535 data rows plus the header.
Large reports occupy a server request and keep bytes in the session; lazy entity
relationships must remain available during generation.

Verification: run verification/verify.ps1. It tests the download controller against
PrimeFaces 6.1 with stub report beans and parses XHTML. It does not compile or test the
real DPS facades, entities or exporters. In DPS, test all three regular statuses and
the statistical report, inspect workbook contents, and download each result twice.

If you installed the earlier split version, remove SpecialImportationStatisticalReportController.java.
The XHTML bindings remain unchanged.

