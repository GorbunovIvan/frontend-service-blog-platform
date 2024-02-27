package org.example.frontendservice.bdd.steps;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class BasicPageDetails {

    public void openURL(String url) {
        open(url);
    }

    public void clickLinkWithText(String text) {
        var element = $(By.xpath("//a[text()='" + text + "']"));
        element.click();
    }

    public void clickLinkContainingText(String text) {
        var element = $(By.xpath("//a[contains(text(), '" + text + "')]"));
        element.click();
    }

    public void findTagWithText(String tag, String text) {
        var element = $(By.xpath("//" + tag + "[text()='" + text + "']"));
        element.shouldBe(Condition.visible);
    }

    public void findTagWhichContainsText(String tag, String text) {
        var element = $(By.xpath("//" + tag + "[contains(text(), '" + text + "')]"));
        element.shouldBe(Condition.visible);
    }

    public void enterTextInInputFieldByName(String text, String fieldName) {
        var element = $(By.xpath("//input[@name='" + fieldName + "']"));
        element.setValue(text);
    }

    public void enterTextInInputFieldByNameInFromWithId(String text, String fieldType, String fieldName, String formId) {
        var element = $(By.xpath("//form[@id='" + formId + "']//" + fieldType + "[@name='" + fieldName + "']"));
        element.setValue(text);
    }

    public void clickInputSubmitFieldWithValue(String valueOfSubmitField) {
        var element = $(By.xpath("//input[@type='submit'][@value='" + valueOfSubmitField + "']"));
        element.click();
    }

    public void clickInputSubmitFieldWithValueInFromWithId(String valueOfSubmitField, String formId) {
        var element = $(By.xpath("//form[@id='" + formId + "']//input[@type='submit'][@value='" + valueOfSubmitField + "']"));
        element.click();
    }
}
