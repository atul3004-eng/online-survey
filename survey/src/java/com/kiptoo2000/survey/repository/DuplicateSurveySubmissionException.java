package com.kiptoo2000.survey.repository;

public class DuplicateSurveySubmissionException extends RuntimeException {

    public DuplicateSurveySubmissionException(String message) {
        super(message);
    }
}
