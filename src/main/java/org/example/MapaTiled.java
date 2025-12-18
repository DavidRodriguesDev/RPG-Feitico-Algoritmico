package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapaTiled {

    private final List<NPC> npcs = new ArrayList<>();

    public List<NPC> getNPCs() {
        return npcs;
    }



    // ===== DADOS DO MAPA =====
    private int mapWidthTiles;
    private int mapHeightTiles;
    private int tileSize;

    // ===== CAMADAS =====
    private final List<int[][]> layersBase = new ArrayList<>();
    private final List<int[][]> layersTopo = new ArrayList<>();
    private int[][] camadaColisao;

    // ===== TILESET =====
    private Image tileset;
    private int tilesetCols;
    private int firstGid;

    public MapaTiled(String caminhoTMX) {
        carregarTMX(caminhoTMX);
    }

    private void carregarTMX(String caminho) {
        try {
            InputStream mapStream = getClass().getResourceAsStream(caminho);
            if (mapStream == null)
                throw new RuntimeException("Mapa não encontrado: " + caminho);

            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(mapStream);

            Element map = doc.getDocumentElement();

            mapWidthTiles  = Integer.parseInt(map.getAttribute("width"));
            mapHeightTiles = Integer.parseInt(map.getAttribute("height"));

            // ===== TILESET =====
            Element tilesetElement =
                    (Element) map.getElementsByTagName("tileset").item(0);

            firstGid = Integer.parseInt(tilesetElement.getAttribute("firstgid"));

            Document tilesetDoc;
            Element tilesetRoot;

            if (tilesetElement.hasAttribute("source")) {
                String tsxPath = tilesetElement.getAttribute("source");
                while (tsxPath.startsWith("../")) {
                    tsxPath = tsxPath.substring(3);
                }

                InputStream tsxStream =
                        getClass().getResourceAsStream("/" + tsxPath);

                tilesetDoc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(tsxStream);

                tilesetRoot = tilesetDoc.getDocumentElement();
            } else {
                tilesetRoot = tilesetElement;
            }

            tileSize = Integer.parseInt(tilesetRoot.getAttribute("tilewidth"));

            Element imageElement =
                    (Element) tilesetRoot.getElementsByTagName("image").item(0);

            String imageSource = imageElement.getAttribute("source");
            while (imageSource.startsWith("../")) {
                imageSource = imageSource.substring(3);
            }

            tileset = new Image(getClass().getResourceAsStream("/" + imageSource));
            tilesetCols = (int) (tileset.getWidth() / tileSize);

            // ===== CAMADAS =====
            NodeList layerNodes = map.getElementsByTagName("layer");

            for (int l = 0; l < layerNodes.getLength(); l++) {

                Element layer = (Element) layerNodes.item(l);
                String nomeLayer = layer.getAttribute("name").toLowerCase();

                Element data = (Element) layer
                        .getElementsByTagName("data").item(0);

                String[] raw = data.getTextContent().trim().split(",");

                int[][] tiles = new int[mapHeightTiles][mapWidthTiles];

                int index = 0;
                for (String r : raw) {
                    int tileId = Integer.parseInt(r.trim());
                    int y = index / mapWidthTiles;
                    int x = index % mapWidthTiles;

                    tiles[y][x] = (tileId == 0) ? -1 : (tileId - firstGid);
                    index++;
                }

                switch (nomeLayer) {
                    case "colisao" -> camadaColisao = tiles;
                    case "topo" -> layersTopo.add(tiles);
                    default -> layersBase.add(tiles);
                }
            }

            // ===== NPCs (Object Layer) =====
            NodeList objectGroups = map.getElementsByTagName("objectgroup");

            for (int i = 0; i < objectGroups.getLength(); i++) {
                Element group = (Element) objectGroups.item(i);

                if (!group.getAttribute("name").toLowerCase().startsWith("npc1"))
                    continue;


                NodeList objects = group.getElementsByTagName("object");

                for (int j = 0; j < objects.getLength(); j++) {
                    Element obj = (Element) objects.item(j);

                    double x = Double.parseDouble(obj.getAttribute("x"));
                    double y = Double.parseDouble(obj.getAttribute("y"));

                    Image spriteNpc = new Image(
                            getClass().getResourceAsStream("/sprites/magonpc.png")
                    );

                    npcs.add(new NPC(x, y, spriteNpc));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== DESENHO =====
    public void desenharBase(GraphicsContext gc) {
        desenharCamadas(gc, layersBase);
    }

    public void desenharTopo(GraphicsContext gc) {
        desenharCamadas(gc, layersTopo);
    }

    private void desenharCamadas(GraphicsContext gc, List<int[][]> layers) {
        for (int[][] layer : layers) {
            for (int y = 0; y < mapHeightTiles; y++) {
                for (int x = 0; x < mapWidthTiles; x++) {

                    int tileId = layer[y][x];
                    if (tileId < 0) continue;

                    int sx = (tileId % tilesetCols) * tileSize;
                    int sy = (tileId / tilesetCols) * tileSize;

                    gc.drawImage(
                            tileset,
                            sx, sy, tileSize, tileSize,
                            x * tileSize, y * tileSize,
                            tileSize, tileSize
                    );
                }
            }
        }
    }

    public void desenharNPCs(GraphicsContext gc) {
        for (NPC npc : npcs) {
            npc.desenhar(gc);
        }
    }


    // ===== COLISÃO =====
    public boolean isSolido(int tileX, int tileY) {

        if (camadaColisao == null) return false;

        if (tileX < 0 || tileY < 0 ||
                tileX >= mapWidthTiles || tileY >= mapHeightTiles)
            return true;

        return camadaColisao[tileY][tileX] != -1;
    }

    // ===== GETTERS =====
    public int getTileSize() {
        return tileSize;
    }

    public int getLarguraPixels() {
        return mapWidthTiles * tileSize;
    }

    public int getAlturaPixels() {
        return mapHeightTiles * tileSize;
    }
}
