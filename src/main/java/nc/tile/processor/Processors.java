package nc.tile.processor;

import nc.block.tile.energy.processor.BlockAlloyFurnace;
import nc.block.tile.energy.processor.BlockDecayHastener;
import nc.block.tile.energy.processor.BlockFuelReprocessor;
import nc.block.tile.energy.processor.BlockIsotopeSeparator;
import nc.block.tile.energy.processor.BlockManufactory;
import nc.config.NCConfig;
import nc.crafting.processor.AlloyFurnaceRecipes;
import nc.crafting.processor.DecayHastenerRecipes;
import nc.crafting.processor.FuelReprocessorRecipes;
import nc.crafting.processor.IsotopeSeparatorRecipes;
import nc.crafting.processor.ManufactoryRecipes;

public class Processors {
	
	public static class TileManufactory extends TileEnergyItemProcessor {

		public TileManufactory() {
			super("Manufactory", 1, 1, NCConfig.processor_time[0], NCConfig.processor_power[0], ManufactoryRecipes.instance(), 1);
		}
		
		public void setBlockState() {
			BlockManufactory.setState(isProcessing, worldObj, pos);
		}
	}
	
	public static class TileIsotopeSeparator extends TileEnergyItemProcessor {

		public TileIsotopeSeparator() {
			super("Isotope Separator", 1, 2, NCConfig.processor_time[1], NCConfig.processor_power[1], IsotopeSeparatorRecipes.instance(), 2);
		}
		
		public void setBlockState() {
			BlockIsotopeSeparator.setState(isProcessing, worldObj, pos);
		}
	}
	
	public static class TileDecayHastener extends TileEnergyItemProcessor {

		public TileDecayHastener() {
			super("Decay Hastener", 1, 1, NCConfig.processor_time[2], NCConfig.processor_power[2], DecayHastenerRecipes.instance(), 3);
		}
		
		public void setBlockState() {
			BlockDecayHastener.setState(isProcessing, worldObj, pos);
		}
	}
	
	public static class TileFuelReprocessor extends TileEnergyItemProcessor {

		public TileFuelReprocessor() {
			super("Fuel Reprocessor", 1, 4, NCConfig.processor_time[3], NCConfig.processor_power[3], FuelReprocessorRecipes.instance(), 4);
		}
		
		public void setBlockState() {
			BlockFuelReprocessor.setState(isProcessing, worldObj, pos);
		}
	}
	
	public static class TileAlloyFurnace extends TileEnergyItemProcessor {

		public TileAlloyFurnace() {
			super("alloy_furnace", 2, 1, NCConfig.processor_time[4], NCConfig.processor_power[4], AlloyFurnaceRecipes.instance(), 5);
		}
		
		public void setBlockState() {
			BlockAlloyFurnace.setState(isProcessing, worldObj, pos);
		}
	}
	
	/*public static class TileInfuser extends TileEnergyItemFluidProcessor {

		public TileInfuser() {
			super("infuser", 1, 1, 1, 0, new int[] {16000}, new FluidConnection[] {FluidConnection.IN}, new String[] {}, NCConfig.processor_time[5], NCConfig.processor_power[5], InfuserRecipes.instance(), 6);
		}
		
		public void setBlockState() {
			BlockInfuser.setState(isProcessing, worldObj, pos);
		}
	}*/
}
