package com.kiptoo2000.survey.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "school_health_response")
public class SchoolHealthResponse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long id;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "SUBMITTED";
    @Column(name = "resume_token", unique = true, length = 64)
    private String resumeToken;
    @Column(name = "response_locale", nullable = false, length = 2)
    private String responseLocale = "en";
    @javax.persistence.Version
    @Column(name = "version", nullable = false)
    private long version;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResumeToken() { return resumeToken; }
    public void setResumeToken(String token) { this.resumeToken = token; }
    public String getResponseLocale() { return responseLocale; }
    public void setResponseLocale(String locale) { this.responseLocale = locale; }

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submitted_on")
    private Date submittedOn;

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SchoolHealthAnswer> answers = new ArrayList<SchoolHealthAnswer>();

    @PrePersist
    public void onCreate() {
        if (submittedOn == null && "SUBMITTED".equals(status)) {
            submittedOn = new Date();
        }
    }

    public void addAnswer(SchoolHealthAnswer answer) {
        answer.setResponse(this);
        answers.add(answer);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Date getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(Date submittedOn) {
        this.submittedOn = submittedOn;
    }

    public List<SchoolHealthAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SchoolHealthAnswer> answers) {
        this.answers = answers;
    }
}
