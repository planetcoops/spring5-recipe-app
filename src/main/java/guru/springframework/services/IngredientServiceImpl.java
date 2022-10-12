package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.IngredientRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class IngredientServiceImpl implements IngredientService {
    private static final Logger log = LoggerFactory.getLogger(IngredientServiceImpl.class);
    private final IngredientRepository ingredientRepository;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    @Autowired
    public IngredientServiceImpl(IngredientRepository ingredientRepository, IngredientCommandToIngredient ingredientCommandToIngredient, IngredientToIngredientCommand ingredientToIngredientCommand, RecipeRepository recipeRepository, UnitOfMeasureRepository unitOfMeasureRepository) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public IngredientCommand findCommandByRecipeIdAndId(long recipeId, long id) {
        return ingredientToIngredientCommand.convert(ingredientRepository.findIngredientByRecipeIdAndId(recipeId, id));
    }

    @Override
    public IngredientCommand saveIngredientCommand(IngredientCommand ingredientCommand) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(ingredientCommand.getRecipeId());
        if (!recipeOptional.isPresent()) {
            log.error("Recipe not found for id: " + ingredientCommand.getRecipeId());
            return new IngredientCommand();
        } else {
            Recipe recipe = recipeOptional.get();
            Ingredient ingredient = ingredientRepository.findIngredientByRecipeIdAndId(recipe.getId(), ingredientCommand.getId());
            if (ingredient != null) {
                ingredient.setDescription(ingredientCommand.getDescription());
                ingredient.setAmount(ingredientCommand.getAmount());
                ingredient.setUom(unitOfMeasureRepository.findById(ingredientCommand.getUom().getId()).orElseThrow(() -> new RuntimeException("UOM NOT FOUND")));
            } else {
                ingredient = ingredientCommandToIngredient.convert(ingredientCommand);
                ingredient.setRecipe(recipe);
                recipe.addIngredient(ingredient);
            }
            Recipe savedRecipe = recipeRepository.save(recipe);
            Optional<Ingredient> ingredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredient -> recipeIngredient.getId().equals(ingredientCommand.getId()))
                    .findFirst();
            if (!ingredientOptional.isPresent()) {
                ingredientOptional = savedRecipe.getIngredients().stream()
                        .filter(i -> i.getDescription().equals((ingredientCommand.getDescription())))
                        .filter(i -> i.getAmount().equals(ingredientCommand.getAmount()))
                        .filter(i -> i.getUom().getId().equals(ingredientCommand.getUom().getId()))
                        .findFirst();
            }
            return ingredientToIngredientCommand.convert(ingredientOptional.get());
        }
    }

    @Override
    @Transactional
    public void deleteByRecipeIdAndId(long recipeId, long id) {
        ingredientRepository.deleteIngredientByRecipeIdAndId(recipeId, id);
    }
}
