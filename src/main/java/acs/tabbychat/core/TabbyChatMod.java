package acs.tabbychat.core;

import acs.tabbychat.proxy.CommonProxy;
import acs.tabbychat.util.TabbyChatUtils;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(name = TabbyChatUtils.name, modid = TabbyChatUtils.modid, version = TabbyChatUtils.version)
public class TabbyChatMod {

    @SidedProxy(serverSide = "acs.tabbychat.proxy.ServerProxy", clientSide = "acs.tabbychat.proxy.ClientProxy")
    public static CommonProxy proxy;
    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.load(event);
    }
}
