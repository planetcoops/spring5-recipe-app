package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Controller
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping({"/recipe/{id}/show"})
    public String getRecipePage(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findById(Long.parseLong(id)));
        return "recipe/show";
    }

    @GetMapping(path = "recipe/new")
    public String newRecipe(Model model) {
        model.addAttribute("recipe", new RecipeCommand());
        return "recipe/recipeform";
    }

    @GetMapping({"/recipe/{id}/update"})
    public String updateRecipe(@PathVariable String id, Model model) {
        try {
            model.addAttribute("recipe", recipeService.findCommandById(Long.parseLong(id)));
        } catch (NumberFormatException e) {
            log.error("Invalid id", e);
        }
        return "recipe/recipeform";
    }

    @GetMapping({"/recipe/{id}/delete"})
    public String deleteRecipe(@PathVariable String id) {
        try {
            recipeService.deleteById(Long.parseLong(id));
            log.debug("Deleted id: " + id);
        } catch (NumberFormatException e) {
            log.error("Invalid id", e);
        }
        return "redirect:/";
    }

    @PostMapping("/recipe")
    //@RequestMapping(path = "/recipe", method = RequestMethod.POST)
    public String saveOrUpdate(@ModelAttribute RecipeCommand command) {
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command);
        return "redirect:/recipe/" + savedCommand.getId() + "/show";
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(HttpServletRequest request, Throwable throwable, Model model) {
        final StringBuilder names = new StringBuilder("");
        request.getHeaderNames().asIterator().forEachRemaining(h -> names.append(h).append(" "));
        log.error("Handling not found exception: " + names.toString());
        log.error("user-agent: " + request.getHeader("user-agent"));
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("404error");
        model.addAttribute("exception", throwable);
        StringBuilder sb = new StringBuilder();
        Arrays.asList(throwable.getStackTrace()).forEach(e -> sb.append(e.toString()).append("\n"));
        model.addAttribute("stack", sb.toString());
        return "404error";
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
