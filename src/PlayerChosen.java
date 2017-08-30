/**
 * Class for the ChampionshipPools project. Shorthand. Contains a field for a
 * player's tag and if they have already been placed in a pool or not.
 *
 * @author Joe Forsman
 */
public class PlayerChosen implements Comparable {

    /**
     * If the player is already in a pool ("chosen").
     */
    private boolean chosen;

    /**
     * Tag of this competitor.
     */
    private String tag;

    /**
     * Creates this PlayerChosen.
     *
     * @param name
     *            Tag of the competitor.
     */
    public PlayerChosen(String name) {
        this.tag = name;
        this.chosen = false;
    }

    /**
     * Notify if has been chosen.
     *
     * @return {@code true} if chosen.
     */
    public final boolean isChosen() {
        return this.chosen;
    }

    /**
     * This player has been chosen.
     */
    public final void choose() {
        this.chosen = true;
    }

    /**
     * Gives this player's {@code tag}.
     *
     * @return tag of this {@code PlayerChosen}.
     */
    public final String tag() {
        return this.tag;
    }

    @Override
    public final String toString() {
        return this.tag;
    }

    @Override
    public int compareTo(Object arg0) {
        return this.toString().compareTo(arg0.toString());
    }
}