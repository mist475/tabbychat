package acs.tabbychat.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerProxy extends CommonProxy{
    @Override
    public void load(FMLInitializationEvent event) {
        Logger logger = LogManager.getLogger("tabbychat");
        logger.warn("tabbychat found on server side, tabbychat won't crash the server but it'll do nothing but waste resources");
    }
}