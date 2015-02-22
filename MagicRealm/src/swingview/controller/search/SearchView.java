package swingview.controller.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import swingview.MainView;
import model.enums.CharacterType;
import model.enums.TableType;

@SuppressWarnings("serial")
public class SearchView extends JPanel implements
		view.controller.search.SearchView {

	public SearchView(MainView parent, CharacterType character) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JLabel("Search View"));
		add(new JLabel(character.toString()));
		finished = false;

		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));
		tablePanel.add(new JLabel("Select Table: "));
		tables = new JComboBox<TableType>(TableType.values());
		tablePanel.add(tables);
		add(tablePanel);
		JButton done = new JButton("Done");
		done.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectedTable = (TableType) tables.getSelectedItem();
				finishSearching();
			}
		});
		add(done);
		revalidate();
	}

	@Override
	public boolean doneSearching() {
		return finished;
	}

	private synchronized void finishSearching() {
		finished = true;
		notify();
	}

	public TableType getSelectedTable() {
		return selectedTable;
	}

	private boolean finished;
	private JComboBox<TableType> tables;
	private TableType selectedTable;

}
