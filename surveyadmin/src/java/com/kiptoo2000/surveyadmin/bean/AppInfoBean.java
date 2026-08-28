package com.kiptoo2000.surveyadmin.bean;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@ApplicationScoped
public class AppInfoBean {

    public String getAppTitle() {
        return "Survey Admin";
    }
}
