package genesisprototype.pca;

import genesisprototype.model.PCASubject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class contains the methods needed to convert eigenstrat/Plink input into
 * the PCASubject array
 *
 * @author A Salmi
 */
public class EigenDataReader {

    private EigenDataReader() {
    }

    /**
     * Create a PCASubject list from file
     *
     * @param inputFile the eigenstrat/Plink input file
     * @return PCASubject list
     * @throws EigenDataException indicates a buggy file
     */
    public static List<PCASubject> createDataFromEigenInput(String inputFile) throws EigenDataException {
        if (!EigenDataReader.checkEigenFile(inputFile))
            return null;

        if (inputFile.endsWith(".eigenvec"))
            return extractDataFromPlinkFile(inputFile);
        else if (inputFile.endsWith(".evec"))
            return extractDataFromEigenstratFile(inputFile);

        return null;
    }


    /**
     * Parse PCASubject objects from Eigenstrat file
     *
     * @param inputFile the eigenstrat input file
     * @return PCASubject list
     */
    private static List<PCASubject> extractDataFromEigenstratFile(String inputFile) {
        List<PCASubject> pcaList = new ArrayList<>(100);
        try {
            Files.lines(Paths.get(inputFile))
                    .map(l -> l.trim().replaceAll(" +", " "))
                    .forEach(l -> {
                        if (l.startsWith("#"))
                            return;
                        String[] split = l.split("\\s+");
                        //TODO check if lines have different numbers of components
                        PCASubject object = new PCASubject(extractName(split), conv(split, 1));
                        pcaList.add(object);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pcaList;
    }

    /**
     * Parse PCASubject objects from Plink file
     *
     * @param inputFile the eigenstrat input file
     * @return PCASubject list
     */
    private static List<PCASubject> extractDataFromPlinkFile(String inputFile) {
        List<PCASubject> pcaList = new ArrayList<>(100);
        try {
            Files.lines(Paths.get(inputFile)).forEach(l -> {
                String[] split = l.trim().split("\\s+");
                //TODO check if lines have different numbers of components
                PCASubject object = new PCASubject(extractName(split), conv(split, 2));
                pcaList.add(object);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pcaList;
    }

    /**
     * Extract the name from a line of the plin/eigenstrat input.
     * plink line format: "firstname optionalsecondname col1 col2 ... "
     * eigen line format: "firstname:optionalsecondname col1 col2 ... "
     * @param input the (split) input line
     * @return the name (first and second name seperated by a space if there are 2 names)
     */
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

    /**
     * Converts 1 line of eigenstrat data to an array of doubles that represent
     * the values for each PCA axis
     *
     * @param line   the (split) input line
     * @param offset 1 for eigenstrat and 2 for plink
     * @return the array of doubles
     * @throws NumberFormatException
     */
    private static double[] conv(String[] line, int offset) throws NumberFormatException {
        double[] result = new double[line.length - 2];
        for (int i = offset; i < line.length - offset; i++) {
            result[i - offset] = Double.parseDouble(line[i].trim());
        }
        return result;
    }


    /**
     * Checks input file for errors
     * @param inputFile input file
     * @return true if file is OK, otherwise throws an exception
     * @throws EigenDataException
     */
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
