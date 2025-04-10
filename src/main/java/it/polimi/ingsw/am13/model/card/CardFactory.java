package it.polimi.ingsw.am13.model.card;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am13.model.card.points.*;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class parses the information of card_json and creates {@link Card}
 * objects in accordance to the Factory pattern
 */

public class CardFactory {
    /**
     * Path to <code>card_data.json</code> file
     */
    private final static String CARD_JSON_PATH = "/json/card_data.json";

    /**
     * Deserializes a list of Resource cards from the <a href = "src/main/resources/json/card_data.json">card_data.json</a>
     * @return <code>LinkedList&lt;&gt;</code> of CardResource
     * @throws InvalidCardCreationException if it fails to create a valid card
     */
    public LinkedList<CardResource> createCardsResource() throws InvalidCardCreationException {
        LinkedList<CardResource> deck = new LinkedList<>();
        try {
            JsonNode cardsFront = getFrontsNode("cardsResource");
            JsonNode cardsBack = getBacksNode("cardsResource");
            JsonNode cardFront;
            JsonNode cardBack;

            for (int i = 0; i < 40; i++) {
                cardFront = cardsFront.get(i);
                cardBack = cardsBack.get((i/10)%10);

                String id = cardFront.get("code").asText().replace("f","");
                Color color = colorConverter(cardBack.get("center").asText());
                // --- SideFront construction
                // resourceCards have no requirements so the map is empty
                Map<Resource, Integer> reqs = new HashMap<>();
                List<Corner> cornersFront = getCorners(cardFront);
                List<Resource> centerFront = new ArrayList<>();
                PointsPlayable pointsFront = new PointsInstant(cardFront.get("points").asInt());
                CardSidePlayable sideFront = new CardSidePlayable(reqs, cornersFront, centerFront, pointsFront, color,id,Side.SIDEFRONT);

                // -- SideBack construction
                // all backside corners are empty (visibility true)
                List<Corner> cornersBack = emptyCorners();
                List<Resource> centerBack = new ArrayList<>();
                centerBack.add(resourceConverter(cardBack.get("center").asText().charAt(0)));
                // cardBacks never reward points
                PointsPlayable pointsBack = new PointsInstant(0);
                CardSidePlayable sideBack = new CardSidePlayable(reqs, cornersBack, centerBack, pointsBack, color,id,Side.SIDEBACK);
                deck.add(new CardResource(id, sideFront, sideBack));
            }

        } catch (IOException e) {
            throw new InvalidCardCreationException(e.getMessage());
        }
        return deck;
    }

    /**
     * Deserializes a list of Gold cards from the <a href = "src/main/resources/json/card_data.json">card_data.json</a>
     * @return <code>LinkedList&lt;&gt;</code> of CardGold
     * @throws InvalidCardCreationException if it fails to create a valid card
     */
    public LinkedList<CardGold> createCardsGold() throws InvalidCardCreationException{
        LinkedList<CardGold> deck = new LinkedList<>();
        try {
            JsonNode cardsFront = getFrontsNode("cardsGold");
            JsonNode cardsBack = getBacksNode("cardsGold");
            JsonNode cardFront;
            JsonNode cardBack;

            for (int i = 0; i < 40; i++) {
                cardFront = cardsFront.get(i);
                cardBack = cardsBack.get((i/10)%10);

                String id = cardFront.get("code").asText().replace("f","");
                Color color = colorConverter(cardBack.get("center").asText());
                // --- SideFront construction
                Map<Resource, Integer> reqs = new HashMap<>();
                JsonNode cardReqs = cardFront.get("reqs");
                reqs.put(resourceConverter(cardReqs.get(0).get("type").asText().charAt(0)), cardReqs.get(0).get("num").asInt());
                if(cardReqs.get(1) != null)
                    reqs.put(resourceConverter(cardReqs.get(1).get("type").asText().charAt(0)), cardReqs.get(1).get("num").asInt());
                List<Corner> cornersFront = getCorners(cardFront);

                List<Resource> centerFront = new ArrayList<>();
                PointsPlayable pointsFront = switch (cardFront.get("preq").asText().charAt(0)) {
                    case 'c' -> new PointsCorner(cardFront.get("points").asInt());
                    case 'n' -> new PointsInstant(cardFront.get("points").asInt());
                    case 'w', 'q', 'm' ->
                            new PointsResource(cardFront.get("points").asInt(), resourceConverter(cardFront.get("preq").asText().charAt(0)));
                    default -> new PointsInstant(0);
                };
                CardSidePlayable sideFront = new CardSidePlayable(reqs, cornersFront, centerFront, pointsFront, color,id,Side.SIDEFRONT);

                // -- SideBack construction
                // all backside corners are empty (visibility true)
                List<Corner> cornersBack = emptyCorners();
                List<Resource> centerBack = new ArrayList<>();
                centerBack.add(resourceConverter(cardBack.get("center").asText().charAt(0)));
                // cardBacks never reward points
                PointsPlayable pointsBack = new PointsInstant(0);
                CardSidePlayable sideBack = new CardSidePlayable(new HashMap<>(), cornersBack, centerBack, pointsBack, color,id,Side.SIDEBACK);
                deck.add(new CardGold(id, sideFront, sideBack));
            }

        } catch (IOException e) {
            throw new InvalidCardCreationException(e.getMessage());
        }
        return deck;
    }

    /**
     * Deserializes a list of Starter cards from the <a href = "src/main/resources/json/card_data.json">card_data.json</a>
     * @return <code>LinkedList&lt;&gt;</code> of CardStarter
     * @throws InvalidCardCreationException if it fails to create a valid card
     */
    public LinkedList<CardStarter> createCardsStarter() throws InvalidCardCreationException{
        LinkedList<CardStarter> deck = new LinkedList<>();
        try {
            JsonNode cardsFront = getFrontsNode("cardsStarter");
            JsonNode cardsBack = getBacksNode("cardsStarter");
            JsonNode cardFront;
            JsonNode cardBack;

            for (int i = 0; i < 6; i++) {
                cardFront = cardsFront.get(i);
                cardBack = cardsBack.get(i);

                String id = cardFront.get("code").asText().replace("f","");
                Color color = Color.NO_COLOR;
                // --- SideFront construction
                // starterCards have no requirements so the map is empty
                Map<Resource, Integer> reqs = new HashMap<>();
                List<Corner> cornersFront = getCorners(cardFront);
                List<Resource> centerFront = new ArrayList<>();
                int j=0;
                while (cardFront.get("center").get(j) != null && j<3){
                    centerFront.add(resourceConverter(cardFront.get("center").get(j).asText().charAt(0)));
                    j++;
                }
                // cardStarters never reward points
                PointsPlayable pointsFront = new PointsInstant(0);
                CardSidePlayable sideFront = new CardSidePlayable(reqs, cornersFront, centerFront, pointsFront, color,id,Side.SIDEFRONT);

                // -- SideBack construction
                List<Corner> cornersBack = getCorners(cardBack);
                List<Resource> centerBack = new ArrayList<>();
                // cardBacks never reward points
                CardSidePlayable sideBack = new CardSidePlayable(reqs, cornersBack, centerBack, pointsFront, color,id,Side.SIDEBACK);
                deck.add(new CardStarter(id, sideFront, sideBack));
            }

        } catch (IOException e) {
            throw new InvalidCardCreationException(e.getMessage());
        }
        return deck;
    }

    /**
     * Deserializes a list of Objective cards from the <a href = "src/main/resources/json/card_data.json">card_data.json</a>
     * @return <code>LinkedList&lt;&gt;</code> of CardObjective
     * @throws InvalidCardCreationException if it fails to create a valid card
     */
    public LinkedList<CardObjective> createCardsObjective()  throws InvalidCardCreationException{
        LinkedList<CardObjective> deck = new LinkedList<>();
        try {
            JsonNode cardsFront = getFrontsNode("cardsObjective");
            JsonNode cardFront;

            for (int i = 0; i < 16; i++) {
                cardFront = cardsFront.get(i);

                String id = cardFront.get("code").asText().replace("f","");
                // --- SideFront construction
                int points = cardFront.get("points").asInt();
                // pointsType p = pattern
                if(cardFront.get("pointsType").asText().charAt(0) == 'p'){
                    Color col1 = colorConverter(cardFront.get("color1").asText());
                    Color col2 = colorConverter(cardFront.get("color2").asText());
                    Color col3 = colorConverter(cardFront.get("color3").asText());
                    int pos12 = cardFront.get("pos1").asInt();
                    int pos23 = cardFront.get("pos2").asInt();
                    deck.add(new CardObjective(id, points, col1, col2, col3, pos12, pos23));
                } else if (cardFront.get("pointsType").asText().charAt(0) == 's') {
                    Map<Resource, Integer> set = new HashMap<>();
                    int j = 0;
                    while(cardFront.get("set").get(j) != null && j<3){
                        set.put(resourceConverter(cardFront.get("set").get(j).get("type").asText().charAt(0)), cardFront.get("set").get(j).get("num").asInt());
                        j++;
                    }
                    deck.add(new CardObjective(id, points, set));
                }
            }

        } catch (IOException | InvalidCardCreationException e) {
            throw new InvalidCardCreationException(e.getMessage());
        }
        return deck;
    }

    // Utils methods
    /**
     *
     * @param cardType String used to reference this card type in the <code>card_data.json</code>
     * @return JsonNode of the list of "front sides" of the given {@code cardType}
     * @throws IOException If it fails to retrieve the correct JsonNode from the JSON file
     * @see JsonNode
     */
    private JsonNode getFrontsNode(String cardType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonRootNode = mapper.readTree(getClass().getResourceAsStream(CARD_JSON_PATH));
        JsonNode cardTypeNode = jsonRootNode.get("cards").get(cardType);
        return cardTypeNode.get("cardsFront");
    }

    /**
     *
     * @param cardType String used to reference this card type in the <code>card_data.json</code>
     * @return JsonNode of the list of "front sides" of the given {@code cardType}
     * @throws IOException If it fails to retrieve the correct JsonNode from the JSON file
     * @see JsonNode
     */
    private JsonNode getBacksNode(String cardType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonRootNode = mapper.readTree((getClass().getResourceAsStream(CARD_JSON_PATH)));
            JsonNode cardTypeNode = jsonRootNode.get("cards").get(cardType);
            return cardTypeNode.get("cardsBack");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param cardSide {@code JsonNode} of the card side to which the corners are a part of.
     * @return <code>List&lt;&gt;</code> of {@link Corner} objects
     */
    private List<Corner> getCorners(JsonNode cardSide){
        List<Corner> corners = new ArrayList<>();
        char[] crnrs = new char[4];
        crnrs[0] = cardSide.get("ul").asText().charAt(0);
        crnrs[1] = cardSide.get("ur").asText().charAt(0);
        crnrs[2] = cardSide.get("lr").asText().charAt(0);
        crnrs[3] = cardSide.get("ll").asText().charAt(0);
        for (int j = 0; j < 4; j++) {
            if (crnrs[j] == 'n')
                corners.add(new Corner());
            else
                corners.add(new Corner(resourceConverter(crnrs[j])));
        }
        return corners;
    }

    /**
     *
     * @param s String whose first letter represents a color for a card
     * @return corresponding {@link Color}
     */
    private Color colorConverter(String s){
        char c = s.charAt(0);
        return switch (c) {
            case 'p' -> Color.PLANT;
            case 'a' -> Color.ANIMAL;
            case 'f' -> Color.FUNGUS;
            case 'i' -> Color.INSECT;
            default -> Color.NO_COLOR;
        };
    }

    /**
     *
     * @param c Character indicating a resource
     * @return corresponding {@link Resource}
     */
    private Resource resourceConverter(char c){
        return switch (c) {
            case 'n', 'e' -> Resource.NO_RESOURCE;
            case 'p' -> Resource.PLANT;
            case 'a' -> Resource.ANIMAL;
            case 'f' -> Resource.FUNGUS;
            case 'i' -> Resource.INSECT;
            case 'q' -> Resource.QUILL;
            case 'm' -> Resource.MANUSCRIPT;
            case 'w' -> Resource.INKWELL;
            default -> null;
        };
    }

    /**
     *
     * @return <code>List</code> of empty corners
     */
    private List<Corner> emptyCorners(){
        List<Corner> emptyCorners = new ArrayList<>();
        for (int j = 0; j < 4; j++) {
            emptyCorners.add(new Corner(Resource.NO_RESOURCE));
        }
        return emptyCorners;
    }

}
