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
                HelloCommandResult helloResult = player.helloCommand();
                System.out.println("Gold to win: " + helloResult.goldToWin);
                break;
            case "GOLD":
                GoldCommandResult goldResult = player.goldCommand();
                System.out.println("Gold owned: " + goldResult.goldOwned);
                break;
            case "PICKUP":
                PickUpCommandResult pickUpResult = player.pickUpCommand();
                if (!pickUpResult.success) {
                    System.out.println("Fail");
                    break;
                }
                System.out.println("Success. Gold owned: " + pickUpResult.currentGold);
                break;
            case "LOOK":
                LookCommandResult lookResult = player.lookCommand();
                System.out.println(MapRenderer.renderAsString(lookResult.visibleMap));
                break;
            case "QUIT":
                QuitCommandResult quitResult = player.quitCommand();
                if (quitResult.win) {
                    System.out.println("Win");
                }
                else {
                    System.out.println("Lose");
                }
                Main.freeze = true;
                break;
            default:
                if (command.length() > 4
                        && command.startsWith("MOVE")
                        && (command.endsWith("N")
                        || command.endsWith("E")
                        || command.endsWith("S")
                        || command.endsWith("W"))) {
                    CommandResult moveResult = player.moveCommand(Direction.valueOf(command.substring(command.length() - 2, command.length() - 1)));
                    System.out.println(moveResult.success ? "Success" : "Fail");
                }
                System.out.println("Invalid command, try again:");
                executeTurn();
                break;
        }
    }
}
