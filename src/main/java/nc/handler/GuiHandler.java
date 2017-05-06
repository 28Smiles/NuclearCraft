package nc.handler;

import nc.container.generator.ContainerFissionController;
import nc.container.generator.ContainerFusionCore;
import nc.container.processor.ContainerAlloyFurnace;
import nc.container.processor.ContainerDecayHastener;
import nc.container.processor.ContainerElectrolyser;
import nc.container.processor.ContainerFuelReprocessor;
import nc.container.processor.ContainerInfuser;
import nc.container.processor.ContainerIrradiator;
import nc.container.processor.ContainerIsotopeSeparator;
import nc.container.processor.ContainerManufactory;
import nc.container.processor.ContainerMelter;
import nc.container.processor.ContainerNuclearFurnace;
import nc.container.processor.ContainerSupercooler;
import nc.gui.generator.GuiFissionController;
import nc.gui.generator.GuiFusionCore;
import nc.gui.processor.GuiAlloyFurnace;
import nc.gui.processor.GuiDecayHastener;
import nc.gui.processor.GuiElectrolyser;
import nc.gui.processor.GuiFuelReprocessor;
import nc.gui.processor.GuiInfuser;
import nc.gui.processor.GuiIrradiator;
import nc.gui.processor.GuiIsotopeSeparator;
import nc.gui.processor.GuiManufactory;
import nc.gui.processor.GuiMelter;
import nc.gui.processor.GuiNuclearFurnace;
import nc.gui.processor.GuiSupercooler;
import nc.tile.generator.TileFissionController;
import nc.tile.generator.TileFusionCore;
import nc.tile.processor.Processors.TileAlloyFurnace;
import nc.tile.processor.Processors.TileDecayHastener;
import nc.tile.processor.Processors.TileElectrolyser;
import nc.tile.processor.Processors.TileFuelReprocessor;
import nc.tile.processor.Processors.TileInfuser;
import nc.tile.processor.Processors.TileIrradiator;
import nc.tile.processor.Processors.TileIsotopeSeparator;
import nc.tile.processor.Processors.TileManufactory;
import nc.tile.processor.Processors.TileMelter;
import nc.tile.processor.Processors.TileSupercooler;
import nc.tile.processor.TileNuclearFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		IInventory entity = (IInventory) world.getTileEntity(new BlockPos(x, y, z));
		
		if (entity != null) {
			switch(ID) {
			case 0:
				if (entity instanceof TileNuclearFurnace) return new ContainerNuclearFurnace(player, entity);
			case 1:
				if (entity instanceof TileManufactory) return new ContainerManufactory(player, (TileManufactory) entity);
			case 2:
				if (entity instanceof TileIsotopeSeparator) return new ContainerIsotopeSeparator(player, (TileIsotopeSeparator) entity);
			case 3:
				if (entity instanceof TileDecayHastener) return new ContainerDecayHastener(player, (TileDecayHastener) entity);
			case 4:
				if (entity instanceof TileFuelReprocessor) return new ContainerFuelReprocessor(player, (TileFuelReprocessor) entity);
			case 5:
				if (entity instanceof TileAlloyFurnace) return new ContainerAlloyFurnace(player, (TileAlloyFurnace) entity);
			case 6:
				if (entity instanceof TileInfuser) return new ContainerInfuser(player, (TileInfuser) entity);
			case 7:
				if (entity instanceof TileMelter) return new ContainerMelter(player, (TileMelter) entity);
			case 8:
				if (entity instanceof TileSupercooler) return new ContainerSupercooler(player, (TileSupercooler) entity);
			case 9:
				if (entity instanceof TileElectrolyser) return new ContainerElectrolyser(player, (TileElectrolyser) entity);
			case 10:
				if (entity instanceof TileIrradiator) return new ContainerIrradiator(player, (TileIrradiator) entity);
			case 100:
				if (entity instanceof TileFissionController) return new ContainerFissionController(player, (TileFissionController) entity);
			case 101:
				if (entity instanceof TileFusionCore) return new ContainerFusionCore(player, (TileFusionCore) entity);
			}
		}
		
		return null;
	}
	
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		IInventory entity = (IInventory) world.getTileEntity(new BlockPos(x, y, z));
		
		if (entity != null) {
			switch(ID) {
			case 0:
				if (entity instanceof TileNuclearFurnace) return new GuiNuclearFurnace(player, entity);
			case 1:
				if (entity instanceof TileManufactory) return new GuiManufactory(player, (TileManufactory) entity);
			case 2:
				if (entity instanceof TileIsotopeSeparator) return new GuiIsotopeSeparator(player, (TileIsotopeSeparator) entity);
			case 3:
				if (entity instanceof TileDecayHastener) return new GuiDecayHastener(player, (TileDecayHastener) entity);
			case 4:
				if (entity instanceof TileFuelReprocessor) return new GuiFuelReprocessor(player, (TileFuelReprocessor) entity);
			case 5:
				if (entity instanceof TileAlloyFurnace) return new GuiAlloyFurnace(player, (TileAlloyFurnace) entity);
			case 6:
				if (entity instanceof TileInfuser) return new GuiInfuser(player, (TileInfuser) entity);
			case 7:
				if (entity instanceof TileMelter) return new GuiMelter(player, (TileMelter) entity);
			case 8:
				if (entity instanceof TileSupercooler) return new GuiSupercooler(player, (TileSupercooler) entity);
			case 9:
				if (entity instanceof TileElectrolyser) return new GuiElectrolyser(player, (TileElectrolyser) entity);
			case 10:
				if (entity instanceof TileIrradiator) return new GuiIrradiator(player, (TileIrradiator) entity);
			case 100:
				if (entity instanceof TileFissionController) return new GuiFissionController(player, (TileFissionController) entity);
			case 101:
				if (entity instanceof TileFusionCore) return new GuiFusionCore(player, (TileFusionCore) entity);
			}
		}
		
		return null;
	}

}
