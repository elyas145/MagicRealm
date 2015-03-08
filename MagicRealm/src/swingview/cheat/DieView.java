package swingview.cheat;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import client.ClientController;

public class DieView extends JFrame implements ActionListener {
	private int rollValue;
	private JTextField roll;
	private ClientController parent;
	private boolean finished = false;

	public DieView(ClientController parent) {
		this.parent = parent;
		setPreferredSize(new Dimension(200, 200));
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		roll = new JTextField();

		getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		JButton btn = new JButton("Ok");
		btn.addActionListener(this);
		getContentPane().add(roll);
		getContentPane().add(btn);
		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		try {
			rollValue = Integer.parseInt(roll.getText());
			if (rollValue > 0 && rollValue < 7) {
				setFinished();
			} else {
				throw new NumberFormatException("invalid number");
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Error",
					"Invalid roll. please enter a number between 1 and 6.",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void setFinished() {
		finished = true;
		synchronized (parent) {
			parent.notifyAll();
		}
	}

	public boolean isFinished() {
		return finished;
	}

	public int getRollValue() {
		return rollValue;
	}

	public void setRollValue(int rollValue) {
		this.rollValue = rollValue;
	}

}
