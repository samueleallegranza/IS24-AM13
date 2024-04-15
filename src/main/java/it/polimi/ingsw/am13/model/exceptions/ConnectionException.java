package it.polimi.ingsw.am13.model.exceptions;

public class ConnectionException extends Exception{
    public ConnectionException(){
        super();
    }

    public ConnectionException(String msg){
        super(msg);
    }
}
