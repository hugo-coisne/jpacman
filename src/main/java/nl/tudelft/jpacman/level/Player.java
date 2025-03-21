package nl.tudelft.jpacman.level;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.sprite.AnimatedSprite;
import nl.tudelft.jpacman.sprite.Sprite;

/**
 * A player operated unit in our game.
 *
 * @author Jeroen Roosen
 */
public class Player extends Unit {

    /**
     * The amount of points accumulated by this player.
     */
    private int score;

    /**
     * The starting number of lives of the player.
     */
    private int lives = 3;

    /**
     * The objects observing this player.
     */
    private final Set<LifeObserver> observers;

    /**
     * The animations for every direction.
     */
    private final Map<Direction, Sprite> sprites;

    /**
     * The animation that is to be played when Pac-Man dies.
     */
    private final AnimatedSprite deathSprite;

    /**
     * <code>true</code> iff this player is alive.
     */
    private boolean alive;

    /**
     * {@link Unit} iff this player died by collision, <code>null</code> otherwise.
     */
    private Unit killer;

    /**
     * Creates a new player with a score of 0 points.
     *
     * @param spriteMap
     *                       A map containing a sprite for this player for every
     *                       direction.
     * @param deathAnimation
     *                       The sprite to be shown when this player dies.
     */
    protected Player(Map<Direction, Sprite> spriteMap, AnimatedSprite deathAnimation) {
        this.score = 0;
        this.alive = true;
        this.sprites = spriteMap;
        this.deathSprite = deathAnimation;
        deathSprite.setAnimating(false);
        this.observers = new HashSet<>();
    }

    /**
     * Adds an observer that will be notified when a life is lost.
     *
     * @param observer
     *                 The observer that will be notified.
     */
    public void addObserver(LifeObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer if it was listed.
     *
     * @param observer
     *                 The observer to be removed.
     */
    public void removeObserver(LifeObserver observer) {
        observers.remove(observer);
    }

    /**
     * Returns whether this player is alive or not.
     *
     * @return <code>true</code> iff the player is alive.
     */
    public boolean isAlive() {
        return alive;
    }

    public void playDeathAnimation() {
        deathSprite.restart();
    }

    /**
     * Sets whether this player is alive or not.
     *
     * If the player comes back alive, the {@link killer} will be reset.
     *
     * @param isAlive
     *                <code>true</code> iff this player is alive.
     */
    public void setAlive(boolean isAlive) {
        if (isAlive) {
            deathSprite.setAnimating(false);
            this.killer = null;
        }
        if (!isAlive) {
            playDeathAnimation();
        }
        this.alive = isAlive;
    }

    /**
     * Returns the unit that caused the death of Pac-Man.
     *
     * @return <code>Unit</code> iff the player died by collision, otherwise
     *         <code>null</code>.
     */
    public Unit getKiller() {
        return killer;
    }

    /**
     * Sets the cause of death.
     *
     * @param killer is set if collision with ghost happens.
     */
    public void setKiller(Unit killer) {
        this.killer = killer;
    }

    /**
     * Removes a life from the lives count.
     */
    public void removeLife() {
        this.lives -= 1;
        this.setAlive(false);
        updateObservers();
    }

    public int getLives() {
        return this.lives;
    }

    /**
     * Returns the amount of points accumulated by this player.
     *
     * @return The amount of points accumulated by this player.
     */
    public int getScore() {
        return score;
    }

    @Override
    public Sprite getSprite() {
        if (isAlive()) {
            return sprites.get(getDirection());
        }
        return deathSprite;
    }

    /**
     * Adds points to the score of this player.
     *
     * @param points
     *               The amount of points to add to the points this player already
     *               has.
     */
    public void addPoints(int points) {
        score += points;
    }

    /**
     * Updates the observers about the state of this level.
     */
    private void updateObservers() {
        for (LifeObserver observer : observers) {
            observer.lifeLost();
        }
    }

    public interface LifeObserver {
        /**
         * A life has been lost. The level should be restarted when
         * this event is received.
         */
        void lifeLost();
    }
}
