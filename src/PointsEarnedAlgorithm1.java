import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 * Main path for a set of classes to compute scores and other variables for
 * local smash tourneys. Extremely alpha stage at this point.
 *
 * @author Joe Forsman
 *
 */
public final class PointsEarnedAlgorithm1 {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private PointsEarnedAlgorithm1() {
    }

    /**
     * Compute the amount of points earned for a tournament placement, modified
     * for improvement or bustering out.
     *
     * A ratchet effect is added to reduce the downward modification of points
     * if you place worse than you're seeded to, in order to create and upward
     * spiral of point values and create an environment more of improvement and
     * increase than punishment.
     *
     * @param defaultPoints
     *            the amount of points the finish would be worth, unmodified.
     *
     * @param seed
     *            seed of the competitor entering the tournament.
     *
     * @param placement
     *            placement of the competitor at the end of the tournament.
     *
     * @requires seed > 0, placement > 0, defaultPoints > 0
     *
     * @return modified value of points earned.
     */
    private static int computePointsEarned(int defaultPoints, int seed,
            int placement) {
        //creating a double value to not round any values until the end
        double defPoints1 = defaultPoints;
        double difference = (double) seed - placement;

        /*
         * A value designed to reduce the impact of scoring poorly. It is set to
         * a much higher value (and difference is then divided by this) if
         * difference is negative, to produce a ratchet effect, to create an
         * upward spiral of points
         */
        int ratchet = 0;

        if (difference < 0) {
            ratchet = 35;
        } else {
            ratchet = 10;
        }

        return (int) (defPoints1 * (1 + ((difference / ratchet))));
    }

    /**
     * Quick method to output a single line to an output file, for this program
     * only.
     *
     * @param out
     *            the output stream
     * @param defaultVal
     *            the default value, before calculation
     * @param seed
     *            seed of the player used in computePointsEarned()
     * @param placement
     *            placement used in computePointsEarned()
     * @param newVal
     *            value returned by a computePointsEarned call with the above
     *            values.
     *
     * @requires out.isOpen
     */
    private static void outputLine(PrintWriter out, int defaultVal, int seed,
            int placement, int newVal) {
        //adding zeroes to seeds, new values, and placements for uniformity
        String seed1, place1, newVal1;

        if (seed < 10) {
            seed1 = "0" + seed;
        } else {
            seed1 = "" + seed;
        }

        if (placement < 10) {
            place1 = "0" + placement;
        } else {
            place1 = "" + placement;
        }

        if (newVal < 100) {
            newVal1 = "0" + newVal;
        } else {
            newVal1 = "" + newVal;
        }

        //outputting
        out.println("   " + defaultVal + "   ||   " + seed1 + "   ||    "
                + place1 + "     ||   " + newVal1);
    }

    /**
     * Main method -- used essentially as a testing method right now.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        //debugging counter of times run;
        int executionCounter = 0;

        //printing warning for immaturity
        System.out.println("***Notice: This is an extremely alpha build"
                + " and thus has no data validation.***");
        System.out.println("***Entering strings or floats "
                + "or anything will crash the program.***\n");

        //sentinel for data validation on the path selection
        boolean sentinelPath = true;

        while (sentinelPath) {
            /*
             * Prompting user for mode entry.
             * 
             * NOTE: 1, for demo, can be used anywhere, but 2, for random output
             * to file, is specialized for workspace output and should only be
             * used in a sanitized IDE.
             */
            System.out
                    .print("Select a mode to demo. 1 for case by case, 2 for random "
                            + "output, 3 for quit: ");
            int path = in.nextInt();

            switch (path) {
                case 1:
                    /*
                     * Entering and testing variables on a case by case
                     * situation, good for in person testing.
                     */

                    //sentinel variable used to end/continue repeating execution loop
                    boolean sentinel = true;

                    do {
                        //incrementing run-counter
                        executionCounter++;

                        //input values
                        int defVal, seedVal, placeVal;

                        //spacing
                        System.out.println("\n***EXECUTION RUN: "
                                + executionCounter + "***");

                        //getting values from the user
                        System.out.print("Enter the seed of the player: ");
                        seedVal = in.nextInt();

                        System.out
                                .print("Enter the final placement of the player: ");
                        placeVal = in.nextInt();

                        System.out
                                .print("Enter the default value of the placement: ");
                        defVal = in.nextInt();

                        //processing values and outputting
                        System.out
                                .println("Total Points awarded: "
                                        + computePointsEarned(defVal, seedVal,
                                                placeVal));

                        //updating sentinel
                        System.out
                                .print("Enter 1 to continue, anything else to quit: ");

                        /*
                         * Checking input here. This contains some of the only
                         * data validation thus far in the program, as it parse
                         * the input as a string, so that "any other input" will
                         * take basically any string input and not crash.
                         */
                        if (Integer.parseInt(in.next()) != 1) {
                            sentinel = false;
                        }
                    } while (sentinel);

                    System.out.println("\n***EXITING EXECUTION AFTER "
                            + executionCounter + " RUNS***");
                    break;
                case 2:
                    /*
                     * Entering execution for randomly generated values to an
                     * output file, good for internal testing.
                     */

                    /*
                     * sentinel variable to ensure that a proper filename is
                     * chosen and used to create a writer variable.
                     */
                    boolean failed = false;

                    do {
                        //getting output filename
                        System.out.print("Enter a filename to output"
                                + " to, including extension: ");

                        //handling exceptions
                        try {
                            PrintWriter writer = new PrintWriter(in.next(),
                                    "UTF-8");

                            //getting number of times to run the algorithm
                            System.out
                                    .print("Enter number of times to execute: ");
                            int times = in.nextInt();

                            //outputting header to the file
                            writer.println("Num  || Default ||  Seed  || Placement || Points Earned");

                            for (int i = 0; i < times; i++) {
                                /*
                                 * Generating values to perform calculation.
                                 * Using 1-16 for the seeds and placement as
                                 * they are the usual tourney attendance
                                 * numbers. Default value is 100 so it can be
                                 * converted to a percentage.
                                 * 
                                 * .5 is used for rounding before casting to an
                                 * int.
                                 */

                                /*
                                 * Outputting the number of execution. Zeroes
                                 * have been slightly formatted, I'm sure theres
                                 * a better way to do it, oh well.
                                 */
                                if (i < 9) {
                                    writer.print("00" + (i + 1) + "  ||");
                                } else if (i < 99) {
                                    writer.print("0" + (i + 1) + "  ||");
                                } else {
                                    writer.print((i + 1) + "  ||");
                                }

                                int seed1 = (int) (((Math.random() * 15) + 1) + .5);
                                int place1 = (int) (((Math.random() * 15) + 1) + .5);
                                int returned = computePointsEarned(100, seed1,
                                        place1);

                                outputLine(writer, 100, seed1, place1, returned);
                            }

                            writer.close();
                        } catch (FileNotFoundException e) {
                            System.out
                                    .println("FileNotFoundException thrown. Enter a "
                                            + "correct filename next time.");

                            //forcing loop to continue until filename entered correctly
                            failed = true;
                        } catch (UnsupportedEncodingException e) {
                            //should never occur

                            //dummy statement to satisfy compiler
                            System.out.print("");
                        }
                    } while (failed);

                    break;
                case 3:
                    sentinelPath = false;
                    break;
                default:
                    System.out.println("Input not recognized, try again.");
                    break;
            }
        }

        /*
         * Close input stream
         */
        in.close();
    }
}
