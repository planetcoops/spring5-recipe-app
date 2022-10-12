package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;

import java.util.Set;

public interface RecipeService {

    Recipe findById(long id);

    RecipeCommand findCommandById(long id);
    Set<Recipe> getRecipes();
    RecipeCommand saveRecipeCommand(RecipeCommand command);
    void deleteById(long id);
}
