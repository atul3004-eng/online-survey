package com.kiptoo2000.survey;

import com.kiptoo2000.survey.model.WellnessAnswer;
import com.kiptoo2000.survey.model.WellnessQuestionNumbers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

public class WellnessQuestionNumbersTest {
    @Test public void everyVisibleNumberResolvesToTheSharedCatalog() throws Exception {
        String page = new String(Files.readAllBytes(Paths.get("web/wellness.xhtml")), StandardCharsets.UTF_8);
        Matcher keys = Pattern.compile("questionNumbers\\['([^']+)'\\]").matcher(page);
        int labels = 0;
        while (keys.find()) {
            assertNotNull(keys.group(1), WellnessQuestionNumbers.getNumbers().get(keys.group(1)));
            labels++;
        }
        assertEquals(67, labels);
    }

    @Test public void historicalAnswersReceiveNumbersWithoutChangingStoredLabels() {
        WellnessAnswer answer = new WellnessAnswer();
        answer.setQuestionKey("industry");
        answer.setQuestionLabel("Type of industry");
        assertEquals("6. Type of industry", answer.getNumberedQuestionLabel());
        assertEquals("Type of industry", answer.getQuestionLabel());
        answer.setQuestionKey("industryOther");
        assertEquals("6a. Type of industry", answer.getNumberedQuestionLabel());
    }

    @Test public void legacyFieldsRetainTheirLabelsRatherThanInventingNumbers() {
        assertEquals("Company name", WellnessQuestionNumbers.format("companyName", "Company name"));
        assertEquals("Legacy field", WellnessQuestionNumbers.format("unknownLegacyKey", "Legacy field"));
        assertNull(WellnessQuestionNumbers.format("industry", null));
    }
}
