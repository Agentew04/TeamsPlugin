package io.github.agentew04.teamsplugin.commands;

import io.github.agentew04.teamsplugin.Teamsplugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            sender.sendMessage(ChatColor.RED+"Usage: /teams <create|invite|leave|edit|list|find|delete|accept|decline>");
            return true;
        }
        if (!availableCommands.contains(args[0])) {
            sender.sendMessage(ChatColor.RED+"Usage: /teams <create|invite|leave|edit|list|find|delete|accept|decline>");
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

            if (!result) {
                sender.sendMessage(ChatColor.RED+"Team already exists");
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
                sender.sendMessage(ChatColor.RED+"Player is already in a team");
                return true;
            }
            sender.sendMessage(ChatColor.GREEN+"Player invited");
            return true;
        }
        return false;
    }
}
