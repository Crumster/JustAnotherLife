package com.life;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 *
 */
public class DeadPlayer extends Entity {
    private World world;

    private Body body;
    private Fixture physicsFixture;

    private Texture sheet;
    private Animation animation;
    private float stateTime;
    private Sprite sprite;

    private float width = 1f;
    private float height = 1f;
    private int direction = 1;

    /**
     *
     * @param gameScreen
     * @param position
     */
    public DeadPlayer(GameScreen gameScreen, Vector2 position, int direction){
        super(gameScreen);
        this.world = gameScreen.getWorld();
        this.direction = direction;

        /*
        sheet = new Texture(Gdx.files.internal("spritesheets/deadPlayer/deadPlayer.png"));
        TextureRegion[][] splitSheet = TextureRegion.split(sheet, 32, 32);

        TextureRegion[]animFrames = new TextureRegion[5];
        for(int i = 0; i < 5; i++){
            animFrames[i] = splitSheet[0][i];
        }

        animation = new Animation(1f, animFrames);
        */

        stateTime = 0;

        //Scale the sprite to meters
        sprite = new Sprite();
        sprite.setSize(width, height);
        sprite.setOriginCenter();

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
        stateTime += delta;
        sprite.setRegion(animation.getKeyFrame(stateTime, true));
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        if(direction == -1){
            sprite.flip(true, false);
        }
        sprite.draw(batch);

        //Destroy the dead body if "decayed"
        if(animation.isAnimationFinished(stateTime)){
            scheduleDestruction();
        }
    }

    @Override
    public void dispose() {
        System.out.println("DeadPlayer disposed");
    }
}
