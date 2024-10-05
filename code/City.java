import java.util.ArrayList;

/**
 * A class representing city
 */
public class City {
    public String cityName;
    /** City's x coordinate */
    public int x;
    /** City's y coordinate */
    public int y;
    /** Array to store cities, to which this City object is connected */
    public double[] neighbours;
    /** Index of the city in the array in which it was stored, in the main method */
    public int id;
    City(){
    }
    City(String cityName, int x , int y){
        this.cityName = cityName;
        this.x = x;
        this.y = y;
    }
    /** Sets each city a unique id, which is also their index in arrays of main method */
    void setId(int id){
            this.id = id;
    }
    /** Assigns connections of the city and distance to a connected city */
    void setNeighbour(int id, double distance){
        neighbours[id] = distance;
    }
    /** Initializes empty array of connections with fixed size, the initial distances to connection is set to zero */
    void setNeighbourSize(int size){
        neighbours = new double[size];
        for(int i=0; i<size; i++){
            neighbours[i] = 0;
        }
    }
}
