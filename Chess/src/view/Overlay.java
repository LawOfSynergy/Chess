package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import model.Location;

/**
 * a transparent overlay showing different types of moves.
 * @author kstimson
 *
 */
public enum Overlay {
	select(new Color(0x01, 0xdf, 0x01), 0.75f), move(new Color(0xff, 0xff, 0x00), 0.5f), attack(new Color(0xff, 0x00, 0x00), 0.75f), castle(new Color(0x00, 0x00, 0xff), 0.5f);
	
	private static final int OVERLAY_TYPE = BufferedImage.TYPE_INT_ARGB;
	private Color color;
	private float alpha;
	
	private Overlay(Color color, float alpha){
		this.color = color;
		this.alpha = alpha;
	}
	
	public void paint(Graphics g, Location loc, int cellSize){
		BufferedImage overlay = new BufferedImage(cellSize, cellSize, OVERLAY_TYPE);
		
		//set up overlay
		Graphics ovg = overlay.createGraphics();
		ovg.setColor(color);
		ovg.fillRect(0, 0, overlay.getWidth(), overlay.getHeight());
		ovg.dispose();
        
        float[] scales = { 1f, 1f, 1f, alpha };
        float[] offsets = new float[4];
        RescaleOp rop = new RescaleOp(scales, offsets, null);
        ((Graphics2D)g).drawImage(overlay, rop, loc.getX() * cellSize,  (GamePanel.DIVISOR - 1 - loc.getY()) * cellSize);
	}
}
