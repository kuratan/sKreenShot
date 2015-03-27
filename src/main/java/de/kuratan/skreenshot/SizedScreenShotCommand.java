package de.kuratan.skreenshot;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SizedScreenShotCommand implements ICommand {
    private List<String> aliases;

    public SizedScreenShotCommand() {
        aliases = new ArrayList<String>();
        aliases.add("sizedscreenshot");
        aliases.add("ssh");
    }

    @Override
    public String getCommandName() {
        return "sizedscreenshot";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "sizedscreenshot <width> <height>";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
        if (p_71515_1_ instanceof EntityPlayer) {
            new SizedScreenShotCreator((EntityPlayer) p_71515_1_, Integer.parseInt(p_71515_2_[0]), Integer.parseInt(p_71515_2_[1]));
        } else {
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
        if (p_71519_1_ instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) p_71519_1_;
            if (Config.OP_ONLY) {
                // do check here...
            }
            return true;
        }
        return false;
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
