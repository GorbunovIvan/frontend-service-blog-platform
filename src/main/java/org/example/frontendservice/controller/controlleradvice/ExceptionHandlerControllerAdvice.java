package org.example.frontendservice.controller.controlleradvice;

import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Log4j2
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        printStackTraceToLogger(e);
        model.addAttribute("error", e.getMessage());
        return "error";
    }

    private void printStackTraceToLogger(Exception e) {
        if (!shouldPrintStackTrace(e)) {
            return;
        }
        if (e.getStackTrace() != null) {
            for (var el : e.getStackTrace()) {
                log.error(el.toString());
            }
        }
    }

    private boolean shouldPrintStackTrace(Exception e) {
        if (e instanceof NoResourceFoundException noStaticResourceException) {
            if (noStaticResourceException.getResourcePath().equals("favicon.ico")) {
                return false;
            }
        }
        return true;
    }
}
