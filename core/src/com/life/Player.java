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
public class Player extends Entity {
    private World world;

    private Body body;
    private Fixture physicsFixture;
    private Fixture groundSensorFixture;

    private Texture sheet;
    private Animation currentAnimation;
    private Animation idling;
    private Animation walking;
    private Animation jumping;

    private float stateTime;

    private Sprite sprite;

    //Action booleans
    private boolean movingLeft;
    private boolean movingRight;
    private boolean sprint;

    //Jumping
    private boolean jump = false;
    private boolean grounded = false;

    private int groundContacts = 0;

    private float width = 1;
    private float height = 1;

    private float maxSpeed = 8f;
    private float speed = 2f;
    private float direction = 1f;

    private Vector2 spawnPoint;

    /**
     *
     * @param gameScreen
     * @param position
     */
    public Player(GameScreen gameScreen, Vector2 position){
        super(gameScreen);
        this.world = gameScreen.getWorld();
        setSpawnPoint(position);

        //Setup the animation
        /*
        sheet = new Texture(Gdx.files.internal("spritesheets/player/player.png"));
        TextureRegion[][] splitSheet = TextureRegion.split(sheet, 32, 32);


        TextureRegion[]idleFrames = new TextureRegion[5];
        for(int i = 0; i < 5; i++){
            idleFrames[i] = splitSheet[0][i];
        }
        idling = new Animation(0.1f, idleFrames);

        TextureRegion[] walkFrames = new TextureRegion[5];
        for(int i = 0; i < 5; i++){
            walkFrames[i] = splitSheet[1][i];
        }
        walking = new Animation(0.1f, walkFrames);

        TextureRegion[] jumpFrames = new TextureRegion[3];
        for(int i = 0; i < 3; i++){
            jumpFrames[i] = splitSheet[2][i];
        }
        jumping = new Animation(0.1f, jumpFrames);

        currentAnimation = idling;
        */

        stateTime = 0;

        //Scale the sprite to meters
        sprite = new Sprite();
        sprite.setSize(width, height);
        sprite.setOriginCenter();

        //Body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        bodyDef.fixedRotation = true;
        bodyDef.bullet = true;

        //Create body in box2d world
        body = world.createBody(bodyDef);
        body.setUserData(this);

        //player body shape definition
        PolygonShape physicsShape = new PolygonShape();
        physicsShape.setAsBox(width / 2f, height / 2f);
        physicsFixture = body.createFixture(physicsShape, 1f);
        physicsFixture.setUserData(BodyIdentifiers.PLAYER);

        //Ground sensor shape definition
        PolygonShape groundSensorShape = new PolygonShape();
        groundSensorShape.setAsBox(width / 2.5f, height / 10f, new Vector2(0f, -height / 2f), 0f);
        groundSensorFixture = body.createFixture(groundSensorShape, 0f);
        groundSensorFixture.setSensor(true);
        groundSensorFixture.setUserData(BodyIdentifiers.PLAYERSENSOR);

        physicsShape.dispose();
        groundSensorShape.dispose();
    }

    @Override
    public void update(float delta) {
        //Get the current position and velocity for calculations
        Vector2 vel = body.getLinearVelocity();
        Vector2 pos = body.getPosition();

        //Cap movement
        if(Math.abs(vel.x) > maxSpeed){
            vel.x = Math.signum(vel.x) * maxSpeed;
            body.setLinearVelocity(vel);
        }

        //Set if grounded based on ground contacts
        if(groundContacts > 0){
            if(!grounded && !jump) {
                grounded = true;
            }
        } else{
            grounded = false;
        }

        //Disable friction while jumping
        if(!grounded){
            currentAnimation = jumping;
            physicsFixture.setFriction(0f);
        } else{
            if(!movingLeft && !movingRight){
                currentAnimation = idling;
                physicsFixture.setFriction(100f);
            } else{
                currentAnimation = walking;
                physicsFixture.setFriction(0.2f);
            }
            //for moving platforms
            //body.applyLinearImpulse(new Vector2(0f, -20f), pos, true);
        }

        //Move left if below the max speed
        if(movingLeft && vel.x > -maxSpeed){
            body.applyLinearImpulse(new Vector2(-speed, 0f), pos, true);
        }
        //Move right if below the max speed
        if(movingRight && vel.x < maxSpeed){
            body.applyLinearImpulse(new Vector2(speed, 0f), pos, true);
        }
        if((movingLeft && movingRight) || (!movingLeft && !movingRight)){
            body.setLinearVelocity(0f, vel.y);
        }

        //Jump if grounded
        if(jump){
            if(grounded){
                body.applyLinearImpulse(new Vector2(0f, 20f), pos, true);
                grounded = false;
            }
        }
        //Variable Jump
        if(vel.y > 0 && !jump){
            body.setLinearVelocity(vel.x, vel.y / 1.2f);
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        stateTime += delta;
        sprite.setRegion(currentAnimation.getKeyFrame(stateTime, true));
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        if(direction == -1f){
            sprite.flip(true, false);
        }
        sprite.draw(batch);
    }

    @Override
    public void dispose() {
        //sheet.dispose();

        System.out.println("Player disposed");
    }

    /**
     * Sets the players spawn point
     * @param position
     */
    public void setSpawnPoint(Vector2 position){
        spawnPoint = position;
    }

    /**
     * Respawns the player to its spawn point
     */
    public void respawn(){
        //Add a dead player
        gameScreen.addEntity(new DeadPlayer(gameScreen, body.getPosition()));
        //Move to spawn point
        body.setLinearVelocity(new Vector2(0f, 0f));
        body.setTransform(spawnPoint, 0f);
    }

    /**
     *
     */
    public void addGroundContact(){
        groundContacts++;
    }

    /**
     *
     */
    public void removeGroundContact(){
        groundContacts--;
    }

    /**
     * Gets the current position of the player.
     * @return
     */
    public Vector2 getPosition(){
        return body.getPosition();
    }

    /**
     * Sets if the player wants to jump or not (not actually jump)
     * @param jump
     */
    public void setJump(boolean jump){
        this.jump = jump;
    }

    /**
     * Sets if the player is moving left or not.
     * @param movingLeft
     */
    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
        if(movingLeft){
            direction = -1f;
        }
    }

    /**
     * Sets if the player is moving right or not.
     * @param movingRight
     */
    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
        if (movingRight) {
            direction = 1f;
        }
    }
}
