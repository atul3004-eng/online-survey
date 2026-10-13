package com.kiptoo2000.survey;

import com.kiptoo2000.survey.bean.WellnessSurveyBean;
import org.junit.Test;
import static org.junit.Assert.*;

public class WellnessSurveyTest {
    @Test public void serviceTypesRequirePositiveContractedEmployeeCount() {
        WellnessSurveyBean bean = new WellnessSurveyBean();
        assertFalse(bean.isContractedEmployeesPresent());
        for (String count : new String[]{"", "0", "000", "-1", "1.5", "abc"}) {
            bean.getAnswers().put("contracted", count);
            assertFalse(bean.isContractedEmployeesPresent());
        }
        for (String count : new String[]{"1", "001", "999999999999999999999"}) {
            bean.getAnswers().put("contracted", count);
            assertTrue(bean.isContractedEmployeesPresent());
        }
    }

    @Test public void zeroCountClearsHiddenServiceAnswers() {
        WellnessSurveyBean bean = new WellnessSurveyBean();
        bean.getAnswers().put("contracted", "2");
        bean.getMultiAnswers().put("outsourcedServices", new String[]{"Other"});
        bean.getAnswers().put("outsourcedOther", "Transport");
        bean.onContractedEmployeesChange();
        assertTrue(bean.isOtherSelected("outsourcedServices"));
        bean.getAnswers().put("contracted", "0");
        bean.onContractedEmployeesChange();
        assertFalse(bean.getMultiAnswers().containsKey("outsourcedServices"));
        assertFalse(bean.getAnswers().containsKey("outsourcedOther"));
    }
}
