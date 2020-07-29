/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
https://s3.amazonaws.com/academia.edu.documents/30667660/85128742-Ant-Colony-Optimization.pdf?AWSAccessKeyId=AKIAIWOWYYGZ2Y53UL3A&Expires=1543242906&Signature=NkmOHgov1DsSnoFE%2BxI8OynIBfg%3D&response-content-disposition=inline%3B%20filename%3DAnt_Colony_Optimization_and_Swarm_Intell.pdf
 */
package aco;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex5
 */
public class Elitist extends AntSystem {
    protected final int e;
    
    public Elitist(double c,double alpha,double beta,double Q,double evap,double antFactor,short numOfCities,int e) {
        super(c,alpha,beta,Q,evap,antFactor,numOfCities);
        if(e<0)
            throw new IllegalArgumentException("Number of elitist ants should be greater or equal to 0.");
        this.e=e;
        
    }
    public Elitist(double c,double alpha,double beta,double Q,double evap,double antFactor,ArrayList<City> cities, int e){
        super(c,alpha,beta,Q,evap,antFactor,cities);
        if(e<0)
            throw new IllegalArgumentException("Number of elitist ants should be greater or equal to 0.");
        this.e=e;
    }
    
    public void updateTrails(){
        super.updateTrails();
        applyBestAntTrails();
    }
    
    
    
    public void applyBestAntTrails(){
        ArrayList<City> bestRoute=bestAnt.getRoute();
        City from,to;
        for(int i=0;i<bestRoute.size()-1;i++){
            from=bestRoute.get(i);
            to=bestRoute.get(i+1);
            double trail=getTrail(from,to);
            trail+=(ants.size()*Q)/bestAnt.getRouteLength();
            setTrail(from,to,trail);
        }
    }
    
    
    
    public int getElitistSize(){
        return e;
    }
    public String toString(){
        return "Elitist System:"+"c="+c+"alpha="+alpha+"beta="+beta+"Q="+Q+"number of cities="+cities.size()+"ants="+ants.size()+"Elitist Ants:"+e;
    }
}
