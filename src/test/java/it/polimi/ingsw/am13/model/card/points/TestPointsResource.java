package it.polimi.ingsw.am13.model.card.points;
import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.exceptions.*;
import it.polimi.ingsw.am13.model.player.Field;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class TestPointsResource {
    /**
     * This test verifies that the constructor correctly instantiates PointsIstant, and that calcPoints returns the correct value
     * In particular, it adds two identical cards with 2 of the required resource each, and verifies that calcPoints returns 4*points.
     */
    @Test
    public void testSetGet() throws InvalidCardCreationException {
        int points=2;
        Map<Resource,Integer> req=new HashMap<>();
        List<Corner> corners=new ArrayList<>(Arrays.asList(new Corner(Resource.PLANT),new Corner(Resource.NO_RESOURCE),new Corner(),new Corner()));
        List<Resource> centerResources=new ArrayList<>(List.of(Resource.PLANT));
        PointsResource pointsResource=new PointsResource(points,Resource.PLANT);
        CardSidePlayable cardSide=new CardSidePlayable(req,corners,centerResources,pointsResource, Color.PLANT);
        CardStarter cardStarter=new CardStarter("s001",cardSide,cardSide);
        Field field=new Field();
        Coordinates origin;
        try{
            origin=new Coordinates(0,0);
            field.playCardSide(cardSide,origin);
            Coordinates coordinates=new Coordinates(1,1);
            field.playCardSide(cardSide,coordinates);
            assertEquals(4*points,pointsResource.calcPoints(cardSide,field));
        } catch (RequirementsNotMetException | InvalidPlayCardException | InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }
}
