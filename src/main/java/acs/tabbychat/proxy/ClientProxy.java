package acs.tabbychat.proxy;

import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public class ClientProxy extends CommonProxy{
    @Override
    public void load(FMLInitializationEvent event) {
        TabbyChatUtils.startup();
        FMLCommonHandler.instance().bus().register(this);
        //needed for early loading
        MinecraftForge.EVENT_BUS.register(this);
        TabbyChat.modLoaded = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (!event.phase.equals(TickEvent.Phase.START) && Minecraft.getMinecraft().theWorld != null) {
            onTickInGui(Minecraft.getMinecraft());
        }
    }

    /**
     * Ensures tc gets loaded before logging in, ensuring no messages are lost in the time between joining a world and opening the chat window
     * @param event {@link WorldEvent.Load} event
     */
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        System.out.println("World Load Event should load but no debug?");
        //Should wake up tabbychat earlier, resulting in no messages being lost due to tc only activating once you open chat instead of on world
        GuiNewChatTC wakeUp = GuiNewChatTC.getInstance();
    }
    private boolean onTickInGui(Minecraft minecraft) {
        TabbyChatUtils.chatGuiTick(minecraft);
        return true;
    }
}
