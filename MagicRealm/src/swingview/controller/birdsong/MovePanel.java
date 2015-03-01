package swingview.controller.birdsong;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import view.controller.ViewController;
import model.enums.TileName;

@SuppressWarnings("serial")
public class MovePanel extends JPanel implements ActionListener {
	private JComboBox<TileName> tiles;
	private JComboBox<Integer> clearings;
	private ViewController controller;
	private Map<TileName, List<Integer>> tileClearings;

	public MovePanel(ViewController controller, int i,
			Map<TileName, List<Integer>> tileClrs) {
		this.controller = controller;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel lblMovePhase = new JLabel("Move Phase: " + i);
		JLabel lblMoveTo = new JLabel("Move To: ");
		JPanel tilePanel = new JPanel();
		tilePanel.setLayout(new BoxLayout(tilePanel, BoxLayout.X_AXIS));
		JLabel lblTile = new JLabel("Tile: ");
		JLabel lblClearing = new JLabel("Clearing: ");
		tilePanel.add(lblTile);
		tileClearings = tileClrs;
		tiles = new JComboBox<TileName>((TileName[]) tileClearings.keySet()
				.toArray());
		tilePanel.add(tiles);
		clearings = new JComboBox<Integer>();
		tiles.addActionListener(this);
		clearings.addActionListener(this);
		setClearings();

		JPanel clearingsPanel = new JPanel();
		clearingsPanel
				.setLayout(new BoxLayout(clearingsPanel, BoxLayout.X_AXIS));
		clearingsPanel.add(lblClearing);
		clearingsPanel.add(clearings);

		add(lblMovePhase);
		add(lblMoveTo);
		add(tilePanel);
		add(clearingsPanel);
	}

	private void setClearings() {
		TileName selectedTile = getSelectedTile();
		List<Integer> clearingsArray = tileClearings.get(selectedTile);
		clearings.setModel(new JComboBox<Integer>((Integer[]) clearingsArray
				.toArray()).getModel());
	}

	private void changeFocusTile() {
		controller.focusOnBoard(getSelectedTile());
	}

	private void changeFocusClearing() {
		controller.focusOnBoard(getSelectedTile(), getSelectedClearing());
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == tiles) {
			setClearings();
			changeFocusTile();
		} else if (ae.getSource() == clearings) {
			changeFocusClearing();
		}
	}

	public TileName getSelectedTile() {
		return (TileName) tiles.getSelectedItem();
	}

	public Integer getSelectedClearing() {
		return (Integer) clearings.getSelectedItem();
	}
}
