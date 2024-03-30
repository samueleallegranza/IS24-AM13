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
                Color.NO_COLOR
        );
        CardStarter starter_card = new CardStarter("starter001", starter, starter);

        // Initialize starter card for the player
        player.initStarter(starter_card);

        assertThrows(VariableAlreadySetException.class, () -> {
            player.initStarter(starter_card);
        });
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

        // Initialize objective card for the player
        player.initObjective(objectiveCard);

        // Attempt to initialize objective card again, should throw VariableAlreadySetException
        assertThrows(VariableAlreadySetException.class, () -> {
            player.initObjective(objectiveCard);
        });
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
                Color.ANIMAL
        );

        CardSidePlayable backSide = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(2),
                Color.FUNGUS
        );

        CardStarter starterCard = new CardStarter("starter001", frontSide, backSide);

        // Initialize starter card for the player
        player.initStarter(starterCard);

        // Play the starter card front side
        player.playStarter(Side.SIDEFRONT);

        // Attempt to play the starter card again, should throw InvalidPlayCardException
        assertThrows(InvalidPlayCardException.class, () -> {
            player.playStarter(Side.SIDEBACK);
        });
    }

    @Test
    public void testPlayCard() throws InvalidCardCreationException, VariableAlreadySetException, PlayerHandException, InvalidPlayCardException, RequirementsNotMetException, PlayerHandException, InvalidCoordinatesException {
        // Create a Player instance
        Player player = new Player("TestPlayer", new Token(ColorToken.RED));

        // Create a starter card
        CardSidePlayable frontSide = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(3),
                Color.ANIMAL
        );

        CardSidePlayable backSide = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(2),
                Color.FUNGUS
        );

        CardStarter starterCard = new CardStarter("starter001", frontSide, backSide);

        // Initialize starter card for the player
        player.initStarter(starterCard);

        // Play the starter card front side
        player.playStarter(Side.SIDEFRONT);

        // Create a card side for the player's hand
        CardSidePlayable sideToPlay = new CardSidePlayable(
                norequirements,
                Arrays.asList(new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.PLANT), new Corner(Resource.NO_RESOURCE)),
                Arrays.asList(Resource.PLANT, Resource.PLANT),
                new PointsInstant(3),
                Color.ANIMAL
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
        assertThrows(InvalidPlayCardException.class, () -> {
            player.playCard(sideToPlay, new Coordinates(1, 1));
        });
    }
}
