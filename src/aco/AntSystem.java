                                                    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Alex
 */
public class AntSystem {
    protected double c ;
    protected double alpha;
    protected double beta;
    protected double evap;
    protected double Q ;
    private double antFactor;
    protected Random random;
    
    protected ArrayList<Ant> ants;
    protected ArrayList<City> cities;
    protected ArrayList<ArrayList<Double>> trails;
    protected Ant bestAnt;
    protected int cycleCounter=0;
    protected boolean commonCityStart=true;
    protected boolean citiesUpdated=false;
    
    
  
    
    public AntSystem(double c,double alpha,double beta,double Q,double evap,double antFactor,int numOfCities) {
        this(c,alpha,beta,Q,evap,antFactor,generateCities(numOfCities));
    }
    
    public AntSystem(double c,double alpha,double beta,double Q,double evap,double antFactor, ArrayList<City> cities) {
        this.c=c;
        this.alpha=alpha;
        this.beta=beta;
        this.Q=Q;
        this.evap=evap;
        this.antFactor=antFactor;
        random=new Random();
        ants = new ArrayList<Ant>();
        this.cities=cities;
        generateEdges((short)cities.size());
        
        short numOfAnts =(short) (cities.size() * antFactor);
 
        City cityStart=cities.get(0);

        generateAnts(numOfAnts,cityStart);

        bestAnt=ants.get(random.nextInt(ants.size()-1));
        
        
        this.evap=evap;
    }
    
    
    /************************************
    ******** VARIOUS METHODS *******
    ************************************/
    
    public void cycle(){
        resetAnts();
        for(int i=1;i<cities.size();i++){
            moveAnts();    
        }
        moveAntsToStartCity();
        updateBestAnt();
        updateTrails();
        cycleCounter++;  
    }
    
    
    protected City getRandomCity(){
        return cities.get(random.nextInt(cities.size()));
    }
    
    protected double getDoubleRandom(double end){ 
        double roll=random.nextDouble()*end;
        if(roll==0.0)
            return Double.MIN_VALUE;
        else
            return  roll;
     
    }
    
    protected boolean hasCommonCityStart(){
        return commonCityStart;
    }
    
    
    
    public int getCycleCounter(){
        return cycleCounter;
    }
    
    protected void setCommonStartingCity(){
        if(!hasCommonCityStart()){
            City cityStart=cities.get(0);
            for(Ant ant:ants){
                ant.setCityStart(cityStart);
                ant.reset();
            }
        }
    }
    
    protected void setDifferentStartingCity(){
        if(commonCityStart){
            if(ants.size()<cities.size()){
                System.out.println("Ants less than cities");
               for(int i=0;i<ants.size();i++){
                ants.get(i).setCityStart(cities.get(i));
                System.out.println("Ant:"+ants.get(i).getId()+"Placed in city"+cities.get(i).getId());
               }
            }
            else{
                int antCityDiff=ants.size()-cities.size();
                System.out.println("Ants are more by:"+antCityDiff);
                for(int i=0;i<cities.size();i++){
                    ants.get(i).setCityStart(cities.get(i));
                                    System.out.println("Ant:"+ants.get(i).getId()+"Placed in city"+cities.get(i).getId());

                }
                int k=cities.size()-1;
                for(int i=0;i<antCityDiff;i++){
                    ants.get(k).setCityStart(cities.get(i));
                    k++;
                    System.out.println("Ant:"+ants.get(k).getId()+"Placed in city"+cities.get(i).getId());

                }    
            }
          
        }
    }
    
    
    
    /************************************
    //****** ANT CODE ******
    ************************************/
    protected void moveAnts(){
        for(int i=0;i<ants.size();i++){

            
            ants.get(i).visitCity(selectNextCity(calculateProbabilityTable(ants.get(i))));                          
        }
    }
    
    protected void moveAntsToStartCity(){
        for(Ant antIter:ants)            
            antIter.visitCity(antIter.getCityStart());               
    }
    
    protected void updateBestAnt(){
        if(citiesUpdated){
            bestAnt=new Ant(ants.get(random.nextInt(ants.size()-1)));
            citiesUpdated=false;
        }
        for(Ant ant:ants){
            if(ant.getRouteLength()<bestAnt.getRouteLength()){
            
                bestAnt=new Ant(ant);
            }
        }
    }
    
    protected void resetAnts(){
        Ant antIter;
        for(int i=0;i<ants.size();i++){
            antIter=ants.get(i);
            antIter.reset();
        }
    }
      
    protected void generateAnts(short numOfAnts,City cityStart){
        //System.out.println("NUM OF ANTS ARE:"+numOfAnts+" <---generateAnts");
        for(int i=0;i<numOfAnts;i++){
            ants.add(new Ant(cityStart, i));
        }
    }
    
    protected void generateAnts(short numOfAnts){
        for(int i=0;i<numOfAnts;i++)
            ants.add(new Ant(getRandomCity(), i));
    }
    
 
    
    public ArrayList<Ant> getAnts(){
        return ants;
    }
    
           
    protected double[] calculateProbabilityTable(Ant ant){
       
        double[] probabilities=new double[cities.size()+1];

        City currentCity=ant.getVisitedCity(0);
        double heuristic=0.0;
        double trail,distance,divider=0.0;
        
        String testPrint="";
        for(int i=0;i<cities.size();i++){
            if(!ant.hasVisited(cities.get(i))){
                distance=City.distance(currentCity,cities.get(i));
                
                trail=getTrail(cities.get(i),currentCity);
                distance=Math.pow(distance,beta);
                trail=Math.pow(trail,alpha);
                heuristic=trail;
                heuristic=heuristic/distance;
                if(heuristic==0.0){
                    heuristic=Double.MIN_VALUE;
                }
                probabilities[i]=heuristic;
                
                 
                divider+=(probabilities[i]);
                testPrint+="\nprob["+i+"]="+probabilities[i]+"   trail(curentCity-->"+cities.get(i)+"):"+getTrail(cities.get(i),currentCity)+" Heuristic:"+heuristic;
                
            }
            else
                probabilities[i]=0.0;
        }
        probabilities[cities.size()]=divider;
        
        if(divider==0){
            this.printTrailMatrix();
            throw new IllegalArgumentException("sum of prob = "+divider+"\nProbability table is.."+testPrint+"\n Route Done:"+ant.getRoute()+"\n Route Size:"+ant.getRoute().size()+"\ntrail matrix...");
        }
        return probabilities;
    }
    
    public City selectNextCity(double[] probabilityTable){//ERRORS
        
        double heuristicSum=probabilityTable[probabilityTable.length-1];
        if(heuristicSum<=0)
            throw new IllegalArgumentException("recieved a probability table of 0 or less heuristic SSUM");
       
        double roll=getDoubleRandom(heuristicSum);
        double cumulativeProbability=0.0;
        for(int i=0;i<probabilityTable.length-1;i++){
            cumulativeProbability+=probabilityTable[i];        
            if(cumulativeProbability>=roll)
                return cities.get(i);    
        }
        throw new IllegalStateException();
    }
    
    /*public City selectNextCity(double[] probabilityTable){
        
        double heuristicSum=probabilityTable[probabilityTable.length-1];
        if(heuristicSum<=0)
            throw new IllegalArgumentException("recieved a probability table of 0 or less heuristic SSUM");
        double sum=0.0;
        for(int i=0;i<probabilityTable.length-1;i++){
            probabilityTable[i]/=probabilityTable[probabilityTable.length-1];
            sum+=probabilityTable[i];
        }
        //System.out.println("total prob="+sum+" at cycle:"+cycleCounter);
        /*String probt="";
        
        for(int i=0;i<probabilityTable.length-1;i++){
            probt+="probability["+i+"]="+probabilityTable[i];
        }
        System.out.println(probt);
        double roll=random.nextDouble();
        double cumulativeProb=0.0;
        for(int i=0;i<probabilityTable.length-1;i++){
            cumulativeProb+=probabilityTable[i];
            if(cumulativeProb>=roll){
               // System.out.println("Returning city["+i+"] and its probability was:"+probabilityTable[i]);
                return cities.get(i);
            }
        }
        throw new IllegalStateException("SelectNextCity Failiure.");
        //return cities.get(cities.size()-2);
    }
*/
    
    /************************************
    //****** GRAPH CODE ******
    ************************************/
    protected boolean containsEdge(City a,City b){
        return containsEdge(a.getId(),b.getId());
    }
    
    protected boolean containsEdge(int aCityId,int bCityId){
        return trails.get(aCityId).get(bCityId)>-1.0;
        //return trails[aCityId][bCityId]>-1.0;
    }
   
    public static ArrayList<City> generateCities(int numOfCities){
        ArrayList<City> cities=new ArrayList();
        for(int i=0;i<numOfCities;i++)
            cities.add(new City(i));
        return cities;
    }
    
    protected void updateCities(ArrayList<City> cities){
        int commonCounter=countEqualCities(cities);
        if(commonCounter<this.cities.size()){
            citiesUpdated=true;
            removeLastNCities(this.cities.size()-commonCounter);
            if(cities.size()>commonCounter){
                System.out.println("We in da case");
                int diff=cities.size()-this.cities.size();
                for(int i=diff;i>0;i--){
                    this.cities.add(cities.get(cities.size()-i));
                    addNewEdges(cities.get(cities.size()-i));
                }
            }
        } 
        if(commonCounter==this.cities.size()){
            
            if(this.cities.size()<cities.size()){
                citiesUpdated=true;
                int diff=cities.size()-this.cities.size();
                for(int i=diff;i>0;i--){
                    this.cities.add(cities.get(cities.size()-i));
                    addNewEdges(cities.get(cities.size()-i));
                }
            }
        }
        /*if(this.cities.size()<cities.size()){
            citiesUpdated=true;
            int diff=cities.size()-this.cities.size();
            for(int i=diff;i>0;i--){
                this.cities.add(cities.get(cities.size()-i));
                addNewEdges(cities.get(cities.size()-i));
            }
        }
        else{
            citiesUpdated=true;
            int diff=cities.size()-this.cities.size();
            for(int i=diff;i>0;i--){
                this.cities.remove(this.cities.size()-1);
                removeLastEdge();
            }
        }
        */
    }
    
    protected void removeLastNCities(int n){
        for(int i=0;i<n;i++){
            this.cities.remove(this.cities.size()-1);
            removeLastEdge();
        }
    }
   
    public City getCityStart(){
        if(hasCommonCityStart())
            return ants.get(0).getCityStart();
        else
            throw new UnsupportedOperationException();
    }
    
    public ArrayList<City> getCities(){
        return cities;
    }
    
    public int countEqualCities(ArrayList<City> cities){
        int counter=0;
        for(int i=0;i<cities.size() && i<this.cities.size();i++){
            if(cities.get(i).equals(this.cities.get(i)))
                counter++;
        }
        System.out.println(counter);
        return counter;
    }
    
    protected void addNewEdges(City city){
        trails.add(new ArrayList());
        for(int i=0;i<cities.size()-1;i++)
            if(i!=city.getId())
                trails.get(i).add(c);
        for(int i=0;i<cities.size();i++)
            trails.get(cities.size()-1).add(i, c);

    }
    
    protected void removeLastEdge(){
        trails.remove(trails.size()-1);
        for(int i=0;i<trails.size()-1;i++)
            trails.get(i).remove(trails.get(i).size()-1);    
    }
    
    public void printPheromoneTable(){
        String table="";
        for(int i=0;i<trails.size();i++){
            for(int j=0;j<trails.size();j++){
                table+="trails["+i+","+j+"]:"+trails.get(i).get(j)+" ";
            }
            table+="\n";
        }
        System.out.println(table);
    }
    
    protected void generateEdges(short numOfCities){
        trails=new ArrayList();
        for(int i=0;i<numOfCities;i++){
            trails.add(new ArrayList());
            for(int j=0;j<numOfCities;j++){
                trails.get(i).add(j,-1.0);
            }
        }
        

        for(int i=0;i<numOfCities;i++){
            
            for(int j=0;j<numOfCities;j++)

                if(i<j)
                    
                    setTrail(cities.get(i),cities.get(j),c);

            
        }
    }
    
    public boolean addEdge(City a,City b){ //[i][j] i<j this side of array is used.
        int aCityId=a.getId();
        int bCityId=b.getId();
        if(!containsEdge(aCityId,bCityId)){
                setTrail(a,b,c);
            return true;
        }
        else
            return false;
    }
    
    protected String getMapStruct(){
        String out="";
        for(int i=0;i<cities.size();i++){
            out+="cities["+i+"]=new City("+cities.get(i).getX()+","+cities.get(i).getY()+","+i+");";
            if(i<cities.size()-1){
                out+="\n";
            }
        }
        return out;
    }
    
    /*protected static ArrayList<ArrayList<City>> retrieveMaps() throws IOException{
        City iter=new City(-1,-1,-1);
        BufferedReader br = new BufferedReader(new InputStreamReader(iter.getClass().getResourceAsStream("/Maps/SavedMaps.txt")));
        String s=br.readLine();
        br.close();
        
        String[]FileNames=s.split(" ");
        //BufferedReader[] mapAr=new BufferedReader[FileNames.length];
        
        ArrayList<ArrayList<City>> mapList=new ArrayList();
        mapList.add(AntSystem.loadOliver30Cities());
        mapList.add(AntSystem.loadTestMapA());
        mapList.add(AntSystem.loadTestMapB());
        mapList.add(AntSystem.loadAlex40Cities());
        mapList.add(AntSystem.loadAlex50Cities());
        ArrayList<City>map=new ArrayList();
        
        for(String name:FileNames){
            br=new BufferedReader(new InputStreamReader(iter.getClass().getResourceAsStream("/Maps/"+name)));
            String d;
            while((d=br.readLine())!=null){
                //System.out.println(d);
                String[]split;
                split=d.split(" ");
                //System.out.println(split[0]);
                map.add(new City(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2])));
            }
            br.close();
            mapList.add(map);
        }
        return mapList;
    }*/
    
    /*protected static void createMap(ArrayList<City> cities,String filename)throws IOException{
        City iter=new City(-1,-1,-1);
        File file=new File("src/Maps/"+filename+".txt");
        new OutputStreamWriter(file);
        OutputStreamWriter stream=new OutputStreamWriter(iter.getClass().getResourceAsStream("/Maps/"+filename));
        
        BufferedWriter writer=new BufferedWriter()
        if(file.exists()) throw new FileAlreadyExistsException(filename);
        BufferedWriter writer=new BufferedWriter(new FileWriter(file));

        for(City city:cities){
            writer.write(city.getX()+" "+city.getY()+" "+city.getId());
            writer.newLine();
        }
        writer.close();
                           
    }*/
    
    public static void createMap(ArrayList<City> cities,String filename)throws IOException{
        if(filename.equals(""))
            throw new IOException("Enter a name for the file");
        createMapDir();
        
        File out=new File(getMapFolderPath()+"/"+filename+".txt");
        if(out.exists())
            throw new IOException("Name already exists, Please pick another one.");
        BufferedWriter writer=new BufferedWriter(new FileWriter(out));
        for(City city:cities){
            writer.write(city.getX()+" "+city.getY()+" "+city.getId());
            writer.newLine();
        }
        writer.close();
    }
    public static String getMapFolderPath(){
        City iter=new City(-1,-1,-1);
        File jar=new File(iter.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        File parent=jar.getParentFile();
        
        File theDir = new File(parent.getAbsolutePath()+"/Maps");
        return theDir.getAbsolutePath();
    }
    
    public static void createMapDir(){
        City iter=new City(-1,-1,-1);
        File jar=new File(iter.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        File parent=jar.getParentFile();
        
        File theDir = new File(parent.getAbsolutePath()+"/Maps");
        
        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + theDir.getName());

            try{
                theDir.mkdir();
            } 
            catch(SecurityException se){
                //handle it
            }        
            
        }
    }
    protected static ArrayList<String> retrieveMapNames() throws IOException{
        createMapDir();
        File mapfolder=new File(getMapFolderPath());
        System.out.println(getMapFolderPath());
        ArrayList<String> names=new ArrayList();
        names.add("Oliver30");
        names.add("A-10");
        names.add("B-20");
        names.add("Alex-40");
        names.add("Alex-50");
        File[] files=mapfolder.listFiles();
        
        for(int i=0;i<files.length;i++){
            System.out.println("what the hell");
            names.add(files[i].getName());
        }
        return names;
    }
   
   /* protected static ArrayList<String> retrieveMapNames() throws IOException{
        
        City iter=new City(-1,-1,-1);
        BufferedReader br = new BufferedReader(new InputStreamReader(iter.getClass().getResourceAsStream("/Maps/SavedMaps.txt")));
        String s=br.readLine();
        br.close();
        
        String[]FileNames=s.split(" ");
        
        
        ArrayList<String> names=new ArrayList();
        //String namelel=AntSystem.convertStreamToString(AntSystem.class.getClassLoader().getResourceAsStream("/src/Map")); //AntSystem.getClassLoader().getResourceAsStream("/myfile.txt");
        names.add("Oliver30");
        names.add("A-10");
        names.add("B-20");
        names.add("Alex-40");
        names.add("Alex-50");
        
        for(String name:FileNames)
            names.add(name);
        return names;
        
        File mapfolder=new File("/src/Maps");
        File[] files=mapfolder.listFiles();
        for(File file:files){
            names.add(file.getName());
        }
        return names;
        
        
        final String path = "Maps";
        final File jarFile = new File(new City(1,1,1).getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        //System.out.println("W H E R E");
        //System.out.println(jarFile.getAbsolutePath());
        
        
        
        File[] mapfiles=jarFile.listFiles();
            //JOptionPane.showMessageDialog(null, "WE IN HERE");
        for(int i=0;i<mapfiles.length;i++){
            //System.out.println(mapfiles[i].getName());
                        JOptionPane.showMessageDialog(null, "WE IN HERE");

            //JOptionPane.showMessageDialog(null, mapfiles[i].getName());
        }
        
        
        
       /* if(jarFile.isFile()) {  // Run with JAR file
            //System.out.println("in here");
                        JOptionPane.showMessageDialog(null, jarFile.getAbsolutePath());

            final JarFile jar = new JarFile(jarFile);
            //JOptionPane.showMessageDialog(null, "ASDAISJD");
            final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            while(entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                //System.out.println(name);
                if (name.startsWith(path + "/")) { //filter according to the path
                    System.out.println(name);
                    names.add(name);
                }
            }
        jar.close();
        
        }
                return names;
        

    }
    
    
    public void test(){
        
    }
    
    */

    protected static ArrayList<ArrayList<City>> retrieveMaps() throws FileNotFoundException, IOException {
        
        ArrayList<ArrayList<City>> maps=new ArrayList();
        
        maps.add(AntSystem.loadOliver30Cities());
        maps.add(AntSystem.loadTestMapA());
        maps.add(AntSystem.loadTestMapB());
        maps.add(AntSystem.loadAlex40Cities());
        maps.add(AntSystem.loadAlex50Cities());
        
        
        BufferedReader br;
        File[] mapfiles=new File(getMapFolderPath()).listFiles();
        for(int i=0;i<mapfiles.length;i++){
            br=new BufferedReader(new FileReader(mapfiles[i].getCanonicalPath()));
            String s = null;
            ArrayList<City> citieslist=new ArrayList();
            while ((s=br.readLine())!=null){
                String[]split;
                split=s.split(" ");
                citieslist.add(new City(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2])));
                
            }
            maps.add(citieslist);
            br.close();
        }
        return maps;
        
    }
    
    


    public static ArrayList<City> loadAlex50Cities(){
        ArrayList<City> cities=new ArrayList();
        cities.add(new City(9,46,0));
        cities.add(new City(215,225,1));
        cities.add(new City(272,202,2));
        cities.add(new City(95,33,3));
        cities.add(new City(151,203,4));
        cities.add(new City(106,121,5));
        cities.add(new City(205,46,6));
        cities.add(new City(219,80,7));
        cities.add(new City(278,190,8));
        cities.add(new City(191,292,9));
        cities.add(new City(37,233,10));
        cities.add(new City(217,136,11));
        cities.add(new City(3,294,12));
        cities.add(new City(254,185,13));
        cities.add(new City(120,179,14));
        cities.add(new City(35,245,15));
        cities.add(new City(164,228,16));
        cities.add(new City(77,238,17));
        cities.add(new City(288,289,18));
        cities.add(new City(79,212,19));
        cities.add(new City(117,68,20));
        cities.add(new City(156,110,21));
        cities.add(new City(48,75,22));
        cities.add(new City(284,191,23));
        cities.add(new City(54,53,24));
        cities.add(new City(187,50,25));
        cities.add(new City(299,20,26));
        cities.add(new City(277,110,27));
        cities.add(new City(200,134,28));
        cities.add(new City(72,120,29));
        cities.add(new City(179,214,30));
        cities.add(new City(10,60,31));
        cities.add(new City(36,4,32));
        cities.add(new City(289,144,33));
        cities.add(new City(256,41,34));
        cities.add(new City(27,23,35));
        cities.add(new City(254,77,36));
        cities.add(new City(62,162,37));
        cities.add(new City(272,13,38));
        cities.add(new City(195,81,39));
        cities.add(new City(88,61,40));
        cities.add(new City(271,110,41));
        cities.add(new City(286,232,42));
        cities.add(new City(213,182,43));
        cities.add(new City(293,75,44));
        cities.add(new City(13,233,45));
        cities.add(new City(167,142,46));
        cities.add(new City(65,251,47));
        cities.add(new City(51,10,48));
        cities.add(new City(100,293,49));
        return cities;
    }
    
    public static ArrayList<City> loadAlex40Cities(){
        ArrayList<City> cities=new ArrayList();
   
        
        cities.add(new City(18,5,0));
        cities.add(new City(17,164,1));
        cities.add(new City(147,32,2));
        cities.add(new City(49,280,3));
        cities.add(new City(99,13,4));
        cities.add(new City(102,215,5));
        cities.add(new City(43,270,6));
        cities.add(new City(33,26,7));
        cities.add(new City(195,26,8));
        cities.add(new City(183,230,9));
        cities.add(new City(93,85,10));
        cities.add(new City(261,123,11));
        cities.add(new City(179,9,12));
        cities.add(new City(123,294,13));
        cities.add(new City(110,86,14));
        cities.add(new City(182,293,15));
        cities.add(new City(101,156,16));
        cities.add(new City(266,209,17));
        cities.add(new City(175,260,18));
        cities.add(new City(91,92,19));
        cities.add(new City(222,186,20));
        cities.add(new City(146,129,21));
        cities.add(new City(71,101,22));
        cities.add(new City(33,293,23));
        cities.add(new City(26,93,24));
        cities.add(new City(256,223,25));
        cities.add(new City(81,217,26));
        cities.add(new City(216,83,27));
        cities.add(new City(3,48,28));
        cities.add(new City(67,175,29));
        cities.add(new City(69,220,30));
        cities.add(new City(43,9,31));
        cities.add(new City(227,127,32));
        cities.add(new City(98,216,33));
        cities.add(new City(87,26,34));
        cities.add(new City(39,219,35));
        cities.add(new City(242,298,36));
        cities.add(new City(255,86,37));
        cities.add(new City(120,38,38));
        cities.add(new City(26,77,39));
        
        return cities;
    }
    
    public static ArrayList<City> loadOliver30Cities(){
        ArrayList<City> cities=new ArrayList();
        cities.add(new City(54, 67,0));
        cities.add(new City(54, 62,1));
        cities.add(new City(37, 84,2));
        cities.add(new City(41, 94,3));
        cities.add(new City(2, 99,4));
        cities.add(new City(7, 64,5));
        cities.add(new City(25, 62,6));
        cities.add(new City(22, 60,7));
        cities.add(new City(18, 54,8));
        cities.add(new City(4, 50,9));
        cities.add(new City(13, 40,10));
        cities.add(new City(18, 40,11));
        cities.add(new City(24, 42,12));
        cities.add(new City(25, 38,13));
        cities.add(new City(44, 35,14));
        cities.add(new City(41, 26,15));
        cities.add(new City(45, 21,16));
        cities.add(new City(58, 35,17));
        cities.add(new City(62, 32,18));
        cities.add(new City(82,  7,19));
        cities.add(new City(91, 38,20));
        cities.add(new City(83, 46,21));
        cities.add(new City(71, 44,22));
        cities.add(new City(64, 60,23));
        cities.add(new City(68, 58,24));
        cities.add(new City(83, 69,25));
        cities.add(new City(87, 76,26));
        cities.add(new City(71, 71,27));
        cities.add(new City(58, 69,28));
        cities.add(new City(74, 78,29));
        return cities;
    }
    
    public static ArrayList<City> loadTestMapA(){
        ArrayList<City> cities=new ArrayList();
        cities.add(new City(145,286,0));
        cities.add(new City(188,97,1));
        cities.add(new City(171,181,2));
        cities.add(new City(275,92,3));
        cities.add(new City(28,59,4));
        cities.add(new City(149,8,5));
        cities.add(new City(240,52,6));
        cities.add(new City(175,204,7));
        cities.add(new City(272,139,8));
        cities.add(new City(70,131,9));
        return cities;
    }
    
    public static ArrayList<City> loadTestMapB(){
        ArrayList<City> cities=new ArrayList();
        cities.add(new City(18,46,0));
        cities.add(new City(126,192,1));
        cities.add(new City(172,44,2));
        cities.add(new City(255,48,3));
        cities.add(new City(207,211,4));
        cities.add(new City(227,48,5));
        cities.add(new City(115,132,6));
        cities.add(new City(172,184,7));
        cities.add(new City(96,29,8));
        cities.add(new City(35,197,9));
        cities.add(new City(235,176,10));
        cities.add(new City(160,142,11));
        cities.add(new City(255,24,12));
        cities.add(new City(297,106,13));
        cities.add(new City(53,114,14));
        cities.add(new City(219,44,15));
        cities.add(new City(236,235,16));
        cities.add(new City(281,203,17));
        cities.add(new City(13,43,18));
        cities.add(new City(274,223,19));
        return cities;
    }
    
    protected void edgeMatrixTest(){
        String shitfuck="";
        for(int i=0;i<cities.size();i++){
            for(int j=cities.size()-1;j>i;j--){
                if(trails.get(i).get(j)!=trails.get(j).get(i))
                    shitfuck+="\ntrails["+i+"]["+j+"] != trails["+j+"]["+i+"]\n"+trails.get(i).get(j)+" != "+trails.get(j).get(i);
                //if(trails[i][j]!=trails[j][i])
                    //throw new IllegalArgumentException("trails["+i+"]["+j+"] != trails["+j+"]["+i+"]\n"+trails.get(i).get(j)+" != "+trails.get(j).get(i));
            }
        }
        System.out.println(shitfuck);
    }
    
    /************************************
    //****** TRAIL MANIPULATION CODE ******
    ************************************/
  
    protected void evaporateTrails(){
        for(int i=0;i<cities.size();i++){
            for(int j=i+1;j<cities.size();j++){
                double evaporated=trails.get(cities.get(i).getId()).get(cities.get(j).getId()) * (evap);
                if(evaporated<=Double.MIN_VALUE)
                    evaporated=Double.MIN_VALUE;
                trails.get(cities.get(i).getId()).set(cities.get(j).getId(), evaporated);                
                trails.get(cities.get(j).getId()).set(cities.get(i).getId(), trails.get(cities.get(i).getId()).get(cities.get(j).getId()));

            }
        }

    }
    
    protected void updateTrails(){
        evaporateTrails();
        applyTrails();
    }
    
    protected void setTrail(City a, City b,double trail){
        if(trail<0)
            throw new IllegalArgumentException("adjusting trails["+a.getId()+"]["+b.getId()+"]"
                    + " to "+trail+""
                            + " from:"+trails.get(a.getId()).get(b.getId()));

        
        trails.get(a.getId()).set(b.getId(), trail);
        trails.get(b.getId()).set(a.getId(), trail);
    }
    
    
    
    protected double getTrail(City a,City b){
        int bCityId=b.getId();
        int aCityId=a.getId();
        if(containsEdge(aCityId,bCityId)){
            return trails.get(aCityId).get(bCityId);
            //return trails[aCityId][bCityId];
        }
        else{
            System.out.println("Error while checking Cities("+a+","+b+")");
            printTrailMatrix();
            throw new NullPointerException("Edge 'trails["+aCityId+"]["+bCityId+"]="+ trails.get(aCityId).get(bCityId)+"' does not exist!!");
        }
    }
    
    /*protected double getTrail(City a,City b){
        int bCityId=b.getId();
        int aCityId=a.getId();
        int smallId,bigId;
        if(bCityId>aCityId){
            smallId=aCityId;
            bigId=bCityId;
        }
        else{
            smallId=bCityId;
            bigId=aCityId;
        }
        if(containsEdge(smallId,bigId)){
            return trails.get(smallId).get(bigId);
            //return trails[aCityId][bCityId];
        }
        else{
            System.out.println("Error while checking Cities("+a+","+b+")");
            printTrailMatrix();
            throw new NullPointerException("Edge 'trails["+aCityId+"]["+bCityId+"]="+ trails.get(aCityId).get(bCityId)+"' does not exist!!");
        }
    }*/
    
    public void printTrailMatrix(){
        System.out.println("CYCLE:"+cycleCounter);
        for(int i=0;i<cities.size();i++){
            String line="";
            for(int j=0;j<cities.size();j++){
                line+="Edge["+i+"]["+j+"]:"+trails.get(i).get(j)+"   ";
            }
            System.out.println(line);
        }
    }
    
    protected void applyTrails(){
        double trail;
        City a,b;
        for(int i=0;i<ants.size();i++){
            for(int j=0;j<ants.get(i).getRoute().size()-1;j++){    
                //System.out.println("Ant "+i+"'s routeLength--> "+ants.get(i).getRouteLength()+" applying trail..");
                
                //try{
                    //a=ants.get(i).getVisitedCity(j);
                    //b=ants.get(i).getVisitedCity(j+1);
                    a=ants.get(i).getRoute().get(j);
                    b=ants.get(i).getRoute().get(j+1);
                    trail=getTrail(a,b);
                    
                    trail+= Q/ants.get(i).getRouteLength();
                    //System.out.println("trail"+getTrail(a,b)+"+"+(Q/ants.get(i).getRouteLength())+"-->"+trail);
                    //if(trail<=0.0)
                    //    throw new IllegalArgumentException("Trail dropped to 0 in the applyTrails method. Cycle:"+cycleCounter);
                    setTrail(a,b,trail);
                //}
                /*catch(IllegalArgumentException e){
                    System.out.println("EXCEPTION EXCEPTION EXCEPTION");
                    System.out.println(" IN edge('"+ants.get(i).getVisitedCity(j).getId()+"'.'"+ants.get(i).getVisitedCity(j+1).getId()+"') J="+j);
                    ants.get(i).printRoute();
                    throw new IllegalArgumentException("EXCEPTION EXCEPTION EXCEPTION");
                }
                */
            }
        }
    }
    
    
    /************************************
    //****** VARIOUS GETTER CODE ******
    ************************************/
    public double getC(){
        return c;
    }
    public double getAlpha(){
        return alpha;
    }
    public double getBeta(){
        return beta;
    }
    public double getQ(){
        return Q;
    }
    public double getAntFactor(){
        return antFactor;
    }
    public int getM(){
        return (int)antFactor*cities.size();
    }
    public Double getMinRouteLength(){
        return bestAnt.getRouteLength();
    }
    public double getEvap(){
        return evap;
    }
    public Ant getBestAnt(){
        return bestAnt;
    }
    
   
  
    
    
    
    
    
    
    
    
    public static void main(String[]args)throws IOException{
            AntSystem as=new AntSystem(10.0,100.0,1.0,3.0,0.5,1.0,AntSystem.loadAlex40Cities());
            
        
   
    }
    
    
 

    public static void generateMapCode(int num){
        ArrayList<City> cities=AntSystem.generateCities(num);
        for(int i=0;i<num;i++){
            System.out.println("cities.add(new City("+cities.get(i).getX()+","+ cities.get(i).getY()+","+cities.get(i).getId()+"));");
        }
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

