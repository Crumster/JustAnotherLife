package com.life;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Iterator;

public class GameScreen implements Screen, InputProcessor {
    private JustAnotherLife game;

    //to switch between debug rendering and normal rendering
    private boolean debug = true;

    //box2d necessities
    private Box2DDebugRenderer debugRenderer;
    private World world;
    private BodyBuilder bodyBuilder;

    //tmx map stuff
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    //camera and viewport
    private Camera camera;
    private Viewport viewport;

    //Entities in the game
    private ArrayList<Entity> entities = new ArrayList<Entity>();
    private Player player;

    public GameScreen(JustAnotherLife game){
        this.game = game;

        //float aspectRatio = (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();

        //create the camera and setup the viewport
        camera = new Camera();
        viewport = new FitViewport(10f, 10f, camera);
        viewport.apply();

        //set the initial position of the camera
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0f);

        //setup box2d world
        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0f, -50f), true);

        //load the tmx map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/map1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / JustAnotherLife.PPM);

        //build the box2d objects
        bodyBuilder = new BodyBuilder();
        bodyBuilder.createBodies(this);

        player = (Player) entities.get(0);
    }

    public void addEntity(Entity entity){
        entities.add(entity);
    }

    public Player getPlayer(){
        return player;
    }

    public World getWorld(){
        return world;
    }

    public TiledMap getMap(){
        return map;
    }

    public Camera getCamera(){
        return camera;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                //Collision with player and ground
                if(contact.getFixtureA().getUserData() == BodyIdentifiers.PLAYERSENSOR || contact.getFixtureB().getUserData() == BodyIdentifiers.PLAYERSENSOR){
                    player.addGroundContact();
                }
            }

            @Override
            public void endContact(Contact contact) {
                //Collision with player and ground
                if(contact.getFixtureA().getUserData() == BodyIdentifiers.PLAYERSENSOR || contact.getFixtureB().getUserData() == BodyIdentifiers.PLAYERSENSOR){
                    player.removeGroundContact();
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                contact.resetFriction();
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    public void update(float delta){
        for(int i = 0; i < entities.size(); i++){
            entities.get(i).update(delta);
        }

        //Delete entities if needed
        Iterator<Entity> iterator = entities.iterator();
        while(iterator.hasNext()){
            Entity entity = iterator.next();
            if(entity.destructionScheduled()){
                entity.dispose();
                iterator.remove();
            }
        }
        world.step(delta, 6, 2);

        camera.follow(player.getPosition(), 6, delta);
        camera.updateShake(player.getPosition(), delta);
        camera.update();
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(!debug){
            mapRenderer.setView(camera);
            mapRenderer.render();

            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();
            for(int i = 0; i < entities.size(); i++){
                entities.get(i).render(game.batch, delta);
            }
            game.batch.end();
        } else{
            debugRenderer.render(world, camera.combined);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        //dispose of all game entities
        for(int i = 0; i < entities.size(); i++){
            entities.get(i).dispose();
        }
        world.dispose();
        debugRenderer.dispose();
        mapRenderer.dispose();
        map.dispose();

        System.out.println("GameScreen disposed");
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.ESCAPE){
            Gdx.app.exit();
        }
        if(keycode == Input.Keys.GRAVE){
            if(debug){
                debug = false;
            } else{
                debug = true;
            }
        }

        //Handle player movement
        if(keycode == Input.Keys.LEFT){
            player.setMovingLeft(true);
        } else if(keycode == Input.Keys.RIGHT){
            player.setMovingRight(true);
        }
        if(keycode == Input.Keys.UP){
            player.setJump(true);
        }
        if(keycode == Input.Keys.SPACE){
            player.respawn();
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT){
            player.setMovingLeft(false);
        } else if(keycode == Input.Keys.RIGHT){
            player.setMovingRight(false);
        }
        if(keycode == Input.Keys.UP){
            player.setJump(false);
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
