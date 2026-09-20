package com.kiptoo2000.survey;

import com.kiptoo2000.survey.validation.SurveyEmailValidator;
import org.junit.Test;
import static org.junit.Assert.*;

public class SurveyEmailValidatorTest {
    @Test public void rejectsMissingAndMalformedAddresses() {
        for (String email : new String[]{null, "", " ", "abc", "a@", "a@localhost", "a..b@example.org",
                ".a@example.org", "a@-example.org", "a@example..org", "a@example.org,b@example.org"}) {
            assertFalse(String.valueOf(email), SurveyEmailValidator.isValid(email));
        }
    }
    @Test public void acceptsOneAddressWithOptionalSurroundingWhitespace() {
        assertTrue(SurveyEmailValidator.isValid("person.name+test@example.org"));
        assertTrue(SurveyEmailValidator.isValid(" person@example.org "));
    }
}
