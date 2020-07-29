/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.io.IOException;
import java.util.ArrayList;
import static java.util.Arrays.binarySearch;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class Ranked extends Elitist {
    
    private TreeSet<Ant> rankedAntList;   
    private final int w;
    

    public Ranked(double c,double alpha,double beta,double Q,double evap,double antFactor,short numOfCities,int e,int w) {
        super(c,alpha,beta,Q,evap,antFactor,numOfCities,e);
        if(w>=e) throw new NumberFormatException("number of rankedAnts 'w'(="+w+") should not\nbe greater or equal than the number of elitist ants 'e'=("+e+")");

        this.w=w;
        rankedAntList= new TreeSet();
    }
    
 
    public Ranked(double c,double alpha,double beta,double Q,double evap,double antFactor,ArrayList<City> cities,int e,int w) {
        super(c,alpha,beta,Q,evap,antFactor,cities,e);
        if(w>=e) throw new NumberFormatException("number of rankedAnts 'w'(="+w+") should not\nbe greater or equal than the number of elitist ants 'e'=("+e+")");

        this.w=w;
        rankedAntList= new TreeSet();
    }
    
    

    
    public void cycle(){
        resetAnts();
        
        for(int i=1;i<cities.size();i++){  
            moveAnts();    
        }
        
        moveAntsToStartCity();  
        
        addAntRanking();
        updateBestAnt();
        updateTrails();
        cycleCounter++;
        
    }
    

    public void updateBestAnt(){
        Ant bestOfCycle=rankedAntList.first();
        if (bestOfCycle.getRouteLength()<bestAnt.getRouteLength())
            bestAnt=new Ant(bestOfCycle);
        
    }
    
    
    
    public void addAntRanking(){
        for(Ant antIter:getAnts())
            
            rankedAntList.add(antIter);        
    }   

    
    public void applyRankedTrails(){
        //1st-best ant (σ-μ) where σ: numofElitistAnts, μ-st best ant
        City from,to;
        Iterator<Ant> iter=rankedAntList.iterator();
        for(int i=1;i<w+1;i++){
            Ant rankedAnt=iter.next();
            for(int j=0;j<rankedAnt.getRoute().size()-1;j++){
                from=rankedAnt.getRoute().get(j);
                to=rankedAnt.getRoute().get(j+1);
                applyRankedTrail(rankedAnt,from,to,e-i);
            }
        }
    }
        
    public void applyRankedTrail(Ant ant,City from,City to,int weight){
        if(weight<0)
            throw new IllegalArgumentException("ranked weight is <0!! :"+weight);
        double trail=getTrail(from,to);
        trail+=(weight*Q)/ant.getRouteLength();
        setTrail(from,to,trail);      
    }
    
   
    public void updateTrails(){
        evaporateTrails();
        applyBestAntTrails();
        applyRankedTrails();
    }
    
    
    public void resetAnts(){
        super.resetAnts();
        rankedAntList= new TreeSet();
    }
    
 
    
    public Ant[] getRankedAnts(){
        Iterator<Ant> antIter=rankedAntList.iterator();
        Ant[]rankedAnts=new Ant[w];
        for(int i=0;i<w;i++){
            rankedAnts[i]=antIter.next();
        }
        return rankedAnts;
    }
    

    
    public int getRankedAntSize(){
        return w;
    }
    
    public TreeSet<Ant>getOrderedAnts(){
        return rankedAntList;
    }
    
    public String toString(){
        return "Ranked System:"+"c="+c+"alpha="+alpha+"beta="+beta+"Q="+Q+"number of cities="+cities.size()+"ants="+ants.size()+"Elitist Ants:"+e+" Ranked Ants:"+w;
    }
       
    
    

   
  
}
