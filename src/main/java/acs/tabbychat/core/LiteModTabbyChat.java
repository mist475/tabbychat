package acs.tabbychat.core;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import acs.tabbychat.util.TabbyChatUtils;

import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;

public class LiteModTabbyChat implements Tickable, JoinGameListener {
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
	public void onJoinGame(INetHandler netHandler, S01PacketJoinGame joinGamePacket) {
		GuiNewChatTC.getInstance();
	}

	@Override
	public void onTick(Minecraft var1, float var2, boolean var3, boolean var4) {
		TabbyChatUtils.chatGuiTick(var1);
	}

	@Override
	public void init(File configPath) {
		TabbyChat.liteLoaded = true;
		String relativeConfig = "tabbychat";
		File liteConfigDir = new File(LiteLoader.getCommonConfigFolder(), relativeConfig);
		File mcConfigDir = new File(new File(LiteLoader.getGameDirectory(), "config"), relativeConfig);

		// If forge old exist and liteloader configs don't, copy over.
		if (!liteConfigDir.exists() && mcConfigDir.exists()) {
			try {
				FileUtils.copyDirectory(mcConfigDir, liteConfigDir);
				log.info("Old configs found! Converting.");
			} catch (IOException e) {
				log.warn("Old configs found, but unable to convert.", e);
			}
		}
		TabbyChatUtils.startup();
	}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {
		// TODO Auto-generated method stub

	}
}
