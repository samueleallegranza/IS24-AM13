package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.VariableAlreadySetException;
import it.polimi.ingsw.am13.model.player.Field;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPointstInstant {
    /**
     * This test verifies that the constructor correctly instantiates PointsIstant, and that calcPoints returns the correct value
     */
    @Test
    public void testSetGet(){
        int points=5;
        Map<Resource,Integer> req=new HashMap<>();
        List<Corner> corners=new ArrayList<>();
        List<Resource> centerResources=new ArrayList<>();
        PointsInstant cardPoints=new PointsInstant(points);
        CardSidePlayable cardSide=new CardSidePlayable(req,corners,centerResources,cardPoints, Color.NO_COLOR);
        CardStarter cardStarter=new CardStarter("s001",cardSide,cardSide);
        Field field=new Field();
        try {
            field.initStartCard(cardStarter);
        } catch (VariableAlreadySetException e) {
            throw new RuntimeException(e);
        }
        assertEquals(Integer.valueOf(points),cardPoints.calcPoints(cardSide,field));
    }
}
