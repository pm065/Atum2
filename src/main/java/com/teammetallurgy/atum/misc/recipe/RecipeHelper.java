package com.teammetallurgy.atum.misc.recipe;

import com.teammetallurgy.atum.api.recipe.recipes.KilnRecipe;
import com.teammetallurgy.atum.blocks.machines.tileentity.KilnTileEntity;
import com.teammetallurgy.atum.misc.StackHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

import static net.minecraft.world.item.alchemy.PotionUtils.setPotion;

public class RecipeHelper {
    public static void addBrewingRecipeWithSubPotions(ItemStack stack, Potion potionType) {
        addBrewingRecipeWithSubPotions(Ingredient.of(stack), potionType);
    }

    public static void addBrewingRecipeWithSubPotions(Tags.IOptionalNamedTag<Item> tag, Potion potionType) {
        addBrewingRecipeWithSubPotions(Ingredient.of(tag), potionType);
    }

    public static void addBrewingRecipeWithSubPotions(Ingredient ingredient, Potion potionType) {
        addRecipe(setPotion(new ItemStack(Items.POTION), Potions.WATER), ingredient, setPotion(new ItemStack(Items.POTION), Potions.MUNDANE));
        addRecipe(setPotion(new ItemStack(Items.POTION), Potions.AWKWARD), ingredient, setPotion(new ItemStack(Items.POTION), potionType));
        addRecipe(setPotion(new ItemStack(Items.SPLASH_POTION), Potions.AWKWARD), ingredient, setPotion(new ItemStack(Items.SPLASH_POTION), potionType));
        addRecipe(setPotion(new ItemStack(Items.SPLASH_POTION), Potions.WATER), ingredient, setPotion(new ItemStack(Items.SPLASH_POTION), Potions.MUNDANE));
        addRecipe(setPotion(new ItemStack(Items.LINGERING_POTION), Potions.WATER), ingredient, setPotion(new ItemStack(Items.LINGERING_POTION), Potions.MUNDANE));
        addRecipe(setPotion(new ItemStack(Items.LINGERING_POTION), Potions.AWKWARD), ingredient, setPotion(new ItemStack(Items.LINGERING_POTION), potionType));
    }

    public static boolean addRecipe(@Nonnull ItemStack input, @Nonnull ItemStack ingredient, @Nonnull ItemStack output) {
        return addRecipe(input, Ingredient.of(ingredient), output);
    }

    public static boolean addRecipe(@Nonnull ItemStack input, @Nonnull Ingredient ingredient, @Nonnull ItemStack output) {
        return BrewingRecipeRegistry.addRecipe(new BrewingNBT(Ingredient.of(input), ingredient, output));
    }

    public static <C extends IInventory, T extends Recipe<C>> Collection<T> getRecipes(RecipeManager recipeManager, Recipe<T> recipeType) {
        Map<ResourceLocation, Recipe<C>> recipesMap = recipeManager.byType(recipeType);
        return (Collection<T>) recipesMap.values();
    }

    public static <C extends IInventory, T extends Recipe<C>> boolean isItemValidForSlot(World world, @Nonnull ItemStack stack, Recipe<T> recipeType) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            Collection<T> recipes = RecipeHelper.getRecipes(serverWorld.getRecipeManager(), recipeType);
            for (Recipe<C> recipe : recipes) {
                for (Ingredient ingredient : recipe.getIngredients()) {
                    if (StackHelper.areIngredientsEqualIgnoreSize(ingredient, stack)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static  <C extends IInventory, T extends Recipe<C>> Boolean isValidRecipeInput(Collection<T> recipes, @Nonnull ItemStack input) {
        for (Recipe<C> recipe : recipes) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (StackHelper.areIngredientsEqualIgnoreSize(ingredient, input)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Collection<KilnRecipe> getKilnRecipesFromFurnace(RecipeManager recipeManager) {
        Collection<KilnRecipe> kilnRecipes = new ArrayList<>();
        for (FurnaceRecipe furnaceRecipe : RecipeHelper.getRecipes(recipeManager, Recipe.SMELTING)) {
            for (Ingredient input : furnaceRecipe.getIngredients()) {
                ItemStack output = furnaceRecipe.getResultItem();
                if (input != null && !output.isEmpty()) {
                    if (!KilnTileEntity.canKilnNotSmelt(input) && !KilnTileEntity.canKilnNotSmelt(output)) {
                        kilnRecipes.add(new KilnRecipe(input, output, furnaceRecipe.getExperience(), furnaceRecipe.getCookingTime()));
                    }
                }
            }
        }
        return kilnRecipes;
    }
}