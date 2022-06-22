package acs.tabbychat.proxy;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;

public class ClientProxy extends CommonProxy{
    @Override
    public void load(FMLInitializationEvent event) {
        TabbyChatUtils.startup();
        FMLCommonHandler.instance().bus().register(this);
        TabbyChat.modLoaded = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (!event.phase.equals(TickEvent.Phase.START) && Minecraft.getMinecraft().theWorld != null) {
            onTickInGui(Minecraft.getMinecraft());
        }
    }

    private boolean onTickInGui(Minecraft minecraft) {
        TabbyChatUtils.chatGuiTick(minecraft);
        return true;
    }
}
