package de.kuratan.skreenshot;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuBackgroundCommand implements ICommand {
    private List<String> aliases;
    private HashMap<EntityPlayer, Integer> rotation;

    public MenuBackgroundCommand() {
        aliases = new ArrayList<String>();
        aliases.add("menubackground");
        aliases.add("mbg");
    }

    @Override
    public String getCommandName() {
        return "menubackground";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "menubackground";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
        if (p_71515_1_ instanceof EntityPlayer) {
            new MenuBackgroundCreator((EntityPlayer) p_71515_1_);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
