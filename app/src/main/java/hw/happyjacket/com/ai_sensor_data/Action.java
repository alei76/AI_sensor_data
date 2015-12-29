package hw.happyjacket.com.ai_sensor_data;

/**
 * Created by jacket on 2015/12/29.
 */
public class Action {
    private String label;
    private int id;

    public Action(String label, int id) {
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }
}
