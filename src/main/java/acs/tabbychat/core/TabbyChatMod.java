package acs.tabbychat.core;

import java.io.File;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import acs.tabbychat.util.TabbyChatUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

@Mod(name = TabbyChatUtils.name, modid = TabbyChatUtils.modid, version = TabbyChatUtils.version)
public class TabbyChatMod{
	
	@EventHandler
	public void load(FMLInitializationEvent event){
		if(willBeLiteLoaded()){
			TabbyChatUtils.log.warn("LiteLoader version detected.  Will use that instead.");
			return;
		}
		TabbyChatUtils.startup();
		FMLCommonHandler.instance().bus().register(this);
		TabbyChat.modLoaded = true;
	}
	
	@EventHandler
	public void postLoad(FMLServerStartedEvent event){
		if(!TabbyChat.liteLoaded)
			GuiNewChatTC.getInstance();
	}
	
	@SubscribeEvent
	public void onTick(RenderTickEvent event){
		if(!event.phase.equals(Phase.START) && Minecraft.getMinecraft().theWorld != null){
			onTickInGui(Minecraft.getMinecraft());
		}
	}

	private boolean onTickInGui(Minecraft minecraft) {
		TabbyChatUtils.chatGuiTick(minecraft);
		return true;
	}
	
	private boolean willBeLiteLoaded(){
		try{
			Class.forName("acs.tabbychat.core.LiteModTabbyChat");
			Class.forName("com.mumfrey.liteloader.core.LiteLoader");
			return true;
		}catch(Exception e){
		}
		return false;
	}
}
