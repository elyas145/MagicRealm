package model.board.interfaces;

import model.board.enums.TileType;

public interface HexTileInterface
{
  
  /**
   * @return type of hex tile
   */
  TileType getType();
  
  /**
   * @return type of hex tile
   */
  boolean isEnchanted();
  
  /**
   * @return tile column on the board
   */
  int getBoardColumn();
  
  /**
   * @return tile row on the board
   */
  int getBoardRow();
  
  /**
   * @return rotation of the tile
   * 0 for up, 1 for 60deg clockwise, 3 for down
   */
  int getRotation();
  
}
