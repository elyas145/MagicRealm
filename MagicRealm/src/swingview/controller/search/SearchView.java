package swingview.controller.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.enums.CharacterType;

@SuppressWarnings("serial")
public class SearchView extends JPanel implements view.controller.search.SearchView {
	
	public SearchView(CharacterType character) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JLabel("Search View"));
		add(new JLabel(character.toString()));
		finished = false;
		JButton done = new JButton("Done");
		done.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				finishSearching();
			}
		});
		add(done);
	}

	@Override
	public boolean doneSearching() {
		return finished;
	}
	
	private synchronized void finishSearching() {
		finished = true;
		notify();
	}
	
	private boolean finished;

}
