package org.example.frontendservice.bdd.steps;

import io.cucumber.java.en.Then;

public class MenuDefinitions {

    private final MenuDetails menuDetails = new MenuDetails();

    @Then("Click on menu item {string}")
    public void clickOnMenuItem(String text) {
        menuDetails.clickOnMenuItemWithText(text);
    }

    @Then("Click on menu auth-item {string}")
    public void clickOnMenuAuthItem(String text) {
        menuDetails.clickOnMenuAuthItemWithText(text);
    }
}
