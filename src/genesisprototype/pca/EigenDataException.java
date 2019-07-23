package genesisprototype.pca;

/**
 * project genesis2
 * Created by ayyoub on 2019-07-23.
 */
public class EigenDataException extends Exception {

    public EigenDataException() {
        super("Eigen file invalid format");
    }

    public EigenDataException(String message) {
        super(message);
    }
}
