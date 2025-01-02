import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static boolean freeze;

    public static void main(String[] args) {
        //Load map.
        String mapString = getMapAsString();
        DungeonMap map = DungeonMap.fromString(mapString);

        //Spawn creatures.
        CreatureArgs creatureArgs = new CreatureArgs();
        Vec playerSpawn = map.getSpawnLocation();
        creatureArgs.x = playerSpawn.x;
        creatureArgs.y = playerSpawn.y;
        creatureArgs.map = map;
        creatureArgs.controller = new HumanController();
        creatureArgs.letter = 'P';
        creatureArgs.sightHeight = 5;
        creatureArgs.sightWidth = 5;
        Creature player = new Creature(creatureArgs);
        Vec botSpawn = map.getSpawnLocation();
        creatureArgs.x = botSpawn.x;
        creatureArgs.y = botSpawn.y;
        creatureArgs.controller = new BotController();
        creatureArgs.letter = 'B';
        Creature bot = new Creature(creatureArgs);

        //Turn while loop.
        while (true) {
            //Enable for debug full map view.
            System.out.println(MapRenderer.renderAsString(new MapView(map, 0, 19, 0, 8)));

            //allowCreatureToAct() must be called before act to enforce the one command per turn rule.
            player.allowCreatureToAct();
            player.act();
            handleBotPlayerContact(player, bot);
            //The game should be frozen after a win or loss so players can read the text, and does so by setting freeze to true.
            while (freeze);
            bot.allowCreatureToAct();
            bot.act();
            handleBotPlayerContact(player, bot);
            while (freeze);
        }
    }

    /***
     * Gets the map from a file from a path read from console.
     * @return The string read from the file.
     */
    private static String getMapAsString() {
        Scanner consoleScanner = new Scanner(System.in);
        System.out.println("Map file path:");
        String path = consoleScanner.nextLine();
        File file = new File(path);
        try (Scanner fileScanner = new Scanner(file)) {
            StringBuilder mapString = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                String data = fileScanner.nextLine();
                mapString.append(data).append("\n");
            }
            return mapString.toString();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred. Try again.");
            return getMapAsString();
        }
    }

    private static boolean checkBotContactingPlayer(Creature player, Creature bot) {
        return player.getX() == bot.getX() && player.getY() == bot.getY();
    }

    /***
     * Checks if the bot is on the same tile as the player, in which case it prints the game loss text.
     * @param player The player creature.
     * @param bot The bot creature.
     */
    private static void handleBotPlayerContact(Creature player, Creature bot) {
        if (checkBotContactingPlayer(player, bot)) {
            System.out.println("LOSE (Bot Caught The Player)");
            Main.freeze = true;
        }
    }
}
