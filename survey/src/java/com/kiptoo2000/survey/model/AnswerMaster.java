package com.kiptoo2000.survey.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "answers_master")
public class AnswerMaster implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "surveyid")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topicid", nullable = false)
    private Topic topic;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "answered_on", nullable = false)
    private Date answeredOn;

    @PrePersist
    public void onCreate() {
        if (answeredOn == null) {
            answeredOn = new Date();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Date getAnsweredOn() {
        return answeredOn;
    }

    public void setAnsweredOn(Date answeredOn) {
        this.answeredOn = answeredOn;
    }
}
