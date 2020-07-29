/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 *
 * @author Alex
 */
public class AcoStatWrapper {
    private AntSystem system;
    private ArrayList<Double> cycleMinRoutes;
    private ArrayList<Double> avgNodeBranches;
    
    private double lamda;
    
    
    public AcoStatWrapper(AntSystem system,double lamda){
        if(lamda<0 || lamda>1)
            throw new NumberFormatException("Lamda has to be 0>lamda<1");
        this.system=system;
        this.system.setDifferentStartingCity();
        this.lamda=lamda;
        cycleMinRoutes=new ArrayList();
        avgNodeBranches=new ArrayList();
    }
    
    public void cycleAndCreateData(){
        system.cycle();
        cycleMinRoutes.add(system.getMinRouteLength());
        avgNodeBranches.add(createAvgNodeBranch(lamda)); 
    }
    
    public ArrayList<Double> getAvgNodeBranches(){
        return avgNodeBranches;
    }
    
    public ArrayList<Double> getCycleMinRoutes(){
        return cycleMinRoutes;
    }
    
    
    public static void main(String[]Args){
        Elitist as=new Elitist(10.0,1.0,5.0,100.0,0.5,1.0,AntSystem.loadOliver30Cities(),15);
        as.setDifferentStartingCity();
        AcoStatWrapper statwrapper=new AcoStatWrapper(as,0.1);
        for(int i=0;i<2500;i++){
         statwrapper.cycleAndCreateData();
         ArrayList<Double>nb=statwrapper.getAvgNodeBranches();
         System.out.println("Cycle:"+i+" NB:"+nb.get(i)+" MinRoute:"+statwrapper.getSystem().getMinRouteLength());
        }
        
        ArrayList<Double>nb=statwrapper.getAvgNodeBranches();
        for(int i=0;i<nb.size();i++){
            System.out.println(nb.get(i));
        }
    }
    
    
    private double createAvgNodeBranch(double lamda){

       ArrayList<City> cities=system.getCities();
       City current=cities.get(0);
       double average=0;
       int sum=0;
       
       for(int i=0;i<cities.size();i++){
           current=cities.get(i);
           int branchFact=createBranchingFactor(current,lamda);
           //System.out.println(branchFact);
           sum+=branchFact;
       }
       average=(double)sum/(double)(cities.size()-1);
       
       return average;
    }
    
    private int createBranchingFactor(City city,double lamda){
        double trailFloor=createBranchTrailFloor(city,lamda);
        int branches=0;
        ArrayList<City> cities=system.getCities();
        for(int i=0;i<cities.size();i++){
            if(!city.equals(cities.get(i)))
                if(system.getTrail(city,cities.get(i))>trailFloor)
                    branches++;
        }
        return branches;
    }
    
    private double createBranchTrailFloor(City current,double lamda){
        
        ArrayList<City> cities=system.getCities();
        double tMin=0,tMax=0;
        for(int i=0;i<cities.size();i++){
            if(!cities.get(i).equals(current)){
                double trail=system.getTrail(cities.get(i), current);
                if(tMin>trail)
                    tMin=trail;
                if(tMax<trail)
                    tMax=trail;
            }
       }
        double floor=tMin+(tMax-tMin)*lamda;

        return floor;
    }
    
    private double createBranchTrailFloorB(City current){
        
        ArrayList<City> cities=system.getCities();
        double tMin=0,tMax=0;
        for(int i=0;i<cities.size();i++){
            if(!cities.get(i).equals(current)){
                double trail=system.getTrail(cities.get(i), current);
                if(tMin>trail)
                    tMin=trail;
                if(tMax<trail)
                    tMax=trail;
            }
       }
        
        double floor=tMax/100;

        return floor;
    }
    
    
    public AntSystem getSystem(){
        return system;
    }
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
}
    
    
    
}
