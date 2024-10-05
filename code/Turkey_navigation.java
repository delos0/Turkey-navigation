import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

/**
 * Program to calculate and draw the shortest path between two cities.
 * Coordinates of cities and their connections to other cities are taken from a file.
 * The starting city and destination city is inputted by the user.
 *
 * @author Yeldos Urashev, Student ID: 2022400372
 * @since 03.04.2024
 * Student ID: 2022400372
 */

public class YeldosUrashev {

    /**
     * Finds the closest unvisited city
     *
     * @param distances array of distances from a source city
     * @param visited array that captures if the city was already visited or not
     * @return index of the closest unvisited city
     */
    public static int minDistance(double[] distances, boolean[] visited) {
        double min = Double.MAX_VALUE;
        int index = -1;
        for(int i=0; i<distances.length; i++) {
            if(!visited[i] && distances[i] <= min) {
                min = distances[i];
                index = i;
            }
        }
        return index;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Locale.setDefault(new Locale("America", "US"));
        ArrayList<City> cities = new ArrayList<>();
        int cityNumber = 0, connectionNumber = 0;
        ArrayList<ArrayList<String>> cityConnections = new ArrayList<>();
        int width = 1901, height = 843; // 0.9 * resolution of file
        int xScale = 2377, yScale = 1055;
        double connectionRadius = 0.003;
        double cityRadius = 4;
        double textCoor = 12; // height between y coordinate of text and city
        double[] distances;
        int[] parents;
        boolean[] visited;
        int source = 0;
        int target = 0;

        /*
        Getting a coordinates file
        Checking if it was found
         */
        File coordinates = new File("city_coordinates.txt");
        if(!coordinates.exists()) {
            System.out.printf("%s cannot be found.", coordinates);
            System.exit(1);
        }
        Scanner scanCoordinates = new Scanner(coordinates);

        /*
        Scanning the coordinates file
        Inserting the coordinates of city to 'cities' ArrayList
        Saving the index of each city in its City class
        Counting number of cities with 'cityNumber' int
         */
        while(scanCoordinates.hasNextLine()) {
            String[] line = scanCoordinates.nextLine().split(", ");
            String cityName = line[0];
            int x = Integer.parseInt(line[1]);
            int y = Integer.parseInt(line[2]);
            City tempCity = new City(cityName, x, y );
            tempCity.setId(cityNumber);
            cities.add(tempCity);
            cityNumber = cityNumber + 1;
        }
        scanCoordinates.close();

        /*
        Getting a connections file
        Checking if it was found
         */
        File connections = new File("city_connections.txt");
        if(!connections.exists()) {
            System.out.printf("%s cannot be found.", connections);
            System.exit(1);
        }
        Scanner scanConnections = new Scanner(connections);

        /*
        Scanning the connections file
        Saving the connections to 'cityConnections' 2D ArrayList of Strings
        Counting number of connections with 'connectionNumber' int
         */
        while(scanConnections.hasNextLine()) {
            String[] line = scanConnections.nextLine().split(",");
            ArrayList<String> tempConnection = new ArrayList<>();
            tempConnection.add(line[0]);
            tempConnection.add(line[1]);
            cityConnections.add(tempConnection);
            connectionNumber = connectionNumber + 1;
        }
        scanConnections.close();

        /*
        Getting input from user
        Handling improper input
        Looping until valid input is obtained
         */
        Scanner scanner = new Scanner(System.in);
        String destination;
        String start;
        boolean exists = false;
        do {
            System.out.print("Enter starting city: ");
            start = scanner.next();

            for (City city : cities) {
                if (Objects.equals(city.cityName, start)) {
                    exists = true;
                    target = city.id;
                }
            }
            if(!exists) System.out.printf("City named '%s' not found. Please enter a valid city name.\n", start);
        } while (!exists);

        exists = false;
        do {
            System.out.print("Enter destination city: ");
            destination = scanner.next();

            for (City city : cities) {
                if (Objects.equals(city.cityName, destination)) {
                    exists = true;
                    source = city.id;
                }

            }
            if(!exists) System.out.printf("City named '%s' not found. Please enter a valid city name.\n", destination);
        } while (!exists);

        /*
        Initializing empty array of neighbours with fixed size
        for each City in cities
         */
        for(int i=0; i<cityNumber; i++) {
            cities.get(i).setNeighbourSize(cityNumber);
        }

        /*
        Calculating distances between connected cities
        Inserting id and distance to connected city
        Which will be used later in algorithm
         */
        for(int i=0; i<connectionNumber; i++) {
            String city1 = cityConnections.get(i).get(0);
            String city2 = cityConnections.get(i).get(1);
            int id1 = 0, id2 = 0;
            double distance = 0;
            for(City city : cities){
                if(Objects.equals(city.cityName, city1)) {
                    id1 = city.id;
                }
                if(Objects.equals(city.cityName, city2)) {
                    id2 = city.id;
                }
            }
            distance = (cities.get(id1).x - cities.get(id2).x) * (cities.get(id1).x - cities.get(id2).x);
            distance += (cities.get(id1).y - cities.get(id2).y) * (cities.get(id1).y - cities.get(id2).y);
            distance = Math.pow(distance, 0.5);
            cities.get(id1).setNeighbour(id2, distance);
            cities.get(id2).setNeighbour(id1, distance);
        }

        /*
        Implementing the algorithm
        Detailed description and pseudocode is in report
         */
        distances = new double[cityNumber];
        parents = new int[cityNumber];
        visited = new boolean[cityNumber];

        for(int i=0; i<cityNumber; i++) {
            distances[i] = Double.MAX_VALUE;
            visited[i] = false;
        }

        distances[source] = 0;
        for(int i = 0; i < cityNumber - 1; i++) {
            int closest = minDistance(distances, visited);
            visited[closest] = true;

            for(int j = 0; j < cityNumber; j++) {
                if (!visited[j] && cities.get(closest).neighbours[j] != 0
                    && distances[closest] != Double.MAX_VALUE
                    && distances[closest] + cities.get(closest).neighbours[j] < distances[j]) {
                    distances[j] = distances[closest] + cities.get(closest).neighbours[j];
                    parents[j] = closest;
                }
            }
        }

        /*
        Drawing the map
         */
        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, xScale);
        StdDraw.setYscale(0, yScale);
        StdDraw.enableDoubleBuffering();
        StdDraw.picture(xScale/2.0, yScale/2.0, "map.png", xScale, yScale);

        /*
        Drawing the cities
         */
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.setPenRadius(connectionRadius);
        for(int i=0; i<cityNumber; i++) {
            int x = cities.get(i).x;
            int y = cities.get(i).y;
            String cityName = cities.get(i).cityName;
            StdDraw.filledCircle(x, y, cityRadius);
            StdDraw.text(x, y + textCoor, cityName);
        }

        /*
        Drawing the connections
         */
        for(int i=0; i<connectionNumber; i++) {
            int cityX1 = 0, cityY1 = 0;
            int cityX2 = 0, cityY2 = 0;
            for(int j=0; j<cityNumber; j++) {
                if(Objects.equals(cities.get(j).cityName, cityConnections.get(i).get(0))){
                    cityX1 = cities.get(j).x;
                    cityY1 = cities.get(j).y;
                }
                if(Objects.equals(cities.get(j).cityName, cityConnections.get(i).get(1))){
                    cityX2 = cities.get(j).x;
                    cityY2 = cities.get(j).y;
                }
            }
            StdDraw.line(cityX1, cityY1, cityX2, cityY2);
        }

        StdDraw.setPenRadius(connectionRadius*4);
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        /*
        Special output case if the destination is the same as starting city
         */
        boolean same = false;
        if (Objects.equals(destination, start)) {
            int x = cities.get(target).x;
            int y = cities.get(target).y;
            StdDraw.text(x, y + textCoor, cities.get(target).cityName);
            StdDraw.filledCircle(x, y, cityRadius);
            same = true;
        }
        /* If no there is no path from start to destination, program exits */
        if (parents[target] == 0 && !same) {
            System.out.print("No path could be found.");
            System.exit(0);
        }

        /*
        Outputting general result
         */
        int temp2 = target;
        System.out.printf("Total Distance: %.2f. ", distances[target]);
        System.out.print("Path: ");

        /*
        Drawing the path from starting city to destination city
         */
        while (target!=source) {
            System.out.print(cities.get(target).cityName + " -> ");
            temp2 = target;
            target = parents[target];
            int x1 = cities.get(target).x, y1 = cities.get(target).y;
            int x2 = cities.get(temp2).x, y2 = cities.get(temp2).y;

            StdDraw.line(x1, y1, x2, y2);
            StdDraw.filledCircle(x1, y1, cityRadius);
            StdDraw.filledCircle(x2, y2, cityRadius);
            StdDraw.text(x1, y1 + textCoor, cities.get(target).cityName);
            StdDraw.text(x2, y2 + textCoor, cities.get(temp2).cityName);
        }
        System.out.print(cities.get(target).cityName);

        StdDraw.show();
    }
}