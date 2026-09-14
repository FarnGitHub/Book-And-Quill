package farn.bookandquill.gui;

import farn.bookandquill.util.PageFocus;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StringTranslate;

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
            title = StringTranslate.getInstance().translateKey("bookandquill.book.no.title");

        String author = this.getBookData().getString("author");
        if(author.isEmpty())
            author = StringTranslate.getInstance().translateKey("bookandquill.book.unknown.author");

        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, 0);
        String authorFormat = StringTranslate.getInstance().translateKeyFormat("bookandquill.book.byAuthor", author);
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

        String editTitle = StringTranslate.getInstance().translateKey("bookandquill.book.editTitle");
        int newTitleWidth = this.fontRenderer.getStringWidth(editTitle);
        this.fontRenderer.drawString(editTitle, this.bookTitleCenterX + 36 + (116 - newTitleWidth) / 2, 34, 0);
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, 0);
        String author = StringTranslate.getInstance().translateKeyFormat("bookandquill.book.byAuthor", this.player.username);
        int authorWidth = this.fontRenderer.getStringWidth(author);
        this.fontRenderer.drawString(author, this.bookTitleCenterX + 36 + (116 - authorWidth) / 2, 60, 0);
        String warningFinal = StringTranslate.getInstance().translateKey("bookandquill.book.finalizeWarning");
        this.fontRenderer.func_27278_a(warningFinal, this.bookTitleCenterX + 36, 82, 116, 0);
    }

    @Override
    public void renderBook() {
        this.drawBook();
        String pageIndicator = StringTranslate.getInstance().translateKeyFormat("bookandquill.book.pageIndicator", this.currentPage + 1, this.totalPages);
        String content = getContent(PageFocus.FIRST);

        int pageIndiWidth = this.fontRenderer.getStringWidth(pageIndicator);
        this.fontRenderer.drawString(pageIndicator, this.bookTitleCenterX - pageIndiWidth + this.titleImageWidth - 44, 18, 0);
        this.drawPageContent(content, this.bookTitleCenterX + 36, PageFocus.FIRST);
    }

    @Override
    public int getContentX(PageFocus focus) {
        return this.bookTitleCenterX + 36;
    }

    @Override
    public int getContentY(PageFocus focus) {
        return 34;
    }

    @Override
    public int getContentWidth(PageFocus focus) {
        return 116;
    }

    public void drawBook() {
        String tex = "/assets/bookandquill/gui/legacy_book.png";
        this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture(tex));
        this.drawTexturedModalRect(this.bookTitleCenterX, 2, 0, 0, this.titleImageWidth, this.imageHeight);
    }
}