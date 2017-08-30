import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * File to represent a placement in a tournament. This is designed to be used in
 * the {@code Player} class, to fill the array of all the placings of this
 * {@code Player}.
 *
 * @author Joe Forsman
 *
 */
public class TournamentPlacing {

    /**
     * Name of the tournament that this placement was earned at.
     */
    private String tourneyName;

    /**
     * Points that this placement was worth.
     */
    private int pointsGained;

    /**
     * Place that the owner of this object placed at in this tournament.
     */
    private int placement;

    /**
     * Date that this tournament took place.
     */
    private Date date;

    /**
     * Link to the bracket.
     */
    private String link;

    /**
     * Default and only constructor.
     *
     * @param name
     *            Name of the tournament.
     * @param points
     *            Points earned for the placement.
     * @param place
     *            Placing at this tournament.
     * @param stringDate
     *            Date this tournament happened, in the format mm/dd/yyyy
     * @param linky
     *            Link to the bracket.
     */
    public TournamentPlacing(String name, int points, int place,
            String stringDate, String linky) {
        this.placement = place;
        this.pointsGained = points;
        this.tourneyName = name;
        this.link = linky;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        try {
            this.date = dateFormat.parse(stringDate);
        } catch (ParseException e) {
            System.out.println("Unexpected error: Code 6");
            System.out.println("Incorrect date formatting encountered.");
            System.out.println("Date: " + stringDate + ", Tournament: " + name);
        }
    }

    /**
     * Accessor method for {@code pointsGained} at this tourney.
     *
     * @return Points earned for this placement.
     */
    public final int points() {
        return this.pointsGained;
    }

    /**
     * Accessor for link.
     * 
     * @return the link to the bracket of this tournament.
     */
    public final String link() {
        return this.link;
    }

    /**
     * Accessor method for the placement of the owner at this tournament.
     *
     * @return Placement at this tournament.
     */
    public final int placement() {
        return this.placement;
    }

    /**
     * Accessor name for {@code tourneyName}.
     *
     * @return Name of this tournament.
     */
    public final String tourneyName() {
        return this.tourneyName;
    }

    /**
     * Accessor method for the date of this tournament.
     *
     * @return Date this tourney occured on.
     */
    public final Date date() {
        return this.date;
    }

    @Override
    public final String toString() {
        String placement1;

        if (this.placement % 10 == 1) {
            placement1 = this.placement + "st";
        } else if (this.placement % 10 == 2) {
            placement1 = this.placement + "nd";
        } else if (this.placement % 10 == 3) {
            placement1 = this.placement + "rd";
        } else {
            placement1 = this.placement + "th";
        }

        return this.date.toString() + "\t\t" + this.tourneyName + "\t"
        + placement1 + "\t" + this.pointsGained;
    }
}
