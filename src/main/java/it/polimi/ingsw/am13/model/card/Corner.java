package it.polimi.ingsw.am13.model.card;

public class Corner {
    private boolean visibility;
    private boolean covered;
    private Resource resource;
    private Card link;


    public boolean isPlaceable(){
        return true;
    }

    public void addLinkToCard(Card card){

    }
}
