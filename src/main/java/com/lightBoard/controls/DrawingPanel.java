/**
 * 
 */
package com.lightBoard.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;

import javax.swing.JPanel;

/**
 * @author Mohammad
 *
 */
public class DrawingPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private MasterControls mC;

	public DrawingPanel(MasterControls masterControls){ mC = masterControls; }
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
//		// TODO Auto-generated method stub
//		super.paintComponent(g);
//
//		Graphics2D g2 = (Graphics2D)g;
//		BasicStroke stroke = new BasicStroke(mC.getBrushSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
//		g2.setStroke(stroke);
//		// clear drawing panel
//		g2.setColor(mC.getBackColor());
//		g2.fillRect(0, 0, getWidth(), getHeight());
//
//		// set Points color
//		int red = mC.getPatternColor().getRed();
//		int green = mC.getPatternColor().getGreen();
//		int blue = mC.getPatternColor().getBlue();
//		int alpha = 255;
//
//		LinkedList<Point> buffer = mC.updateBuffer(this);
//		int bufferSize =  mC.getMaxBufferSize();
//		// draw desired points
//		int index;
//		for (index = 0; index < Math.min(bufferSize, buffer.size()); index++)
//		{
//			alpha = (int) ((bufferSize -index)*255.0/bufferSize);
//			g2.setColor(new Color(red, green, blue, alpha));
//			Point p = buffer.get(index);
//			g2.drawLine(p.x, p.y, p.x+1, p.y+1);
////			System.out.println("Point at " + p.toString());
//		}
//		while (buffer.size() > mC.getMaxBufferSize())
//			buffer.removeLast();
//
//		System.out.println(buffer.size());
	}
}
