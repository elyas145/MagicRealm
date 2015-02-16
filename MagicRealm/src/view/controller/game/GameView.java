package view.controller.game;

import view.controller.birdsong.BirdsongView;
import view.controller.trade.TradeView;

public interface GameView {
	
	BirdsongView enterBirdsong();
	
	TradeView enterTrade();

}
