package com.kiptoo2000.surveyadmin.model;
import java.io.Serializable;
public class SchoolSurveyOption implements Serializable {
    private Long id;
    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    private String description;
    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }
    private String descriptionAr;
    public String getDescriptionAr() { return descriptionAr; }
    public void setDescriptionAr(String value) { descriptionAr = value; }
    private String eventUuid;
    public String getEventUuid() { return eventUuid; }
    public void setEventUuid(String value) { eventUuid = value; }
    private String optionType;
    public String getOptionType() { return optionType; }
    public void setOptionType(String value) { optionType = value; }
}
