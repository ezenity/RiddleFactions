package net.riddlebit.mc.controller;

import dev.morphia.query.Query;
import net.riddlebit.mc.RiddleFactions;
import net.riddlebit.mc.data.FactionData;
import net.riddlebit.mc.data.Invite;
import net.riddlebit.mc.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionController {

    private RiddleFactions plugin;

    private HashMap<String, FactionData> factions;

    private List<Invite> invites;

    public FactionController(RiddleFactions plugin) {
        this.plugin = plugin;
        factions = new HashMap<>();
        invites = new ArrayList<>();

        // Load factions from database
        Query query = plugin.datastore.createQuery(FactionData.class);
        List<FactionData> loadedFactions = query.find().toList();
        if (loadedFactions.size() > 0) {
            for (FactionData factionData : loadedFactions) {
                factions.put(factionData.name, factionData);
            }
        }

    }

    public boolean createFaction(String factionName, Player player) {

        if (isPlayerInFaction(player)) {
            player.sendMessage("You're already in a faction...");
            return true;
        }

        if (factions.containsKey(factionName)) {
            player.sendMessage("A faction with that name already exists");
            return true;
        }

        // Create the new faction
        PlayerData playerData = plugin.playerController.getPlayer(player);
        FactionData factionData = new FactionData(playerData);
        factionData.name = factionName;
        factions.put(factionName, factionData);
        plugin.datastore.save(factionData);

        player.sendMessage(factionName + " was created!");
        return true;
    }

    public boolean invite(Player inviter, Player invitee) {

        if (!isPlayerInFaction(inviter)) {
            inviter.sendMessage("You must be in a faction...");
            return true;
        }

        FactionData factionData = getFactionForPlayer(inviter);
        PlayerData inviterData = plugin.playerController.getPlayer(inviter);
        PlayerData inviteeData = plugin.playerController.getPlayer(invitee);

        if (factionData == null || inviterData == null || inviteeData == null) {
            return false;
        }

        if (inviterData.equals(inviteeData)) {
            inviter.sendMessage("You cannot invite yourself...");
            return true;
        }

        Invite invite = new Invite(factionData, inviterData, inviteeData);
        invites.add(invite);

        inviter.sendMessage(invitee.getDisplayName() + " was invited to your faction");
        invitee.sendMessage("You have been invited to " + factionData.name);
        return true;
    }

    public boolean isPlayerInFaction(Player player) {
        return getFactionForPlayer(player) != null;
    }

    public FactionData getFaction(String factionName) {
        return factions.get(factionName);
    }

    public FactionData getFactionForPlayer(Player player) {
        PlayerData playerData = plugin.playerController.getPlayer(player);
        for (FactionData factionData : factions.values()) {
            if (factionData.players.contains(playerData)) {
                return factionData;
            }
        }
        return null;
    }

}
