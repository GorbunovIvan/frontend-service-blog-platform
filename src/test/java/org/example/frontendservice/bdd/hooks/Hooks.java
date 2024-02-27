package org.example.frontendservice.bdd.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.example.frontendservice.bdd.steps.BasicPageDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {

    private final BasicPageDefinitions basicPage = new BasicPageDefinitions();

    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);

    // Just to check if it works fine
    public static void main(String[] args) {
        var hooks = new Hooks();
        hooks.before();
        hooks.after();
    }

    @Before(value = "@AuthHook", order = 1) // must be run first
    public void before() {
        logger.info("Before hook");
        performRegistration();
        performLogin();
    }

    @Before(value = "@PostsHooks", order = 2)
    public void beforePostsScenarios() {
        logger.info("Before posts hook");
        performAddingPost();
    }

    @After(value = "@AuthHook", order = 1) // must be run last
    public void after() {
        logger.info("After hook");
        performDeletingUser();
    }

    @After(value = "@PostsHooks", order = 2)
    public void afterPostsScenarios() {
        logger.info("After posts hook");
        performDeletingPost();
    }

    private void performRegistration() {
        basicPage.openURL("http://localhost:8084/auth/register");
        basicPage.hasText("h2", "Registration");
        basicPage.enterTextInInputFieldByName("delete-username","username");
        basicPage.enterTextInInputFieldByName("delete-password", "password");
        basicPage.enterTextInInputFieldByName("+1234598765", "phoneNumber");
        basicPage.enterTextInInputFieldByName("01.11.2012", "birthDate");
        basicPage.clickInputSubmitFieldWithValue("Register");
        basicPage.hasText("h2", "Log in");
    }

    private void performLogin() {
        basicPage.openURL("http://localhost:8084/auth/login");
        basicPage.hasText("h2", "Log in");
        basicPage.enterTextInInputFieldByName("delete-username","username");
        basicPage.enterTextInInputFieldByName("delete-password", "password");
        basicPage.clickInputSubmitFieldWithValue("Log in");
        basicPage.hasText("a", "My page");
        basicPage.hasText("h2", "User");
        basicPage.hasText("span", "delete-username");
    }

    private void performDeletingUser() {
        basicPage.openURL("http://localhost:8084/users/self");
        basicPage.hasText("a", "My page");
        basicPage.hasText("span", "delete-username");
        basicPage.clickInputSubmitFieldWithValueInFromWithId("Delete", "user-deleting-form");
    }

    private void performAddingPost() {
        basicPage.openURL("http://localhost:8084/users/self");
        basicPage.hasText("h2", "User");
        basicPage.enterTextInInputFieldByNameInFromWithId("post to delete", "textarea", "content", "post-adding-form");
        basicPage.clickInputSubmitFieldWithValueInFromWithId("Add post", "post-adding-form");
        basicPage.containsText("a", "post to delete");
    }

    private void performDeletingPost() {
        basicPage.openURL("http://localhost:8084/users/self");
        basicPage.hasText("h2", "User");
        basicPage.containsText("a", "post to delete");
        basicPage.clickLinkContainingText("post to delete");
        basicPage.hasText("h2", "Post");
        basicPage.hasText("span", "post to delete");
        basicPage.clickInputSubmitFieldWithValueInFromWithId("Delete", "post-delete-form");
        basicPage.hasText("h2", "User");
    }
}
