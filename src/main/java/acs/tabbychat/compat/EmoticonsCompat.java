package acs.tabbychat.compat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import acs.tabbychat.api.IChatMouseExtension;
import acs.tabbychat.api.IChatUpdateExtension;

public class EmoticonsCompat implements IChatMouseExtension, IChatUpdateExtension {
	public static Object emoteObject = null;
	public static Class emoteButtonClass = null;
	private static Constructor emoteConstructor = null;
	private static Method emoteActionPerformed = null;
	private static Method emoteInitGui = null;
	private static Method emoteDrawScreen = null;
	public static int emoteOffsetX = 0;
	public static boolean present = true;
	/**
	 * 
	 */
	public void load() {
		if(present) {
			if(emoteConstructor == null || emoteActionPerformed == null || emoteInitGui == null || emoteDrawScreen == null || emoteButtonClass == null) {
				try {
					// Load new Emoticons object
					Class EmoticonsClass = Class.forName("mudbill.Emoticons");
					emoteButtonClass = Class.forName("mudbill.GuiSimpleButton");
					emoteConstructor = EmoticonsClass.getConstructor((Class[])null);
					emoteObject = emoteConstructor.newInstance((Object[])null);
					// Assign Emoticons actionPerformed Method
					Class[] cArgsAP = new Class[3];
					cArgsAP[0] = GuiButton.class;
					cArgsAP[1] = List.class;
					cArgsAP[2] = GuiTextField.class;
					emoteActionPerformed = EmoticonsClass.getDeclaredMethod("actionPerformed", cArgsAP);
					// Assign Emoticons initGui Method;
					Class[] cArgsIG = new Class[1];
					cArgsIG[0] = List.class;
					emoteInitGui = EmoticonsClass.getDeclaredMethod("initGui", cArgsIG);
					// Assign Emoticons drawScreen Method;
					Class[] cArgsDS = new Class[4];
					cArgsDS[0] = int.class;
					cArgsDS[1] = int.class;
					cArgsDS[2] = float.class;
					cArgsDS[3] = GuiScreen.class;
					emoteDrawScreen = EmoticonsClass.getDeclaredMethod("drawScreen", cArgsDS);
				} catch (Exception e) {
					present = false;
				}
			} else {
				try {
					emoteObject = emoteConstructor.newInstance((Object[])null);
				} catch (Exception e) {
					present = false;
				}
			}
		}
	}
	
	public boolean actionPerformed(GuiButton par1) {
		if(!present) return false;
		try {
			emoteActionPerformed.invoke(emoteObject, par1);
		} catch (Exception e) {
			present = false;
		}
		return false;
	}

	public void initGui(GuiScreen screen) {
		if(!present) return;
		Object[] args = new Object[1];
		try {
			emoteInitGui.invoke(emoteObject);
		} catch (Exception e) {
			present = false;
		}
	}

	@Override
	public void onGuiClosed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mouseClicked(int x, int y, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void handleMouseInput() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateScreen() {
		// TODO Auto-generated method stub
		
	}

}
