package com.kiptoo2000.survey.bean;

import com.kiptoo2000.survey.repository.DuplicateSurveySubmissionException;
import com.kiptoo2000.survey.repository.SchoolHealthSurveyRepository;
import com.kiptoo2000.survey.repository.SchoolHealthSurveyRepository.OptionRow;
import java.io.Serializable;
import com.kiptoo2000.survey.model.SchoolHealthResponse;
import com.kiptoo2000.survey.model.SchoolHealthAnswer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean(name = "schoolHealthSurvey")
@SessionScoped
public class SchoolHealthSurveyBean implements Serializable {

    private final SchoolHealthSurveyRepository schoolHealthSurveyRepository = new SchoolHealthSurveyRepository();
    private final Map<String, String> answers = new LinkedHashMap<String, String>();
    private final Map<String, String[]> multiAnswers = new LinkedHashMap<String, String[]>();
    private String resumeToken = java.util.UUID.randomUUID().toString();
    private static final List<String> MULTI_KEYS = Arrays.asList("humanResources","infrastructureSupport","preparatoryGrades","primaryGrades","secondaryGrades","targetPopulation","targetedTopics");
    private String returnCode;
    public String getReturnCode() { return returnCode; }
    public void setReturnCode(String code) { returnCode = code == null ? null : code.trim(); }
    public String getResumeToken() { return resumeToken; }
    public boolean isSavingDraft() {
        return "true".equals(FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("saveDraft"));
    }
    public String saveDraft() {
        try {
            savedResponseId = schoolHealthSurveyRepository.save(savedResponseId, resumeToken, answers, multiAnswers, locale, true);
            returnCode = resumeToken;
            message(FacesMessage.SEVERITY_INFO, "Draft saved. Keep your private return code to continue later.");
        } catch (RuntimeException ex) {
            message(FacesMessage.SEVERITY_ERROR, "Draft could not be saved. Please try again.");
        }
        return null;
    }
    public String reopen() {
        try {
            SchoolHealthResponse response = schoolHealthSurveyRepository.findByToken(returnCode);
            if (response == null) {
                message(FacesMessage.SEVERITY_ERROR, "No survey found for this return code.");
                return null;
            }
            answers.clear(); multiAnswers.clear();
            Map<String, List<String>> selections = new LinkedHashMap<String, List<String>>();
            for (SchoolHealthAnswer answer : response.getAnswers()) {
                String key = answer.getQuestionKey();
                if (MULTI_KEYS.contains(key)) {
                    if (!selections.containsKey(key)) { selections.put(key, new ArrayList<String>()); }
                    selections.get(key).add(answer.getAnswerValue());
                } else { answers.put(key, answer.getAnswerValue()); }
            }
            for (Map.Entry<String, List<String>> entry : selections.entrySet()) {
                multiAnswers.put(entry.getKey(), entry.getValue().toArray(new String[0]));
            }
            savedResponseId = response.getId(); resumeToken = response.getResumeToken();
            locale = response.getResponseLocale();
            submitted = "SUBMITTED".equals(response.getStatus());
            submittedOn = response.getSubmittedOn() == null ? null
                    : new SimpleDateFormat("yyyy-MM-dd HH:mm").format(response.getSubmittedOn());
            return (submitted ? "school-health-complete" : "school-health") + "?faces-redirect=true";
        } catch (RuntimeException ex) {
            message(FacesMessage.SEVERITY_ERROR, "Survey could not be loaded. Please try again.");
            return null;
        }
    }
    private void message(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, ""));
    }
    public void downloadBlank() {
        try (java.io.InputStream in = getClass().getResourceAsStream("/docs/school-health-questionnaire.pdf")) {
            if (in == null) { throw new java.io.IOException("Missing blank questionnaire"); }
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[8192]; int count;
            while ((count = in.read(buffer)) != -1) { out.write(buffer, 0, count); }
            sendPdf(out.toByteArray(), "school-health-questionnaire.pdf");
        } catch (Exception ex) { message(FacesMessage.SEVERITY_ERROR, "Questionnaire download failed. Please try again."); }
    }
    public void downloadCompleted() {
        try {
            SchoolHealthResponse response = schoolHealthSurveyRepository.findByToken(resumeToken);
            if (response == null || !"SUBMITTED".equals(response.getStatus())) {
                message(FacesMessage.SEVERITY_ERROR, "Submit the survey before downloading the completed questionnaire.");
                return;
            }
            byte[] pdf = new com.kiptoo2000.survey.pdf.SchoolHealthPdf().generate(response,
                    schoolHealthSurveyRepository.questionnaire(response.getResponseLocale()), this);
            sendPdf(pdf, "school-health-completed-" + response.getId() + ".pdf");
        } catch (Exception ex) { message(FacesMessage.SEVERITY_ERROR, "Completed questionnaire download failed. Please try again."); }
    }
    private void sendPdf(byte[] bytes, String filename) throws java.io.IOException {
        FacesContext faces = FacesContext.getCurrentInstance();
        javax.faces.context.ExternalContext context = faces.getExternalContext();
        context.responseReset(); context.setResponseContentType("application/pdf");
        context.setResponseHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        context.setResponseHeader("Cache-Control", "no-store");
        context.setResponseContentLength(bytes.length);
        context.getResponseOutputStream().write(bytes); faces.responseComplete();
    }
    private Long savedResponseId;
    private boolean submitted;
    private String submittedOn;
    private String locale = "en";

    public Map<String, String> getAnswers() {
        return answers;
    }

    public Map<String, String[]> getMultiAnswers() {
        return multiAnswers;
    }

    public Long getSavedResponseId() {
        return savedResponseId;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public String getSubmittedOn() {
        return submittedOn;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = normalizeLocale(locale);
    }

    public String getLang() {
        return locale;
    }

    public String getDir() {
        return isArabic() ? "rtl" : "ltr";
    }

    public boolean isArabic() {
        return "ar".equals(locale);
    }

    public String switchToArabic() {
        locale = "ar";
        return null;
    }

    public String switchToEnglish() {
        locale = "en";
        return null;
    }

    public String toggleLanguage() {
        locale = isArabic() ? "en" : "ar";
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        String outcome = viewId;
        if (outcome.startsWith("/")) {
            outcome = outcome.substring(1);
        }
        if (outcome.endsWith(".xhtml")) {
            outcome = outcome.substring(0, outcome.length() - ".xhtml".length());
        }
        return outcome + "?faces-redirect=true&includeViewParams=true&lang=" + locale;
    }

    public String text(String key) {
        return schoolHealthSurveyRepository.findQuestionText(key, locale);
    }

    public List<SelectItem> options(String group) {
        List<OptionRow> rows = schoolHealthSurveyRepository.findOptions(group, locale);
        if (rows.isEmpty()) {
            rows = fallbackOptions(group);
        }
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (OptionRow row : rows) {
            items.add(new SelectItem(row.getValue(), row.getLabel()));
        }
        return items;
    }

    public boolean isOtherSelected(String key) {
        String[] selectedValues = multiAnswers.get(key);
        if (selectedValues == null) {
            return false;
        }
        for (String selectedValue : selectedValues) {
            if ("other".equals(selectedValue) || "Other".equals(selectedValue)) {
                return true;
            }
        }
        return false;
    }

    public String beginSurvey() {
        resumeToken = java.util.UUID.randomUUID().toString();
        returnCode = null;
        answers.clear();
        multiAnswers.clear();
        submitted = false;
        submittedOn = null;
        savedResponseId = null;
        return "school-health?faces-redirect=true";
    }

    public String submit() {
        try {
            savedResponseId = schoolHealthSurveyRepository.save(savedResponseId, resumeToken, answers, multiAnswers, locale, false);
            submitted = true;
            submittedOn = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Survey saved",
                            "Response #" + savedResponseId + " was saved to the database."));
        } catch (DuplicateSurveySubmissionException ex) {
            FacesContext.getCurrentInstance().validationFailed();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Duplicate submission",
                            "This email address or mobile phone number has already been used for this survey."));
            return null;
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Save failed",
                            "The school health survey could not be saved. Please check the database connection and schema."));
            return null;
        }
        return "school-health-complete?faces-redirect=true";
    }

    public String reset() {
        resumeToken = java.util.UUID.randomUUID().toString();
        returnCode = null;
        answers.clear();
        multiAnswers.clear();
        submitted = false;
        submittedOn = null;
        savedResponseId = null;
        return "school-health-start?faces-redirect=true";
    }

    public List<String> getInstitutionTypes() {
        return Arrays.asList("Government", "Semi-Governmental", "Private", "NGO", "Other");
    }

    public List<String> getStatusOptions() {
        return Arrays.asList("Ongoing", "Planned", "One-time or repeated activity", "Other");
    }

    public List<String> getTargetedTopics() {
        return Arrays.asList("Tobacco and nicotine product control", "Nutrition", "Physical Activity",
                "Non-Communicable Diseases (chronic)", "Mental Health", "Oral Health",
                "Communicable Diseases", "Vaccination", "Environmental health", "Safety", "Other");
    }

    public List<String> getTargetPopulations() {
        return Arrays.asList("Students", "Teachers", "Parents", "Other");
    }

    public List<String> getPrimaryGrades() {
        return Arrays.asList("All primary grade levels", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6");
    }

    public List<String> getPreparatoryGrades() {
        return Arrays.asList("All preparatory grade levels", "Grade 7", "Grade 8", "Grade 9");
    }

    public List<String> getSecondaryGrades() {
        return Arrays.asList("All secondary grade levels", "Grade 10", "Grade 11", "Grade 12", "Grade 13");
    }

    public List<String> getCoverageOptions() {
        return Arrays.asList("National (all government and private schools)", "All government schools only",
                "All private schools only", "Selected government schools only", "Selected private schools only",
                "Selected government and private schools");
    }

    public List<String> getIndicatorOptions() {
        return Arrays.asList("Yes", "No", "Under Development");
    }

    public List<String> getYesNoOptions() {
        return Arrays.asList("Yes", "No");
    }

    public List<String> getDocumentStatusOptions() {
        return Arrays.asList("Yes", "No", "Planned");
    }

    public List<String> getHumanResources() {
        return Arrays.asList("Nurses", "Health Educators", "Social workers", "Canteen supervisors", "Coordinators", "Other");
    }

    public List<String> getTrainingFrequencies() {
        return Arrays.asList("Monthly", "Quarterly", "Biannually", "Annually", "Other");
    }

    public List<String> getInfrastructureSupports() {
        return Arrays.asList("School Clinics", "Mobile Units", "Digital Platforms", "Training Centers", "Other");
    }

    private String normalizeLocale(String locale) {
        return "ar".equalsIgnoreCase(locale) ? "ar" : "en";
    }

    private List<OptionRow> fallbackOptions(String group) {
        List<OptionRow> rows = new ArrayList<OptionRow>();
        if ("institutionTypes".equals(group)) {
            add(rows, "government", "Government");
            add(rows, "semi_governmental", "Semi-Governmental");
            add(rows, "private", "Private");
            add(rows, "ngo", "NGO");
            add(rows, "other", "Other");
        } else if ("statusOptions".equals(group)) {
            add(rows, "ongoing", "Ongoing");
            add(rows, "planned", "Planned");
            add(rows, "one_time", "One-time or repeated activity");
            add(rows, "other", "Other");
        } else if ("targetedTopics".equals(group)) {
            for (String value : getTargetedTopics()) {
                add(rows, optionCode(value), value);
            }
        } else if ("targetPopulations".equals(group)) {
            add(rows, "students", "Students");
            add(rows, "teachers", "Teachers");
            add(rows, "parents", "Parents");
            add(rows, "other", "Other");
        } else if ("primaryGrades".equals(group)) {
            for (String value : getPrimaryGrades()) {
                add(rows, optionCode(value), value);
            }
        } else if ("preparatoryGrades".equals(group)) {
            for (String value : getPreparatoryGrades()) {
                add(rows, optionCode(value), value);
            }
        } else if ("secondaryGrades".equals(group)) {
            for (String value : getSecondaryGrades()) {
                add(rows, optionCode(value), value);
            }
        } else if ("coverageOptions".equals(group)) {
            for (String value : getCoverageOptions()) {
                add(rows, optionCode(value), value);
            }
        } else if ("indicatorOptions".equals(group)) {
            add(rows, "yes", "Yes");
            add(rows, "no", "No");
            add(rows, "under_development", "Under Development");
        } else if ("yesNoOptions".equals(group)) {
            add(rows, "yes", "Yes");
            add(rows, "no", "No");
        } else if ("documentStatusOptions".equals(group)) {
            add(rows, "yes", "Yes");
            add(rows, "no", "No");
            add(rows, "planned", "Planned");
        } else if ("humanResources".equals(group)) {
            for (String value : getHumanResources()) {
                add(rows, optionCode(value), value);
            }
        } else if ("trainingFrequencies".equals(group)) {
            for (String value : getTrainingFrequencies()) {
                add(rows, optionCode(value), value);
            }
        } else if ("infrastructureSupports".equals(group)) {
            for (String value : getInfrastructureSupports()) {
                add(rows, optionCode(value), value);
            }
        }
        return rows;
    }

    private void add(List<OptionRow> rows, String value, String label) {
        rows.add(new OptionRow(value, label));
    }

    private String optionCode(String value) {
        String code = value == null ? "" : value.toLowerCase().replaceAll("[^a-z0-9]+", "_");
        code = code.replaceAll("^_+", "").replaceAll("_+$", "");
        return "other".equals(code) ? "other" : code;
    }
}
