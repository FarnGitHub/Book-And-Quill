package farn.bookandquill.gui;

import farn.bookandquill.util.PageFocus;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class GuiBookClassic extends GuiBook {
    protected GuiBookClassic(EntityPlayer player, ItemStack book, boolean writable) {
        super(player, book, writable);
    }

    @Override
    public boolean singlePage() {
        return true;
    }

    @Override
    public void renderBookCover() {
        this.drawBook();
        String title = this.getBookData().getString("title");
        if(title.isEmpty())
            title = "No Title";

        String author = this.getBookData().getString("author");
        if(author.isEmpty())
            author = "Unknown Author";

        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, 0);
        String authorFormat = String.format("by %1$s", author);
        int authorWidth = this.fontRenderer.getStringWidth(authorFormat);
        this.fontRenderer.drawString(authorFormat, this.bookTitleCenterX + 36 + (116 - authorWidth) / 2, 60, 0);
    }

    @Override
    public void renderSigning() {
        this.drawBook();
        String title = this.title;

        if(this.writable) {
            title += this.underscore;
        }

        String editTitle = "Enter Book Title:";
        int newTitleWidth = this.fontRenderer.getStringWidth(editTitle);
        this.fontRenderer.drawString(editTitle, this.bookTitleCenterX + 36 + (116 - newTitleWidth) / 2, 34, 0);
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, 0);
        String author = String.format("by %1$s", this.player.username);
        int authorWidth = this.fontRenderer.getStringWidth(author);
        this.fontRenderer.drawString(author, this.bookTitleCenterX + 36 + (116 - authorWidth) / 2, 60, 0);
        String warningFinal = "Note! When you sign the book, it will no longer be editable.";
        this.fontRenderer.func_27278_a(warningFinal, this.bookTitleCenterX + 36, 82, 116, 0);
    }

    @Override
    public void renderBook() {
        this.drawBook();
        String pageIndicator = String.format("Page %1$s of %2$s", this.currentPage + 1, this.totalPages);
        String content = getContent(PageFocus.FIRST);
        if(this.writable) {
            content += this.underscore;
        }

        int pageIndiWidth = this.fontRenderer.getStringWidth(pageIndicator);
        this.fontRenderer.drawString(pageIndicator, this.bookTitleCenterX - pageIndiWidth + this.titleImageWidth - 44, 18, 0);
        this.fontRenderer.func_27278_a(content, this.bookTitleCenterX + 36, 34, 116, 0);
    }

    public void drawBook() {
        String tex = "/assets/bookandquill/gui/legacy_book.png";
        this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture(tex));
        this.drawTexturedModalRect(this.bookTitleCenterX, 2, 0, 0, this.titleImageWidth, this.imageHeight);
    }
}