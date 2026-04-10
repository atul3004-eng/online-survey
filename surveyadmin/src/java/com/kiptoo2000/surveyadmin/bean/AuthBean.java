package com.kiptoo2000.surveyadmin.bean;

import com.kiptoo2000.surveyadmin.repository.UserRepository;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class AuthBean implements Serializable {

    private final UserRepository userRepository = new UserRepository();
    private String username;
    private String password;
    private String loggedInUser;

    public String login() {
        if (userRepository.isValidLogin(username, password)) {
            loggedInUser = username;
            password = null;
            return "/topics.xhtml?faces-redirect=true";
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Login failed", "Invalid username or password."));
        return null;
    }

    public void logout() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.invalidateSession();
        externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
    }

    public boolean isLoggedIn() {
        return loggedInUser != null && !loggedInUser.trim().isEmpty();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }
}
