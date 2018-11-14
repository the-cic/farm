/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.farm.game.render;

import com.mush.farm.game.Game;
import static com.mush.farm.game.logic.GameSizes.TILE_SIZE;
import com.mush.farm.game.model.Body;
import com.mush.farm.game.model.BodyType;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author mush
 */
public class GameRenderer {

    public static final int TILE_ZOOM = 2;

    // For now same as map
    public static final int VIEW_WIDTH = GameMap.MAP_WIDTH;
    public static final int VIEW_HEIGHT = GameMap.MAP_HEIGHT;

    private Game game;
    private GameMap gameMap;
    private BufferedImage tilesImage = null;
    private BufferedImage spritesImage = null;
    private Map<MapObjectType, BufferedImage> tileMap;
    private Map<BodyType, BufferedImage> bodySpriteMap;
    private final BodyDrawComparator bodyDrawComparator;
    private List<Body>[] bodiesByLine;

    public GameRenderer(Game game) {
        this.game = game;
        this.gameMap = game.gameMap;

        tileMap = new HashMap<>();
        bodySpriteMap = new HashMap<>();
        bodyDrawComparator = new BodyDrawComparator();
        bodiesByLine = new List[VIEW_HEIGHT];
        for (int i = 0; i < bodiesByLine.length; i++) {
            bodiesByLine[i] = new ArrayList<>();
        }

        try {
            tilesImage = ImageIO.read(new File("img/tiles.png"));
            spritesImage = ImageIO.read(new File("img/sprites.png"));

            cutTile(tilesImage, 0, 0, MapObjectType.DIRT);
            cutTile(tilesImage, 1, 0, MapObjectType.GRASS);
            cutTile(tilesImage, 2, 0, MapObjectType.WATER);
            cutTile(tilesImage, 3, 0, MapObjectType.DIRT_HOLE);
            cutTile(tilesImage, 0, 1, MapObjectType.ORGANIC_RUBBLE);
            cutTile(tilesImage, 1, 1, MapObjectType.STONE_RUBBLE);
            cutTile(tilesImage, 4, 1, MapObjectType.STONE_WALL);
            cutTile(tilesImage, 4, 0, MapObjectType.STONE_WALL_TOP);
            cutTile(tilesImage, 0, 2, MapObjectType.POTATO_PLANTED);
            cutTile(tilesImage, 1, 2, MapObjectType.POTATO_SAPLING);
            cutTile(tilesImage, 2, 2, MapObjectType.POTATO_PLANT);

            bodySpriteMap.put(BodyType.PERSON, cutTile(spritesImage, 0, 0));
            bodySpriteMap.put(BodyType.POTATO, cutTile(spritesImage, 1, 0));
            bodySpriteMap.put(BodyType.BUCKET_EMPTY, cutTile(spritesImage, 2, 0));
            bodySpriteMap.put(BodyType.BUCKET_FULL, cutTile(spritesImage, 3, 0));
            bodySpriteMap.put(BodyType.SHOVEL, cutTile(spritesImage, 4, 0));

        } catch (IOException e) {

        }
    }

    private BufferedImage cutTile(BufferedImage img, int u, int v) {
        BufferedImage tile = new BufferedImage(TILE_SIZE, TILE_SIZE, img.getType());
        Graphics2D g = tile.createGraphics();
        g.drawImage(img, -u * TILE_SIZE, -v * TILE_SIZE, null);
        g.dispose();
        return tile;
    }

    private void cutTile(BufferedImage img, int u, int v, MapObjectType type) {
        BufferedImage tile = cutTile(img, u, v);
        tileMap.put(type, tile);
    }

    public void render(Graphics2D g) {
        AffineTransform t = g.getTransform();
        g.scale(TILE_ZOOM, TILE_ZOOM);

        for (int j = 0; j < VIEW_WIDTH; j++) {
            for (int i = 0; i < VIEW_HEIGHT; i++) {
                MapObject object = gameMap.getMapObject(i, j);
                if (object != null) {
                    render(g, i, j, object);

                    if (game.getShowStats()) {
                        MapWater water = gameMap.getWaterObject(i, j);
                        renderStats(g, i, j, object, water);
                    }
                }
            }
        }

        splitBodiesIntoLines(game.bodies.getBodies());
        depthSortBodiesInLines();

//        List<Body> depthSortedBodies = depthSortBodies(game.bodies.getBodies());
        for (int j = 0; j < bodiesByLine.length; j++) {
//            for (int i = 0; i < VIEW_HEIGHT; i++) {
//                MapObject object = gameMap.getMapObject(i, j);
//                if (object != null && object.type == MapObjectType.STONE_WALL) {
//                    BufferedImage tile = tileMap.get(MapObjectType.STONE_WALL_TOP);
//                    int x = i * TILE_SIZE;
//                    int y = j * TILE_SIZE;
//                    if (tile != null) {
//                        g.drawImage(tile, x, y - TILE_SIZE, null);
//                    }
////                    tile = tileMap.get(MapObjectType.STONE_WALL);
////                    if (tile != null) {
////                        g.drawImage(tile, x, y, null);
////                    }
//                }
//            }
            for (Body body : bodiesByLine[j]) {
                render(g, body);
            }
        }

//        for (Body body : depthSortedBodies) {
//            render(g, body);
//        }
        g.setTransform(t);

        clearBodyLines();
    }

    private void splitBodiesIntoLines(List<Body> allBodies) {
        for (Body body : allBodies) {
            int v = (int) (body.position.y / TILE_SIZE);
            if (v >= 0 && v < bodiesByLine.length) {
                bodiesByLine[v].add(body);
            }
        }
    }

    private void clearBodyLines() {
        for (List<Body> lineList : bodiesByLine) {
            lineList.clear();
        }
    }

    private void depthSortBodiesInLines() {
        for (int v = 0; v < bodiesByLine.length; v++) {
            Collections.sort(bodiesByLine[v], bodyDrawComparator);
        }
    }

    private List<Body> depthSortBodies(List<Body> original) {
        List<Body> ordered = new ArrayList<>(original);

        Collections.sort(ordered, bodyDrawComparator);

        return ordered;
    }

    private void render(Graphics2D g, Body body) {
        if (!body.containedBodies.isEmpty()) {
            for (int i = body.containedBodies.size() - 1; i >= 0; i--) {
                Body subBody = body.containedBodies.get(i);
                g.drawImage(bodySpriteMap.get(subBody.type),
                        (int) body.position.x + i % 2 - TILE_SIZE / 2,
                        (int) (body.position.y - TILE_SIZE * 0.75) - i * 3,
                        null);
            }
        }

        g.drawImage(bodySpriteMap.get(body.type),
                (int) body.position.x - TILE_SIZE / 2,
                (int) body.position.y,
                null);

        if (body.creature != null) {
            Body equippedBody = body.creature.getEquipped();
            if (equippedBody != null) {
                g.drawImage(bodySpriteMap.get(equippedBody.type),
                        (int) body.position.x,
                        (int) body.position.y - TILE_SIZE / 10,
                        null);
            }
        }
    }

    private void render(Graphics2D g, int u, int v, MapObject object) {
        BufferedImage tile = tileMap.get(object.type);
        int x = u * TILE_SIZE;
        int y = v * TILE_SIZE;
        if (tile != null) {
            g.drawImage(tile, x, y, null);
        }
    }

    private void renderStats(Graphics2D g, int u, int v, MapObject object, MapWater water) {
        int x = u * TILE_SIZE;
        int y = v * TILE_SIZE;

        g.setColor(Color.BLUE);
        int waterLenght = (int) (water.getValue() * (TILE_SIZE - 2));
        g.drawLine(x + 1, y + 3, x + 1 + waterLenght, y + 3);

        g.setColor(new Color(0, 0.2f, 1, 0.2f));
        if (water.getValue() > 0) {
            g.fillRect(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2);
        }

        g.setColor(Color.YELLOW);
        int ageLength = (int) (object.getAgePercent() * (TILE_SIZE - 2));
        g.drawLine(x + 1, y + 1, x + 1 + ageLength, y + 1);

        g.setColor(Color.RED);
        int healthLength = (int) (object.integrity * (TILE_SIZE - 2));
        g.drawLine(x + 1, y + 2, x + 1 + healthLength, y + 2);
    }

}
