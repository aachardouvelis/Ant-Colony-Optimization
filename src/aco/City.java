/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
/**
 *
 * @author Alex
 */
public class City{
    
    private final short x,y,id;
    
    public City(int id){
        Random random=new Random();
        this.id=(short)id;
        x=(short)random.nextInt(300);
        y=(short)random.nextInt(300);
    }
    
    public City(int x,int y,int id){
        this.x=(short)(x);
        this.y=(short)(y);
        this.id=(short)id;
    }
    
    public City(short x,short y,short id){
        this.x=x;
        this.y=y;
        this.id=id;
    }
    
    public short getX(){
        return x;
    }
    
    public short getY(){
        return y;
    }
    
    public int getId(){
        return this.id;
    }
    
    public boolean equals(City city){
        return this.id==city.getId() && this.x==city.x && this.y==city.y;
    }
    
    public static double distance(City a,City b){
        return Math.sqrt(Math.pow(a.getX()-b.getX(),2)+ Math.pow(a.getY()- b.getY(),2));
    }
    
    public static double calculatePathDistance(ArrayList<City> path){
        double distance=0.0;
        for(int i=1;i<path.size();i++){
            distance+=City.distance(path.get(i-1),path.get(i));
        }
        return distance;
    }
    
    public static void printPath(ArrayList<City> path){
        String out="--";
        for(City iter:path){
            out+=iter+" -->";
        }
        System.out.println(out);
    }
    
    public String toString(){
        return getId()+"";
    }
  
    
}
