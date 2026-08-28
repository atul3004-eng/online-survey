package com.kiptoo2000.survey.bean;

import com.kiptoo2000.survey.model.Question;
import com.kiptoo2000.survey.model.Topic;
import com.kiptoo2000.survey.repository.SurveyRepository;
import com.kiptoo2000.survey.repository.TopicRepository;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name = "topic")
@SessionScoped
public class SurveyBean implements Serializable {

    private final TopicRepository topicRepository = new TopicRepository();
    private final SurveyRepository surveyRepository = new SurveyRepository();

    private Long id;
    private String title;
    private List<Question> questions;
    private int position;

    public SurveyBean() {
        questions = new ArrayList<Question>();
    }

    public String process() {
        position = 0;
        Topic selectedTopic = topicRepository.findById(id);
        title = selectedTopic != null ? selectedTopic.getTitle() : null;
        questions = topicRepository.findQuestionsByTopicId(id);
        return questions.isEmpty() ? "index" : "survey";
    }

    public List<SelectItem> getTopics() {
        List<Topic> topics = topicRepository.findAll();
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (Topic topic : topics) {
            items.add(new SelectItem(topic.getId(), topic.getTitle()));
        }
        return items;
    }

    public Question getQuestion() {
        return questions.get(position);
    }

    public int getQuestionCount() {
        return questions.size();
    }

    public void next() {
        if (position < questions.size() - 1) {
            position++;
        }
    }

    public void previous() {
        if (position > 0) {
            position--;
        }
    }

    public String cancel() {
        reset();
        return "index";
    }

    public String finish() {
        boolean done = surveyRepository.storeSurveyResults(id, questions);
        reset();
        return done ? "finish" : "error";
    }

    private void reset() {
        id = null;
        title = null;
        questions = new ArrayList<Question>();
        position = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
