package org.example.frontendservice.bdd.steps;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class MenuDetails {

    public void clickOnMenuItemWithText(String text) {
        var element = $(By.xpath("//div[@id='commonMenu']//a[text()='" + text + "']"));
        element.click();
    }

    public void clickOnMenuAuthItemWithText(String text) {
        var element = $(By.xpath("//div[@id='commonMenu']//div[@id='auth-block']//a[text()='" + text + "']"));
        element.click();
    }
}
