package neoe;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import neoe.swing.HR;
import neoe.swing.SimpleLayout;

public class MyPage {

	private Stack<JPanel> ps;
	Stack<SimpleLayout> ss;
	Stack<Font> fs;

	Document doc;
	private String url;

	public MyPage(String url) throws Exception {
		ps = new Stack<JPanel>();
		ss = new Stack<SimpleLayout>();
		fs = new Stack<Font>();
		addPanel();
		doc = Jsoup.connect(url).get();
		this.url = url;
	}

	private void addPanel() {
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEtchedBorder());
		SimpleLayout s = new SimpleLayout(p);
		if (ss.size() > 0) {
			ss.peek().add(p);
		}

		ps.add(p);
		ss.add(s);
		fs.add(new Font("simsun", Font.PLAIN, 16));

	}

	public void visit(Node ele, boolean inline) throws Exception {

		if (ele instanceof TextNode) {
			String txt = ((TextNode) ele).getWholeText().trim();
			if (!txt.isEmpty()) {
				if (!inline && (txt.contains("\n") || txt.length() > 20)) {
					txt = wrapLine(txt, 80);
					// System.out.println("output:" + txt);
					// EditorPanel ep;
					// add(ep = new EditorPanel());
					// ep.page.pageData.setText(txt);
					// int h = ep.page.pageData.lines.size() * 16;
					// h = Math.min(600, Math.max(50, h));
					// ep.setPreferredSize(new Dimension(800, h));
					// newline();
					String[] ss = txt.split("\\n");
					for (String s : ss) {
						JLabel jl;
						add(jl = new JLabel(s));
						jl.setFont(fs.peek());
						newline();
					}
				} else {
					JLabel jl;
					add(jl = new JLabel(txt));
					jl.setFont(fs.peek());
				}
			}
		} else {
			String name = ele.nodeName();
			if ("br".equals(name)) {
				newline();
			} else if ("hr".equals(name)) {
				newline();
				add(new HR(ps.peek()));
				newline();
			} else if ("h1".equals(name)) {
				newline();
				addPanel();
				fs.push(fs.pop().deriveFont(Font.BOLD, 16 * 5));
				visitChildren(ele);
				newline();
				popPanel();
				newline();
			} else if ("h2".equals(name)) {
				newline();
				addPanel();
				fs.push(fs.pop().deriveFont(Font.BOLD, 16 * 4));
				visitChildren(ele);
				newline();
				popPanel();
				newline();
			} else if ("h3".equals(name)) {
				newline();
				addPanel();
				fs.push(fs.pop().deriveFont(Font.BOLD, 16 * 3));
				visitChildren(ele);
				newline();
				popPanel();
				newline();
			} else if ("h4".equals(name)) {
				newline();
				addPanel();
				fs.push(fs.pop().deriveFont(Font.BOLD, 16 * 2));
				visitChildren(ele);
				newline();
				popPanel();
				newline();
			} else if ("h5".equals(name)) {
				newline();
				addPanel();
				fs.push(fs.pop().deriveFont(Font.BOLD, 16 * 1));
				visitChildren(ele);
				newline();
				popPanel();
				newline();
			} else if ("i".equals(name)) {
				fs.push(fs.pop().deriveFont(Font.ITALIC));
			} else if ("b".equals(name)) {
				fs.push(fs.pop().deriveFont(Font.BOLD));
			} else if ("a".equals(name)) {
				JButton jb;
				addPanel();
				add(jb = new JButton("A"));
				final String href = ele.attr("href");
				jb.setToolTipText(href);
				jb.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						openNewPage(href);
					}
				});
				visitChildren(ele, true);
				newline();
				popPanel();
				newline();
			} else {
				newline();
				addPanel();
				if (name.equals("div") || name.equals("tr")) {
					visitChildren(ele);
					newline();
					popPanel();
					newline();
				} else if (name.equals("span") || name.equals("td")) {
					visitChildren(ele);
					newline();
					popPanel();
				} else {
					add(new JLabel("<" + name + ">"));
					visitChildren(ele);
					newline();
					popPanel();
					newline();
				}

			}
		}

	}

	protected void openNewPage(String href) {
		final String url2 = getNewUrl(url, href);
		System.out.println("open: [" + url2 + "]");
		try {
			new MyPage(url2).show();
		} catch (UnsupportedMimeTypeException ex1) {
			new Thread() {
				public void run() {
					try {
						download(url2);
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(ps.get(0), e1);
					}
				}
			}.start();

		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(ps.get(0), e1);
		}

	}

	protected void download(String url2) throws Exception {
		URL website = new URL(url2);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		JFileChooser chooser = new JFileChooser();
		int p1 = url2.lastIndexOf("/");
		if (p1 < 0)
			p1 = -1;
		String fn = url2.substring(p1 + 1).replace('?', '_').replace('&', '_').replace(':', '_');
		chooser.setSelectedFile(new File(fn));
		int retrival = chooser.showSaveDialog(null);
		if (retrival == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();

			FileOutputStream fos = new FileOutputStream(f);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			JOptionPane.showMessageDialog(ps.get(0),
					String.format("downloaded %s to\n %s\n", url2, f.getAbsolutePath()));
		}

	}

	private String getNewUrl(String url, String href) {
		String url2;
		if (href.indexOf("://") > 0) {
			url2 = href;
		} else {
			int p1 = url.indexOf("?");
			if (p1 > 0) {
				url2 = url.substring(0, p1);
			} else {
				url2 = url;
			}
			if (href.startsWith("#") || href.startsWith("?")) {
				url2 = url2 + href;
			} else {
				int p2 = url2.lastIndexOf("/");
				int p3 = url2.indexOf("://");
				if (p2 == p3 + 2) {
					if (href.startsWith("/")) {
						url2 = url2 + href;
					} else {
						url2 = url2 + "/" + href;
					}

				} else {
					int p4 = url2.indexOf("/", p3 + 3);
					if (href.startsWith("/")) {
						url2 = url2.substring(0, p4) + href;
					} else {
						url2 = url2.substring(0, p4 + 1) + href;
					}

				}
			}

		}
		return url2;
	}

	private void visitChildren(Node ele) throws Exception {
		visitChildren(ele, false);
	}

	private String wrapLine(String txt, int max) {
		StringBuilder sb = new StringBuilder();
		int x = 0;
		int add = 0;
		for (char c : txt.toCharArray()) {
			sb.append(c);
			x++;
			if (x >= max && Character.isWhitespace(c)) {
				x = 0;
				sb.append('\n');
				add++;
			}
		}
		if (add == 0)
			return txt;
		return sb.toString();
	}

	private void visitChildren(Node ele, boolean inline) throws Exception {
		if (ele.childNodeSize() > 0) {
			for (Node node : ele.childNodes()) {
				visit(node, inline);
			}
		}

	}

	private void popPanel() {
		newline();
		ss.pop();
		ps.pop();
		fs.pop();
	}

	private void newline() {
		ss.peek().newline();
	}

	private void add(JComponent comp) {
		ss.peek().add(comp);
	}

	public void show() throws Exception {
		visit(doc.body(), false);
		for (int i = 0; i < ss.size(); i++)
			ss.get(i).newline();
		JFrame f = new JFrame(url);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel p = new JPanel(new BorderLayout());
		final JTextField jf = new JTextField();
		p.add(jf, BorderLayout.NORTH);
		jf.setText(url);
		jf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					openNewPage(jf.getText().trim());
				}
			}
		});
		p.add(new JScrollPane(ps.get(0)), BorderLayout.CENTER);
		f.getContentPane().add(p);
		f.pack();
		f.setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		new MyPage("http://jsoup.org/").show();
	}

}
