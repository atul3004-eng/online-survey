package com.kiptoo2000.surveyadmin.bean;

import com.kiptoo2000.surveyadmin.model.SchoolSurveyDetail;
import com.kiptoo2000.surveyadmin.model.SchoolSurveyOption;
import com.kiptoo2000.surveyadmin.repository.SchoolSurveySetupRepository;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class SchoolSurveySetupBean implements Serializable {
    private final SchoolSurveySetupRepository repository = new SchoolSurveySetupRepository();
    private SchoolSurveyDetail detail = new SchoolSurveyDetail();
    private SchoolSurveyOption option = new SchoolSurveyOption();
    private boolean editingDetail;
    private boolean editingOption;
    private java.util.List<SchoolSurveyDetail> details = new java.util.ArrayList<SchoolSurveyDetail>();
    private java.util.List<SchoolSurveyOption> options = new java.util.ArrayList<SchoolSurveyOption>();
    @javax.annotation.PostConstruct
    public void refresh() {
        try { details = repository.findDetails(); options = repository.findOptions(); }
        catch (RuntimeException ex) { failure(ex); }
    }
    public java.util.List<SchoolSurveyDetail> getDetails() { return details; }
    public java.util.List<SchoolSurveyOption> getOptions() { return options; }
    public boolean isEditingDetail() { return editingDetail; }
    public boolean isEditingOption() { return editingOption; }
    public void editDetail(SchoolSurveyDetail row) { detail = row; editingDetail = true; refresh(); }
    public void editOption(SchoolSurveyOption row) { option = row; editingOption = true; refresh(); }
    public void newDetail() { detail = new SchoolSurveyDetail(); editingDetail = false; }
    public void newOption() { option = new SchoolSurveyOption(); editingOption = false; }
    public void deleteDetail(SchoolSurveyDetail row) {
        try {
            repository.deleteDetail(row.getId());
            if (row.getId().equals(detail.getId())) { newDetail(); }
            message(FacesMessage.SEVERITY_INFO, "Survey detail deleted."); refresh();
        } catch (RuntimeException ex) { failure(ex); }
    }
    public void deleteOption(SchoolSurveyOption row) {
        try {
            repository.deleteOption(row.getId());
            if (row.getId().equals(option.getId())) { newOption(); }
            message(FacesMessage.SEVERITY_INFO, "Option deleted."); refresh();
        } catch (RuntimeException ex) { failure(ex); }
    }
    public SchoolSurveyDetail getDetail() { return detail; }
    public SchoolSurveyOption getOption() { return option; }
    public void saveDetail() {
        try {
            repository.saveDetail(detail, editingDetail);
            message(FacesMessage.SEVERITY_INFO, "Survey detail saved with ID " + detail.getId() + ".");
            newDetail(); refresh();
        } catch (RuntimeException ex) { failure(ex); }
    }
    public void saveOption() {
        try {
            repository.saveOption(option, editingOption);
            message(FacesMessage.SEVERITY_INFO, "Option saved with ID " + option.getId() + ".");
            newOption(); refresh();
        } catch (RuntimeException ex) { failure(ex); }
    }
    private void failure(RuntimeException ex) {
        Logger.getLogger(getClass().getName()).log(Level.WARNING, "School survey setup insert failed", ex);
        message(FacesMessage.SEVERITY_ERROR, ex instanceof IllegalArgumentException ? ex.getMessage()
                : "Operation failed. Check the database connection, field values, and related records. Your entries have been kept.");
    }
    private void message(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, null));
    }
}
