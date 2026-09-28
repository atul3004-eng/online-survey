package com.kiptoo2000.surveyadmin.model;
import java.io.Serializable;
public class SchoolSurveyDetail implements Serializable {
    private Long id;
    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    private String eventUuid;
    public String getEventUuid() { return eventUuid; }
    public void setEventUuid(String value) { eventUuid = value; }
    private Long headingId;
    public Long getHeadingId() { return headingId; }
    public void setHeadingId(Long value) { headingId = value; }
    private String heading;
    public String getHeading() { return heading; }
    public void setHeading(String value) { heading = value; }
    private String question;
    public String getQuestion() { return question; }
    public void setQuestion(String value) { question = value; }
    private String fieldType;
    public String getFieldType() { return fieldType; }
    public void setFieldType(String value) { fieldType = value; }
    private boolean mandatory;
    public boolean isMandatory() { return mandatory; }
    public void setMandatory(boolean value) { mandatory = value; }
    private String fieldOption;
    public String getFieldOption() { return fieldOption; }
    public void setFieldOption(String value) { fieldOption = value; }
    private String requiredMessage;
    public String getRequiredMessage() { return requiredMessage; }
    public void setRequiredMessage(String value) { requiredMessage = value; }
    private boolean preSurvey;
    public boolean isPreSurvey() { return preSurvey; }
    public void setPreSurvey(boolean value) { preSurvey = value; }
    private boolean postSurvey;
    public boolean isPostSurvey() { return postSurvey; }
    public void setPostSurvey(boolean value) { postSurvey = value; }
    private Long correctAnswerId;
    public Long getCorrectAnswerId() { return correctAnswerId; }
    public void setCorrectAnswerId(Long value) { correctAnswerId = value; }
    private String headingAr;
    public String getHeadingAr() { return headingAr; }
    public void setHeadingAr(String value) { headingAr = value; }
    private String questionAr;
    public String getQuestionAr() { return questionAr; }
    public void setQuestionAr(String value) { questionAr = value; }
    private String requiredMessageAr;
    public String getRequiredMessageAr() { return requiredMessageAr; }
    public void setRequiredMessageAr(String value) { requiredMessageAr = value; }
}
