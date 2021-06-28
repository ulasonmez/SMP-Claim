package me.blume.koyclaim;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import me.blume.koyclaim.commands.KöyKurma;


public class Main extends JavaPlugin {
	public static HashMap<UUID,UUID> bekleyenDavetler = new HashMap<UUID,UUID>();
	public static HashMap<UUID,UUID> bekleyenÝsimDegistirme = new HashMap<UUID,UUID>();
	public static HashMap<UUID,String> bekleyenÝsimÖnerisi = new HashMap<UUID,String>();
	@Override
	public void onEnable() {
		getCommand("koy").setExecutor(new KöyKurma(this));
		loadConfig();
	}
	@Override
	public void onDisable() {
		
	}
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

}
