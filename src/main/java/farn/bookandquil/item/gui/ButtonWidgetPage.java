package farn.bookandquil.item.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.opengl.GL11;

class ButtonWidgetPage extends ButtonWidget {
	private final boolean nextPage;

	public ButtonWidgetPage(int var1, int var2, int var3, boolean var4) {
		super(var1, var2, var3, 23, 13, "");
		this.nextPage = var4;
	}

	public void render(Minecraft var1, int var2, int var3) {
		if(this.visible) {
			boolean var4 = var2 >= this.x && var3 >= this.y && var2 < this.x + this.width && var3 < this.y + this.height;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			var1.textureManager.bindTexture(var1.textureManager.getTextureId("/gui/book.png"));
			int var5 = 0;
			int var6 = 192;
			if(var4) {
				var5 += 23;
			}

			if(!this.nextPage) {
				var6 += 13;
			}

			this.drawTexture(this.x, this.y, var5, var6, 23, 13);
		}
	}
}
