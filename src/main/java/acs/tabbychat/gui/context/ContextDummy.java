package acs.tabbychat.gui.context;

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Dummy context menu that includes setter methods and extra getter methods.
 */
public class ContextDummy extends ChatContext {

    private Method onClick;
    private Method isValid;
    private Behavior behavior = Behavior.GRAY;
    private ResourceLocation icon;
    private List<ChatContext> children;

    public ContextDummy() {}

    public ContextDummy(String display) {
        this.displayString = display;
    }

    @Override
    public void onClicked() {
        if (onClick == null)
            return;
        try {
            onClick.invoke(null);
        }
        catch (Exception e) {
            LogManager.getLogger().error(e);
        }
    }

    @Override
    public String getDisplayString() {
        return this.displayString;
    }

    public void setDisplayString(String string) {
        this.displayString = string;
    }

    @Override
    public ResourceLocation getDisplayIcon() {
        return this.icon;
    }

    public void setDisplayIcon(ResourceLocation icon) {
        this.icon = icon;
    }

    @Override
    public List<ChatContext> getChildren() {
        return this.children;
    }

    public void setChildren(List<ChatContext> children) {
        this.children = children;
    }

    @Override
    public boolean isPositionValid(int x, int y) {
        if (this.isValid == null)
            return false;
        try {
            return ((Boolean) isValid.invoke(null)).booleanValue();
        }
        catch (Exception e) {
            LogManager.getLogger().error(e);
            return false;
        }
    }

    @Override
    public Behavior getDisabledBehavior() {
        return this.behavior;
    }

    public Method getOnClickMethod() {
        return this.onClick;
    }

    /**
     * Sets the method that is invoked when clicked
     *
     * @param method Must be public static
     */
    public void setOnClickMethod(Method method) {
        this.onClick = method;
    }

    public Method getIsValidMethod() {
        return this.isValid;
    }

    /**
     * Sets the method called to determine the if the current location is valid.
     * If null, will return true. If errors, returns false.
     *
     * @param method Must be public static and return boolean
     */
    public void setIsValidMethod(Method method) {
        this.isValid = method;
    }

    public void setBehavior(Behavior behavior) {
        this.behavior = behavior;
    }

}
