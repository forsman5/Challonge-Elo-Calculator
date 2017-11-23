import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Program to generate pools taking one player from each of four groups.
 * Designed to be done fast many times in order to find a satisfactory grouping.
 *
 * Works with one group per pool, thus n(groups) must equal n(pools).
 *
 * @author Joe Forsman
 *
 */
public final class ChampionshipPools {

    /**
     * Private constructor to prevent initialization of this utility class.
     */
    private ChampionshipPools() {
    }

    /**
     * Main method. May be configured for output / input method.
     *
     * @param args
     *            Command-line arguments.
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Scanner file;
        int groupNumber;
        int groupSize;
        Queue<String> allPlayers = new ConcurrentLinkedQueue<String>();
        Queue<PlayerChosen> allPlayerChosens = new ConcurrentLinkedQueue<PlayerChosen>();
        ArrayList<TreeSet<PlayerChosen>> allGroups = new ArrayList<TreeSet<PlayerChosen>>();
        ArrayList<TreeSet<String>> allPools = new ArrayList<TreeSet<String>>();
        PrintWriter output;

        //user warning
        System.out.println("WARNING -- DATA IS UNSANITIZED.");

        System.out.print("Enter the size of groups to build: ");
        groupSize = Integer.parseInt(in.nextLine());

        System.out
        .println("This program will use a file with each player's tag"
                + " on it's own line.");
        System.out
        .println("The top seeded player should be first and the bottom "
                + "seed should be last.");
        System.out.print("Enter the name of that file, including extension: ");

        try {
            file = new Scanner(new File(in.nextLine()));

            System.out
            .print("Enter the name of a file to output the groups to: ");

            output = new PrintWriter(in.nextLine());

            //emptying input file and filling queue
            while (file.hasNext()) {
                allPlayers.add(file.nextLine());
            }

            //determining the number of groups
            groupNumber = allPlayers.size() / groupSize; //TODO double math?

            while (allPlayers.size() > 0) {
                allPlayerChosens.add(new PlayerChosen(allPlayers.poll()));
            }

            for (int i = 0; i < groupNumber; i++) {
                TreeSet<PlayerChosen> toAdd = new TreeSet<PlayerChosen>();

                while (toAdd.size() < groupSize) {
                    toAdd.add(allPlayerChosens.poll());
                }

                allGroups.add(toAdd);
            }

            //filling pools
            for (int i = 0; i < groupNumber; i++) {
                TreeSet<String> toAdd = new TreeSet<String>();
                int random = (int) (Math.random() * groupSize + .5);

                for (TreeSet<PlayerChosen> group : allGroups) {
                    int count = 0;
                    boolean added = false;

                    for (PlayerChosen player : group) {
                        if (count < random) {
                            count++;
                        } else if (!added) {
                            if (!player.isChosen()) {
                                added = true;
                                player.choose();
                                toAdd.add(player.tag());
                            }
                        }
                    }

                }

                allPools.add(toAdd);
            }

            //outputting
            char count = 'a';
            for (TreeSet<String> pool : allPools) {
                output.write("Pool " + count + ":\n");
                System.out.println("Pool " + count + ":\n");
                for (String name : pool) {
                    output.write(name + "\n");
                    System.out.println(name + "\n");
                }
                System.out.println("\n");
                count++;
            }

            /*
             * Close input and output streams.
             */
            in.close();
            file.close();
            output.close();
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND -- PROGRAM ABORTING.");
        }
    }
}
