/**@author Sebastian Wizert
  *
  * This application simulates flow of traffick on the regular cross road (junction) with lights in place.
  * Mixture of AWT and swing brings project to life in smooth animation (graphic representation).
  */


package com.sebastianwizert.tlc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Container; 
import java.awt.Insets; 
import java.awt.Dimension;


import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;



public class Tlc extends JPanel implements ActionListener {

	int lightsFrequency;
	int redCarSpeed ;

	// this refers to Action listener
	Timer tm = new Timer(5, this);

	// x - position , velX - speed - red car
	int x = 0, velX = 3, y = 250;
	// xi - position , velXi - speed - yellow car
	int xi = 600, velXi = 3, yi = 325;
	// xj - position, velY - speed - green truck
	int xj = 225, velY = 2, yj = 600;

	public static Tlc tlc = new Tlc();

	GridBagConstraints c = new GridBagConstraints();

	
	boolean wayThroughLR;
	boolean wayThroughUD;
	boolean autoSwitch = false;
	boolean colisionFromUD;
	boolean colisionsTrigger;
	boolean redCarCollision;

	JSpinner spinner1 = new JSpinner();
	JSpinner spinner2 = new JSpinner();


//***********************************************************************************************************************


	public Tlc() {

		setLayout(new GridBagLayout() );

		//Spinner1(init head) lights frequency
		spinner1.setModel(new SpinnerNumberModel(2000, 1, 10000, 1000) );
		c.weightx = 0.0;
		c.weighty = 0.5;
		c.gridx = 1;
		c.gridy = 1;
		add(spinner1, c);

		//Spinner2 red car speed
		spinner2.setModel(new SpinnerNumberModel(3, 1, 20, 1) );
		c.weightx = 0.0;
		c.weighty = 0.1;
		c.gridx = 3;
		c.gridy = 0;
		add(spinner2, c);
		

		//Button Green Up Down
		JButton b1 = new JButton("Up/Down Green");
		c.weightx = 0.1;
		c.weighty = 0.5;
		c.gridx = 0;
		c.gridy = 3;
		add(b1, c);
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wayThroughUD = true;
			} //inner class
		} );

		//Button Red Up Down
		JButton b2 = new JButton("Up/Down Red");
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.gridx = 0;
		c.gridy = 4;
		add(b2, c);
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wayThroughUD = false;
			} //inner class
		} );

		//Button Green Left Right
		JButton b3 = new JButton("Left/Right Green");
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.gridx = 4;
		c.gridy = 3;
		add(b3, c);
		b3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wayThroughLR = true;
			} //inner class
		} );

		JButton b4 = new JButton("Left/Right Red");
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.gridx = 4;
		c.gridy = 4;
		add(b4, c);
		b4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wayThroughLR = false;
			} //inner class
		} );

		JButton b5 = new JButton("Auto Control");
		c.weightx = 0.0;
		c.weighty = 0.1;
		c.gridx = 0;
		c.gridy = 1;
		add(b5, c);
		b5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				autoSwitch = !autoSwitch;
				if(autoSwitch == true) {

					auto();

				}
				else if(autoSwitch == false) {
					System.out.println("switched off" + autoSwitch);
				}

			} //inner class
		} );

		JButton b6 = new JButton("Colisions On/Off");
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.gridx = 0;
		c.gridy = 0;
		add(b6, c);
		b6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				colisionsTrigger = !colisionsTrigger;

			} //inner class
		} );

		//fake components to fill the grid
		JLabel l1 = new JLabel("Center");
		c.weightx = 5.0;
		c.weighty = 0.1;
		c.gridx = 2;
		c.gridy = 2;
		add(l1, c);

		JLabel l2 = new JLabel("mid right");
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.gridx = 3;
		c.gridy = 2;
		add(l2, c);
		

	}

//**********************************************************************************************************************

	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		

		//background
		ImageIcon i = new ImageIcon("Figure1.gif");
		i.paintIcon(this, g, 0, 0);

		


		//****************************paint red car left -> right****************************

	/*
		Color newRed = new Color(255, 1, 1, 200); //Red 
		g.setColor(newRed);
		g.fillRect(x, y, 60, 30);
		//paint wheels
		g.setColor(Color.BLACK);
		g.fillOval(x + 5,  y-5,  15, 5); //top left
		g.fillOval(x + 5,  y+28, 15, 5); //top right
		g.fillOval(x + 40, y-5,  15, 5); //bot left
		g.fillOval(x + 40, y+28, 15, 5); //bot right
		//paint windows
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(x + 35 , y+2, 5, 25);
	*/
		//replacing drawing with da bitmap :)

		if (redCarCollision == false) {

			ImageIcon redCar = new ImageIcon("viper.png");
			redCar.paintIcon(this, g, (x - 50), y);
		}
		else if (redCarCollision == true) {

			ImageIcon redCar2 = new ImageIcon("viperBW.png");
			redCar2.paintIcon(this, g, (x - 50), y);

		}

		//****************************paint yellow car left <- right :)**********************
	/*
		g.setColor(Color.YELLOW);
		g.fillRect(xi, yi, 70, 27);
		//paint wheels
		g.setColor(Color.BLACK);
		g.fillOval(xi + 5,  yi-5,  15, 5); //top left
		g.fillOval(xi + 5,  yi+28, 15, 5); //top right
		g.fillOval(xi + 52, yi-5,  15, 5); //bot left
		g.fillOval(xi + 52, yi+28, 15, 5); //bot right
		//paint windows
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(xi + 20 , yi+2, 5, 25);
	*/
		//replacing drawing with bitmap again
		ImageIcon yellowCar = new ImageIcon("porsche.png");
		yellowCar.paintIcon(this, g, xi, yi);


		//****************************paint green truck up -> down****************************
	/*
		g.setColor(Color.GREEN);
		//body/trailer
		g.fillRect(xj, yj, 40, 120); 
		//body/cabin
		g.setColor(Color.DARK_GRAY);
		g.fillRect(xj + 1, yj - 30, 38, 40); 
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(xj + 2, yj - 30, 36, 5);
	*/
		//replacing drawing with bitmap again
		ImageIcon truck = new ImageIcon("truck.png");
		truck.paintIcon(this, g, xj, (yj - 50) );

		//****************************up down light*******************************************
		if(wayThroughUD) {
			g2d.setColor(Color.GREEN);
			g2d.fillOval(15, 425, 100, 100);
		} 
		else {
			g2d.setColor(Color.RED);
			g2d.fillOval(15, 425, 100, 100);
		}

		//**************************left right light****************************
		if(wayThroughLR) {
			g.setColor(Color.GREEN);
			g.fillOval(480, 425, 100, 100);
		} 
		else {
			g.setColor(Color.RED);
			g.fillOval(480, 425, 100, 100);
		}
		
		//****************************Collision light*************************************************
		if(colisionFromUD) {
			g.setColor(Color.ORANGE);
			g.fillOval(20, 45, 100, 100);
		}
		else if(colisionsTrigger) {
			g.setColor(Color.CYAN);
			g.fillOval(20, 45, 100, 100);
		}
		else {
			g.setColor(Color.BLACK);
			g.fillOval(20, 45, 100, 100);
		}

		
		tm.start();

	}

//*********************************************************************************************************************

	public void actionPerformed(ActionEvent e) {


		//check settings for traffic lights frequency
		lightsFrequency = (Integer)spinner1.getValue();

		//monitor changes to speed settings of red car
		redCarSpeed = (Integer)spinner2.getValue();
		//System.out.println("Red car speed set to -> " + redCarSpeed + " velX -> " + velX);
		velX = redCarSpeed;


		//*******watch for traffic/ no collision mode for Left Right direction***********

			if(colisionsTrigger) {

				if( (yj < 400) && (yj > 110) ) {

					colisionFromUD = true;

				}
				else {

					colisionFromUD = false;

				}
			}

		//red car and truck collsion***************************************************************

		if ((x > xj-10) && (x < xj + 10)) {

			if ((yj < y+140) && (yj > y-140)) {

				redCarCollision = true;
			}
		}

		System.out.println("red car collision ->" + redCarCollision);

		//********************************Red car actions*********************************

		if(redCarCollision == false) {

			if( wayThroughLR == true && colisionFromUD == true ) {  
										//stop before colision even if green

				if( (x > 156) || (x < 154) ) {

					if(x > 650) 
						x = -50;
						x = x + velX;
						repaint();

				}

				else {
					x = 155;
				}
			
			} //if outter
			else if(wayThroughLR == true && colisionFromUD == false) {
										//run normaly if green and no colision
				if(x > 650) 
					x = -50;
					x = x + velX;
					repaint();
					
			}
			else if (wayThroughLR == false ) {
										//stop for red light
				if( (x > 156) || (x < 154) ) {

					if(x > 650) 
						x = -50;
						x = x + velX;
						repaint();

				}

				else {
					x = 155;
				}

			} 
				}
		else if (redCarCollision == true) {
		
		
			x=x;

			//redCarCollision = false;	
		}



		//*************************************Yellow car actions*********************************
		if(wayThroughLR == true && colisionFromUD == true) {

			if( (xi > 356) || (xi < 354) ) {
				
				if(xi < -50)
					xi = 650;
					xi = xi - velXi;
					repaint();
			}
			else {
				xi = 355;
			}

		} //if outter
		else if(wayThroughLR == true && colisionFromUD == false) {

			if(xi < -50)
				xi = 650;
				xi = xi - velXi;
				repaint();

		} //if outter
		else if (wayThroughLR == false ) {

			if( (xi > 356) || (xi < 354) ) {
				
				if(xi < -50)
					xi = 650;
					xi = xi - velXi;
					repaint();
			}
			else {
				xi = 355;
			}
		} //else if outter

		//****************************************green truck actions****************************************
		if (wayThroughUD == true) {

			if(yj < -100)
				yj = 610;
				yj = yj - velY;
				repaint();

		} //if outter
		else if (wayThroughUD == false) {

			if( (yj > 426) || (yj < 424) ) {

				if(yj < -100)
					yj = 610;
					yj = yj - velY;
					repaint();
			}
			else {
				yj = 425;
			}

		} //else if outter


	} //action performed

//***********************************************************************************************************************

	private static void createAndShowGUI() {

		JFrame frame = new JFrame();

		frame.setSize(600, 600);
		frame.add(tlc);
		frame.setTitle("Traffick Lights Control");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   		frame.setVisible(true);
		frame.setResizable(false);


	}

//*********************************************************************************************************************
//*********************************************************************************************************************

	public static void main ( String args[] ) {

		        javax.swing.SwingUtilities.invokeLater(new Runnable() {            
				public void run() { 
              				 createAndShowGUI();           
				}       
			}); 

	
	} //main

//*********************************************************************************************************************
//*********************************************************************************************************************

	private void auto() {
		
		SwingWorker<Boolean, Boolean> worker = new SwingWorker<Boolean, Boolean>() {
	
		@Override
		protected Boolean doInBackground() throws Exception {

			autoSwitch = true;
			wayThroughUD = true;
			wayThroughLR = false;

			
			while (autoSwitch == true) {

					

					Thread.sleep(lightsFrequency);

					System.out.println("autoSwitch set to ->" + autoSwitch);

					if(wayThroughUD && autoSwitch) {
						wayThroughUD = false;
						wayThroughLR = true;	
					}
					else if(wayThroughLR && autoSwitch) {
						wayThroughUD = true;
						wayThroughLR = false;
					}

					System.out.println("wayThroughLR set to ->" + wayThroughLR);
					System.out.println("wayThroughUD set to ->" + wayThroughUD);

			} //while

			return false;

		} //doInBackground method end

	/*******************************************under construction :)****************************************
		//update GUI from this method
		protected void done() {

			

				try {
					autoSwitch = get();
					//l1.setText("Completed :)" + wayThroughUD);

				}
				catch (InterruptedException e) { 
				}
				catch (ExecutionException ee) {
				}
			} //done
	*********************************************************************************************************/

		}; // Swing Worker

		worker.execute();

		System.out.println("even here? <------------------");

	} //auto method

} //class
