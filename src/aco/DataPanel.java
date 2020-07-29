/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Alex
 */
public class DataPanel extends JPanel{
    private JLabel cycleLabel,cycleOutput,minRouteLabel,minRouteOutput;
    private JPanel cycleOutputPanel,cycleInputPanel,minRouteOutputPanel,confirmPanel;
    private JTextField skipInputField;
    private JButton okay,exit;
    //AntSystem model;
    
    public DataPanel(){
        //DISPLAY INFORMATION AREA
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //cycleOutputPanel=createCycleOutputPanel();
        
        cycleInputPanel=createCycleInputPanel();
        //minRouteOutputPanel=createMinRouteOutputPanel();
        confirmPanel=createConfirmCancelPanel();

        //add(cycleOutputPanel);
        //add(minRouteOutputPanel);
        add(cycleInputPanel);
        add(confirmPanel);
        
        setBorder(BorderFactory.createLineBorder(Color.black));  
        
    } 
    
    
    private JPanel createCycleOutputPanel(){
        JPanel cycleOutputPanel=new JPanel();
        JLabel cycleLabel=new JLabel("Cycle:");
        cycleOutput=new JLabel("1");
        cycleOutputPanel.add(cycleLabel);
        cycleOutputPanel.add(cycleOutput);
        return cycleOutputPanel;
    }
    
    
    private JPanel createCycleInputPanel(){
        //DISPLAY INPUT AREA
        JPanel cycleInputPanel=new JPanel();
        JLabel informationText=new JLabel("Specify the amount of cycles to skip:");
         
        //Input Area
        JPanel chooseCyclePanel=new JPanel();
        //JLabel skipInputLabel=new JLabel("cycles");
        skipInputField=new JTextField("0",3);
        //chooseCyclePanel.add(skipInputLabel);
        chooseCyclePanel.add(skipInputField);
        
        cycleInputPanel.add(informationText);
        cycleInputPanel.add(chooseCyclePanel);
        return cycleInputPanel;
    }
    
    private JPanel createMinRouteOutputPanel(){
        //MIN ROUTE OUTPPUT PANEL
        JPanel minRouteOutputPanel=new JPanel();
        minRouteLabel=new JLabel("Minimum Route Length:");
        minRouteOutput=new JLabel("0");
        minRouteLabel.setFont(new Font("Bauhaus 93",Font.BOLD,20));
        minRouteOutput.setFont(new Font("Bauhaus 93",Font.BOLD,20));
        
        minRouteLabel.setForeground(Color.cyan);
        minRouteOutput.setForeground(Color.cyan);
        
        
        minRouteOutputPanel.add(minRouteLabel);
        minRouteOutputPanel.add(minRouteOutput);
        return minRouteOutputPanel;
    }
    
    private JPanel createConfirmCancelPanel(){
        //Confirm Cancel Button Area
        JPanel confirmPanel=new JPanel();
        okay=new JButton("Animate");
        exit=new JButton("Exit");
        confirmPanel.add(okay);
        confirmPanel.add(exit);
        return confirmPanel;
    }
            
    
    public void addAnimateButtonListener(ActionListener listener){
        System.out.println("Animate Listener added.");
        okay.addActionListener(listener);
    }
    
    public void addCancelButtonListener(ActionListener listener){
        exit.addActionListener(listener);
    }
    
    
    public int getSkipCyclesInput(){
        return Integer.parseInt(skipInputField.getText());
    }
    
    public JButton getAnimateButton(){
        return okay;
    }
    
    public static void main(String[] args){
        DataPanel wtf=new DataPanel();
        
    }
}
