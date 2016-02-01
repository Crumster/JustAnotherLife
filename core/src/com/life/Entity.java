/**
 * 
 */
package com.life;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This class is an abstract definition of an entity.
 *
 */
public abstract class Entity {
	protected GameScreen gameScreen;
	
	private boolean destructionScheduled = false;

	/**
	 * 
	 * @param gameScreen
	 */
	public Entity(GameScreen gameScreen){
		this.gameScreen = gameScreen;
	}
	
	/**
	 * Every entity object must have an update method
	 * @param delta
	 */
	public abstract void update(float delta);
	
	/**
	 * Every entity object must have a render method
	 * @param delta
	 */
	public abstract void render(SpriteBatch batch, float delta);
	
	/**
	 * Every entity object must have a dispose method
	 */
	public abstract void dispose();
	
	/**
	 * Schedules the enemy to be destroyed
	 */
	public void scheduleDestruction(){
		destructionScheduled = true;
	}
	
	/**
	 * Gets if the enemy is scheduled to be destroyed
	 * @return
	 */
	public boolean destructionScheduled(){
		return destructionScheduled;
	}

}