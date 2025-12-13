package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapaTiled {

    // ===== DADOS DO MAPA =====
    private int mapWidthTiles;
    private int mapHeightTiles;
    private int tileSize;

    // ===== CAMADAS =====
    private final List<int[][]> layers = new ArrayList<>();

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

                if (tsxStream == null)
                    throw new RuntimeException("TSX não encontrado: " + tsxPath);

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

            tileset = new Image(
                    getClass().getResourceAsStream("/" + imageSource)
            );

            tilesetCols = (int) (tileset.getWidth() / tileSize);

            // ===== TODAS AS CAMADAS =====
            NodeList layerNodes = map.getElementsByTagName("layer");

            for (int l = 0; l < layerNodes.getLength(); l++) {

                Element layer = (Element) layerNodes.item(l);
                Element data = (Element) layer.getElementsByTagName("data").item(0);

                String[] raw = data.getTextContent().trim().split(",");

                int[][] tiles = new int[mapHeightTiles][mapWidthTiles];

                int index = 0;
                for (String r : raw) {
                    r = r.trim();
                    if (r.isEmpty()) continue;

                    int tileId = Integer.parseInt(r);
                    int y = index / mapWidthTiles;
                    int x = index % mapWidthTiles;

                    tiles[y][x] = (tileId == 0) ? -1 : (tileId - firstGid);
                    index++;
                }

                layers.add(tiles);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== DESENHA TODAS AS CAMADAS =====
    public void desenhar(GraphicsContext gc) {
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

    // ===== GETTERS (ESSENCIAIS PRA CÂMERA E COLISÃO) =====

    public int getMapWidthTiles() {
        return mapWidthTiles;
    }

    public int getMapHeightTiles() {
        return mapHeightTiles;
    }

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
