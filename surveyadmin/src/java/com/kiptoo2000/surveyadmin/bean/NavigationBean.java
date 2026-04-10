package com.kiptoo2000.surveyadmin.bean;

import java.io.IOException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@RequestScoped
public class NavigationBean {

    @ManagedProperty(value = "#{authBean}")
    private AuthBean authBean;

    public void redirectToDefaultPage() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String target = authBean != null && authBean.isLoggedIn() ? "/topics.xhtml" : "/login.xhtml";
        externalContext.redirect(externalContext.getRequestContextPath() + target);
    }

    public AuthBean getAuthBean() {
        return authBean;
    }

    public void setAuthBean(AuthBean authBean) {
        this.authBean = authBean;
    }
}
