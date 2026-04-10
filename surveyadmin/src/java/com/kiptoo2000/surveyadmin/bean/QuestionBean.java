package com.kiptoo2000.surveyadmin.bean;

import com.kiptoo2000.surveyadmin.model.Question;
import com.kiptoo2000.surveyadmin.model.Topic;
import com.kiptoo2000.surveyadmin.repository.QuestionRepository;
import com.kiptoo2000.surveyadmin.repository.TopicRepository;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class QuestionBean implements Serializable {

    private final TopicRepository topicRepository = new TopicRepository();
    private final QuestionRepository questionRepository = new QuestionRepository();
    private Long topicId;
    private Long editId;

    private Topic topic;
    private Question currentQuestion;
    private List<Question> questions;

    @PostConstruct
    public void init() {
        questions = new ArrayList<Question>();
        topicId = resolveTopicId();
        editId = resolveLongParam("editId");
        loadData();
        if (editId != null) {
            loadQuestionForEdit(editId);
        } else {
            resetForm();
        }
    }

    public void save() {
        if (topic == null || topicId == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Missing topic", "Select a topic before adding questions.");
            return;
        }
        try {
            questionRepository.save(currentQuestion, topicId);
            addMessage(FacesMessage.SEVERITY_INFO, "Saved", "Question saved successfully.");
            resetForm();
            loadQuestions();
        } catch (RuntimeException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Save failed", ex.getMessage());
        }
    }

    public void edit(Question question) {
        Question editable = new Question();
        editable.setId(question.getId());
        editable.setText(question.getText());
        editable.setOption1(question.getOption1());
        editable.setOption2(question.getOption2());
        editable.setOption3(question.getOption3());
        editable.setTopicId(topicId);
        currentQuestion = editable;
    }

    public void delete(Question question) {
        try {
            questionRepository.delete(question.getId());
            addMessage(FacesMessage.SEVERITY_INFO, "Deleted", "Question deleted successfully.");
            if (currentQuestion.getId() != null && currentQuestion.getId().equals(question.getId())) {
                resetForm();
            }
            loadQuestions();
        } catch (RuntimeException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Delete failed", ex.getMessage());
        }
    }

    public void resetForm() {
        currentQuestion = new Question();
        currentQuestion.setTopicId(topicId);
        editId = null;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Topic getTopic() {
        return topic;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public boolean isTopicSelected() {
        return topic != null;
    }

    private void loadData() {
        currentQuestion = new Question();
        if (topicId == null) {
            return;
        }
        topic = topicRepository.findById(topicId);
        loadQuestions();
    }

    private void loadQuestions() {
        questions = topicId != null ? questionRepository.findByTopicId(topicId) : new ArrayList<Question>();
    }

    private Long resolveTopicId() {
        return resolveLongParam("topicId");
    }

    private Long resolveLongParam(String paramName) {
        String value = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get(paramName);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void loadQuestionForEdit(Long id) {
        Question question = questionRepository.findById(id);
        if (question == null) {
            resetForm();
            return;
        }
        Question editable = new Question();
        editable.setId(question.getId());
        editable.setText(question.getText());
        editable.setOption1(question.getOption1());
        editable.setOption2(question.getOption2());
        editable.setOption3(question.getOption3());
        editable.setTopicId(topicId);
        currentQuestion = editable;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
