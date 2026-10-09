Standalone replacement for the pasted snippet

1. Add large-event-export/RegisterationMasterValuesFacade-method.java.inc to the external application's RegisterationMasterValuesFacade, then replace JSF/ExcelController.java with this Java file. Deploy both together.
2. Replace the old export controls with excel-export.xhtml (or include the fragment).
3. The selected event must already be saved in eventMasterController.eventid before Generate Excel is clicked. If the event selector has not submitted its value, process/update that selector first.

Requirements / integration points

- Java 8+, javax-based JSF, PrimeFaces and Apache POI. At your request, export generation uses a named daemon Thread and requires no ManagedExecutorService resource. Only one export can run per view. View destruction interrupts the worker; batch/row loops check interruption. An interrupt may not stop an in-flight database query immediately. Plain threads do not inherit container-managed security or transaction context; verify facade calls on your WebLogic configuration. Each EJB facade must establish its own transaction as needed. Concurrent views can each start a worker; this is not a server-wide thread pool.
- As in the supplied snippet, EventMasterController must expose registerationMasterFacade, registerationMasterValuesFacade and actionFacade to subclasses (protected fields or accessible getters). They must be container-managed EJB proxies, not a shared application-created EntityManager or request-bound objects.
- Facade queries must return the field definitions needed by getFieldId().getFieldName()/getFieldname_ar() already loaded. If these relationships are lazy, fetch them inside the facade transaction, e.g. with a fetch join. Exact query edits require the external entity/facade source.
- The example uses the view locale for dynamic field labels. For English-only output, replace the captured locale with Locale.ENGLISH.
- Keep only one export fragment per page because it uses a fixed poll widget name. If removing the form wrapper to embed in another form, also change the poll's running-input selector to match that form's client ID.
- No p:fileDownload or StreamedContent is needed: download() streams the completed temporary file on a non-AJAX request. The download button uses immediate="true" so unrelated form validation cannot block it.

Behavior

- One .xlsx download; dynamic columns are the union of all applicant fields. Missing values are blank. Additional sheets are created only at Excel's row limit; zero applicants still produces a header row.
- Loading accounts for 0–60% and row writing for 60–95%; the file is ready only after workbook serialization completes. These are phase estimates, not predicted remaining time.
- Starts are guarded, stale files are cleared, thread-start/generation failures are visible, elapsed time uses actual time, and temporary workbook files are disposed on failure too.
- Duplicate/reserved field labels fail visibly rather than silently losing a value. Supporting duplicate labels requires stable field IDs from the external schema.
- SXSSF bounds workbook row memory. The completed XLSX is stored on disk and streamed to the response, avoiding byte-array copies and storage in the view. Temporary exports are deleted on regeneration and view destruction; configure periodic cleanup of abandoned applicants-export-*.xlsx files after server crashes. Applicant maps and registration entities still remain in memory during generation.

Validation

The external EventMasterController, entities, facade implementations, dependency versions and server are absent from this workspace. This replacement has not been compiled or integration-tested against that application. Verify empty data, dynamic fields present only on later applicants, Arabic labels, repeated generation, failure messages, and download on the target server.

Large exports (5,000+ applicants)

- Action labels are cached per export, reducing repeated lookups for the same action ID.
- The loading loop now calls getValuesForExportBatch once per 250 registrations, plus the registration query. See large-event-export/README.md for integration requirements and remaining limits.
- To fix that bottleneck, provide the host application's RegisterationMasterFacade, RegisterationMasterValuesFacade, ActionFacade and entity sources. Implement ordered registration pagination and one bulk values query per page (including field definitions), then write pages using a stable column schema. Do not parallelize calls on a shared EntityManager.
- Verify database indexes against the actual event/registration foreign-key columns and query plans. This repository does not contain those facades or the schema needed for an exact query/index patch.
- Validate on the target server with 5,000+ records: loading versus workbook finishing time, actual downloaded row count, download after changing an unrelated form input, repeated generation and cleanup. Polling preserves progress visibility but does not extend the application's session lifetime.
- The replacement requires writable server temporary storage and permission to start application threads. Runtime compilation and load testing require the external host application.