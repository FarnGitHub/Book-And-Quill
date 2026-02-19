package farn.bookandquil.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.opengl.GL11;

class PageButton extends ButtonWidget {
	private final boolean nextButton;

	public PageButton(int id, int x, int y, boolean next) {
		super(id, x, y, 23, 13, "");
		this.nextButton = next;
	}

	public void render(Minecraft mc, int mouseX, int mouseY) {
		if(this.visible) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.textureManager.bindTexture(mc.textureManager.getTextureId("/farn/bookandquill/gui/book.png"));
			int texWidth = 0;
			int textHeight = 192;
			if(mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
				texWidth += 23;
			}

			if(!this.nextButton) {
				textHeight += 13;
			}

			this.drawTexture(this.x, this.y, texWidth, textHeight, 23, 13);
		}
	}
}
