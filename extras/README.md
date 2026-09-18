Standalone replacement for the pasted snippet

1. Replace the external application's JSF/ExcelController.java with this Java file.
2. Replace the old export controls with excel-export.xhtml (or include the fragment).
3. The selected event must already be saved in eventMasterController.eventid before Generate Excel is clicked. If the event selector has not submitted its value, process/update that selector first.

Requirements / integration points

- Java 8+, javax-based Java EE 7/8 with java:comp/DefaultManagedExecutorService, JSF, PrimeFaces and Apache POI. This deliberately uses the application server's managed executor; no executor helper class is needed. Plain Tomcat without a managed-executor implementation needs a different executor setup.
- As in the supplied snippet, EventMasterController must expose registerationMasterFacade, registerationMasterValuesFacade and actionFacade to subclasses (protected fields or accessible getters). They must be container-managed EJB proxies, not a shared application-created EntityManager or request-bound objects.
- Facade queries must return the field definitions needed by getFieldId().getFieldName()/getFieldname_ar() already loaded. If these relationships are lazy, fetch them inside the facade transaction, e.g. with a fetch join. Exact query edits require the external entity/facade source.
- The example uses the view locale for dynamic field labels. For English-only output, replace the captured locale with Locale.ENGLISH.
- Keep only one export fragment per page because it uses a fixed poll widget name. If removing the form wrapper to embed in another form, also change the poll's running-input selector to match that form's client ID.
- No p:fileDownload or StreamedContent is needed: download() writes the completed bytes on a non-AJAX request.

Behavior

- One .xlsx download; dynamic columns are the union of all applicant fields. Missing values are blank. Additional sheets are created only at Excel's row limit; zero applicants still produces a header row.
- Loading accounts for 0–60% and row writing for 60–95%; the file is ready only after workbook serialization completes. These are phase estimates, not predicted remaining time.
- Starts are guarded, stale files are cleared, submission/generation failures are visible, elapsed time uses actual time, and temporary workbook files are disposed on failure too.
- Duplicate/reserved field labels fail visibly rather than silently losing a value. Supporting duplicate labels requires stable field IDs from the external schema.
- SXSSF bounds workbook row memory, but this compact implementation still keeps the applicant maps and completed file bytes in memory, like the supplied example. Large datasets need facade pagination / DTO loading and disk-backed download storage.

Validation

The external EventMasterController, entities, facade implementations, dependency versions and server are absent from this workspace. This replacement has not been compiled or integration-tested against that application. Verify empty data, dynamic fields present only on later applicants, Arabic labels, repeated generation, failure messages, and download on the target server.
