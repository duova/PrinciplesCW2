public class GoldCommandResult extends CommandResult {
    public int goldOwned;

    public GoldCommandResult(boolean success, int goldOwned) {
        super(success);
        this.goldOwned = goldOwned;
    }
}
