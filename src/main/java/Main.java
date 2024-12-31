import java.io.File;
import java.io.FileNotFoundException;
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

        while (true) {
            player.allowCreatureToAct();
            player.act();
            handleBotPlayerContact(player, bot);
            while (freeze);
            bot.allowCreatureToAct();
            bot.act();
            handleBotPlayerContact(player, bot);
            while (freeze);
        }
    }

    private static String getMapAsString() {
        Scanner consoleScanner = new Scanner(System.in);
        System.out.println("Map file path:");
        String path = consoleScanner.nextLine();
        try {
            File file = new File(path);
            Scanner fileScanner = new Scanner(file);
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
    
    private static void handleBotPlayerContact(Creature player, Creature bot) {
        if (checkBotContactingPlayer(player, bot)) {
            System.out.println("LOSE (Bot Caught The Player)");
            Main.freeze = true;
        }
    }
}
