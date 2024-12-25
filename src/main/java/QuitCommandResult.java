public class QuitCommandResult extends CommandResult {
    public boolean win;

    public QuitCommandResult(boolean success, boolean win) {
        super(success);
    }
}
