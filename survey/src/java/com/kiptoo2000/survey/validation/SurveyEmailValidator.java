package com.kiptoo2000.survey.validation;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.mail.internet.InternetAddress;

@FacesValidator("surveyEmailValidator")
public class SurveyEmailValidator implements Validator {
    public static boolean isValid(String value) {
        if (value == null) return false;
        String email = value.trim();
        if (email.length() > 254 || !email.matches("[A-Za-z0-9_%+\\-]+(?:\\.[A-Za-z0-9_%+\\-]+)*@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)+")) return false;
        try {
            new InternetAddress(email, true).validate();
            return true;
        } catch (javax.mail.internet.AddressException ex) {
            return false;
        }
    }

    @Override public void validate(FacesContext context, UIComponent component, Object value) {
        if (!isValid(value == null ? null : value.toString())) {
            String message = java.util.ResourceBundle.getBundle("messages", context.getViewRoot().getLocale())
                    .getString("schoolHealth.emailRequired");
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        }
    }
}
