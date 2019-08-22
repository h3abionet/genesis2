package org.h3abionet.genesis.model;

import java.util.Map;
import java.util.Optional;

/**
 * @author A Salmi
 */
public class PCAUtils {

    private PCAUtils() {
    }


    /**
     * Finds PCASubject corresponding to 2D coordinates
     *
     * @param xCoord        x coordinate
     * @param yCoord        y coordinate
     * @param xAxis         x axis index
     * @param yAxis         y axis index
     * @param pcaSubjectMap map containing PCA subjects
     * @return PCASubject instance
     */
    public static PCASubject coordinatesToPACSubject2D(double xCoord, double yCoord, int xAxis, int yAxis, final Map<String, PCASubject> pcaSubjectMap) {
        Optional<Map.Entry<String, PCASubject>> result = pcaSubjectMap.entrySet()
                .parallelStream()
                .filter(e -> e.getValue().getValues()[xAxis] == xCoord && e.getValue().getValues()[yAxis] == yCoord)
                .findAny();

        return result.map(Map.Entry::getValue).orElse(null);

    }
}
