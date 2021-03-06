package de.kuratan.skreenshot;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class MenuBackgroundCommand implements ICommand {
    private List<String> aliases;

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
        } else {
            throw new WrongUsageException("Caller must be a player entity");
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
