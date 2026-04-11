package com.kiptoo2000.survey.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "answers_details")
public class AnswerDetail implements Serializable {

    @EmbeddedId
    private AnswerDetailId id = new AnswerDetailId();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("surveyId")
    @JoinColumn(name = "surveyid", nullable = false)
    private AnswerMaster survey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("questionId")
    @JoinColumn(name = "questionid", nullable = false)
    private Question question;

    @Column(name = "answer", nullable = false, length = 10)
    private String answer;

    public AnswerDetailId getId() {
        return id;
    }

    public void setId(AnswerDetailId id) {
        this.id = id;
    }

    public AnswerMaster getSurvey() {
        return survey;
    }

    public void setSurvey(AnswerMaster survey) {
        this.survey = survey;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
