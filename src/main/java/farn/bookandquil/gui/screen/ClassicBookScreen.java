package farn.bookandquil.gui.screen;

import farn.bookandquil.util.MainUtil;
import farn.bookandquil.util.PageFocus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ClassicBookScreen extends BookScreen {

    public ClassicBookScreen(PlayerEntity player, ItemStack book, boolean writable) {
        super(player, book, writable);
    }

    @Override
    public int pageDisplayInScreen() {
        return 1;
    }

    @Override
    public void renderBookCover() {
        this.drawBook();
        int contentXStart = getContentXStart(PageFocus.UNFOCUS);
        int contentYStart = getContentYStart(PageFocus.UNFOCUS);
        int contentWidth = getContentWidth(PageFocus.UNFOCUS);
        String title = this.getBookData().getString("title");
        if(title.isEmpty())
            title = MainUtil.translate("book.no.title");

        String author = this.getBookData().getString("author");
        if(author.isEmpty())
            author = MainUtil.translate("book.unknown.author");

        int titleWidth = this.textRenderer.getWidth(title);
        this.textRenderer.draw(title, contentXStart + (contentWidth - titleWidth) / 2, contentYStart + 16, 0);
        String authorFormat = MainUtil.translate("book.byAuthor", author);
        int authorWidth = this.textRenderer.getWidth(authorFormat);
        this.textRenderer.draw(authorFormat, contentXStart + (contentWidth - authorWidth) / 2, contentYStart + 26, 0);
    }

    @Override
    public void renderSigning() {
        this.drawBook();
        int contentXStart = getContentXStart(PageFocus.UNFOCUS);
        int contentYStart = getContentYStart(PageFocus.UNFOCUS);
        int contentWidth = getContentWidth(PageFocus.UNFOCUS);

        String editTitle = MainUtil.translate("book.editTitle");
        int newTitleWidth = this.textRenderer.getWidth(editTitle);
        this.textRenderer.draw(editTitle, contentXStart + (contentWidth - newTitleWidth) / 2, contentYStart, 0);
        int titleWidth = this.textRenderer.getWidth(this.title);
        int titleStartX = contentXStart + (contentWidth - titleWidth) / 2;
        if(this.cursorFlag) {
            int titleEndX = titleStartX + titleWidth;
            this.fill(titleEndX - 1, contentYStart + 24, titleEndX, contentYStart + 16, 0xFF808080);
        }
        this.textRenderer.draw(this.title, titleStartX, contentYStart + 16, 0);
        String author = String.format(MainUtil.translate("book.byAuthor"), this.player.name);
        int authorWidth = this.textRenderer.getWidth(author);
        this.textRenderer.draw(author, contentXStart + (contentWidth - authorWidth) / 2, contentYStart + 26, 0);
        String warningFinal = MainUtil.translate("book.finalizeWarning");
        this.textRenderer.drawSplit(warningFinal, contentXStart, contentYStart + 48, contentWidth, 0);
    }

    @Override
    public void renderBook() {
        this.drawBook();
        String pageIndicator = String.format(MainUtil.translate("book.pageIndicator"), this.getCurrentPage() + 1, this.totalPages);
        String content = getContent(PageFocus.FIRST);

        int pageIndiWidth = this.textRenderer.getWidth(pageIndicator);
        this.textRenderer.draw(pageIndicator, this.bookTitleCenterX - pageIndiWidth + this.titleImageWidth - 44, this.bookCenterY + 18, 0);
        this.drawPageContent(content, getContentXStart(PageFocus.FIRST), PageFocus.FIRST);
    }

    public void drawBook() {
        String tex = "/assets/bookandquill/gui/legacy_book.png";
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId(tex));
        this.drawTexture(this.bookTitleCenterX, this.bookCenterY + 2, 0, 0, this.titleImageWidth, this.imageHeight);
    }
}