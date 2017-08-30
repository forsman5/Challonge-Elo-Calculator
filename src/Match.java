/**
 * Lightweight class designed to model a set of games.
 *
 * Package-wide convention -- tags of player may not contain the character '-'.
 * Consider this a precondition to every method.
 *
 * @author Joe Forsman
 *
 */
public class Match implements Comparable {
    /**
     * The tag of the winner of this match.
     */
    private String winner;

    /**
     * The tag of the loser of the match.
     */
    private String loser;

    /**
     * Games won in the set by the winner.
     */
    private int winnerWins;

    /**
     * Games won in the set by the loser.
     */
    private int loserWins;

    /**
     * Name of the tournament when this {@code Match} took place.
     */
    private String setting;

    /**
     * Default constructor.
     *
     * @param winnerName
     *            Name of winner of this set.
     * @param loserName
     *            Name of loser of this set.
     * @param winnerWinsNum
     *            Amount of games the winner of this set won.
     * @param loserWinsNum
     *            Amount of games the loser of this set won.
     * @param when
     *            Tourney where this match took place.
     * @requires winnerWinsNum > loserWinsNum
     */
    public Match(String winnerName, String loserName, int winnerWinsNum,
            int loserWinsNum, String when) {
        if (winnerName.contains("-") || loserName.contains("-")) {
            System.out
            .println("BREAKING PACKAGE-WIDE CONVENTION -- ERROR CODE 1");
        } else {
            this.winner = winnerName;
            this.loser = loserName;
            this.winnerWins = winnerWinsNum;
            this.loserWins = loserWinsNum;
            this.setting = when;
        }
    }

    @Override
    public final String toString() {
        return this.winner + " " + this.winnerWins + "-" + this.loserWins + " "
                + this.loser + " at " + this.setting;
    }

    /**
     * toString method that returns with the winner being first.
     *
     * @return {@code this} as a string.
     */
    public final String toStringWinnerFirst() {
        return this.toString();
    }

    /**
     * toString method that returns with the loser being first.
     *
     * @return {@code this} as a string.
     */
    public final String toStringLoserFirst() {
        return this.loser + " " + this.loserWins + "-" + this.winnerWins + " "
                + this.winner + " at " + this.setting;
    }

    /**
     * Accessor method for loser.
     *
     * @return Name of the loser of this {@code Match}.
     */
    public final String loserName() {
        return this.loser;
    }

    /**
     * Accessor method for winner.
     *
     * @return Name of the winner of this {@code Match}.
     */
    public final String winnerName() {
        return this.winner;
    }

    /**
     * Accessor method for the setting.
     *
     * @return Setting of this {@code Match}.
     */
    public final String setting() {
        return this.setting;
    }

    /**
     * Accessor method for winnerWins.
     *
     * @return Games won by the winner of this set.
     */
    public final int winnerGamesWon() {
        return this.winnerWins;
    }

    /**
     * Acessor method for loserWins.
     *
     * @return Games won in this set by the loser.
     */
    public final int loserGamesWon() {
        return this.loserWins;
    }

    @Override
    public final int compareTo(Object arg0) {
        int toReturn = 0;

        if (arg0 == this) {
            toReturn = 0;
        } else if (arg0 == null) {
            toReturn = -1;
        } else if (!(arg0 instanceof Match)) {
            toReturn = -1;
        } else {
            Match toCompare = (Match) arg0;

            if (this.winner.equals(toCompare.winner)) {
                if (this.loser.equals(toCompare.loser)) {
                    if (this.winnerWins == toCompare.winnerWins) {
                        if (this.loserWins == toCompare.loserWins) {
                            toReturn = 0;
                        } else {
                            toReturn = toCompare.loserWins - this.loserWins;
                        }
                    } else {
                        toReturn = toCompare.winnerWins - this.winnerWins;
                    }
                } else {
                    toReturn = this.loser.compareTo(toCompare.loser);
                }
            } else {
                toReturn = this.winner.compareTo(toCompare.winner);
            }
        }
        return toReturn;
    }
}
