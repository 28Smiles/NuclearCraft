package nc.container.generator;

import nc.container.SlotProcessorInput;
import nc.recipe.generator.FissionRecipes;
import nc.tile.generator.TileFissionController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerFissionController extends ContainerItemGenerator {
	
	protected int processTime;
	protected int processPower;
	protected int heat;
	protected int cooling;
	protected int efficiency;
	protected int rateMultiplier;
	protected int heatChange;

	public ContainerFissionController(EntityPlayer player, TileFissionController tileEntity) {
		super(tileEntity, FissionRecipes.instance());
		
		addSlotToContainer(new SlotProcessorInput(tileEntity, recipes, 0, 56, 35));
		
		addSlotToContainer(new SlotFurnaceOutput(player, tileEntity, 1, 116, 35));
		
		addSlotToContainer(new SlotFurnaceOutput(player, tileEntity, 2, -4095, -4095));
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(player.inventory, j + 9*i + 9, 8 + 18*j, 95 + 18*i));
			}
		}
		
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + 18*i, 153));
		}
	}
	
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		for (int i = 0; i < listeners.size(); i++) {
			IContainerListener icontainerlistener = (IContainerListener) listeners.get(i);

			icontainerlistener.sendProgressBarUpdate(this, 0, tile.getField(0) >> 16);
			icontainerlistener.sendProgressBarUpdate(this, 100, tile.getField(0));

			icontainerlistener.sendProgressBarUpdate(this, 1, tile.getField(1) >> 16);
			icontainerlistener.sendProgressBarUpdate(this, 101, tile.getField(1));

			icontainerlistener.sendProgressBarUpdate(this, 2, tile.getField(2) >> 16);
			icontainerlistener.sendProgressBarUpdate(this, 102, tile.getField(2));

			icontainerlistener.sendProgressBarUpdate(this, 3, tile.getField(3) >> 16);
			icontainerlistener.sendProgressBarUpdate(this, 103, tile.getField(3));

			icontainerlistener.sendProgressBarUpdate(this, 4, tile.getField(4) >> 16);
			icontainerlistener.sendProgressBarUpdate(this, 104, tile.getField(4));

			icontainerlistener.sendProgressBarUpdate(this, 5, tile.getField(5) >> 16);
			icontainerlistener.sendProgressBarUpdate(this, 105, tile.getField(5));

			icontainerlistener.sendProgressBarUpdate(this, 6, tile.getField(6) >> 16);
			icontainerlistener.sendProgressBarUpdate(this, 106, tile.getField(6));

			icontainerlistener.sendProgressBarUpdate(this, 8, tile.getField(8) >> 16);
			icontainerlistener.sendProgressBarUpdate(this, 108, tile.getField(8));

			icontainerlistener.sendProgressBarUpdate(this, 12, tile.getField(12) >> 16);
			icontainerlistener.sendProgressBarUpdate(this, 112, tile.getField(12));
			
			for (int j : new int[] {7, 9, 10, 11, 13, 14, 15, 16, 17, 18}) icontainerlistener.sendProgressBarUpdate(this, j, tile.getField(j));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 100) time = upcast(data);
		else if (id == 101) energy = upcast(data);
		else if (id == 102) processTime = upcast(data);
		else if (id == 103) processPower = upcast(data);
		else if (id == 104) heat = upcast(data);
		else if (id == 105) cooling = upcast(data);
		else if (id == 106) efficiency = upcast(data);
		else if (id == 108) rateMultiplier = upcast(data);
		else if (id == 112) heatChange = upcast(data);
		
		else if (id == 0) tile.setField(id, time | data << 16);
		else if (id == 1) tile.setField(id, energy | data << 16);
		else if (id == 2) tile.setField(id, processTime | data << 16);
		else if (id == 3) tile.setField(id, processPower | data << 16);
		else if (id == 4) tile.setField(id, heat | data << 16);
		else if (id == 5) tile.setField(id, cooling | data << 16);
		else if (id == 6) tile.setField(id, efficiency | data << 16);
		else if (id == 8) tile.setField(id, rateMultiplier | data << 16);
		else if (id == 12) tile.setField(id, heatChange | data << 16);
		
		else if (id == 7 || (id >= 9 && id <= 11) || (id >= 13 && id <= 18)) tile.setField(id, data);
	}
}
