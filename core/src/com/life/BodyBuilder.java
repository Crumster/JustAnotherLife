/**
 * 
 */
package com.life;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * This class creates box2d objects defined in a tmx world through object layers.
 *
 */
public class BodyBuilder {
	private World world;
	private TiledMap map;

	/**
	 * 
	 */
	public BodyBuilder() {
	}
	
	/**
	 * Reads the objects from the tmx map and creates the corresponding box2d bodies for physics.
	 * The first entity in the arraylist of entities has to be the player.
	 * @param gameScreen
	 */
	public void createBodies(GameScreen gameScreen){
		world = gameScreen.getWorld();
		map = gameScreen.getMap();

		/*
		//Add the player to the map
		gameScreen.addEntity(new Player(gameScreen, new Vector2(12f, 9f)));
		
		MapObjects crates = map.getLayers().get("crate").getObjects();
		Vector2[] crateSpawnLocations = new Vector2[crates.getCount()];
		for(int i = 0; i < crates.getCount(); i++){
			RectangleMapObject crate = (RectangleMapObject) crates.get(i);
			Rectangle rect = crate.getRectangle();
			crateSpawnLocations[i] = new Vector2((rect.x + rect.width / 2f) / SupaBax.PPM, (rect.y + rect.height / 2f) / SupaBax.PPM);
		}
		gameScreen.addEntity(new Crate(gameScreen, crateSpawnLocations));
		
		genBodies(map.getLayers().get("ground"));
		genBodies(map.getLayers().get("wall"));
		
		//Generate hell objects
		for(MapObject object : map.getLayers().get("hell").getObjects()){
			RectangleMapObject entity = (RectangleMapObject) object;
			Rectangle rect = entity.getRectangle();
			gameScreen.addEntity(new HellObject(gameScreen, new Vector2((rect.x + rect.width / 2f) / SupaBax.PPM, (rect.y + rect.height / 2f) / SupaBax.PPM), rect.width / SupaBax.PPM, rect.height / SupaBax.PPM));
		}
		*/
	}
	
	/**
	 * Generates static box2d bodies of the world of a given map layer
	 * @param layer
	 */
	private void genBodies(MapLayer layer){
		for(MapObject object : layer.getObjects()){
			if(object instanceof RectangleMapObject){
				createRectangle((RectangleMapObject) object, Float.parseFloat((String) layer.getProperties().get("friction")), Float.parseFloat((String) layer.getProperties().get("restitution")));
			} else if(object instanceof PolygonMapObject){
				createPolygon((PolygonMapObject) object, Float.parseFloat((String) layer.getProperties().get("friction")), Float.parseFloat((String) layer.getProperties().get("restitution")));
			} else if(object instanceof PolylineMapObject){
				createPolyline((PolylineMapObject) object, Float.parseFloat((String) layer.getProperties().get("friction")), Float.parseFloat((String) layer.getProperties().get("restitution")));
			} else if(object instanceof EllipseMapObject){
				createEllipse((EllipseMapObject) object, Float.parseFloat((String) layer.getProperties().get("friction")), Float.parseFloat((String) layer.getProperties().get("restitution")));
			} else{
				Gdx.app.error("Error", "Invalid map object");
			}
		}
	}
	
	/**
	 * Creates a rectangle box2d object in the box2d world
	 * @param rectangleObject
	 * @param friction
	 * @param restitution
	 */
	private void createRectangle(RectangleMapObject rectangleObject, float friction, float restitution){
		Rectangle rect = rectangleObject.getRectangle();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(rect.width / JustAnotherLife.PPM / 2f, rect.height / JustAnotherLife.PPM / 2f);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(new Vector2((rect.x + rect.width / 2f) / JustAnotherLife.PPM, (rect.y + rect.height / 2f) / JustAnotherLife.PPM));
		
		Body body = world.createBody(bodyDef);
		
		body.createFixture(shape, 0f);
		
		shape.dispose();
	}
	
	/**
	 * Creates a polygon box2d object in the box2d world
	 * @param polygonObject
	 * @param friction
	 * @param restitution
	 */
	private void createPolygon(PolygonMapObject polygonObject, float friction, float restitution){
		Polygon polygon = polygonObject.getPolygon();
		PolygonShape shape = new PolygonShape();
		float[] vertices = polygon.getTransformedVertices();
		float[] worldVertices = new float[vertices.length];
		
		for(int i = 0; i < vertices.length; i++){
			worldVertices[i] = vertices[i] / JustAnotherLife.PPM;
		}
		
		shape.set(worldVertices);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		Body body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		fixtureDef.density = 0f;
		
		body.createFixture(fixtureDef);
		
		shape.dispose();
	}
	
	/**
	 * Creates a chain box2d object in the box2d world
	 * @param polylineObject
	 * @param friction
	 * @param restitution
	 */
	private void createPolyline(PolylineMapObject polylineObject, float friction, float restitution){
		Polyline polyline = polylineObject.getPolyline();
		ChainShape shape = new ChainShape();
		float[] vertices = polyline.getTransformedVertices();
		float[] worldVertices = new float[vertices.length];
		
		for(int i = 0; i < vertices.length; i++){
			worldVertices[i] = vertices[i] / JustAnotherLife.PPM;
		}
		
		shape.createChain(worldVertices);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		Body body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		fixtureDef.density = 0f;
		
		body.createFixture(fixtureDef);
		
		shape.dispose();
	}
	
	/**
	 * Creates a circle box2d object in the box2d world
	 * @param ellipseObject
	 * @param friction
	 * @param restitution
	 */
	private void createEllipse(EllipseMapObject ellipseObject, float friction, float restitution){
		Ellipse circle = ellipseObject.getEllipse();
		CircleShape shape = new CircleShape();
		shape.setRadius(circle.width / 2f / JustAnotherLife.PPM);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(new Vector2((circle.x + circle.width / 2f) / JustAnotherLife.PPM, (circle.y + circle.width / 2f) / JustAnotherLife.PPM));
		Body body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;
		fixtureDef.density = 0f;
		
		body.createFixture(fixtureDef);
		
		shape.dispose();
	}

}