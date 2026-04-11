package com.kiptoo2000.survey.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AnswerDetailId implements Serializable {

    @Column(name = "surveyid")
    private Long surveyId;

    @Column(name = "questionid")
    private Long questionId;

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    // 🔥 REQUIRED
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerDetailId)) return false;
        AnswerDetailId that = (AnswerDetailId) o;
        return Objects.equals(surveyId, that.surveyId) &&
                Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surveyId, questionId);
    }
}