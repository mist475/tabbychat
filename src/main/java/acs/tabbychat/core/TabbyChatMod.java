package acs.tabbychat.core;

import net.minecraft.client.Minecraft;
import acs.tabbychat.util.TabbyChatUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

@Mod(name = TabbyChatUtils.name, modid = TabbyChatUtils.modid, version = TabbyChatUtils.version)
public class TabbyChatMod{
	private static GuiNewChatTC gnc;
	
	@EventHandler
	public void load(FMLPostInitializationEvent event){
		FMLCommonHandler.instance().bus().register(this);
	}
	
	public void clientChat(String var1){
		if(gnc == null){
			gnc = GuiNewChatTC.getInstance();
			gnc.tc.modLoaded = true;
		}
	}
	
	@SubscribeEvent
	public void onTick(RenderTickEvent event){
		if(!event.phase.equals(Phase.START) && Minecraft.getMinecraft().theWorld != null){
			onTickInGui(Minecraft.getMinecraft());
		}
	}

	public boolean onTickInGui(Minecraft minecraft) {
		TabbyChatUtils.chatGuiTick(minecraft);
		return true;
	}
}
