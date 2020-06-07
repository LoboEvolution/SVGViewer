package org.csiro.svg.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ClipAreaExample extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Canvas1 canvas;
	JButton button1, button2;

	public ClipAreaExample() {
		super("Clip Area");
		Container contentPane = getContentPane();
		canvas = new Canvas1();
		contentPane.add(canvas);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		button1 = new JButton("Clip1");
		button1.addActionListener(event -> {
			canvas.clip1 = true;
			canvas.clip2 = false;
			canvas.repaint();
		});
		button2 = new JButton("Clip2");
		button2.addActionListener(event -> {
			canvas.clip2 = true;
			canvas.repaint();
		});
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(button1);
		buttonGroup.add(button2);
		panel.add(button1);
		panel.add(button2);
		contentPane.add(BorderLayout.SOUTH, panel);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});
		setVisible(true);
	}

	public static void main(String arg[]) {
		new ClipAreaExample();
	}
}

class Canvas1 extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean clip1 = true;
	boolean clip2 = false;

	Canvas1() {
		setSize(450, 400);
		setBackground(Color.white);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		int w = getSize().width;
		int h = getSize().height;

		Ellipse2D e = new Ellipse2D.Float(0, 0, 200, 200);
		g2.setClip(e);

		g2.setColor(Color.yellow);
		g2.fillRect(0, 0, w, h);

		Rectangle r = new Rectangle(0, 0, 200, 100);
		g2.clip(r);

		g2.setColor(Color.green);
		g2.fillRect(0, 0, w, h);

	}
}
