package farn.bookandquill.gui;

import farn.bookandquill.util.PageFocus;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StringTranslate;
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
        if(title.isEmpty())
            title = StringTranslate.getInstance().translateKey("bookandquill.book.no.title");

        String author = this.getBookData().getString("author");
        if(author.isEmpty())
            author = StringTranslate.getInstance().translateKey("bookandquill.book.unknown.author");

        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, -1);
        String authorFormat = StringTranslate.getInstance().translateKeyFormat("bookandquill.book.byAuthor", author);
        int authorWidth = this.fontRenderer.getStringWidth(authorFormat);
        this.fontRenderer.drawString(authorFormat, this.bookTitleCenterX + 36 + (116 - authorWidth) / 2, 60, -1);
    }

    @Override
    public void renderSigning() {
        this.drawBookCover();
        String title = this.title;

        if(this.writable)
            title += this.underscore;

        String editTitle = StringTranslate.getInstance().translateKey("bookandquill.book.editTitle");
        int newTitleWidth = this.fontRenderer.getStringWidth(editTitle);
        this.fontRenderer.drawString(editTitle, this.bookTitleCenterX + 36 + (116 - newTitleWidth) / 2, 34, -1);
        int titleWidth = this.fontRenderer.getStringWidth(title);
        this.fontRenderer.drawString(title, this.bookTitleCenterX + 36 + (116 - titleWidth) / 2, 50, -1);
        String author = StringTranslate.getInstance().translateKeyFormat("bookandquill.book.byAuthor", this.player.username);
        int authorWidth = this.fontRenderer.getStringWidth(author);
        this.fontRenderer.drawString(author, this.bookTitleCenterX + 36 + (116 - authorWidth) / 2, 60, -1);
        String warningFinal = StringTranslate.getInstance().translateKey("bookandquill.book.finalizeWarning");
        this.fontRenderer.func_27278_a(warningFinal, this.bookTitleCenterX + 36, 82, 116, -1);
    }

    @Override
    public void renderBook() {
        this.drawDoubleBook();
        int total = getTotalDisplay();
        String pageIndicator1 = StringTranslate.getInstance().translateKeyFormat("bookandquill.book.pageIndicator", this.currentPage + 1, total);
        String pageIndicator2 = StringTranslate.getInstance().translateKeyFormat("bookandquill.book.pageIndicator", this.currentPage + 2, total);
        String content1 = this.getContent(PageFocus.FIRST);
        String content2 = this.getContent(PageFocus.SECOND);

        int indicatorWidth = this.fontRenderer.getStringWidth(pageIndicator2);
        this.fontRenderer.drawString(pageIndicator1, this.bookDoubleCenterX + 44, 18, 0);
        this.drawPageContent(content1, this.bookDoubleCenterX + 42, PageFocus.FIRST);
        this.fontRenderer.drawString(pageIndicator2, this.bookDoubleCenterX - indicatorWidth + this.bookImageWidth - 44, 18, 0);
        this.drawPageContent(content2, this.bookDoubleCenterX + this.bookImageWidth - 156, PageFocus.SECOND);
    }

    @Override
    public int getContentX(PageFocus focus) {
        return focus == PageFocus.FIRST ?
                this.bookDoubleCenterX + 42 :
                this.bookDoubleCenterX + this.bookImageWidth - 156;
    }

    @Override
    public int getContentY(PageFocus focus) {
        return 34;
    }

    @Override
    public int getContentWidth(PageFocus focus) {
        return 116;
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