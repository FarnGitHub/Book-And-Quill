package farn.bookandquill.gui;

import farn.bookandquill.util.PageFocus;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Tessellator;

public class GuiBookDouble extends GuiBook {

    public GuiBookDouble(EntityPlayer player, ItemStack book, boolean writable) {
        super(player, book, writable);
    }

    @Override
    public boolean singlePage() {
        return false;
    }

    @Override
    public void renderBookCover() {
        this.drawBookCover();
        String title = this.getBookData().getString("title");
        if(title.isEmpty()) {
            title = "No Title";
        }
        String author = this.getBookData().getString("author");
        if(author.isEmpty()) {
            author = "Unknown Author";
        }

        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, -1);
        String authorFormat = String.format("by %1$s", author);
        int authorWidth = this.fontRenderer.getStringWidth(authorFormat);
        this.fontRenderer.drawString(authorFormat, this.bookTitleCenterX + 36 + (116 - authorWidth) / 2, 60, -1);
    }

    @Override
    public void renderSigning() {
        this.drawBookCover();
        String title = this.title;

        if(this.writable)
            title += this.underscore;

        String editTitle = "Enter Book Title:";
        int newTitleWidth = this.fontRenderer.getStringWidth(editTitle);
        this.fontRenderer.drawString(editTitle, this.bookTitleCenterX + 36 + (116 - newTitleWidth) / 2, 34, -1);
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, -1);
        String author = String.format("by %1$s", this.player.username);
        int authorWidth = this.fontRenderer.getStringWidth(author);
        this.fontRenderer.drawString(author, this.bookTitleCenterX + 36 + (116 - authorWidth) / 2, 60, -1);
        String warningFinal = "Note! When you sign the book, it will no longer be editable.";
        this.fontRenderer.func_27278_a(warningFinal, this.bookTitleCenterX + 36, 82, 116, -1);
    }

    @Override
    public void renderBook() {
        this.drawDoubleBook();
        int total = getTotalDisplay();
        String pageIndicator1 = String.format("Page %1$s of %2$s", this.currentPage + 1, total);
        String pageIndicator2 = String.format("Page %1$s of %2$s", this.currentPage + 2, total);
        String content1 = this.getContent(PageFocus.FIRST);
        String content2 = this.getContent(PageFocus.SECOND);

        if(this.writable) {
            if(pageFocus == PageFocus.FIRST)
                content1 = content1 + this.underscore;
            else if(pageFocus == PageFocus.SECOND)
                content2 = content2 + this.underscore;
        }

        int indicatorWidth = this.fontRenderer.getStringWidth(pageIndicator2);
        this.fontRenderer.drawString(pageIndicator1, this.bookDoubleCenterX + 44, 18, 0);
        this.fontRenderer.func_27278_a(content1, this.bookDoubleCenterX + 42, 34, 116, 0);
        this.fontRenderer.drawString(pageIndicator2, this.bookDoubleCenterX - indicatorWidth + this.bookImageWidth - 44, 18, 0);
        this.fontRenderer.func_27278_a(content2, this.bookDoubleCenterX + this.bookImageWidth - 156, 34, 116, 0);
    }

    public void drawBookCover() {
        String tex = "/assets/bookandquill/gui/title_book.png";
        this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture(tex));
        this.drawTexturedModalRect(this.bookTitleCenterX, 2, 0, 0, this.titleImageWidth, this.imageHeight);
    }

    public void drawDoubleBook() {
        this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/assets/bookandquill/gui/double_book.png"));
        float xNudge = 0.0029296875F;
        float yNudge = 0.00520833333F;
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(this.bookDoubleCenterX, 2 + this.imageHeight, this.zLevel, 0.0F, (float)(this.imageHeight) * yNudge);
        tess.addVertexWithUV(this.bookDoubleCenterX + this.bookImageWidth, 2 + this.imageHeight, this.zLevel, (float)(this.bookImageWidth) * xNudge, (float)(this.imageHeight) * yNudge);
        tess.addVertexWithUV(this.bookDoubleCenterX + this.bookImageWidth, 2, this.zLevel, (float)(this.bookImageWidth) * xNudge, 0.0F);
        tess.addVertexWithUV(this.bookDoubleCenterX, 2, this.zLevel, 0.0F, 0.0F);
        tess.draw();
    }
}