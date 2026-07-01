package farn.bookandquill.gui;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public abstract class GuiBookClassic extends GuiBook {
    protected GuiBookClassic(EntityPlayer player, ItemStack book, boolean writable) {
        super(player, book, writable);
    }

    /*

    @Override
    public boolean singlePage() {
        return true;
    }

    @Override
    public void renderBookCover() {
        this.drawBook();
        String title = this.getBookData().getString("title");
        if(title.isEmpty())
            title = MainUtil.translate("book.no.title");

        String author = this.getBookData().getString("author");
        if(author.isEmpty())
            author = MainUtil.translate("book.unknown.author");

        int titleWidth = this.textRenderer.getWidth(title);
        this.textRenderer.draw(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, 0);
        String authorFormat = MainUtil.translate("book.byAuthor", author);
        int authorWidth = this.textRenderer.getWidth(authorFormat);
        this.textRenderer.draw(authorFormat, this.bookTitleCenterX + 36 + (116 - authorWidth) / 2, 60, 0);
    }

    @Override
    public void renderSigning() {
        this.drawBook();
        String title = this.title;

        if(this.writable) {
            title += this.underscore;
        }

        String editTitle = MainUtil.translate("book.editTitle");
        int newTitleWidth = this.textRenderer.getWidth(editTitle);
        this.textRenderer.draw(editTitle, this.bookTitleCenterX + 36 + (116 - newTitleWidth) / 2, 34, 0);
        int titleWidth = this.textRenderer.getWidth(title);
        this.textRenderer.draw(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, 0);
        String author = String.format(MainUtil.translate("book.byAuthor"), this.player.name);
        int authorWidth = this.textRenderer.getWidth(author);
        this.textRenderer.draw(author, this.bookTitleCenterX + 36 + (116 - authorWidth) / 2, 60, 0);
        String warningFinal = MainUtil.translate("book.finalizeWarning");
        this.textRenderer.drawSplit(warningFinal, this.bookTitleCenterX + 36, 82, 116, 0);
    }

    @Override
    public void renderBook() {
        this.drawBook();
        String pageIndicator = String.format(MainUtil.translate("book.pageIndicator"), this.currentPage + 1, this.totalPages);
        String content = getContent(PageFocus.FIRST);
        if(this.writable) {
            content += this.underscore;
        }

        int pageIndiWidth = this.textRenderer.getWidth(pageIndicator);
        this.textRenderer.draw(pageIndicator, this.bookTitleCenterX - pageIndiWidth + this.titleImageWidth - 44, 18, 0);
        this.textRenderer.drawSplit(content, this.bookTitleCenterX + 36, 34, 116, 0);
    }

    public void drawBook() {
        String tex = "/assets/bookandquill/gui/legacy_book.png";
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId(tex));
        this.drawTexture(this.bookTitleCenterX, 2, 0, 0, this.titleImageWidth, this.imageHeight);
    }*/
}