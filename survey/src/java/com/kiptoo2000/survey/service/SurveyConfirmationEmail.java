package com.kiptoo2000.survey.service;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/** Sends a receipt only after the response transaction has committed. */
public class SurveyConfirmationEmail {
    private static final Logger LOG = Logger.getLogger(SurveyConfirmationEmail.class.getName());
    private static final Configuration TEMPLATES = templateConfiguration();
    private static final Properties MAIL_SETTINGS = loadMailSettings();
    private final Session session;
    private final String from;

    public SurveyConfirmationEmail() {
        this(configuredSession(), "school-health@localhost");
    }

    public SurveyConfirmationEmail(Session session, String from) {
        this.session = session;
        this.from = from;
    }

    public boolean send(String recipient, Long responseId, String language) {
        return sendMessage(recipient, responseId, language, "confirmation", "schoolHealth.emailSubject",
                Collections.singletonMap("responseId", responseId));
    }

    public boolean sendResumeLink(String recipient, Long responseId, String language, String resumeUrl) {
        return sendMessage(recipient, responseId, language, "resume", "schoolHealth.resumeSubject",
                Collections.singletonMap("resumeUrl", resumeUrl));
    }

    private boolean sendMessage(String recipient, Long responseId, String language, String template,
            String subjectKey, java.util.Map<String, ?> data) {
        if (session == null || from == null || from.trim().isEmpty()) {
            LOG.warning("Survey confirmation email is not configured; response " + responseId + " remains saved.");
            return false;
        }
        try {
            InternetAddress address = new InternetAddress(recipient, true);
            address.validate();
            ResourceBundle labels = ResourceBundle.getBundle("messages",
                    new Locale("ar".equals(language) ? "ar" : "en"));
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, true));
            message.setRecipient(Message.RecipientType.TO, address);
            message.setSubject(labels.getString(subjectKey), "UTF-8");
            StringWriter body = new StringWriter();
            TEMPLATES.getTemplate("school-health-" + template + "_"
                    + ("ar".equals(language) ? "ar" : "en") + ".ftl")
                    .process(data, body);
            message.setText(body.toString(), "UTF-8");
            message.setSentDate(new Date());
            deliver(message);
            return true;
        } catch (Exception ex) {
            // Do not expose addresses, credentials, or survey answers in logs.
            LOG.warning("Survey confirmation email failed for response " + responseId
                    + " (" + ex.getClass().getSimpleName() + "); response remains saved.");
            return false;
        }
    }

    protected void deliver(MimeMessage message) throws Exception {
        Transport.send(message);
    }

    private static Configuration templateConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setClassForTemplateLoading(SurveyConfirmationEmail.class, "/templates");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLocalizedLookup(false);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        return configuration;
    }

    private static Session configuredSession() {
        String host = setting("SURVEY_SMTP_HOST", "localhost");
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", setting("SURVEY_SMTP_PORT", "25"));
        properties.setProperty("mail.smtp.connectiontimeout", "10000");
        properties.setProperty("mail.smtp.timeout", "10000");
        properties.setProperty("mail.smtp.writetimeout", "10000");
        properties.setProperty("mail.smtp.auth", "false");
        return Session.getInstance(properties);
    }

    private static String setting(String name, String fallback) {
        String value = MAIL_SETTINGS.getProperty(name);
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private static Properties loadMailSettings() {
        Properties settings = new Properties();
        try (java.io.InputStream input = SurveyConfirmationEmail.class.getResourceAsStream("/school-health-mail.properties")) {
            if (input != null) settings.load(input);
        } catch (java.io.IOException ex) {
            LOG.warning("Could not load school health mail settings.");
        }
        return settings;
    }
}
