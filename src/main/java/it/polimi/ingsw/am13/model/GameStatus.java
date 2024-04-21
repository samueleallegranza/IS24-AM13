package it.polimi.ingsw.am13.model;

/**
 * Represents phases of the flow of the game
 * <ul>
 *     <li>Creation of the game itself doesn't have a game phase. In this pre-game status, decks are created
 *     and already shuffled. The common cards are so set and can be considered already in field.
 *     It is also selected randomly the first player </li>
 *     <li><code>INIT</code>: Initialization, during which players are given their first cards.
 *     Each player must play their starter card and choose their personal objective card </li>
 *     <li><code>IN_GAME</code>: The turn-based phase. Each player, starting from the first player chosen, play their
 *     turn by placing a card on the field and subsequently picking a card from the six "pickable" cards in the common field.
 *     This phase ends when one of the player reaches 20 points after playing a card, or when both of the decks
 *     are empty after picking a card. </li>
 *     <li><code>FINAL_PHASE</code>: The last turn-based phase. It's the same as IN_GAME, but it lasts for a determined
 *     number of turns, calculated when phase IN_GAME becomes FINAL_PHASE, in order to finish round (reaching the turn
 *     of the first player) and play another extra round. In the end, all players must play the same number of turns </li>
 *     <li><code>CALC_POINTS</code>: Now the turn-bases phases are completed, and players don't have to do anything.
 *     In this phase, the extra points given by the objective card (2 common, 1 personal) are added to the players' score. </li>
 *     <li><code>ENDED</code>: After adding the extra points, the winner can be calculated as the player with
 *     the maximum accumulated points </li>
 * </ul>>
 */
public enum GameStatus {
    INIT,IN_GAME,FINAL_PHASE,CALC_POINTS,ENDED
}
