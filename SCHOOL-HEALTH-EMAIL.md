# School health survey email

Configure only the SMTP server IP and port in `survey/src/main/resources/school-health-mail.properties`:

```properties
SURVEY_SMTP_HOST=127.0.0.1
SURVEY_SMTP_PORT=25
```

These settings match the local FakeSMTP GUI. No SMTP credentials or sender-email setting are required. The application supplies `school-health@localhost` as the sender. This configuration uses a local/test SMTP server or an unauthenticated relay.

The participant email in Section 1 remains mandatory. Saving a draft sends a resume link; submitting sends a receipt. Invalid or missing addresses block saving and submission. Mail failures leave the response saved and display a message.

English and Arabic FreeMarker `.ftl` files are directly in `survey/src/main/resources/templates`. Labels and subjects are in the root `messages_en.properties` and `messages_ar.properties` files.

Rebuild with `mvn -pl survey -am package` and reload the application after changes. Tests capture mail without contacting SMTP. FakeSMTP captures messages locally rather than delivering them to real inboxes.
