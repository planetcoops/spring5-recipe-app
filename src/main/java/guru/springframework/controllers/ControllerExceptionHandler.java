package guru.springframework.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @InitBinder
    void initBinder(WebDataBinder webDataBinder) {
        //webDataBinder.setDisallowedFields("id");
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNumberFormatException(HttpServletRequest request, Throwable throwable, Model model) {
        final StringBuilder names = new StringBuilder("");
        request.getHeaderNames().asIterator().forEachRemaining(h -> names.append(h).append(" "));
        log.error("Handling number format exception: " + names.toString());
        log.error("user-agent: " + request.getHeader("user-agent"));
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("404error");
        model.addAttribute("exception", throwable);
        StringBuilder sb = new StringBuilder();
        Arrays.asList(throwable.getStackTrace()).forEach(e -> sb.append(e.toString()).append("\n"));
        model.addAttribute("stack", sb.toString());
        return "400error";
    }

}
