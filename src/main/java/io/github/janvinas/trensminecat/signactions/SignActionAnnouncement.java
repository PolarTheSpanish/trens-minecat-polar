package io.github.janvinas.trensminecat.signactions;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import io.github.janvinas.trensminecat.TrensMinecat;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.Normalizer;
import java.util.HashMap;


public class SignActionAnnouncement extends SignAction {
    @Override
    public boolean match(SignActionEvent info) {
        return info.isType("pas");
    }

    @Override
    public boolean canSupportRC() {
        return false;
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        if (!event.isType("pas")) {
            return false;
        }

        return SignBuildOptions.create()
                .setName("pas")
                .setDescription("Play a public address system depending of the train service")
                .handle(event.getPlayer());
    }

    @Override
    public void execute(SignActionEvent info) {
        if (info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)) {
            if (!info.isPowered()) return;
            MinecartGroup group = info.getGroup();
            playSound(group, info);
        }
    }

    public static void playSound(MinecartGroup group, SignActionEvent info) {
        JavaPlugin plugin = TrensMinecat.getPlugin(TrensMinecat.class);
        World world = group.getWorld();

        String prefix = "";
        Double[] coordsDef = new Double[3];
        float volum;

        String[] coords = info.getLine(2).split(" ");
        //coords[0] = x | coords[1] = y | coords[2] = z

        String[] extra = info.getLine(3).split(" ");
        //extra[0] = prefix | extra[1] = codi de estació | extra[2] = via | extra[3] = volum

        if (extra[0].equalsIgnoreCase("rod")){
            prefix = "meg_rodalies.";
        } else if (extra[0].equalsIgnoreCase("adif")){
            prefix = "meg_adif.";
        } else if (extra[0].equalsIgnoreCase("fgc")){
            prefix = "meg_fgc.";
        }

        try {
            coordsDef[0] = Double.parseDouble(coords[0]);
            coordsDef[1] = Double.parseDouble(coords[1]);
            coordsDef[2] = Double.parseDouble(coords[2]);
        } catch (NullPointerException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            coordsDef[0] = null;
            coordsDef[1] = null;
            coordsDef[2] = null;
        }

        try {
            volum = Float.parseFloat(extra[3]);
        } catch (NullPointerException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            volum = 1.0F;
        }

        Location location;
        if (coordsDef[0] == null) {
            location = group.get(0).getBlock().getLocation();
        } else {
            location = new Location(world, coordsDef[0], coordsDef[1], coordsDef[2]);
        }

        Location finalLocation = location;
        String finalPrefix = prefix;
        float finalVolum = volum;
        long temps = 0L;
        TrainProperties propietats = group.getProperties();

        if (!propietats.matchTag("-IGNORE-")) {
            if (propietats.matchTag(extra[1])) {
                if (!propietats.matchTag("R1") && !propietats.matchTag("R2") && !propietats.matchTag("R3") && !propietats.matchTag("R4") && !propietats.matchTag("R7") && !propietats.matchTag("R8") && !propietats.matchTag("R10") && !propietats.matchTag("RG1") && !propietats.matchTag("RT1") && !propietats.matchTag("RT2")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "proxima_circulacio_per_via", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "proxima_circulacio_per_via"));
                    temps = temps + 25L;
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero." + extra[2], finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero." + extra[2]));
                    if (propietats.matchTag("-RODALIES-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "prefix.rodalies", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "prefix.rodalies"));
                    else if (propietats.matchTag("-REGIONAL-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "prefix.regional", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "prefix.regional"));
                    else if (propietats.matchTag("-REGEXP-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "prefix.regional_expres", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "prefix.regional_expres"));
                    else if (propietats.matchTag("-CATEXP-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "prefix.catalunya_expres", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "prefix.catalunya_expres"));
                    else if (propietats.matchTag("-MD-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "prefix.media_distancia", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "prefix.media_distancia"));
                    else if (propietats.matchTag("-IC-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "prefix.intercity", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "prefix.intercity"));
                    else
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "prefix.tren", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "prefix.tren"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "amb_destinacio", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "amb_destinacio"));
                    temps = temps + 10L;
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName(propietats.getDestination()), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName(propietats.getDestination())));
                } else {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "lletra.r", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "lletra.r"));
                    if (propietats.matchTag("RT1") || propietats.matchTag("RT2")) {
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "lletra.t", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "lletra.t"));
                        if (propietats.matchTag("RT1"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero.1", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero.1"));
                        else if (propietats.matchTag("RT2"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero.2", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero.2"));
                    } else if (propietats.matchTag("RG1")) {
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "lletra.g", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "lletra.g"));
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero.1", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero.1"));
                    } else {
                        if (propietats.matchTag("R1"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero.1", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero.1"));
                        if (propietats.matchTag("R2"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero.2", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero.2"));
                        if (propietats.matchTag("R3"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero.3", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero.3"));
                        if (propietats.matchTag("R4"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero.4", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero.4"));
                        if (propietats.matchTag("R7"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero.7", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero.7"));
                        if (propietats.matchTag("R8"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero.8", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero.8"));
                        if (propietats.matchTag("R10"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero.10", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero.10"));
                    }
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName(propietats.getDestination()), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName(propietats.getDestination())));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "via", finalVolum, 1.0F), temps = temps + (sons.get(finalPrefix + "cat." + "via") + sons.get(finalPrefix + "estacio." + adaptStationName(propietats.getDestination()))));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "numero." + extra[2], finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero." + extra[2]));
                }

                //anotacions
                if (propietats.matchTag("R2A") || propietats.matchTag("R3B") || propietats.matchTag("R2C") || propietats.matchTag("R4C") || propietats.matchTag("R4D") || propietats.matchTag("R11B") || propietats.matchTag("R13B") || propietats.matchTag("R14B") || propietats.matchTag("R16A") || propietats.matchTag("R16C")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "anotacions.para_a_totes_les_estacions_excepte", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "anotacions.para_a_totes_les_estacions_excepte"));
                }

                temps = temps + 58L;
                if (propietats.matchTag("R2A")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("El Prat de Llobregat"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("El Prat de Llobregat")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Bellvitge"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Bellvitge")));
                } else if (propietats.matchTag("R3B") || propietats.matchTag("R3C")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Franqueses del Vallès"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Franqueses del Vallès")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Sant Martí de Centelles"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Sant Martí de Centelles")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Balenyà"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Balenyà")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Borgonyà"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Borgonyà")));
                } else if (propietats.matchTag("R4C")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Lavern - Subirats"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Lavern - Subirats")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Castellbell i el Vilar"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Castellbell i el Vilar")));
                } else if (propietats.matchTag("R4D")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Montcada i Reixac - Santa María"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Montcada i Reixac - Santa María")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Cerdanyola del Vallès"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Cerdanyola del Vallès")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Barbera del Vallès"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Barbera del Vallès")));
                } else if (propietats.matchTag("R11B")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Gualba"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Gualba")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Hostalric"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Hostalric")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Bordils - Juià"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Bordils - Juià")));
                } else if (propietats.matchTag("R13B") || propietats.matchTag("R14B")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Sitges"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Sitges")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Cunit"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Cunit")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Salomó"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Salomó")));
                    if (propietats.matchTag("R13B"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("La Floresta"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("La Floresta")));
                    if (propietats.matchTag("R14B"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("La Selva del Camp"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("La Selva del Camp")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Puigverd de Lleida - Artesa de Lleida"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Puigverd de Lleida - Artesa de Lleida")));
                } else if (propietats.matchTag("R16A")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Vilanova i la Geltrú"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Vilanova i la Geltrú")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Altafulla - Tamarit"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Altafulla - Tamarit")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Vila-seca"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Vila-seca")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Camp-redó"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Camp-redó")));
                } else if (propietats.matchTag("R16C")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Camp-redó"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Camp-redó")));
                }

                //TODO: INCLOURE ESTACIONS DE RODALIA VALÈNCIA
                //TODO: INCLOURE ESTACIONS DE ALTA VELOCITAT

                if (!propietats.matchTag("R1") && !propietats.matchTag("R2") && !propietats.matchTag("R3") && !propietats.matchTag("R4") && !propietats.matchTag("R7") && !propietats.matchTag("R8") && !propietats.matchTag("R10"))
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "anotacions.per_aquest_tren_no_son_valids_els_bitllets_de_rodalies", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "anotacions.per_aquest_tren_no_son_valids_els_bitllets_de_rodalies"));
                if (propietats.matchTag("-PMR-"))
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "anotacions.tren_accessible", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "anotacions.tren_accessible"));
                if (!(propietats.size() <= 4))
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "cat." + "anotacions.tren_curt", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "anotacions.tren_curt"));

                ////////////////////////////////////// EN CASTELLANO //////////////////////////////////////
                temps = temps + 100L;
                if (!propietats.matchTag("R1") && !propietats.matchTag("R2") && !propietats.matchTag("R3") && !propietats.matchTag("R4") && !propietats.matchTag("R7") && !propietats.matchTag("R8") && !propietats.matchTag("R10") && !propietats.matchTag("RG1") && !propietats.matchTag("RT1") && !propietats.matchTag("RT2")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "proxima_circulacio_per_via", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "proxima_circulacio_per_via"));
                    temps = temps + 25L;
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero." + extra[2], finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "numero." + extra[2]));
                    if (propietats.matchTag("-RODALIES-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "prefix.rodalies", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "prefix.rodalies"));
                    else if (propietats.matchTag("-REGIONAL-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "prefix.regional", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "prefix.regional"));
                    else if (propietats.matchTag("-REGEXP-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "prefix.regional_expres", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "prefix.regional_expres"));
                    else if (propietats.matchTag("-CATEXP-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "prefix.catalunya_expres", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "prefix.catalunya_expres"));
                    else if (propietats.matchTag("-MD-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "prefix.media_distancia", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "prefix.media_distancia"));
                    else if (propietats.matchTag("-IC-"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "prefix.intercity", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "prefix.intercity"));
                    else
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "prefix.tren", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "prefix.tren"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "amb_destinacio", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "amb_destinacio"));
                    temps = temps + 10L;
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName(propietats.getDestination()), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName(propietats.getDestination())));
                } else {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "lletra.r", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "lletra.r"));
                    if (propietats.matchTag("RT1") || propietats.matchTag("RT2")) {
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "lletra.t", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "lletra.t"));
                        if (propietats.matchTag("RT1"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero.1", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "numero.1"));
                        else if (propietats.matchTag("RT2"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero.2", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "cat." + "numero.2"));
                    } else if (propietats.matchTag("RG1")) {
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "lletra.g", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "lletra.g"));
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero.1", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "numero.1"));
                    } else {
                        if (propietats.matchTag("R1"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero.1", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "numero.1"));
                        if (propietats.matchTag("R2"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero.2", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "numero.2"));
                        if (propietats.matchTag("R3"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero.3", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "numero.3"));
                        if (propietats.matchTag("R4"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero.4", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "numero.4"));
                        if (propietats.matchTag("R7"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero.7", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "numero.7"));
                        if (propietats.matchTag("R8"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero.8", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "numero.8"));
                        if (propietats.matchTag("R10"))
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero.10", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "numero.10"));
                    }
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName(propietats.getDestination()), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName(propietats.getDestination())));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "via", finalVolum, 1.0F), temps = temps + (sons.get(finalPrefix + "es." + "via") + sons.get(finalPrefix + "estacio." + adaptStationName(propietats.getDestination()))));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "numero." + extra[2], finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "numero." + extra[2]));
                }

                //anotacions
                if (propietats.matchTag("R2A") || propietats.matchTag("R3B") || propietats.matchTag("R2C") || propietats.matchTag("R4C") || propietats.matchTag("R4D") || propietats.matchTag("R11B") || propietats.matchTag("R13B") || propietats.matchTag("R14B") || propietats.matchTag("R16A") || propietats.matchTag("R16C")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "anotacions.para_a_totes_les_estacions_excepte", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "anotacions.para_a_totes_les_estacions_excepte"));
                }

                temps = temps + 58L;
                if (propietats.matchTag("R2A")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("El Prat de Llobregat"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("El Prat de Llobregat")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Bellvitge"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Bellvitge")));
                } else if (propietats.matchTag("R3B") || propietats.matchTag("R3C")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Franqueses del Vallès"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Franqueses del Vallès")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Sant Martí de Centelles"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Sant Martí de Centelles")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Balenyà"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Balenyà")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Borgonyà"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Borgonyà")));
                } else if (propietats.matchTag("R4C")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Lavern - Subirats"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Lavern - Subirats")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Castellbell i el Vilar"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Castellbell i el Vilar")));
                } else if (propietats.matchTag("R4D")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Montcada i Reixac - Santa María"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Montcada i Reixac - Santa María")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Cerdanyola del Vallès"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Cerdanyola del Vallès")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Barbera del Vallès"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Barbera del Vallès")));
                } else if (propietats.matchTag("R11B")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Gualba"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Gualba")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Hostalric"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Hostalric")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Bordils - Juià"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Bordils - Juià")));
                } else if (propietats.matchTag("R13B") || propietats.matchTag("R14B")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Sitges"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Sitges")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Cunit"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Cunit")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Salomó"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Salomó")));
                    if (propietats.matchTag("R13B"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("La Floresta"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("La Floresta")));
                    if (propietats.matchTag("R14B"))
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("La Selva del Camp"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("La Selva del Camp")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Puigverd de Lleida - Artesa de Lleida"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Puigverd de Lleida - Artesa de Lleida")));
                } else if (propietats.matchTag("R16A")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Vilanova i la Geltrú"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Vilanova i la Geltrú")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Altafulla - Tamarit"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Altafulla - Tamarit")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Vila-seca"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Vila-seca")));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "i", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "i"));
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Camp-redó"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Camp-redó")));
                } else if (propietats.matchTag("R16C")) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "estacio." + adaptStationName("Camp-redó"), finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "estacio." + adaptStationName("Camp-redó")));
                }

                if (!propietats.matchTag("R1") && !propietats.matchTag("R2") && !propietats.matchTag("R3") && !propietats.matchTag("R4") && !propietats.matchTag("R7") && !propietats.matchTag("R8") && !propietats.matchTag("R10"))
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "anotacions.per_aquest_tren_no_son_valids_els_bitllets_de_rodalies", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "anotacions.per_aquest_tren_no_son_valids_els_bitllets_de_rodalies"));
                if (propietats.matchTag("-PMR-"))
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "anotacions.tren_accessible", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "anotacions.tren_accessible"));
                if (!(propietats.size() <= 4))
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> world.playSound(finalLocation, finalPrefix + "es." + "anotacions.tren_curt", finalVolum, 1.0F), temps = temps + sons.get(finalPrefix + "es." + "anotacions.tren_curt"));
            } else {
                world.playSound(finalLocation, finalPrefix + "tren_sense_parada_original", finalVolum, 1.0F);
            }
        }
    }

    public static String adaptStationName(String station){
        station = Normalizer.normalize(station, Normalizer.Form.NFD);
        station = station.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase()
                .replace(' ', '_')
                .replace('-', '_')
                .replace('–', '_')
                .replace('‒', '_')
                .replace('—', '_')
                .replace('―', '_')
                .replace('·', '_')
                .replace('\'', '_')
                .replace("sta.", "santa")
                .replace("est.", "estacio")
                .replace("av.", "avinguda")
                .replace("pg.", "passeig")
                .replace("pl.", "placa")
                .replace("st.", "sant")
                .replace(".", "");
        return station;
    }

    public static HashMap<String, Long> sons = new HashMap<>() {{
        put("meg_rodalies.null", 2L);
        put("meg_rodalies.tren_sense_parada_original", 292L);

        //Català
        put("meg_rodalies.cat.es_a_la_via", 18L);
        put("meg_rodalies.cat.amb_destinacio", 24L);
        put("meg_rodalies.cat.proxima_circulacio_per_via", 40L);
        put("meg_rodalies.cat.tren_sense_parada", 140L);
        put("meg_rodalies.cat.via", 15L);
        put("meg_rodalies.cat.lletra.r", 10L);
        put("meg_rodalies.cat.lletra.t", 8L);
        put("meg_rodalies.cat.lletra.g", 10L);
        put("meg_rodalies.cat.prefix.catalunya_expres", 28L);
        put("meg_rodalies.cat.prefix.intercity", 20L);
        put("meg_rodalies.cat.prefix.media_distancia", 28L);
        put("meg_rodalies.cat.prefix.regional", 19L);
        put("meg_rodalies.cat.prefix.regional_expres", 29L);
        put("meg_rodalies.cat.prefix.rodalies", 22L);
        put("meg_rodalies.cat.prefix.tren", 9L);
        put("meg_rodalies.cat.anotacions.para_a", 16L);
        put("meg_rodalies.cat.anotacions.para_a_totes_les_estacions", 37L);
        put("meg_rodalies.cat.anotacions.sense_parada_a_totes_les_estacions", 274L);
        put("meg_rodalies.cat.anotacions.para_a_totes_les_estacions_excepte", 55L);
        put("meg_rodalies.cat.anotacions.per_la_via_de", 20L);
        put("meg_rodalies.cat.anotacions.per_aquest_tren_no_son_valids_els_bitllets_de_rodalies", 70L);
        put("meg_rodalies.cat.anotacions.tren_accessible", 29L);
        put("meg_rodalies.cat.anotacions.tren_curt", 19L);
        put("meg_rodalies.cat.numero.0", 13L);
        put("meg_rodalies.cat.numero.1", 9L);
        put("meg_rodalies.cat.numero.2", 13L);
        put("meg_rodalies.cat.numero.3", 11L);
        put("meg_rodalies.cat.numero.4", 11L);
        put("meg_rodalies.cat.numero.5", 13L);
        put("meg_rodalies.cat.numero.6", 12L);
        put("meg_rodalies.cat.numero.7", 11L);
        put("meg_rodalies.cat.numero.8", 10L);
        put("meg_rodalies.cat.numero.9", 11L);
        put("meg_rodalies.cat.numero.10", 10L);
        put("meg_rodalies.cat.numero.11", 12L);
        put("meg_rodalies.cat.numero.12", 13L);
        put("meg_rodalies.cat.numero.13", 16L);
        put("meg_rodalies.cat.numero.14", 15L);
        put("meg_rodalies.cat.numero.15", 13L);
        put("meg_rodalies.cat.numero.16", 15L);
        put("meg_rodalies.cat.numero.17", 14L);
        put("meg_rodalies.cat.numero.18", 14L);
        put("meg_rodalies.cat.numero.19", 16L);
        put("meg_rodalies.cat.numero.20", 13L);
        put("meg_rodalies.cat.i", 12L);
        put("meg_rodalies.cat.error.dades_desconegudes_1", 130L);
        put("meg_rodalies.cat.error.dades_desconegudes_2", 110L);
        put("meg_rodalies.cat.error.disculpes", 50L);

        //Español
        put("meg_rodalies.es.es_a_la_via", 30L);
        put("meg_rodalies.es.amb_destinacio", 20L);
        put("meg_rodalies.es.proxima_circulacio_per_via", 40L);
        put("meg_rodalies.es.tren_sense_parada", 144L);
        put("meg_rodalies.es.via", 14L);
        put("meg_rodalies.es.lletra.r", 14L);
        put("meg_rodalies.es.lletra.t", 10L);
        put("meg_rodalies.es.lletra.g", 13L);
        put("meg_rodalies.es.prefix.catalunya_expres", 28L);
        put("meg_rodalies.es.prefix.intercity", 20L);
        put("meg_rodalies.es.prefix.media_distancia", 28L);
        put("meg_rodalies.es.prefix.regional", 21L);
        put("meg_rodalies.es.prefix.regional_expres", 31L);
        put("meg_rodalies.es.prefix.rodalies", 26L);
        put("meg_rodalies.es.prefix.tren", 13L);
        put("meg_rodalies.es.anotacions.para_a", 17L);
        put("meg_rodalies.es.anotacions.para_a_totes_les_estacions", 42L);
        put("meg_rodalies.es.anotacions.sense_parada_a_totes_les_estacions", 240L);
        put("meg_rodalies.es.anotacions.para_a_totes_les_estacions_excepte", 51L);
        put("meg_rodalies.es.anotacions.per_la_via_de", 21L);
        put("meg_rodalies.es.anotacions.per_aquest_tren_no_son_valids_els_bitllets_de_rodalies", 71L);
        put("meg_rodalies.es.anotacions.tren_accessible", 24L);
        put("meg_rodalies.es.anotacions.tren_curt", 20L);
        put("meg_rodalies.es.numero.0", 14L);
        put("meg_rodalies.es.numero.1", 13L);
        put("meg_rodalies.es.numero.2", 14L);
        put("meg_rodalies.es.numero.3", 13L);
        put("meg_rodalies.es.numero.4", 15L);
        put("meg_rodalies.es.numero.5", 17L);
        put("meg_rodalies.es.numero.6", 17L);
        put("meg_rodalies.es.numero.7", 17L);
        put("meg_rodalies.es.numero.8", 14L);
        put("meg_rodalies.es.numero.9", 17L);
        put("meg_rodalies.es.numero.10", 17L);
        put("meg_rodalies.es.numero.11", 14L);
        put("meg_rodalies.es.numero.12", 17L);
        put("meg_rodalies.es.numero.13", 14L);
        put("meg_rodalies.es.numero.14", 17L);
        put("meg_rodalies.es.numero.15", 16L);
        put("meg_rodalies.es.numero.16", 23L);
        put("meg_rodalies.es.numero.17", 24L);
        put("meg_rodalies.es.numero.18", 24L);
        put("meg_rodalies.es.numero.19", 24L);
        put("meg_rodalies.es.numero.20", 19L);
        put("meg_rodalies.es.i", 12L);
        put("meg_rodalies.es.error.dades_desconegudes_1", 130L);
        put("meg_rodalies.es.error.dades_desconegudes_2", 90L);
        put("meg_rodalies.es.error.disculpes", 40L);

        //Estacions
        put("meg_rodalies.estacio.aeroport_del_prat", 28L);
        put("meg_rodalies.estacio.aeroport", 28L);
        put("meg_rodalies.estacio.alcover", 17L);
        put("meg_rodalies.estacio.altafulla_tamarit", 28L);
        put("meg_rodalies.estacio.altafulla___tamarit", 28L);
        put("meg_rodalies.estacio.arenys_de_mar", 24L);
        put("meg_rodalies.estacio.badalona", 16L);
        put("meg_rodalies.estacio.balenya", 16L);
        put("meg_rodalies.estacio.barbera_del_valles", 32L);
        put("meg_rodalies.estacio.arc_de_triomf", 40L);
        put("meg_rodalies.estacio.barcelona_arc_de_triomf", 40L);
        put("meg_rodalies.estacio.barcelona___arc_de_triomf", 40L);
        put("meg_rodalies.estacio.estacio_de_franca", 44L);
        put("meg_rodalies.estacio.barcelona_estacio_de_franca", 44L);
        put("meg_rodalies.estacio.barcelona___estacio_de_franca", 44L);
        put("meg_rodalies.estacio.pg_de_gracia", 41L);
        put("meg_rodalies.estacio.passeig_de_gracia", 41L);
        put("meg_rodalies.estacio.barcelona_pg_de_gracia", 41L);
        put("meg_rodalies.estacio.barcelona___pg_de_gracia", 41L);
        put("meg_rodalies.estacio.barcelona_passeig_de_gracia", 41L);
        put("meg_rodalies.estacio.barcelona___passeig_de_gracia", 41L);
        put("meg_rodalies.estacio.placa_catalunya", 41L);
        put("meg_rodalies.estacio.barcelona_placa_catalunya", 41L);
        put("meg_rodalies.estacio.barcelona___placa_catalunya", 41L);
        put("meg_rodalies.estacio.sants", 27L);
        put("meg_rodalies.estacio.barcelona_sants", 27L);
        put("meg_rodalies.estacio.barcelona___sants", 27L);
        put("meg_rodalies.estacio.bellvitge", 17L);
        put("meg_rodalies.estacio.blanes", 16L);
        put("meg_rodalies.estacio.bordils_juia", 24L);
        put("meg_rodalies.estacio.bordils___juia", 24L);
        put("meg_rodalies.estacio.borgonya", 16L);
        put("meg_rodalies.estacio.calella", 14L);
        put("meg_rodalies.estacio.cambrils", 19L);
        put("meg_rodalies.estacio.camp_redo", 19L);
        put("meg_rodalies.estacio.camp___redo", 19L);
        put("meg_rodalies.estacio.castellbell_el_vilar", 32L);
        put("meg_rodalies.estacio.castellbell___el_vilar", 32L);
        put("meg_rodalies.estacio.castellbell_i_el_vilar", 32L);
        put("meg_rodalies.estacio.cerbere", 18L);
        put("meg_rodalies.estacio.cerdanyola_del_valles", 36L);
        put("meg_rodalies.estacio.cerdanyola_universitat", 39L);
        put("meg_rodalies.estacio.cunit", 14L);
        put("meg_rodalies.estacio.clot_arago", 22L);
        put("meg_rodalies.estacio.el_clot_arago", 22L);
        put("meg_rodalies.estacio.barcelona_el_clot_arago", 22L);
        put("meg_rodalies.estacio.barcelona___el_clot_arago", 22L);
        put("meg_rodalies.estacio.barcelona_clot_arago", 22L);
        put("meg_rodalies.estacio.barcelona___clot_arago", 22L);
        put("meg_rodalies.estacio.el_prat", 28L);
        put("meg_rodalies.estacio.barcelona_el_prat", 28L);
        put("meg_rodalies.estacio.barcelona___el_prat", 28L);
        put("meg_rodalies.estacio.el_prat_de_llobregat", 28L);
        put("meg_rodalies.estacio.barcelona_el_prat_de_llobregat", 28L);
        put("meg_rodalies.estacio.barcelona___el_prat_de_llobregat", 28L);
        put("meg_rodalies.estacio.figueres", 18L);
        put("meg_rodalies.estacio.flaca", 16L);
        put("meg_rodalies.estacio.les_franqueses_del_valles", 33L);
        put("meg_rodalies.estacio.franqueses_del_valles", 33L);
        put("meg_rodalies.estacio.gelida", 18L);
        put("meg_rodalies.estacio.girona", 16L);
        put("meg_rodalies.estacio.granollers", 30L);
        put("meg_rodalies.estacio.granollers_centre", 30L);
        put("meg_rodalies.estacio.gualba", 19L);
        put("meg_rodalies.estacio.hospitalet", 35L);
        put("meg_rodalies.estacio.hospitalet_de_llobregat", 35L);
        put("meg_rodalies.estacio.l_hospitalet", 35L);
        put("meg_rodalies.estacio.l_hospitalet_de_llobregat", 35L);
        put("meg_rodalies.estacio.hostalric", 19L);
        put("meg_rodalies.estacio.aldea_amposta_tortosa", 39L);
        put("meg_rodalies.estacio.aldea___amposta___tortosa", 39L);
        put("meg_rodalies.estacio.l_aldea_amposta_tortosa", 39L);
        put("meg_rodalies.estacio.l_aldea___amposta___tortosa", 39L);
        put("meg_rodalies.estacio.l_arboc", 19L);
        put("meg_rodalies.estacio.la_floresta", 20L);
        put("meg_rodalies.estacio.la_molina", 23L);
        put("meg_rodalies.estacio.la_plana_picamoixons", 35L);
        put("meg_rodalies.estacio.la_plana___picamoixons", 35L);
        put("meg_rodalies.estacio.la_selva_del_camp", 32L);
        put("meg_rodalies.estacio.la_tor_de_querol_enveitg", 25L);
        put("meg_rodalies.estacio.la_tor_de_querol___enveitg", 25L);
        put("meg_rodalies.estacio.la_tor_de_querol", 25L);
        put("meg_rodalies.estacio.latour_de_carol_enveitg", 25L);
        put("meg_rodalies.estacio.latour_de_carol___enveitg", 25L);
        put("meg_rodalies.estacio.latour_de_carol", 25L);
        put("meg_rodalies.estacio.lavern_subirats", 29L);
        put("meg_rodalies.estacio.lavern___subirats", 29L);
        put("meg_rodalies.estacio.les_borges_blanques", 33L);
        put("meg_rodalies.estacio.lleida_pirineus", 28L);
        put("meg_rodalies.estacio.lleida___pirineus", 28L);
        put("meg_rodalies.estacio.lleida", 28L);
        put("meg_rodalies.estacio.macanet___macannes", 29L);
        put("meg_rodalies.estacio.macanet_macannes", 29L);
        put("meg_rodalies.estacio.macanet", 29L);
        put("meg_rodalies.estacio.manresa", 20L);
        put("meg_rodalies.estacio.martorell", 18L);
        put("meg_rodalies.estacio.mataro", 15L);
        put("meg_rodalies.estacio.molins_de_rei", 24L);
        put("meg_rodalies.estacio.montblanc", 19L);
        put("meg_rodalies.estacio.montcada_i_reixac", 19L);
        put("meg_rodalies.estacio.montcada_i_reixac_santa_maria", 54L);
        put("meg_rodalies.estacio.montcada_i_reixac___santa_maria", 54L);
        put("meg_rodalies.estacio.montmelo", 19L);
        put("meg_rodalies.estacio.portbou", 16L);
        put("meg_rodalies.estacio.puigcerda", 17L);
        put("meg_rodalies.estacio.puigverd_de_lleida_artesa_de_lleida", 49L);
        put("meg_rodalies.estacio.puigverd_de_lleida___artesa_de_lleida", 49L);
        put("meg_rodalies.estacio.reus", 17L);
        put("meg_rodalies.estacio.reus_bellissens", 34L);
        put("meg_rodalies.estacio.reus___bellissens", 34L);
        put("meg_rodalies.estacio.ribes_de_freser", 28L);
        put("meg_rodalies.estacio.ripoll", 17L);
        put("meg_rodalies.estacio.rubi", 15L);
        put("meg_rodalies.estacio.sabadell_centre", 29L);
        put("meg_rodalies.estacio.sabadell_nord", 24L);
        put("meg_rodalies.estacio.salomo", 17L);
        put("meg_rodalies.estacio.portaventura", 34L);
        put("meg_rodalies.estacio.port_aventura", 34L);
        put("meg_rodalies.estacio.salou_portaventura", 34L);
        put("meg_rodalies.estacio.salou___portaventura", 34L);
        put("meg_rodalies.estacio.salou_port_aventura", 34L);
        put("meg_rodalies.estacio.salou___port_aventura", 34L);
        put("meg_rodalies.estacio.sant_celoni", 24L);
        put("meg_rodalies.estacio.sant_cugat_del_valles", 32L);
        put("meg_rodalies.estacio.sant_marti_de_centelles", 38L);
        put("meg_rodalies.estacio.sant_pol_de_mar", 28L);
        put("meg_rodalies.estacio.sant_sadurni_d_anoia", 33L);
        put("meg_rodalies.estacio.sant_vicenc_de_calders", 36L);
        put("meg_rodalies.estacio.santa_susanna", 22L);
        put("meg_rodalies.estacio.sitges", 16L);
        put("meg_rodalies.estacio.tarragona", 20L);
        put("meg_rodalies.estacio.terrassa", 15L);
        put("meg_rodalies.estacio.tordera", 14L);
        put("meg_rodalies.estacio.tortosa", 19L);
        put("meg_rodalies.estacio.ulldecona_alcanar_la_senia", 42L);
        put("meg_rodalies.estacio.ulldecona___alcanar___la_senia", 42L);
        put("meg_rodalies.estacio.ulldecona", 42L);
        put("meg_rodalies.estacio.valls", 15L);
        put("meg_rodalies.estacio.vic", 12L);
        put("meg_rodalies.estacio.vilaseca", 20L);
        put("meg_rodalies.estacio.vila_seca", 20L);
        put("meg_rodalies.estacio.vila___seca", 20L);
        put("meg_rodalies.estacio.vilafranca", 38L);
        put("meg_rodalies.estacio.vilafranca_del_penedes", 38L);
        put("meg_rodalies.estacio.vilanova", 35L);
        put("meg_rodalies.estacio.vilanova___la_geltru", 35L);
        put("meg_rodalies.estacio.vilanova_i_la_geltru", 35L);
        put("meg_rodalies.estacio.vinaixa", 15L);
    }};
}
