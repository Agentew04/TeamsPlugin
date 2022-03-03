package io.github.agentew04.teamsplugin.commands;

import io.github.agentew04.teamsplugin.Teamsplugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.swing.text.StyledEditorKit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeamsCommand implements CommandExecutor {

    private final Teamsplugin plugin;
    private List<String> availableCommands;


    public TeamsCommand(Teamsplugin plugin) {
        this.plugin = plugin;
        this.availableCommands = new ArrayList<>(
                Arrays.asList(
                        "create",
                        "invite",
                        "leave",
                        "edit",
                        "list",
                        "find",
                        "delete",
                        "accept",
                        "decline"));

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED+"Usage: /teams <create | invite | leave | edit | list | find | delete | accept | decline>");
            return true;
        }
        if (!availableCommands.contains(args[0])) {
            sender.sendMessage(ChatColor.RED+"Usage: /teams <create | invite | leave | edit | list | find | delete | accept | decline>");
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"Usage: /teams create <teamName>");
                return true;
            }
            args = Arrays.copyOfRange(args, 1, args.length);
            String teamName = String.join(" ", args);
            boolean result = plugin.createTeam(teamName, ((Player)sender).getUniqueId());

            if(!result){
                sender.sendMessage(ChatColor.RED+"Team already exists/You are already in a team");
                return true;
            }
            sender.sendMessage(ChatColor.GREEN+"Team created");
            return true;
        }else if(args[0].equalsIgnoreCase("invite")){
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED+"Usage: /teams invite <player>");
                return true;
            }
            Player player = plugin.getServer().getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED+"Player not found");
                return true;
            }
            boolean result = plugin.inviteToTeam(plugin.getPlayerTeam(((Player)sender).getUniqueId()), player.getUniqueId());

            if(!result){
                sender.sendMessage(ChatColor.RED+"Player is already in a team/already invited/team does not exist");
                return true;
            }
            sender.sendMessage(ChatColor.GREEN+"Player invited");
            return true;
        }else if(args[0].equalsIgnoreCase("leave")){
            boolean result = plugin.leaveTeam(((Player)sender).getUniqueId());

            if(!result){
                sender.sendMessage(ChatColor.RED+"You are not in a team");
                return true;
            }
            return true;
        }else if(args[0].equalsIgnoreCase("accept")){
            if(args.length < 2){
                sender.sendMessage(ChatColor.RED+"Usage: /teams accept <player>");
                return true;
            }
            args = Arrays.copyOfRange(args, 1, args.length);
            String teamName = String.join(" ", args);

            boolean result = plugin.acceptInvite(teamName, ((Player)sender).getUniqueId());

            if(!result){
                sender.sendMessage(ChatColor.RED+"You are not invited to this team/already in a team/team does not exist");
                return true;
            }
            return true;
        }else if(args[0].equalsIgnoreCase("decline")){
            if(args.length < 2) {
                sender.sendMessage(ChatColor.RED+"Usage: /teams decline <teamName>");
                return true;
            }

            args = Arrays.copyOfRange(args, 1, args.length);
            String teamName = String.join(" ", args);

            boolean result = plugin.declineInvite(teamName, ((Player)sender).getUniqueId());

            if(!result){
                sender.sendMessage(ChatColor.RED+"You are not invited to this team/already in a team/team does not exist");
                return true;
            }
            sender.sendMessage(ChatColor.GREEN+"Invite declined");
            return true;
        }else if(args[0].equalsIgnoreCase("edit")){
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED+"Usage: /teams edit <friendlyFire | color> <[true | false] | [color]>");
            }
            boolean isFriendlyFire = args[1].equalsIgnoreCase("friendlyFire");
            boolean isColor = args[1].equalsIgnoreCase("color");
            if(!isFriendlyFire && !isColor){
                sender.sendMessage(ChatColor.RED+"Usage: /teams edit <friendlyFire | color> <[true | false] | [color]>");
                return true;
            }
            if(isFriendlyFire){
                boolean newFF = Boolean.parseBoolean(args[2]);
                boolean result = plugin.setFriendlyFire(((Player)sender).getUniqueId(), newFF);
                if(!result){
                    sender.sendMessage(ChatColor.RED+"You are not the owner of this team/not in a team");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN+"Friendly fire set to "+newFF);
                return true;
            }
            if(isColor){
                ChatColor newcolor;
                try {
                    newcolor = ChatColor.valueOf(args[2]);
                }catch (IllegalArgumentException e){
                    sender.sendMessage(ChatColor.RED+"Invalid color");
                    return true;
                }
                boolean result = plugin.setTeamColor(((Player)sender).getUniqueId(), newcolor);
                if(!result){
                    sender.sendMessage(ChatColor.RED+"You are not the owner of this team/not in a team");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN+"Team color set to "+newcolor);
                return true;
            }
            return true;
        }


        // todo: add list command
        // todo: add find command
        // todo add delete command
        return false;
    }
}
