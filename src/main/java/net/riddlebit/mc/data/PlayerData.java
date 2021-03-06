package net.riddlebit.mc.data;

import com.google.gson.annotations.Expose;
import dev.morphia.annotations.*;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

@Entity(value = "players", noClassnameStored = true)
public class PlayerData {

    public PlayerData() {
        id = new ObjectId();
    }

    @Id
    private ObjectId id;

    @Expose
    public String uuid;

    @Expose
    public String name;

    @Expose
    public float reputation;

    public boolean isDead() {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        return player == null || player.isDead();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerData that = (PlayerData) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
