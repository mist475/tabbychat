package acs.tabbychat.core;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.util.TabbyChatUtils;

import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.RenderListener;
import com.mumfrey.liteloader.core.LiteLoader;

public class LiteModTabbyChat implements RenderListener {
	private static GuiNewChatTC gnc;
	private static Logger log = TabbyChatUtils.log;

	@Override
	public String getName() {
		return TabbyChatUtils.name;
	}

	@Override
	public String getVersion() {
		return TabbyChatUtils.version;
	}

	@Override
	public void init(File configPath) {
		TabbyChat.liteLoaded = true;
		
		String relativeConfig = "tabbychat";
		File liteConfigDir = new File(LiteLoader.getCommonConfigFolder(),
				relativeConfig);
		File mcConfigDir = new File(LiteLoader.getGameDirectory(),
				new StringBuilder().append("config").append(File.separatorChar)
						.append(relativeConfig).toString());

		// If forge old exist and liteloader configs don't, copy over.
		if (!liteConfigDir.exists() && mcConfigDir.exists()) {
			try {
				FileUtils.copyDirectory(mcConfigDir, liteConfigDir);
				log.info("Old configs found! Converting.");
			} catch (IOException e) {
				log.warn("Old configs found, but unable to convert.\n" + e);
			}
		}
		
		TabbyChatUtils.startup();
	}

	@Override
	public void upgradeSettings(String version, File configPath,
			File oldConfigPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRenderGui(GuiScreen currentScreen) {
		// TODO Auto-generated method stub

		TabbyChatUtils.chatGuiTick();
	}

	@Override
	public void onRenderWorld() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetupCameraTransform() {
		// TODO Auto-generated method stub
		
	}
}
