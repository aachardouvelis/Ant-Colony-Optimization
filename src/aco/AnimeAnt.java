    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Alex
 */
public class AnimeAnt implements Comparable<AnimeAnt> {
    private Ant ant;
    private Point start,cur,dest;
    private int subCycle=0;
    final long playTime;
    private long startTime,diffTime;
    private double step=0;
    private int cityPointer;
    private boolean spawned;
    private boolean lastTransit;
    private final double XYScaleFactor,XPlusFactor,YPlusFactor;
    
    public AnimeAnt(AnimeAnt animeAnt){
        this(animeAnt.ant, animeAnt.XYScaleFactor,animeAnt.XPlusFactor,animeAnt.YPlusFactor,animeAnt.playTime);
    }
    
    public AnimeAnt(Ant ant,double XYScaleFactor,double XPlusFactor,double YPlusFactor,long playTime){
        this(ant.getId(),ant.getRoute(),ant.getCityStart(),ant.getRouteLength(),XYScaleFactor,XPlusFactor,YPlusFactor,playTime);
    }
    public AnimeAnt(City cityStart,int id,double XYScaleFactor,double XPlusFactor,double YPlusFactor,long playTime){
        //super(cityStart,id);
        ant=new Ant(cityStart,id);
        this.playTime=playTime;
        this.XYScaleFactor=XYScaleFactor;
        this.XPlusFactor=XPlusFactor;
        this.YPlusFactor=YPlusFactor;
        this.cur=new Point((int)(cityStart.getX()*XYScaleFactor+XPlusFactor),(int)(cityStart.getY()*XYScaleFactor+YPlusFactor));
        this.start=new Point(this.cur);
        this.dest=new Point();
    }
    
    public AnimeAnt(int id,ArrayList<City> route,City cityStart,double routeLength,double XYScaleFactor,double XPlusFactor,double YPlusFactor,long playTime){
        //super(id,route,cityStart,routeLength);
        ant=new Ant(id,route,cityStart,routeLength);
        //System.out.println("City Start:"+cityStart);
        this.playTime=playTime;
        this.XYScaleFactor=XYScaleFactor;
        this.XPlusFactor=XPlusFactor;
        this.YPlusFactor=YPlusFactor;
        this.cur=new Point((int) (cityStart.getX()*XYScaleFactor+XPlusFactor),(int) (cityStart.getY()*XYScaleFactor+YPlusFactor));
        this.start=new Point(this.cur);
        this.dest=new Point();
    }
    
    public void spawn(long startTime){
        spawned=true;
        moveNextCity(startTime);
    }
    
 
    public void moveNextCity(long startTime){
        this.startTime=startTime;
        cityPointer++;
        // if new subcycle, restart cityPointer
        
        
        City from=ant.getRoute().get(cityPointer-1);
        City to=ant.getRoute().get(cityPointer);
        start.setLocation(from.getX()*XYScaleFactor+XPlusFactor,from.getY()*XYScaleFactor+YPlusFactor);
        cur.setLocation(start);
        dest.setLocation(to.getX()*XYScaleFactor+XPlusFactor,to.getY()*XYScaleFactor+YPlusFactor);
        
        if(cityPointer+1>=ant.getRoute().size()){
            
            endCycle();    
        }
    }
    
    private void endCycle(){
        cityPointer=0;
        lastTransit=true;
    }
    
    public boolean reachedPercentage(double percentage){
        if(percentage>1.0)
            throw new IllegalArgumentException("percentage argument was larger than 1.0");
        return diffTime>=percentage*playTime;
    }
    
    public boolean isLastTransit(){
        return lastTransit;
    }
    
    public boolean reachedDest(long now){
        diffTime=now-startTime;
        if(diffTime>=playTime){
            diffTime=playTime;
            return true;
        }
        else
            return false;   
    }
    
    public boolean spawned(){
        return spawned;
    }
    
    public void spendTime(long now){
        diffTime=now-startTime;
        if(diffTime>=playTime)
            diffTime=playTime;
    }
 
    public void makeXYStep(){
        
        step=(double)diffTime/ (double) playTime;
        
        cur.move((int)(start.getX()+((dest.getX() - start.getX()) * step)),(int)(start.getY()+((dest.getY() - start.getY()) * step)));
    }
    
    public Point getCurrentPos(){ return cur;}
    public Point getDestinationPos(){ return dest;}
    public Point getStartPos(){return start;}
    public Ant getAnt(){return ant;}
    
    public ArrayList<City> getRoute(){
        return ant.getRoute();
    }
    
    public int getId(){
        return ant.getId();
    }
    
    public City getCityStart(){
        return ant.getCityStart();
    }
    public boolean hasVisited(City city) {
        return ant.hasVisited(city);
    }
  
    
    public int compareTo(AnimeAnt animeAnt) {
        return ant.compareTo(animeAnt.getAnt());
    }
}
