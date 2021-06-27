package me.blume.koyclaim.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.blume.koyclaim.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class K�yKomutlar� implements CommandExecutor{

	private Main plugin;
	public K�yKomutlar�(Main plugin) {
		this.plugin=plugin;
	}
	ChatColor kirmizi = ChatColor.RED;
	ChatColor green = ChatColor.GREEN;
	ChatColor aqua = ChatColor.AQUA;
	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(label.equals("koy")) {
				if(args.length==2) {
					if(args[0].equals("kur")) {
						String koyname = args[1];
						plugin.getConfig().set(koyname+".koysahibi-ismi", player.getName());
						plugin.getConfig().set(koyname+".koysahibi-id",player.getUniqueId().toString());
						player.sendMessage(koyname+" ad�ndaki k�y�n�z kuruldu");
						plugin.saveConfig();
					}
					else if(args[0].equals("davet")) {
						Player davetEdilen = Bukkit.getPlayer(args[1]);
						TextComponent kabul = new TextComponent("Kabul et");
						TextComponent red = new TextComponent("Reddet");
						red.setColor(kirmizi);
						kabul.setColor(green);
						kabul.setBold(true);
						red.setBold(true);
						if(!plugin.bekleyenDavetler.containsKey(davetEdilen.getUniqueId())) {
						kabul.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/koy kabul "+player.getName()));
						}
						davetEdilen.sendMessage(aqua+player.getName()+green +" seni k�y�ne davet ediyor.");
						davetEdilen.spigot().sendMessage(kabul);
						plugin.bekleyenDavetler.put(davetEdilen.getUniqueId(),player.getUniqueId());
					}
					else if(args[0].equals("kabul")) {
						if(plugin.bekleyenDavetler.containsKey(player.getUniqueId())) {
							Player davetEden = Bukkit.getPlayer(plugin.bekleyenDavetler.get(player.getUniqueId()));
							if(args[1].equals(davetEden.getName())) {
								List<String> �yeler = plugin.getConfig().getStringList(sahibinK�y�n�n�smi(davetEden)+".�yeler");
								�yeler.add(player.getName());
								plugin.getConfig().set(sahibinK�y�n�n�smi(davetEden)+".�yeler", �yeler);
								plugin.saveConfig();
								player.sendMessage(green+"Art�k "+aqua+sahibinK�y�n�n�smi(davetEden)+green+" k�y�n�n bir �yesisin!");
								davetEden.sendMessage(aqua+player.getName()+green+" art�k k�y�n�n bir �yesi!");
								plugin.bekleyenDavetler.remove(player.getUniqueId());
							}else {
								player.sendMessage(kirmizi+"Yanl�� ki�inin davetini kabul etmeye �al��t�n.");
							}
						}else {
							player.sendMessage(kirmizi+"Seni bekleyen bir davet yok.");
						}
					}
				}
			}
			if(args[0].equals("list")) {
				sender.sendMessage("K�yler: "+aqua+k�y�simleriniD�nd�r());
			}
		}
		return false;
	}
	public ArrayList<String> k�y�simleriniD�nd�r() {
		ArrayList<String> koyler = new ArrayList<String>();
		for(String k : plugin.getConfig().getKeys(false)) {
			koyler.add(k);
		}
		return koyler;
	}
	public String sahibinK�y�n�n�smi(Player p) {
		ArrayList<String> k�yler = k�y�simleriniD�nd�r();
		for(String a : k�yler) {
			String uuid = plugin.getConfig().getString(a+".koysahibi-id");
			if(p.getUniqueId().toString().equals(uuid)) {
				return a;
			}
		}
		return null;
	}
}
