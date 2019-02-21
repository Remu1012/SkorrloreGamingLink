package me.skorrloregaming.hooks;

import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.event.EventBus;
import me.lucko.luckperms.api.event.user.track.UserPromoteEvent;
import me.skorrloregaming.Link$;
import me.skorrloregaming.LinkServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPerms_Listener {

	public void register() {
		RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
		if (provider != null) {
			LuckPermsApi api = provider.getProvider();
			EventBus eventBus = api.getEventBus();
			eventBus.subscribe(UserPromoteEvent.class, this::onUserPromote);
		}
	}

	private void onUserPromote(UserPromoteEvent event) {
		Bukkit.getScheduler().runTask(LinkServer.getPlugin(), () -> {
			Player player = Bukkit.getPlayer(event.getUser().getUuid());
			if (player != null) {
				String displayName = Link$.getFlashPlayerDisplayName(player);
				player.setDisplayName(displayName);
				player.setPlayerListName(displayName);
			}
		});
	}

}