package org.h3abionet.genesis.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author A Salmi
 */
public class AdmixDataReader {

    private AdmixDataReader() {
    }

    public static List<AdmixSubject> readAdmixInput(String file) throws AdmixDataException {
        checkAdmixInput(file);
        if (file.contains(".Q."))
            return readAdmix(file);
        return null;
    }

    private static List<AdmixSubject> readAdmix(String file) {
        List<AdmixSubject> data = null;
        try {
            data = Files.lines(Paths.get(file))
                    .parallel()
                    .map(l -> new AdmixSubject(l.split("\\s+")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            //TODO handle the exception if any
            e.printStackTrace();
        }
        return data;
    }

    private static void checkAdmixInput(String file) throws AdmixDataException {
        if (!Files.exists(Paths.get(file)))
            throw new AdmixDataException("Admixture file not found");

        try {
            Files.lines(Paths.get(file))
                    .parallel()
                    .flatMap(l -> Stream.of(l.split("\\s+")))
                    .forEach(number -> {
                        try {
                            Double.valueOf(number);
                        } catch (NumberFormatException e) {
                            throw new AdmixDataException("Parsing error");
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
