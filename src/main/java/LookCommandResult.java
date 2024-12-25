public class LookCommandResult extends CommandResult {
    public MapView visibleMap;

    public LookCommandResult(boolean success, MapView visibleMap) {
        super(success);
        this.visibleMap = visibleMap;
    }
}
