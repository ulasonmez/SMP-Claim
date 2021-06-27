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

public class KöyKomutlarý implements CommandExecutor{

	private Main plugin;
	public KöyKomutlarý(Main plugin) {
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
						player.sendMessage(koyname+" adýndaki köyünüz kuruldu");
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
						davetEdilen.sendMessage(aqua+player.getName()+green +" seni köyüne davet ediyor.");
						davetEdilen.spigot().sendMessage(kabul);
						plugin.bekleyenDavetler.put(davetEdilen.getUniqueId(),player.getUniqueId());
					}
					else if(args[0].equals("kabul")) {
						if(plugin.bekleyenDavetler.containsKey(player.getUniqueId())) {
							Player davetEden = Bukkit.getPlayer(plugin.bekleyenDavetler.get(player.getUniqueId()));
							if(args[1].equals(davetEden.getName())) {
								List<String> üyeler = plugin.getConfig().getStringList(sahibinKöyününÝsmi(davetEden)+".üyeler");
								üyeler.add(player.getName());
								plugin.getConfig().set(sahibinKöyününÝsmi(davetEden)+".üyeler", üyeler);
								plugin.saveConfig();
								player.sendMessage(green+"Artýk "+aqua+sahibinKöyününÝsmi(davetEden)+green+" köyünün bir üyesisin!");
								davetEden.sendMessage(aqua+player.getName()+green+" artýk köyünün bir üyesi!");
								plugin.bekleyenDavetler.remove(player.getUniqueId());
							}else {
								player.sendMessage(kirmizi+"Yanlýþ kiþinin davetini kabul etmeye çalýþtýn.");
							}
						}else {
							player.sendMessage(kirmizi+"Seni bekleyen bir davet yok.");
						}
					}
				}
			}
			if(args[0].equals("list")) {
				sender.sendMessage("Köyler: "+aqua+köyÝsimleriniDöndür());
			}
		}
		return false;
	}
	public ArrayList<String> köyÝsimleriniDöndür() {
		ArrayList<String> koyler = new ArrayList<String>();
		for(String k : plugin.getConfig().getKeys(false)) {
			koyler.add(k);
		}
		return koyler;
	}
	public String sahibinKöyününÝsmi(Player p) {
		ArrayList<String> köyler = köyÝsimleriniDöndür();
		for(String a : köyler) {
			String uuid = plugin.getConfig().getString(a+".koysahibi-id");
			if(p.getUniqueId().toString().equals(uuid)) {
				return a;
			}
		}
		return null;
	}
}
