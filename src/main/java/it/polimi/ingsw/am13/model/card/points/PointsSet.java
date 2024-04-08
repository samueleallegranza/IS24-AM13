package it.polimi.ingsw.am13.model.card.points;

import it.polimi.ingsw.am13.model.card.Resource;
import it.polimi.ingsw.am13.model.player.FieldIF;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Representation of points of an objective card of type "x points for each set of resources/objects"
 * (first 3 types of objective cards presentd in the rule book).
 * An object instantiated from this class is immutable.
 */
public class PointsSet implements PointsObjective {

    /**
     * Represets the set of resources/objects that must be present to obtain the points
     */
    private final Map<Resource, Integer> set;
    /**
     * Represetns how many points the card gives for each complete set present in field
     */
    private final int points;

    /**
     * The only constructor of the class. The only way to set the parameters of the class is to instantiate the object via this constructor
     * @param set The set of resources/objects that must be present to obtain the points
     * @param points How many points the card gives for each complete set present in field
     */
    public PointsSet(Map<Resource, Integer> set, int points) {
        this.set = set;
        this.points = points;
    }

    /**
     * Calculate points of the card, according to how many sets of resources/objects, which are indicated by the card itself, are visible.
     * @param field Field of the player for whom  you want to calculate the points given by the card objective.
     * @return Number of points guaranteed by the card objective
     */
    @Override
    public int calcPoints(FieldIF field) {
        Map<Resource, Integer> freqs = new HashMap<>(field.getResourcesInField());
        int count = 0;
        boolean ok = true;
        do {
            for(Resource r : set.keySet()) {
                int actualFreq = freqs.get(r);
                if(actualFreq < set.get(r)) {
                    ok = false;
                    break;
                } else
                    freqs.put(r, actualFreq - set.get(r));
            }
            if(ok)
                count++;
        } while(ok);

        return count*points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointsSet pointsSet = (PointsSet) o;
        return points == pointsSet.points && Objects.equals(set, pointsSet.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set, points);
    }
}
