package org.example.frontendservice.bdd.steps;

import io.cucumber.java.en.Then;

public class BasicPageDefinitions {

    private final BasicPageDetails basicPage = new BasicPageDetails();

    @Then("Open url {string}")
    public void openURL(String arg0) {
        basicPage.openURL(arg0);
    }

    @Then("Content on tag {string} with text {string} visible")
    public void hasText(String tag, String text) {
        basicPage.findTagWithText(tag, text);
    }

    @Then("Content on tag {string} containing text {string} visible")
    public void containsText(String tag, String text) {
        basicPage.findTagWhichContainsText(tag, text);
    }

    @Then("Click {string} link")
    public void clickLink(String arg0) {
        basicPage.clickLinkWithText(arg0);
    }

    @Then("Click containing text {string} link")
    public void clickLinkContainingText(String arg0) {
        basicPage.clickLinkContainingText(arg0);
    }

    @Then("Enter {string} in the input field by name {string}")
    public void enterTextInInputFieldByName(String text, String fieldName) {
        basicPage.enterTextInInputFieldByName(text, fieldName);
    }

    @Then("Enter {string} in the {string} field by name {string} of form with id {string}")
    public void enterTextInInputFieldByNameInFromWithId(String text, String fieldType, String fieldName, String formId) {
        basicPage.enterTextInInputFieldByNameInFromWithId(text, fieldType, fieldName, formId);
    }

    @Then("Click {string} input-submit")
    public void clickInputSubmitFieldWithValue(String valueOfSubmitField) {
        basicPage.clickInputSubmitFieldWithValue(valueOfSubmitField);
    }

    @Then("Click {string} input-submit of form with id {string}")
    public void clickInputSubmitFieldWithValueInFromWithId(String valueOfSubmitField, String formId) {
        basicPage.clickInputSubmitFieldWithValueInFromWithId(valueOfSubmitField, formId);
    }
}
