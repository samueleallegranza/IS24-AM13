package it.polimi.ingsw.am13.model.card;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am13.model.card.points.PointsCorner;
import it.polimi.ingsw.am13.model.card.points.PointsInstant;
import it.polimi.ingsw.am13.model.card.points.PointsPlayable;
import it.polimi.ingsw.am13.model.card.points.PointsResource;
import it.polimi.ingsw.am13.model.exceptions.InvalidCardCreationException;

import java.io.File;
import java.io.IOException;
import java.util.*;

// initDecks di match chiamerà 4 volte questi metodi in modo da creare i 4 deck

// nomenclatura angoli delle carte:
// e - empty: visibility = true, noresource
// n - nothing: visibility = false, noresource
// p - plant
// a - animal
// i - insect
// f - fungus
// q - quill
// m - manuscript
// w - inkwell

// nomenclatura pointsType delle cardGold
// c - PointsCorner
// n - PointsInstant
// q/m/w - Points

// nomenclatura pointsType di cardsObjective
// p - pattern
// s - set: se il set ha elementi di tipo diverso allora req è un tipo

public class CardFactory {
    private final static String CARD_JSON_PATH = "src/main/resources/json/card_data.json";

    public LinkedList<CardResource> createCardsResource() throws InvalidCardCreationException {
        LinkedList<CardResource> deck = new LinkedList<>();
        try {
            JsonNode cardsFront = getFrontsNode("cardsResource");
            JsonNode cardsBack = getBacksNode("cardsResource");
            JsonNode cardFront;
            JsonNode cardBack;

            for (int i = 0; i < 40; i++) {
                cardFront = cardsFront.get(i);
                cardBack = cardsBack.get(i);

                String id = cardFront.get("code").asText().replace("f","");
                Color color = colorConverter(cardBack.get("center").asText());
                // --- SideFront construction
                // resourceCards have no requirements so the map is empty
                Map<Resource, Integer> reqs = new HashMap<>();
                List<Corner> cornersFront = getCorners(cardFront);
                List<Resource> centerFront = new ArrayList<>();
                PointsPlayable pointsFront = new PointsInstant(cardFront.get("points").asInt());
                CardSidePlayable sideFront = new CardSidePlayable(reqs, cornersFront, centerFront, pointsFront, color);

                // -- SideBack construction
                // all backside corners are empty (visibility true)
                List<Corner> cornersBack = emptyCorners();
                List<Resource> centerBack = new ArrayList<>();
                centerBack.add(resourceConverter(cardBack.get("center").asText().charAt(0)));
                // cardBacks never reward points
                PointsPlayable pointsBack = new PointsInstant(0);
                CardSidePlayable sideBack = new CardSidePlayable(reqs, cornersBack, centerBack, pointsBack, color);
                deck.add(new CardResource(id, sideFront, sideBack));
            }

        } catch (IOException e) {
            throw new InvalidCardCreationException(e.getMessage());
        }
        return deck;
    }

    public LinkedList<CardGold> createCardsGold() throws InvalidCardCreationException{
        LinkedList<CardGold> deck = new LinkedList<>();
        try {
            JsonNode cardsFront = getFrontsNode("cardsGold");
            JsonNode cardsBack = getBacksNode("cardsGold");
            JsonNode cardFront;
            JsonNode cardBack;

            for (int i = 0; i < 40; i++) {
                cardFront = cardsFront.get(i);
                cardBack = cardsBack.get(i);

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
                PointsPlayable pointsFront;
                switch (cardFront.get("preq").asText().charAt(0)){
                    case 'c' : pointsFront = new PointsCorner(cardFront.get("points").asInt()); break;
                    case 'n' : pointsFront = new PointsInstant(cardFront.get("points").asInt()); break;
                    case 'w' :
                    case 'q' :
                    case 'm' : pointsFront = new PointsResource(cardFront.get("points").asInt(), resourceConverter(cardFront.get("preq").asText().charAt(0))); break;
                    default: pointsFront = new PointsInstant(0); break;
                }
                CardSidePlayable sideFront = new CardSidePlayable(reqs, cornersFront, centerFront, pointsFront, color);

                // -- SideBack construction
                // all backside corners are empty (visibility true)
                List<Corner> cornersBack = emptyCorners();
                List<Resource> centerBack = new ArrayList<>();
                centerBack.add(resourceConverter(cardBack.get("center").asText().charAt(0)));
                // cardBacks never reward points
                PointsPlayable pointsBack = new PointsInstant(0);
                CardSidePlayable sideBack = new CardSidePlayable(reqs, cornersBack, centerBack, pointsBack, color);
                deck.add(new CardGold(id, sideFront, sideBack));
            }

        } catch (IOException e) {
            throw new InvalidCardCreationException(e.getMessage());
        }
        return deck;
    }

    public LinkedList<CardStarter> createCardsStarter() {
        return null;
    }

    public LinkedList<CardObjective> createCardsObjective() {
        return null;
    }

    private JsonNode getFrontsNode(String cardType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonRootNode = mapper.readTree(new File(CARD_JSON_PATH));
            JsonNode cardTypeNode = jsonRootNode.get("cards").get(cardType);
            return cardTypeNode.get("cardsFront");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private JsonNode getBacksNode(String cardType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonRootNode = mapper.readTree(new File(CARD_JSON_PATH));
            JsonNode cardTypeNode = jsonRootNode.get("cards").get(cardType);
            return cardTypeNode.get("cardsBack");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    private Corner cornerConverter(char c){
        if(c == 'n')
            return new Corner();
        return new Corner(resourceConverter(c));
    }

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

    private Resource resourceConverter(char c){
        return switch (c) {
            case 'n' -> Resource.NO_RESOURCE;
            case 'e' -> Resource.NO_RESOURCE;
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

    private List<Corner> emptyCorners(){
        List<Corner> emptyCorners = new ArrayList<>();
        for (int j = 0; j < 4; j++) {
            emptyCorners.add(cornerConverter('e'));
        }
        return emptyCorners;
    }

}
