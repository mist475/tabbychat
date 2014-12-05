package acs.tabbychat.core;

import acs.tabbychat.util.TabbyChatUtils;

import com.mumfrey.liteloader.core.LiteLoader;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraft.client.Minecraft;

@Mod(name = TabbyChatUtils.name, modid = TabbyChatUtils.modid, version = TabbyChatUtils.version)
public class TabbyChatMod {

    @EventHandler
    public void load(FMLInitializationEvent event) {
        if (willBeLiteLoaded()) {
            TabbyChatUtils.log
                    .warn("LiteModTabbyChat detected and enabled.  Will use that instead.");
            return;
        }
        TabbyChatUtils.startup();
        FMLCommonHandler.instance().bus().register(this);
        TabbyChat.modLoaded = true;
    }

    @SubscribeEvent
    public void postLoad(ClientConnectedToServerEvent event) {
        if (!TabbyChat.liteLoaded)
            GuiNewChatTC.getInstance();
    }

    @SubscribeEvent
    public void onTick(RenderTickEvent event) {
        if (!event.phase.equals(Phase.START) && Minecraft.getMinecraft().theWorld != null) {
            onTickInGui(Minecraft.getMinecraft());
        }
    }

    private boolean onTickInGui(Minecraft minecraft) {
        TabbyChatUtils.chatGuiTick(minecraft);
        return true;
    }

    private boolean willBeLiteLoaded() {
        try {
            LiteLoader liteloader = LiteLoader.getInstance();
            if (liteloader != null)
                return liteloader.isModEnabled(TabbyChatUtils.name);
        } catch (NoClassDefFoundError e) {
        }
        return false;
    }
}
