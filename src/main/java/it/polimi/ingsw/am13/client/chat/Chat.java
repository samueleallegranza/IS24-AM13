package it.polimi.ingsw.am13.client.chat;

import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.*;

public class Chat  {
    private final Map<List<PlayerLobby>,List<ChatMessage>> chat;
    private final PlayerLobby thisPlayer;
    private final List<PlayerLobby> otherPlayers;
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
