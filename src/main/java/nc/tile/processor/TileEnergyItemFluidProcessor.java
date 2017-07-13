package nc.tile.processor;

import ic2.api.energy.EnergyNet;
import nc.ModCheck;
import nc.config.NCConfig;
import nc.energy.EnumStorage.EnergyConnection;
import nc.fluid.EnumTank.FluidConnection;
import nc.handler.ProcessorRecipeHandler;
import nc.init.NCItems;
import nc.tile.IGui;
import nc.tile.dummy.IInterfaceable;
import nc.tile.energyFluid.TileEnergyFluidSidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;

public abstract class TileEnergyItemFluidProcessor extends TileEnergyFluidSidedInventory implements IInterfaceable, IGui {
	
	public final int defaultProcessTime;
	public int baseProcessTime;
	public final int baseProcessPower;
	public final int itemInputSize;
	public final int fluidInputSize;
	public final int itemOutputSize;
	public final int fluidOutputSize;
	
	public int time;
	public boolean isProcessing;
	
	public final boolean hasUpgrades;
	public final int upgradeMeta;
	
	public int tickCount;
	
	public final ProcessorRecipeHandler recipes;
	
	public TileEnergyItemFluidProcessor(String name, int itemInSize, int fluidInSize, int itemOutSize, int fluidOutSize, int[] fluidCapacity, FluidConnection[] fluidConnection, String[][] allowedFluids, int time, int power, ProcessorRecipeHandler recipes) {
		this(name, itemInSize, fluidInSize, itemOutSize, fluidOutSize, fluidCapacity, fluidConnection, allowedFluids, time, power, false, recipes, 1);
	}
	
	public TileEnergyItemFluidProcessor(String name, int itemInSize, int fluidInSize, int itemOutSize, int fluidOutSize, int[] fluidCapacity, FluidConnection[] fluidConnection, String[][] allowedFluids, int time, int power, ProcessorRecipeHandler recipes, int upgradeMeta) {
		this(name, itemInSize, fluidInSize, itemOutSize, fluidOutSize, fluidCapacity, fluidConnection, allowedFluids, time, power, true, recipes, upgradeMeta);
	}
	
	public TileEnergyItemFluidProcessor(String name, int itemInSize, int fluidInSize, int itemOutSize, int fluidOutSize, int[] fluidCapacity, FluidConnection[] fluidConnection, String[][] allowedFluids, int time, int power, boolean upgrades, ProcessorRecipeHandler recipes, int upgradeMeta) {
		super(name, itemInSize + itemOutSize + (upgrades ? 2 : 0), 32000, EnergyConnection.IN, fluidCapacity, fluidCapacity, fluidCapacity, fluidConnection, allowedFluids);
		itemInputSize = itemInSize;
		fluidInputSize = fluidInSize;
		itemOutputSize = itemOutSize;
		fluidOutputSize = fluidOutSize;
		defaultProcessTime = time;
		baseProcessTime = time;
		baseProcessPower = power;
		hasUpgrades = upgrades;
		this.recipes = recipes;
		this.upgradeMeta = upgradeMeta;
		
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
		updateProcessor();
	}
	
	public void updateProcessor() {
		boolean flag = isProcessing;
		boolean flag1 = false;
		if(!worldObj.isRemote) {
			tick();
			if (canProcess() && !isPowered()) {
				isProcessing = true;
				time += getSpeedMultiplier();
				storage.changeEnergyStored(-getProcessPower());
				if (time >= baseProcessTime) {
					time = 0;
					process();
				}
			} else {
				isProcessing = false;
				if (time != 0 && !isPowered()) time = MathHelper.clamp_int(time - 2*getSpeedMultiplier(), 0, baseProcessTime);
			}
			if (flag != isProcessing) {
				flag1 = true;
				if (isEnergyTileSet && ModCheck.ic2Loaded()) {
					/*MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));*/ EnergyNet.instance.removeTile(this);
					isEnergyTileSet = false;
				}
				setBlockState();
				//invalidate();
			}
		} else {
			isProcessing = canProcess() && !isPowered();
		}
		
		if (flag1) {
			markDirty();
		}
	}
	
	public abstract void setBlockState();
	
	public void tick() {
		if (tickCount > NCConfig.processor_update_rate) {
			tickCount = 0;
		} else {
			tickCount++;
		}
	}
	
	public boolean shouldCheck() {
		return tickCount > NCConfig.processor_update_rate;
	}
	
	public void onAdded() {
		super.onAdded();
		baseProcessTime = defaultProcessTime;
		if (!worldObj.isRemote) isProcessing = isProcessing();
	}
	
	public boolean isProcessing() {
		if (worldObj.isRemote) return isProcessing;
		return !isPowered() && canProcess();
	}
	
	public boolean isPowered() {
		return worldObj.isBlockPowered(pos);
	}
	
	public boolean canProcess() {
		return canProcessStacks();
	}
	
	// IC2 Tiers
	
	public int getSourceTier() {
		return 1;
	}
	
	public int getSinkTier() {
		return 2;
	}
	
	// Processing
	
	public int getSpeedMultiplier() {
		if (!hasUpgrades) return 1;
		ItemStack speedStack = inventoryStacks[itemInputSize + itemOutputSize];
		if (speedStack == null) return 1;
		return speedStack.stackSize + 1;
	}
	
	public int getProcessTime() {
		return Math.max(1, baseProcessTime/getSpeedMultiplier());
	}
	
	public int getProcessPower() {
		return baseProcessPower*getSpeedMultiplier()*(getSpeedMultiplier() + 1) / 2;
	}
	
	public int getProcessEnergy() {
		return getProcessTime()*getProcessPower();
	}
	
	public boolean canProcessStacks() {
		for (int i = 0; i < itemInputSize; i++) {
			if (inventoryStacks[i] == null) {
				return false;
			}
		}
		for (int i = 0; i < fluidInputSize; i++) {
			if (tanks[i].getFluidAmount() <= 0) {
				return false;
			}
		}
		if (time >= baseProcessTime) {
			return true;
		}
		if (getProcessEnergy() > getMaxEnergyStored() && time <= 0 && getEnergyStored() < getMaxEnergyStored() /*- getProcessPower()*/) {
			return false;
		}
		if (getProcessEnergy() <= getMaxEnergyStored() && time <= 0 && getProcessEnergy() > getEnergyStored()) {
			return false;
		}
		if (getEnergyStored() < getProcessPower()) {
			return false;
		}
		Object[] output = getOutput(inputs());
		int[] itemInputOrder = recipes.getItemInputOrder(inputs(), recipes.getInput(output));
		int[] fluidInputOrder = recipes.getFluidInputOrder(inputs(), recipes.getInput(output));
		if (output == null || output.length != itemOutputSize + fluidOutputSize || itemInputOrder == ProcessorRecipeHandler.INVALID_ORDER || fluidInputOrder == ProcessorRecipeHandler.INVALID_ORDER) {
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
		if (recipes.getExtras(inputs()) instanceof Integer) baseProcessTime = (int) recipes.getExtras(inputs());
		return true;
	}
	
	public void process() {
		Object[] output = getOutput(inputs());
		int[] itemInputOrder = recipes.getItemInputOrder(inputs(), recipes.getInput(output));
		int[] fluidInputOrder = recipes.getFluidInputOrder(inputs(), recipes.getInput(output));
		if (output[0] == null || itemInputOrder == ProcessorRecipeHandler.INVALID_ORDER || fluidInputOrder == ProcessorRecipeHandler.INVALID_ORDER) return;
		for (int j = 0; j < itemOutputSize; j++) {
			if (inventoryStacks[j + itemInputSize] == null) {
				ItemStack outputStack = ((ItemStack)output[j]).copy();
				inventoryStacks[j + itemInputSize] = outputStack;
			} else if (inventoryStacks[j + itemInputSize].isItemEqual((ItemStack)output[j])) {
				inventoryStacks[j + itemInputSize].stackSize = inventoryStacks[j + itemInputSize].stackSize + ((ItemStack)output[j]).stackSize;
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
		for (int i = 0; i < itemInputSize; i++) {
			if (recipes != null) {
				inventoryStacks[i].stackSize = inventoryStacks[i].stackSize - recipes.getInputSize(itemInputOrder[i], output);
			} else {
				inventoryStacks[i].stackSize = inventoryStacks[i].stackSize - 1;
			}
			if (inventoryStacks[i].stackSize <= 0) {
				inventoryStacks[i] = null;
			}
		}
		for (int i = 0; i < fluidInputSize; i++) {
			if (recipes != null) {
				tanks[i].changeFluidStored(-recipes.getInputSize(fluidInputOrder[i], output));
			} else {
				tanks[i].changeFluidStored(-1000);
			}
			if (tanks[i].getFluidAmount() <= 0) {
				tanks[i].setFluidStored(null);
			}
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
	
	public Object[] getOutput(Object... stacks) {
		return recipes.getOutput(stacks);
	}
	
	// Inventory
	
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (stack == null) return false;
		if (hasUpgrades) {
			if (stack.getItem() == NCItems.upgrade) {
				if (slot == itemInputSize + itemOutputSize) return stack.getMetadata() == 0;
				else if (slot == itemInputSize + itemOutputSize + 1) return stack.getMetadata() == upgradeMeta;
			}
		}
		if (slot >= itemInputSize) return false;
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
		nbt.setBoolean("isProcessing", isProcessing);
		return nbt;
	}
	
	public void readAll(NBTTagCompound nbt) {
		super.readAll(nbt);
		time = nbt.getInteger("time");
		isProcessing = nbt.getBoolean("isProcessing");
	}
	
	// Inventory Fields

	public int getFieldCount() {
		return 3;
	}

	public int getField(int id) {
		switch (id) {
		case 0:
			return time;
		case 1:
			return getEnergyStored();
		case 2:
			return baseProcessTime;
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
			break;
		case 2:
			baseProcessTime = value;
		}
	}
}
