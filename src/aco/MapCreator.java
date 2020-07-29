/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

/**
 *
 * @author Alex
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MapCreator extends JPanel {
    private ArrayList<City> cities;
    private Image background;
    
    private JButton okay,exit;
    private JTextField mapName;

    public MapCreator() {
        URL url=getClass().getResource("background3.jpg");
        background = new ImageIcon(url).getImage();
        Dimension bgsize = new Dimension(background.getWidth(null), background.getHeight(null));
        cities = new ArrayList<City>();
        setLayout(null);
        
        
        JPanel confirmPanel=createConfirmCancelPanel();
        add(confirmPanel);
        confirmPanel.setBounds(bgsize.width/2-200,bgsize.height-110,400,100);
        setPreferredSize(bgsize);
        setMinimumSize(bgsize);
        setMaximumSize(bgsize);
        setSize(bgsize);
        setVisible(true);     
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(background, 0,0, null);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        paintInfo(g2d);
        paintEdges(g2d);
        paintCities(g2d);
    }
    
    public void paintInfo(Graphics2D g2d){
        g2d.setColor(Color.black);
        g2d.setFont(new Font("Bauhaus 93",Font.ITALIC,25));
        g2d.drawString("Right-Click to add a node", (int)(background.getWidth(null)/2.7),(int)(background.getHeight(null)/15.0));
        g2d.drawString("Left-Click to undo a node", (int)(background.getWidth(null)/2.7),(int)(background.getHeight(null)/15.0)+25);
    }
    
    public void paintEdges(Graphics2D g2d){
        //****** Edges Paint ******
        g2d.setColor(Color.LIGHT_GRAY);
        
        for(int i=0;i<cities.size();i++){
            for(int j=cities.size()-1;j>i;j--){
                g2d.drawLine((int)(cities.get(i).getX()),(int)(cities.get(i).getY()),(int)(cities.get(j).getX()),(int) (cities.get(j).getY()));
            }
        }
    }
    
    public void paintCities(Graphics2D g2d){
        g2d.setColor(Color.black);
        for (City city : cities) {
            g2d.fill(new Ellipse2D.Double(city.getX(), city.getY(), 12, 12));
        }
    }
    
    private JPanel createConfirmCancelPanel(){
        //Confirm Cancel Button Area
        JPanel confirmPanel=new JPanel();
        JPanel textOutputArea=new JPanel();
        JPanel saveExitArea=new JPanel();
        JPanel textInputArea=new JPanel();
        confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.Y_AXIS));
        
        
        JLabel text=new JLabel("Save map?");
        textOutputArea.add(text);
        
        JLabel text2=new JLabel("Enter map name:");
        mapName=new JTextField(10);
        textInputArea.add(text2);
        textInputArea.add(mapName);
        
        okay=new JButton("Save");
        exit=new JButton("Exit");
        saveExitArea.add(okay);
        saveExitArea.add(exit);
        
        
        
        confirmPanel.add(textOutputArea);
        confirmPanel.add(textInputArea);
        confirmPanel.add(saveExitArea);
        
        confirmPanel.setBorder(BorderFactory.createLineBorder(Color.black));  

        
        return confirmPanel;
    }
    
    public void addClickListener(MouseListener listener){
        addMouseListener(listener);
    }

    public void addSaveListener(ActionListener listener){
        okay.addActionListener(listener);
    }
    
    public void addExitListener(ActionListener listener){
        exit.addActionListener(listener);
    }
    public String getFileName(){
        return mapName.getText();
    }
    
    public JButton getExitButton(){
        return exit;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.add(new MapCreator());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 400);
                frame.setVisible(true);
                frame.pack();
            }
        });
    }

    public ArrayList<City> getCities() {
        return cities;
    }


    public void closeWindow(JFrame mapCreatorFrame) {
        mapCreatorFrame.dispose();
        
    }

}