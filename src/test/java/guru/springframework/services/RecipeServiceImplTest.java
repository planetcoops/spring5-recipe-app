package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.repositories.RecipeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

class RecipeServiceImplTest {

    RecipeServiceImpl recipeService;
    @Mock
    RecipeRepository recipeRepository;
    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        recipeService = new RecipeServiceImpl(recipeRepository, null, null);
        Recipe recipe = new Recipe();
        HashSet<Recipe> recipesData= new HashSet<>();
        recipesData.add(recipe);
        when(recipeRepository.findAll()).thenReturn(recipesData);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void getRecipeByIdNotFound() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> {
            Recipe recipeReturned = recipeService.findById(1L);
        });

    }

    @Test
    public void getRecipeById() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        Recipe recipeReturned = recipeService.findById(1L);

        assertNotNull("Null recipe returned", recipeReturned);
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, never()).findAll();
    }

    @Test
    void getRecipes() {
        HashSet<Recipe> recipesData = new HashSet<>();
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipesData.add(recipe);
        when(recipeRepository.findAll()).thenReturn(recipesData);

        Set<Recipe> recipesSet = recipeService.getRecipes();
        assertEquals(1, recipesSet.size());
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    void testDeleteById() {
        recipeService.deleteById(2L);
        verify(recipeRepository, times(1)).deleteById(anyLong());
    }
}