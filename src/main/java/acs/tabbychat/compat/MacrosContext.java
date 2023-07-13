package acs.tabbychat.compat;

import acs.tabbychat.gui.context.ChatContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class MacrosContext extends ChatContext {

    private int id; // 0 = execute, 1 = edit, 2 = design

    public MacrosContext(int name) {
        this.id = name;
        switch (name) {
            case 0 -> this.displayString = "Execute Macro";
            case 1 -> this.displayString = "Edit Macro";
            case 2 -> {
                try {
                    Class<?> loc = Class
                        .forName("net.eq2online.macros.compatibility.LocalisationProvider");
                    Method locStr = loc.getMethod("getLocalisedString", String.class);
                    this.displayString = "\u0A7e" + locStr.invoke(null, "tooltip.guiedit");
                    // this.setIconUV(26, 16);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClicked() {
        try {
            Class<?> guiChatAdapter = Class.forName("net.eq2online.macros.gui.ext.GuiChatAdapter");
            Class<?> guiControl = Class
                .forName("net.eq2online.macros.gui.designable.DesignableGuiControl");
            Class<?> guiMacroEdit = Class.forName("net.eq2online.macros.gui.screens.GuiMacroEdit");
            Class<?> guiDesigner = Class.forName("net.eq2online.macros.gui.screens.GuiDesigner");
            Constructor<?> editConst = guiMacroEdit.getConstructor(int.class, GuiScreen.class);
            Constructor<?> designerConst = guiDesigner.getConstructor(String.class,
                                                                      GuiScreen.class, boolean.class);
            Method bindable = guiControl.getMethod("getWidgetIsBindable");
            Method playMacro = guiChatAdapter.getDeclaredMethod("playMacro");
            playMacro.setAccessible(true);
            Field controlId = guiControl.getField("id");
            Object control = MacroKeybindCompat.getControl();
            boolean isBindable = false;
            if (control != null)
                isBindable = (Boolean) bindable.invoke(control, new Object[0]);
            switch (id) {
                case 0 -> {
                    if (isBindable) {
                        playMacro.invoke(MacroKeybindCompat.getChatHook());
                    }
                }
                case 1 -> {
                    if (isBindable) {
                        int id = controlId.getInt(control);
                        GuiScreen screen = (GuiScreen) editConst.newInstance(id,
                                                                             Minecraft.getMinecraft().currentScreen);
                        Minecraft.getMinecraft().displayGuiScreen(screen);
                    }
                }
                case 2 -> {
                    GuiScreen screen = (GuiScreen) designerConst.newInstance("inchat",
                                                                             Minecraft.getMinecraft().currentScreen, true);
                    Minecraft.getMinecraft().displayGuiScreen(screen);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDisplayString() {
        return this.displayString;
    }

    @Override
    public ResourceLocation getDisplayIcon() {
        return null;
    }

    @Override
    public List<ChatContext> getChildren() {
        return null;
    }

    @Override
    public boolean isPositionValid(int x, int y) {
        return true;
    }

    @Override
    public Behavior getDisabledBehavior() {
        return Behavior.HIDE;
    }

}
