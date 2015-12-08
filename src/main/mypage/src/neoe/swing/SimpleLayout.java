package neoe.swing;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class SimpleLayout {
	JPanel curr;
	JPanel p;
	private int child;

	public SimpleLayout(JPanel p) {
		this.p = p;
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		newCurrent();
	}

	public void add(JComponent co) {
		curr.add(co);
		child++;
	}

	void newCurrent() {
		curr = new JPanel();
		curr.setLayout(new BoxLayout(curr, BoxLayout.LINE_AXIS));
		child = 0;
	}

	public void newline() {
		if (child > 0) {
			p.add(curr);
			newCurrent();
		}
	}
}
