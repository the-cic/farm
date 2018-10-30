/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game;

import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.BodyType;
import com.mush.farm.game.model.Character;
import com.mush.farm.game.model.GameMap;
import com.mush.farm.game.model.MapObject;
import com.mush.farm.game.model.MapObjectType;
import com.mush.farm.game.model.MapWater;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author cic
 */
public class GameRenderer {

    private Game game;
    private GameMap gameMap;
    private BufferedImage tilesImage = null;
    private BufferedImage spritesImage = null;
    private Map<MapObjectType, BufferedImage> tileMap;
    private Map<BodyType, BufferedImage> bodySpriteMap;
    public static final int tileSize = 16;

    public GameRenderer(Game game) {
        this.game = game;
        this.gameMap = game.gameMap;

        tileMap = new HashMap<>();
        bodySpriteMap = new HashMap<>();

        try {
            tilesImage = ImageIO.read(new File("img/tiles.png"));
            spritesImage = ImageIO.read(new File("img/sprites.png"));

            cutTile(tilesImage, 0, 0, MapObjectType.DIRT);
            cutTile(tilesImage, 1, 0, MapObjectType.GRASS);
            cutTile(tilesImage, 2, 0, MapObjectType.WATER);
            cutTile(tilesImage, 0, 1, MapObjectType.ORGANIC_RUBBLE);
            cutTile(tilesImage, 1, 1, MapObjectType.STONE_RUBBLE);
            cutTile(tilesImage, 2, 1, MapObjectType.STONE_WALL);
            cutTile(tilesImage, 0, 2, MapObjectType.POTATO_PLANTED);
            cutTile(tilesImage, 1, 2, MapObjectType.POTATO_SAPLING);
            cutTile(tilesImage, 2, 2, MapObjectType.POTATO_PLANT);

            bodySpriteMap.put(BodyType.PERSON, cutTile(spritesImage, 0, 0));
            bodySpriteMap.put(BodyType.POTATO, cutTile(spritesImage, 1, 0));

        } catch (IOException e) {

        }
    }

    private BufferedImage cutTile(BufferedImage img, int u, int v) {
        BufferedImage tile = new BufferedImage(tileSize, tileSize, img.getType());
        Graphics2D g = tile.createGraphics();
        g.drawImage(img, -u * tileSize, -v * tileSize, null);
        g.dispose();
        return tile;
    }

    private void cutTile(BufferedImage img, int u, int v, MapObjectType type) {
        BufferedImage tile = cutTile(img, u, v);
        tileMap.put(type, tile);
    }

    public void render(Graphics2D g) {
        AffineTransform t = g.getTransform();
        g.scale(2, 2);

        for (int j = 0; j < 50; j++) {
            for (int i = 0; i < 50; i++) {
                MapObject object = gameMap.getMapObject(i, j);
                MapWater water = gameMap.getWaterObject(i, j);
                if (object != null /*&& object.type != null*/) {
                    render(g, i, j, object, water);
                }
            }
        }

        // todo: depth sorting
        for (Body body : gameMap.getBodies()) {
            render(g, body);
        }

        g.setTransform(t);
    }

    private void render(Graphics2D g, Body body) {
        if (!body.containedBodies.isEmpty()) {
            for (int i = body.containedBodies.size() - 1; i >= 0; i--) {
                Body subBody = body.containedBodies.get(i);
                g.drawImage(bodySpriteMap.get(subBody.type), (int) body.position.x + i % 2, (int) body.position.y - 9 - i * 3, null);
            }
        }
        g.drawImage(bodySpriteMap.get(body.type), (int) body.position.x, (int) body.position.y, null);
    }

    private void render(Graphics2D g, int u, int v, MapObject object, MapWater water) {
        BufferedImage tile = tileMap.get(object.type);
        int x = u * tileSize;
        int y = v * tileSize;
        if (tile != null) {
            g.drawImage(tile, x, y, null);
        }

        if (game.showStats) {
            /**/
            g.setColor(Color.BLUE);
            int waterLenght = (int) (water.getValue() * (tileSize - 2));
            g.drawLine(x + 1, y + 3, x + 1 + waterLenght, y + 3);

            g.setColor(new Color(0, 0.2f, 1, 0.2f));
            if (water.getValue() > 0) {
//                g.drawRect(x + 1, y + 1, tileSize - 2, tileSize - 2);
                g.fillRect(x + 1, y + 1, tileSize - 2, tileSize - 2);
            }
            /**/
            g.setColor(Color.YELLOW);
            int ageLength = (int) (object.getAgePercent() * (tileSize - 2));
            g.drawLine(x + 1, y + 1, x + 1 + ageLength, y + 1);

            g.setColor(Color.RED);
            int healthLength = (int) (object.integrity * (tileSize - 2));
            g.drawLine(x + 1, y + 2, x + 1 + healthLength, y + 2);
        }
    }

}
