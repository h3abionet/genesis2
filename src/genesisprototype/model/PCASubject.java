package genesisprototype.model;

/**
 * project genesis2
 * Created by ayyoub on 2019-07-23.
 */
public class PCASubject {
    private double[] values; //contains the data.
    private String name;

    public PCASubject(String name, double[] values) {
        this.values = values;
        this.name = name;
    }

    // setters and getters

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
