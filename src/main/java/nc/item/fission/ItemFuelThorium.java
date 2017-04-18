package nc.item.fission;

import java.util.List;

import nc.Global;
import nc.handler.EnumHandler.ThoriumFuelTypes;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemFuelThorium extends Item {

	public ItemFuelThorium(String unlocalizedName, String registryName) {
		setUnlocalizedName(unlocalizedName);
		setRegistryName(new ResourceLocation(Global.MOD_ID, registryName));
		setHasSubtypes(true);
	}

	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
		for (int i = 0; i < ThoriumFuelTypes.values().length; i++) {
			items.add(new ItemStack(item, 1, i));
		}
	}
	
	public String getUnlocalizedName(ItemStack stack) {
		for (int i = 0; i < ThoriumFuelTypes.values().length; i++) {
			if (stack.getItemDamage() == i) {
				return getUnlocalizedName() + "." + ThoriumFuelTypes.values()[i].getName();
			} else {
				continue;
			}
		}
		return this.getUnlocalizedName() + "." + ThoriumFuelTypes.values()[0].getName();
	}
}
