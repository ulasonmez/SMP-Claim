package me.blume.koyclaim.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
							red.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/koy red "+player.getName()));
						}
						davetEdilen.sendMessage(aqua+player.getName()+green +" seni k�y�ne davet ediyor.");
						davetEdilen.spigot().sendMessage(kabul);
						davetEdilen.spigot().sendMessage(red);
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
					else if(args[0].equals("red")) {
						if(plugin.bekleyenDavetler.containsKey(player.getUniqueId())) {
							Player davetEden = Bukkit.getPlayer(plugin.bekleyenDavetler.get(player.getUniqueId()));
							if(args[1].equals(davetEden.getName())) {
								player.sendMessage(aqua+davetEden.getName()+green+"' in k�y davetini kabul etmedin.");
								davetEden.sendMessage(aqua+player.getName()+kirmizi+" yollad���n iste�i kabul etmedi.");
								plugin.bekleyenDavetler.remove(player.getUniqueId());
							}
						}
					}
					else if(args[0].equals("isim")) {
						String yeni�neri = args[1];
						String k�y�n�n�smi = �yeninK�y�n�n�smi(player);
						if(k�y�n�n�smi!=null) {
							UUID sahipUuid = k�y�nSahibi(k�y�n�n�smi);
							Player klanSahibi = Bukkit.getPlayer(sahipUuid);
							klanSahibi.sendMessage(aqua+player.getName()+green+" k�y i�in �u ismi �neriyor: "+yeni�neri);
							plugin.bekleyen�simDegistirme.put(player.getUniqueId(), sahipUuid);
							plugin.bekleyen�sim�nerisi.put(sahipUuid, yeni�neri);
							TextComponent kabul1 = new TextComponent("Kabul et");
							TextComponent red1 = new TextComponent("Reddet");
							red1.setColor(kirmizi);
							kabul1.setColor(green);
							kabul1.setBold(true);
							red1.setBold(true);
							klanSahibi.spigot().sendMessage(kabul1);
							klanSahibi.spigot().sendMessage(red1);
							if(plugin.bekleyen�simDegistirme.containsValue(klanSahibi.getUniqueId())) {
									kabul1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/koy isim kabul"));
									red1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/koy isim red"));
								}
							
						}else {
							player.sendMessage("�sim �nerisi verebilmek i�in herhangi bir k�ye �ye de�ilsin.");
						}
						
					
						if(args[1].equals("kabul")) {
							if(plugin.bekleyenDavetler.containsValue(player.getUniqueId())) {
								player.sendMessage(green+"K�y�n ismi "+aqua+plugin.bekleyen�sim�nerisi.get(player.getUniqueId())+green+" olarak de�i�tirildi.");
								Player degisiklik�steyen = Bukkit.getPlayer(plugin.bekleyen�simDegistirme.get(player.getUniqueId()));
								degisiklik�steyen.sendMessage(green+"K�y�n ismi sahibinin izin vermesiyle k�y�n ad� istedi�in gibi "+aqua+plugin.bekleyen�sim�nerisi.get(player.getUniqueId())+green+" oldu");
								plugin.getConfig().set(sahibinK�y�n�n�smi(player), plugin.bekleyen�sim�nerisi.get(player.getUniqueId()));
								plugin.bekleyen�simDegistirme.remove(player.getUniqueId());
								plugin.bekleyen�sim�nerisi.remove(player.getUniqueId());
							}else {
								player.sendMessage("Sana g�nderilen herhangi bir �neri g�z�km�yor.");
							}
						}
						else if(args[1].equals("red")) {
							if(plugin.bekleyenDavetler.containsValue(player.getUniqueId())) {
								Player degisiklik�steyen = Bukkit.getPlayer(plugin.bekleyen�simDegistirme.get(player.getUniqueId()));
								degisiklik�steyen.sendMessage(kirmizi+"K�y sahibi de�i�iklik iste�ini kabul etmedi.");
								plugin.bekleyen�simDegistirme.remove(player.getUniqueId());
								plugin.bekleyen�sim�nerisi.remove(player.getUniqueId());
							}
						}
					}
				}
				if(args[0].equals("list") && args.length==1) {
					sender.sendMessage("K�yler: "+aqua+k�y�simleriniD�nd�r());
				}
				else if(args.length==1 && args[0].equals("list")) {
					String k�y�smi = args[1];
					if(k�y�simleriniD�nd�r().contains(k�y�smi)) {
						String k�ySahibi = plugin.getConfig().getString(args[1]+"..koysahibi-ismi");
						ArrayList<String> �yeler = k�y�simleriniD�nd�r();
						player.sendMessage(kirmizi+k�ySahibi+" "+�yeler);
					}else {
						player.sendMessage(kirmizi+"Girdi�iniz isimde bir k�y yok.");
					}
				}
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
	public String �yeninK�y�n�n�smi(Player p) {
		ArrayList<String> k�yler = k�y�simleriniD�nd�r();
		for(String a : k�yler) {
			List<String> �ye�simleri = plugin.getConfig().getStringList(a+".�yeler");
			if(�ye�simleri.contains(p.getName())) return a;
		}
		return null;
	}
	public UUID k�y�nSahibi(String a) {
		String uuid = plugin.getConfig().getString(a+".koysahibi-id");
		for(Player p: Bukkit.getOnlinePlayers()) {
			if(p.getUniqueId().toString().equals(uuid)) return p.getUniqueId();
		}
		return null;
	}
}
