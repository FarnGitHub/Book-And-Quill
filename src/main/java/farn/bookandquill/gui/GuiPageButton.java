package farn.bookandquill.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import org.lwjgl.opengl.GL11;

class GuiPageButton extends GuiButton {
	private final boolean next;
	private final int titleX;
	private final int altX;
	private boolean title;
	private final GuiBook bookScreen;

	public GuiPageButton(int id, int x, int y, boolean next, int altX, GuiBook screen) {
		super(id, x, y, 23, 13, "");
		this.next = next;
		this.titleX = x;
		this.altX = altX;
		this.bookScreen = screen;
	}

	public void drawButton(Minecraft mc, int x, int y) {
		if(this.enabled2) {
			boolean mouseOver = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/assets/bookandquill/gui/title_book.png"));
			int textureU = 0;
			int textureV = 192;
			if(mouseOver) {
				textureU += 23;
			} else if(!this.bookScreen.singlePage() && this.title) {
				textureU += 46;
			}

			if(!this.next) {
				textureV += 13;
			}

			this.drawTexturedModalRect(this.xPosition, this.yPosition, textureU, textureV, 23, 13);
		}
	}

	public void updatePosition(boolean titlePage) {
		this.title = titlePage;
		if(titlePage)
			this.xPosition = titleX;
		else
			this.xPosition = altX;
	}
}
