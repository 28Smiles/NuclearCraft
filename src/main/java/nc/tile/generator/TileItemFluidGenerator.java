package nc.tile.generator;

import ic2.api.energy.event.EnergyTileUnloadEvent;
import nc.ModCheck;
import nc.config.NCConfig;
import nc.energy.EnumStorage.EnergyConnection;
import nc.fluid.EnumTank.FluidConnection;
import nc.handler.ProcessorRecipeHandler;
import nc.tile.IGui;
import nc.tile.dummy.IInterfaceable;
import nc.tile.energyFluid.TileEnergyFluidSidedInventory;
import nc.util.NCUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;

public abstract class TileItemFluidGenerator extends TileEnergyFluidSidedInventory implements IInterfaceable, IGui {

	public final int itemInputSize;
	public final int fluidInputSize;
	public final int itemOutputSize;
	public final int fluidOutputSize;
	public final int otherSlotsSize;
	
	public int time;
	public boolean isGenerating;
	public boolean hasConsumed;
	
	public int tickCount;
	
	public final ProcessorRecipeHandler recipes;
	
	public TileItemFluidGenerator(String name, int itemInSize, int fluidInSize, int itemOutSize, int fluidOutSize, int otherSize, int[] fluidCapacity, FluidConnection[] fluidConnection, String[][] allowedFluids, int capacity, ProcessorRecipeHandler recipes) {
		super(name, 2*itemInSize + itemOutSize + otherSize, capacity, EnergyConnection.OUT, fluidCapacity, fluidCapacity, fluidCapacity, fluidConnection, allowedFluids);
		itemInputSize = itemInSize;
		fluidInputSize = fluidInSize;
		itemOutputSize = itemOutSize;
		fluidOutputSize = fluidOutSize;
		otherSlotsSize = otherSize;
		this.recipes = recipes;
		
		int[] topSlots1 = new int[itemInSize];
		for (int i = 0; i < topSlots1.length; i++) {
			topSlots1[i] = i;
		}
		topSlots = topSlots1;
		
		int[] sideSlots1 = new int[itemInSize + itemOutSize];
		for (int i = 0; i < sideSlots1.length; i++) {
			sideSlots1[i] = i;
		}
		sideSlots = sideSlots1;
		
		int[] bottomSlots1 = new int[itemOutSize];
		for (int i = itemInSize; i < itemInSize + bottomSlots1.length; i++) {
			bottomSlots1[i - itemInSize] = i;
		}
		bottomSlots = bottomSlots1;
	}
	
	public void update() {
		super.update();
		updateGenerator();
	}
	
	public void updateGenerator() {
		boolean flag = isGenerating;
		boolean flag1 = false;
		if(!worldObj.isRemote) {
			if (time == 0) {
				consume();
			}
			if (canProcess() && isPowered()) {
				isGenerating = true;
				time += getRateMultiplier();
				storage.changeEnergyStored(getProcessPower());
				if (time >= getProcessTime()) {
					time = 0;
					output();
				}
			} else {
				isGenerating = false;
			}
			if (flag != isGenerating) {
				flag1 = true;
				setBlockState();
				//invalidate();
				if (isEnergyTileSet && ModCheck.ic2Loaded()) {
					MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
					isEnergyTileSet = false;
				}
			}
			pushEnergy();
			if (shouldCheck() && !flag1) {
				setBlockState();
				if (isEnergyTileSet && ModCheck.ic2Loaded()) {
					MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
					isEnergyTileSet = false;
				}
			}
		} else {
			isGenerating = canProcess() && isPowered();
		}
		
		if (flag1) {
			markDirty();
		}
	}
	
	public abstract void setBlockState();
	
	public void tick() {
		if (tickCount > NCConfig.generator_update_rate) {
			tickCount = 0;
		} else {
			tickCount++;
		}
	}
	
	public boolean shouldCheck() {
		return tickCount > NCConfig.generator_update_rate;
	}
	
	public void onAdded() {
		super.onAdded();
		if (!worldObj.isRemote) isGenerating = isGenerating();
		if (!worldObj.isRemote) hasConsumed = hasConsumed();
	}
	
	public boolean isGenerating() {
		if (worldObj.isRemote) return isGenerating;
		return isPowered() && time > 0;
	}
	
	public boolean isPowered() {
		return worldObj.isBlockPowered(pos);
	}
	
	public boolean hasConsumed() {
		if (worldObj.isRemote) return hasConsumed;
		for (int i = 0; i < itemInputSize; i++) {
			if (inventoryStacks[i + itemInputSize + itemOutputSize + otherSlotsSize] != null) {
				return true;
			}
		}
		for (int i = 0; i < fluidInputSize; i++) {
			if (tanks[i + fluidInputSize + fluidOutputSize].getFluid() != null) {
				return true;
			}
		}
		return false;
	}
	
	public boolean canProcess() {
		return canProcessStacks();
	}
	
	// Processing
	
	public abstract int getRateMultiplier();
		
	public abstract void setRateMultiplier(int value);
		
	public abstract int getProcessTime();
		
	public abstract void setProcessTime(int value);
		
	public abstract int getProcessPower();
		
	public abstract void setProcessPower(int value);
		
	public boolean canProcessStacks() {
		for (int i = 0; i < itemInputSize; i++) {
			if (inventoryStacks[i] == null && !hasConsumed) {
				return false;
			}
		}
		for (int i = 0; i < fluidInputSize; i++) {
			if (tanks[i].getFluidAmount() <= 0 && !hasConsumed) {
				return false;
			}
		}
		if (time >= getProcessTime()) {
			return true;
		}
		Object[] output = hasConsumed ? getOutput(consumedInputs()) : getOutput(inputs());
		if (output == null || output.length != itemOutputSize + fluidInputSize) {
			return false;
		}
		for(int j = 0; j < itemOutputSize; j++) {
			if (output[j] == null) {
				return false;
			} else {
				if (inventoryStacks[j + itemInputSize] != null) {
					if (!inventoryStacks[j + itemInputSize].isItemEqual((ItemStack)output[j])) {
						return false;
					} else if (inventoryStacks[j + itemInputSize].stackSize + ((ItemStack)output[j]).stackSize > inventoryStacks[j + itemInputSize].getMaxStackSize()) {
						return false;
					}
				}
			}
		}
		for(int j = 0; j < fluidOutputSize; j++) {
			if (output[recipes.itemOutputSize + j] == null) {
				return false;
			} else {
				if (tanks[j + fluidInputSize].getFluid() != null) {
					if (!tanks[j + fluidInputSize].getFluid().isFluidEqual((FluidStack)output[recipes.itemOutputSize + j])) {
						return false;
					} else if (tanks[j + fluidInputSize].getFluidAmount() + ((FluidStack)output[recipes.itemOutputSize + j]).amount > tanks[j + fluidInputSize].getCapacity()) {
						return false;
					}
				}
			}
		}
		return true;
	}
		
	public void consume() {
		if (!hasConsumed) {
			for (int i = 0; i < itemInputSize; i++) {
				if (inventoryStacks[i + itemInputSize + itemOutputSize + otherSlotsSize] != null) {
					inventoryStacks[i + itemInputSize + itemOutputSize + otherSlotsSize] = null;
				}
			}
			for (int i = 0; i < fluidInputSize; i++) {
				if (tanks[i + fluidInputSize + fluidOutputSize].getFluid() != null) {
					tanks[i + fluidInputSize + fluidOutputSize].setFluid(null);
				}
			}
			Object[] output = getOutput(inputs());
			int[] itemInputOrder = recipes.getItemInputOrder(inputs(), recipes.getInput(output));
			int[] fluidInputOrder = recipes.getFluidInputOrder(inputs(), recipes.getInput(output));
			if (itemInputOrder.length > 0 && shouldCheck()) NCUtil.getLogger().info("First item input: " + itemInputOrder[0]);
			if (fluidInputOrder.length > 0 && shouldCheck()) NCUtil.getLogger().info("First fluid input: " + fluidInputOrder[0]);
			if (output[0] == null || itemInputOrder == ProcessorRecipeHandler.INVALID_ORDER || fluidInputOrder == ProcessorRecipeHandler.INVALID_ORDER) return;
			for (int i = 0; i < itemInputSize; i++) {
				if (recipes != null) {
					inventoryStacks[i + itemInputSize + itemOutputSize + otherSlotsSize] = new ItemStack(inventoryStacks[i].getItem(), recipes.getInputSize(itemInputOrder[i], output), inventoryStacks[i].getMetadata());
					inventoryStacks[i].stackSize = inventoryStacks[i].stackSize - recipes.getInputSize(itemInputOrder[i], output);
				} else {
					inventoryStacks[i + itemInputSize + itemOutputSize + otherSlotsSize] = new ItemStack(inventoryStacks[i].getItem(), 1, inventoryStacks[i].getMetadata());
					inventoryStacks[i].stackSize = inventoryStacks[i].stackSize - 1;
				}
				if (inventoryStacks[i].stackSize <= 0) {
					inventoryStacks[i] = null;
				}
			}
			for (int i = 0; i < fluidInputSize; i++) {
				if (recipes != null) {
					tanks[i + fluidInputSize + fluidOutputSize].changeFluidStored(tanks[i].getFluid().getFluid(), recipes.getInputSize(fluidInputOrder[i], output));
					tanks[i].changeFluidStored(-recipes.getInputSize(fluidInputOrder[i], output));
				} else {
					tanks[i + fluidInputSize + fluidOutputSize].changeFluidStored(tanks[i].getFluid().getFluid(), 1000);
					tanks[i].changeFluidStored(-1000);
				}
				if (tanks[i].getFluidAmount() <= 0) {
					tanks[i].setFluidStored(null);
				}
			}
			hasConsumed = true;
		}
	}
		
	public void output() {
		if (hasConsumed) {
			Object[] output = getOutput(consumedInputs());
			for (int j = 0; j < itemOutputSize; j++) {
				if (output[j] != null) {
					if (inventoryStacks[j + itemInputSize] == null) {
						ItemStack outputStack = ((ItemStack)output[j]).copy();
						inventoryStacks[j + itemInputSize] = outputStack;
					} else if (inventoryStacks[j + itemInputSize].isItemEqual((ItemStack)output[j])) {
						inventoryStacks[j + itemInputSize].stackSize = inventoryStacks[j + itemInputSize].stackSize + ((ItemStack)output[j]).stackSize;
					}
				}
			}
			for (int j = 0; j < fluidOutputSize; j++) {
				if (output[j] != null) {
					if (tanks[j + fluidInputSize].getFluid() == null) {
						FluidStack outputStack = ((FluidStack)output[j]).copy();
						tanks[j + fluidInputSize].setFluidStored(outputStack);
					} else if (tanks[j + fluidInputSize].getFluid().isFluidEqual((FluidStack)output[j])) {
						tanks[j + fluidInputSize].changeFluidStored(((FluidStack)output[j]).amount);
					}
				}
			}
			for (int i = itemInputSize + itemOutputSize + otherSlotsSize; i < 2*itemInputSize + itemOutputSize + otherSlotsSize; i++) {
				inventoryStacks[i] = null;
			}
			for (int i = fluidInputSize + fluidOutputSize; i < 2*fluidInputSize + fluidOutputSize; i++) {
				tanks[i].setFluid(null);
			}
			hasConsumed = false;
		}
	}
		
	public Object[] inputs() {
		Object[] input = new Object[itemInputSize + fluidInputSize];
		for (int i = 0; i < itemInputSize; i++) {
			input[i] = inventoryStacks[i];
		}
		for (int i = itemInputSize; i < fluidInputSize + itemInputSize; i++) {
			input[i] = tanks[i - itemInputSize].getFluid();
		}
		return input;
	}
		
	public Object[] consumedInputs() {
		Object[] input = new Object[itemInputSize + fluidInputSize];
		for (int i = 0; i < itemInputSize; i++) {
			input[i] = inventoryStacks[i + itemInputSize + itemOutputSize + otherSlotsSize];
		}
		for (int i = itemInputSize; i < fluidInputSize; i++) {
			input[i] = tanks[i + fluidInputSize + fluidOutputSize].getFluid();
		}
		return input;
	}
		
	public Object[] getOutput(Object... stacks) {
		return recipes.getOutput(stacks);
	}
	
	// Inventory
	
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (stack == null) return false;
		else if (slot >= itemInputSize && slot < itemInputSize + itemOutputSize) return false;
		return isItemValid(stack);
	}
	
	public boolean isItemValid(ItemStack stack) {
		Object[] inputSets = recipes.getRecipes().keySet().toArray();
		for (int i = 0; i < inputSets.length; i++) {
			Object[] inputSet = (Object[])(inputSets[i]);
			for (int j = 0; j < inputSet.length; j++) {
				if (inputSet[j] instanceof ItemStack) {
					if (ItemStack.areItemsEqual((ItemStack)(inputSet[j]), stack)) return true;
				} else if (inputSet[j] instanceof ItemStack[]) {
					ItemStack[] stacks = (ItemStack[])(inputSet[j]);
					for (int k = 0; k < stacks.length; k++) {
						if (ItemStack.areItemsEqual(stacks[k], stack)) return true;
					}
				}
			}
		}
		return false;
	}
	
	// SidedInventory
	
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.DOWN ? bottomSlots : (side == EnumFacing.UP ? topSlots : sideSlots);
	}

	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing direction) {
		return isItemValidForSlot(slot, stack) && direction != EnumFacing.DOWN;
	}

	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction) {
		return direction != EnumFacing.UP && slot >= itemInputSize && slot < itemInputSize + itemOutputSize;
	}
	
	// Fluids
	
	public boolean canFill(FluidStack resource, int tankNumber) {
		if (tankNumber >= fluidInputSize) return false;
		for (int i = 0; i < fluidInputSize; i++) {
			if (tankNumber != i && tanks[i].getFluid() != null) if (tanks[i].getFluid().isFluidEqual(resource)) return false;
		}
		return true;
	}
	
	// NBT
	
	public NBTTagCompound writeAll(NBTTagCompound nbt) {
		super.writeAll(nbt);
		nbt.setInteger("time", time);
		nbt.setBoolean("isGenerating", isGenerating);
		nbt.setBoolean("hasConsumed", hasConsumed);
		return nbt;
	}
		
	public void readAll(NBTTagCompound nbt) {
		super.readAll(nbt);
		time = nbt.getInteger("time");
		isGenerating = nbt.getBoolean("isGenerating");
		hasConsumed = nbt.getBoolean("hasConsumed");
	}
	
	// Inventory Fields

	public int getFieldCount() {
		return 2;
	}

	public int getField(int id) {
		switch (id) {
		case 0:
			return time;
		case 1:
			return getEnergyStored();
		default:
			return 0;
		}
	}

	public void setField(int id, int value) {
		switch (id) {
		case 0:
			time = value;
			break;
		case 1:
			storage.setEnergyStored(value);
		}
	}
}
