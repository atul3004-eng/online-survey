package com.kiptoo2000.survey.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Stable visible question numbers, shared by the form, historical results and export. */
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
        numbers.put("totalEmployees", "7");
        numbers.put("contracted", "8");
        numbers.put("outsourcedServices", "9");
        numbers.put("occupations", "10");
        numbers.put("age18", "11");
        numbers.put("age25", "12");
        numbers.put("age45", "13");
        numbers.put("age65", "14");
        numbers.put("male", "15");
        numbers.put("female", "16");
        numbers.put("qatari", "17");
        numbers.put("nonQatari", "18");
        numbers.put("employeeLanguages", "19");
        numbers.put("ohsPolicy", "20");
        numbers.put("ohsPolicyAspects", "21");
        numbers.put("responsibleOfficer", "22");
        numbers.put("ohsCommittee", "23");
        numbers.put("discussionFrequency", "24");
        numbers.put("riskAssessment", "25");
        numbers.put("hazards", "26");
        numbers.put("riskFrequency", "27");
        numbers.put("riskProgram", "28");
        numbers.put("riskDocumented", "29");
        numbers.put("hygieneRecords", "30");
        numbers.put("trainingPlan", "31");
        numbers.put("ohsTrainingItems", "32");
        numbers.put("firstAidBoxes", "33");
        numbers.put("firstAidUnit", "34");
        numbers.put("companyClinic", "35");
        numbers.put("generalPractitioner", "36");
        numbers.put("occupationalPhysician", "37");
        numbers.put("occupationalNurse", "38");
        numbers.put("preEmploymentExam", "39");
        numbers.put("periodicExam", "40");
        numbers.put("lifestyleSurveys", "41");
        numbers.put("wellnessPolicy", "42");
        numbers.put("wellnessPolicyElements", "43");
        numbers.put("wellnessProgram", "44");
        numbers.put("wellnessStrategies", "45");
        numbers.put("wellnessOwners", "46");
        numbers.put("wellnessProgramElements", "47");
        numbers.put("communicationMethods", "48");
        numbers.put("freeHealthyFood", "49");
        numbers.put("cafeteria", "50");
        numbers.put("cafeteriaPromotion", "51");
        numbers.put("physicalActivityFacilities", "52");
        numbers.put("tobaccoBan", "53");
        numbers.put("mentalHealthSupport", "54");
        numbers.put("otherComments", "55");
        numbers.put("communicationOther", numbers.get("communicationMethods") + "a");
        numbers.put("hazardOther", numbers.get("hazards") + "a");
        numbers.put("industryOther", numbers.get("industry") + "a");
        numbers.put("occupationOther", numbers.get("occupations") + "a");
        numbers.put("ohsPolicyOther", numbers.get("ohsPolicyAspects") + "a");
        numbers.put("outsourcedOther", numbers.get("outsourcedServices") + "a");
        numbers.put("periodicExamOther", numbers.get("periodicExam") + "a");
        numbers.put("preEmploymentOther", numbers.get("preEmploymentExam") + "a");
        numbers.put("trainingOther", numbers.get("ohsTrainingItems") + "a");
        numbers.put("wellnessOwnerOther", numbers.get("wellnessOwners") + "a");
        numbers.put("wellnessPolicyOther", numbers.get("wellnessPolicyElements") + "a");
        numbers.put("wellnessProgramOther", numbers.get("wellnessProgramElements") + "a");
        return Collections.unmodifiableMap(numbers);
    }
}