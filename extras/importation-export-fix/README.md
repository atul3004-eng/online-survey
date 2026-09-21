# Importation export fix — PrimeFaces 6.1

This older synchronous alternative is superseded by `../importation-method-fix/`,
which provides background preparation and all export logic in one Java file.

Replace the existing `dps.jsf.ImportationExportController` with the Java file here.
Replace the original importation list view with `importationList.xhtml` (keep your existing view filename).
Deploy both files together, rebuild the application, and start a fresh session.
These files belong to the supplied DPS application, not the school health survey application.

## Changes

- Report preparation runs in an AJAX request. There are no raw threads or polling.
- The report's stream is read completely and closed during preparation. Only the bytes,
  filename, and MIME type are kept in the session. Each download gets a fresh stream.
- Null, missing, empty, consumed, or unreadable streams produce an error instead of a
  Download button. Non-empty content alone does not prove that it is a valid Excel workbook.
- Repeated downloads work. Starting a new export discards the previous download.
- One download button serves the prepared report. Existing generation visibility rules
  are preserved, including Approved, All, pending, and admin-only statistical exports.
- The download is a non-AJAX, immediate request in a form containing only export controls
  and the existing request-new-importation action. It has no JavaScript monitor callback
  that could fail before the POST is sent.

## Limits and report-generator requirements

The attachment does not contain `ReportsController.getSpecialImportation(String)` or
`DetailedReportController.generateSpecialImportationReport(int)`, so their real database,
authorization, and workbook behavior cannot be verified here. Do not remove their permission
checks. UI visibility is not a substitute for authorization inside the report service.

Each generator must return a non-null StreamedContent with a readable, unconsumed stream,
correct filename/extension, and correct content type. It must finish writing the workbook
before returning, and must not write directly to the HTTP response. It must not return a
FileInputStream that was closed in a try-with-resources block. Returning a new
ByteArrayInputStream from the completed workbook bytes is compatible with this fix.

Preparation occupies one server request until completion. For reports exceeding request or
proxy timeouts, move generation into an application-server-managed background job service,
pass an immutable snapshot of filters and authorized user identity, and use job-specific
results. Do not move existing JSF controller methods onto a raw Thread without inspecting
their dependencies. Large files should use temporary file storage rather than session bytes.

## Verification

The included verification harness compiles the controller against the real PrimeFaces 6.1
API with stubs for the three DPS controllers that were not supplied. It checks failure states,
status selection, stale result clearing, and repeated downloads. It does not test the real
report generators or the live DPS application.

In the real app, test Approved/All/pending and statistical exports, then download each twice.
Check server logs if preparation reports an error. If clicking Download still returns HTML,
inspect the browser Network response for a login redirect, JSF exception, or expired session.

PrimeFaces 6.1 sources:
- https://github.com/primefaces/primefaces/blob/6_1/src/main/java/org/primefaces/component/filedownload/FileDownloadActionListener.java
- https://github.com/primefaces/primefaces/blob/6_1/src/main/java/org/primefaces/model/DefaultStreamedContent.java
