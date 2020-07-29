/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Label;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import static java.util.Arrays.binarySearch;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 *
 * @author Alex
 */


public class AnimationPanel extends JPanel {
    private double alpha,beta,evap;
    
    private ArrayList<City> cities;
    private City cityStart;
    private ArrayList<AnimeAnt> animeAnts;
    private Ant[]rankedAnts;
    private int citiesStartedNum;

    private int rankedAntAmount,elitistAmount,cycleCounter;
    private Image background;
    private final double XYscaleFactor, XplusFactor,YplusFactor,XPlusFactorLetters,YPlusFactorLetters;
    private long playTime =1000;
    private int routePointer;
    private AnimeAnt bestAnt;
    private AnimeAnt prevBestAnt;
    private boolean hasCommonStartCity=true,isAntSystem=false,isElitist=false,isRanked=false;
    private boolean isRunning=false;
    private boolean isSmaller=false;
    private DataPanel dataPanel;
    
   /* public AnimationPanel(){
        antSystem=new AntSystem(AntSystem.generateCities(10));
        
        cities=antSystem.getCities();
        bruteForceRoute=AntSystem.solveBruteForce(cities);
        bruteForceDistance=City.calculatePathDistance(bruteForceRoute);
        animeAnts=new ArrayList();
        background = new ImageIcon("src/aco/background3.jpg","grass").getImage();
        Dimension bgsize = new Dimension(background.getWidth(null), background.getHeight(null));
        
        
        XplusFactor= bgsize.getWidth()/2.0;
        
        YplusFactor=bgsize.getHeight()/6.0;
        
        XYscaleFactor=getXYScale(bgsize.getWidth()/2.0,bgsize.getHeight()*4.0 /6.0);
        
        for(Ant ant:antSystem.getAnts())
            animeAnts.add(new AnimeAnt(ant,XYscaleFactor,XplusFactor,YplusFactor,playTime));    
        
        setPreferredSize(bgsize);
        setMinimumSize(bgsize);
        setMaximumSize(bgsize);
        setSize(bgsize);
        setLayout(null);
        setVisible(true);
    }
    */
    public AnimationPanel(AntSystem system){
        URL url=getClass().getResource("background3.jpg");
        background = new ImageIcon(url).getImage();
        Dimension bgsize = new Dimension(background.getWidth(null), background.getHeight(null));
        
        dataPanel=new DataPanel(); 
        cycleCounter=system.getCycleCounter();
        alpha=system.getAlpha();
        beta=system.getBeta();
        evap=system.getEvap();
        cities=new ArrayList();
        for(int i=0;i<system.getCities().size();i++){
            cities.add(system.getCities().get(i));
        }
        hasCommonStartCity=system.hasCommonCityStart();
        if(system.hasCommonCityStart())
            cityStart=system.getCityStart();
        //bruteForceRoute=AntSystem.solveBruteForce(cities);
        //bruteForceDistance=City.calculatePathDistance(bruteForceRoute);
        animeAnts=new ArrayList();
        
        
        XYscaleFactor=getXYScale(bgsize.getWidth()/2.0,bgsize.getHeight()*4.0 /6.0);
        
        //XplusFactor= bgsize.getWidth()/2.0;
        XPlusFactorLetters=bgsize.getWidth()/2.0;
        XplusFactor=calculateXPlusFactor(bgsize.getWidth()*4.0/10.0, bgsize.width);
        YplusFactor=calculateYPlusFactor(0,bgsize.height);
        
        //YplusFactor=bgsize.getHeight()/6.0;
        YPlusFactorLetters=bgsize.getHeight()/6.0;
        
        if(system instanceof Ranked){
            Ranked rankedSystem=(Ranked)system;
            isRanked=true;
            rankedAntAmount=rankedSystem.getRankedAntSize();
            //queue=new PriorityQueue();
            //Ant[] orderedAnts=rankedSystem.getAntsOrdered();
            //orderedAnimeAnts=new AnimeAnt[rankedAntAmount];
            //for(int i=0;i<rankedAntAmount;i++){
             //  orderedAnimeAnts[i]=new AnimeAnt(new AnimeAnt(orderedAnts[i],XYscaleFactor,XplusFactor,YplusFactor,playTime));
           // }
            
        }
        
        
        
        else if(system instanceof Elitist){
            isElitist=true;
            elitistAmount=((Elitist)system).getElitistSize();
        }
        else
            isAntSystem=true;
        
       
        addMouseListener(new MouseAdapter() {
            @Override
            
            public void mousePressed(MouseEvent e) {
                int x=e.getX();
                int y=e.getY();
                if(!isRunning){
                    if(e.getButton()==MouseEvent.BUTTON1 && e.getX()>(int)(background.getWidth(null)/2.5) && x<background.getWidth(null) && y>1 && y<background.getHeight(null)-1){
                        cities.add(new City((int)((e.getX()-XplusFactor)/XYscaleFactor), (int)((e.getY()-YplusFactor)/XYscaleFactor),cities.size()));
                        
                        //cities.add(new City((int)(e.getX()-XplusFactor),(int)(e.getY()-YplusFactor),cities.size()));
                    }//&& citiesAddedCounter>0
                    if(e.getButton()==MouseEvent.BUTTON3 && cities.size()>2){
                        cities.remove(cities.size()-1);
                        if(cities.size()<citiesStartedNum)
                            isSmaller=true;
                    }
                    repaint();
                }
            }
        });
        
        for(Ant ant:system.getAnts())
            animeAnts.add(new AnimeAnt(ant,XYscaleFactor,XplusFactor,YplusFactor,playTime));    
        /*
        if(system instanceof Ranked){
            Ranked rankedAntSystem=(Ranked)system;
            for(Ant ant:rankedAntSystem.getRankedAnts().getList())
                animeRankedAnts.add(new AnimeAnt(ant,XYscaleFactor,XplusFactor,YplusFactor,playTime));
        }
        */
        
        
        /*if(isRanked){
            
            LinkedList<Ant> rankedQueue=((Ranked)system).getRankedAnts();
            Ant antIter;
            int rankedQueueSize=((Ranked)system).getRankedAntSize();
            for(int i=0;i<rankedQueueSize;i++){
                antIter=rankedQueue.poll();
                animeAnts.add(new AnimeAnt(antIter,XYscaleFactor,XplusFactor,YplusFactor,playTime));
            }
                
        }
        else{
            for(Ant ant:system.getAnts())
                animeAnts.add(new AnimeAnt(ant,XYscaleFactor,XplusFactor,YplusFactor,playTime));
        }
        */
        
        
        setLayout(null);
        add(dataPanel);
        dataPanel.setBounds(50,500,400,100);
        
        setPreferredSize(bgsize);
        setMinimumSize(bgsize);
        setMaximumSize(bgsize);
        setSize(bgsize);
        setVisible(true);
        
    }
    
    public double calculateXPlusFactor(double allowedStart,double allowedEnd){
                //double maxX=cities[0].getX(),maxY=cities[0].getY(),minX=cities[0].getX(),minY=cities[0].getY();

        double maxX=cities.get(0).getX()* XYscaleFactor,minX=cities.get(0).getX()* XYscaleFactor;
        for(int i=0;i<cities.size();i++){
            if(cities.get(i).getX() * XYscaleFactor>maxX)
                maxX=cities.get(i).getX() * XYscaleFactor;
            if(cities.get(i).getX() * XYscaleFactor<minX)
                minX=cities.get(i).getX()* XYscaleFactor;
        }
        
        double allowedDiff=allowedEnd-allowedStart;
        double allowedCenter=(allowedDiff/2) + allowedStart;
        
        double diffX=maxX-minX;
        double centerX=(diffX/2)+ minX;
        
      
        
        return allowedCenter-centerX;
        
    }
    
    public double calculateYPlusFactor(double allowedStart,double allowedEnd){
                //double maxX=cities[0].getX(),maxY=cities[0].getY(),minX=cities[0].getX(),minY=cities[0].getY();
        double maxY=cities.get(0).getY()* XYscaleFactor,minY=cities.get(0).getY()* XYscaleFactor;
        for(int i=0;i<cities.size();i++){
            if(cities.get(i).getY() * XYscaleFactor>maxY)
                maxY=cities.get(i).getY()* XYscaleFactor;
            if(cities.get(i).getY() * XYscaleFactor<minY)
                minY=cities.get(i).getY()* XYscaleFactor;
        }
        
        double allowedDiff=allowedEnd-allowedStart;
        double allowedCenter=(allowedDiff/2) + allowedStart;
        
        double diffY=maxY-minY;
        double centerY=(diffY/2)+ minY;
        
      
        
        return allowedCenter-centerY;
        
    }
    
    
    public double getXYScale(double allowedWidth,double allowedHeight){
        /*find the largest distance of X or Y coordinates,
        compare it to the allowed distance of the screen,
        return the XY scaleFactor.
        */
        
        double maxX=cities.get(0).getX(),maxY=cities.get(0).getY(),minX=cities.get(0).getX(),minY=cities.get(0).getY();
        for(int i=0;i<cities.size();i++){
            if(cities.get(i).getX()>maxX)
                maxX=cities.get(i).getX();
            if(cities.get(i).getX()<minX)
                minX=cities.get(i).getX();
            if(cities.get(i).getY()>maxY)
                maxY=cities.get(i).getY();
            if(cities.get(i).getY()<minY)
                minY=cities.get(i).getY();
        }
        
        
        double distanceX=maxX-minX,distanceY=maxY-minY;
        double widthScale=distanceX/allowedWidth;
        double heightScale=distanceY/allowedHeight;
        
        double largestScale=widthScale>heightScale?widthScale:heightScale;
        return 1/largestScale;
        
        
        /*if(widthScale>1){
                return widthScale<heightScale ? widthScale:heightScale;  
        }
        else{
            if(heightScale>1)
                return  heightScale;
            else
                return widthScale<heightScale ? widthScale:heightScale;
        }
        */
        
        
    }
    
    public void paintComponent(Graphics g){
        
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        final Font defaultFont=g2d.getFont();
        //***** BackGround Paint *****
        g2d.drawImage(background, 0,0, null);
        
        //paintAllowedXY(g2d);
        
        paintSystemType(g2d);
        g2d.setFont(defaultFont);

        paintSystemInfo(g2d);
        g2d.setFont(defaultFont);

        paintSystemCycle(g2d);
        g2d.setFont(defaultFont);
 
        paintBestRouteDistance(g2d);
        g2d.setFont(defaultFont);

        paintEdges(g2d);
        g2d.setFont(defaultFont);
        if(!isSmaller){
            paintBestAntRoute(g2d);
            g2d.setFont(defaultFont);
        }
        paintCities(g2d);
        
        if(!isRunning){
            paintAddCitiesText(g2d);
        }
       
        paintAnts(g2d);
    }
    
    /*public void paintAllowedXY(Graphics2D g2d){
        Dimension bgsize = new Dimension(background.getWidth(null), background.getHeight(null));  
        g2d.fill(new Ellipse2D.Double((bgsize.width)*4.0/10.0,bgsize.height/6.0,12,12));
    }
    */
    
    public void paintSystemType(Graphics g2d){
        String systemTypeStr="";
        g2d.setFont(new Font("Bauhaus 93",Font.BOLD,40));
        if(isRanked)
            systemTypeStr="Ranked Ant System";
        else if(isElitist)
            systemTypeStr="Elitist Ant Sytem";
        else
            systemTypeStr="Ant System";
        
        g2d.drawString(systemTypeStr,(int) (XPlusFactorLetters/6.0),(int)YPlusFactorLetters);
        
    }
    
    public void paintSystemInfo(Graphics2D g2d){
        //***** System Info*******
        g2d.setFont(new Font("Bauhaus 93",Font.BOLD,20));
        /*double alpha=system.getAlpha();
        double beta=system.getBeta();
        double evap=system.getEvap();
        int antSize=system.getAnts().size();
        int elitistSize;
        int rankedSize;
        */
        final int fontSize=15;
        final int smallSpacing=10;
        final int fontPos=150;
  
        g2d.drawString("Alpha (pheromone influence):"+alpha,(int) (XPlusFactorLetters/6.0),(int)YPlusFactorLetters+fontPos+smallSpacing+(fontSize)*1);
        g2d.drawString("Beta (distance influence):"+beta,(int) (XPlusFactorLetters/6.0),(int)YPlusFactorLetters+fontPos+(smallSpacing*2)+(fontSize)*2);
        g2d.drawString("Pheromone Evaporation rate:"+(1.0-evap),(int) (XPlusFactorLetters/6.0),(int)YPlusFactorLetters+fontPos+(smallSpacing*3)+(fontSize)*3);
        g2d.drawString("Ants:"+animeAnts.size(),(int) (XPlusFactorLetters/6.0),(int)YPlusFactorLetters+fontPos+(smallSpacing*4)+(fontSize)*4);
       
        if(isElitist)
            g2d.drawString("\nElitist Ants:"+elitistAmount,(int) (XPlusFactorLetters/6.0),(int)YPlusFactorLetters+fontPos+(smallSpacing*5)+(fontSize)*5);
        
        else if(isRanked){
            g2d.drawString("\nElitist Ants:"+elitistAmount,(int) (XPlusFactorLetters/6.0),(int)YPlusFactorLetters+fontPos+(smallSpacing*5)+(fontSize)*5);
            g2d.drawString("Rank-Based Ants:"+rankedAntAmount,(int) (XPlusFactorLetters/6.0),(int)YPlusFactorLetters+fontPos+(smallSpacing*6)+(fontSize)*6);  
        }
    }
    
    public void paintSystemCycle(Graphics2D g2d){
        g2d.setFont(new Font("Bauhaus 93",Font.BOLD,40));
        g2d.drawString("Cycle:"+cycleCounter,(int) (XPlusFactorLetters/6.0),(int)YPlusFactorLetters+40);
    }
    
    public void paintBestRouteDistance(Graphics2D g2d){
        g2d.setFont(new Font("Bauhaus 93",Font.BOLD,20));
        g2d.setColor(Color.cyan);
        g2d.drawString("Best Route Found:"+String.format("%.2f", bestAnt.getAnt().getRouteLength()),(int) (XPlusFactorLetters/6.0),(int)YPlusFactorLetters+80);
    }
    
    /*public void paintBruteForceRouteDistance(Graphics2D g2d){
        g2d.setFont(new Font("Bauhaus 93",Font.BOLD,20));
        g2d.setColor(Color.red);
        g2d.drawString("Brute Force Route:"+String.format("%.2f", bruteForceDistance),(int) (XplusFactor/6.0),(int)YplusFactor+80);
    }
    */
    
    /*public void paintBruteForceRoute(Graphics2D g2d){
        g2d.setColor(Color.red);
        for(int i=0;i<bruteForceRoute.size()-1;i++)
            g2d.drawLine((int)(bruteForceRoute.get(i).getX()*XYscaleFactor+XplusFactor-2),(int)(bruteForceRoute.get(i).getY()*XYscaleFactor+YplusFactor-2),(int)(bruteForceRoute.get(i+1).getX()*XYscaleFactor+XplusFactor-2),(int)(bruteForceRoute.get(i+1).getY()*XYscaleFactor+YplusFactor-2));  
    }
    */
    
    public void paintEdges(Graphics2D g2d){
        //****** Edges Paint ******
        g2d.setColor(Color.LIGHT_GRAY);
        
        for(int i=0;i<cities.size();i++){
            for(int j=cities.size()-1;j>i;j--){
                g2d.drawLine((int)(cities.get(i).getX()*XYscaleFactor+XplusFactor),(int)(cities.get(i).getY()*XYscaleFactor+YplusFactor),(int)(cities.get(j).getX()*XYscaleFactor+XplusFactor),(int) (cities.get(j).getY()*XYscaleFactor+YplusFactor));
            }
        }
    }
    
    public void paintCities(Graphics2D g2d){
        g2d.setColor(Color.black);
        for(int i=0;i<cities.size();i++){
            g2d.fill(new Ellipse2D.Double(cities.get(i).getX()*XYscaleFactor+XplusFactor-6,cities.get(i).getY()*XYscaleFactor+YplusFactor-6,12,12));
            g2d.drawString(cities.get(i).toString(),(int) (cities.get(i).getX()*XYscaleFactor+XplusFactor),(int)(cities.get(i).getY()*XYscaleFactor+YplusFactor-7));
        }
        
        g2d.setColor(Color.PINK);
        if(hasCommonStartCity)
            g2d.fill(new Ellipse2D.Double(cityStart.getX()*XYscaleFactor+XplusFactor-8,cityStart.getY()*XYscaleFactor+YplusFactor-8,16,16));
        g2d.setColor(Color.BLACK);
    }
    
    public void paintAnts(Graphics2D g2d){
        //****** Ants Paint **********
        
        /*for(int i=0;i<animeAnts.size();i++)
            g2d.fill(new Ellipse2D.Double(animeAnts.get(i).getCurrentPos().getX()-5,animeAnts.get(i).getCurrentPos().getY()-5,10,10));
        */
        //int rankedAntPainted=0;
        if(isRanked){
            for(int i=0;i<animeAnts.size();i++){
                AnimeAnt ant=animeAnts.get(i);
                if(isRankedAnimeAnt(ant))
                    g2d.setColor(Color.RED);
                else
                    g2d.setColor(Color.BLUE);
                
                g2d.fill(new Ellipse2D.Double(animeAnts.get(i).getCurrentPos().getX()-5,animeAnts.get(i).getCurrentPos().getY()-5,10,10));
            }

        }
        else{
            g2d.setColor(Color.BLUE);
            for(int i=0;i<animeAnts.size();i++)
                g2d.fill(new Ellipse2D.Double(animeAnts.get(i).getCurrentPos().getX()-5,animeAnts.get(i).getCurrentPos().getY()-5,10,10));
        }
        
        
        
  
    }
    
    public void paintBestAntRoute(Graphics g2d){
        
        ArrayList<City> bestRoute=bestAnt.getRoute();
        g2d.setColor(Color.cyan);

        for(int i=0;i<bestRoute.size()-1;i++){
            g2d.drawLine((int)(bestRoute.get(i).getX()*XYscaleFactor+XplusFactor),(int)(bestRoute.get(i).getY()*XYscaleFactor+YplusFactor),(int)(bestRoute.get(i+1).getX()*XYscaleFactor+XplusFactor),(int)(bestRoute.get(i+1).getY()*XYscaleFactor+YplusFactor));
        }
        
    }
    
    public void antsToAnimeAnts(AntSystem system){
        ArrayList<Ant> antSystemAnts=system.getAnts();
        for(int i=0;i<antSystemAnts.size();i++){
            animeAnts.set(i,new AnimeAnt(antSystemAnts.get(i),XYscaleFactor,XplusFactor,YplusFactor,playTime));
        }
    }
    
    public void loadOrderedAnts(AntSystem system){
        rankedAnts=((Ranked)system).getRankedAnts();
    }
    

    public boolean isRankedAnimeAnt(AnimeAnt animeAnt){
        int index=binarySearch(rankedAnts,animeAnt.getAnt());
        if (index>=0 && index<rankedAntAmount)
            return true;
        else
            return false;

    }
    
    public void addAnimateButtonListener(ActionListener listener){
        dataPanel.addAnimateButtonListener(listener);
    }
    
    public void addCancelButtonListener(ActionListener listener){
        dataPanel.addCancelButtonListener(listener);
    }

    public void updateView(AntSystem model){
        cycleCounter=model.getCycleCounter();
        if(isRanked){
            //rankedAntsToAnimeAnts(model);
            loadOrderedAnts(model);
        }
        
        antsToAnimeAnts(model);
        bestAnt=new AnimeAnt(model.getBestAnt(),XYscaleFactor,XplusFactor,YplusFactor,playTime);
        citiesStartedNum=model.getCities().size();
        isSmaller=false;
    }
    
    public void animateCycle(){
        //antSystem.cycle();
        
        isRunning=true;
        final int lastAnt=animeAnts.size()-1;
        
        Timer timer  = new Timer(40, new ActionListener() {
            
            int spawnPointer=0;
            @Override
            public void actionPerformed(ActionEvent e) {
                
                long now = System.currentTimeMillis();
                
                AnimeAnt ant;
                for(int i =0;i<=spawnPointer;i++){
                    ant=animeAnts.get(i);
                    if(!ant.spawned())
                        ant.spawn(now);
                    else if(!ant.isLastTransit()){
                        ant.spendTime(now);
                        if(ant.reachedPercentage(1.0)){
                            ant.moveNextCity(now);
                        }
                        else if(ant.reachedPercentage(0.5) && spawnPointer==i && spawnPointer<animeAnts.size()-1){
                            spawnPointer++;
                            ant.makeXYStep();
                        }
                        else{
                            ant.makeXYStep(); 
                        }
                    }
                        
                    else{
                        ant.spendTime(now);
                        
                            ant.makeXYStep(); 
                        //ANIMATION CYCLE END
                        if(i==lastAnt && ant.reachedPercentage(1.0)){
                            ((Timer) e.getSource()).stop();
                            isRunning=false;
                            dataPanel.getAnimateButton().setEnabled(true);
                            repaint();
                            return;
                        }
                    }
                    
                }
                repaint();
             }
            });
        timer.start();
        
    }
    
    /*public JPanel createMinRouteLabel(){
        JPanel MinRouteLabel
    }
    */
    
    
   
    
   
    
    public void createAndShowUI(){
        JFrame frame=new JFrame("Ant System");
        validate();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension bgDim = new Dimension(background.getWidth(null),background.getHeight(null));
        frame.setLocation(screenDim.width/2-bgDim.width/2, screenDim.height/2-bgDim.height/2);
        frame.setVisible(true);
        frame.pack();
    }
    
    public static void printFonts(){
        String fonts[] = 
      GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for ( int i = 0; i < fonts.length; i++ )
        {
            System.out.println(fonts[i]);
        }
    }
    
    public JButton getAnimateButton(){
        return dataPanel.getAnimateButton();
    }
    
    public int getSkipCyclesInput(){
        return dataPanel.getSkipCyclesInput();
    }
    
    public ArrayList<City> getCities(){
        return cities;
    }

    private void paintAddCitiesText(Graphics2D g2d) {
        int xplus=(int)(background.getWidth(null)/2.5);
        int yplus=(int)(background.getHeight(null))-1;
        int xend=(int)(background.getWidth(null)-1);
        
        g2d.setColor(Color.red);
        g2d.drawLine((int)(xplus),1,(int)xend,1);
        g2d.drawLine(xend,1,xend,yplus-1);
        g2d.drawLine(xend,yplus-1,xplus,yplus-1);
        g2d.drawLine(xplus,yplus-1,xplus,1);
        g2d.setFont(new Font("Bauhaus 93",Font.PLAIN,20));
        
        g2d.drawString("Right click to add a node. Left-Click to undo.", xplus+160, 50);
        
        
        g2d.setColor(Color.black);
    }

    
    
   
   
    

}
