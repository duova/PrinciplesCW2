public interface Controller {

    /***
     * Links the controller to a player that it will call commands on.
     * @param player The creature to link the controller to.
     */
    void linkPlayer(Creature player);

    /***
     * Implementation of what the controlled player should do each turn.
     */
    void executeTurn();
}