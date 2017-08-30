import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class with a main method used to compile multiple tournament data sets to a
 * single file, showing each player's record against each other in games and
 * sets.
 *
 * Package-wide convention -- tags of player may not contain the character '-'.
 * Consider this a precondition to every method.
 *
 * @author Joe Forsman
 *
 */
public final class CompileStats {

    /*
     * Fields
     *
     * These are here for easy changing of directories, as the program takes no
     * parameters.
     */

    /**
     * Location of the configuration file, used to determine the values of all
     * the other fields.
     */
    private static final String CONFIG_PATH = "\\doc\\config.cfg";

    /**
     * The default place to output each of the files for each player.
     */
    private static String OUTPUT_DIR = "\\data\\records\\";

    /**
     * The file name of the main file containing all lower files. Will be output
     * inside {@code OUTPUT_DIR}.
     */
    private static String OUTPUT_TOP_FILE_NAME = "index.html";

    /**
     * Where to find the tournament data. Change this to
     * "/data/tournaments/2015/ to find only stats from 2015, for example.
     */
    private static String DIR_PATH = "\\data\\tournaments\\";

    /**
     * Keyword to signify that the matches will immediately follow this line in
     * a tournament file (.tm), and continue until the end of the file.
     */
    private static String MATCHES_KEYWORD = "Matches";

    /**
     * The directory, when added onto {@code DIR_PATH}, where no points should
     * be added from these tournies. This is used to reset the rankings after
     * the end of a season.
     */
    private static String DEFUNCT_DIR = "\\defunct_seasons\\";

    /**
     * Variable to store the amount of days that a player has not been to a
     * tournament before the player is considered inactive. Default is 84, or
     * three months.
     */
    private static int DAYS_BEFORE_INACTIVE = 84;

    /**
     * Variable to store the most recent tourney. This is used to determine when
     * players are inactive, using {@code DAYS_BEFORE_INACTIVE}.
     */
    private static String MOST_RECENT_TOURNEY = null;

    /**
     * Private constructor to prevent this utility class from being
     * instantiated.
     */
    private CompileStats() {
    }

    /**
     * Reads from the config.cfg file to fill all other fields. Allows for
     * end-user path customization.
     */
    private static void fillFields() {
        File currentDir = new File(".");
        String dir = currentDir.getPath();

        File configFile = new File(dir + CONFIG_PATH);
        try {
            Scanner in = new Scanner(configFile);

            while (in.hasNextLine()) {
                String line = in.nextLine();

                //ignore commented out lines
                if (line.charAt(0) != '#') {
                    int pivot = line.indexOf('=');
                    String varName = line.substring(0, pivot - 1);
                    String varVal = line.substring(pivot + 2, line.length());

                    switch (varName) {
                        case "OUTPUT_TOP_FILE_NAME":
                            OUTPUT_TOP_FILE_NAME = varVal;
                            break;
                        case "OUTPUT_DIR":
                            OUTPUT_DIR = varVal;
                            break;
                        case "DIR_PATH":
                            DIR_PATH = varVal;
                            break;
                        case "MATCHES_KEYWORD":
                            MATCHES_KEYWORD = varVal;
                            break;
                        case "DEFUNCT_DIR":
                            DEFUNCT_DIR = varVal;
                            break;
                        case "DAYS_BEFORE_INACTIVE":
                            DAYS_BEFORE_INACTIVE = Integer.parseInt(varVal);
                            break;
                        default:
                            System.out
                                    .println("Unexpected error encountered: Code 5.");
                            System.out.println("Offending Line: " + varName);
                            break;
                    }
                }
            }

            /*
             * Closing output stream.
             */
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unexpected error encountered: Code 4.");
            System.out
                    .println("Could not find configuration file. Did you move it?");
            System.out.println("Correct path is: " + configFile.getPath());
        }
    }

    /**
     * Reads the {@code line} parameter and returns it as a Match.
     *
     * @param line
     *            String to be read from.
     * @param tourneyName
     *            Name of the tournament where the set that will be represented
     *            by the return value took place.
     * @return line as a {@code Match}, or null if the input was incorrect.
     * @requires line is a valid match representation. See examples in
     *           /data/tournamentExample.txt. Any line below the first is a
     *           valid input for this method.
     */
    public static Match parseMatch(String line, String tourneyName) {
        int pivot = line.indexOf("-");
        int winner;
        int score1 = Integer.parseInt(line.substring(pivot - 1, pivot));
        int score2 = Integer.parseInt(line.substring(pivot + 1, pivot + 2));
        Match toReturn;

        if (score1 > score2) {
            winner = 1;
        } else {
            winner = 2;
        }

        String name1 = line.substring(0, pivot - 2);
        String name2 = line.substring(pivot + 3, line.length());

        if (winner == 1) {
            toReturn = new Match(name1, name2, score1, score2, tourneyName);
        } else {
            toReturn = new Match(name2, name1, score2, score1, tourneyName);
        }

        return toReturn;
    }

    /**
     * Reads a file that represents a tournament and returns a {@code ArrayList}
     * of all the {@code Match}es that took place there.
     *
     * @param fileName
     *            File representing the tourney to be parsed. Must include full
     *            extension.
     * @return A {@code ArrayList} of {@code Match}es that took place at this
     *         tourney.
     * @requires {@code fileName} must be in the correct format. The file
     *           data/tournamentExample is a file in the valid format.
     */
    public static ArrayList<Match> readTournamentMatches(String fileName) {
        ArrayList<Match> toReturn = new ArrayList<Match>();

        try {
            Scanner in = new Scanner(new File(fileName));

            //sentinel to find where the matches start in each file
            boolean matchesFound = false;

            String tourneyName = in.nextLine();

            while (in.hasNextLine() && !matchesFound) {
                //discarding lines until time to read matches
                String toTest = in.nextLine();
                if (toTest.equals(MATCHES_KEYWORD)) {
                    matchesFound = true;
                }
            }

            if (!matchesFound) {
                System.out
                .println("Unexpected Error Encountered: Error Code 3.");
                System.out.println("Keyword \"" + MATCHES_KEYWORD
                        + "\" not found in file " + fileName);
            }

            while (in.hasNextLine()) {
                toReturn.add(parseMatch(in.nextLine(), tourneyName));
            }

            //closing input stream
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unknown Exception: " + e);
        }

        return toReturn;
    }

    /**
     * Performs {@code readTournament} on every file in a directory, returning a
     * single {@code ArrayList} of every {@code Match} in that directory.
     *
     * @param dirName
     *            Name of the directory to parse. Must be a valid path.
     * @return A {@code ArrayList} of all the {@code Match}es in {@code dirName}
     *         .
     */
    public static ArrayList<Match> readTournamentsMatches(String dirName) {
        File dir = new File(dirName);
        ArrayList<Match> toReturn = new ArrayList<Match>();

        for (File fileEntry : dir.listFiles()) {
            ArrayList<Match> toAdd;

            if (fileEntry.isDirectory()) {
                toAdd = readTournamentsMatches(fileEntry.getPath());
            } else {
                toAdd = readTournamentMatches(fileEntry.getPath());
            }

            for (Match m : toAdd) {
                toReturn.add(m);
            }
        }

        return toReturn;
    }

    /**
     * Performs {@code readTournament} on every file in a directory, returning a
     * single {@code ArrayList} of every {@code TournamentPlacing} in that
     * directory.
     *
     * @param dirName
     *            Name of the directory to parse. Must be a valid path.
     * @return A {@code ArrayList} of all the {@code TournamentPlacing}s in
     *         {@code dirName}.
     */
    private static Map<String, ArrayList<TournamentPlacing>> readTournamentsPlacings(
            String dirName) {
        File dir = new File(dirName);
        Map<String, ArrayList<TournamentPlacing>> toReturn = new TreeMap<>();

        for (File fileEntry : dir.listFiles()) {
            Map<String, ArrayList<TournamentPlacing>> toAdd;

            if (fileEntry.isDirectory()) {
                toAdd = readTournamentsPlacings(fileEntry.getPath());
            } else {
                if (fileEntry.getPath().contains(DEFUNCT_DIR)) {
                    toAdd = readTournamentPlacings(fileEntry.getPath(), true);
                } else {
                    toAdd = readTournamentPlacings(fileEntry.getPath(), false);
                }
            }

            Set<Entry<String, ArrayList<TournamentPlacing>>> toAdd1 = toAdd
                    .entrySet();
            for (Entry<String, ArrayList<TournamentPlacing>> t : toAdd1) {
                if (toReturn.containsKey(t.getKey())) {
                    ArrayList<TournamentPlacing> tempToBuild = toReturn.get(t
                            .getKey());
                    ArrayList<TournamentPlacing> tempToDrain = t.getValue();

                    for (TournamentPlacing placing : tempToDrain) {
                        tempToBuild.add(placing);
                    }
                } else {
                    toReturn.put(t.getKey(), t.getValue());
                }
            }
        }

        return toReturn;
    }

    /**
     * Reads a file that represents a tournament and returns a {@code ArrayList}
     * of all the {@code TournamentPlacing} of every player's finishing.
     *
     * @param fileName
     *            File representing the tourney to be parsed. Must include full
     *            extension.
     * @param defunct
     *            True if this file is in the defunct folder, false otherwise.
     * @return A {@code ArrayList} of {@code TournamentPlacing}s that took place
     *         at this tourney.
     * @requires {@code fileName} must be in the correct format. The file
     *           data/tournamentExample is a file in the valid format.
     */
    private static Map<String, ArrayList<TournamentPlacing>> readTournamentPlacings(
            String fileName, boolean defunct) {
        Map<String, ArrayList<TournamentPlacing>> toReturn = new TreeMap<>();

        try {
            Scanner in = new Scanner(new File(fileName));

            //sentinel to find where the matches start in each file
            boolean matchesFound = false;

            String tourneyName = in.nextLine();
            String tourneyLink = in.nextLine();
            //will be assigned later, just used to force the compiler to accept it
            String tourneyDate = "";
            ArrayList<Integer> tourneyPointsArr = new ArrayList<Integer>();

            /*
             * Quick sentinel to determine if this is the first time a block has
             * executed. This is used to only dequeue once, but only once the
             * program is sure the second line of the tournament file is not
             * MATCHES_KEYWORD
             */
            boolean first = true;

            /*
             * Index used for connect the players name with points. Starts at
             * negative one because the first execution is simply reading the
             * date and such, not an actual tournament finish.
             */
            int pointsIndex = -1;

            while (in.hasNextLine() && !matchesFound) {
                String toTest = in.nextLine();

                if (toTest.equals(MATCHES_KEYWORD)) {
                    matchesFound = true;
                } else {
                    if (first) {
                        first = false;
                        tourneyDate = toTest;

                        //checking to see if this tourneyDate is the most recent date.
                        if (MOST_RECENT_TOURNEY == null) {
                            MOST_RECENT_TOURNEY = tourneyDate;
                        } else {
                            System.out.println(MOST_RECENT_TOURNEY);
                            int reFirstSlash = MOST_RECENT_TOURNEY.indexOf('/');
                            int reTempDays = Integer
                                    .parseInt(MOST_RECENT_TOURNEY.substring(0,
                                            reFirstSlash));
                            int reSecondSlash = MOST_RECENT_TOURNEY
                                    .lastIndexOf('/');
                            int reTempMonths = Integer
                                    .parseInt(MOST_RECENT_TOURNEY.substring(
                                            reFirstSlash + 1, reSecondSlash));
                            int reTempYears = Integer
                                    .parseInt(MOST_RECENT_TOURNEY
                                            .substring(reSecondSlash + 1));

                            int toFirstSlash = tourneyDate.indexOf('/');
                            int toTempDays = Integer.parseInt(tourneyDate
                                    .substring(0, toFirstSlash));
                            int toSecondSlash = tourneyDate.lastIndexOf('/');
                            int toTempMonths = Integer
                                    .parseInt(tourneyDate.substring(
                                            toFirstSlash + 1, toSecondSlash));
                            int toTempYears = Integer.parseInt(tourneyDate
                                    .substring(toSecondSlash + 1));

                            System.out.println(tourneyDate);

                            if (toTempYears > reTempYears) {
                                MOST_RECENT_TOURNEY = tourneyDate;
                            } else if (toTempYears == reTempYears) {
                                if (toTempMonths > reTempMonths) {
                                    MOST_RECENT_TOURNEY = tourneyDate;
                                } else if (toTempMonths == reTempMonths) {
                                    if (toTempDays > reTempDays) {
                                        MOST_RECENT_TOURNEY = tourneyDate;
                                    }
                                }
                            }
                        }

                        String tourneyPoints = in.nextLine();

                        //breaking up tourneyPoints
                        while (tourneyPoints.length() > 0) {
                            int slash = tourneyPoints.indexOf('/');

                            if (slash == -1) {
                                tourneyPointsArr.add(Integer
                                        .parseInt(tourneyPoints));
                                tourneyPoints = "";
                            } else {
                                tourneyPointsArr.add(Integer
                                        .parseInt(tourneyPoints.substring(0,
                                                slash)));

                                //shorten tourneyPoints
                                tourneyPoints = tourneyPoints.substring(
                                        slash + 1, tourneyPoints.length());
                            }
                        }
                    } else {
                        //checking for comment character
                        if (toTest.charAt(0) == '#') {
                            /*
                             * accounting for the automatic increase while not
                             * processing data due to a commented line
                             */
                            pointsIndex--;
                        } else {
                            int dotIndex = toTest.indexOf('.');

                            int placing = Integer.parseInt(toTest.substring(0,
                                    dotIndex));
                            String name = toTest.substring(dotIndex + 2,
                                    toTest.length());

                            ArrayList<TournamentPlacing> toAdd = new ArrayList<>();

                            if (!defunct) {
                                toAdd.add(new TournamentPlacing(tourneyName,
                                        tourneyPointsArr.get(pointsIndex),
                                        placing, tourneyDate, tourneyLink));
                            } else {
                                toAdd.add(new TournamentPlacing(tourneyName, 0,
                                        placing, tourneyDate, tourneyLink));
                            }

                            toReturn.put(name, toAdd);
                        }
                    }
                }

                pointsIndex++;
            }

            if (!matchesFound) {
                System.out
                .println("Unexpected Error Encountered: Error Code 3.");
                System.out.println("Keyword \"" + MATCHES_KEYWORD
                        + "\" not found in file " + fileName);
            }

            //closing input stream
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unknown Exception: " + e);
        }

        return toReturn;

    }

    /**
     * Main method.
     *
     * @param args
     *            Command-line arguments.
     */
    public static void main(String[] args) {
        //reading config file
        fillFields();

        File currentDir = new File(".");
        String dir = currentDir.getPath();

        Map<String, ArrayList<TournamentPlacing>> allPlacings = readTournamentsPlacings(dir
                + DIR_PATH);
        ArrayList<Match> allMatches = readTournamentsMatches(dir + DIR_PATH);
        Set<String> playerNames = new TreeSet<String>();
        Map<String, TreeMap<String, ArrayList<Match>>> matchups = new TreeMap<>();

        //ArrayList used here to sort later, even though all entries will be unique
        ArrayList<Player> players = new ArrayList<Player>();

        //filling the set of player names
        for (Match m : allMatches) {
            if (!playerNames.contains(m.winnerName())) {
                playerNames.add(m.winnerName());
            }

            if (!playerNames.contains(m.loserName())) {
                playerNames.add(m.loserName());
            }
        }

        for (String player : playerNames) {
            TreeMap<String, ArrayList<Match>> toAdd = new TreeMap<>();

            for (String opponent : playerNames) {
                //two strings taken from playerNames, as long as they're not the same.
                if (!player.equals(opponent)) {
                    ArrayList<Match> toAdd1 = new ArrayList<Match>();

                    for (Match ma : allMatches) {
                        /*
                         * The following if statement means that it will add the
                         * match to the queue if: (the winnerName of the match
                         * equals player AND the loserName of the match equals
                         * opp) OR (the winnerName of the match equals opp AND
                         * the loserName of the match equals player)
                         */
                        if ((ma.winnerName().equals(player) && ma.loserName()
                                .equals(opponent))
                                || (ma.winnerName().equals(opponent) && ma
                                        .loserName().equals(player))) {
                            toAdd1.add(ma);
                        }
                    }

                    toAdd.put(opponent, toAdd1);
                }
            }

            matchups.put(player, toAdd);

            //creating a Player object for every player, to add tournaments to later.
            players.add(new Player(player));
        }

        Set<Entry<String, ArrayList<TournamentPlacing>>> entriesTP = allPlacings
                .entrySet();

        //adding Tournament placings to player objects, and draining the allplacings map
        for (Player p : players) {
            String n = p.getName();

            for (Entry<String, ArrayList<TournamentPlacing>> en : entriesTP) {
                if (n.equals(en.getKey())) {
                    ArrayList<TournamentPlacing> toTransfer = en.getValue();

                    for (TournamentPlacing transferring : toTransfer) {
                        p.addTourney(transferring);
                    }
                }
            }
        }

        output(matchups, players);
    }

    /**
     * Outputs all the records to a set of files in the /data/ folder from this
     * directory. See /data/exampleOuput.txt to see an example of the output.
     * Notice that it accesses data/exampleOutputFiles/.
     *
     * @param records
     *            All the matchups to be outputted.
     * @param players
     *            All the players, with points and tournies.
     */
    public static void output(
            Map<String, TreeMap<String, ArrayList<Match>>> records,
            ArrayList<Player> players) {
        Set<String> names = records.keySet();
        Set<Entry<String, TreeMap<String, ArrayList<Match>>>> entrySet = records
                .entrySet();

        File currentDir = new File(".");
        String dir = currentDir.getPath();

        try {
            PrintWriter topFile = new PrintWriter(new FileWriter(dir
                    + OUTPUT_DIR + OUTPUT_TOP_FILE_NAME));
            outputHTMLHeaderTop(topFile);
            outputMatchupTotal(topFile, names, players);

            for (Entry<String, TreeMap<String, ArrayList<Match>>> entry : entrySet) {
                //if the IOException occurs here or before, no indication
                PrintWriter playerFile = new PrintWriter(new FileWriter(dir
                        + OUTPUT_DIR + entry.getKey() + ".html"));

                //finding the right player to send forward
                Player temp = new Player("");
                for (Player p : players) {
                    if (p.getName().equals(entry.getKey())) {
                        temp = p;
                    }
                }

                outputHTMLHeader(playerFile, entry.getKey());
                outputMatchupCharts(playerFile, entry.getValue(), temp);
                outputHTMLClosing(playerFile);
            }

            outputHTMLClosing(topFile);
        } catch (IOException e) {
            System.out.println("UNEXPECTED ERROR: CODE 2");
            System.out.println(e);
        }

    }

    /**
     * Outputs the standard html closing and closes {@code outFile}.
     *
     * @param outFile
     *            Output stream to output to.
     * @ensures outFile is closed.
     * @requires outFile is open.
     */
    private static void outputHTMLClosing(PrintWriter outFile) {
        outFile.println("</div>");
        outFile.println("</body>");
        outFile.println("</html>");

        outFile.close();
    }

    /**
     * Outputs the records of the player that is the key of {@code value}.
     *
     * @param outFile
     *            Output stream to output to.
     * @param value
     *            Every player this player has player against, and every set
     *            they've played.
     * @param player1
     *            The player object to output the tournament listing for.
     * @requires outFile is open.
     */
    private static void outputMatchupCharts(PrintWriter outFile,
            TreeMap<String, ArrayList<Match>> value, Player player1) {
        outFile.println(player1);
        outFile.println("<p></p>");

        outFile.println("<table border = \"1\">");
        outFile.println("<tr>");
        outFile.println("<td>Date</td>");
        outFile.println("<td>Name</td>");
        outFile.println("<td>Placement</td>");
        outFile.println("<td>Points Earned</td>");
        outFile.println("</tr>");
        player1.printTournies(outFile);
        outFile.println("</table>");

        Set<Entry<String, ArrayList<Match>>> entries = value.entrySet();

        for (Entry<String, ArrayList<Match>> e : entries) {
            ArrayList<Match> record = e.getValue();
            ArrayList<Match> wins = new ArrayList<Match>();
            ArrayList<Match> losses = new ArrayList<Match>();

            int setsWon = 0;
            int setsLost = 0;
            int gamesWon = 0;
            int gamesLost = 0;

            for (Match m : record) {

                if (m.loserName().equals(e.getKey())) {
                    //winner
                    setsWon++;
                    gamesWon += m.winnerGamesWon();
                    gamesLost += m.loserGamesWon();
                    wins.add(m);
                } else {
                    //loss, winnerName equals the key of e
                    setsLost++;
                    gamesWon += m.loserGamesWon();
                    gamesLost += m.winnerGamesWon();
                    losses.add(m);
                }
            }

            if (!wins.isEmpty() || !losses.isEmpty()) {
                outFile.println("<p></p>");
                outFile.println("<p><b>Against <a href = \"" + e.getKey()
                        + ".html\">" + e.getKey() + "</a>:</b></p>");

                for (Match m1 : wins) {
                    outFile.println("<p>" + m1 + "</p>");
                }

                for (Match m1 : losses) {
                    outFile.println("<p>" + m1.toStringLoserFirst() + "</p>");
                }

                //outputting total record
                outFile.println("<p>Total Record in Sets: " + setsWon + "-"
                        + setsLost + "</p>");
                outFile.println("<p>Total Record in Games: " + gamesWon + "-"
                        + gamesLost + "</p>");
                outFile.println("<p> </p>");
            }
        }
    }

    /**
     * Outputs each of the links to the files to see a player's matchups, and a
     * PR at the top of the file.
     *
     * @param outFile
     *            Output stream to output to.
     * @param names
     *            {@code Set} of names to output a line with a link to a file
     *            for them.
     * @param players
     *            All the players to make the PR with, with relevant point
     *            values.
     * @requires outFile is open.
     */
    private static void outputMatchupTotal(PrintWriter outFile,
            Set<String> names, ArrayList<Player> players) {
        File currentDir = new File(".");
        String dir = currentDir.getPath();

        PointsOrder comp = new PointsOrder();
        players.sort(comp);

        int rank = 1;
        for (Player p : players) {
            outFile.println("<p>" + rank + ". " + "<a href=\"" + dir + "\\"
                    + p.getName() + ".html" + "\"> " + p.getName()
                    + "</a> -- Points: " + p.getPoints() + "</p>");
            rank++;
        }
    }

    /**
     * Outputs the html opening for the top file.
     *
     * @param outFile
     *            Output stream to output to.
     * @requires outFile is open.
     */
    private static void outputHTMLHeaderTop(PrintWriter outFile) {
        outFile.println("<html>");
        outFile.println("<head>");
        outFile.println("<title>Powell Smash Rankings</title>");
        outFile.println("<link rel=\"stylesheet\" type=\"text/css\" "
                + "href=\"./resources/style.css\">");
        outFile.println("</head>");

        outFile.println("<body>");
        outFile.println("<div id=\"background\">");
        outFile.println("<img src=\"resources/background.png\" class=\"stretch\""
                + " alt=\"\" />");
        outFile.println("</div>");
        outFile.println("<div class=\"center\">");
        outFile.println("<img src=\"resources/logo.png\" width=\"100%\" "
                + "height=\"35%\" alt=\"logo\" />");
        outFile.println("<ul>");
        outFile.println("<li><a href=\"index.html\">About Us</a></li>");
        outFile.println("<li><a href=\"rankings.html\">Rankings</a></li>");
        outFile.println("<li><a href=\"tournaments.html\">Tournaments</a></li>");
        outFile.println("<li><a href =\"players.html\">Players</a></li>");
        outFile.println("<li><a href=\"blog.html\">Blog</a></li>");
        outFile.println("</ul>");
        outFile.println("<h2><b>Powell Power Rankings</b></h2>");
        outFile.println("<p><b>Click a player's name to see their records.</b></p>");
        outFile.println("<p><b>Last Updated: " + new Date() + "</b></p>");
    }

    /**
     * Outputs the HTML opening for a player's sheet.
     *
     * @param outFile
     *            Output stream to output to.
     * @param name
     *            Player this sheet is being made for.
     */
    private static void outputHTMLHeader(PrintWriter outFile, String name) {
        outFile.println("<html>");
        outFile.println("<head>");
        outFile.println("<title>Powell Smash -- " + name + "</title>");
        outFile.println("<link rel=\"stylesheet\" type=\"text/css\" "
                + "href=\"./resources/style.css\">");
        outFile.println("</head>");

        outFile.println("<body>");
        outFile.println("<div id=\"background\">");
        outFile.println("<img src=\"resources/background.png\" class=\"stretch\""
                + " alt=\"\" />");
        outFile.println("</div>");
        outFile.println("<div class=\"center\">");
        outFile.println("<img src=\"resources/logo.png\" width=\"100%\" "
                + "height=\"35%\" alt=\"logo\" />");
        outFile.println("<ul>");
        outFile.println("<li><a href=\"index.html\">About Us</a></li>");
        outFile.println("<li><a href=\"rankings.html\">Rankings</a></li>");
        outFile.println("<li><a href=\"tournaments.html\">Tournaments</a></li>");
        outFile.println("<li><a href =\"players.html\">Players</a></li>");
        outFile.println("<li><a href=\"blog.html\">Blog</a></li>");
        outFile.println("</ul>");
        outFile.println("<h2>Stats and Ranking for " + name + "</h2>");
    }

    /**
     * Compare {@code Player}s by their points.
     *
     * Should the points be the same, it will then compare the names.
     *
     * Only if both fields are the same will it return 0.
     */
    private static class PointsOrder implements Comparator<Player> {

        @Override
        public int compare(Player o1, Player o2) {
            int toReturn;

            if (o1.getPoints() == o2.getPoints()) {
                toReturn = o1.getName().compareTo(o2.getName());
            } else if (o1.getPoints() < o2.getPoints()) {
                toReturn = 1;
            } else {
                toReturn = -1;
            }

            return toReturn;
        }
    }
}
