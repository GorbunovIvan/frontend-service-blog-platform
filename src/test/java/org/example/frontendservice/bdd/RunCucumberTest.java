package org.example.frontendservice.bdd;

import io.cucumber.testng.*;
import org.testng.annotations.*;

@CucumberOptions(
        plugin = "testng",
        features = "src/test/resources/features",
        glue = { "org.example.frontendservice.bdd.steps", "org.example.frontendservice.bdd.hooks" }
)
public class RunCucumberTest extends AbstractTestNGCucumberTests {

    @Test
    @Ignore(value = "We need to run super.runScenario() method to run scenarios testing," +
            "but without any tests in this (RunCucumberTest) class, " +
            "IntelliJ IDEA cannot run any test of the parent class.")
    public void test() {
    }
}
