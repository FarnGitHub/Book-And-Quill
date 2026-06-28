package farn.bookandquil.gui;

import farn.bookandquil.util.MainUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.opengl.GL11;

class PageButton extends ButtonWidget {
	private final boolean nextButton;
	private final int titleX;
	private final int altX;
	private boolean title;

	public PageButton(int id, int x, int y, boolean next, int altX) {
		super(id, x, y, 23, 13, "");
		this.nextButton = next;
		this.titleX = x;
		this.altX = altX;
	}

	public void render(Minecraft mc, int mouseX, int mouseY) {
		if(this.visible) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.textureManager.bindTexture(mc.textureManager.getTextureId("/assets/bookandquill/gui/title_book.png"));
			int u = 0;
			int v = 192;
			if(mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
				u += 23;
			} else if(!MainUtil.classicBook() && this.title) {
				u += 46;
			}

			if(!this.nextButton) {
				v += 13;
			}

			this.drawTexture(this.x, this.y, u, v, 23, 13);
		}
	}

	public void updatePosition(boolean titlePage) {
		this.title = titlePage;
		if(titlePage)
			this.x = titleX;
		else
			this.x = altX;
	}
}
