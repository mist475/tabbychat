package acs.tabbychat.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acs.tabbychat.core.ChatChannel;
//import acs.tabbychat.core.FilterTest;
import acs.tabbychat.core.GuiChatTC;
import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TCChatLine;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.gui.ITCSettingsGUI;
import acs.tabbychat.gui.context.ChatContextMenu;
import acs.tabbychat.gui.context.ContextCopy;
import acs.tabbychat.gui.context.ContextCut;
import acs.tabbychat.gui.context.ContextPaste;
import acs.tabbychat.settings.ChannelDelimEnum;
import acs.tabbychat.settings.ColorCodeEnum;
import acs.tabbychat.settings.FormatCodeEnum;
import acs.tabbychat.settings.NotificationSoundEnum;
import acs.tabbychat.settings.TimeStampEnum;
import acs.tabbychat.threads.BackgroundChatThread;

import com.google.common.collect.Lists;
//import com.mumfrey.liteloader.core.LiteLoader;

public class TabbyChatUtils {
	private static Calendar logDay = Calendar.getInstance();
	private static File logDir = new File(new File(Minecraft.getMinecraft().mcDataDir, "logs"), "TabbyChat");
	private static SimpleDateFormat logNameFormat = new SimpleDateFormat("'_'MM-dd-yyyy'.txt'");
	public final static String version = "@@VERSION@@";
	public final static String name = "TabbyChat";
	public final static String modid = "tabbychat";
	public static Logger log = LogManager.getLogger(name);
 
	public static void startup(){
		// check if forge is installed.
		try {
			Class.forName("net.minecraftforge.common.MinecraftForge");
			TabbyChat.forgePresent = true;
			log.info("MinecraftForge detected.  Will check for client-commands.");
		} catch (ClassNotFoundException e) {
			TabbyChat.forgePresent = false;
		}
		ChatContextMenu.addContext(ContextCut.class);
		ChatContextMenu.addContext(ContextCopy.class);
		ChatContextMenu.addContext(ContextPaste.class);
	}
	
	public static void chatGuiTick(Minecraft mc) {
		if (mc.currentScreen == null)
			return;
		if (!(mc.currentScreen instanceof GuiChat))
			return;
		if (mc.currentScreen.getClass() == GuiChatTC.class)
			return;

		String inputBuffer = "";
		try {
			int ind = 0;
			for (Field fields : GuiChat.class.getDeclaredFields()) {
				if (fields.getType() == String.class) {
					if (ind == 1) {
						fields.setAccessible(true);
						inputBuffer = (String) fields.get(mc.currentScreen);
						break;
					}
					ind++;
				}
			}
		} catch (Exception e) {
			TabbyChat.printException("Unable to display chat interface", e);
		}
		mc.displayGuiScreen(new GuiChatTC(inputBuffer));
	}
	
	public static ComponentList chatLinesToComponent(List<TCChatLine> lines) {
		ComponentList result = ComponentList.newInstance();
		for (TCChatLine line : lines) {
			result.add(line.getChatLineString());
		}
		return result;
	}
	

	public static ServerData getServerData() {
		Minecraft mc = Minecraft.getMinecraft();
		ServerData serverData = null;
		for (Field field : Minecraft.class.getDeclaredFields()) {
			if (field.getType() == ServerData.class) {
				field.setAccessible(true);
				try {
					serverData = (ServerData) field.get(mc);
				} catch (Exception e) {
					TabbyChat.printException("Unable to find server information", e);
				}
				break;
			}
		}
		return serverData;
	}

	public static File getServerDir() {
		String ip = getServerIp();
		if (ip.contains(":")) {
			ip = ip.replaceAll(":", "(") + ")";
		}
		return new File(ITCSettingsGUI.tabbyChatDir, ip);
	}

	/**
	 * Returns the IP of the current server.
	 */
	public static String getServerIp() {
		String ip;
		if (Minecraft.getMinecraft().isSingleplayer()) {
			ip = "singleplayer";
		} else if (getServerData() == null) {
			ip = "unknown";
		} else {
			ip = getServerData().serverIP;
		}
		return ip;
	}

	/**
	 * Returns the directory the the configs are stored.
	 */
	public static File getTabbyChatDir() {
		if (TabbyChat.liteLoaded) {
			return null;//new File(LiteLoader.getCommonConfigFolder(), "tabbychat");
		} else {
			return new File(new File(Minecraft.getMinecraft().mcDataDir, "config"), "tabbychat");
		}
	}

	public static void hookIntoChat(GuiNewChatTC _gnc) {
		if (Minecraft.getMinecraft().ingameGUI.getChatGUI().getClass() != GuiNewChatTC.class) {
			try {
				Class<GuiIngame> IngameGui = GuiIngame.class;
				Field persistantGuiField = IngameGui.getDeclaredFields()[6];
				persistantGuiField.setAccessible(true);
				persistantGuiField.set(Minecraft.getMinecraft().ingameGUI, _gnc);

				int tmp = 0;
				for (Field fields : GuiNewChat.class.getDeclaredFields()) {
					if (fields.getType() == List.class) {
						fields.setAccessible(true);
						if (tmp == 0) {
							_gnc.sentMessages = (List) fields.get(_gnc);
						} else if (tmp == 1) {
							_gnc.backupLines = (List) fields.get(_gnc);
						} else if (tmp == 2) {
							_gnc.chatLines = (List) fields.get(_gnc);
							break;
						}
						tmp++;
					}
				}
			} catch (Exception e) {
				TabbyChat.printException("Error loading chat hook.", e);
			}
		}
	}

	/**
	 * Logs chat.
	 */
	public static void logChat(String theChat, ChatChannel theChannel) {
		Calendar tmpcal = Calendar.getInstance();
		File fileDir;
		try {
			if (theChannel.getTitle().equals(null)) {
				theChannel = new ChatChannel("default");
			}
		} catch (NullPointerException e) {
			theChannel = new ChatChannel("default");
		}
		if (getServerIp() == "singleplayer") {
			IntegratedServer ms = Minecraft.getMinecraft().getIntegratedServer();
			String worldName = ms.getWorldName();
			fileDir = new File(new File(new File(logDir, "singleplayer"), worldName), theChannel.getTitle());

		} else {
			fileDir = new File(new File(logDir, getServerIp()),	theChannel.getTitle());
		}
		if (!fileDir.exists())
			fileDir.mkdirs();

		if (theChannel.getLogFile() == null
				|| tmpcal.get(Calendar.DAY_OF_YEAR) != logDay.get(Calendar.DAY_OF_YEAR)) {
			logDay = tmpcal;
			theChannel.setLogFile(new File(fileDir, theChannel.getTitle() + logNameFormat.format(logDay.getTime())));
		}

		if (!theChannel.getLogFile().exists()) {
			try {
				fileDir.mkdirs();
				theChannel.getLogFile().createNewFile();
			} catch (Exception e) {
				TabbyChat.printErr("Cannot create log file : '" + e.getLocalizedMessage() + "' : " + e.toString());
				return;
			}
		}

		try {
			FileOutputStream logStream = new FileOutputStream(theChannel.getLogFile(), true);
			PrintStream logPrint = new PrintStream(logStream);
			logPrint.println(theChat);
			logPrint.close();
		} catch (Exception e) {
			TabbyChat.printErr("Cannot write to log file : '"
					+ e.getLocalizedMessage() + "' : " + e.toString());
			return;
		}
	}

	public static Float median(float val1, float val2, float val3) {
		if (val1 < val2 && val1 < val3)
			return Math.min(val2, val3);
		else if (val1 > val2 && val1 > val3)
			return Math.max(val2, val3);
		else
			return val1;
	}

	public static ColorCodeEnum parseColor(Object _input) {
		if (_input == null)
			return null;
		String input = _input.toString();
		try {
			return ColorCodeEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static ChannelDelimEnum parseDelimiters(Object _input) {
		if (_input == null)
			return null;
		String input = _input.toString();
		try {
			return ChannelDelimEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static FormatCodeEnum parseFormat(Object _input) {
		if (_input == null)
			return null;
		String input = _input.toString();
		try {
			return FormatCodeEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static Integer parseInteger(String _input, int min, int max, int fallback) {
		Integer result;
		try {
			result = Integer.parseInt(_input);
			result = Math.max(min, result);
			result = Math.min(max, result);
		} catch (NumberFormatException e) {
			result = fallback;
		}
		return result;
	}

	public static int parseInteger(String _input) {
		NumberFormat formatter = NumberFormat.getInstance();
		boolean state = formatter.isParseIntegerOnly();
		formatter.setParseIntegerOnly(true);
		ParsePosition pos = new ParsePosition(0);
		int result = formatter.parse(_input, pos).intValue();
		formatter.setParseIntegerOnly(state);
		if (_input.length() == pos.getIndex())
			return result;
		else
			return -1;
	}

	public static NotificationSoundEnum parseSound(Object _input) {
		if (_input == null)
			return NotificationSoundEnum.ORB;
		String input = _input.toString();
		try {
			return NotificationSoundEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return NotificationSoundEnum.ORB;
		}
	}

	public static String parseString(Object _input) {
		if (_input == null)
			return " ";
		else
			return _input.toString();
	}

	public static TimeStampEnum parseTimestamp(Object _input) {
		if (_input == null)
			return null;
		String input = _input.toString();
		try {
			return TimeStampEnum.valueOf(input);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static List<TCChatLine> componentToChatLines(int stamp, ComponentList filtered,
			int id, boolean status) {
		List<TCChatLine> result = Lists.newArrayList();
		boolean first = true;
		for (IChatComponent split : filtered) {
			if (first) {
				result.add(new TCChatLine(stamp, split, id, status));
				first = false;
			} else
				result.add(new TCChatLine(stamp, new ChatComponentText(" ").appendSibling(split), id, status));
		}
		return result;
	}

	public static LinkedHashMap<String, ChatChannel> swapChannels(LinkedHashMap<String, ChatChannel> currentMap, int _left, int _right) {
		// Ensure ordering of 'indices' is 0<=_left<_right<=end
		if (_left == _right)
			return currentMap;
		else if (_left > _right) {
			int _tmp = _left;
			_left = _right;
			_right = _tmp;
		}
		if (_right >= currentMap.size())
			return currentMap;

		// Convert map to array for access by index
		String[] arrayCopy = new String[currentMap.size()];
		arrayCopy = currentMap.keySet().toArray(arrayCopy);
		// Swap array entries using passed index arguments
		String tmp = arrayCopy[_left];
		arrayCopy[_left] = arrayCopy[_right];
		arrayCopy[_right] = tmp;
		// Create new map and populate
		int n = arrayCopy.length;
		LinkedHashMap<String, ChatChannel> returnMap = new LinkedHashMap<String, ChatChannel>(n);
		for (int i = 0; i < n; i++) {
			returnMap.put(arrayCopy[i], currentMap.get(arrayCopy[i]));
		}
		return returnMap;
	}

	public static void writeLargeChat(String toSend) {
		List<String> actives = TabbyChat.getInstance().getActive();
		BackgroundChatThread sendProc;
		if (!TabbyChat.getInstance().enabled() || actives.size() != 1)
			sendProc = new BackgroundChatThread(toSend);
		else {
			ChatChannel active = TabbyChat.getInstance().channelMap.get(actives.get(0));
			String tabPrefix = active.cmdPrefix;
			boolean hiddenPrefix = active.hidePrefix;

			if (TabbyChat.advancedSettings.convertUnicodeText.getValue()) {
				toSend = convertUnicode(toSend);
			}

			if (tabPrefix != null && tabPrefix.length() > 0) {
				if (!hiddenPrefix)
					sendProc = new BackgroundChatThread(toSend, tabPrefix);
				else if (!toSend.startsWith("/"))
					sendProc = new BackgroundChatThread(tabPrefix + " " + toSend, tabPrefix);
				else
					sendProc = new BackgroundChatThread(toSend);
			} else
				sendProc = new BackgroundChatThread(toSend);
		}
		sendProc.start();
	}

	/**
	 * Converts strings to unicode. Essentially replaces \\uabcd with \uabcd.
	 * 
	 * @param chat
	 * @return
	 */
	public static String convertUnicode(String chat) {
		String newChat = "";
		for (String s : chat.split("\\\u0000")) {
			if (s.contains("u")) {
				try {
					newChat += StringEscapeUtils.unescapeJava(s);
				} catch (IllegalArgumentException e) {
					newChat += s;
				}
			} else
				newChat += s;
		}
		return newChat;
	}
}
