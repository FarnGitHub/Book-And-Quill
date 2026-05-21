package farn.bookandquill.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import org.lwjgl.opengl.GL11;

class GuiPageButton extends GuiButton {
	private final boolean next;

	public GuiPageButton(int id, int x, int y, boolean next) {
		super(id, x, y, 23, 13, "");
		this.next = next;
	}

	public void drawButton(Minecraft mc, int x, int y) {
		if(this.enabled2) {
			boolean mouseOver = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/farn/bookandquill/resource/bookGui.png"));
			int textureU = 0;
			int textureV = 192;
			if(mouseOver) {
				textureU += 23;
			}

			if(!this.next) {
				textureV += 13;
			}

			this.drawTexturedModalRect(this.xPosition, this.yPosition, textureU, textureV, 23, 13);
		}
	}
}
