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
    public boolean singlePage() {
        return false;
    }

    @Override
    public void renderBookCover() {
        this.drawBookCover();
        String title = this.getBookData().getString("title");
        if(title.isEmpty()) {
            title = MainUtil.translate("book.no.title");
        }
        String author = this.getBookData().getString("author");
        if(author.isEmpty()) {
            author = MainUtil.translate("book.unknown.author");
        }

        int titleWidth = this.textRenderer.getWidth(title);
        this.textRenderer.draw(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, -1);
        String authorFormat = MainUtil.translate("book.byAuthor", author);
        int authorWidth = this.textRenderer.getWidth(authorFormat);
        this.textRenderer.draw(authorFormat, this.bookTitleCenterX + 36 + (116 - authorWidth) / 2, 60, -1);
    }

    @Override
    public void renderSigning() {
        this.drawBookCover();
        String title = this.title;

        if(this.writable)
            title += this.underscore;

        String editTitle = MainUtil.translate("book.editTitle");
        int newTitleWidth = this.textRenderer.getWidth(editTitle);
        this.textRenderer.draw(editTitle, this.bookTitleCenterX + 36 + (116 - newTitleWidth) / 2, 34, -1);
        int titleWidth = this.textRenderer.getWidth(title);
        this.textRenderer.draw(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, -1);
        String author = String.format(MainUtil.translate("book.byAuthor"), this.player.name);
        int authorWidth = this.textRenderer.getWidth(author);
        this.textRenderer.draw(author, this.bookTitleCenterX + 36 + (116 - authorWidth) / 2, 60, -1);
        String warningFinal = MainUtil.translate("book.finalizeWarning");
        this.textRenderer.drawSplit(warningFinal, this.bookTitleCenterX + 36, 82, 116, -1);
    }

    @Override
    public void renderBook() {
        this.drawDoubleBook();
        int total = getTotalDisplay();
        String pageIndicator1 = MainUtil.translate("book.pageIndicator", this.currentPage + 1, total);
        String pageIndicator2 = MainUtil.translate("book.pageIndicator", this.currentPage + 2, total);
        String content1 = this.getContent(PageFocus.FIRST);
        String content2 = this.getContent(PageFocus.SECOND);

        if(this.writable) {
            if(pageFocus == PageFocus.FIRST)
                content1 = content1 + this.underscore;
            else if(pageFocus == PageFocus.SECOND)
                content2 = content2 + this.underscore;
        }

        int indicatorWidth = this.textRenderer.getWidth(pageIndicator2);
        this.textRenderer.draw(pageIndicator1, this.bookDoubleCenterX + 44, 18, 0);
        this.textRenderer.drawSplit(content1, this.bookDoubleCenterX + 42, 34, 116, 0);
        this.textRenderer.draw(pageIndicator2, this.bookDoubleCenterX - indicatorWidth + this.bookImageWidth - 44, 18, 0);
        this.textRenderer.drawSplit(content2, this.bookDoubleCenterX + this.bookImageWidth - 156, 34, 116, 0);
    }

    public void drawBookCover() {
        String tex = "/assets/bookandquill/gui/title_book.png";
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId(tex));
        this.drawTexture(this.bookTitleCenterX, 2, 0, 0, this.titleImageWidth, this.imageHeight);
    }

    public void drawDoubleBook() {
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId("/assets/bookandquill/gui/double_book.png"));
        float xNudge = 0.0029296875F;
        float yNudge = 0.00520833333F;
        Tessellator tess = Tessellator.INSTANCE;
        tess.startQuads();
        tess.vertex(this.bookDoubleCenterX, 2 + this.imageHeight, this.zOffset, 0.0F, (float)(this.imageHeight) * yNudge);
        tess.vertex(this.bookDoubleCenterX + this.bookImageWidth, 2 + this.imageHeight, this.zOffset, (float)(this.bookImageWidth) * xNudge, (float)(this.imageHeight) * yNudge);
        tess.vertex(this.bookDoubleCenterX + this.bookImageWidth, 2, this.zOffset, (float)(this.bookImageWidth) * xNudge, 0.0F);
        tess.vertex(this.bookDoubleCenterX, 2, this.zOffset, 0.0F, 0.0F);
        tess.draw();
    }
}