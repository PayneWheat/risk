package main.java.game;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import java.util.*;

public class Map extends JFrame {
	
	private JFrame f;
	private JPanel p;
	private JLabel lab = new JLabel();
	private JButton bl;
	
	public Map() {
		//Open the window and load the map
		f = new JFrame("RISK");
		f.setVisible(true);
		f.setSize(800,800);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = (JPanel)f.getContentPane();		
		lab.setIcon(new ImageIcon("res/riskmap.png"));
		panel.add(lab);
		panel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				// press the mouse and get the xy position
				int x = e.getX();
				int y = e.getY();
				System.out.println((x + " , " + y));
				}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			
		});
		f.setLocationRelativeTo(null);
		f.pack();
	}
		
}
