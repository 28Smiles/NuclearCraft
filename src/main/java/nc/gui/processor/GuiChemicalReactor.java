package nc.gui.processor;

import nc.container.processor.ContainerChemicalReactor;
import nc.gui.GuiFluidRenderer;
import nc.gui.GuiItemRenderer;
import nc.init.NCItems;
import nc.network.PacketGetFluidInTank;
import nc.network.PacketHandler;
import nc.tile.processor.TileEnergyFluidProcessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidStack;

public class GuiChemicalReactor extends GuiEnergyFluidProcessor {
	
	public static FluidStack fluid0, fluid1, fluid2, fluid3 = null;

	public GuiChemicalReactor(EntityPlayer player, TileEnergyFluidProcessor tile) {
		super("chemical_reactor", player, new ContainerChemicalReactor(player, tile));
		this.tile = tile;
		xSize = 176;
		ySize = 166;
	}
	
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		fontRendererObj.drawString(tile.storage.getEnergyStored() + " RF", 28, ySize - 94, 4210752);
		
		GuiItemRenderer itemRenderer = new GuiItemRenderer(132, ySize - 102, 0.5F, NCItems.upgrade, 0);
		itemRenderer.draw();
	}
	
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		
		double e = Math.round(((double) tile.storage.getEnergyStored()) / ((double) tile.storage.getMaxEnergyStored()) * 74);
		drawTexturedModalRect(guiLeft + 8, guiTop + 6 + 74 - (int) e, 176, 90 + 74 - (int) e, 16, (int) e);
		
		int k = getCookProgressScaled(37);
		drawTexturedModalRect(guiLeft + 70, guiTop + 35, 176, 3, k, 18);
		
		if (tick == 0) {
			PacketHandler.INSTANCE.sendToServer(new PacketGetFluidInTank(tile.getPos(), 0, "nc.gui.processor.GuiChemicalReactor", "fluid0"));
			PacketHandler.INSTANCE.sendToServer(new PacketGetFluidInTank(tile.getPos(), 1, "nc.gui.processor.GuiChemicalReactor", "fluid1"));
			PacketHandler.INSTANCE.sendToServer(new PacketGetFluidInTank(tile.getPos(), 2, "nc.gui.processor.GuiChemicalReactor", "fluid2"));
			PacketHandler.INSTANCE.sendToServer(new PacketGetFluidInTank(tile.getPos(), 3, "nc.gui.processor.GuiChemicalReactor", "fluid3"));
		}
		
		GuiFluidRenderer.renderGuiTank(fluid0, tile.tanks[0].getCapacity(), guiLeft + 32, guiTop + 35, zLevel, 16, 16);
		GuiFluidRenderer.renderGuiTank(fluid1, tile.tanks[1].getCapacity(), guiLeft + 52, guiTop + 35, zLevel, 16, 16);
		GuiFluidRenderer.renderGuiTank(fluid2, tile.tanks[2].getCapacity(), guiLeft + 108, guiTop + 31, zLevel, 24, 24);
		GuiFluidRenderer.renderGuiTank(fluid3, tile.tanks[3].getCapacity(), guiLeft + 136, guiTop + 31, zLevel, 24, 24);
	}
}