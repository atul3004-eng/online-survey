package com.kiptoo2000.survey.bean;

import com.kiptoo2000.survey.model.WellnessAnswer;
import com.kiptoo2000.survey.model.WellnessResponse;
import com.kiptoo2000.survey.repository.DuplicateSurveySubmissionException;
import com.kiptoo2000.survey.repository.WellnessSurveyRepository;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean(name = "wellnessSurvey")
@SessionScoped
public class WellnessSurveyBean implements Serializable {

    private final WellnessSurveyRepository wellnessSurveyRepository = new WellnessSurveyRepository();
    private final Map<String, String> answers = new LinkedHashMap<String, String>();
    private final Map<String, String[]> multiAnswers = new LinkedHashMap<String, String[]>();
    private List<WellnessAnswer> selectedAnswers = new ArrayList<WellnessAnswer>();
    private WellnessResponse selectedResponse;
    private Long savedResponseId;
    private boolean submitted;
    private String submittedOn;
    private String referenceId;
    private String submittedBy;
    private String surveyCategory;
    private String surveyOpenedOn;

    public WellnessSurveyBean() {
        initializePrefilledData();
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public Map<String, String[]> getMultiAnswers() {
        return multiAnswers;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public Long getSavedResponseId() {
        return savedResponseId;
    }

    public String getSubmittedOn() {
        return submittedOn;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public String getSurveyCategory() {
        return surveyCategory;
    }

    public String getSurveyOpenedOn() {
        return surveyOpenedOn;
    }

    public String getDisplayCompanyName() {
        return valueOrFallback(answers.get("companyName"), "-");
    }

    public String getDisplayEmail() {
        return valueOrFallback(answers.get("email"), "-");
    }

    public String getDisplayPhone() {
        return valueOrFallback(answers.get("phone"), "-");
    }

    public String getDisplayAddress() {
        String address = buildAddressText();
        if (isBlank(address)) {
            return "-";
        }
        return address;
    }

    public String getProgressText(Long sectionNumber) {
        return getProgressPercent(sectionNumber) + "%";
    }

    public int getProgressPercent(Long sectionNumber) {
        if (sectionNumber == null) {
            return 0;
        }
        int section = sectionNumber.intValue();
        if (section < 1) {
            return 0;
        }
        if (section > 4) {
            section = 4;
        }
        return section * 25;
    }

    public String getProgressText() {
        return getProgressText(Long.valueOf(1L));
    }

    public int getProgressPercent() {
        return getProgressPercent(Long.valueOf(1L));
    }

    public boolean isOtherSelected(String key) {
        String[] selectedValues = multiAnswers.get(key);
        if (selectedValues == null) {
            return false;
        }
        for (String selectedValue : selectedValues) {
            if ("Other".equals(selectedValue)) {
                return true;
            }
        }
        return false;
    }

    public String submit() {
        if (!isEmployeeTotalValid()) {
            FacesContext.getCurrentInstance().validationFailed();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid employee total",
                            "Total employees must equal full-time + part-time + contracted/outsourced employees."));
            return null;
        }

        try {
            savedResponseId = wellnessSurveyRepository.save(answers, multiAnswers);
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
                            "The survey could not be saved. Please check the database connection and schema."));
            return null;
        }
        return "wellness-complete?faces-redirect=true";
    }

    private boolean isEmployeeTotalValid() {
        String fullTime = answers.get("fullTime");
        String partTime = answers.get("partTime");
        String contracted = answers.get("contracted");
        String totalEmployees = answers.get("totalEmployees");
        if (isBlank(fullTime) || isBlank(partTime) || isBlank(contracted) || isBlank(totalEmployees)) {
            return true;
        }
        try {
            int expectedTotal = Integer.parseInt(fullTime) + Integer.parseInt(partTime) + Integer.parseInt(contracted);
            return expectedTotal == Integer.parseInt(totalEmployees);
        } catch (NumberFormatException ex) {
            return true;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String valueOrFallback(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private void appendAddressPart(StringBuilder builder, String label, String value) {
        if (isBlank(value)) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(label).append(' ').append(value);
    }

    private String buildAddressText() {
        StringBuilder builder = new StringBuilder();
        appendAddressPart(builder, "Zone", trimToEmpty(answers.get("zone")));
        appendAddressPart(builder, "Street", trimToEmpty(answers.get("street")));
        appendAddressPart(builder, "Building", trimToEmpty(answers.get("buildingNo")));
        appendAddressPart(builder, "Unit", trimToEmpty(answers.get("unit")));
        return builder.toString();
    }

    private void initializePrefilledData() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String suffix = timestamp.length() > 6 ? timestamp.substring(timestamp.length() - 6) : timestamp;
        referenceId = "SUR-" + suffix;
        submittedBy = "Survey Participant";
        surveyCategory = "Workplace Wellness";
        surveyOpenedOn = new SimpleDateFormat("dd MMM yyyy").format(new Date());
    }

    public String beginSurvey() {
        answers.clear();
        multiAnswers.clear();
        submitted = false;
        submittedOn = null;
        savedResponseId = null;
        initializePrefilledData();
        return "wellness?faces-redirect=true";
    }

    public String startSurvey() {
        submitted = false;
        submittedOn = null;
        savedResponseId = null;
        answers.put("address", buildAddressText());
        initializePrefilledData();
        return "wellness?faces-redirect=true";
    }

    public String reset() {
        answers.clear();
        multiAnswers.clear();
        submitted = false;
        submittedOn = null;
        savedResponseId = null;
        return "wellness-start?faces-redirect=true";
    }

    public List<WellnessResponse> getSavedResponses() {
        return wellnessSurveyRepository.findAllResponses();
    }

    public List<WellnessAnswer> getExportAnswers() {
        return wellnessSurveyRepository.findAllAnswersForExport();
    }

    public void downloadFullResultsExcel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        try {
            externalContext.responseReset();
            externalContext.setResponseContentType("application/vnd.ms-excel; charset=UTF-16LE");
            externalContext.setResponseHeader("Content-Disposition",
                    "attachment; filename=\"workplace-wellness-full-results.xls\"");

            OutputStream outputStream = externalContext.getResponseOutputStream();
            outputStream.write(new byte[]{(byte) 0xFF, (byte) 0xFE});
            outputStream.write(buildFullResultsExcelText().getBytes(Charset.forName("UTF-16LE")));
            outputStream.flush();
            facesContext.responseComplete();
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Download failed",
                            "The Excel file could not be generated."));
        }
    }

    private String buildFullResultsExcelText() {
        StringBuilder builder = new StringBuilder();
        appendExcelRow(builder, new Object[]{
            "Response ID",
            "Submitted On",
            "Company",
            "Email",
            "Phone",
            "Section",
            "Question",
            "Answer"
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (WellnessAnswer answer : wellnessSurveyRepository.findAllAnswersForExport()) {
            WellnessResponse response = answer.getResponse();
            appendExcelRow(builder, new Object[]{
                response != null ? response.getId() : null,
                response != null ? formatDate(response.getSubmittedOn(), dateFormat) : "",
                response != null ? response.getCompanyName() : "",
                response != null ? response.getEmail() : "",
                response != null ? response.getPhone() : "",
                answer.getSectionName(),
                answer.getQuestionLabel(),
                answer.getAnswerValue()
            });
        }
        return builder.toString();
    }

    private void appendExcelRow(StringBuilder builder, Object[] values) {
        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                builder.append('\t');
            }
            builder.append(formatExcelValue(values[index]));
        }
        builder.append("\r\n");
    }

    private String formatExcelValue(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value).replace("\"", "\"\"");
        if (text.indexOf('\t') >= 0 || text.indexOf('\n') >= 0 || text.indexOf('\r') >= 0 || text.indexOf('"') >= 0) {
            return "\"" + text + "\"";
        }
        return text;
    }

    private String formatDate(Date date, SimpleDateFormat dateFormat) {
        return date == null ? "" : dateFormat.format(date);
    }

    public void viewSavedResponse(Long responseId) {
        selectedResponse = wellnessSurveyRepository.findResponse(responseId);
        selectedAnswers = wellnessSurveyRepository.findAnswers(responseId);
    }

    public WellnessResponse getSelectedResponse() {
        return selectedResponse;
    }

    public List<WellnessAnswer> getSelectedAnswers() {
        return selectedAnswers;
    }

    public List<String> getIndustries() {
        return Arrays.asList(
                "Agriculture, plantation and other rural sectors",
                "Commerce",
                "Construction and infrastructure",
                "Education and research",
                "Financial services; professional services",
                "General industries: Basic metal production",
                "General industries: Chemical industries",
                "General industries: Oil and gas production; oil refining",
                "General industries: Textiles; clothing; leather; footwear",
                "General industries: wood; pulp and paper",
                "General industries: Food; drink",
                "Health services",
                "Hotels; catering; tourism",
                "Mechanical and Electrical Engineering",
                "Media; culture; graphical",
                "Postal and telecom services",
                "Public service",
                "Shipping; ports; fisheries; inland waterways",
                "Transport (including civil aviation; railways; road)",
                "Transport equipment",
                "Utilities (water; gas; electricity)",
                "Other");
    }

    public List<String> getOutsourcedServices() {
        return Arrays.asList("Admin", "IT", "Construction", "Catering", "Cleaning", "Security", "Other");
    }

    public List<String> getOccupations() {
        return Arrays.asList("Admin staff", "Carpenter", "Chef / Cook / kitchen assistant", "Cleaner / housekeeper",
                "Driver - heavy", "Driver - light", "Electrician", "Forklift operator", "Construction worker",
                "Painter", "Plumber", "Security guard / officer / facility security", "Site engineer / project manager",
                "Steel fixer (rebar worker)", "Storekeeper", "Waiter / Waitress / Barista", "Other");
    }

    public List<String> getCountries() {
        return Arrays.asList("Bangladesh", "Egypt", "India", "Kenya", "Nepal", "Pakistan", "Philippines",
                "Sri Lanka", "Uganda", "Other");
    }

    public List<String> getOhsPolicyAspects() {
        return Arrays.asList("Workplace health promotion", "OHS training and employee orientation",
                "Hazard and risk assessment", "Chemical/biological safety", "Laboratories",
                "Mechanical machines and equipment workplace", "Working at heights", "Welding work",
                "Excavation work", "Energy isolation tag out / lock out", "Fire Safety", "Electrical Safety",
                "Violence / bullying / harassment", "Environmental health", "Pesticides",
                "Employee health coverage policy", "Prevention of communicable diseases",
                "Prevention of non-communicable diseases", "Mental health", "Other");
    }

    public List<String> getHazards() {
        return Arrays.asList("Biological Hazards", "Chemical and Dust Hazards", "Physical Hazards",
                "Electrical Hazards", "Mechanical Hazards", "Safety Hazards", "Fire Hazards",
                "Ergonomics Hazards", "Work Company Hazards", "Psychosocial Environment", "Other");
    }

    public List<String> getOhsTrainingItems() {
        return Arrays.asList("Use of working equipment and PPE", "Workplace hazards, risk detection and control measures",
                "Use of dangerous substances", "Psychosocial risks and mental health related issues",
                "Heavy lifting and manual handling", "Other");
    }

    public List<String> getWellnessElements() {
        return Arrays.asList("Physical activity", "Nutrition", "Mental Health", "Tobacco Use", "Other");
    }

    public List<String> getWellnessStrategies() {
        return Arrays.asList("Health education classes, sessions, webinars",
                "Health campaigns, screening and medical checkups", "Access to local/on-site fitness facilities",
                "Policies that promote healthy behaviors", "Healthy food options in vending machines or cafeterias",
                "Safe work environment with systems to manage new risks");
    }

    public List<String> getWellnessOwners() {
        return Arrays.asList("Occupational health department/committee", "Human resources department",
                "Workplace health promotion and wellness officer/coordinator",
                "Wellness / sports and recreation committee", "Staff clinic", "No assigned responsible body", "Other");
    }

    public List<String> getCommunicationMethods() {
        return Arrays.asList("Flyers", "Posters", "Events and conference", "Workshops, webinars",
                "Company social media", "Other");
    }
}
