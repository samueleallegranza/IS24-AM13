package it.polimi.ingsw.am13.client.chat;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.*;

/**
 * This class stores all the chat messages that meet the following requirements:
 * -they have thisPlayer as a sender or as one of the receivers
 * -the number of receivers is either one, or number of players-1 (all the players aside from the sender)
 */
public class Chat  {
    /**
     * This is the map that stores the chat messages, associating to every valid list of
     * PlayerLobby a list of ChatMessage
     */
    private final Map<List<PlayerLobby>,List<ChatMessage>> chat;
    /**
     * The player who is associated (owns) to a specific instance of this class
     */
    private final PlayerLobby thisPlayer;
    /**
     * All the other players who are in the same match as thisPlayer
     */
    private final List<PlayerLobby> otherPlayers;

    /**
     * This constructor sets the value of thisPlayer and otherPlayers, and initializes the chat by associating an
     * empty list to each valid list of PlayerLobby
     * @param players all the players of the match thisPlayer is in
     * @param thisPlayer the player who is associated to a specific instance of this class
     */
    public Chat(List<PlayerLobby> players, PlayerLobby thisPlayer) {
        this.thisPlayer = thisPlayer;
        this.chat = new HashMap<>();
        otherPlayers=new ArrayList<>();
        for(PlayerLobby player : players)
            if(!player.equals(thisPlayer)) {
                otherPlayers.add(player);
                chat.put(new ArrayList<>(Collections.singleton(player)), new ArrayList<>());
            }
        chat.put(otherPlayers,new ArrayList<>());
    }

    /**
     * Adds the chatMessage to the appropriate entry of the chat. This means that if the sender is thisPlayer the
     * message is added to the chat with the receivers; while if thisPlayer is one of the receivers the message
     * is added to the chat with sender, or the chat between all the players if the receivers are all the players
     * who are not the sender.
     * This method assumes that the parameters respect the required format, relying on the checks performed on
     * the server (InvalidReceiversException). It does not check in a complete manner that the parameters are correct.
     * @param receivers of the message
     * @param chatMessage that needs to be added to the chat
     */
    public void addMessage(List<PlayerLobby> receivers, ChatMessage chatMessage){
        if(chatMessage.getSender().equals(thisPlayer))
            chat.get(receivers).add(chatMessage);
        else if (receivers.contains(thisPlayer)) {
            if(receivers.size()==1)
                chat.get(List.of(chatMessage.getSender())).add(chatMessage);
            else
                chat.get(otherPlayers).add(chatMessage);
        }
    }

    /**
     * @param receivers Receivers indicating the specific chatroom whose messages are to be retrieved
     * @return The list of messages of the chatroom with the specified receivers, or null if no chat with those receivers is present
     */
    public List<ChatMessage> getChatWith(List<PlayerLobby> receivers){
        return chat.get(receivers);
    }
}
