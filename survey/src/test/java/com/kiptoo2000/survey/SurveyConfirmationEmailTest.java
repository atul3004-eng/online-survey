package com.kiptoo2000.survey;

import com.kiptoo2000.survey.service.SurveyConfirmationEmail;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.junit.Test;
import static org.junit.Assert.*;

public class SurveyConfirmationEmailTest {
    @Test public void sendsLocalizedReceiptToOneParticipant() throws Exception {
        final MimeMessage[] captured = new MimeMessage[1];
        SurveyConfirmationEmail mail = new SurveyConfirmationEmail(
                Session.getInstance(new Properties()), "survey@example.org") {
            @Override protected void deliver(MimeMessage message) { captured[0] = message; }
        };
        assertTrue(mail.send("participant@example.org", 42L, "en"));
        assertEquals(1, captured[0].getAllRecipients().length);
        assertEquals("participant@example.org", captured[0].getAllRecipients()[0].toString());
        assertTrue(captured[0].getContent().toString().contains("Submission reference: 42"));
        assertTrue(mail.send("participant@example.org", 42L, "ar"));
        assertTrue(captured[0].getSubject().contains("تأكيد"));
        assertTrue(captured[0].getContent().toString().contains("الرقم المرجعي للاستبيان: 42"));
        captured[0] = null;
        assertFalse(mail.send("one@example.org,two@example.org", 42L, "en"));
        assertNull(captured[0]);
    }

    @Test public void missingConfigurationAndTransportFailureDoNotThrow() {
        assertFalse(new SurveyConfirmationEmail(null, null).send("participant@example.org", 42L, "en"));
        SurveyConfirmationEmail mail = new SurveyConfirmationEmail(
                Session.getInstance(new Properties()), "survey@example.org") {
            @Override protected void deliver(MimeMessage message) throws Exception {
                throw new javax.mail.MessagingException("Test failure");
            }
        };
        assertFalse(mail.send("participant@example.org", 42L, "en"));
    }
}
