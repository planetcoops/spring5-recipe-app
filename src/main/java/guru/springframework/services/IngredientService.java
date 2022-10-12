package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;

public interface IngredientService {

    IngredientCommand findCommandByRecipeIdAndId(long recipeId, long id);
    IngredientCommand saveIngredientCommand(IngredientCommand ingredientCommand);
    void deleteByRecipeIdAndId(long recipeId, long id);
}
