package com.kiptoo2000.surveyadmin.bean;

import com.kiptoo2000.surveyadmin.model.Topic;
import com.kiptoo2000.surveyadmin.repository.TopicRepository;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class TopicBean implements Serializable {

    private final TopicRepository topicRepository = new TopicRepository();

    @ManagedProperty(value = "#{authBean}")
    private AuthBean authBean;

    private List<Topic> topics;
    private Topic currentTopic;
    private Long editId;

    @PostConstruct
    public void init() {
        loadTopics();
        editId = resolveLongParam("editId");
        if (editId != null) {
            loadTopicForEdit(editId);
        } else {
            resetForm();
        }
    }

    public void save() {
        try {
            if (currentTopic.getId() == null) {
                currentTopic.setCreatedBy(authBean.getLoggedInUser());
            }
            topicRepository.save(currentTopic);
            addMessage(FacesMessage.SEVERITY_INFO, "Saved", "Topic saved successfully.");
            resetForm();
            loadTopics();
        } catch (RuntimeException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Save failed", ex.getMessage());
        }
    }

    public void edit(Topic topic) {
        Topic editable = new Topic();
        editable.setId(topic.getId());
        editable.setTitle(topic.getTitle());
        editable.setAddedOn(topic.getAddedOn());
        editable.setCreatedBy(topic.getCreatedBy());
        currentTopic = editable;
    }

    public void delete(Topic topic) {
        try {
            topicRepository.delete(topic.getId());
            addMessage(FacesMessage.SEVERITY_INFO, "Deleted", "Topic deleted successfully.");
            if (currentTopic.getId() != null && currentTopic.getId().equals(topic.getId())) {
                resetForm();
            }
            loadTopics();
        } catch (RuntimeException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Delete failed", ex.getMessage());
        }
    }

    public void resetForm() {
        currentTopic = new Topic();
        editId = null;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public Topic getCurrentTopic() {
        return currentTopic;
    }

    public void setCurrentTopic(Topic currentTopic) {
        this.currentTopic = currentTopic;
    }

    public AuthBean getAuthBean() {
        return authBean;
    }

    public void setAuthBean(AuthBean authBean) {
        this.authBean = authBean;
    }

    private void loadTopics() {
        topics = topicRepository.findAll();
    }

    private void loadTopicForEdit(Long id) {
        Topic topic = topicRepository.findById(id);
        if (topic == null) {
            resetForm();
            return;
        }
        Topic editable = new Topic();
        editable.setId(topic.getId());
        editable.setTitle(topic.getTitle());
        editable.setAddedOn(topic.getAddedOn());
        editable.setCreatedBy(topic.getCreatedBy());
        currentTopic = editable;
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

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
