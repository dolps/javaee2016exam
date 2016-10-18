package com.woact.dolplads.exam2016.frontend.controller;

import com.woact.dolplads.exam2016.backend.entity.AbstractPost;
import com.woact.dolplads.exam2016.backend.entity.Post;
import com.woact.dolplads.exam2016.backend.entity.User;
import com.woact.dolplads.exam2016.backend.entity.Vote;
import com.woact.dolplads.exam2016.backend.service.PostEJB;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Created by dolplads on 17/10/2016.
 */

@Log
@SessionScoped
@Named
public class NewsController implements Serializable {
    @Inject
    private LoginController loginController;
    @EJB
    private PostEJB postEJB;
    private Post post;

    private String sortValue;
    private Map<Long, Integer> userVoteMap;
    private ListDataModel<Post> listDataModel;

    public ListDataModel<Post> getListDataModel() {
        List<Post> posts = getPostsBySortValue();

        User sessionUser = loginController.getSessionUser();
        if (sessionUser != null) {
            setMyVote(sessionUser, posts);
        }
        listDataModel = new ListDataModel<>(posts);
        return listDataModel;
    }

    public void setListDataModel(ListDataModel<Post> listDataModel) {
        this.listDataModel = listDataModel;
    }

    @PostConstruct
    public void doConstruct() {
        post = new Post();
        userVoteMap = new ConcurrentHashMap<>();
        listDataModel = new ListDataModel<>();
        sortValue = "time";
    }

    public void voteListener(ValueChangeEvent event) {
        int index = listDataModel.getRowIndex();
        Post p = listDataModel.getRowData();
        String value = event.getNewValue().toString();
        postEJB.voteForPost(loginController.getSessionUser().getUserName(), p.getId(), Integer.parseInt(value));
    }

    public String doCreate() {
        User current = loginController.getSessionUser();

        Post persisted = postEJB.createPost(current.getUserName(), post);

        post = new Post();
        return "home.jsf";
    }

    private List<Post> getPostsBySortValue() {
        List<Post> posts;
        if (sortValue != null) {
            if (sortValue.equals("time")) {
                posts = postEJB.findAllPostsByTime();
            } else {
                posts = postEJB.findAllPostsByScore();
            }
        } else {
            posts = postEJB.findAllPostsByTime();
        }
        return posts;
    }

    private void setMyVote(User sessionUser, List<Post> posts) {
        posts.stream().map(Post::getId)
                .forEach(postId -> {
                    int val = postEJB.findVoteValueForPost(sessionUser.getUserName(), postId);
                    userVoteMap.put(postId, val);

                });
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getSortValue() {
        return sortValue;
    }

    public void setSortValue(String sortValue) {
        this.sortValue = sortValue;
    }

    public Map<Long, Integer> getUserVoteMap() {
        log.log(Level.INFO, "getting uservotemap");
        return userVoteMap;
    }

    public void setUserVoteMap(Map<Long, Integer> userVoteMap) {
        log.log(Level.INFO, "setting uservotemap");
        this.userVoteMap = userVoteMap;
    }
}