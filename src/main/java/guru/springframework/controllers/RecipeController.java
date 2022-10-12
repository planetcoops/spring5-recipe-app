package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
        try {
            model.addAttribute("recipe", recipeService.findById(Long.parseLong(id)));
        } catch (NumberFormatException e) {
            log.error("Invalid id", e);
        }
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
}
