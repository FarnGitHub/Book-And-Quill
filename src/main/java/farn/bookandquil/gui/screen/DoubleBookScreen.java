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
    public void pageClick(int x, int y) {
        int fX = (this.width - DOUBLE_IMAGE_WIDTH) / 2;
        int sX = fX + (DOUBLE_IMAGE_WIDTH / 2);
        int yMin = this.centerY;
        int pageWidth = DOUBLE_IMAGE_WIDTH / 2;
        int yMax = yMin + IMAGE_HEIGHT;
        if(x >= fX && x < fX + pageWidth && y >= yMin && y < yMax) {
            this.focus = PageFocus.FIRST;
        } else if(x >= sX && x < sX + pageWidth && y >= yMin && y < yMax) {
            this.focus = PageFocus.SECOND;
        } else {
            this.focus = PageFocus.UNFOCUS;
        }
    }

    @Override
    public int pageDisplayInScreen() {
        return 2;
    }

    @Override
    public int contentXStart(PageFocus focus) {
        if(focus == PageFocus.FIRST)
            return this.doubleCenterX + 42;
        else if(focus == PageFocus.SECOND)
            return this.doubleCenterX + DOUBLE_IMAGE_WIDTH - 156;
        else
            return this.centerX + 36;
    }

    @Override
    public int contentYStart(PageFocus focus) {
        return this.centerY + 34;
    }

    @Override
    public int contentWidth(PageFocus focus) {
        return 116;
    }

    @Override
    public void updateButton() {
        super.updateButton();
        boolean isTitlePage = this.signing || this.getCurrentPage() < 0;
        this.nextButton.setAltTexture(isTitlePage);
        this.previousButton.setAltTexture(isTitlePage);
        this.nextButton.x = isTitlePage ? this.centerX + 120 : this.doubleCenterX + 272;
        this.previousButton.x = isTitlePage ? this.centerX + 38 : this.doubleCenterX + 48;
    }

    @Override
    public void renderBookCover() {
        this.bindBookCoverTexture();
        int contentXStart = contentXStart(PageFocus.UNFOCUS);
        int contentYStart = contentYStart(PageFocus.UNFOCUS);
        int contentWidth = contentWidth(PageFocus.UNFOCUS);
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
        this.bindBookCoverTexture();
        int contentXStart = contentXStart(PageFocus.UNFOCUS);
        int contentYStart = contentYStart(PageFocus.UNFOCUS);
        int contentWidth = contentWidth(PageFocus.UNFOCUS);

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
        this.bindBookTexture();
        int total = getTotalDisplay();
        String pageIndicator1 = MainUtil.translate("book.pageIndicator", this.getCurrentPage() + 1, total);
        String pageIndicator2 = MainUtil.translate("book.pageIndicator", this.getCurrentPage() + 2, total);
        String content1 = this.getContent(PageFocus.FIRST);
        String content2 = this.getContent(PageFocus.SECOND);

        int indicatorWidth = this.textRenderer.getWidth(pageIndicator2);
        this.textRenderer.draw(pageIndicator1, this.doubleCenterX + 44, this.centerY + 18, 0);
        this.drawPageContent(content1, contentXStart(PageFocus.FIRST), PageFocus.FIRST);
        this.textRenderer.draw(pageIndicator2, this.doubleCenterX - indicatorWidth + DOUBLE_IMAGE_WIDTH - 44, this.centerY + 18, 0);
        this.drawPageContent(content2, contentXStart(PageFocus.SECOND), PageFocus.SECOND);
    }

    public void bindBookCoverTexture() {
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId("/assets/bookandquill/gui/title_book.png"));
        this.drawTexture(this.centerX, this.centerY + 2, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    public void bindBookTexture() {
        final int x = this.doubleCenterX;
        final int y = this.centerY + 2;
        final int u = 0;
        final int v = 0;
        final int width = DOUBLE_IMAGE_WIDTH;
        final int height = IMAGE_HEIGHT;
        float xNormal = 1F / width;
        float yNormal = 1F / height;

        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId("/assets/bookandquill/gui/double_book.png"));
        Tessellator tess = Tessellator.INSTANCE;
        tess.startQuads();
        tess.vertex(x, y + height, this.zOffset, u * xNormal, v + height * yNormal);
        tess.vertex(x + width, y + height, this.zOffset, u + width * xNormal, v + height * yNormal);
        tess.vertex(x + width, y, this.zOffset, u + width * xNormal, v * yNormal);
        tess.vertex(x, y, this.zOffset, u * xNormal, v * yNormal);
        tess.draw();
    }
}