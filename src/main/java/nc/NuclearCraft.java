package nc;

import nc.config.NCConfig;
import nc.handler.GuiHandler;
import nc.proxy.CommonProxy;
import nc.util.NCUtils;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = Global.MOD_ID, name = Global.MOD_NAME, version = Global.VERSION, guiFactory = Global.GUI_FACTORY)
public class NuclearCraft {
	
	static {
		FluidRegistry.enableUniversalBucket();
	}
	
	@Instance(Global.MOD_ID)
	public static NuclearCraft instance;
	
	@SidedProxy(clientSide = Global.NC_CLIENT_PROXY, serverSide = Global.NC_COMMON_PROXY)
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent preEvent) {
		NCUtils.getLogger().info("Pre Initializing...");
		NCConfig.preInit();
		proxy.preInit(preEvent);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		NCUtils.getLogger().info("Initializing...");
		proxy.init(event);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent postEvent) {
		NCUtils.getLogger().info("Post Initializing...");
		proxy.postInit(postEvent);
	}
}
