import java.util.Scanner;

public class HumanController implements Controller {
    Creature player;

    @Override
    public void linkPlayer(Creature player) {
        this.player = player;
    }

    @Override
    public void executeTurn() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter command:");
        String command = scanner.nextLine().toUpperCase();

        switch (command) {
            case "HELLO":
                player.helloCommand();
                break;
            case "GOLD":
                player.goldCommand();
                break;
            case "PICKUP":
                player.pickUpCommand();
                break;
            case "LOOK":
                player.lookCommand();
                break;
            case "QUIT":
                player.quitCommand();
                break;
            default:
                if (command.length() > 4
                        && command.startsWith("MOVE")
                        && (command.endsWith("N")
                        || command.endsWith("E")
                        || command.endsWith("S")
                        || command.endsWith("W"))) {
                    player.moveCommand(Direction.valueOf(command.substring(command.length() - 2, command.length() - 1)));
                }
                System.out.println("Invalid command, try again:");
                executeTurn();
                break;
        }
    }
}
