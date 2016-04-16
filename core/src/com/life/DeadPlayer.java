package com.life;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 *
 */
public class DeadPlayer extends Entity {
    private World world;

    private Body body;
    private Fixture physicsFixture;

    private float width = 1f;
    private float height = 1f;

    /**
     *
     * @param gameScreen
     * @param position
     */
    public DeadPlayer(GameScreen gameScreen, Vector2 position){
        super(gameScreen);
        this.world = gameScreen.getWorld();

        //Body Definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position);
        bodyDef.fixedRotation = true;

        //Create body in box2d world
        body = world.createBody(bodyDef);
        body.setUserData(this);

        //Add fixture
        PolygonShape physicsShape = new PolygonShape();
        physicsShape.setAsBox(width / 2f, height / 2f);
        physicsFixture = body.createFixture(physicsShape, 1f);
        physicsFixture.setUserData(BodyIdentifiers.DEADPLAYER);

        physicsShape.dispose();
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(SpriteBatch batch, float delta) {

    }

    @Override
    public void dispose() {
        System.out.println("DeadPlayer disposed");
    }
}
