package me.blume.koyclaim;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import me.blume.koyclaim.commands.K�yKomutlar�;


public class Main extends JavaPlugin {
	public static HashMap<UUID,UUID> bekleyenDavetler = new HashMap<UUID,UUID>();
	public static HashMap<UUID,UUID> bekleyen�simDegistirme = new HashMap<UUID,UUID>();
	public static HashMap<UUID,String> bekleyen�sim�nerisi = new HashMap<UUID,String>();
	@Override
	public void onEnable() {
		getCommand("koy").setExecutor(new K�yKomutlar�(this));
		loadConfig();
		//getServer().getPluginManager().registerEvents(new TurnInvis(this), this);
	}
	@Override
	public void onDisable() {
		
	}
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

}
