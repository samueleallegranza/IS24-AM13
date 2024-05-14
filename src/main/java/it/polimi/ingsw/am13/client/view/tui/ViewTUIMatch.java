package it.polimi.ingsw.am13.client.view.tui;

import it.polimi.ingsw.am13.client.gamestate.GameState;
import it.polimi.ingsw.am13.client.view.tui.menu.MenuItem;
import it.polimi.ingsw.am13.model.card.CardSidePlayableIF;
import it.polimi.ingsw.am13.model.card.Coordinates;
import it.polimi.ingsw.am13.model.card.Resource;
import it.polimi.ingsw.am13.model.card.Side;
import it.polimi.ingsw.am13.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.am13.model.player.PlayerLobby;

import java.util.*;
import java.util.stream.Stream;

public class ViewTUIMatch implements ViewTuiMenuDisplayer {

    private GameState gameState;
    private PlayerLobby thisPlayer;

    public ViewTUIMatch() {
        this.gameState = null;
    }

    @Override
    public Map<String, MenuItem> getMenu() {
        return null;
    }

    public void printMatch() {
        // print player header
        System.out.println(sectionPlayers());
    }

    // --------------------------------------------------------------------
    // ----------------------------- SECTIONS -----------------------------
    // --------------------------------------------------------------------

    public String sectionPlayers() {
        return "";
    }


    // --------------------------------------------------------------------
    // ------------------------------ UTILS -------------------------------
    // --------------------------------------------------------------------
    private List<List<Character>> cardToStr(CardSidePlayableIF cardSidePlayableIF){
        List<List<Character>> strCard=new ArrayList<>(3);
        List<Resource> cornerResources=cardSidePlayableIF.getCornerResources();
        List<Resource> centerResources=cardSidePlayableIF.getCenterResources();
        strCard.get(0).add(1,'─');
        strCard.get(1).add(0,'│');
        strCard.get(1).add(1,' ');
        strCard.get(1).add(2,'│');
        strCard.get(2).add(1,'─');
        strCard.get(0).add(0,ViewTUIConstants.resourceToSymbol(cornerResources.get(0)).charAt(0));
        strCard.get(0).add(2,ViewTUIConstants.resourceToSymbol(cornerResources.get(1)).charAt(0));
        strCard.get(2).add(2,ViewTUIConstants.resourceToSymbol(cornerResources.get(2)).charAt(0));
        strCard.get(2).add(0,ViewTUIConstants.resourceToSymbol(cornerResources.get(3)).charAt(0));
        int startInd;
        if(centerResources.size()<3)
            startInd=1;
        else
            startInd=0;
        for (int i = 0; i < centerResources.size(); i++)
            strCard.get(1).set(startInd+i,ViewTUIConstants.resourceToSymbol(centerResources.get(i)).charAt(0));
        return strCard;
    }
    private List<List<Character>> availableStr(int index){
        List<List<Character>> strPos=new ArrayList<>(3);
        strPos.get(0).add(0,'┌');
        strPos.get(0).add(1,'─');
        strPos.get(0).add(2,'┐');
        strPos.get(1).add(0,(char)('0'+index/100));
        index-=index/100;
        strPos.get(1).add(1,(char)('0'+index/10));
        index-=index/10;
        strPos.get(1).add(2,(char)('0'+index));
        strPos.get(2).add(0,'└');
        strPos.get(2).add(1,'─');
        strPos.get(2).add(2,'┘');
        return strPos;
    }
    public String printField(PlayerLobby playerLobby){
        StringBuilder strField= new StringBuilder();
        try {
            CardSidePlayableIF starterCard=gameState.getPlayerState(playerLobby).getField().getCardSideAtCoord(new Coordinates(0,0));
            if(starterCard!=null) {
                Coordinates origin=new Coordinates(0, 0);
                int minX=0,maxX=0,minY=0,maxY=0;
                for(Coordinates coord : gameState.getPlayerState(playerLobby).getField().getPlacedCoords()){
                    if(coord.getPosX()<minX)
                        minX= coord.getPosX();
                    else if(coord.getPosX()>maxX)
                        maxX= coord.getPosX();
                    if(coord.getPosY()<minY)
                        minY= coord.getPosY();
                    else if(coord.getPosY()>maxY)
                        maxY= coord.getPosY();
                }
                int dimX=2*(maxX-minY+2),dimY=2*(maxY-minY+2);
                List<List<Character>> fieldMatrix=new ArrayList<>(dimY);
                List<Character> emptyLine= new ArrayList<>(dimX);
                for (int i = 0; i < dimX; i++) {
                    emptyLine.set(i,' ');
                }
                for (int i = 0; i < dimY; i++) {
                    fieldMatrix.set(i, emptyLine);
                }
                for(Coordinates coord : gameState.getPlayerState(playerLobby).getField().getPlacedCoords()){
                    CardSidePlayableIF cardSidePlayableIF=gameState.getPlayerState(playerLobby).getField().getCardSideAtCoord(coord);
                    List<List<Character>> strCard=cardToStr(cardSidePlayableIF);
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if(fieldMatrix.get(2*coord.getPosY()+j).get(2*coord.getPosX()+i)==' ')
                                fieldMatrix.get(2*coord.getPosY()+j).set(2*coord.getPosX()+i,strCard.get(j).get(i));
                        }
                    }
                    if(!cardSidePlayableIF.getCoveredCorners().get(0))
                        fieldMatrix.get(2*coord.getPosY()).set(2*coord.getPosX(),strCard.get(0).get(0));
                    if(!cardSidePlayableIF.getCoveredCorners().get(1))
                        fieldMatrix.get(2*coord.getPosY()).set(2*coord.getPosX()+2,strCard.get(0).get(2));
                    if(!cardSidePlayableIF.getCoveredCorners().get(2))
                        fieldMatrix.get(2*coord.getPosY()+2).set(2*coord.getPosX()+2,strCard.get(2).get(2));
                    if(!cardSidePlayableIF.getCoveredCorners().get(3))
                        fieldMatrix.get(2*coord.getPosY()+2).set(2*coord.getPosX(),strCard.get(2).get(0));
                }
                for(int i=0; i<gameState.getPlayerState(playerLobby).getField().getAvailableCoords().size();i++){
                    List<List<Character>> strCard=availableStr(i);
                    Coordinates coord=gameState.getPlayerState(playerLobby).getField().getAvailableCoords().get(i);
                    for (int j = 0; j < 3; j++) {
                        for (int k = 0; k < 3; k++) {
                            if(fieldMatrix.get(2*coord.getPosY()+k).get(2*coord.getPosX()+j)==' ')
                                fieldMatrix.get(3*coord.getPosY()+k).set(coord.getPosX()+j,strCard.get(k).get(j));
                        }
                    }
                }
                for (int i = 0; i < dimY; i++) {
                    for (int j = 0; j < dimX; j++) {
                        strField.append(fieldMatrix.get(i).get(j));
                    }
                    strField.append('\n');
                }
            }
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
        return strField.toString();
    }
}
