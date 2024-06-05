package it.polimi.ingsw.am13.client.chat;

/**
 * This exception is thrown when a client requests to send a message whose receivers are not valid, that is when
 * they are more than 1 and less than all the other players, or they contain the sender, or they contain a player who
 * is not in the match
 */
public class InvalidReceiversException extends Exception{
    public InvalidReceiversException(String message) {
        super(message);
    }
}
