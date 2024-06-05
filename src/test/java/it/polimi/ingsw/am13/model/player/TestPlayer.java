package it.polimi.ingsw.am13.model.player;

import it.polimi.ingsw.am13.model.card.*;
import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestPlayer {
    private final HashMap<Resource, Integer> norequirements = new HashMap<>();
    @Test
    public void testInitStarter() throws InvalidCardCreationException, VariableAlreadySetException {
        // Create a Player instance
        Player player = new Player("TestPlayer", new Token(ColorToken.RED));

        // Create a starter card
        CardSidePlayable starter = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(0),
                Color.NO_COLOR,
                "",
                Side.SIDEFRONT
        );
        CardStarter starter_card = new CardStarter("starter001", starter, starter);

        // Initialize starter card for the player
        player.initStarter(starter_card);

        assertThrows(VariableAlreadySetException.class, () -> player.initStarter(starter_card));
    }

    @Test
    public void testInitObjective() throws VariableAlreadySetException {
        // Create a Player instance
        Player player = new Player("TestPlayer", new Token(ColorToken.RED));

        // Create an objective card
        HashMap<Resource, Integer> objres = new HashMap<>();
        objres.put(Resource.PLANT, 1);
        objres.put(Resource.ANIMAL, 2);

        CardObjective objectiveCard = new CardObjective(
                "obj001",
                2,
                objres
        );
        //set possible personal objectives
        player.initPossiblePersonalObjectives(objectiveCard,objectiveCard);
        // Initialize objective card for the player
        try{
            player.initObjective(objectiveCard);
        } catch (InvalidChoiceException e){
            System.out.println("The passed objective card is not one of the two objective cards assigned to the given player");
        }

        // Attempt to initialize objective card again, should throw VariableAlreadySetException
        assertThrows(VariableAlreadySetException.class, () -> player.initObjective(objectiveCard));
    }

    @Test
    public void testPlayStarter() throws InvalidCardCreationException, VariableAlreadySetException, InvalidPlayCardException {
        // Create a Player instance
        Player player = new Player("TestPlayer", new Token(ColorToken.RED));

        // Create a starter card
        CardSidePlayable frontSide = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(3),
                Color.ANIMAL,
                "",
                Side.SIDEFRONT
        );

        CardSidePlayable backSide = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(2),
                Color.FUNGUS,
                "",
                Side.SIDEFRONT
        );

        CardStarter starterCard = new CardStarter("starter001", frontSide, backSide);

        // Initialize starter card for the player
        player.initStarter(starterCard);

        // Play the starter card front side
        try {
            player.playStarter(Side.SIDEFRONT);
        } catch (InvalidChoiceException e){
            System.out.println("The passed side does not belong to the starter card assigned to the given player");
        }
        // Attempt to play the starter card again, should throw InvalidPlayCardException
        assertThrows(InvalidPlayCardException.class, () -> player.playStarter(Side.SIDEBACK));
    }

    @Test
    public void testPlayCard() throws InvalidCardCreationException, VariableAlreadySetException, InvalidPlayCardException, RequirementsNotMetException, PlayerHandException, InvalidCoordinatesException {
        // Create a Player instance
        Player player = new Player("TestPlayer", new Token(ColorToken.RED));

        // Create a starter card
        CardSidePlayable frontSide = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(3),
                Color.ANIMAL,
                "",
                Side.SIDEFRONT
        );

        CardSidePlayable backSide = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(2),
                Color.FUNGUS,
                "",
                Side.SIDEFRONT
        );

        CardStarter starterCard = new CardStarter("starter001", frontSide, backSide);

        // Initialize starter card for the player
        player.initStarter(starterCard);

        // Play the starter card front side
        try {
            player.playStarter(Side.SIDEFRONT);
        }catch (InvalidChoiceException e){
            System.out.println("The passed side does not belong to the starter card assigned to the given player");
        }
        // Create a card side for the player's hand
        CardSidePlayable sideToPlay = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(3),
                Color.ANIMAL,
                "",
                Side.SIDEFRONT
        );

        // Create a card for the player's hand
        CardPlayable cardToPlay = new CardResource("card001", sideToPlay, sideToPlay);

        // Add the card to the player's hand
        player.addCardToHand(cardToPlay);

        // Play the card on the field
        player.playCard(sideToPlay, new Coordinates(1, 1));

        // Check if the points are correctly added
        assertEquals(3, player.getPoints());

        // Attempt to play the same card again, should throw InvalidPlayCardException
        assertThrows(InvalidPlayCardException.class, () -> player.playCard(sideToPlay, new Coordinates(1, 1)));
    }

    @Test
    public void testAdd4HandCards() throws InvalidCardCreationException {
        // Create a Player instance
        Player player = new Player("TestPlayer", new Token(ColorToken.RED));
        // Create a card side for the player's hand
        CardSidePlayable sideToPlay = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(3),
                Color.ANIMAL,
                "",
                Side.SIDEFRONT
        );
        //create a card resource
        CardResource cardResource=new CardResource("r000",sideToPlay,sideToPlay);
        //add the same card 3 times
        for (int i = 0; i < 3; i++) {
            try {
                player.addCardToHand(cardResource);
            } catch (PlayerHandException e) {
                throw new RuntimeException(e);
            }
        }
        //the fourth time it will cause an exception
        assertThrows(PlayerHandException.class, () -> player.addCardToHand(cardResource));
    }
}
