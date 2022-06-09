package io.github.janvinas.trensminecat;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class ManualDisplays {

    static String imgDir = "img/";

    public static class ManualDisplay1 extends ManualDisplay {
        String type = "_orange/";
        boolean updateTime = true;
        String brand;

        public boolean updateInformation(String displayID, String via, MinecartGroup dadesTren, Integer clearIn){
            if (via != null) {
                displayID = displayID + via;
            }

            String codiParada = displayID.replaceAll("[0-9]","");

            if(! properties.get("ID", String.class).equals(displayID)) return false;

            brand = properties.get("brand", String.class, "rodalies"); //si no s'ha especificat una marca, retorna rodalies.

            if (Objects.equals(brand, "rodalies")){
                type = "_orange/";
            } else if (Objects.equals(brand, "renfe")){
                type = "_white/";
            }

            String trainLine;
            String dest;
            if(!dadesTren.getProperties().matchTag(codiParada)){
                trainLine = "info";
                dest = "TREN SENSE PARADA";
            } else {
                trainLine = BoardUtils.getTrainLine(dadesTren.getProperties().getTrainName());
                dest = dadesTren.getProperties().getDestination().toUpperCase();
                if(dest.length() == 0) dest = dadesTren.getProperties().getDisplayName();
            }

            getLayer(5).clear();
            getLayer(4).clear();
            MapTexture lineIcon;
            try{
                lineIcon = Assets.getMapTexture(imgDir + "28px" + type + trainLine + ".png");
            } catch(MapTexture.TextureLoadException e) {
                dest = dadesTren.getProperties().getDisplayName();
                lineIcon = Assets.getMapTexture(imgDir + "28px" + type + "what.png");
            }
            getLayer(4).draw(lineIcon, 5, 14);

            //print the destination, wrapping the lines and centering it vertically and horizontally.
            /*BufferedImage destinationText = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = destinationText.createGraphics();
            g.setColor(new Color(255, 255, 255));
            g.setFont(TrensMinecat.minecraftiaJavaFont);

            AttributedString attributedString = new AttributedString(dest);
            LineBreakMeasurer lineBreakMeasurer = new LineBreakMeasurer(attributedString.getIterator(), g.getFontRenderContext());
            int lineCount = 0;
            final int lineSpacing = 11;
            while(lineBreakMeasurer.getPosition() < attributedString.getIterator().getEndIndex()){
                lineBreakMeasurer.nextLayout(100);
                lineCount++;
            }
            //center at 72, 22
            int i = 0;
            while(lineBreakMeasurer.getPosition() < attributedString.getIterator().getRunLimit()){
                TextLayout textLayout = lineBreakMeasurer.nextLayout(100);
                double width = textLayout.getBounds().getWidth();
                //textLayout.draw(g, (float) (72F - width/2F), 22 - lineSpacing*lineCount/2F + 11*i);
                textLayout.draw(g, 30, 30);
                i++;
            }
            g.dispose();
            getLayer(4).draw(MapTexture.fromImage(destinationText), 0, 0);
            */

            BufferedImage destinationText = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = destinationText.createGraphics();
            if (Objects.equals(brand, "rodalies")) {
                g.setColor(new Color(252, 76, 0));
            } else {
                g.setColor(new Color(255, 255, 255));
            }
            g.setFont(TrensMinecat.minecraftiaJavaFont);
            int offset = g.getFontMetrics().stringWidth(dest) / 2;
            g.drawString(dest, 77 - offset, 37);
            g.dispose();
            getLayer(4).draw(MapTexture.fromImage(destinationText), 0, 0);

            updateTime = false;

            if(clearIn != 0){
                getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                        this.clearInformation(properties.get("ID", String.class)), clearIn * 20L);
            }

            return true;
        }

        public boolean clearInformation(String displayID){
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            getLayer(4).clear();
            getLayer(4).draw(Assets.getMapTexture(imgDir + "28px" + type + brand + ".png"), 5, 14);
            getLayer(5).clear();

            updateTime = true;
            return true;
        }

        @Override
        public void onAttached() {
            super.onAttached();
            setUpdateWithoutViewers(false);
            brand = properties.get("brand", String.class, "rodalies"); //si no s'ha especificat una marca, retorna rodalies.
            getLayer(1).clear();
            getLayer(1).fillRectangle(5, 14, 118, 28, MapColorPalette.getColor(40, 40, 40));
            getLayer(3).clear();
            getLayer(3).draw(Assets.getMapTexture(imgDir + "DepartureBoard3.png"), 0, 0);
            getLayer(4).clear();
            getLayer(4).draw(Assets.getMapTexture(imgDir + "28px" + type + brand + ".png"), 5, 14);

        }

        @Override
        public void onTick() {
            super.onTick();
            if(updateTime){
                getLayer(5).clear();
                getLayer(5).setAlignment(MapFont.Alignment.RIGHT);
                LocalDateTime now = LocalDateTime.now();
                if (Objects.equals(brand, "rodalies")) {
                    getLayer(5).draw(MapFont.MINECRAFT, 119, 24, MapColorPalette.getColor(252, 76, 0), now.format(DateTimeFormatter.ofPattern("HH:mm")));
                } else if (Objects.equals(brand, "renfe")){
                    getLayer(5).draw(MapFont.MINECRAFT, 119, 24, MapColorPalette.COLOR_WHITE, now.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
            }
        }
    }

    /*
    public static class ManualDisplay2 extends MapDisplay{
        TreeMap<String, LocalDateTime> lastTrains = new TreeMap<>();
        HashMap<String, Duration> trainIntervals = properties.get("trainIntervals", HashMap.class);
        int tickCount = 0;

        public boolean updateInformation(String displayID, String displayName, String destination){
            if (via != null) {
                displayID = displayID + via;
            }
            String codiParada = displayID.replaceAll("[0-9]","");
            if(! properties.get("ID", String.class).equals(displayID)) return false;
            LocalDateTime now = LocalDateTime.now();
            lastTrains.put(destination, now);
            return true;
        }

        public boolean clearInformation(String displayID){
            return true;
        }

        @Override
        public void onAttached() {
            getLayer(0).draw(loadTexture(imgDir + "DepartureBoard4.png"), 0, 0);
            super.onAttached();
        }

        @Override
        public void onTick() {

            LocalDateTime now = LocalDateTime.now();
            getLayer(2).clear();
            getLayer(2).setAlignment(MapFont.Alignment.MIDDLE);
            getLayer(2).draw(MapFont.MINECRAFT, 227, 7, MapColorPalette.COLOR_BLACK,
                    now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            if( (tickCount % updateTime) == 0){
                int secondsToDisplayOnBoard = TrensMinecat.secondsToDisplayOnBoard;
                TreeMap<LocalDateTime, String> departureBoardTrains = new TreeMap<>();

                for(String train : lastTrains.keySet()){


                    departureBoardTrains.put(lastTrains.get(train).plus(interval), train);  //add first expected train
                    departureBoardTrains.put(lastTrains.get(train).plus(interval.multipliedBy(2)), train); //add the next expected train twice the duration of the first
                }

                //print train lines on screen
                getLayer(1).clear();
                getLayer(1).setAlignment(MapFont.Alignment.LEFT);
                int i = 0;
                for(LocalDateTime departureTime : departureBoardTrains.keySet()){


                    Duration untilDeparture = Duration.between(now, departureTime);
                    if(untilDeparture.minusSeconds(secondsToDisplayOnBoard).isNegative()) {
                        getLayer(1).draw(MapFont.MINECRAFT, 113, 34 + i * 14,
                                MapColorPalette.getColor(255, 0, 0), "imminent");
                    }else if(untilDeparture.minusMinutes(5).isNegative()){
                        getLayer(1).draw(MapFont.MINECRAFT, 113, 34 + i * 14,
                                MapColorPalette.getColor(0, 128, 0),
                                untilDeparture.getSeconds()/60 + "min");
                    }else{
                        getLayer(1).draw(MapFont.MINECRAFT, 113, 34 + i*14,
                                MapColorPalette.getColor(0, 0, 0),
                                departureTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    }
                    //getLayer(1).draw(loadTexture(imgDir + "11px/" +
                            //departureBoardTrains.get(departureTime).name + ".png"), 1, 33 + i*14);

                    String destination = departureBoardTrains.get(departureTime).destination;
                    if(!destination.equals("_")) getLayer(1).draw(MapFont.MINECRAFT, 21, 34 + i*14,
                            MapColorPalette.getColor(0, 0, 0),
                            departureBoardTrains.get(departureTime).destination);
                    String platform = departureBoardTrains.get(departureTime).platform;
                    if(!platform.equals("_")) getLayer(1).draw(MapFont.MINECRAFT, 99, 34 + i*14,
                            MapColorPalette.getColor(0, 0, 0),
                            departureBoardTrains.get(departureTime).platform);
                    String information = departureBoardTrains.get(departureTime).information;
                    if(!information.equals("_")){
                        getLayer(1).draw(MapFont.MINECRAFT, 162, 34 + i*14,
                                MapColorPalette.getColor(0, 0, 0),
                                departureBoardTrains.get(departureTime).information);
                    }

                    i++;
                }
            }

            tickCount++;
            super.onTick();
        }
    }
    */

    public static class ManualDisplay3 extends ManualDisplay{ //pantalla fgc primer tren (2*1)
        String codiParada;
        String liniatren = "";
        String plataforma = "0";
        String[] estacions;
        int tickCount = 0;
        Boolean multiplePantalla = false;
        Boolean seguentPagina = false;
        Boolean senseParada = false;
        Boolean hasFGCTrain = false;
        static Font minecraftiaWide = TrensMinecat.minecraftiaJavaFont;
        static MapFont<Character> minecraftia;

        static {
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.TRACKING, -0.125);
            minecraftia = MapFont.fromJavaFont(minecraftiaWide.deriveFont(attributes).deriveFont(8F));
        }

        @Override
        public void onAttached() {
            super.onAttached();
            setUpdateWithoutViewers(false);
            getLayer(0).clear();
            getLayer(0).draw(Assets.getMapTexture(imgDir + "ManualDisplay3.png"), 0, 0);
        }

        @Override
        public void onTick() {
            super.onTick();

            //layer0: permanent background (never updated)
            //layer1: --
            //layer2: circumstancial background (updated from signs)
            //layer3: text and icons (updated every updateTime seconds)
            //layer4: time (updated every tick)

            LocalTime tempsActual = LocalTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            boolean fiDeServei = (tempsActual.isAfter(LocalTime.parse("02:04")) && tempsActual.isBefore(LocalTime.parse("04:54")));

            if (!fiDeServei) {
                if (hasFGCTrain && !senseParada) {
                    if (multiplePantalla) {
                        if (tickCount % 100 == 0) {
                            seguentPagina = !seguentPagina;
                        }
                    }

                    getLayer(0).clear();
                    getLayer(0).draw(Assets.getMapTexture(imgDir + "displaysVariations/ManualDisplay3/ManualDisplay3B.png"), 0, 0);

                    if (liniatren.contains("RL1")) { //Spawn a Lleida
                        estacions = new String[]{"Lleida", "Alcoletge", "Térmens", "V Balaguer", "Balaguer"};
                        if (Objects.equals(liniatren, "RL1B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("RL2")) { //Spawn a Lleida
                        estacions = new String[]{"Lleida", "Alcoletge", "Térmens", "V Balaguer", "Balaguer", "Gerb", "SL Mongai", "G Tremp", "P Noguera", "Tremp", "Talarn", "Salàs", "P de Segur"};
                        if (Objects.equals(liniatren, "RL2B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("L6")) { //Spawn a Sarrià
                        estacions = new String[] {"Sarrià", "Provença", "Pl. Cat."};
                        if (Objects.equals(liniatren, "L6B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("L7")) { //Spawn a Pl. Catalunya
                        estacions = new String[] {"Pl. Cat.", "Provença", "Av. Tibidabo"};
                        if (Objects.equals(liniatren, "L7B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("L8")) { //Spawn a Molí Nou
                        estacions = new String[] {"Molí Nou", "St. Boi", "Cornellà-R", "Gornal", "Euro.|Fira", "Pl. Esp."};
                        if (Objects.equals(liniatren, "L8B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("L12")) { //Spawn a Reina Elisenda
                        estacions = new String[] {"Reina Elisenda", "Sarrià"};
                        if (Objects.equals(liniatren, "L12B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("S1")) { //Spawn a Terrassa Estació del Nord
                        estacions = new String[] {"TE Nord", "V Univ.", "T Rambla", "Les Fonts", "Rubí", "St. Cugat", "P Funicul.", "Sarrià", "Provença", "Pl. Cat."};
                        if (Objects.equals(liniatren, "S1B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("S2")) { //Spawn a Sabadell Estació del Nord
                        estacions = new String[] {"S Nord", "S Pl. Major", "Can Feu", "UAB", "Volpell.", "St. Cugat", "P Funicul.", "Sarrià", "Provença", "Pl. Cat."};
                        if (Objects.equals(liniatren, "S2B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("S3")) { //Spawn a Sant Esteve Sesrovires
                        estacions = new String[] {"SE Sesrov.", "Martorell", "Molí Nou", "St. Boi", "Cornellà-R", "Gornal", "Euro.|Fira", "Pl. Esp."};
                        if (Objects.equals(liniatren, "S3B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("S4")) { //Spawn a Olesa de Montserrat
                        estacions = new String[] {"Olesa de M", "Martorell", "Molí Nou", "St. Boi", "Cornellà-R", "Gornal", "Euro.|Fira", "Pl. Esp."};
                        if (Objects.equals(liniatren, "S4B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("S5")) { //Spawn a Sant Cugat
                        estacions = new String[] {"St. Cugat", "Sarrià", "Provença", "Pl. Cat."};
                        if (Objects.equals(liniatren, "S5B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("S6")) { //Spawn a Can Feu | Gràcia (Comença Servei a Universitat Autònoma)
                        estacions = new String[] {"UAB", "Volpell.", "St. Cugat", "Sarrià", "Provença", "Pl. Cat."};
                        if (Objects.equals(liniatren, "S6B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("S7")) { //Spawn a Rubí
                        estacions = new String[] {"Rubí", "St. Cugat", "Sarrià", "Provença", "Pl. Cat."};
                        if (Objects.equals(liniatren, "S7B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("S8")) { //Spawn a Pl. Espanya
                        estacions = new String[] {"Pl. Esp.", "Euro.|Fira", "Gornal", "Cornellà-R", "St. Boi", "Molí Nou", "Martorell"};
                        if (Objects.equals(liniatren, "S8B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("R50")) { //Spawn a Manresa-Baixador
                        estacions = new String[] {"M Baixador", "M Viladord.", "Monistrol M", "Olesa M", "Martorell", "St. Boi", "Cornellà-R", "Gornal", "Euro.|Fira", "Pl. Esp."};
                        if (Objects.equals(liniatren, "R50B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("R53")) { //Spawn a Manresa-Baixador
                        estacions = new String[] {"M Baixador", "M Viladord.", "C el Vilar", "Monistrol M", "Olesa M", "Martorell"};
                        if (Objects.equals(liniatren, "R53B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("R5")) { //Spawn a Manresa-Baixador
                        estacions = new String[] {"M Baixador", "M Viladord.", "C el Vilar", "Monistrol M", "Olesa M", "Martorell", "Molí Nou", "St. Boi", "Cornellà-R", "Gornal", "Euro.|Fira", "Pl. Esp."};
                        if (Objects.equals(liniatren, "R5B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("R60")) { //Spawn a Igualada
                        estacions = new String[] {"Igualada", "Martorell", "St. Boi", "Cornellà-R", "Gornal", "Euro.|Fira", "Pl. Esp."};
                        if (Objects.equals(liniatren, "R60B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("R63")) { //Spawn a Igualada
                        estacions = new String[] {"Igualada", "V del Camí", "SE Sesrov.", "Martorell"};
                        if (Objects.equals(liniatren, "R63B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("R6")) { //Spawn a Igualada
                        estacions = new String[] {"Igualada", "V del Camí", "SE Sesrov.", "Martorell", "Molí Nou", "St. Boi", "Cornellà-R", "Gornal", "Euro.|Fira", "Pl. Esp."};
                        if (Objects.equals(liniatren, "R6B")) Collections.reverse(Arrays.asList(estacions));
                    }

                    else if (liniatren.contains("DESC") || liniatren == null){
                        estacions = null;
                    }

                    else {
                        estacions = null;
                    }

                    if (estacions != null) {
                        if (estacions.length > 12 && !multiplePantalla) multiplePantalla = true;
                        getLayer(2).clear();
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 8, 63, MapColorPalette.getColor(0, 0, 0), estacions[0]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 8, 63, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 8, 82, MapColorPalette.getColor(0, 0, 0), estacions[1]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 8, 82, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 8, 99, MapColorPalette.getColor(0, 0, 0), estacions[2]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 8, 99, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 62, 63, MapColorPalette.getColor(0, 0, 0), estacions[3]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 62, 63, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 62, 82, MapColorPalette.getColor(0, 0, 0), estacions[4]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 62, 82, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 62, 99, MapColorPalette.getColor(0, 0, 0), estacions[5]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 62, 99, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 127, 63, MapColorPalette.getColor(0, 0, 0), estacions[6]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 127, 63, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 127, 82, MapColorPalette.getColor(0, 0, 0), estacions[7]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 127, 82, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 127, 99, MapColorPalette.getColor(0, 0, 0), estacions[8]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 127, 99, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 192, 63, MapColorPalette.getColor(0, 0, 0), estacions[9]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 192, 63, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 192, 82, MapColorPalette.getColor(0, 0, 0), estacions[10]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 192, 82, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (!seguentPagina) getLayer(2).draw(minecraftia, 192, 99, MapColorPalette.getColor(0, 0, 0), estacions[11]); } catch (IndexOutOfBoundsException e) { if (!seguentPagina) getLayer(2).draw(minecraftia, 192, 99, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (seguentPagina) getLayer(2).draw(minecraftia, 8, 63, MapColorPalette.getColor(0, 0, 0), estacions[12]); } catch (IndexOutOfBoundsException e) { if (seguentPagina) getLayer(2).draw(minecraftia, 8, 63, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (seguentPagina) getLayer(2).draw(minecraftia, 8, 82, MapColorPalette.getColor(0, 0, 0), estacions[13]); } catch (IndexOutOfBoundsException e) { if (seguentPagina) getLayer(2).draw(minecraftia, 8, 82, MapColorPalette.getColor(0, 0, 0), ""); }
                        try { if (seguentPagina) getLayer(2).draw(minecraftia, 8, 99, MapColorPalette.getColor(0, 0, 0), estacions[14]); } catch (IndexOutOfBoundsException e) { if (seguentPagina) getLayer(2).draw(minecraftia, 8, 99, MapColorPalette.getColor(0, 0, 0), ""); }
                    } else {
                        getLayer(0).clear();
                        getLayer(3).clear();
                        getLayer(0).draw(Assets.getMapTexture(imgDir + "displaysVariations/ManualDisplay3/ManualDisplay3D.png"), 0, 0);
                    }
                }

                else if (senseParada) {
                    getLayer(0).clear();
                    getLayer(3).clear();
                    getLayer(0).draw(Assets.getMapTexture(imgDir + "displaysVariations/ManualDisplay3/ManualDisplay3C.png"), 0, 0);
                }

                else {
                    getLayer(0).clear();
                    getLayer(3).clear();
                    getLayer(0).draw(Assets.getMapTexture(imgDir + "ManualDisplay3.png"), 0, 0);
                }

                if (((liniatren.contains("S1") && Objects.equals(codiParada, "TRSN")) ||
                     (liniatren.contains("S2") && Objects.equals(codiParada, "SBN")) ||
                     (liniatren.contains("S3") && Objects.equals(codiParada, "SES")) ||
                     (liniatren.contains("S4") && Objects.equals(codiParada, "ODM")) ||
                     (liniatren.contains("S5") && Objects.equals(codiParada, "SCF")) ||
                     (liniatren.contains("S6") && Objects.equals(codiParada, "UAB")) ||
                     (liniatren.contains("S7") && Objects.equals(codiParada, "RBF")) ||
                     (liniatren.contains("S8") && Objects.equals(codiParada, "BCNPE")) ||
                     (liniatren.contains("L6") && Objects.equals(codiParada, "SRA")) ||
                     (liniatren.contains("L7") && Objects.equals(codiParada, "BCNPC")) ||
                     (liniatren.contains("L8") && Objects.equals(codiParada, "MNCC")) ||
                     (liniatren.contains("L12") && Objects.equals(codiParada, "RE")) ||
                     ((liniatren.contains("RL1") || liniatren.contains("RL2")) && Objects.equals(codiParada, "LLD")) ||
                     ((liniatren.contains("R53") || liniatren.contains("R50") || liniatren.contains("R5")) && Objects.equals(codiParada, "MRSB")) ||
                     ((liniatren.contains("R63") || liniatren.contains("R60") || liniatren.contains("R6")) && Objects.equals(codiParada, "IGD"))) && !senseParada) {
                    getLayer(0).clear();
                    getLayer(2).clear();
                    getLayer(3).clear();
                    getLayer(0).draw(Assets.getMapTexture(imgDir + "displaysVariations/ManualDisplay3/ManualDisplay3F.png"), 0, 0);
                }

            } else {
                getLayer(5).clear();
                getLayer(3).clear();
                getLayer(2).clear();
                getLayer(1).clear();
                getLayer(0).clear();
                getLayer(0).draw(Assets.getMapTexture(imgDir + "displaysVariations/ManualDisplay3/ManualDisplay3E.png"), 0, 0);
            }

            tickCount++;
            LocalDateTime now = LocalDateTime.now();
            getLayer(4).clear();
            getLayer(4).setAlignment(MapFont.Alignment.MIDDLE);
            getLayer(4).draw(minecraftia, 28, 10, MapColorPalette.COLOR_WHITE,
                    now.format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        public boolean updateInformation(String displayID, String via, MinecartGroup dadesTren, Integer clearIn){
            if (via != null) {
                displayID = displayID + via;
            }

            if (via == null){
                plataforma = displayID.replaceAll("[^\\d.]", "");
            } else {
                plataforma = via;
            }

            String trainLine;
            String dest = null;

            String codiParada = displayID.replaceAll("[0-9]","");
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            if (dadesTren.getProperties().matchTag("RL1") && dadesTren.getProperties().matchTag("FGC")) {
                trainLine = "RL1";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Balaguer")) liniatren = "RL1A";
                else liniatren = "RL1B";
            }

            else if (dadesTren.getProperties().matchTag("RL2") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "RL2";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pobla de Segur")) liniatren = "RL2A";
                else liniatren = "RL2B";
            }

            else if (dadesTren.getProperties().matchTag("L6") && dadesTren.getProperties().matchTag("FGC")) {
                trainLine = "L6";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Catalunya")) liniatren = "L6A";
                else liniatren = "L6B";
            }

            else if (dadesTren.getProperties().matchTag("L7") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "L7";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Av. Tibidabo")) liniatren = "L7A";
                else liniatren = "L7B";
            }

            else if (dadesTren.getProperties().matchTag("L8") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "L8";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Espanya")) liniatren = "L8A";
                else liniatren = "L8B";
            }

            else if (dadesTren.getProperties().matchTag("L12") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "L12";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Sarrià")) liniatren = "L12A";
                else liniatren = "L12B";
            }

            else if (dadesTren.getProperties().matchTag("R53") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "R53";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Martorell")) liniatren = "R53A";
                else liniatren = "R53B";
            }

            else if (dadesTren.getProperties().matchTag("R50") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "R50";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Espanya")) liniatren = "R50A";
                else liniatren = "R50B";
            }

            else if (dadesTren.getProperties().matchTag("R5") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "R5";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Espanya")) liniatren = "R5A";
                else liniatren = "R5B";
            }

            else if (dadesTren.getProperties().matchTag("R63") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "R63";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Martorell")) liniatren = "R63A";
                else liniatren = "R63B";
            }

            else if (dadesTren.getProperties().matchTag("R60") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "R60";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Espanya")) liniatren = "R60A";
                else liniatren = "R60B";
            }

            else if (dadesTren.getProperties().matchTag("R6") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "R6";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Espanya")) liniatren = "R6A";
                else liniatren = "R6B";
            }

            else if (dadesTren.getProperties().matchTag("S1") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "S1";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Catalunya")) liniatren = "S1A";
                else liniatren = "S1B";
            }

            else if (dadesTren.getProperties().matchTag("S2") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "S2";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Catalunya")) liniatren = "S2A";
                else liniatren = "S2B";
            }

            else if (dadesTren.getProperties().matchTag("S3") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "S3";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Espanya")) liniatren = "S3A";
                else liniatren = "S3B";
            }

            else if (dadesTren.getProperties().matchTag("S4") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "S4";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Espanya")) liniatren = "S4A";
                else liniatren = "S4B";
            }

            else if (dadesTren.getProperties().matchTag("S5") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "S5";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Catalunya")) liniatren = "S5A";
                else liniatren = "S5B";
            }

            else if (dadesTren.getProperties().matchTag("S6") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "S6";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Catalunya")) liniatren = "S6A";
                else liniatren = "S6B";
            }

            else if (dadesTren.getProperties().matchTag("S7") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "S7";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Catalunya")) liniatren = "S7A";
                else liniatren = "S7B";
            }

            else if (dadesTren.getProperties().matchTag("S8") && dadesTren.getProperties().matchTag("FGC")){
                trainLine = "S8";
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Pl. Espanya")) liniatren = "S8A";
                else liniatren = "S8B";
            }

            else {
                trainLine = "what";
                liniatren = "DESC";
            }

            getLayer(2).clear();
            getLayer(3).clear();


            if(!dadesTren.getProperties().matchTag(codiParada)) {
                senseParada = true;
            } else {
                dest = dadesTren.getProperties().getDestination();
            }

            if (!senseParada) {
                hasFGCTrain = true;

                MapTexture lineIcon = Assets.getMapTexture(imgDir + "11px/" + trainLine + ".png");
                if (!(lineIcon.getHeight() > 1)) {
                    dest = dadesTren.getProperties().getDestination();
                    lineIcon = Assets.getMapTexture(imgDir + "11px/what.png");
                }

                //DISPLAY
                getLayer(3).draw(lineIcon, 22, 45);
                getLayer(3).draw(minecraftia, 51, 45, MapColorPalette.COLOR_BLACK, dest);
                getLayer(3).draw(minecraftia, 124, 45, MapColorPalette.COLOR_BLACK, plataforma);
                getLayer(3).draw(minecraftia, 138, 45, MapColorPalette.COLOR_BLACK, LocalDateTime.now().plusSeconds(clearIn).format(DateTimeFormatter.ofPattern("HH:mm")));

                if (liniatren.contains("R50") || liniatren.contains("R60")) {
                    getLayer(3).draw(minecraftia, 176, 45, MapColorPalette.COLOR_BLACK, "Semidirecte");
                } else if (liniatren.contains("S5")) {
                    getLayer(3).draw(minecraftia, 176, 45, MapColorPalette.COLOR_BLACK, "Servei Escolar");
                } else if (liniatren.contains("L6") || liniatren.contains("L7") || liniatren.contains("L8") || liniatren.contains("L12")) {
                    getLayer(3).draw(minecraftia, 176, 45, MapColorPalette.COLOR_BLACK, "Bitllet T-4 válid");
                } else if (liniatren.equalsIgnoreCase("RL2")) {
                    getLayer(3).draw(minecraftia, 176, 45, MapColorPalette.COLOR_BLACK, "ATM no válid");
                } else if (displayID.equalsIgnoreCase("PDF1")) {
                    getLayer(3).draw(minecraftia, 176, 45, MapColorPalette.COLOR_BLACK, "Ult. vagó no obre");
                } else if (displayID.equalsIgnoreCase("PDF2")) {
                    getLayer(3).draw(minecraftia, 176, 45, MapColorPalette.COLOR_BLACK, "Pri. vagó no obre");
                } else if (displayID.contains("ALG") || displayID.contains("TMN") || displayID.contains("VBLG") || displayID.contains("GRB") || displayID.contains("SRM") || displayID.contains("GTMP") || displayID.contains("PDN") || displayID.contains("TLN") || displayID.contains("SLS")) {
                    getLayer(3).draw(minecraftia, 176, 45, MapColorPalette.COLOR_BLACK, "Surt en 15 seg.");
                }

                //////////////////////////////////////////////////////////
            }

            if(clearIn != 0){
                if (!senseParada) getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> this.clearInformation(properties.get("ID", String.class)), clearIn * 20L);
                else getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> this.clearInformation(properties.get("ID", String.class)), 10 * 20L);
            }

            return true;
        }

        public boolean clearInformation(String displayID){
            if(! properties.get("ID", String.class).equals(displayID)) return false;
            liniatren = "";
            estacions = null;
            multiplePantalla = false;
            senseParada = false;
            hasFGCTrain = false;

            //wip
            getLayer(0).clear();
            getLayer(0).draw(Assets.getMapTexture(imgDir + "ManualDisplay3.png"), 0, 0);

            getLayer(2).clear();
            getLayer(3).clear();
            return true;
        }
    }

    public static class ManualDisplay4 extends ManualDisplay{  //pantalla ADIF pròxima sortida. 2*1 blocs.
        Integer retard = 0;
        int tickCount = 0;
        LocalDateTime horaActual;
        boolean textInSpanish = false;
        boolean senseParada = false;
        boolean sortidaImmediata = false;
        boolean sortidaAmbRetard = false;
        static MapTexture background = MapTexture.loadPluginResource(JavaPlugin.getPlugin(TrensMinecat.class), "img/ManualDisplay4.png");


        //layer0: black background (onAttached)
        //layer1: static text
        //layer2: image (onAttached)
        //layer3: dynamic text (every tick)
        //layer4: platform number
        //layer5: clock handles (onTick)

        @Override
        public void onAttached() {
            getLayer(0).fillRectangle(0, 10, 256, 85, MapColorPalette.getColor(0x2E, 0x2E, 0X2E));
            getLayer(2).draw(background, 0, 0);
            updatePlatformNumber();
            setUpdateWithoutViewers(false);
            super.onAttached();
        }

        private void updatePlatformNumber(){
            BufferedImage platformNumber = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = platformNumber.createGraphics();
            g.setColor(new Color(255, 255, 255));
            g.setFont(TrensMinecat.helvetica46JavaFont);
            String platform = properties.get("platform", String.class, "");
            int x = 159;
            if(platform.length() > 1) x = 145; //shift platform number slightly to the left
            g.drawString(platform, x, 80); //platform number
            g.dispose();
            getLayer(4).draw(MapTexture.fromImage(platformNumber), 0, 0);
        }

        @Override
        public void onTick() {

            super.onTick();

            LocalDateTime now = LocalDateTime.now();
            getLayer(5).clear();

            getLayer(5).drawLine(222, 52,
                    222 + getX(now.getSecond(), 60, 20),
                    52 + getY(now.getSecond(), 60, 20),
                    MapColorPalette.getColor(0x76, 0x76, 0x76)
            );
            getLayer(5).drawLine(222, 52,
                    222 + getX(now.getMinute(), 60, 18),
                    52 + getY(now.getMinute(), 60, 18),
                    MapColorPalette.getColor(0x3b, 0x3b, 0x3b)
            );
            getLayer(5).drawLine(222, 52,
                    222 + getX(now.getHour() + now.getMinute() / 60F, 12, 12),
                    52 + getY(now.getHour() + now.getMinute() / 60F, 12, 12),
                    MapColorPalette.getColor(0, 0, 0)
            );


            if (tickCount % 100 == 0){
                textInSpanish = !textInSpanish;
            }

            if(senseParada && !sortidaImmediata && !sortidaAmbRetard){ //TREN SENSE PARADA
                Color c = new Color(255, 75, 75);
                getLayer(3).clear();
                BufferedImage layer3 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = layer3.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.setColor(c);
                g.setFont(TrensMinecat.minecraftiaJavaFont);
                if (!textInSpanish) {
                    g.drawString("NO SE ACERQUEN A LA VÍA", 6, 88); //ANOTACIONES
                } else g.drawString("NO S'APROPIN A LA VIA", 6, 88); //ANOTACIONES
                g.dispose();
                getLayer(3).draw(MapTexture.fromImage(layer3),0 , 5);
            }

            if(!senseParada && sortidaImmediata && !sortidaAmbRetard){ //TREN AMB SORTIDA INMEDIATA
                int n = tickCount % 20;
                Color c = new Color(255, 196, 0);

                if(n == 0) c = new Color(255, 196, 0);
                if(n == 10) c = new Color(255, 75, 75);

                if(n == 0 || n == 10){
                    getLayer(3).clear();
                    BufferedImage layer3 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = layer3.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                    g.setColor(c);
                    g.setFont(TrensMinecat.minecraftiaJavaFont);
                    if (!textInSpanish) {
                        g.drawString("SORTIDA IMMEDIATA", 6, 88); //ANOTACIONES
                    } else g.drawString("SALIDA INMEDIATA", 6, 88); //ANOTACIONES
                    g.dispose();
                    getLayer(3).draw(MapTexture.fromImage(layer3),0 , 5);
                }
            }

            if(!senseParada && !sortidaImmediata && sortidaAmbRetard){ //TREN AMB RETARD
                Color c = new Color(255, 75, 75);
                getLayer(3).clear();
                BufferedImage layer3 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = layer3.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.setColor(c);
                g.setFont(TrensMinecat.minecraftiaJavaFont);
                g.drawString("ESTIMAT / ESTIMADO: " + horaActual.plusSeconds(retard).format(DateTimeFormatter.ofPattern("HH:mm")), 6, 88); //ANOTACIONES
                g.dispose();
                getLayer(3).draw(MapTexture.fromImage(layer3),0 , 5);
            }

            tickCount++;
        }

        public boolean updateInformation(String displayID, String via, MinecartGroup dadesTren, Integer clearIn) {
            if (via != null) {
                displayID = displayID + via;
            }
            String codiParada = displayID.replaceAll("[0-9]","");
            if(!properties.get("ID", String.class).equals(displayID)) return false;

            if(!dadesTren.getProperties().matchTag(codiParada)){
                senseParada = true;
                sortidaImmediata = false;
                sortidaAmbRetard = false;
            }

            getLayer(1).clear();
            BufferedImage layer1 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = layer1.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setColor(new Color(255, 242, 0));
            g.setFont(TrensMinecat.minecraftiaJavaFont);
            horaActual = LocalDateTime.now();

            if (!senseParada) {
                g.drawString(horaActual.plusSeconds(clearIn).format(DateTimeFormatter.ofPattern("HH:mm")), 6, 41); //HORA
                g.drawString(dadesTren.getProperties().getDisplayName().toUpperCase(), 78, 41); //SERVEI
                g.drawString(dadesTren.getProperties().getDestination().toUpperCase(), 6, 73); //DESTINACIÓ

                if (clearIn >= 120){
                    retard = clearIn;
                    sortidaAmbRetard = true;
                    sortidaImmediata = false;
                } else {
                    sortidaAmbRetard = false;
                    sortidaImmediata = true;
                }
            } else {
                g.drawString("00:10", 6, 41); //HORA
                g.drawString("NO S'ATURA", 78, 41); //SERVEI
                g.drawString("TREN SIN PARADA", 6, 73); //DESTINACIÓ
            }

            g.dispose();

            updatePlatformNumber();

            getLayer(1).draw(MapTexture.fromImage(layer1), 0, 5); //global offset because the text is off (idk why)

            if(clearIn != 0){
                if (!senseParada) getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                        this.clearInformation(properties.get("ID", String.class)), clearIn * 20L);
                else getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                        this.clearInformation(properties.get("ID", String.class)), 10 * 20L);
            }

            return true;

        }

        public boolean clearInformation(String displayID) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;
            retard = 0;
            textInSpanish = false;
            senseParada = false;
            sortidaImmediata = false;
            sortidaAmbRetard = false;
            getLayer(1).clear();
            getLayer(4).clear();
            updatePlatformNumber();
            getLayer(3).clear();
            return true;
        }

        private int getX(float angle, float divideBy, float length){
            return (int) Math.round(Math.sin(angle * 2 * Math.PI / divideBy) * length);
        }

        private int getY(float angle, int divideBy, float length){
            return - (int) Math.round(Math.cos(angle * 2 * Math.PI / divideBy) * length);
        }
    }


    public static class ManualDisplay5 extends ManualDisplay{

        static MapTexture background = MapTexture.loadPluginResource(JavaPlugin.getPlugin(TrensMinecat.class), "img/ManualDisplay5.png");
        Boolean senseParada = false;

        @Override
        public boolean updateInformation(String displayID, String via, MinecartGroup dadesTren, Integer clearIn) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;
            getLayer(1).clear();
            BufferedImage layer1 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = layer1.createGraphics();
            g.setColor(new Color(73, 161, 71));
            g.setFont(TrensMinecat.minecraftiaJavaFont);

            if(!dadesTren.getProperties().matchTag(displayID)){
                senseParada = true;
            }

            if (!senseParada) {
                g.drawString(dadesTren.getProperties().getDestination(), 22, 67); //Destino
                if (via != null) {
                    g.drawString(via, 120, 67); //Vía
                }
                g.drawString(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), 210, 67); //Hora
                g.drawString("SERVICIO: " + dadesTren.getProperties().getDisplayName(), 22, 90); //Servicio (va debajo de "Destino")
            } else {
                g.drawString("VA A PROCEDER A SU PASO UN TREN SIN PARADA", 22, 67);
                g.drawString("¡¡NO SE ACERQUE AL BORDE DEL ANDÉN!!", 22, 90);
            }
            g.dispose();
            getLayer(1).draw(MapTexture.fromImage(layer1),0 , 0);

            if(clearIn != 0){
                if (!senseParada)
                    getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                            this.clearInformation(properties.get("ID", String.class)), clearIn * 20L);
                else
                    getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                            this.clearInformation(properties.get("ID", String.class)), 10 * 20L);
            }
            return true;
        }

        @Override
        public boolean clearInformation(String displayID) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;
            senseParada = false;
            getLayer(1).clear();
            return true;
        }

        @Override
        public void onAttached() {
            super.onAttached();
            getLayer(0).clear();
            getLayer(0).draw(background, 0, 0);
        }

    }

    public static class ManualDisplay6 extends ManualDisplay{
        int animationLength = 1;
        String trainDataDest;
        String trainDataName;
        boolean PMR = false;
        boolean hasTrain = false;
        String brand;

        static final Font helvetica = TrensMinecat.helvetica46JavaFont.deriveFont(Font.BOLD, 14);

        @Override
        public void onAttached() {
            getLayer(1).draw(Assets.getMapTexture(imgDir + "ManualDisplay6.png"), 0, 0);
            brand = properties.get("brand", String.class, "rodalies"); //si no s'ha especificat una marca, retorna rodalies.
            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px/" + brand + ".png"), 13, 41);
            setUpdateWithoutViewers(false);
            super.onAttached();
        }

        @Override
        public void onDetached() {
            super.onDetached();
        }

        @Override
        public void onTick() {
            super.onTick();

            if(!hasTrain){
                animationLength = 1;
                getLayer(3).clear();
                getLayer(5).clear();
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                BufferedImage time = new BufferedImage(255,128, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = time.createGraphics();
                g.setFont(helvetica);
                g.setColor(new Color(255, 255, 255));
                g.drawString(formatter.format(now), 200, 70);
                g.dispose();
                getLayer(3).draw(MapTexture.fromImage(time), 0, 0);
            }

            else {
                if (PMR) { getLayer(5).draw(Assets.getMapTexture(imgDir + "PMR20px.png"), 222, 66); }
                try {
                    if (trainDataName.toLowerCase().contains("r1_serveia") && trainDataDest.equalsIgnoreCase("Hospitalet de Llobregat") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R1/A/HospitaletDeLlobregat/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                    if (trainDataName.toLowerCase().contains("r1_serveia") && trainDataDest.equalsIgnoreCase("Maçanet Massanes") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R1/A/MaçanetMassanes/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                    if (trainDataName.toLowerCase().contains("r1_serveib") && trainDataDest.equalsIgnoreCase("Molins de Rei") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R1/B/MolinsDeRei/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                    if (trainDataName.toLowerCase().contains("r1_serveib") && trainDataDest.equalsIgnoreCase("Arenys de Mar") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R1/B/ArenysDeMar/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                    if (trainDataName.toLowerCase().contains("r2s_serveia") && trainDataDest.equalsIgnoreCase("Barcelona - Estació de França") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R2/A/Barcelona-EstacióDeFrança/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                    if (trainDataName.toLowerCase().contains("r2s_serveia") && trainDataDest.equalsIgnoreCase("Sant Vicenç de Calders") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R2/A/SantVicençDeCalders/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                    if (trainDataName.toLowerCase().contains("r2s_serveib") && trainDataDest.equalsIgnoreCase("Barcelona - Estació de França") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R2/B/Barcelona-EstacióDeFrança/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                    if (trainDataName.toLowerCase().contains("r2s_serveib") && trainDataDest.equalsIgnoreCase("Vilanova i la Geltrú") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R2/B/VilanovaILaGeltrú/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                    if (trainDataName.toLowerCase().contains("r8") && trainDataDest.equalsIgnoreCase("Martorell") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R8/Martorell/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                    if (trainDataName.toLowerCase().contains("r8") && trainDataDest.equalsIgnoreCase("Granollers Centre") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R8/GranollersCentre/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                    if (trainDataName.toLowerCase().contains("r10") && trainDataDest.equalsIgnoreCase("Aeroport del Prat") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R10/AeroportDelPrat/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                    if (trainDataName.toLowerCase().contains("r10") && trainDataDest.equalsIgnoreCase("Barcelona - Estació de França") && animationLength > 0 && animationLength < 602) {
                        if (animationLength != 601) {
                            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px_animated/Rodalies/R10/Barcelona-EstacióDeFrança/fotograma" + animationLength + ".png"), 13, 41);
                            animationLength++;
                        }
                        else {
                            animationLength = 0;
                        }
                    }
                } catch (MapTexture.TextureLoadException e){
                    Bukkit.getServer().getConsoleSender().sendMessage("Oops! Errada al carregar animació pel tren [\" + trainDataName + \" amb destinació \" + trainDataDest + \"] (pot ser d'un tren que no tingui cap animació codificada?");
                }
            }
        }

        @Override
        public boolean updateInformation(String displayID, String via, MinecartGroup dadesTren, Integer clearIn) {
            if (via != null) {
                displayID = displayID + via;
            }

            if (dadesTren.getProperties().matchTag("-PMR-")){
                PMR = true;
            }

            String codiParada = displayID.replaceAll("[0-9]","");
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            hasTrain = true;
            brand = properties.get("brand", String.class, "rodalies"); //si no s'ha especificat una marca, retorna rodalies.

            getLayer(2).clear();
            getLayer(3).clear();

            String trainLine;
            String dest;
            if (!dadesTren.getProperties().matchTag(codiParada)){
                trainLine = "info";
                dest = "Sense parada / Sin parada";
            }
            else {
                trainLine = BoardUtils.getTrainLine(dadesTren.getProperties().getTrainName());
                dest = dadesTren.getProperties().getDestination();
            }

            MapTexture lineIcon;
            try {
                lineIcon = Assets.getMapTexture(imgDir + "46px/" + trainLine + ".png");
            } catch (MapTexture.TextureLoadException e) {
                lineIcon = Assets.getMapTexture(imgDir + "46px/info.png");
                dest = dadesTren.getProperties().getDestination();
            }

            BufferedImage text = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = text.createGraphics();
            g.setFont(helvetica);
            g.setColor(new Color(255, 255, 255));
            g.drawString(dest.toUpperCase(), 65, 70);

            g.dispose();
            getLayer(2).draw(MapTexture.fromImage(text),0 , 0);
            getLayer(2).draw(lineIcon, 13, 41);
            trainDataDest = dadesTren.getProperties().getDestination();
            trainDataName = dadesTren.getProperties().getTrainName();

            if(clearIn != 0){
                getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                        this.clearInformation(properties.get("ID", String.class)), clearIn * 20L);
            }

            return true;
        }

        @Override
        public boolean clearInformation(String displayID) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            hasTrain = false;
            PMR = false;
            getLayer(2).clear();
            getLayer(2).draw(Assets.getMapTexture(imgDir + "46px/" + brand + ".png"), 13, 41);
            getLayer(5).clear();
            return true;
        }
    }

    public static class ManualDisplay7 extends ManualDisplay{
        static final Font helvetica19 = TrensMinecat.helvetica46JavaFont.deriveFont(Font.BOLD, 19);
        static final Font helvetica12 = TrensMinecat.helvetica46JavaFont.deriveFont(Font.BOLD, 12);
        String observacions = "";

        @Override
        public void onAttached() {
            getLayer(1).draw(Assets.getMapTexture(imgDir + "ManualDisplay7.png"), 0, 0);
            this.clearInformation(properties.get("ID", String.class));
            setUpdateWithoutViewers(false);
            super.onAttached();
        }

        @Override
        public boolean updateInformation(String displayID, String via, MinecartGroup dadesTren, Integer clearIn) {
            if (via != null) {
                displayID = displayID + via;
            }
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            getLayer(2).clear();
            BufferedImage departure = new BufferedImage(230,46, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = departure.createGraphics();
            g.setFont(helvetica12);
            g.setColor(new Color(255, 0, 0));
            g.drawString(dadesTren.getProperties().getDestination().toUpperCase(), 4, 13);
            g.drawString(dadesTren.getProperties().getDisplayName().toUpperCase(), 4, 28);
            //if (dadesTren.getProperties().matchTag("R1") || dadesTren.getProperties().matchTag("R2A") || dadesTren.getProperties().matchTag("R2N") || )
            g.drawString(observacions, 4, 34); //observacions (no en tenim)
            g.dispose();
            getLayer(2).draw(MapTexture.fromImage(departure),13 , 55);

            if(clearIn != 0){
                getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                        this.clearInformation(properties.get("ID", String.class)), clearIn * 20L);
            }
            return true;
        }

        @Override
        public boolean clearInformation(String displayID) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;

            getLayer(2).clear();
            BufferedImage renfeRodalies = new BufferedImage(230,46, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = renfeRodalies.createGraphics();
            g.setFont(helvetica19);
            g.setColor(new Color(255, 0, 0));
            g.drawString("RENFE", 84, 20);
            g.drawString("RODALIES", 68, 40);
            g.dispose();
            getLayer(2).draw(MapTexture.fromImage(renfeRodalies),13 , 55);
            return true;
        }
    }

    public static class ManualDisplay8A extends ManualDisplay{  //pantalla ADIF 2 pròxima sortida. 2*1 blocs. (rellotge a la dreta)
        Integer retard = 0;
        int tickCount = 0;
        LocalDateTime horaActual;
        boolean textInSpanish = false;
        boolean senseParada = false;
        boolean sortidaImmediata = false;
        boolean sortidaAmbRetard = false;
        static MapTexture background = MapTexture.loadPluginResource(JavaPlugin.getPlugin(TrensMinecat.class), "img/ManualDisplay8A.png");


        //layer0: black background (onAttached)
        //layer1: static text
        //layer2: image (onAttached)
        //layer3: dynamic text (every tick)
        //layer4: platform number
        //layer5: clock handles (onTick)

        @Override
        public void onAttached() {
            getLayer(0).fillRectangle(0, 10, 256, 85, MapColorPalette.getColor(0x2E, 0x2E, 0X2E));
            getLayer(2).draw(background, 0, 0);
            updatePlatformNumber();
            setUpdateWithoutViewers(false);
            super.onAttached();
        }

        private void updatePlatformNumber(){
            BufferedImage platformNumber = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = platformNumber.createGraphics();
            g.setColor(new Color(255, 255, 255));
            g.setFont(TrensMinecat.helvetica46JavaFont);
            String platform = properties.get("platform", String.class, "");
            int x = 30;
            if(platform.length() > 1) x = 16; //shift platform number slightly to the left
            g.drawString(platform, x, 85); //platform number
            g.dispose();
            getLayer(4).draw(MapTexture.fromImage(platformNumber), 0, 0);
        }

        @Override
        public void onTick() {

            super.onTick();

            LocalDateTime now = LocalDateTime.now();
            getLayer(5).clear();

            getLayer(5).drawLine(224, 52,
                    224 + getX(now.getSecond(), 60, 20),
                    52 + getY(now.getSecond(), 60, 20),
                    MapColorPalette.getColor(0x76, 0x76, 0x76)
            );
            getLayer(5).drawLine(224, 52,
                    224 + getX(now.getMinute(), 60, 18),
                    52 + getY(now.getMinute(), 60, 18),
                    MapColorPalette.getColor(0x3b, 0x3b, 0x3b)
            );
            getLayer(5).drawLine(224, 52,
                    224 + getX(now.getHour() + now.getMinute() / 60F, 12, 12),
                    52 + getY(now.getHour() + now.getMinute() / 60F, 12, 12),
                    MapColorPalette.getColor(0, 0, 0)
            );


            if (tickCount % 100 == 0){
                textInSpanish = !textInSpanish;
            }

            if(senseParada && !sortidaImmediata && !sortidaAmbRetard){ //TREN SENSE PARADA
                Color c = new Color(255, 75, 75);
                getLayer(3).clear();
                BufferedImage layer3 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = layer3.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.setColor(c);
                g.setFont(TrensMinecat.minecraftiaJavaFont);
                if (!textInSpanish) {
                    g.drawString("NO SE ACERQUEN A LA VÍA", 68, 85); //ANOTACIONES
                } else g.drawString("NO S'APROPIN A LA VIA", 68, 85); //ANOTACIONES
                g.dispose();
                getLayer(3).draw(MapTexture.fromImage(layer3),0 , 5);
            }

            if(!senseParada && sortidaImmediata && !sortidaAmbRetard){ //TREN AMB SORTIDA INMEDIATA
                int n = tickCount % 20;
                Color c = new Color(255, 196, 0);

                if(n == 0) c = new Color(255, 196, 0);
                if(n == 10) c = new Color(255, 75, 75);

                if(n == 0 || n == 10){
                    getLayer(3).clear();
                    BufferedImage layer3 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = layer3.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                    g.setColor(c);
                    g.setFont(TrensMinecat.minecraftiaJavaFont);
                    if (!textInSpanish) {
                        g.drawString("SORTIDA IMMEDIATA", 68, 85); //ANOTACIONES
                    } else g.drawString("SALIDA INMEDIATA", 68, 85); //ANOTACIONES
                    g.dispose();
                    getLayer(3).draw(MapTexture.fromImage(layer3),0 , 5);
                }
            }

            if(!senseParada && !sortidaImmediata && sortidaAmbRetard){ //TREN AMB RETARD
                Color c = new Color(255, 75, 75);
                getLayer(3).clear();
                BufferedImage layer3 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = layer3.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.setColor(c);
                g.setFont(TrensMinecat.minecraftiaJavaFont);
                g.drawString("ESTIMAT / ESTIMADO: " + horaActual.plusSeconds(retard).format(DateTimeFormatter.ofPattern("HH:mm")), 68, 85); //ANOTACIONES
                g.dispose();
                getLayer(3).draw(MapTexture.fromImage(layer3),0 , 5);
            }

            tickCount++;
        }

        public boolean updateInformation(String displayID, String via, MinecartGroup dadesTren, Integer clearIn) {
            if (via != null) {
                displayID = displayID + via;
            }
            String codiParada = displayID.replaceAll("[0-9]","");
            if(!properties.get("ID", String.class).equals(displayID)) return false;

            if(!dadesTren.getProperties().matchTag(codiParada)){
                senseParada = true;
                sortidaImmediata = false;
                sortidaAmbRetard = false;
            }

            getLayer(1).clear();
            BufferedImage layer1 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = layer1.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setColor(new Color(255, 242, 0));
            g.setFont(TrensMinecat.minecraftiaJavaFont);
            horaActual = LocalDateTime.now();

            if (!senseParada) {
                g.drawString(horaActual.plusSeconds(clearIn).format(DateTimeFormatter.ofPattern("HH:mm")), 68, 38); //HORA
                g.drawString(dadesTren.getProperties().getDisplayName().toUpperCase(), 107, 38); //SERVEI
                g.drawString(dadesTren.getProperties().getDestination().toUpperCase(), 68, 70); //DESTINACIÓ

                if (clearIn >= 120){
                    retard = clearIn;
                    sortidaAmbRetard = true;
                    sortidaImmediata = false;
                } else {
                    sortidaAmbRetard = false;
                    sortidaImmediata = true;
                }
            } else {
                g.drawString("00:10", 68, 38); //HORA
                g.drawString("NO S'ATURA", 107, 38); //SERVEI
                g.drawString("TREN SIN PARADA", 68, 70); //DESTINACIÓ
            }

            g.dispose();

            updatePlatformNumber();

            getLayer(1).draw(MapTexture.fromImage(layer1), 0, 5); //global offset because the text is off (idk why)

            if(clearIn != 0){
                if (!senseParada)
                getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                        this.clearInformation(properties.get("ID", String.class)), clearIn * 20L);
                else
                    getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                            this.clearInformation(properties.get("ID", String.class)), 10 * 20L);
            }

            return true;

        }

        public boolean clearInformation(String displayID) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;
            retard = 0;
            textInSpanish = false;
            senseParada = false;
            sortidaImmediata = false;
            sortidaAmbRetard = false;
            getLayer(1).clear();
            getLayer(4).clear();
            updatePlatformNumber();
            getLayer(3).clear();
            return true;
        }

        private int getX(float angle, float divideBy, float length){
            return (int) Math.round(Math.sin(angle * 2 * Math.PI / divideBy) * length);
        }

        private int getY(float angle, int divideBy, float length){
            return - (int) Math.round(Math.cos(angle * 2 * Math.PI / divideBy) * length);
        }
    }

    public static class ManualDisplay8B extends ManualDisplay{  //pantalla ADIF 2 pròxima sortida. 2*1 blocs. (rellotge a la esquerra)
        Integer retard = 0;
        int tickCount = 0;
        LocalDateTime horaActual;
        boolean textInSpanish = false;
        boolean senseParada = false;
        boolean sortidaImmediata = false;
        boolean sortidaAmbRetard = false;
        static MapTexture background = MapTexture.loadPluginResource(JavaPlugin.getPlugin(TrensMinecat.class), "img/ManualDisplay8B.png");


        //layer0: black background (onAttached)
        //layer1: static text
        //layer2: image (onAttached)
        //layer3: dynamic text (every tick)
        //layer4: platform number
        //layer5: clock handles (onTick)

        @Override
        public void onAttached() {
            getLayer(0).fillRectangle(0, 10, 256, 85, MapColorPalette.getColor(0x2E, 0x2E, 0X2E));
            getLayer(2).draw(background, 0, 0);
            updatePlatformNumber();
            setUpdateWithoutViewers(false);
            super.onAttached();
        }

        private void updatePlatformNumber(){
            BufferedImage platformNumber = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = platformNumber.createGraphics();
            g.setColor(new Color(255, 255, 255));
            g.setFont(TrensMinecat.helvetica46JavaFont);
            String platform = properties.get("platform", String.class, "");
            int x = 194;
            if(platform.length() > 1) x = 202; //shift platform number slightly to the left
            g.drawString(platform, x, 85); //platform number
            g.dispose();
            getLayer(4).draw(MapTexture.fromImage(platformNumber), 0, 0);
        }

        @Override
        public void onTick() {

            super.onTick();

            LocalDateTime now = LocalDateTime.now();
            getLayer(5).clear();

            getLayer(5).drawLine(31, 52,
                    31 + getX(now.getSecond(), 60, 20),
                    52 + getY(now.getSecond(), 60, 20),
                    MapColorPalette.getColor(0x76, 0x76, 0x76)
            );
            getLayer(5).drawLine(31, 52,
                    31 + getX(now.getMinute(), 60, 18),
                    52 + getY(now.getMinute(), 60, 18),
                    MapColorPalette.getColor(0x3b, 0x3b, 0x3b)
            );
            getLayer(5).drawLine(31, 52,
                    31 + getX(now.getHour() + now.getMinute() / 60F, 12, 12),
                    52 + getY(now.getHour() + now.getMinute() / 60F, 12, 12),
                    MapColorPalette.getColor(0, 0, 0)
            );


            if (tickCount % 100 == 0){
                textInSpanish = !textInSpanish;
            }

            if(senseParada && !sortidaImmediata && !sortidaAmbRetard){ //TREN SENSE PARADA
                Color c = new Color(255, 75, 75);
                getLayer(3).clear();
                BufferedImage layer3 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = layer3.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.setColor(c);
                g.setFont(TrensMinecat.minecraftiaJavaFont);
                if (!textInSpanish) {
                    g.drawString("NO SE ACERQUEN A LA VÍA", 63, 85); //ANOTACIONES
                } else g.drawString("NO S'APROPIN A LA VIA", 63, 85); //ANOTACIONES
                g.dispose();
                getLayer(3).draw(MapTexture.fromImage(layer3),0 , 5);
            }

            if(!senseParada && sortidaImmediata && !sortidaAmbRetard){ //TREN AMB SORTIDA INMEDIATA
                int n = tickCount % 20;
                Color c = new Color(255, 196, 0);

                if(n == 0) c = new Color(255, 196, 0);
                if(n == 10) c = new Color(255, 75, 75);

                if(n == 0 || n == 10){
                    getLayer(3).clear();
                    BufferedImage layer3 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = layer3.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                    g.setColor(c);
                    g.setFont(TrensMinecat.minecraftiaJavaFont);
                    if (!textInSpanish) {
                        g.drawString("SORTIDA IMMEDIATA", 63, 85); //ANOTACIONES
                    } else g.drawString("SALIDA INMEDIATA", 63, 85); //ANOTACIONES
                    g.dispose();
                    getLayer(3).draw(MapTexture.fromImage(layer3),0 , 5);
                }
            }

            if(!senseParada && !sortidaImmediata && sortidaAmbRetard){ //TREN AMB RETARD
                Color c = new Color(255, 75, 75);
                getLayer(3).clear();
                BufferedImage layer3 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = layer3.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.setColor(c);
                g.setFont(TrensMinecat.minecraftiaJavaFont);
                g.drawString("ESTIMAT / ESTIMADO: " + horaActual.plusSeconds(retard).format(DateTimeFormatter.ofPattern("HH:mm")), 63, 85); //ANOTACIONES
                g.dispose();
                getLayer(3).draw(MapTexture.fromImage(layer3),0 , 5);
            }

            tickCount++;
        }

        public boolean updateInformation(String displayID, String via, MinecartGroup dadesTren, Integer clearIn) {
            if (via != null) {
                displayID = displayID + via;
            }
            String codiParada = displayID.replaceAll("[0-9]","");
            if(!properties.get("ID", String.class).equals(displayID)) return false;

            if(!dadesTren.getProperties().matchTag(codiParada)){
                senseParada = true;
                sortidaImmediata = false;
                sortidaAmbRetard = false;
            }

            getLayer(1).clear();
            BufferedImage layer1 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = layer1.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setColor(new Color(255, 242, 0));
            g.setFont(TrensMinecat.minecraftiaJavaFont);
            horaActual = LocalDateTime.now();

            if (!senseParada) {
                g.drawString(horaActual.plusSeconds(clearIn).format(DateTimeFormatter.ofPattern("HH:mm")), 63, 38); //HORA
                g.drawString(dadesTren.getProperties().getDisplayName().toUpperCase(), 102, 38); //SERVEI
                g.drawString(dadesTren.getProperties().getDestination().toUpperCase(), 63, 70); //DESTINACIÓ

                if (clearIn >= 120){
                    retard = clearIn;
                    sortidaAmbRetard = true;
                    sortidaImmediata = false;
                } else {
                    sortidaAmbRetard = false;
                    sortidaImmediata = true;
                }
            } else {
                g.drawString("00:10", 63, 38); //HORA
                g.drawString("NO S'ATURA", 102, 38); //SERVEI
                g.drawString("TREN SIN PARADA", 63, 70); //DESTINACIÓ
            }

            g.dispose();

            updatePlatformNumber();

            getLayer(1).draw(MapTexture.fromImage(layer1), 0, 5); //global offset because the text is off (idk why)

            if(clearIn != 0){
                if (!senseParada)
                    getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                            this.clearInformation(properties.get("ID", String.class)), clearIn * 20L);
                else
                    getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () ->
                            this.clearInformation(properties.get("ID", String.class)), 10 * 20L);
            }

            return true;

        }

        public boolean clearInformation(String displayID) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;
            retard = 0;
            textInSpanish = false;
            senseParada = false;
            sortidaImmediata = false;
            sortidaAmbRetard = false;
            getLayer(1).clear();
            getLayer(4).clear();
            updatePlatformNumber();
            getLayer(3).clear();
            return true;
        }

        private int getX(float angle, float divideBy, float length){
            return (int) Math.round(Math.sin(angle * 2 * Math.PI / divideBy) * length);
        }

        private int getY(float angle, int divideBy, float length){
            return - (int) Math.round(Math.cos(angle * 2 * Math.PI / divideBy) * length);
        }
    }

    public static class ManualDisplay9A extends ManualDisplay{
        static MapTexture background = MapTexture.loadPluginResource(JavaPlugin.getPlugin(TrensMinecat.class), "img/ManualDisplay9A.png");
        String dtype = "unhidden";
        ArrayList<String> hiddenLines = new ArrayList<String>();

        @Override
        public boolean updateInformation(String displayID, String via, MinecartGroup dadesTren, Integer clearIn) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;
            dtype = properties.get("ocultar", String.class, "unhidden"); //si no s'ha especificat quina linia ocultar, retorna unhidden.
            getLayer(1).clear();
            BufferedImage layer1 = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
            getLayer(1).draw(MapTexture.fromImage(layer1),0 , 0);

            if (dadesTren.getProperties().matchTag("FGC") && dadesTren.getProperties().matchTag("L8")){
                getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 9, 10);
            } else if (dadesTren.getProperties().matchTag("FGC") && dadesTren.getProperties().matchTag("S3")){
                getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 9, 26);
            } else if (dadesTren.getProperties().matchTag("FGC") && dadesTren.getProperties().matchTag("S4")){
                getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 9, 42);
            } else if (dadesTren.getProperties().matchTag("FGC") && dadesTren.getProperties().matchTag("S8")){
                getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 9, 58);
            } else if (dadesTren.getProperties().matchTag("FGC") && dadesTren.getProperties().matchTag("R5") && !dadesTren.getProperties().matchTag("R53")){
                getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 116, 10);
            } else if (dadesTren.getProperties().matchTag("FGC") && dadesTren.getProperties().matchTag("R6") && !dadesTren.getProperties().matchTag("R63")){
                getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 116, 26);
            } else if (dadesTren.getProperties().matchTag("FGC") && dadesTren.getProperties().matchTag("R5") && dadesTren.getProperties().matchTag("R53")){
                getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 116, 10);
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Martorell")) {
                    getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 89, 77);
                }
            } else if (dadesTren.getProperties().matchTag("FGC") && dadesTren.getProperties().matchTag("R6") && dadesTren.getProperties().matchTag("R63")){
                getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 116, 26);
                if (dadesTren.getProperties().getDestination().equalsIgnoreCase("Martorell")) {
                    getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 89, 77);
                }
            } else if (dadesTren.getProperties().matchTag("FGC") && dadesTren.getProperties().matchTag("-RES-")){
                getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 116, 58);
            }

            if (!dadesTren.getProperties().matchTag("R50") || !dadesTren.getProperties().matchTag("R53") || !dadesTren.getProperties().matchTag("R60") || !dadesTren.getProperties().matchTag("R63") || !dadesTren.getProperties().matchTag("-RES-")){
                getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 9, 77);
            }

            if (dadesTren.getProperties().matchTag("R50") || dadesTren.getProperties().matchTag("R60")){
                getLayer(2).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/led.png"), 153, 77);
            }

            if(clearIn != 0){
                getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> this.clearInformation(properties.get("ID", String.class)), 10 * 20L);
            }
            return true;
        }

        @Override
        public void onTick() {
            super.onTick();
            if (dtype.equalsIgnoreCase("L8")){
                if (!hiddenLines.contains("L8")) {
                    hiddenLines.add("L8");
                    getLayer(4).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/empty.png"), 7, 7);
                }
            }
            if (dtype.equalsIgnoreCase("S3")){
                if (!hiddenLines.contains("S3")) {
                    hiddenLines.add("S3");
                    getLayer(5).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/empty.png"), 7, 23);
                }
            }
            if (dtype.equalsIgnoreCase("S4")){
                if (!hiddenLines.contains("S4")) {
                    hiddenLines.add("S4");
                    getLayer(6).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/empty.png"), 7, 39);
                }
            }
            if (dtype.equalsIgnoreCase("S8")){
                if (!hiddenLines.contains("S8")) {
                    hiddenLines.add("S8");
                    getLayer(7).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/empty.png"), 7, 55);
                }
            }
            if (dtype.equalsIgnoreCase("R5")){
                if (!hiddenLines.contains("R5")) {
                    hiddenLines.add("R5");
                    getLayer(8).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/empty.png"), 114, 7);
                }
            }
            if (dtype.equalsIgnoreCase("R6")){
                if (!hiddenLines.contains("R6")){
                    hiddenLines.add("R6");
                    getLayer(9).draw(Assets.getMapTexture(imgDir + "displaysVariations/Displays9And10/empty.png"), 114, 23);
                }
            }
            if (dtype.equalsIgnoreCase("unhidden")){
                getLayer(4).clear();
                getLayer(5).clear();
                getLayer(6).clear();
                getLayer(7).clear();
                getLayer(8).clear();
                getLayer(9).clear();
                hiddenLines.clear();
            }
        }

        @Override
        public boolean clearInformation(String displayID) {
            if(! properties.get("ID", String.class).equals(displayID)) return false;
            getLayer(1).clear();
            getLayer(2).clear();
            getLayer(3).clear();
            return true;
        }

        @Override
        public void onAttached() {
            super.onAttached();
            getLayer(0).clear();
            getLayer(0).draw(background, 0, 0);
        }

    }

}
