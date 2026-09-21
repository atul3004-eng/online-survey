# School health survey saving and PDFs

Run `school-health-drafts-mysql.sql` once on an existing MySQL database before deploying the updated WAR. Fresh databases use `mysql-schema.sql`. No database migration has been applied by this change.

Every survey section includes Save and Continue Later. Partial answers are stored as DRAFT without required-field/email validation. The respondent must keep the displayed private return code. Enter that code on the start page to load the database answers (including checkboxes) in a later session or on another device. The code grants access to the response; keep it private. Starting a new survey creates a new code.

Submitting promotes the same response to SUBMITTED and records the submission time. Submitted responses cannot be overwritten. Reopening a submitted response goes to the completion page, where its PDF can be downloaded again. Existing pre-migration submissions remain SUBMITTED; they have no return code.

The start-page Open blank questionnaire (PDF) button opens an existing PDF in a new browser tab, selected using the survey locale. It never generates a blank PDF. English uses `school-health-questionnaire.pdf`; Arabic uses `school-health-questionnaire-ar.pdf`.

Store both PDFs in `survey/src/main/resources/docs/`. The existing English questionnaire is retained and `school-health-questionnaire-ar.pdf` is a clearly labeled dummy PDF for testing locale selection. Replace the dummy with the approved Arabic questionnaire before production use. Missing files show a localized unavailable message without falling back to another language. Browser PDF settings determine whether inline PDFs open or download.

The completion-page download reads answers from the database and generates a PDF with question labels, readable option labels and an embedded font supporting Arabic. Optional unanswered questions are marked Not answered.

Verification: `mvn -pl survey -am package`. The isolated H2 integration test covers creating/updating/reloading drafts, replacing selections, submission, immutable submitted records, duplicate-contact rejection, invalid return codes and PDF generation. Facelets were parsed as XML and all seven save buttons checked. Browser/app-server interaction still needs a smoke test after applying the migration and deploying.
