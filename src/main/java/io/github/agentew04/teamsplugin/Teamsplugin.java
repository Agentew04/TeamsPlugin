package io.github.agentew04.teamsplugin;

import io.github.agentew04.teamsplugin.commands.TeamsCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Teamsplugin extends JavaPlugin {

    public FileConfiguration config;

    @Override
    public void onEnable() {
        // set command executor
        this.getCommand("teams").setExecutor(new TeamsCommand(this));

        // set tab completer

        // load config
        this.config = getConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.saveConfig();
    }

    private boolean teamExists(String teamName) {
        // check if team exists
        return this.config.isSet(getTeamPath(teamName));
    }

    private String getTeamPath(String teamName) {
        return "teams." + teamName;
    }

    private boolean isPlayerInTeam(String teamName, UUID player) {
        // check if team exists
        if(!this.teamExists(teamName)) return false;

        // check if player is in team
        List<String> members = this.config.getStringList(getTeamPath(teamName) + ".members");
        return members.contains(player.toString());
    }

    public String getPlayerTeam(UUID player) {
        for(String teamName : this.config.getConfigurationSection("teams").getKeys(false)) {
            if(this.isPlayerInTeam(teamName, player)) return teamName;
        }
        return null;
    }

    public boolean createTeam(String teamName, UUID owner) {
        // check if team already exists
        if (this.teamExists(teamName)) return false;

        // check if player is already in a team
        if (this.getPlayerTeam(owner) != null) return false;

        List<String> members = new ArrayList<>();
        members.add(owner.toString());
        String path = getTeamPath(teamName);
        this.config.set(path + ".name", teamName);
        this.config.set(path + ".owner", owner.toString());
        this.config.set(path + ".members", members);
        this.config.set(path + ".invited", null);
        this.config.set(path + ".color", ChatColor.WHITE.toString());
        this.config.set(path + ".friendlyFire", false);

        this.saveConfig();
        return true;
    }

    public boolean inviteToTeam(String teamName, UUID player) {
        // check if team  exists
        if (!this.teamExists(teamName)) return false;

        // check if player is already in a team
        if (this.getPlayerTeam(player) != null) return false;

        String path = getTeamPath(teamName);
        List<String> invited = this.config.getStringList(path + ".invited");
        // check if player is already invited
        if (invited.contains(player.toString())) return false;

        invited.add(player.toString());
        this.config.set(path + ".invited", invited);

        this.saveConfig();
        return true;
    }

    public boolean leaveTeam(UUID player) {
        String teamName = this.getPlayerTeam(player);

        // check if player is in a team
        if (teamName == null) return false;

        String path = getTeamPath(teamName);
        List<String> members = this.config.getStringList(path + ".members");
        members.remove(player.toString());
        this.config.set(path + ".members", members);

        this.saveConfig();
        return true;
    }

    public boolean setFriendlyFire(String teamName, boolean friendlyFire) {
        // check if team exists
        if(!this.teamExists(teamName)) return false;

        String path = getTeamPath(teamName);
        this.config.set(path + ".friendlyFire", friendlyFire);

        this.saveConfig();
        return true;
    }

    public boolean getFriendlyFire(String teamName) {
        String path = getTeamPath(teamName);
        return this.config.getBoolean(path + ".friendlyFire");
    }

    public boolean setTeamColor(String teamName, ChatColor color) {
        // check if team exists
        if(!this.teamExists(teamName)) return false;

        String path =getTeamPath(teamName);
        this.config.set(path + ".color", color.toString());

        this.saveConfig();
        return true;
    }

    public ChatColor getTeamColor(String teamName) {
        String path = getTeamPath(teamName);
        return ChatColor.valueOf(this.config.getString(path + ".color"));
    }

    public List<String> getTeams(){
        List<String> teams = new ArrayList<>();
        teams.addAll(this.config.getConfigurationSection("teams").getKeys(false));
    	return teams;
    }

    public Location getPlayerLocation(UUID sender, UUID target){
        String senderTeam = this.getPlayerTeam(sender);
        String targetTeam = this.getPlayerTeam(target);
        if(senderTeam == null || targetTeam == null) return null;
        if(senderTeam.equals(targetTeam)) {
            return Bukkit.getPlayer(target).getLocation();
        }
        return null;
    }

    public boolean deleteTeam(String teamName, UUID owner) {
        // check if team exists
        if (!this.teamExists(teamName)) return false;

        // check if user is owner
        UUID ownerid = UUID.fromString(this.config.getString("teams." + teamName + ".owner", UUID.randomUUID().toString()));
        if(!ownerid.equals(owner)) return false;
        this.config.set(getTeamPath(teamName), null);
        this.saveConfig();
        return true;
    }

    public boolean acceptInvite(String teamName, UUID player) {
        // check if team exists
        if (!this.teamExists(teamName)) return false;

        // check if player is already in a team
        if (this.getPlayerTeam(player) != null) return false;

        String path = getTeamPath(teamName);
        List<String> invited = this.config.getStringList(path + ".invited");
        // check if player is invited
        if (!invited.contains(player.toString())) return false;

        // remove invite
        invited.remove(player.toString());
        this.config.set(path + ".invited", invited);

        // add as member
        List<String> members = this.config.getStringList(path + ".members");
        members.add(player.toString());
        this.config.set(path + ".members", members);

        this.saveConfig();
        return true;
    }

    public boolean declineInvite(String teamName, UUID player) {
        // check if team exists
        if (!this.teamExists(teamName)) return false;

        String path = getTeamPath(teamName);
        List<String> invited = this.config.getStringList(path + ".invited");

        // check if player is invited
        if (!invited.contains(player.toString())) return false;

        // remove invite
        invited.remove(player.toString());
        this.config.set(path + ".invited", invited);

        this.saveConfig();
        return true;
    }

}
