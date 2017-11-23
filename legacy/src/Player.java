import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Representation of a single player, data stored in a text file. Has a series
 * of fields to represent the stats of each player.
 *
 * @author Joe Forsman
 *
 */
public class Player {
    /**
     * Full name of the player.
     */
    private String name;

    /**
     * {@code ArrayList} of every tournament placing this player has earned.
     */
    ArrayList<TournamentPlacing> tournaments;

    /**
     * Creates a new representation of this object.
     *
     * @ensures All fields are their default values.
     */
    private void createNewRep() {
        this.name = "";
        this.tournaments = new ArrayList<TournamentPlacing>();
    }

    /**
     * Overloaded constructor, no - parameters, all values set to base values.
     */
    public Player() {
        this.createNewRep();
    }

    /**
     * Primary constructor for the name class.
     *
     * @param name1
     *            The name of this player, full name, first name then last name.
     */
    public Player(String name1) {
        this.name = name1;
        this.tournaments = new ArrayList<TournamentPlacing>();
    }

    /**
     * Resets this object to its default representation.
     *
     * @ensures this object is equal to what is called by the no-args
     *          constructor.
     */
    public final void clear() {
        this.createNewRep();
    }

    /**
     * Method to set the name of the player.
     *
     * @param newName
     *            Handle to set this player's name to.
     * @ensures name = newName
     */
    public final void setName(String newName) {
        this.name = newName;
    }

    /**
     * Returns the private field name of this player.
     *
     * @return Full name of this player.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Returns the total points of this player.
     *
     * @return Total amount of points earned by this player.
     */
    public final int getPoints() {
        int toReturn = 0;

        for (TournamentPlacing tp : this.tournaments) {
            toReturn += tp.points();
        }

        return toReturn;
    }

    /**
     * Method to add a new {@code TournamentPlacing} to this object.
     *
     * @param x
     *            {@code TournamentPlacing} to add.
     */
    public final void addTourney(TournamentPlacing x) {
        this.tournaments.add(x);
    }

    /**
     * Simple method to print each tourney placement this {@code Player} has
     * had. They are sorted by date, and each is printed on its own html table
     * row.
     *
     * @param out
     *            Output stream to output to.
     * @requires {@code out} is open.
     * @ensures {@code out} is open.
     */
    public final void printTournies(PrintWriter out) {
        DateOrder comp = new DateOrder();
        this.tournaments.sort(comp);

        for (TournamentPlacing x : this.tournaments) {
            out.println("<tr>");
            String newDate = x.date().toString();
            newDate = newDate.substring(0, 10)
                    + newDate.substring(23, newDate.length());
            out.println("<td>" + newDate + "</td>");
            out.println("<td><a href = \"" + x.link() + "\">" + x.tourneyName()
                    + "</a></td>");
            out.println("<td>" + x.placement() + "</td>");
            out.println("<td>" + x.points() + "</td>");
            out.println("</tr>");

        }

    }

    @Override
    public final String toString() {
        /*
         * WARNING -- Breaking kernel purity rule here. Be aware, but it
         * shouldn't be a problem.
         */
        return this.name + ", Total Points: " + this.getPoints();
    }

    /**
     * Compare {@code TournamentPlacings} by their dates.
     *
     * Should the dates be the same, it will then compare the names. If the
     * names are the same, it will then compare the placings. If the placings
     * are the same, it will then compare the points.
     *
     * Only if all four fields are the same will it return 0.
     */
    private static class DateOrder implements Comparator<TournamentPlacing> {

        @Override
        public int compare(TournamentPlacing o1, TournamentPlacing o2) {
            int toReturn;

            if (o1.date().compareTo(o2.date()) == 0) {
                if (o1.tourneyName().equals(o2.tourneyName())) {
                    if (o1.points() == o2.points()) {
                        if (o1.placement() > o2.placement()) {
                            toReturn = -1;
                        } else if (o1.placement() == o2.placement()) {
                            toReturn = 0;
                        } else {
                            toReturn = 1;
                        }
                    } else {
                        if (o1.points() > o2.points()) {
                            toReturn = -1;
                        } else {
                            toReturn = 1;
                        }
                    }
                } else {
                    return o1.tourneyName().compareTo(o2.tourneyName());
                }
            } else {
                toReturn = o1.date().compareTo(o2.date());
            }

            return toReturn;
        }
    }
}
