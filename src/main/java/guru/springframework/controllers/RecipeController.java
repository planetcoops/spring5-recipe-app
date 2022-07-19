package guru.springframework.controllers;

import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping({"/recipe/show/{id}"})
    public String getRecipePage(@PathVariable String id, Model model) {
        try {
            model.addAttribute("recipe", recipeService.findById(Long.parseLong(id)));
        } catch (NumberFormatException e) {
            log.error("Invalid id", e);
        }
        return "recipe/show";
    }
}
