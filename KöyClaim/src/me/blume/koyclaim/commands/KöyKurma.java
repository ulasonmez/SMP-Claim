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

public class KöyKurma implements CommandExecutor{

	private Main plugin;
	public KöyKurma(Main plugin) {
		this.plugin=plugin;
	}
	ChatColor kirmizi = ChatColor.RED;
	ChatColor green = ChatColor.GREEN;
	ChatColor aqua = ChatColor.AQUA;
	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(label.equals("koy")) {
				if(args[0].equals("kur")) {
						String koyname = args[1];
						plugin.getConfig().set( koyname+".koysahibi-ismi", player.getName());
						plugin.getConfig().set( koyname+".koysahibi-id",player.getUniqueId().toString());
						player.sendMessage(aqua+koyname+green+" adýndaki köyünüz kuruldu");
						plugin.saveConfig();
				}
				if(label.equals("koy") && args[0].equals("sil")) {
					String koyname = args[1]; 
					for(String a : köyÝsimleriniDöndür()) {
						if(a.equals(sahibinKöyününÝsmi(player)) && koyname.equals(a)) {
							player.sendMessage(ChatColor.AQUA+a+ChatColor.GREEN+" isimli köyünüz silindi.");
							plugin.getConfig().set(a, null);
							plugin.saveConfig();
							break;
						}
					}
				}
				if(args[0].equals("davet")) {
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
					davetEdilen.sendMessage(aqua+player.getName()+green +" seni köyüne davet ediyor.");
					davetEdilen.spigot().sendMessage(kabul);
					davetEdilen.spigot().sendMessage(red);
					plugin.bekleyenDavetler.put(davetEdilen.getUniqueId(),player.getUniqueId());
				}
				else if(args[0].equals("kabul")) {
					if(plugin.bekleyenDavetler.containsKey(player.getUniqueId())) {
						Player davetEden = Bukkit.getPlayer(plugin.bekleyenDavetler.get(player.getUniqueId()));
						if(args[1].equals(davetEden.getName())) {
							List<String> üyeler = plugin.getConfig().getStringList( sahibinKöyününÝsmi(davetEden)+".üyeler");
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
				else if(args[0].equals("red")) {
					if(plugin.bekleyenDavetler.containsKey(player.getUniqueId())) {
						Player davetEden = Bukkit.getPlayer(plugin.bekleyenDavetler.get(player.getUniqueId()));
						if(args[1].equals(davetEden.getName())) {
							player.sendMessage(aqua+davetEden.getName()+green+"' in köy davetini kabul etmedin.");
							davetEden.sendMessage(aqua+player.getName()+kirmizi+" yolladýðýn isteði kabul etmedi.");
							plugin.bekleyenDavetler.remove(player.getUniqueId());
						}
					}
				}
				
				if(args[0].equals("isim")) {
					String args1 = args[1];
					
					if(!oyuncuMu(args1) && !args1.equals("kabul") && !args1.equals("red")) {
						String yeniÖneri = args[1];
						String köyününÝsmi = üyeninKöyününÝsmi(player);
						if(köyününÝsmi==null) {
							player.sendMessage(kirmizi+"Herhangi bir köye ait deðilsin.");
							return false;
						}
						UUID sahipUuid = köyünSahibi(köyününÝsmi);
						Player klanSahibi = Bukkit.getPlayer(sahipUuid);
						klanSahibi.sendMessage(aqua+player.getName()+green+" köy için þu ismi öneriyor: "+yeniÖneri);
						plugin.bekleyenÝsimDegistirme.put(player.getUniqueId(), sahipUuid);
						plugin.bekleyenÝsimÖnerisi.put(sahipUuid, yeniÖneri);
						TextComponent kabul1 = new TextComponent("Kabul et");
						TextComponent red1 = new TextComponent("Reddet");
						red1.setColor(kirmizi);
						kabul1.setColor(green);
						kabul1.setBold(true);
						red1.setBold(true);
						klanSahibi.spigot().sendMessage(kabul1);
						klanSahibi.spigot().sendMessage(red1);
						if(plugin.bekleyenÝsimDegistirme.containsValue(klanSahibi.getUniqueId())) {
							kabul1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/koy isim kabul"));
							red1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/koy isim red"));
						}
					}
					else if(args[1].equals("kabul")) {
						if(plugin.bekleyenÝsimDegistirme.containsValue(player.getUniqueId())) {
							player.sendMessage(green+"Köyün ismi "+aqua+plugin.bekleyenÝsimÖnerisi.get(player.getUniqueId())+green+" olarak deðiþtirildi.");
							Player degisiklikÝsteyen = Bukkit.getPlayer(plugin.bekleyenÝsimDegistirme.get(player.getUniqueId()));
							köyÝsmiDeðiþtir(sahibinKöyününÝsmi(player),plugin.bekleyenÝsimÖnerisi.get(player.getUniqueId()),player.getName(),
									player.getUniqueId().toString(),plugin.getConfig().getStringList(sahibinKöyününÝsmi(player)+".üyeler"));
							plugin.saveConfig();
							plugin.bekleyenÝsimDegistirme.remove(player.getUniqueId());
							plugin.bekleyenÝsimÖnerisi.remove(player.getUniqueId());
						}else {
							player.sendMessage(kirmizi+"Sana gönderilen herhangi bir öneri gözükmüyor.");
						}
					}
					else if(args[1].equals("red")) {
						if(plugin.bekleyenÝsimDegistirme.containsValue(player.getUniqueId())) {
							Player degisiklikÝsteyen = Bukkit.getPlayer(plugin.bekleyenÝsimDegistirme.get(player.getUniqueId()));
							//degisiklikÝsteyen.sendMessage(kirmizi+"Köy sahibi deðiþiklik isteðini kabul etmedi.");
							plugin.bekleyenÝsimDegistirme.remove(player.getUniqueId());
							plugin.bekleyenÝsimÖnerisi.remove(player.getUniqueId());
						}else {
							player.sendMessage(kirmizi+"Sana gönderilen herhangi bir öneri gözükmüyor.");
						}
					}
				}
				
				if(args[0].equals("list")) {
					List<String> köyler = köyÝsimleriniDöndür();
					sender.sendMessage(köyler.toString());
				}
				
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
			String uuid = plugin.getConfig().getString( 	a+".koysahibi-id");
			if(p.getUniqueId().toString().equals(uuid)) {
				return a;
			}
		}
		return null;
	}
	public String üyeninKöyününÝsmi(Player p) {
		ArrayList<String> köyler = köyÝsimleriniDöndür();
		for(String a : köyler) {
			List<String> üyeÝsimleri = plugin.getConfig().getStringList( a+".üyeler");
			if(üyeÝsimleri.contains(p.getName())) return a;
		}
		return null;
	}
	public UUID köyünSahibi(String a) {
		String uuid = plugin.getConfig().getString( a+".koysahibi-id");
		for(Player p: Bukkit.getOnlinePlayers()) {
			if(p.getUniqueId().toString().equals(uuid)) return p.getUniqueId();
		}
		return null;
	}
	public boolean oyuncuMu(String s) {
		Player p = Bukkit.getPlayer(s);
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.equals(p)) return true;
		}
		return false;
	}
	public void köyÝsmiDeðiþtir(String öncekiÝsmi,String yeniÝsmi,String sahipÝsmi,String sahipUUID,List<String> oyuncular) {
		for(String a : köyÝsimleriniDöndür()) {
			if(a.equals(öncekiÝsmi)) {
				plugin.getConfig().set(a, null);
				plugin.getConfig().set( yeniÝsmi+".koysahibi-ismi", sahipÝsmi);
				plugin.getConfig().set( yeniÝsmi+".koysahibi-id",sahipUUID);
				plugin.getConfig().set(yeniÝsmi+".üyeler", oyuncular);
			}
		}
	}
}

