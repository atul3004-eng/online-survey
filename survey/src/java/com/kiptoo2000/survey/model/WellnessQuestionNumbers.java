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
        numbers.put("companyDetails", "1");
        numbers.put("focalPointDetails", "2");
        numbers.put("industry", "3");
        numbers.put("employmentContracts", "4");
        numbers.put("outsourcedServices", "5");
        numbers.put("occupations", "6");
        numbers.put("employeeAge", "7");
        numbers.put("employeeGender", "8");
        numbers.put("employeeNationality", "9");
        numbers.put("employeeLanguages", "10");
        numbers.put("ohsPolicy", "11");
        numbers.put("ohsPolicyAspects", "12");
        numbers.put("responsibleOfficer", "13");
        numbers.put("ohsCommittee", "14");
        numbers.put("discussionFrequency", "15");
        numbers.put("riskAssessment", "16");
        numbers.put("hazards", "17");
        numbers.put("riskFrequency", "18");
        numbers.put("riskProgram", "19");
        numbers.put("riskDocumented", "20");
        numbers.put("hygieneRecords", "21");
        numbers.put("trainingPlan", "22");
        numbers.put("ohsTrainingItems", "23");
        numbers.put("medicalFacilities", "24");
        numbers.put("healthPersonnel", "25");
        numbers.put("preEmploymentExam", "26");
        numbers.put("periodicExam", "27");
        numbers.put("lifestyleSurveys", "28");
        numbers.put("wellnessPolicy", "29");
        numbers.put("wellnessPolicyElements", "30");
        numbers.put("wellnessProgram", "31");
        numbers.put("wellnessStrategies", "32");
        numbers.put("wellnessOwners", "33");
        numbers.put("wellnessProgramElements", "34");
        numbers.put("communicationMethods", "35");
        numbers.put("freeHealthyFood", "36");
        numbers.put("cafeteria", "37");
        numbers.put("cafeteriaPromotion", "38");
        numbers.put("physicalActivityFacilities", "39");
        numbers.put("tobaccoBan", "40");
        numbers.put("mentalHealthSupport", "41");
        numbers.put("otherComments", "42");
        return Collections.unmodifiableMap(numbers);
    }
}