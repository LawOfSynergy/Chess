package model;

/**
 * Note that the program does not recognize 3-fold repetition or 20 consecutive turns without either a capture or a check as stalemate.
 * It only recognizes stalemate when there are no legal moves and the king is not in check.
 * @author kstimson
 *
 */
public enum GameState {
	check, checkmate, stalemate, ingame, nogame;
}
