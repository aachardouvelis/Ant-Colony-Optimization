/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aco;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 *
 * @author Alex
 */
public class StartMenu extends JPanel {
    private JButton createMap;
    private JButton loadACO;
    private JButton exit;
    public StartMenu(){
        System.out.println(System.getProperty("java.class.path"));
        

        
        
        
        
        /*final File f = new File(StartMenu.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        f.getParentFile();
        System.out.println(f.getAbsolutePath());
        System.out.println(f.getParentFile().getAbsolutePath());*/
        URL url=getClass().getResource("ant.png");
 
        url.getPath();
        //String relativeLoc=AntSystemController.convertStreamToString(StartMenu.class.getClassLoader().getResourceAsStream("/ant.png"));
        Image background = new ImageIcon(url).getImage();
        
        Image newImage = background.getScaledInstance(300, 300, Image.SCALE_DEFAULT);
        ImageIcon ic=new ImageIcon(newImage);
        
        JLabel thumb = new JLabel();
        thumb.setSize(300,300);
        thumb.setIcon(ic);
       
        JPanel welcomePanel=new JPanel();
        JLabel lab=new JLabel("Welcome to the ACO Simulation Program!");
        welcomePanel.add(lab);
        welcomePanel.add(thumb);
        
        welcomePanel.setSize(300,300);
        
        
        
        JPanel buttonPanel=new JPanel();
        
        
        
        Dimension buttonDim=new Dimension(500,1000);
        createMap=new JButton("Create Map");
        loadACO=new JButton("Load ACO");
        exit=new JButton("Exit");
        
        
        createMap.setMinimumSize(buttonDim);
        loadACO.setMinimumSize(buttonDim);
        exit.setMinimumSize(buttonDim);
        
        createMap.setMaximumSize(buttonDim);
        loadACO.setMaximumSize(buttonDim);
        exit.setMaximumSize(buttonDim);
        
        buttonPanel.add(loadACO);
        buttonPanel.add(createMap);
        buttonPanel.add(exit);
        
        //welcomePanel.setLayout(null);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(welcomePanel);
        add(buttonPanel);
        setVisible(true);
        
    }
    
    
    public static void main(String[] args){
        JFrame mainFrame=new JFrame("Menu");
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        StartMenu panel=new StartMenu();
        
        
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        mainFrame.getContentPane().add(panel);
        
        Dimension menuDim=mainFrame.getContentPane().getSize();
        mainFrame.setLocation(screenDim.width/2-menuDim.width, screenDim.height/2-menuDim.height);
        mainFrame.setVisible(true);
        mainFrame.pack();
        
        
        
        
    }
    public void addCreateMapListener(ActionListener listener){
        System.out.println("addCreateMap LIstener added");
        createMap.addActionListener(listener);
    }
    
    public void addLoadAcoListener(ActionListener listener){
        loadACO.addActionListener(listener);
    }
    
    public void addExitListener(ActionListener listener){
        exit.addActionListener(listener);
    }
    
}
