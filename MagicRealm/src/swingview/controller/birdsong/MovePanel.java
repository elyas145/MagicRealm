package swingview.controller.birdsong;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.enums.TileName;
import controller.Controller;

@SuppressWarnings("serial")
public class MovePanel extends JPanel implements ActionListener {
	private JComboBox<TileName> tiles;
	private JComboBox<Integer> clearings;
	private Controller controller;

	public MovePanel(Controller controller, int i) {
		this.controller = controller;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel lblMovePhase = new JLabel("Move Phase: " + i);
		JLabel lblMoveTo = new JLabel("Move To: ");
		JPanel tilePanel = new JPanel();
		tilePanel.setLayout(new BoxLayout(tilePanel, BoxLayout.X_AXIS));
		JLabel lblTile = new JLabel("Tile: ");
		JLabel lblClearing = new JLabel("Clearing: ");
		tilePanel.add(lblTile);

		tiles = new JComboBox<TileName>((TileName[]) controller
				.getPossibleTiles().toArray());
		tilePanel.add(tiles);
		clearings = new JComboBox<Integer>();
		tiles.addActionListener(this);
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
		TileName selectedTile = (TileName) tiles.getSelectedItem();
		ArrayList<Integer> clearingsArray = controller
				.getPossibleClearings(selectedTile);
		clearings.setModel(new JComboBox<Integer>((Integer[]) clearingsArray
				.toArray()).getModel());
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		setClearings();
	}
	
	public TileName getSelectedTile(){
		return (TileName) tiles.getSelectedItem();
	}
	public Integer getSelectedClearing(){
		return (Integer) clearings.getSelectedItem();
	}
}
