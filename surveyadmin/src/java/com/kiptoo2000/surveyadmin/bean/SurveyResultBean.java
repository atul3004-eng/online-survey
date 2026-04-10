package com.kiptoo2000.surveyadmin.bean;

import com.kiptoo2000.surveyadmin.model.SurveyResultRow;
import com.kiptoo2000.surveyadmin.model.Topic;
import com.kiptoo2000.surveyadmin.repository.SurveyResultRepository;
import com.kiptoo2000.surveyadmin.repository.TopicRepository;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class SurveyResultBean implements Serializable {

    private final TopicRepository topicRepository = new TopicRepository();
    private final SurveyResultRepository surveyResultRepository = new SurveyResultRepository();
    private Long topicId;

    private Topic topic;
    private List<SurveyResultRow> results;

    @PostConstruct
    public void init() {
        results = new ArrayList<SurveyResultRow>();
        topicId = resolveTopicId();
        if (topicId != null) {
            topic = topicRepository.findById(topicId);
            results = surveyResultRepository.findByTopicId(topicId);
        }
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

    public List<SurveyResultRow> getResults() {
        return results;
    }

    private Long resolveTopicId() {
        String value = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("topicId");
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
