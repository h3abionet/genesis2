package genesisprototype.pca;

import genesisprototype.model.PCASubject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * project genesis2
 * Created by ayyoub on 2019-07-23.
 */
public class EigenDataReader {

    public EigenDataReader() {
    }

    public static List<PCASubject> createDataFromEigenInput(String inputFile) throws EigenDataException {
        if (!EigenDataReader.checkEigenFile(inputFile))
            return null;

        return extractDataFromFile(inputFile);
    }

    private static List<PCASubject> extractDataFromFile(String inputFile) {
        List<PCASubject> pcaList = new ArrayList<>(100);
        try {
            Files.lines(Paths.get(inputFile)).forEach(l -> {
                String[] split = l.trim().split("\\s+");
                //TODO check if lines have different numbers of components
                PCASubject object = new PCASubject(extractName(split), conv(split));
                pcaList.add(object);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pcaList;
    }

    private static String extractName(String[] input) {
        String[] nameArr = input[0].split(":");
        String name;
        if (nameArr.length == 1) {//uniquely identified individual
            name = nameArr[0] + " " + nameArr[0];
        } else {
            name = nameArr[0] + " " + nameArr[1];
        }
        return name;
    }

    private static double[] conv(String[] split) throws NumberFormatException {
        double[] result = new double[split.length - 2];
        for (int i = 2; i < split.length - 2; i++) {
            result[i - 2] = Double.parseDouble(split[i].trim());
        }
        return result;

    }

    private static boolean checkEigenFile(String inputFile) throws EigenDataException {
        // check if file exists
        if (!Files.exists(Paths.get(inputFile)))
            throw new EigenDataException("File not found!");

        try {
            Optional<String> firstLine = Files.lines(Paths.get(inputFile)).findFirst();
            // check if file is empty
            if (!firstLine.isPresent())
                throw new EigenDataException("File is empty!");

            if (firstLine.get().trim().split("\\s+|\\t+").length - 2 < 1)
                throw new EigenDataException();

        } catch (IOException e) {
            //TODO use proper logging
            e.printStackTrace();
        }

        return true;
    }
}
