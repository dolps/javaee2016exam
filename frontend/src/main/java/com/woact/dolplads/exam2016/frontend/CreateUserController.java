package com.woact.dolplads.exam2016.backend;

import com.woact.dolplads.exam2016.backend.annotations.NotEmpty;
import com.woact.dolplads.exam2016.backend.entity.User;
import com.woact.dolplads.exam2016.backend.service.UserService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by dolplads on 13/10/2016.
 */
@Model
public class CreateUserController {
    @Inject
    private Logger logger;
    @EJB
    private UserService userService;
    @Inject
    private LoginController loginController;
    private User user;
    @NotEmpty
    private String confirmPassword;

    @PostConstruct
    public void init() {
        user = new User();
    }


    public String create() {
        if (!user.getPasswordHash().equals(confirmPassword)) {
            return "newUser.jsf";
        }

        User persisted = userService.save(user);
        if (persisted != null) {
            persisted.setLoggedIn(true);
            loginController.setSessionUser(persisted);

            return "home.jsf";
        }

        return "newUser.jsf";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}