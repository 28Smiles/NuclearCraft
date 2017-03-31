package nc.tile.processor;

import nc.Global;
import nc.blocks.tile.processor.BlockNuclearFurnace;
import nc.container.SlotFuel;
import nc.init.NCBlocks;
import nc.init.NCItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileNuclearFurnace extends TileEntity implements ITickable, ISidedInventory {
	private static final int[] SLOTS_TOP = new int[] {0};
	private static final int[] SLOTS_BOTTOM = new int[] {2, 1};
	private static final int[] SLOTS_SIDES = new int[] {1};
	private ItemStack[] furnaceItemStacks = new ItemStack[3];

	private int furnaceBurnTime;
	private int currentItemBurnTime;
	private int cookTime;
	private int totalCookTime;

	public int getSizeInventory() {
		return furnaceItemStacks.length;
	}

	public boolean isEmpty() {
		for (ItemStack itemstack : furnaceItemStacks) {
			if (!(itemstack == null)) {
				return false;
			}
		}
		return true;
	}

	public ItemStack getStackInSlot(int slot) {
		return furnaceItemStacks[slot];
	}

	public ItemStack decrStackSize(int index, int amount) {
		return ItemStackHelper.getAndSplit(furnaceItemStacks, index, amount);
	}

	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(furnaceItemStacks, index);
	}

	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack itemstack = furnaceItemStacks[index];
		boolean flag = !(stack == null) && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
		furnaceItemStacks[index] = stack;

		if (stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}

		if (index == 0 && !flag) {
			//totalCookTime = getCookTime(stack);
			totalCookTime = getCookTime();
			cookTime = 0;
			markDirty();
		}
	}

	public String getName() {
		return Global.MOD_ID + ".container." + "nuclear_furnace";
	}

	public boolean hasCustomName() {
		return false;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		NBTTagList nbttaglist = new NBTTagList();
		
		for (int i = 0; i < furnaceItemStacks.length; ++i) {
			if (furnaceItemStacks[i] != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				furnaceItemStacks[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}
		
		nbt.setTag("Items", nbttaglist);
		
		furnaceBurnTime = nbt.getInteger("BurnTime");
		cookTime = nbt.getInteger("CookTime");
		totalCookTime = nbt.getInteger("CookTimeTotal");
		currentItemBurnTime = getItemBurnTime(furnaceItemStacks[1]);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("BurnTime", (short) furnaceBurnTime);
		nbt.setInteger("CookTime", (short) cookTime);
		nbt.setInteger("CookTimeTotal", (short) totalCookTime);
		
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
		
		furnaceItemStacks = new ItemStack[getSizeInventory()];
		
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot");
			
			if (j >= 0 && j < furnaceItemStacks.length) {
				furnaceItemStacks[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
		return nbt;
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isBurning() {
		return furnaceBurnTime > 0;
	}

	@SideOnly(Side.CLIENT)
	public static boolean isBurning(IInventory inventory) {
		return inventory.getField(0) > 0;
	}

	@SuppressWarnings("unused")
	public void update() {
		boolean flag = isBurning();
		boolean flag1 = false;

		if (isBurning()) {
			--furnaceBurnTime;
		}

		if (!worldObj.isRemote) {
			ItemStack itemstack = furnaceItemStacks[1];

			if (isBurning() || !(itemstack == null) && !(furnaceItemStacks[0] == null)) {
				if (!isBurning() && canSmelt()) {
					furnaceBurnTime = getItemBurnTime(itemstack);
					currentItemBurnTime = furnaceBurnTime;

					if (isBurning()) {
						flag1 = true;

						if (!(itemstack == null)) {
							Item item = itemstack.getItem();
							itemstack.stackSize--;

							if (itemstack == null) {
								ItemStack item1 = item.getContainerItem(itemstack);
								furnaceItemStacks[1] = item1;
							}
						}
					}
				}

				if (isBurning() && canSmelt()) {
					++cookTime;

					if (cookTime == totalCookTime) {
						cookTime = 0;
						//totalCookTime = getCookTime(furnaceItemStacks.get(0));
						totalCookTime = getCookTime();
						smeltItem();
						flag1 = true;
					}
				} else {
					cookTime = 0;
				}
			} else if (!isBurning() && cookTime > 0) {
				cookTime = MathHelper.clamp_int(cookTime - 2, 0, totalCookTime);
			}

			if (flag != isBurning()) {
				flag1 = true;
				BlockNuclearFurnace.setState(isBurning(), worldObj, pos);
			}
		}

		if (flag1) {
			markDirty();
		}
	}

	public int getCookTime() {
		return 10;
	}

	private boolean canSmelt() {
		if ((furnaceItemStacks[0]) == null) {
			return false;
		} else {
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks[0]);

			if (itemstack == null) {
				return false;
			} else {
				ItemStack itemstack1 = furnaceItemStacks[2];
				if (itemstack1 == null) return true;
				if (!itemstack1.isItemEqual(itemstack)) return false;
				int result = itemstack1.stackSize + itemstack.stackSize;
				return result <= getInventoryStackLimit() && result <= itemstack1.getMaxStackSize();
			}
		}
	}

	public void smeltItem() {
		if (canSmelt()) {
			ItemStack itemstack = furnaceItemStacks[0];
			ItemStack itemstack1 = FurnaceRecipes.instance().getSmeltingResult(itemstack);
			ItemStack itemstack2 = furnaceItemStacks[2];

			if (itemstack2 == null) {
				furnaceItemStacks[2] = itemstack1.copy();
			} else if (itemstack2.getItem() == itemstack1.getItem()) {
				itemstack2.stackSize += itemstack1.stackSize;
			}

			if (itemstack.getItem() == Item.getItemFromBlock(Blocks.SPONGE) && itemstack.getMetadata() == 1 && !(furnaceItemStacks[1] == null) && furnaceItemStacks[1].getItem() == Items.BUCKET) {
				furnaceItemStacks[1] = new ItemStack(Items.WATER_BUCKET);
			}

			itemstack.stackSize--;
		}
	}

	public static int getItemBurnTime(ItemStack stack) {
		if (stack == null) {
			return 0;
		} else {
			Item item = stack.getItem();
			int meta = stack.getMetadata();
			if (item == Item.getItemFromBlock(NCBlocks.ingot_block)) {
				return (meta == 3 || meta == 4) ? 3200 : 0;
			} else if (item == NCItems.ingot || item == NCItems.dust) {
				return (meta == 3 || meta == 4) ? 320 : 0;
			} else if (item == NCItems.ingot_oxide || item == NCItems.dust_oxide) {
				return (meta == 0 || meta == 1) ? 480 : 0;
			}
		}
		return 0;
	}

	public static boolean isItemFuel(ItemStack stack) {
		return getItemBurnTime(stack) > 0;
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
	}

	public void openInventory(EntityPlayer player) {}

	public void closeInventory(EntityPlayer player) {}

	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (index == 2) {
			return false;
		} else if (index != 1) {
			return true;
		} else {
			ItemStack itemstack = furnaceItemStacks[1];
			return isItemFuel(stack) || SlotFuel.isBucket(stack) && itemstack.getItem() != Items.BUCKET;
		}
	}

	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.DOWN ? SLOTS_BOTTOM : (side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES);
	}

	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
		return isItemValidForSlot(index, stack);
	}

	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if (direction == EnumFacing.DOWN && index == 1) {
			Item item = stack.getItem();

			if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
				return false;
			}
		}

		return true;
	}
	
	public int getFieldCount() {
		return 4;
	}

	public int getField(int id) {
		switch (id) {
			case 0:
				return furnaceBurnTime;
			case 1:
				return currentItemBurnTime;
			case 2:
				return cookTime;
			case 3:
				return totalCookTime;
			default:
				return 0;
		}
	}

	public void setField(int id, int value) {
		switch (id) {
			case 0:
				furnaceBurnTime = value;
				break;
			case 1:
				currentItemBurnTime = value;
				break;
			case 2:
				cookTime = value;
				break;
			case 3:
				totalCookTime = value;
		}
	}

	public void clear() {
		for (int i = 0; i < furnaceItemStacks.length; ++i) {
			furnaceItemStacks[i] = null;
		}
	}
	
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(blockType.getLocalizedName());
	}

	net.minecraftforge.items.IItemHandler handlerTop = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.UP);
	net.minecraftforge.items.IItemHandler handlerBottom = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.DOWN);
	net.minecraftforge.items.IItemHandler handlerSide = new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.WEST);

	@SuppressWarnings("unchecked")
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
		if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == EnumFacing.DOWN) {
				return (T) handlerBottom;
			} else if (facing == EnumFacing.UP) {
				return (T) handlerTop;
			} else {
				return (T) handlerSide;
			}
		}
		return super.getCapability(capability, facing);
	}
}
