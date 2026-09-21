# Importation Excel download - one Java file

Copy `ImportationExportController.java` into the DPS application's `dps.jsf` package,
replacing the existing class. All export-specific Java code is in this file: job
submission/state, data queries, regular and statistical workbook builders, and fresh
download streams. The two workbook builders are private nested classes.

Replace the importation list view with `importationList.xhtml` (keep your original
view filename), or copy its export controls and poll into your customized page.
Deploy the Java and XHTML changes together and start a fresh session.

Remove the older export-only classes if previously installed:
`SpecialImportationReportController`, `SpecialImportationExcelExporter`,
`SpecialImportationStatisticalExcelExporter`, and
`SpecialImportationStatisticalReportController`.
Leave the application's existing `ReportsController` and `DetailedReportController`
unchanged. The old standalone method snippets are no longer needed.
Do not deploy anything under `verification/`.

## Background preparation

The AJAX action checks export permission and captures the selected status, then
submits to the application server's `java:comp/DefaultManagedExecutorService` and
returns. Database queries and workbook creation run on the managed worker thread.
The worker does not read FacesContext or the session's filter controller.
The page displays a preparing message and polls every two seconds while the job
is in progress. Successful completion shows a separate non-AJAX Download button.
Each click gets a new stream, so the same prepared file can be downloaded again.

Only one export runs per session. A new export clears the prior file; generation
failure or executor rejection leaves no stale download. Session destruction cancels
the job and prevents a late result from being published. If a session is restored
while a job was running, the user is asked to generate the report again.
Permissions are checked before preparation and download using the page's existing
rules: regular exports require Admin or ImportationAdmin and exclude Vendor;
statistical exports require the current username `admin`.

## Runtime and existing behavior

Requires Java 8 and a Java EE 7+ application server with managed concurrency, EJB,
and JTA, plus the existing PrimeFaces 6.1, Apache POI 3.14, and jsoup dependencies.
Plain Tomcat without those services is not sufficient. No raw thread pool is created.

Facade imports use `dps.sb.ImportationMasterFacade` and `dps.sb.ExternalStatusFacade`;
entity imports use `dps.ejb`. These are assumed from the supplied code; actual DPS
facades/entities are absent in this repository. Keep service-level permission checks.

The worker starts a UserTransaction around queries and workbook construction so
standard REQUIRED EJB facades share the persistence context while lazy relationships
are read. Verify that the real facades use JTA and REQUIRED (rather than
REQUIRES_NEW/NOT_SUPPORTED), or explicitly fetch the report relationships in them.
Configure transaction timeouts and managed-executor capacity for expected report sizes.
Cancellation interrupts the worker; whether an in-flight database query stops depends
on the database driver. The workbook loops check interruption between records.

Regular reports retain Approved, In-Progress, and All queries and all 25 columns.
Statistical reports retain all 51 columns and the original 2021-2025 submission-year
filter. The statistical status parameter remains unused, matching the supplied code.
Receipt date still equals submission date. Both outputs remain XLS with correct MIME
type, fixed widths, and all-column filtering. XLS supports 65,535 data rows plus headers.
Prepared files are held as session bytes; large reports still require adequate heap.

## Verification

Run `verification/verify.ps1`. It compiles the entire consolidated Java file against
real PrimeFaces 6.1, Java EE 7, POI 3.14, and jsoup APIs, using test-only DPS entity and
facade stubs. It verifies asynchronous execution, captured filters, duplicate job
prevention, failures, rejection, retry, session destruction, download authorization
hooks, repeated downloads, and the 25-/51-column workbooks. It also parses XHTML.

This is not a live DPS integration test: verify injected facades, real authorization,
JTA/lazy relationships, and polling on the target application server. Test Approved,
All, In-Progress, and statistical generation and download each file twice.
