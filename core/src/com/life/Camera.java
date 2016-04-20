package com.life;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 *
 */
public class Camera extends OrthographicCamera {
    private float shakeIntensity;
    private long shakeSpeed;
    private float shakeDuration;
    private long shakeStart;
    private long timeSinceLastShake;

    @Override
    public void update() {
        //Update screen shaking
        if(System.currentTimeMillis() - shakeStart < shakeDuration && System.currentTimeMillis() - timeSinceLastShake > shakeSpeed){
            position.x += MathUtils.random(-shakeIntensity, shakeIntensity);
            position.y += MathUtils.random(-shakeIntensity, shakeIntensity);

            timeSinceLastShake = System.currentTimeMillis();
        }

        super.update();
    }

    /**
     * Moves the camera towards a target position
     * @param target
     * @param lerp
     * @param delta
     */
    public void follow(Vector2 target, float lerp, float delta){
        position.x += (target.x - position.x) * lerp * delta;
        position.y += (target.y - position.y) * lerp * delta;
    }

    /**
     * Start a new shake with a certain shakeIntensity and duration
     * @param shakeIntensity intensity in ms
     * @param shakeSpeed speed in ms
     * @param shakeDuration duration in ms
     */
    public void shake(float shakeIntensity, long shakeSpeed, float shakeDuration){
        this.shakeIntensity = shakeIntensity;
        this.shakeSpeed = shakeSpeed;
        this.shakeDuration = shakeDuration;

        shakeStart = System.currentTimeMillis();
        timeSinceLastShake = shakeStart;
    }
}
