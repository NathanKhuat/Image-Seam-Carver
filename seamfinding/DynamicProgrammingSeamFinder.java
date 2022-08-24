package seamfinding;

import Picture;
import SeamCarver;
import energy.EnergyFunction;

import java.util.*;

/**
 * Dynamic programming implementation of the {@link SeamFinder} interface.
 *
 */
public class DynamicProgrammingSeamFinder implements SeamFinder {

    @Override
    public List<Integer> findHorizontal(Picture picture, EnergyFunction f) {
        double[][] plot = new double[picture.width()][picture.height()];
        List<Integer> result = new LinkedList<>();

        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                if (i == 0) {
                    plot[i][j] = f.apply(picture, i, j);
                } else {
                    double energy = Double.POSITIVE_INFINITY;
                    for (int z = j-1; z <= j + 1; z++) {
                        if (0 <= z && z < picture.height()) {
                            energy = Double.min(plot[i-1][z], energy);
                        }
                    }
                    plot[i][j] = energy + f.apply(picture, i, j);
                }
            }
        }

        int index = 0;
        for (int j = 1; j < picture.height(); j++) {
            if(plot[picture.width() - 1][j] < plot[picture.width() - 1][index]) {
                index = j;
            }
        }

        result.add(index);
        int minPredecessorIndex = index;
        for(int i = picture.width() - 2; i >= 0; i--) {
            for (int j = index - 1; j <= index + 1; j++) {
                if (0 <= j && j < picture.height() && plot[i][j] < plot[i][minPredecessorIndex]) {
                    minPredecessorIndex = j;
                }
            }
            result.add(minPredecessorIndex);
            index = minPredecessorIndex;
        }
        Collections.reverse(result);
        return result;
    }
}
