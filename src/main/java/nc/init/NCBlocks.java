package nc.init;

import nc.Global;
import nc.block.BlockIngot;
import nc.block.BlockOre;
import nc.block.BlockTransparent;
import nc.block.NCBlock;
import nc.block.fission.BlockCooler;
import nc.block.fission.BlockFission;
import nc.block.item.ItemBlockCooler;
import nc.block.item.ItemBlockFission;
import nc.block.item.ItemBlockIngot;
import nc.block.item.ItemBlockOre;
import nc.block.item.NCItemBlock;
import nc.block.tile.energy.generator.BlockAmericiumRTG;
import nc.block.tile.energy.generator.BlockCaliforniumRTG;
import nc.block.tile.energy.generator.BlockFissionController;
import nc.block.tile.energy.generator.BlockPlutoniumRTG;
import nc.block.tile.energy.generator.BlockSolarPanelBasic;
import nc.block.tile.energy.generator.BlockUraniumRTG;
import nc.block.tile.energy.processor.BlockAlloyFurnace;
import nc.block.tile.energy.processor.BlockDecayHastener;
import nc.block.tile.energy.processor.BlockFuelReprocessor;
import nc.block.tile.energy.processor.BlockIsotopeSeparator;
import nc.block.tile.energy.processor.BlockManufactory;
import nc.block.tile.energy.storage.BlockLithiumIonBatteryBasic;
import nc.block.tile.energy.storage.BlockVoltaicPileBasic;
import nc.block.tile.processor.BlockNuclearFurnace;
import nc.config.NCConfig;
import nc.handler.EnumHandler.CoolerTypes;
import nc.handler.EnumHandler.FissionBlockTypes;
import nc.handler.EnumHandler.IngotTypes;
import nc.handler.EnumHandler.OreTypes;
import nc.proxy.CommonProxy;
import nc.util.NCUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class NCBlocks {
	
	public static Block ore;
	public static Block ingot_block;
	
	public static Block fission_block;
	public static Block cell_block;
	public static Block cooler;
	
	public static Block block_depleted_uranium;
	
	public static Block nuclear_furnace_idle;
	public static Block nuclear_furnace_active;
	public static Block manufactory_idle;
	public static Block manufactory_active;
	public static Block isotope_separator_idle;
	public static Block isotope_separator_active;
	public static Block decay_hastener_idle;
	public static Block decay_hastener_active;
	public static Block fuel_reprocessor_idle;
	public static Block fuel_reprocessor_active;
	public static Block alloy_furnace_idle;
	public static Block alloy_furnace_active;
	
	public static Block fission_controller_idle;
	public static Block fission_controller_active;
	
	public static Block rtg_uranium;
	public static Block rtg_plutonium;
	public static Block rtg_americium;
	public static Block rtg_californium;
	
	public static Block solar_panel_basic;
	
	public static Block voltaic_pile_basic;
	public static Block lithium_ion_battery_basic;
	
	public static void init() {
		ore = new BlockOre("ore", "ore").setCreativeTab(CommonProxy.TAB_BASE_BLOCK_MATERIALS);
		ingot_block = new BlockIngot("ingot_block", "ingot_block").setCreativeTab(CommonProxy.TAB_BASE_BLOCK_MATERIALS);
		
		fission_block = new BlockFission("fission_block", "fission_block").setCreativeTab(CommonProxy.TAB_FISSION_BLOCKS);
		cell_block = new BlockTransparent("cell_block", "cell_block", Material.IRON, false).setCreativeTab(CommonProxy.TAB_FISSION_BLOCKS);
		cooler = new BlockCooler("cooler", "cooler").setCreativeTab(CommonProxy.TAB_FISSION_BLOCKS);
		
		block_depleted_uranium = new NCBlock("block_depleted_uranium", "block_depleted_uranium", Material.IRON).setCreativeTab(CommonProxy.TAB_BASE_BLOCK_MATERIALS);
		
		nuclear_furnace_idle = new BlockNuclearFurnace("nuclear_furnace_idle", "nuclear_furnace_idle", false, 0);
		nuclear_furnace_active = new BlockNuclearFurnace("nuclear_furnace_active", "nuclear_furnace_active", true, 0);
		manufactory_idle = new BlockManufactory("manufactory_idle", "manufactory_idle", false, 1);
		manufactory_active = new BlockManufactory("manufactory_active", "manufactory_active", true, 1);
		isotope_separator_idle = new BlockIsotopeSeparator("isotope_separator_idle", "isotope_separator_idle", false, 2);
		isotope_separator_active = new BlockIsotopeSeparator("isotope_separator_active", "isotope_separator_active", true, 2);
		decay_hastener_idle = new BlockDecayHastener("decay_hastener_idle", "decay_hastener_idle", false, 3);
		decay_hastener_active = new BlockDecayHastener("decay_hastener_active", "decay_hastener_active", true, 3);
		fuel_reprocessor_idle = new BlockFuelReprocessor("fuel_reprocessor_idle", "fuel_reprocessor_idle", false, 4);
		fuel_reprocessor_active = new BlockFuelReprocessor("fuel_reprocessor_active", "fuel_reprocessor_active", true, 4);
		alloy_furnace_idle = new BlockAlloyFurnace("alloy_furnace_idle", "alloy_furnace_idle", false, 5);
		alloy_furnace_active = new BlockAlloyFurnace("alloy_furnace_active", "alloy_furnace_active", true, 5);
		
		fission_controller_idle = new BlockFissionController("fission_controller_idle", "fission_controller_idle", false, 100);
		fission_controller_active = new BlockFissionController("fission_controller_active", "fission_controller_active", true, 100);
		
		rtg_uranium = new BlockUraniumRTG("rtg_uranium", "rtg_uranium");
		rtg_plutonium = new BlockPlutoniumRTG("rtg_plutonium", "rtg_plutonium");
		rtg_americium = new BlockAmericiumRTG("rtg_americium", "rtg_americium");
		rtg_californium = new BlockCaliforniumRTG("rtg_californium", "rtg_californium");
		
		solar_panel_basic = new BlockSolarPanelBasic("solar_panel_basic", "solar_panel_basic");
		
		voltaic_pile_basic = new BlockVoltaicPileBasic("voltaic_pile_basic", "voltaic_pile_basic");
		lithium_ion_battery_basic = new BlockLithiumIonBatteryBasic("lithium_ion_battery_basic", "lithium_ion_battery_basic");
	}
	
	public static void register() {
		registerBlock(ore, new ItemBlockOre(ore));
		registerBlock(ingot_block, new ItemBlockIngot(ingot_block));
		
		registerBlock(fission_block, new ItemBlockFission(fission_block));
		registerBlock(cell_block, 11);
		registerBlock(cooler, new ItemBlockCooler(cooler));
		
		registerBlock(block_depleted_uranium);
		
		registerBlock(nuclear_furnace_idle, 2);
		registerBlock(nuclear_furnace_active);
		registerBlock(manufactory_idle, 2);
		registerBlock(manufactory_active);
		registerBlock(isotope_separator_idle, 2);
		registerBlock(isotope_separator_active);
		registerBlock(decay_hastener_idle, 2);
		registerBlock(decay_hastener_active);
		registerBlock(fuel_reprocessor_idle, 2);
		registerBlock(fuel_reprocessor_active);
		registerBlock(alloy_furnace_idle, 2);
		registerBlock(alloy_furnace_active);
		
		registerBlock(fission_controller_idle, 9);
		registerBlock(fission_controller_active);
		
		registerBlock(rtg_uranium, I18n.translateToLocalFormatted("tile.rtg.des0") + " " + NCConfig.rtg_power[0] + " " + I18n.translateToLocalFormatted("tile.rtg.des1"));
		registerBlock(rtg_plutonium, I18n.translateToLocalFormatted("tile.rtg.des0") + " " + NCConfig.rtg_power[1] + " " + I18n.translateToLocalFormatted("tile.rtg.des1"));
		registerBlock(rtg_americium, I18n.translateToLocalFormatted("tile.rtg.des0") + " " + NCConfig.rtg_power[2] + " " + I18n.translateToLocalFormatted("tile.rtg.des1"));
		registerBlock(rtg_californium, I18n.translateToLocalFormatted("tile.rtg.des0") + " " + NCConfig.rtg_power[3] + " " + I18n.translateToLocalFormatted("tile.rtg.des1"));
		
		registerBlock(solar_panel_basic, I18n.translateToLocalFormatted("tile.solar_panel.des0") + " " + NCConfig.solar_power[0] + " " + I18n.translateToLocalFormatted("tile.solar_panel.des1"));
		
		registerBlock(voltaic_pile_basic, I18n.translateToLocalFormatted("tile.energy_storage.des0") + " " + NCConfig.battery_capacity[0]/1000 + " " + I18n.translateToLocalFormatted("tile.energy_storage.des1"), I18n.translateToLocalFormatted("tile.energy_storage.des2"), I18n.translateToLocalFormatted("tile.energy_storage.des3"), I18n.translateToLocalFormatted("tile.energy_storage.des4"));
		registerBlock(lithium_ion_battery_basic, I18n.translateToLocalFormatted("tile.energy_storage.des0") + " " + NCConfig.battery_capacity[1]/1000 + " " + I18n.translateToLocalFormatted("tile.energy_storage.des1"), I18n.translateToLocalFormatted("tile.energy_storage.des2"), I18n.translateToLocalFormatted("tile.energy_storage.des3"), I18n.translateToLocalFormatted("tile.energy_storage.des4"));
	}
	
	public static void registerRenders() {
		for (int i = 0; i < OreTypes.values().length; i++) {
			registerRender(ore, i, "ore_" + OreTypes.values()[i].getName());
		}
		
		for (int i = 0; i < IngotTypes.values().length; i++) {
			registerRender(ingot_block, i, "ingot_block_" + IngotTypes.values()[i].getName());
		}
		
		for (int i = 0; i < FissionBlockTypes.values().length; i++) {
			registerRender(fission_block, i, "fission_block_" + FissionBlockTypes.values()[i].getName());
		}
		
		registerRender(cell_block);
		
		for (int i = 0; i < CoolerTypes.values().length; i++) {
			registerRender(cooler, i, "cooler_" + CoolerTypes.values()[i].getName());
		}
		
		registerRender(block_depleted_uranium);
		
		registerRender(nuclear_furnace_idle);
		registerRender(nuclear_furnace_active);
		registerRender(manufactory_idle);
		registerRender(manufactory_active);
		registerRender(isotope_separator_idle);
		registerRender(isotope_separator_active);
		registerRender(decay_hastener_idle);
		registerRender(decay_hastener_active);
		registerRender(fuel_reprocessor_idle);
		registerRender(fuel_reprocessor_active);
		registerRender(alloy_furnace_idle);
		registerRender(alloy_furnace_active);
		
		registerRender(fission_controller_idle);
		registerRender(fission_controller_active);
		
		registerRender(rtg_uranium);
		registerRender(rtg_plutonium);
		registerRender(rtg_americium);
		registerRender(rtg_californium);
		
		registerRender(solar_panel_basic);
		
		registerRender(voltaic_pile_basic);
		registerRender(lithium_ion_battery_basic);
	}
	
	public static void registerBlock(Block block, Object... info) {
		GameRegistry.register(block);
		GameRegistry.register(new NCItemBlock(block, info).setRegistryName(block.getRegistryName()));
		NCUtils.getLogger().info("Registered block " + block.getUnlocalizedName().substring(5));
	}
	
	public static void registerBlock(Block block, ItemBlock itemBlock) {
		GameRegistry.register(block);
		GameRegistry.register(itemBlock.setRegistryName(block.getRegistryName()));
		NCUtils.getLogger().info("Registered block " + block.getUnlocalizedName().substring(5));
	}
	
	public static void registerRender(Block block) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(new ResourceLocation(Global.MOD_ID, block.getUnlocalizedName().substring(5)), "inventory"));
		NCUtils.getLogger().info("Registered render for block " + block.getUnlocalizedName().substring(5));
	}
	
	public static void registerRender(Block block, int meta, String fileName) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(new ResourceLocation(Global.MOD_ID, fileName), "inventory"));
		NCUtils.getLogger().info("Registered render for block " + fileName);
	}
}
