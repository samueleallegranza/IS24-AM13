package it.polimi.ingsw.am13.network.socket.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//FIXME: magari questa classe potrebbe essere un'interfaccia
//FIXME: oppure una classe astratta che include l'attributo command
public class Message {
    public String toJson() {
        // Convert the object to JSON using Jackson
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }
}
