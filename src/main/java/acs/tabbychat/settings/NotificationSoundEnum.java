package acs.tabbychat.settings;

import net.minecraft.client.resources.I18n;

public enum NotificationSoundEnum {
    ORB(I18n.format("sounds.orb"), "random.orb"),
    ANVIL(I18n.format("sounds.anvil"), "random.anvil_land"),
    BOWHIT(I18n.format("sounds.bowhit"), "random.bowhit"),
    BREAK(I18n.format("sounds.break"), "random.break"),
    CLICK(I18n.format("sounds.click"), "random.click"),
    GLASS(I18n.format("sounds.glass"), "random.glass"),
    BASS(I18n.format("sounds.bass"), "note.bassattack"),
    HARP(I18n.format("sounds.harp"), "note.harp"),
    PLING(I18n.format("sounds.pling"), "note.pling"),
    CAT(I18n.format("sounds.cat"), "mob.cat.meow"),
    BLAST(I18n.format("sounds.blast"), "fireworks.blast"),
    SPLASH(I18n.format("sounds.splash"), "liquid.splash"),
    SWIM(I18n.format("sounds.swim"), "liquid.swim"),
    BAT(I18n.format("sounds.bat"), "mob.bat.hurt"),
    BLAZE(I18n.format("sounds.blaze"), "mob.blaze.hit"),
    CHICKEN(I18n.format("sounds.chicken"), "mob.chicken.hurt"),
    COW(I18n.format("sounds.cow"), "mob.cow.hurt"),
    DRAGON(I18n.format("sounds.dragon"), "mob.enderdragon.hit"),
    ENDERMEN(I18n.format("sounds.endermen"), "mob.endermen.hit"),
    GHAST(I18n.format("sounds.ghast"), "mob.ghast.moan"),
    PIG(I18n.format("sounds.pig"), "mob.pig.say"),
    WOLF(I18n.format("sounds.wolf"), "mob.wolf.bark");

    private String title;
    private String file;

    private NotificationSoundEnum(String _title, String _file) {
        this.title = _title;
        this.file = _file;
    }

    public String toString() {
        return this.title;
    }

    public String file() {
        return this.file;
    }
}
