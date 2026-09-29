package spiel1;

import java.awt.Color;

//Abschnitt 1: Fenster und Zeichenfläche erzeugen und anzeigen
//Abschnitt 2: Menüleiste mit Einträgen erzeugen (noch ohne Funktion)
//Abschnitt 3: ActionListener für den Menüeintrag registrieren
//Abschnitt 4: WindowListener für's Spielfenster registrieren
//Abschnitt 5: Ball anzeigen und bewegen

import java.awt.Dimension;  //Abschnitt 1
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;  //Abschnitt 1
import javax.swing.JPanel;	//Abschnitt 1
import javax.swing.Timer;
import javax.swing.JMenuBar;	//Abschnitt 2
import javax.swing.JMenu;		//Abschnitt 2
import javax.swing.JMenuItem;	//Abschnitt 2
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;	//Abschnitt 3
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionEvent;		//Abschnitt 3

import java.awt.event.WindowAdapter;	//Abschnitt 4
import java.awt.event.WindowEvent;		//Abschnitt 4
import java.awt.geom.Ellipse2D;

public class SpielFenster extends JFrame{ //Durch Ableiten von JFrame werden z.B.Fenstereigenschaften geerbt
    //Darüber hinaus ist JFrame ein Container für andere Objekte (JPanel, Menu,..)

    
	private static final long serialVersionUID = 1L;//gelber fehler fix
	
	
	private Spielfeld spielfeld;

    public SpielFenster() {

        //Spielfeld erzeugen
        spielfeld = new Spielfeld();

        registerComponentListener();	// für Bildschirmgroeße ändert sich

        add(spielfeld);  //Hinzufügen des Spielfeldes zum SpielFenster ; (add() erben alle von Container)
        pack();  //Ideale Größe berechnen

        this.setTitle("Fliegender Ball");
        this.setLocation(0,0); //Linke obere Fensterecke festlegen
        this.setResizable(true);
        this.setVisible(true);


        repaint();
    }
       
    

    private void registerComponentListener() {
    	addComponentListener(new ComponentAdapter() {
        	@Override
        	public void componentResized(ComponentEvent e) {
        		Spielfeld.screenWidth = getWidth();
        		Spielfeld.screenHeight = getHeight();
                // hier auf Bildschirmgrößenänderung reagieren
            }
        });		
	}
}
