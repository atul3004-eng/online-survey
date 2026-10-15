package com.kiptoo2000.survey.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Heading question numbers, shared by the form, historical results and export. */
public final class WellnessQuestionNumbers {
    private static final Map<String, String> NUMBERS = createNumbers();
    private WellnessQuestionNumbers() { }
    public static Map<String, String> getNumbers() { return NUMBERS; }
    public static String format(String key, String label) {
        String number = NUMBERS.get(key);
        return number == null || label == null ? label : number + ". " + label;
    }
    private static Map<String, String> createNumbers() {
        Map<String, String> numbers = new LinkedHashMap<String, String>();
        numbers.put("focalName", "1");
        numbers.put("jobTitle", "2");
        numbers.put("focalEmail", "3");
        numbers.put("officePhone", "4");
        numbers.put("mobilePhone", "5");
        numbers.put("industry", "6");
        numbers.put("employmentContracts", "7");
        numbers.put("outsourcedServices", "8");
        numbers.put("occupations", "9");
        numbers.put("employeeAge", "10");
        numbers.put("employeeGender", "11");
        numbers.put("employeeNationality", "12");
        numbers.put("employeeLanguages", "13");
        numbers.put("ohsPolicy", "14");
        numbers.put("ohsPolicyAspects", "15");
        numbers.put("responsibleOfficer", "16");
        numbers.put("ohsCommittee", "17");
        numbers.put("discussionFrequency", "18");
        numbers.put("riskAssessment", "19");
        numbers.put("hazards", "20");
        numbers.put("riskFrequency", "21");
        numbers.put("riskProgram", "22");
        numbers.put("riskDocumented", "23");
        numbers.put("hygieneRecords", "24");
        numbers.put("trainingPlan", "25");
        numbers.put("ohsTrainingItems", "26");
        numbers.put("medicalFacilities", "27");
        numbers.put("healthPersonnel", "28");
        numbers.put("preEmploymentExam", "29");
        numbers.put("periodicExam", "30");
        numbers.put("lifestyleSurveys", "31");
        numbers.put("wellnessPolicy", "32");
        numbers.put("wellnessPolicyElements", "33");
        numbers.put("wellnessProgram", "34");
        numbers.put("wellnessStrategies", "35");
        numbers.put("wellnessOwners", "36");
        numbers.put("wellnessProgramElements", "37");
        numbers.put("communicationMethods", "38");
        numbers.put("freeHealthyFood", "39");
        numbers.put("cafeteria", "40");
        numbers.put("cafeteriaPromotion", "41");
        numbers.put("physicalActivityFacilities", "42");
        numbers.put("tobaccoBan", "43");
        numbers.put("mentalHealthSupport", "44");
        numbers.put("otherComments", "45");
        return Collections.unmodifiableMap(numbers);
    }
}