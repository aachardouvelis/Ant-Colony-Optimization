/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Alex
 */
public class AntSystemController {
    private AnimationPanel animePanel;
    private AntSystem model;
    private ACOLoader acoLoaderPanel;
    private StartMenu startViewPanel;
    private MapCreator mapCreatorPanel;
    
    private JFrame animationFrame;
    private JFrame acoLoaderFrame;
    private JFrame startMenuFrame;
    private JFrame mapCreatorFrame;
 
    
    public AntSystemController(){

        startViewPanel=new StartMenu();
        addStartMenuListeners();
        
    }
    
    public void addMapCreatorListeners(){
        mapCreatorPanel.addClickListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ArrayList<City> cities=mapCreatorPanel.getCities();
                if(e.getButton()==MouseEvent.BUTTON1){
                    cities.add(new City(e.getX(), e.getY(),cities.size()));
                }
                if(e.getButton()==MouseEvent.BUTTON3 && cities.size()>0){
                    cities.remove(cities.size()-1);
                }
                mapCreatorPanel.repaint();
            }
        });
        mapCreatorPanel.addSaveListener(new ActionListener(){
                @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    AntSystem.createMap(mapCreatorPanel.getCities(), mapCreatorPanel.getFileName());
                    mapCreatorFrame.dispose();
                    startMenuFrame.setVisible(true);

                }
                catch(IOException io){
                    JOptionPane.showMessageDialog(null, io.getMessage());
                }      
            }
        }); 
        mapCreatorPanel.getExitButton().addActionListener(new ActionListener(){
                @Override
            public void actionPerformed(ActionEvent e) {
                mapCreatorFrame.dispose();
                startMenuFrame.setVisible(true);
            }
        });
        
        
        
    }
    
    public void addStartMenuListeners(){
        
        startViewPanel.addCreateMapListener(new ActionListener(){
                @Override
            public void actionPerformed(ActionEvent e) {
                startMenuFrame.setVisible(false);
                createAndShowMapCreatorPanel();
                addMapCreatorListeners();
            }
        });
        
        startViewPanel.addLoadAcoListener(new ActionListener(){
                @Override
            public void actionPerformed(ActionEvent e) {
                startMenuFrame.setVisible(false);
                createAndShowACOLoaderPanel();
                addACOLoaderListeners();
            }
        });
        
        startViewPanel.addExitListener(new ActionListener(){
                @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        
    }
    
    public void createAndShowMapCreatorPanel(){
        mapCreatorFrame = new JFrame("Create Map");
                handleFrameClosing(mapCreatorFrame);
                
                mapCreatorPanel=new MapCreator();
                
                
                mapCreatorFrame.add(mapCreatorPanel);
                mapCreatorFrame.setVisible(true);
                mapCreatorFrame.pack();
                
                
                Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
                Dimension frameDim=mapCreatorFrame.getContentPane().getSize();
                int y=(int) ((int)screenDim.getHeight()/2.0-(int)frameDim.getHeight()/2.0);
                int x=(int) ((int)screenDim.getWidth()/2.0-(int)frameDim.getWidth()/2.0);
                mapCreatorFrame.setLocation(x,y);
    }
    
    public void createAndShowACOLoaderPanel(){
                    try {
                        acoLoaderPanel=new ACOLoader();
                    } catch (IOException ex) {
                        Logger.getLogger(AntSystemController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                acoLoaderFrame=new JFrame("ACO Loader");
                handleFrameClosing(acoLoaderFrame);
                acoLoaderFrame.getContentPane().add(acoLoaderPanel,"Center");
                Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
                //Dimension bgDim = new Dimension(this.background.getWidth(null)/2, this.background.getHeight(null)/2);
                //mainFrame.setLocation(screenDim.width/2-bgDim.width, screenDim.height/2-bgDim.height);

                acoLoaderFrame.setVisible(true);
                acoLoaderFrame.pack();
                startMenuFrame.setVisible(false);
                
                Dimension frameDim=acoLoaderFrame.getContentPane().getSize();
                int y=(int) ((int)screenDim.getHeight()/2.0-(int)frameDim.getHeight()/2.0);
                int x=(int) ((int)screenDim.getWidth()/2.0-(int)frameDim.getWidth()/2.0);

                acoLoaderFrame.setLocation(x,y);
    }
    
    
    
    public void addACOLoaderListeners(){
        acoLoaderPanel.addAlgoBoxListener(new ActionListener(){
                @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox algorithmBox=acoLoaderPanel.getAlgorithmBox();
                if(algorithmBox.getSelectedIndex()==1){
                    acoLoaderPanel.remove(acoLoaderPanel.getConfirmCancelPanel());
                    acoLoaderPanel.remove(acoLoaderPanel.getRankedPanel());
                    acoLoaderPanel.add(acoLoaderPanel.getElitistPanel());
                    acoLoaderPanel.add(acoLoaderPanel.getConfirmCancelPanel());
                }
                else if(algorithmBox.getSelectedIndex()==2){
                    acoLoaderPanel.remove(acoLoaderPanel.getConfirmCancelPanel());
                    acoLoaderPanel.remove(acoLoaderPanel.getElitistPanel());
                    acoLoaderPanel.remove(acoLoaderPanel.getRankedPanel());
                    acoLoaderPanel.add(acoLoaderPanel.getElitistPanel());
                    acoLoaderPanel.add(acoLoaderPanel.getRankedPanel());
                    acoLoaderPanel.add(acoLoaderPanel.getConfirmCancelPanel());
                }
                    
                else{
                    acoLoaderPanel.remove(acoLoaderPanel.getElitistPanel());  
                    acoLoaderPanel.remove(acoLoaderPanel.getRankedPanel());
                }
                
                acoLoaderFrame.pack();
            }
        });
        
        acoLoaderPanel.addConfirmListener(new ActionListener(){
                @Override
            public void actionPerformed(ActionEvent e) {
                try{
                //menuView.getAlgorithmBox().getSelectedIndex();
                double alpha=acoLoaderPanel.getAlphaSlider().getValue();
                double beta=acoLoaderPanel.getBetaSlider().getValue();
                double antFactor=acoLoaderPanel.getAntFactorSlider().getValue()/100.0;
                if(antFactor==0)
                    antFactor=1/100.0;
                int Q=100;
                double c=10.0;
                double evap=1.00-acoLoaderPanel.getEvaporationSlider().getValue()/100.0;
                boolean commonCity=acoLoaderPanel.getCommonStartingCityRadioButton().isSelected();
                ArrayList<City> map= AntSystem.loadOliver30Cities();
                
                //MAP SELECTION
                if(acoLoaderPanel.getYesRadioButton().isSelected()){
                    int mapSelection=acoLoaderPanel.getMapBox().getSelectedIndex();  
                    
                    try {
                        map=AntSystem.retrieveMaps().get(mapSelection);
                        
                    } catch (IOException ex) {
                        Logger.getLogger(AntSystemController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else{
                    try{
                        int citiesAmount=Integer.parseInt(acoLoaderPanel.getCitiesAmountField().getText());
                        map=AntSystem.generateCities(citiesAmount);
                    }
                    catch (NumberFormatException ex){
                        throw new NumberFormatException("Please enter a positive integer in the number of cities field.");
                        //JOptionPane.showMessageDialog(null, "Please enter a positive integer in the number of cities field."); 
                        
                    }
                }
                
                //STARTING CITY SELECTION
                
                

                //Final Creation of AntSystem instance

                //Ant System instance
                int algIndex=acoLoaderPanel.getAlgorithmBox().getSelectedIndex();
                if(algIndex==0){
                    model=new AntSystem(c,alpha,beta,Q,evap,antFactor,map);
                }

                //Elitist Instance
                else if(algIndex==1){
                    int elitistAnts=1;
                    try{
                    elitistAnts=Integer.parseInt(acoLoaderPanel.getElitistField().getText());
                    if(elitistAnts>=antFactor*map.size())
                        throw new NumberFormatException("Please enter a positive integer less than "+(antFactor*map.size())+" in the elitist Ants fields.");
                    }
                    catch(NumberFormatException elitistNumException){
                        JOptionPane.showMessageDialog(null, "Please enter a positive integer less than "+(antFactor*map.size())+" in the elitist Ants fields."); 
                    }
                    model=new Elitist( c,alpha,beta,Q,evap,antFactor,map,elitistAnts);
                }

                //Ranked Instance

                else{
                    int elitistAnts=-1,rankedAnts=-1;
                    
                        elitistAnts=Integer.parseInt(acoLoaderPanel.getElitistField().getText());
                        rankedAnts=Integer.parseInt(acoLoaderPanel.getRankedAntField().getText());
                        if(elitistAnts<0)
                            throw new NumberFormatException("Please enter a non negative integer in the Elitist Ants field");
                        if(rankedAnts<0)
                            throw new NumberFormatException("Please enter a non negative integer in the Ranked Ants field");
                        if(elitistAnts>antFactor*map.size()) // IF Elitist ants> ants
                            throw new NumberFormatException("Please enter a non negative integer less than or equal to the number of ants ("+(antFactor*map.size())+").Number given:"+antFactor*map.size());
                        rankedAnts=Integer.parseInt(acoLoaderPanel.getRankedField().getText());
                        if(rankedAnts>=elitistAnts)
                            throw new NumberFormatException("Please enter a non negative integer less than then elitist ants ( "+elitistAnts+") in the elitist Ants fields.");
                    
                    

                    model=new Ranked( c, alpha, beta, Q, evap, antFactor, map, elitistAnts, rankedAnts);

                }
                if(!commonCity){
                    model.setDifferentStartingCity();
                }
                
                initAnimationView(model);
                    
                
                
                acoLoaderFrame.dispose();
                }
                catch(NumberFormatException exception){
                    JOptionPane.showMessageDialog(null,exception.getMessage()); 
                }
            }
            
            
            
        });
        
        acoLoaderPanel.addCancelListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                acoLoaderFrame.dispose();
                startMenuFrame.setVisible(true);
            }
        });
        
        acoLoaderPanel.addMapBoxListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int antsNum=acoLoaderPanel.getAntsAmount();
                acoLoaderPanel.getAntsNumLabel().setText(" = "+acoLoaderPanel.getAntsAmount());
                //acoLoaderPanel.getMaxElitist().setText("Max:"+(antsNum));
                //int elitist=Integer.parseInt(acoLoaderPanel.getElitistField().getText());
                //acoLoaderPanel.getMaxRanked().setText("Max:"+(elitist-1));
            }
        });
        
        acoLoaderPanel.addElitistFieldListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
              updateWarningMessages();
            }
            public void removeUpdate(DocumentEvent e) {
              updateWarningMessages();
            }
            public void insertUpdate(DocumentEvent e) {
              updateWarningMessages();
            }

            public void updateWarningMessages() {
                try{
                    int antsNum=acoLoaderPanel.getAntsAmount();
                    int elitistNum=Integer.parseInt(acoLoaderPanel.getElitistField().getText())-1;
                    if(elitistNum>0){
                        //acoLoaderPanel.getMaxElitist().setText("Max:"+(antsNum));
                        acoLoaderPanel.getMaxRanked().setText("Max:"+elitistNum);
                    }
                    else{
                        //acoLoaderPanel.getMaxElitist().setText("Max:0");
                        acoLoaderPanel.getMaxRanked().setText("Max:0");
                    }
                }
                catch(NumberFormatException e){
                    //acoLoaderPanel.getMaxElitist().setText("Max: N.A.");
                    acoLoaderPanel.getMaxRanked().setText("Max: N.A.");
                }
            }
          });
        acoLoaderPanel.getCitiesAmountField().getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
              updateWarningMessages();
            }
            public void removeUpdate(DocumentEvent e) {
              updateWarningMessages();
            }
            public void insertUpdate(DocumentEvent e) {
              updateWarningMessages();
            }

            public void updateWarningMessages() {
                try{
                    int antsNum=acoLoaderPanel.getAntsAmount();
                    if(antsNum>0){
                        acoLoaderPanel.getAntsNumLabel().setText(" = :"+antsNum);
                    }
                    else{
                        throw new NumberFormatException();
                    }
                }
                catch(NumberFormatException e){
                    //acoLoaderPanel.getMaxElitist().setText("Max: N.A.");
                    acoLoaderPanel.getMaxRanked().setText("Max: N.A.");
                }
            }
          });
        
        acoLoaderPanel.addAntFactorListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
              
              try{
                int antsNum=acoLoaderPanel.getAntsAmount();
                if(antsNum>0){
                          acoLoaderPanel.getAntsNumLabel().setText(" = "+antsNum);
                      }
                }
                catch(NumberFormatException e){
                    acoLoaderPanel.getAntsNumLabel().setText(" = N.A.");
                }
                    
            }
          });
        
        acoLoaderPanel.addNoButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                acoLoaderPanel.getAntsNumLabel().setText(" = N.A.");
                    
            }
          });
        
        acoLoaderPanel.addYesButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                acoLoaderPanel.getAntsNumLabel().setText("= "+acoLoaderPanel.getAntsAmount());
                    
            }
          });
    }
    
    public void addMainViewListeners(){
        
        try{
            
            animePanel.addCancelButtonListener(new ActionListener(){
                
                public void actionPerformed(ActionEvent e) {
                    animationFrame.dispose();
                    startMenuFrame.setVisible(true);
                    
                }
            });
        }
        catch(NullPointerException e){
            System.out.println("Animation Frame not created...");
        }
        
        animePanel.addAnimateButtonListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    
                    int cycles=0;
                    try{
                        cycles=animePanel.getSkipCyclesInput();
                    
                    }
                    catch(NumberFormatException numberformat){
                        JOptionPane.showMessageDialog(null,numberformat.getMessage()+"You need to enter a non-negative integer");
                        
                    }
                    runMainView(cycles);
                }
            });
    }
    
    
    
    
    public void initAnimationView(AntSystem model){
         animePanel=new AnimationPanel(model);
         addMainViewListeners();
         createAndShowAnimationWindow();
         runMainView(0);
    }
    
    
    private void createAndShowAnimationWindow(){
        URL url=getClass().getResource("background3.jpg");
        Image background = new ImageIcon(url).getImage();
        animationFrame=new JFrame("Ant System");
        handleFrameClosing(animationFrame);
        //validate();
        animationFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        animationFrame.getContentPane().add(animePanel);
         
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension bgDim = new Dimension(background.getWidth(null),background.getHeight(null));
        animationFrame.setLocation(screenDim.width/2-bgDim.width/2, screenDim.height/2-bgDim.height/2);
        animationFrame.setVisible(true);
        animationFrame.pack();
    }
    
    /*private void createAndShowMenuWindow(){
        menuFrame=new JFrame("Menu");
        menuFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menuFrame.getContentPane().add(menuView,"Center");
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        //Dimension bgDim = new Dimension(this.background.getWidth(null)/2, this.background.getHeight(null)/2);
        //mainFrame.setLocation(screenDim.width/2-bgDim.width, screenDim.height/2-bgDim.height);
       
        menuFrame.setVisible(true);
        menuFrame.pack();
       
        Dimension frameDim=menuFrame.getContentPane().getSize();
        int y=(int) ((int)screenDim.getHeight()/2.0-(int)frameDim.getHeight()/2.0);
        int x=(int) ((int)screenDim.getWidth()/2.0-(int)frameDim.getWidth()/2.0);
        
        menuFrame.setLocation(x,y);
        
    }*/
    
    private void createAndShowStartMenuWindow(){
        startMenuFrame=new JFrame("ACO Simulator");
                
        startMenuFrame.setSize(500,500);
                
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        


        startMenuFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        startMenuFrame.getContentPane().add(startViewPanel);

        startMenuFrame.setLocation(screenDim.width/2-startMenuFrame.getWidth()/2, screenDim.height/2-startMenuFrame.getHeight()/2);
        startMenuFrame.setVisible(true);
        //startMenuFrame.pack();
    }
    
    public void runMainView(int skipCycles){
        animePanel.getAnimateButton().setEnabled(false);
        model.updateCities(animePanel.getCities());
        model.cycle();
        
        for(int i=0;i<skipCycles;i++)
            model.cycle();
        
        animePanel.updateView(model);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run(){

            animePanel.animateCycle(); 
         }
        });
    }
    
    
    public AntSystem getModel(){
        return model;
    }
    
    public ACOLoader getMenuView(){
        return acoLoaderPanel;
    }
    
    public void handleFrameClosing(JFrame frame){
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         
                frame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        startMenuFrame.setVisible(true);
                    }
                });
    }
    
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
    
   
    public static void main(String[]args){
        AntSystemController controller=new AntSystemController();
        controller.createAndShowStartMenuWindow();
       
    }
}
