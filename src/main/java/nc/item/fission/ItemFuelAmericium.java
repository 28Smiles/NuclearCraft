package nc.item.fission;

import nc.Global;
import nc.handler.EnumHandler.AmericiumFuelTypes;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class ItemFuelAmericium extends Item {

	public ItemFuelAmericium(String unlocalizedName, String registryName) {
		setUnlocalizedName(unlocalizedName);
		setRegistryName(new ResourceLocation(Global.MOD_ID, registryName));
		setHasSubtypes(true);
	}

	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		for (int i = 0; i < AmericiumFuelTypes.values().length; i++) {
			items.add(new ItemStack(this, 1, i));
		}
	}
	
	public String getUnlocalizedName(ItemStack stack) {
		for (int i = 0; i < AmericiumFuelTypes.values().length; i++) {
			if (stack.getItemDamage() == i) {
				return getUnlocalizedName() + "." + AmericiumFuelTypes.values()[i].getName();
			} else {
				continue;
			}
		}
		return this.getUnlocalizedName() + "." + AmericiumFuelTypes.values()[0].getName();
	}
}
