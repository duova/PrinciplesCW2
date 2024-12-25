public class HelloCommandResult extends CommandResult {
    public int goldToWin;

    public HelloCommandResult(boolean success, int goldToWin) {
        super(success);
        this.goldToWin = goldToWin;
    }
}
