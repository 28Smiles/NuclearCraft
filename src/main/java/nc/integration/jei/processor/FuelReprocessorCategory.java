package nc.integration.jei.processor;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import nc.config.NCConfig;
import nc.integration.jei.BaseCategory;
import nc.integration.jei.IJEIHandler;
import nc.integration.jei.JEIMethods.RecipeItemMapper;
import nc.recipe.SorptionType;

public class FuelReprocessorCategory extends BaseCategory {
	
	public FuelReprocessorCategory(IGuiHelper guiHelper, IJEIHandler handler) {
		super(guiHelper, handler, "fuel_reprocessor_idle", NCConfig.processor_time[3], 49, 30, 94, 38, 176, 3, 37, 38, 68, 30);
	}
	
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		RecipeItemMapper itemMapper = new RecipeItemMapper();
		itemMapper.map(SorptionType.INPUT, 0, 0, 50 - backPosX, 41 - backPosY);
		itemMapper.map(SorptionType.OUTPUT, 0, 1, 106 - backPosX, 31 - backPosY);
		itemMapper.map(SorptionType.OUTPUT, 1, 2, 126 - backPosX, 31 - backPosY);
		itemMapper.map(SorptionType.OUTPUT, 2, 3, 106 - backPosX, 51 - backPosY);
		itemMapper.map(SorptionType.OUTPUT, 3, 4, 126 - backPosX, 51 - backPosY);
		itemMapper.mapItemsTo(recipeLayout.getItemStacks(), ingredients);
	}
}
