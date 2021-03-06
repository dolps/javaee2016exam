package com.woact.dolplads.exam2016.frontend.controller;

import com.woact.dolplads.exam2016.backend.entity.User;
import com.woact.dolplads.exam2016.backend.service.UserEJB;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by dolplads on 12/10/2016.
 * <p>
 * Handles the user session
 */
@SessionScoped
@Named
public class LoginController implements Serializable {
    private final String home = "home.xhtml";
    private User sessionUser;
    @EJB
    private UserEJB userEJB;
    @Inject
    private CredentialsController credentials;

    public String logIn() {
        User user = userEJB.login(credentials.getUserName(), credentials.getPassword());

        if (user != null) {
            sessionUser = user;
            sessionUser.setLoggedIn(true);

            return home;
        }

        return null;
    }

    public String logOut() {
        sessionUser = null;
        return home;
    }

    public User getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(User sessionUser) {
        this.sessionUser = sessionUser;
    }
}
