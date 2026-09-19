# Submission confirmation email

After a successful school health submission commits, the application sends a plain-text UTF-8 receipt to `contactEmail`, in the survey's selected language. It includes the submission reference, not the answers or private return code. Saving drafts and reopening submissions do not send email.

Email bodies use FreeMarker templates in `survey/src/main/resources/templates/email/school-health-confirmation_en.ftl` and `school-health-confirmation_ar.ftl`. The template model contains `responseId`; `?c` keeps its numeric value ungrouped. Edit these files to change the email text, then rebuild and redeploy. Subjects and completion-page notices remain in the language resource bundles.

The SMTP connection defaults to `localhost:25` without username/password authentication or required STARTTLS. Set the sender address for the Tomcat/application-server process, then restart it. Host and port overrides are optional:

| Variable | Value |
| --- | --- |
| `SURVEY_SMTP_HOST` | SMTP server hostname; defaults to localhost |
| `SURVEY_SMTP_PORT` | SMTP port; defaults to 25 |
| `SURVEY_MAIL_FROM` | Approved sender email address |
| `SURVEY_SMTP_STARTTLS` | Optional: set to true if the relay requires STARTTLS; defaults to false |

No SMTP credentials are used. If STARTTLS is enabled, it is required and the server certificate is verified. Set the variables in the server's launch environment (for IntelliJ, its Tomcat run configuration), not only in a separate terminal. `SURVEY_MAIL_FROM` is required before messages can be sent.

Email is attempted once per successful submission, with 10-second connection/read/write timeouts. There is no delivery retry queue. A mail failure leaves the submission saved and displays a localized notice on the completion page. SMTP acceptance does not guarantee inbox delivery. Failures are logged with the response ID and exception type, without participant addresses or credentials.

Build with `mvn -pl survey package` and redeploy the WAR. Automated tests capture messages without contacting an SMTP server. Verify actual delivery with a test participant after configuring SMTP.
