package com.kiptoo2000.surveyadmin.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "questions")
public class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionid")
    private Long id;

    @Column(name = "questiontext", nullable = false, length = 1000)
    private String text;

    @Column(name = "opt1", nullable = false, length = 255)
    private String option1;

    @Column(name = "opt2", nullable = false, length = 255)
    private String option2;

    @Column(name = "opt3", nullable = false, length = 255)
    private String option3;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topicid", nullable = false)
    private Topic topic;

    @Transient
    private Long topicId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
        this.topicId = topic != null ? topic.getId() : null;
    }

    public Long getTopicId() {
        return topicId != null ? topicId : (topic != null ? topic.getId() : null);
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
}
