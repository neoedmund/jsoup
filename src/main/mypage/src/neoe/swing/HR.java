package neoe.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.Transient;

import javax.swing.JComponent;

public class HR extends JComponent {
 
	private static final long serialVersionUID = -7329001139970289607L;
	Component parent;

	public HR(Component panel) {
		this.parent = panel;
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawLine(0, 1, parent.getWidth(), 1);
	}

	@Override
	@Transient
	public Dimension getPreferredSize() {
		return new Dimension(parent.getWidth(), 3);
	}

	@Override
	@Transient
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

}