package io.github.janvinas.trensminecat.signactions;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import io.github.janvinas.trensminecat.TrensMinecat;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.UUID;

public class SignActionRailCrossing extends SignAction {
    @Override
    public boolean match(SignActionEvent info) {
        return info.isType("railcrossing");
    }

    @Override
    public boolean canSupportRC() {
        return false;
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        if (!event.isType("railcrossing")) {
            return false;
        }

        return SignBuildOptions.create()
                .setName("rail crossing")
                .setDescription("TODO")
                .handle(event.getPlayer());
    }

    @Override
    public void execute(SignActionEvent info) {
        JavaPlugin plugin = TrensMinecat.getPlugin(TrensMinecat.class);
        String uuid = info.getLine(2);
        long temps;
        try {
            temps = Long.parseLong(info.getLine(3));
        } catch (NumberFormatException e){
            temps = 5L;
        }

        if (info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)) {
            if (!info.isPowered()) return;
            ItemFrame frame = (ItemFrame) Bukkit.getEntity(UUID.fromString(uuid));
            System.out.println("BEFORE " + frame);
            ItemStack itemStack = frame.getItem();
            ItemMeta meta = itemStack.getItemMeta();
            meta.setCustomModelData(17);
            System.out.println("AFTER " + frame);
            itemStack.setItemMeta(meta);

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                meta.setCustomModelData(17);
                itemStack.setItemMeta(meta);
                System.out.println("OFF " + frame);
            }, temps * 20L);
        }
    }
}
