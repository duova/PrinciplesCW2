public class PickUpCommandResult extends CommandResult {
    public int currentGold;

    public PickUpCommandResult(boolean success, int currentGold) {
        super(success);
        this.currentGold = currentGold;
    }
}