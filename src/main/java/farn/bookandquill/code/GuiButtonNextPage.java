package farn.bookandquill.code;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import org.lwjgl.opengl.GL11;

class GuiButtonNextPage extends GuiButton {
	private final boolean nextPage;

	public GuiButtonNextPage(int var1, int var2, int var3, boolean var4) {
		super(var1, var2, var3, 23, 13, "");
		this.nextPage = var4;
	}

	public void drawButton(Minecraft var1, int var2, int var3) {
		if(this.enabled2) {
			boolean var4 = var2 >= this.xPosition && var3 >= this.yPosition && var2 < this.xPosition + this.width && var3 < this.yPosition + this.height;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			var1.renderEngine.bindTexture(var1.renderEngine.getTexture("/farn/bookandquill/resource/bookGui.png"));
			int var5 = 0;
			int var6 = 192;
			if(var4) {
				var5 += 23;
			}

			if(!this.nextPage) {
				var6 += 13;
			}

			this.drawTexturedModalRect(this.xPosition, this.yPosition, var5, var6, 23, 13);
		}
	}
}
