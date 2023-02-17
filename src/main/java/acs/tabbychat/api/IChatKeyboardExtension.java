package acs.tabbychat.api;

public interface IChatKeyboardExtension extends IChatExtension {

    void keyTyped(char c, int code);

}
