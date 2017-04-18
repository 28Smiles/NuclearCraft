package nc.integ.jei;

/*import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import nc.handler.ProcessorRecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class JEIWrapper extends BlankRecipeWrapper {
	public final ProcessorRecipeHandler recipe;

	public JEIWrapper(ProcessorRecipeHandler recipe) {    
		this.recipe = recipe;
	}
	
	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		List<List<ItemStack>> inputStacks = recipe.getInputStackAlternatives();
		
		if (inputStacks != null) {
			ingredients.setInputLists(ItemStack.class, inputStacks);
		}
		
		List<ItemStack> outputs = new ArrayList<ItemStack>();
		
		for(RecipeOutput out : recipe.getOutputs()) {
			if(out.getOutput() != null) {
				outputs.add(out.getOutput());
			}
		}
		
		ingredients.setOutputs(ItemStack.class, outputs);
		
		List<FluidStack> inputFluidStacks = recipe.getInputFluidStacks();
		if (inputFluidStacks != null) {
			ingredients.setInputs(FluidStack.class, inputFluidStacks);
		}
		
		List<FluidStack> fluidOutputs = new ArrayList<FluidStack>();
		
		for(RecipeOutput out : recipe.getOutputs()) {
			if(out.getFluidOutput() != null) {
				fluidOutputs.add(out.getFluidOutput());
			}
		}
		
		ingredients.setOutputs(FluidStack.class, fluidOutputs);
	}
	
	public boolean isValid() {
		return recipe != null && recipe.isValid();
	}
	
	public int getEnergyRequired() {
		return recipe.getEnergyRequired();
	}
	
	public IRecipe getRecipe() {
		return recipe;
	}
}*/
