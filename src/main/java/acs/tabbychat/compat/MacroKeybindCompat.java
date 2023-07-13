package acs.tabbychat.compat;

import acs.tabbychat.api.IChatMouseExtension;
import acs.tabbychat.api.IChatRenderExtension;
import acs.tabbychat.api.IChatUpdateExtension;
import acs.tabbychat.gui.context.ChatContextMenu;
import acs.tabbychat.gui.context.ContextDummy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MacroKeybindCompat implements IChatMouseExtension, IChatUpdateExtension,
    IChatRenderExtension {
    public static final ResourceLocation ICONS_MAIN = new ResourceLocation("macros",
                                                                           "textures/gui/macrosGuiMain.png");
    public static boolean present = true;
    private static Object inChatLayout = null;
    private static Object inChatGUI = null;
    private static Object btnGui = null;
    private static Object dropDownMenu = null;
    private static Class<?> guiCustomGui = null;
    private static Constructor<?> createDesignerScreen = null;
    private static Constructor<?> macroEdit = null;
    private static Constructor<?> macroDesign = null;
    private static Method draw = null;
    private static Method controlClicked = null;
    private static Method drawBtnGui = null;
    private static Method layoutTick = null;
    private static Method drawDropDown = null;
    private static Method dropDownSize = null;
    private static Method mousePressed = null;
    private static Method onControlClicked = null;
    private static Method displayScreen = null;
    private static Method coreInstance = null;
    private static Method mkgetBoundLayout;
    private static Field menuLocation;
    private static Field dropDownVisible;
    private static Field clickedControl;
    private static Field boundingBox;
    private static Field chatGuiHook;
    private static boolean hovered = false;

    private GuiScreen screen;

    public static Object getControl() {
        try {
            return clickedControl.get(inChatGUI);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getChatHook() {
        try {
            return chatGuiHook.get(coreInstance.invoke(null));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void load() {
        if (present) {
            if (inChatLayout == null || inChatGUI == null || btnGui == null || dropDownMenu == null
                || guiCustomGui == null || createDesignerScreen == null || draw == null
                || controlClicked == null || drawBtnGui == null || layoutTick == null
                || drawDropDown == null || dropDownSize == null || menuLocation == null
                || dropDownVisible == null || clickedControl == null || mousePressed == null
                || onControlClicked == null || displayScreen == null || macroEdit == null
                || macroDesign == null || boundingBox == null) {
                try {
                    // Classes
                    Class<?> layoutManager = Class
                        .forName("net.eq2online.macros.gui.designable.LayoutManager");
                    Class<?> designableGuiLayout = Class
                        .forName("net.eq2online.macros.gui.designable.DesignableGuiLayout");
                    Class<?> buttonClass = Class
                        .forName("net.eq2online.macros.gui.controls.GuiMiniToolbarButton");
                    Class<?> guiDesigner = Class
                        .forName("net.eq2online.macros.gui.screens.GuiDesigner");
                    Class<?> guiDropDownMenu = Class
                        .forName("net.eq2online.macros.gui.controls.GuiDropDownMenu");
                    Class<?> guiControl = Class
                        .forName("net.eq2online.macros.gui.designable.DesignableGuiControl");
                    Class<?> abstractionLayer = Class
                        .forName("net.eq2online.macros.compatibility.AbstractionLayer");
                    Class<?> guiMacroEdit = Class
                        .forName("net.eq2online.macros.gui.screens.GuiMacroEdit");
                    // Class guiMacroBind =
                    // Class.forName("net.eq2online.macros.gui.screens.GuiMacroBind");
                    guiCustomGui = Class.forName("net.eq2online.macros.gui.screens.GuiCustomGui");
                    Class<?> localisationProvider = Class
                        .forName("net.eq2online.macros.compatibility.LocalisationProvider");
                    Class<?> macroModCore = Class.forName("net.eq2online.macros.core.MacroModCore");

                    // Constructors
                    Constructor<?> mkButtonConstructor = buttonClass
                        .getDeclaredConstructor(Minecraft.class, int.class,
                                                int.class, int.class);
                    Constructor<?> guiConstructor = guiCustomGui.getConstructor(designableGuiLayout, GuiScreen.class);
                    createDesignerScreen = guiDesigner.getDeclaredConstructor(String.class, GuiScreen.class, boolean.class);
                    macroEdit = guiMacroEdit.getDeclaredConstructor(int.class,
                                                                    GuiScreen.class);
                    macroDesign = guiDesigner.getDeclaredConstructor(String.class,
                                                                     GuiScreen.class, boolean.class);

                    mkgetBoundLayout = layoutManager.getDeclaredMethod("getBoundLayout",
                                                                       String.class, boolean.class);
                    layoutTick = designableGuiLayout.getDeclaredMethod("onTick", (Class[]) null);
                    drawBtnGui = buttonClass.getDeclaredMethod("drawControlAt", Minecraft.class, int.class, int.class, int.class, int.class, int.class,
                                                               int.class);
                    drawDropDown = guiDropDownMenu.getDeclaredMethod("drawControlAt", int.class, int.class, int.class, int.class);
                    draw = designableGuiLayout.getDeclaredMethod("draw", Rectangle.class, int.class, int.class);
                    controlClicked = guiCustomGui.getDeclaredMethod("controlClicked", int.class, int.class, int.class);
                    dropDownSize = guiDropDownMenu.getDeclaredMethod("getSize", (Class[]) null);
                    mousePressed = guiDropDownMenu.getDeclaredMethod("mousePressed", int.class, int.class);
                    onControlClicked = guiCustomGui.getDeclaredMethod("onControlClicked",
                                                                      guiControl);
                    displayScreen = abstractionLayer.getDeclaredMethod("displayGuiScreen",
                                                                       GuiScreen.class);
                    Method dropDownAdd = guiDropDownMenu.getDeclaredMethod("addItem", String.class, String.class, int.class, int.class);
                    Method getLocalisedString = localisationProvider.getDeclaredMethod(
                        "getLocalisedString", String.class);
                    coreInstance = macroModCore.getMethod("getInstance");
                    controlClicked.setAccessible(true);
                    onControlClicked.setAccessible(true);

                    // Fields
                    Field mkContextMenu = guiCustomGui.getDeclaredField("contextMenu");
                    menuLocation = guiCustomGui.getDeclaredField("contextMenuLocation");
                    clickedControl = guiCustomGui.getDeclaredField("clickedControl");
                    dropDownVisible = guiDropDownMenu.getDeclaredField("dropDownVisible");
                    boundingBox = guiCustomGui.getDeclaredField("boundingBox");
                    chatGuiHook = macroModCore.getDeclaredField("chatGuiHook");
                    mkContextMenu.setAccessible(true);
                    menuLocation.setAccessible(true);
                    clickedControl.setAccessible(true);
                    dropDownVisible.setAccessible(true);
                    boundingBox.setAccessible(true);
                    chatGuiHook.setAccessible(true);

                    // Objects
                    inChatLayout = mkgetBoundLayout.invoke(null, "inchat", false);
                    btnGui = mkButtonConstructor.newInstance(Minecraft.getMinecraft(), 4, 104, 64);
                    inChatGUI = guiConstructor.newInstance(inChatLayout, null);
                    dropDownMenu = mkContextMenu.get(inChatGUI);
                    dropDownAdd.invoke(
                        dropDownMenu,
                        "design",
                        "\247e"
                            + getLocalisedString.invoke(null,
                                                        "tooltip.guiedit"), 26, 16);

                    // Add context menus in reverse order.
                    // ChatContextMenu.insertContextAtPos(0, new
                    // MacrosContext(0)); // Doesn't work for now
                    ChatContextMenu.insertContextAtPos(1, new MacrosContext(1));
                    ChatContextMenu.insertContextAtPos(2, new MacrosContext(2));
                    ChatContextMenu.insertContextAtPos(3, new ContextDummy("-------"));

                }
                catch (Exception e) {
                    present = false;
                }
            }
            else {
                try {
                    Class<?> designableGuiLayout = Class
                        .forName("net.eq2online.macros.gui.designable.DesignableGuiLayout");
                    Constructor<?> guiConstructor = guiCustomGui.getConstructor(designableGuiLayout, GuiScreen.class);
                    inChatGUI = guiConstructor.newInstance(inChatLayout, null);
                }
                catch (Exception e) {
                    present = false;
                }
            }
        }
    }

    public boolean controlClicked(int par1, int par2, int par3) {
        if (!present)
            return false;
        boolean clicked = false;
        try {
            boundingBox.set(inChatGUI, new Rectangle(0, 0, screen.width, screen.height - 14));
            clicked = ((Boolean) controlClicked
                .invoke(inChatGUI, new Object[]{par1, par2, par3})).booleanValue();
            if (clicked && par3 == 1) {
                dropDownVisible.set(dropDownMenu, true);
                Dimension contextMenuSize = (Dimension) dropDownSize.invoke(dropDownMenu,
                                                                            (Object[]) null);
                menuLocation.set(
                    inChatGUI,
                    new Point(Math.min(par1, screen.width - contextMenuSize.width), Math.min(
                        par2 - 8, screen.height - contextMenuSize.height)));
            }
            if (clicked)
                return true;
            if (hovered) {
                GuiScreen designerScreen = (GuiScreen) createDesignerScreen
                    .newInstance(new Object[]{"inchat", screen, true});
                Minecraft.getMinecraft().displayGuiScreen(designerScreen);
                return true;
            }
        }
        catch (Exception e) {
            present = false;
        }
        return clicked;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        if (!present)
            return;
        Object[] args = new Object[3];
        args[0] = new Rectangle(0, 0, screen.width, screen.height - 14);
        args[1] = par1;
        args[2] = par2;

        Object[] args2 = new Object[7];
        args2[0] = Minecraft.getMinecraft();
        args2[1] = par1;
        args2[2] = par2;
        args2[3] = screen.width - 20;
        args2[4] = screen.height - 14;
        args2[5] = 0xff1200;
        args2[6] = 0x80000000;

        try {
            layoutTick.invoke(inChatLayout, (Object[]) null);
            draw.invoke(inChatLayout, args);
            Object isHovered = drawBtnGui.invoke(btnGui, args2);
            hovered = ((Boolean) isHovered).booleanValue();
        }
        catch (Exception e) {
            present = false;
        }
    }

    @Override
    public void initGui(GuiScreen screen) {
        this.screen = screen;
        if (present) {
            try {
                inChatLayout = mkgetBoundLayout.invoke(null, "inchat", false);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGuiClosed() {

    }

    @Override
    public boolean mouseClicked(int x, int y, int button) {
        return controlClicked(x, y, button);
    }

    @Override
    public void handleMouseInput() {
    }

    @Override
    public boolean actionPerformed(GuiButton button) {
        return false;
    }

    @Override
    public void updateScreen() {

    }

}
