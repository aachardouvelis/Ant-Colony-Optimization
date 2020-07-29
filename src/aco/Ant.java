/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;
import java.util.*;
/**
 *
 * @author Alex
 */

public class Ant implements Comparable<Ant> {
    private int id;
    private City city_start;
    private ArrayList<City> route;//at get(0) no ant move has been made. at get(1) it has.
    private double routeLength;
    
    public Ant(City cityStart,int id){
        this.id=id;
        routeLength=0;
        route=new ArrayList<City>();
        city_start=cityStart;
        route.add(city_start);
    }
    
    public Ant(Ant ant){
        this.id=ant.id;
        this.routeLength=ant.routeLength;
        this.route=ant.route;
        this.city_start=ant.getRoute().get(0);
    }
   
    public void setRouteLength(double length){
        routeLength=length;
    }
    
    
    public Ant(int id,ArrayList<City> route,City cityStart,double routeLength){
        this.route=route;
        this.id=id;
        this.city_start=cityStart;
        this.routeLength=routeLength;
    }
    
    public City getVisitedCity(int iterationsAgo){
        return route.get(route.size()-1-iterationsAgo);
    }
    
    public ArrayList<City> getRoute(){
        return route;
    }
    
    public int getId(){
        return id;
    }
    
    public City getCityStart(){
        return city_start;
    }
    
    public void setCityStart(City city){
       city_start=city;
    }
 
    public void visitCity(City city){
        if(hasVisited(city) && !city.equals(city_start))
            throw new IllegalArgumentException("Ant:"+this+" attempted to add already visited City:"+city+" and its not the start city");
            
        //System.out.println("I am Ant:"+getId()+" and I'm visiting City:"+city.getId());
        double distance=City.distance(city, getVisitedCity(0));
        route.add(city);
        updateRouteLength(distance); 
    }
    
    private void updateRouteLength(double distance){
        routeLength+=distance;
    }
   
    
    public boolean hasVisited(City city) {
        return route.contains(city);
    }
  
    
    public void reset(){
        route=new ArrayList<City>();
        routeLength=0.0;
        route.add(city_start);
        
    }
    
    
    
    public double getRouteLength() {    
        //return routeLength + City.distance(route.get(0),route.get(route.size()-1));
        return routeLength;
    }
    
    public String getPrintedRoute(){
        String line=""+route.get(0).getId();
        for(int i=1;i<route.size();i++)
            line+="-->"+route.get(i).getId();
        line+="\n and the route length is = "+getRouteLength();
        
        System.out.println(line);
        return line;
    }
    

    public String toString(){
        return getId()+"";
    }

 
    /*public int compareTo(Ant o) {
        if(routeLength>o.getRouteLength())
            return 1;
        else if(routeLength==o.getRouteLength())
            return 0;
        else
            return -1;
    }
    */
    public int compareTo(Ant o) {
        if(routeLength>o.getRouteLength())
            return 1;
        else if(routeLength==o.getRouteLength()){
            return ((Integer)id).compareTo(o.getId());
        }
        else
            return -1;
    }
    
    public boolean equals(Ant o){
        if(id==o.getId())
            return true;
        else
            return false;
        
    }
    
    
    
     
     
    

    
       
}
