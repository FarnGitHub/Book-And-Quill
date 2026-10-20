package farn.bookandquil.gui.screen;

import farn.bookandquil.util.MainUtil;
import farn.bookandquil.util.PageFocus;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DoubleBookScreen extends BookScreen {

    public DoubleBookScreen(PlayerEntity player, ItemStack book, boolean writable) {
        super(player, book, writable);
    }

    @Override
    protected void handlePageClick(int x, int y) {
        int fX = (this.width - this.bookImageWidth) / 2;
        int sX = fX + (this.bookImageWidth / 2);
        int yMin = this.bookCenterY;
        int pageWidth = this.bookImageWidth / 2;
        int yMax = yMin + this.imageHeight;
        if(x >= fX && x < fX + pageWidth && y >= yMin && y < yMax) {
            this.pageFocus = PageFocus.FIRST;
        } else if(x >= sX && x < sX + pageWidth && y >= yMin && y < yMax) {
            this.pageFocus = PageFocus.SECOND;
        } else {
            this.pageFocus = PageFocus.UNFOCUS;
        }
    }

    @Override
    public int pageDisplayInScreen() {
        return 2;
    }

    @Override
    public void renderBookCover() {
        this.drawBookCover();
        int contentXStart = getContentXStart(PageFocus.UNFOCUS);
        int contentYStart = getContentYStart(PageFocus.UNFOCUS);
        int contentWidth = getContentWidth(PageFocus.UNFOCUS);
        String title = this.getBookData().getString("title");
        if(title.isEmpty()) {
            title = MainUtil.translate("book.no.title");
        }
        String author = this.getBookData().getString("author");
        if(author.isEmpty()) {
            author = MainUtil.translate("book.unknown.author");
        }

        int titleWidth = this.textRenderer.getWidth(title);
        this.textRenderer.draw(title, contentXStart + (contentWidth - titleWidth) / 2, contentYStart + 16, -1);
        String authorFormat = MainUtil.translate("book.byAuthor", author);
        int authorWidth = this.textRenderer.getWidth(authorFormat);
        this.textRenderer.draw(authorFormat, contentXStart + (contentWidth - authorWidth) / 2, contentYStart + 26, -1);
    }

    @Override
    public void renderSigning() {
        this.drawBookCover();
        int contentXStart = getContentXStart(PageFocus.UNFOCUS);
        int contentYStart = getContentYStart(PageFocus.UNFOCUS);
        int contentWidth = getContentWidth(PageFocus.UNFOCUS);

        String editTitle = MainUtil.translate("book.editTitle");
        int newTitleWidth = this.textRenderer.getWidth(editTitle);
        this.textRenderer.draw(editTitle, contentXStart + (contentWidth - newTitleWidth) / 2, contentYStart, -1);
        int titleWidth = this.textRenderer.getWidth(this.title);
        int titleStartX = contentXStart + (contentWidth - titleWidth) / 2;
        if(this.cursorFlag) {
            int titleEndX = titleStartX + titleWidth;
            this.fill(titleEndX - 1, contentYStart + 24, titleEndX, contentYStart + 16, 0xFF808080);
        }
        this.textRenderer.draw(this.title, titleStartX, contentYStart + 16, -1);
        String author = String.format(MainUtil.translate("book.byAuthor"), this.player.name);
        int authorWidth = this.textRenderer.getWidth(author);
        this.textRenderer.draw(author, contentXStart + (contentWidth - authorWidth) / 2, contentYStart + 26, -1);
        String warningFinal = MainUtil.translate("book.finalizeWarning");
        this.textRenderer.drawSplit(warningFinal, contentXStart, contentYStart + 48, contentWidth, -1);
    }

    @Override
    public void renderBook() {
        this.drawDoubleBook();
        int total = getTotalDisplay();
        String pageIndicator1 = MainUtil.translate("book.pageIndicator", this.getCurrentPage() + 1, total);
        String pageIndicator2 = MainUtil.translate("book.pageIndicator", this.getCurrentPage() + 2, total);
        String content1 = this.getContent(PageFocus.FIRST);
        String content2 = this.getContent(PageFocus.SECOND);

        int indicatorWidth = this.textRenderer.getWidth(pageIndicator2);
        this.textRenderer.draw(pageIndicator1, this.bookDoubleCenterX + 44, this.bookCenterY + 18, 0);
        this.drawPageContent(content1, getContentXStart(PageFocus.FIRST), PageFocus.FIRST);
        this.textRenderer.draw(pageIndicator2, this.bookDoubleCenterX - indicatorWidth + this.bookImageWidth - 44, this.bookCenterY + 18, 0);
        this.drawPageContent(content2, getContentXStart(PageFocus.SECOND), PageFocus.SECOND);
    }

    @Override
    public int getContentXStart(PageFocus focus) {
        if(focus == PageFocus.FIRST)
            return this.bookDoubleCenterX + 42;
        else if(focus == PageFocus.SECOND)
            return this.bookDoubleCenterX + this.bookImageWidth - 156;
        else
            return super.getContentXStart(focus);
    }

    public void drawBookCover() {
        String tex = "/assets/bookandquill/gui/title_book.png";
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId(tex));
        this.drawTexture(this.bookTitleCenterX, this.bookCenterY + 2, 0, 0, this.titleImageWidth, this.imageHeight);
    }

    public void drawDoubleBook() {
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId("/assets/bookandquill/gui/double_book.png"));
        float xNudge = 0.0029296875F;
        float yNudge = 0.00520833333F;
        int centerY = this.bookCenterY + 2;
        Tessellator tess = Tessellator.INSTANCE;
        tess.startQuads();
        tess.vertex(this.bookDoubleCenterX, centerY + this.imageHeight, this.zOffset, 0.0F, (float)(this.imageHeight) * yNudge);
        tess.vertex(this.bookDoubleCenterX + this.bookImageWidth, centerY + this.imageHeight, this.zOffset, (float)(this.bookImageWidth) * xNudge, (float)(this.imageHeight) * yNudge);
        tess.vertex(this.bookDoubleCenterX + this.bookImageWidth, centerY, this.zOffset, (float)(this.bookImageWidth) * xNudge, 0.0F);
        tess.vertex(this.bookDoubleCenterX, centerY, this.zOffset, 0.0F, 0.0F);
        tess.draw();
    }
}