package farn.bookandquil.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.opengl.GL11;

public class PageButton extends ButtonWidget {
	private final boolean nextButton;
	private boolean altTexture;

	public PageButton(int id, int x, int y, boolean next) {
		super(id, x, y, 23, 13, "");
		this.nextButton = next;
	}

	public void render(Minecraft mc, int mouseX, int mouseY) {
		if(this.visible) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.textureManager.bindTexture(mc.textureManager.getTextureId("/assets/bookandquill/gui/title_book.png"));
			int u = 0;
			int v = 192;
			if(mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
				u += 23;
			} else if(this.altTexture) {
				u += 46;
			}

			if(!this.nextButton) {
				v += 13;
			}

			this.drawTexture(this.x, this.y, u, v, 23, 13);
		}
	}

	public void setAltTexture(boolean altTexture) {
		this.altTexture = altTexture;
	}
}
