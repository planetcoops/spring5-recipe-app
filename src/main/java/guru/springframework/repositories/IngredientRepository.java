package guru.springframework.repositories;

import guru.springframework.domain.Ingredient;
import org.springframework.data.repository.CrudRepository;

public interface IngredientRepository extends CrudRepository<Ingredient, Long> {

    Ingredient findIngredientByRecipeIdAndId(Long recipeId, Long id);
    void deleteIngredientByRecipeIdAndId(Long recipeId, Long id);

}
