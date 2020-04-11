package Utils.math;


import core.distance.Entity;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Clustering {

    protected ArrayList<ArrayList<Double>> distanceList;
    protected double[][] distanceMatrix;

    public static Clustering getInstance(int type, double[][] distanceMatrix) {
        if (type == 0) {
            return new Hierarchical(distanceMatrix);
        }
        return null;
    }

    public abstract HashSet<Cluster> clustering(ArrayList<Entity> entities);

}
