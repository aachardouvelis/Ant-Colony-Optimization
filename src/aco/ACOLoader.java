/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Alex
 */
public class ACOLoader extends JPanel{    
    private ArrayList<ArrayList<City>>maps;
    private ArrayList<String>mapNames;
    private Image background;
    private JButton confirm,cancel;
    private JRadioButton yes,no,commonCityStart,differentCitiesStart;
    private JComboBox mapBox,algorithmBox;
    private JTextField citiesAmountField,elitistField,rankedField;
    private JSlider alphaSlider,betaSlider,evapSlider,antFactorSlider;
    private JPanel acoAlgoPanel,completeMapPanel,mapPanel,citiesPanel,alphaPanel,betaPanel,evapPanel,antFactorPanel,elitistPanel,confirmCancelPanel,rankedPanel;
    private JLabel maxElitist,maxRanked,afLabel4;
    
    public ACOLoader() throws IOException{
        background = new ImageIcon("src/aco/background3.jpg","grass").getImage();
        Dimension bgsize = new Dimension(background.getWidth(null), background.getHeight(null));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        
        mapNames=AntSystem.retrieveMapNames();
        maps=AntSystem.retrieveMaps();
        
        acoAlgoPanel=createAcoAlgoPanel();
        completeMapPanel=createMapPanel();
        evapPanel=createEvapPanel();
        alphaPanel=createAlphaPanel();
        betaPanel=createBetaPanel();
        antFactorPanel=createAntFactorPanel();
        elitistPanel=createElitistPanel();
        rankedPanel=createRankedPanel();
        //emptyPanel=createEmptySpacePanel(elitistPanel);
        confirmCancelPanel=createConfirmCancelPanel();
        
        add(acoAlgoPanel);
        add(completeMapPanel);
        addYesNoListener();
        add(evapPanel);
        add(alphaPanel);
        add(betaPanel);
        add(antFactorPanel);
        
        add(confirmCancelPanel);

        
        setVisible(true);

        
    }
    private JPanel createAcoAlgoPanel(){
        //ACO algo panel 
        String[] algorithmSelection={"Ant System", "Elitist Ant System", "Ranked Ant System"};
        JPanel algoPanel=new JPanel();
        algorithmBox=new JComboBox(algorithmSelection);
        algoPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        
        algorithmBox.setSize(100,50);
        algoPanel.setSize(300,100);
        JLabel acoLabel=new JLabel("Algorithm:");
        algoPanel.add(acoLabel);
        algoPanel.add(algorithmBox);
        return algoPanel;
    }
    
    private JPanel createMapPanel(){
        //Include all Map panels in a border
        JPanel completeMapPanel=new JPanel();
        completeMapPanel.setLayout(new BoxLayout(completeMapPanel, BoxLayout.Y_AXIS));
        //Map Radio Button
        JPanel radioPanel=new JPanel();
        ButtonGroup mapGroup=new ButtonGroup();
        yes=new JRadioButton("Yes");
        no=new JRadioButton ("No");
        JLabel mapLabel=new JLabel("Pre-Constructed Map:");
        radioPanel.add(mapLabel);
        mapGroup.add(yes);
        mapGroup.add(no);
        
        radioPanel.add(yes);
        radioPanel.add(no);
        completeMapPanel.add(radioPanel);
        yes.setSelected(true);
        
        //Map panel continuation
        mapPanel=new JPanel();
        
        //tring mapSelection=new String[map];
        String[] mapSelection=new String[mapNames.size()];
        for(int i=0;i<mapNames.size();i++){
            mapSelection[i]=mapNames.get(i);
        }
        //String[] mapSelection={"Oliver-30","A","B","Alex-40"};
        mapBox=new JComboBox(mapSelection);
        JLabel mapSelect=new JLabel("Select a map:");
        
        mapPanel.setSize(300,100);
        mapPanel.add(mapSelect);
        mapPanel.add(mapBox);
        
        completeMapPanel.add(mapPanel);
        
        
        //Specify Amount of Cities panel
        citiesPanel=new JPanel();
        JLabel specifyCities=new JLabel("Number of Cities");
        citiesAmountField=new JTextField(5);
        citiesPanel.add(specifyCities);
        citiesPanel.add(citiesAmountField);
        citiesPanel.setVisible(false);
        completeMapPanel.add(citiesPanel);
        
        //Starting City Panel
        
        JPanel startingCityPanel=new JPanel();
        ButtonGroup startingCityGroup=new ButtonGroup();
        commonCityStart=new JRadioButton("Common");
        differentCitiesStart=new JRadioButton ("Different");
        JLabel cityStartLabel=new JLabel("Ant Starting City:");
        startingCityPanel.add(cityStartLabel);
        startingCityGroup.add(commonCityStart);
        startingCityGroup.add(differentCitiesStart);
        
        startingCityPanel.add(commonCityStart);
        startingCityPanel.add(differentCitiesStart);
        completeMapPanel.add(startingCityPanel);
        commonCityStart.setSelected(true);
        completeMapPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        
        
        return completeMapPanel;
    }
    
    public JPanel createAlphaPanel(){
        //Alpha slide panel
        JPanel alphaPanel=new JPanel();
        alphaPanel.setSize(300,300);
        JLabel alphaLabel=new JLabel("Alpha:");
        alphaSlider = new JSlider(JSlider.HORIZONTAL,
                                      0, 10, 1);
        alphaSlider.setMajorTickSpacing(5);
        alphaSlider.setMinorTickSpacing(1);
        alphaSlider.setPaintTicks(true);
        alphaSlider.setPaintTrack(true);
        alphaSlider.setPaintLabels(true);
        
        
        alphaPanel.add(alphaLabel);
        alphaPanel.add(alphaSlider);
        return alphaPanel;
    }
    
    private JPanel createEvapPanel(){
        // Evaporation slide panel
        JPanel evapPanel=new JPanel();
        evapPanel.setSize(300,300);
        JLabel evapLabel=new JLabel("Evaporation:");
        evapSlider = new JSlider(JSlider.HORIZONTAL,
                                      0, 100, 50);
        Hashtable<Integer, JLabel> evapLabels = new Hashtable<>();
        evapLabels.put(0,new JLabel("0%"));
        evapLabels.put(50, new JLabel("50%"));
        evapLabels.put(100,new JLabel("100%"));
        
        evapSlider.setLabelTable(evapLabels);
        evapSlider.setMajorTickSpacing(10);
        evapSlider.setMinorTickSpacing(2);
        evapSlider.setPaintTicks(true);
        evapSlider.setPaintTrack(true);
        evapSlider.setPaintLabels(true);
        
        
        
        evapPanel.add(evapLabel);
        evapPanel.add(evapSlider);
        return evapPanel;
    }
    
        
    
    private JPanel createBetaPanel(){
        //Beta slide panel
        JPanel betaPanel=new JPanel();
            betaPanel.setSize(300,300);
        JLabel betaLabel=new JLabel("Beta:");
        betaSlider = new JSlider(JSlider.HORIZONTAL,
                                      0, 10, 2);
        betaSlider.setMajorTickSpacing(5);
        betaSlider.setMinorTickSpacing(1);
        betaSlider.setPaintTicks(true);
        betaSlider.setPaintTrack(true);
        betaSlider.setPaintLabels(true);
        betaPanel.add(betaLabel);
        betaPanel.add(betaSlider);
        return betaPanel;
    }
    
    private JPanel createAntFactorPanel(){
        // Ant Factor Panel
        
        JPanel antFactorPanel=new JPanel();
        antFactorPanel.setSize(300,300);
        JLabel afLabel1=new JLabel("Ants");
        JLabel afLabel2=new JLabel("to");
        JLabel afLabel3=new JLabel("Cities");
        afLabel4=new JLabel(" = "+getCitiesAmount());
        
        JPanel antFactorLabelPanel=new JPanel();
        antFactorLabelPanel.add(afLabel1);
        antFactorLabelPanel.add(afLabel2);
        antFactorLabelPanel.add(afLabel3);
        antFactorLabelPanel.setLayout(new BoxLayout(antFactorLabelPanel, BoxLayout.Y_AXIS));


        antFactorSlider = new JSlider(JSlider.HORIZONTAL,
                                      1, 200, 100);
        antFactorSlider.setMajorTickSpacing(10);
        antFactorSlider.setMinorTickSpacing(5);
        Hashtable<Integer, JLabel> antFactorLabels = new Hashtable<>();
        antFactorLabels.put(1,new JLabel("1%"));
        antFactorLabels.put(50, new JLabel("50%"));
        antFactorLabels.put(100,new JLabel("100%"));
        antFactorLabels.put(150, new JLabel("150%"));
        antFactorLabels.put(200,new JLabel("200%"));
        
        antFactorSlider.setLabelTable(antFactorLabels);
        
        
        antFactorSlider.setPaintTicks(true);
        antFactorSlider.setPaintTrack(true);
        antFactorSlider.setPaintLabels(true);
        antFactorPanel.add(antFactorLabelPanel);
        antFactorPanel.add(antFactorSlider);
        antFactorPanel.add(afLabel4);
        antFactorPanel.setBorder(BorderFactory.createLineBorder(Color.gray));

        return antFactorPanel;

    }
    
    private JPanel createElitistPanel(){
        //NUM OF ELITIST ANTS 'e' PANEL
        //maxElitist=new JLabel("Max:"+getAntsAmount());
        //maxElitist.setForeground(Color.red);
        JPanel elitistPanel=new JPanel();
        JLabel elitistLabel=new JLabel("Number of Elitist Ants:");
        elitistField=new JTextField(4);
        //JLabel maxElitist=new JLabel("Max:"+);
        elitistPanel.add(elitistLabel);
        elitistPanel.add(elitistField);
        //elitistPanel.add(maxElitist);
        elitistPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        return elitistPanel;
    }
    
    private JPanel createRankedPanel(){
        maxRanked=new JLabel("Max: N.A.");
        maxRanked.setForeground(Color.red);
        JPanel rankedPanel=new JPanel();
        JLabel rankedLabel=new JLabel("Number of Ranked Ants:");
        rankedField=new JTextField(4);
        rankedPanel.add(rankedLabel);
        rankedPanel.add(rankedField);
        rankedPanel.add(maxRanked);
        rankedPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
        return rankedPanel;
    }
    
    private JPanel createConfirmCancelPanel(){
        //OK CANCEL PANEL
        JPanel confirmPanel=new JPanel();
        confirm=new JButton("Launch");
        cancel=new JButton("Cancel");
        confirmPanel.add(confirm);
        confirmPanel.add(cancel);
        add(confirmPanel);
        
        
        return confirmPanel;
                
    }
    
    public void addAlgoBoxListener(ActionListener listener){
        algorithmBox.addActionListener(listener);
        
    }
    public void addCancelListener(ActionListener listener){
        cancel.addActionListener(listener);
    }
    public void addYesNoListener(){
        yes.addActionListener(new ActionListener(){
                @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.setVisible(true);
                citiesPanel.setVisible(false);
            }
        });
        
        no.addActionListener(new ActionListener(){
                @Override
            public void actionPerformed(ActionEvent e) {
                mapPanel.setVisible(false);
                citiesPanel.setVisible(true);
            }
        });
    }
    public void addConfirmListener(ActionListener listener){
        confirm.addActionListener(listener);
    }
    
   /* public void addCancelListener(ActionListener listener){
        cancel.addActionListener(listener);
    }
    */
    public JButton getConfirmButton(){
        return confirm;
    }
   
    public JButton getCancelButton(){ return cancel;}
    public JRadioButton getYesRadioButton(){ return yes;}
    public JRadioButton getNoRadioButton(){return no;}
    public JComboBox getMapBox(){return mapBox;}
    public JRadioButton getCommonStartingCityRadioButton(){ return commonCityStart;}
    public JRadioButton getDifferentStartingCitiesRadioButton(){ return differentCitiesStart;}
    public JComboBox getAlgorithmBox(){return algorithmBox;}
    public JTextField getCitiesAmountField(){return citiesAmountField;}
    public JTextField getElitistField(){return elitistField;}
    public JTextField getRankedAntField(){return rankedField;}
    public JSlider getAlphaSlider(){return alphaSlider;}
    public JSlider getBetaSlider(){ return betaSlider;}
    public JSlider getAntFactorSlider(){return antFactorSlider;}
    public JSlider getEvaporationSlider(){return evapSlider;}
    public JLabel getAntsNumLabel(){return afLabel4;}
    
    public JPanel getConfirmCancelPanel(){
        return confirmCancelPanel;
    }
    public JPanel getElitistPanel(){
        return elitistPanel;
    }
    public JPanel getRankedPanel(){
        return rankedPanel;
    }
    

   public JTextField getRankedField() {
       return rankedField;
    }

    /*private ArrayList<City[]> getCustomMaps() throws FileNotFoundException, IOException {
        ArrayList<City[]> maps=new ArrayList();
        BufferedReader br;
        File[] mapfiles=new File("src/Maps").listFiles();
        for(int i=0;i<mapfiles.length;i++){
            br=new BufferedReader(new FileReader(mapfiles[i].getName()));
            String s = null;
            ArrayList<City> citieslist=new ArrayList();
            while ((s=br.readLine())!=null){
                String[]split;
                split=s.split(" ");
                citieslist.add(new City(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2])));
            }
            City[]cities=new City[citieslist.size()];
            for(int j=0;j<citieslist.size();j++)
                cities[j]=citieslist.get(j);
            maps.add(cities);
            
        }
        return maps;
        
    }

    private ArrayList<String> getCustomMapNames() {
        ArrayList<String> names=new ArrayList();
        File mapfolder=new File("src/Maps");
        File[] files=mapfolder.listFiles();
        for(File file:files){
            names.add(file.getName());
        }
        return names;
    }
   */
    
    public int getCitiesAmount()throws NumberFormatException{
        if(yes.isSelected()){
            int index=mapBox.getSelectedIndex();
            return maps.get(index).size();
        }
        else
            return Integer.parseInt(citiesAmountField.getText());
            
    }
    
    public int getAntsAmount()throws NumberFormatException{
        return (int)(getCitiesAmount()*getAntFactorSlider().getValue() / 100);
    }


    
    public ArrayList<ArrayList<City>> getMaps(){
        return maps;
    }
    
    public ArrayList<String>getMapNames(){
        return mapNames;
    }
    
    public JLabel getMaxElitist(){
        return maxElitist;
    }

    public JLabel getMaxRanked(){
        return maxRanked;
    }
    
    public boolean isRandomMapSelected(){
        return no.isSelected();
    }
    
    public void addAntFactorListener(ChangeListener listener){
        antFactorSlider.addChangeListener(listener);
    }
    
    public void addMapBoxListener(ActionListener listener){
        mapBox.addActionListener(listener);
    }
    
    public void addAntFieldListener(DocumentListener listener){
        this.citiesAmountField.getDocument().addDocumentListener(listener);
    }
   
    public void addElitistFieldListener(DocumentListener listener){
        this.elitistField.getDocument().addDocumentListener(listener);
    }
    
    public void addYesButtonListener(ActionListener Listener){
        this.getYesRadioButton().addActionListener(Listener);
    }
    
    public void addNoButtonListener(ActionListener listener){
        this.getNoRadioButton().addActionListener(listener);
    }

     
   
    
}
