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
						player.sendMessage(aqua+koyname+green+" adındaki köyünüz kuruldu");
						plugin.saveConfig();
				}
				if(label.equals("koy") && args[0].equals("sil")) {
					String koyname = args[1]; 
					for(String a : köyİsimleriniDöndür()) {
						if(a.equals(sahibinKöyününİsmi(player)) && koyname.equals(a)) {
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
							List<String> üyeler = plugin.getConfig().getStringList( sahibinKöyününİsmi(davetEden)+".üyeler");
							üyeler.add(player.getName());
							plugin.getConfig().set(sahibinKöyününİsmi(davetEden)+".üyeler", üyeler);
							plugin.saveConfig();
							player.sendMessage(green+"Artık "+aqua+sahibinKöyününİsmi(davetEden)+green+" köyünün bir üyesisin!");
							davetEden.sendMessage(aqua+player.getName()+green+" artık köyünün bir üyesi!");
							plugin.bekleyenDavetler.remove(player.getUniqueId());
						}else {
							player.sendMessage(kirmizi+"Yanlış kişinin davetini kabul etmeye çalıştın.");
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
							davetEden.sendMessage(aqua+player.getName()+kirmizi+" yolladığın isteği kabul etmedi.");
							plugin.bekleyenDavetler.remove(player.getUniqueId());
						}
					}
				}
				
				if(args[0].equals("isim")) {
					String args1 = args[1];
					
					if(!oyuncuMu(args1) && !args1.equals("kabul") && !args1.equals("red")) {
						String yeniÖneri = args[1];
						String köyününİsmi = üyeninKöyününİsmi(player);
						if(köyününİsmi==null) {
							player.sendMessage(kirmizi+"Herhangi bir köye ait değilsin.");
							return false;
						}
						UUID sahipUuid = köyünSahibi(köyününİsmi);
						Player klanSahibi = Bukkit.getPlayer(sahipUuid);
						klanSahibi.sendMessage(aqua+player.getName()+green+" köy için şu ismi öneriyor: "+yeniÖneri);
						plugin.bekleyenİsimDegistirme.put(player.getUniqueId(), sahipUuid);
						plugin.bekleyenİsimÖnerisi.put(sahipUuid, yeniÖneri);
						TextComponent kabul1 = new TextComponent("Kabul et");
						TextComponent red1 = new TextComponent("Reddet");
						red1.setColor(kirmizi);
						kabul1.setColor(green);
						kabul1.setBold(true);
						red1.setBold(true);
						klanSahibi.spigot().sendMessage(kabul1);
						klanSahibi.spigot().sendMessage(red1);
						if(plugin.bekleyenİsimDegistirme.containsValue(klanSahibi.getUniqueId())) {
							kabul1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/koy isim kabul"));
							red1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/koy isim red"));
						}
					}
					else if(args[1].equals("kabul")) {
						if(plugin.bekleyenİsimDegistirme.containsValue(player.getUniqueId())) {
							player.sendMessage(green+"Köyün ismi "+aqua+plugin.bekleyenİsimÖnerisi.get(player.getUniqueId())+green+" olarak değiştirildi.");
							Player degisiklikİsteyen = Bukkit.getPlayer(plugin.bekleyenİsimDegistirme.get(player.getUniqueId()));
							köyİsmiDeğiştir(sahibinKöyününİsmi(player),plugin.bekleyenİsimÖnerisi.get(player.getUniqueId()),player.getName(),
									player.getUniqueId().toString(),plugin.getConfig().getStringList(sahibinKöyününİsmi(player)+".üyeler"));
							plugin.saveConfig();
							plugin.bekleyenİsimDegistirme.remove(player.getUniqueId());
							plugin.bekleyenİsimÖnerisi.remove(player.getUniqueId());
						}else {
							player.sendMessage(kirmizi+"Sana gönderilen herhangi bir öneri gözükmüyor.");
						}
					}
					else if(args[1].equals("red")) {
						if(plugin.bekleyenİsimDegistirme.containsValue(player.getUniqueId())) {
							Player degisiklikİsteyen = Bukkit.getPlayer(plugin.bekleyenİsimDegistirme.get(player.getUniqueId()));
							//degisiklikİsteyen.sendMessage(kirmizi+"Köy sahibi değişiklik isteğini kabul etmedi.");
							plugin.bekleyenİsimDegistirme.remove(player.getUniqueId());
							plugin.bekleyenİsimÖnerisi.remove(player.getUniqueId());
						}else {
							player.sendMessage(kirmizi+"Sana gönderilen herhangi bir öneri gözükmüyor.");
						}
					}
				}
				
				if(args[0].equals("list")) {
					List<String> köyler = köyİsimleriniDöndür();
					sender.sendMessage(köyler.toString());
				}
				
			}
		}
		return false;
	}
	public ArrayList<String> köyİsimleriniDöndür() {
		ArrayList<String> koyler = new ArrayList<String>();
		for(String k : plugin.getConfig().getKeys(false)) {
			koyler.add(k);
		}
		return koyler;
	}
	public String sahibinKöyününİsmi(Player p) {
		ArrayList<String> köyler = köyİsimleriniDöndür();
		for(String a : köyler) {
			String uuid = plugin.getConfig().getString( 	a+".koysahibi-id");
			if(p.getUniqueId().toString().equals(uuid)) {
				return a;
			}
		}
		return null;
	}
	public String üyeninKöyününİsmi(Player p) {
		ArrayList<String> köyler = köyİsimleriniDöndür();
		for(String a : köyler) {
			List<String> üyeİsimleri = plugin.getConfig().getStringList( a+".üyeler");
			if(üyeİsimleri.contains(p.getName())) return a;
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
	public void köyİsmiDeğiştir(String öncekiİsmi,String yeniİsmi,String sahipİsmi,String sahipUUID,List<String> oyuncular) {
		for(String a : köyİsimleriniDöndür()) {
			if(a.equals(öncekiİsmi)) {
				plugin.getConfig().set(a, null);
				plugin.getConfig().set( yeniİsmi+".koysahibi-ismi", sahipİsmi);
				plugin.getConfig().set( yeniİsmi+".koysahibi-id",sahipUUID);
				plugin.getConfig().set(yeniİsmi+".üyeler", oyuncular);
			}
		}
	}
}

