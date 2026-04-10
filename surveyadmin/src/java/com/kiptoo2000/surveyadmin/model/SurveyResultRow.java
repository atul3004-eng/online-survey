package com.kiptoo2000.surveyadmin.model;

import java.io.Serializable;

public class SurveyResultRow implements Serializable {

    private String question;
    private String option1;
    private String option2;
    private String option3;
    private int option1Percent;
    private int option2Percent;
    private int option3Percent;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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

    public int getOption1Percent() {
        return option1Percent;
    }

    public void setOption1Percent(int option1Percent) {
        this.option1Percent = option1Percent;
    }

    public int getOption2Percent() {
        return option2Percent;
    }

    public void setOption2Percent(int option2Percent) {
        this.option2Percent = option2Percent;
    }

    public int getOption3Percent() {
        return option3Percent;
    }

    public void setOption3Percent(int option3Percent) {
        this.option3Percent = option3Percent;
    }
}
