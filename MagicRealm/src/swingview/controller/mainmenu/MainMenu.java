package swingview.controller.mainmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import view.controller.game.GameView;
import view.controller.mainmenu.MainMenuView;
import view.controller.mainmenu.MenuItem;
import view.controller.mainmenu.MenuItemListener;

@SuppressWarnings("serial")
public class MainMenu extends JPanel implements MainMenuView {
	JButton btn = new JButton("click me.");
	public MainMenu() {		
		add(btn);
	}

	@Override
	public GameView enterGameView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void whenItemSelected(MenuItemListener listener) {
		this.listener = listener;
	}

	private MenuItemListener listener;

	@Override
	public void startMenu() {
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				listener.onItemSelect(MenuItem.START_GAME);
			}
		});
	}
}
