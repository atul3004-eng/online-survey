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
    private final Session session;
    private final String from;

    public SurveyConfirmationEmail() {
        this(configuredSession(), System.getenv("SURVEY_MAIL_FROM"));
    }

    public SurveyConfirmationEmail(Session session, String from) {
        this.session = session;
        this.from = from;
    }

    public boolean send(String recipient, Long responseId, String language) {
        if (session == null || from == null || from.trim().isEmpty()) {
            LOG.warning("Survey confirmation email is not configured; response " + responseId + " remains saved.");
            return false;
        }
        try {
            InternetAddress address = new InternetAddress(recipient, true);
            address.validate();
            ResourceBundle labels = ResourceBundle.getBundle("com.kiptoo2000.survey.i18n.messages",
                    new Locale("ar".equals(language) ? "ar" : "en"));
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, true));
            message.setRecipient(Message.RecipientType.TO, address);
            message.setSubject(labels.getString("schoolHealth.emailSubject"), "UTF-8");
            StringWriter body = new StringWriter();
            TEMPLATES.getTemplate("school-health-confirmation_"
                    + ("ar".equals(language) ? "ar" : "en") + ".ftl")
                    .process(Collections.singletonMap("responseId", responseId), body);
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
        configuration.setClassForTemplateLoading(SurveyConfirmationEmail.class, "/templates/email");
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
        String startTls = setting("SURVEY_SMTP_STARTTLS", "false");
        properties.setProperty("mail.smtp.starttls.enable", startTls);
        properties.setProperty("mail.smtp.starttls.required", startTls);
        properties.setProperty("mail.smtp.ssl.checkserveridentity", "true");
        properties.setProperty("mail.smtp.connectiontimeout", "10000");
        properties.setProperty("mail.smtp.timeout", "10000");
        properties.setProperty("mail.smtp.writetimeout", "10000");
        properties.setProperty("mail.smtp.auth", "false");
        return Session.getInstance(properties);
    }

    private static String setting(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
