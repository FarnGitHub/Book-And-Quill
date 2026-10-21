package farn.bookandquil.gui.screen;

import farn.bookandquil.BookAndQuill;
import farn.bookandquil.gui.widget.PageButton;
import farn.bookandquil.packet.BookContentC2SPacket;
import farn.bookandquil.packet.SigningBookC2SPacket;
import farn.bookandquil.util.MainUtil;
import farn.bookandquil.util.PageFocus;
import farn.bookandquil.util.text.TextUtils;
import farn.bookandquil.util.text.TextLine;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Collections;
import java.util.List;

public abstract class BookScreen extends Screen {
    public static final int IMAGE_WIDTH = 192;
    public static final int IMAGE_HEIGHT = 192;
    public static final int DOUBLE_IMAGE_WIDTH = 344;
    public static final int MAX_CONTENT_BOUND = 118;
    public static final int MAX_CHAR_PER_PAGE = 256;
    public static final int MAX_PAGE = 100;

    protected int centerX = 0;
    protected int doubleCenterX = 0;
    protected int centerY = 0;

    protected final PlayerEntity player;
    protected final ItemStack book;
    protected final boolean writable;

    protected boolean modified;
    protected boolean signing;
    protected String title = "";
    protected NbtList pages;
    protected PageFocus focus = PageFocus.FIRST;
    protected int totalPages;
    private int currentPage = 0;
    @SuppressWarnings("unchecked")
    public List<TextLine>[] textCaches = new List[pageDisplayInScreen()];

    protected int selectionStartX = -1;
    protected int selectionStartY = -1;
    protected int selectionEndX = -1;
    protected int selectionEndY = -1;
    protected boolean selecting = false;
    public int cursorPos = 0;

    protected boolean cursorFlag = false;
    private int tick;

    protected PageButton nextButton;
    protected PageButton previousButton;
    protected ButtonWidget doneButton;
    protected ButtonWidget signButton;
    protected ButtonWidget finalizeButton;
    protected ButtonWidget cancelButton;

    protected BookScreen(PlayerEntity player, ItemStack book, boolean writable) {
        this.player = player;
        this.book = book;
        this.writable = writable;
        this.pages = this.getBookData().getList("pages").copy();
        int needToAdd = incrementBy(
                this.pages.size(),
                this.pages.size() >= this.pageDisplayInScreen()
        );
        if(needToAdd > 0)
            for (int i = 0; i < needToAdd; ++i)
                this.pages.add(new NbtString(""));
        this.totalPages = this.pages.size();
        this.updateAllTextsCaches();

        if(!writable) {
            this.setCurrentPage(-1);
        }

    }

    public static BookScreen get(PlayerEntity player, ItemStack book, boolean writable) {
         return MainUtil.classicBook() ?
                new ClassicBookScreen(player, book, writable):
                new DoubleBookScreen(player, book, writable);
    }

    public abstract int pageDisplayInScreen();
    protected abstract void renderBookCover();
    protected abstract void renderSigning();
    protected abstract void renderBook();
    public int getContentXStart(PageFocus focus) {
        return this.centerX + 36;
    }
    @SuppressWarnings("unused")
    public int getContentYStart(PageFocus focus) {
        return this.centerY + 34;
    }
    @SuppressWarnings("unused")
    public int getContentWidth(PageFocus focus) {
        return 116;
    }
    protected void handlePageClick(int x, int y) {
        this.focus = PageFocus.FIRST;
    }
    protected void updateButton() {
        this.nextButton.visible = !this.signing && (this.getCurrentPage() < this.totalPages - 1 || this.writable);
        this.previousButton.visible =  !this.signing && (this.getCurrentPage() > 0 || this.getCurrentPage() > -1 && !writable);
        this.doneButton.visible = !this.writable || !this.signing;
        if(this.writable) {
            this.signButton.visible = !this.signing;
            this.cancelButton.visible = this.signing;
            this.finalizeButton.visible = this.signing;
            this.finalizeButton.active = !this.title.trim().isEmpty();
        }
    }

    public NbtCompound getBookData() {
        return this.book.getStationNbt();
    }

    protected void updateBookData(boolean signingBook) {
        if(this.writable && this.modified) {
            if(this.pages != null) {
                while(this.pages.size() > 1) {
                    if(!(
                         this.pages.get(this.pages.size() - 1)
                         instanceof NbtString stringNbt)
                    ) break;

                    if(!stringNbt.value.isEmpty())
                        break;

                    this.pages.value.remove(this.pages.size() - 1);
                }

                this.getBookData().put("pages", this.pages);
                if(signingBook) {
                    this.getBookData().putString("author", this.player.name);
                    this.getBookData().putString("title", this.title.trim());
                    this.book.itemId = BookAndQuill.WRITTEN_BOOK.id;
                    PacketHelper.send(new SigningBookC2SPacket(player.inventory.selectedSlot, book));
                } else
                    PacketHelper.send(new BookContentC2SPacket(player.inventory.selectedSlot, book));
            }

        }
    }

    protected void newPage() {
        if(this.pages != null && this.pages.size() < MAX_PAGE) {
            int increment = incrementBy(this.totalPages);
            for(int i = 0; i < increment; ++i)
                this.pages.add(new NbtString(""));
            this.totalPages += increment;
            this.modified = true;
        }
    }

    protected int getCurrentPage() {
        return this.currentPage;
    }

    @SuppressWarnings("SameParameterValue")
    protected void setCurrentPage(int page) {
        this.currentPage = page;
        this.updateAllTextsCaches();
    }

    protected void pageForward() {
        this.currentPage += this.incrementBy(this.currentPage);
        this.updateAllTextsCaches();
    }

    protected void pageBack() {
        this.currentPage -= this.incrementBy(this.currentPage);
        this.updateAllTextsCaches();
    }

    protected void typeInBook(char character, int keycode) {
        if (hasSelection() && character == '\u0003') {
            this.copyFromSelected();
        } else if (character == '\u0016') {
            this.addContent(Screen.getClipboard(), false);
        } else {
            switch(keycode) {
                case Keyboard.KEY_BACK:
                    this.removeContent();
                    return;
                case Keyboard.KEY_RETURN:
                    this.addContent("\n", hasSelection());
                    return;
                default:
                    if(TextUtils.isCharAllowed(character)) {
                        this.addContent(Character.toString(character), hasSelection());
                    }
            }
        }
    }

    protected void typeInSigning(char character, int keyCode) {
        switch(keyCode) {
            case Keyboard.KEY_BACK:
                if(!this.title.isEmpty()) {
                    this.title = this.title.substring(0, this.title.length() - 1);
                    this.updateButton();
                }

                return;
            case Keyboard.KEY_RETURN:
                if(!this.title.isEmpty()) {
                    this.updateBookData(true);
                    this.minecraft.setScreen(null);
                }

                return;
            default:
                if(this.title.length() < 16 && TextUtils.isCharAllowed(character)) {
                    this.title = this.title + character;
                    this.updateButton();
                    this.modified = true;
                }
        }
    }

    protected String getContent(PageFocus focus) {
        if(focus == null || focus == PageFocus.UNFOCUS)
            return "";

        int page = this.getCurrentPage() + focus.ordinal();
        if(page >= 0 && page < this.pages.size())
            return ((NbtString)this.pages.get(page)).value;

        return "";
    }

    private void setContent(PageFocus focus, String content) {
        int currentPage = this.getCurrentPage() + focus.ordinal();
        if(currentPage >= 0 && currentPage < this.pages.size()) {
            ((NbtString)this.pages.get(currentPage)).value = content;
            updateTextLines(focus);
            this.modified = true;
            this.unSelected();
        }
    }

    private void addContent(String text, boolean selection) {
        String prevContent = this.getContent(focus);
        if(selection)
            deleteSelection(focus);
        this.cursorPos = MainUtil.clamp(this.cursorPos, 0, prevContent.length());
        String newContent = prevContent.substring(0, cursorPos) + text + prevContent.substring(cursorPos);
        this.cursorPos += text.length();
        int contentHeight = TextUtils.splitsTotalHeight(newContent, MAX_CONTENT_BOUND);
        if(contentHeight <= MAX_CONTENT_BOUND && newContent.length() < MAX_CHAR_PER_PAGE)
            this.setContent(focus, newContent);
    }

    private void removeContent() {
        if(hasSelection())
            deleteSelection(this.focus);
        else if(this.cursorPos > 0) {
            String prevContent = this.getContent(focus);
            this.cursorPos = MainUtil.clamp(this.cursorPos, 0, prevContent.length());
            String newContent = prevContent.substring(0, cursorPos - 1) + prevContent.substring(cursorPos);
            this.cursorPos -= 1;
            int contentHeight = TextUtils.splitsTotalHeight(newContent, MAX_CONTENT_BOUND);
            if(contentHeight <= MAX_CONTENT_BOUND && newContent.length() < MAX_CHAR_PER_PAGE)
                this.setContent(focus, newContent);
        }
    }

    private void deleteSelection(PageFocus focus) {
        String message = this.getContent(focus);
        int lo = getSelectionMin();
        int hi = getSelectionMax();
        this.setContent(focus, message.substring(0, lo) + message.substring(hi));
        this.cursorPos = lo;
        unSelected();
    }


    public int incrementBy(int integer) {
        return incrementBy(integer, false);
    }

    public int incrementBy(int integer, boolean accept0) {
        int displayed = this.pageDisplayInScreen();
        int calculated = ((displayed - integer % displayed) % displayed);
        return calculated == 0 && !accept0 ? displayed : calculated;
    }

    public int getTotalDisplay() {
         return this.totalPages + incrementBy(this.totalPages, true);
    }

    protected boolean hasSelection() {
        return this.selectionStartX >= 0
                && this.selectionStartY >= 0
                && this.selectionEndX >= 0
                && this.selectionEndY >= 0
                && (this.selectionStartX != this.selectionEndX
                || this.selectionStartY != this.selectionEndY);
    }


    protected int getSelectionStart() {
        return getIndexAt(this.selectionStartX, this.selectionStartY);
    }

    protected int getSelectionEnd() {
        return getIndexAt(this.selectionEndX, this.selectionEndY);
    }

    protected int getSelectionMin() {
        return Math.min(getSelectionStart(), getSelectionEnd());
    }

    protected int getSelectionMax() {
        return Math.max(getSelectionStart(), getSelectionEnd());
    }

    protected String getSelectedText() {
        if (!hasSelection())
            return "";

        String content = getContent(this.focus);

        int start = getSelectionMin();
        int end = Math.min(getSelectionMax(), content.length());

        if (start >= end)
            return "";

        return content.substring(start, end);
    }

    protected void unSelected() {
        this.selecting = false;
        this.selectionStartX = -1;
        this.selectionStartY = -1;
        this.selectionEndX = -1;
        this.selectionEndY = -1;
    }

    protected void updateSelection(int mouseX, int mouseY) {
        this.selectionEndX = mouseX;
        this.selectionEndY = mouseY;
        this.cursorPos = getIndexAt(mouseX, mouseY);
    }

    protected int getIndexAt(int mouseX, int mouseY) {
        String content = getContent(this.focus);

        List<TextLine> lines = getTextLines(this.focus);

        if (lines ==  null || lines.isEmpty())
            return 0;

        int relativeY = mouseY - getContentYStart(this.focus);
        int lineY = 0;

        if (relativeY < 0)
            return 0;

        int lineIndex = 0;

        while(lineIndex < lines.size()) {
            TextLine line = lines.get(lineIndex);
            int lineHeight = TextUtils.getHeight(line.text);

            if (relativeY < lineY + lineHeight)
                break;

            lineY += lineHeight;
            lineIndex++;
        }

        if (lineIndex >= lines.size())
            return content.length();

        TextLine line = lines.get(lineIndex);

        int relativeX = mouseX - getContentXStart(this.focus);

        if (relativeX <= 0)
            return line.start;

        String lineText = line.text;

        for (int i = 0; i < lineText.length(); i++) {
            int previousWidth = TextUtils.getWidth(lineText.substring(0, i));
            int nextWidth = TextUtils.getWidth(lineText.substring(0, i + 1));

            if (relativeX < (previousWidth + nextWidth) / 2)
                return line.start + i;
        }

        return line.end;
    }

    protected void drawPageContent(String content, int x, PageFocus focus) {
        int cursorX = getContentXStart(focus);
        int cursorY = getContentYStart(focus);
        List<TextLine> lines = getTextLines(focus);
        if(lines == null) return;
        for (int i = 0; i < lines.size(); i++) {
            TextLine line = lines.get(i);
            int lineY = getContentYStart(focus) + i * TextUtils.getHeight(line.text);
            this.textRenderer.draw(line.text,x,lineY,0);
            if(this.focus == focus) {
                if (hasSelection() && getSelectionMax() >= line.start && getSelectionMin() <= line.end) {
                    int selectedStart = Math.max(getSelectionMin(), line.start);
                    int selectedEnd = Math.min(getSelectionMax(), line.end);
                    int startOffset = Math.max(0, Math.min(selectedStart - line.start, line.text.length()));
                    int endOffset = Math.max(0, Math.min(selectedEnd - line.start, line.text.length()));
                    String before = line.text.substring(0, startOffset);
                    String selected = line.text.substring(startOffset, endOffset);
                    int highlightX = getContentXStart(focus) + TextUtils.getWidth(before);
                    int highlightWidth = TextUtils.getWidth(selected);
                    this.renderHighlight(highlightX, lineY, highlightX + highlightWidth, lineY + TextUtils.getHeight(line.text));
                }

                int cursor = MainUtil.clamp(this.cursorPos, 0, content.length());
                if(cursor >= line.start && cursor <= line.end) {
                    int offset = cursor - line.start;
                    String beforeC = line.text.substring(0, offset);
                    cursorX = getContentXStart(focus) + TextUtils.getWidth(beforeC);
                    cursorY = lineY;
                }
            }
        }

        if(this.cursorFlag && this.writable && this.focus == focus)
            this.fill(cursorX - 1, cursorY + 8, cursorX, cursorY, 0xFF808080);
    }

    private void renderHighlight(int x1, int y1, int x2, int y2) {

        if (x1 < x2) {
            int ogX1 = x1;
            x1 = x2;
            x2 = ogX1;
        }

        if (y1 < y2) {
            int ogY1 = y1;
            y1 = y2;
            y2 = ogY1;
        }

        Tessellator tess = Tessellator.INSTANCE;
        GL11.glColor4f(0.0F, 0.5F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);
        tess.startQuads();
        tess.vertex(x1, y2, 0.0F);
        tess.vertex(x2, y2, 0.0F);
        tess.vertex(x2, y1, 0.0F);
        tess.vertex(x1, y1, 0.0F);
        tess.draw();
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void copyFromSelected() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.getSelectedText()), null);
    }

    private List<TextLine> getTextLines(PageFocus focus) {
        int index = focus.ordinal();
        return index < textCaches.length ?
                textCaches[index] : Collections.emptyList();
    }

    private void updateTextLines(PageFocus focus) {
        int index = focus.ordinal();
        if(index < textCaches.length)
            TextUtils.splits(textCaches[index], getContent(focus), MAX_CONTENT_BOUND);
    }

    private void updateAllTextsCaches() {
        for(int i = 0; i < textCaches.length; i++) {
            PageFocus focus = PageFocus.from(i);
            textCaches[i] = TextUtils.splits(textCaches[i], getContent(focus), MAX_CONTENT_BOUND);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        this.buttons.clear();
        Keyboard.enableRepeatEvents(true);
        this.cursorPos = getContent(focus).length();
        this.centerX = (this.width - IMAGE_WIDTH) / 2;
        this.doubleCenterX = (this.width - DOUBLE_IMAGE_WIDTH) / 2;
        this.centerY = (this.height - IMAGE_HEIGHT) / 2 - 10;
        int buttonY = this.centerY + IMAGE_HEIGHT - 4;
        int pageButtonY = this.centerY + 156;
        if(this.writable) {
            this.buttons.add(this.signButton = new ButtonWidget(3, this.width / 2 - 100, buttonY, 98, 20, MainUtil.translate("book.signButton")));
            this.buttons.add(this.doneButton = new ButtonWidget(0, this.width / 2 + 2, buttonY, 98, 20, TranslationStorage.getInstance().get("gui.done")));
            this.buttons.add(this.finalizeButton = new ButtonWidget(5, this.width / 2 - 100, buttonY, 98, 20, MainUtil.translate("book.finalizeButton")));
            this.buttons.add(this.cancelButton = new ButtonWidget(4, this.width / 2 + 2, buttonY, 98, 20, TranslationStorage.getInstance().get("gui.cancel")));
        } else
            this.buttons.add(this.doneButton = new ButtonWidget(0, this.width / 2 - 100, buttonY, 200, 20, TranslationStorage.getInstance().get("gui.done")));

        this.buttons.add(this.nextButton = new PageButton(1, this.centerX + 120, pageButtonY, true));
        this.buttons.add(this.previousButton = new PageButton(2, this.centerX + 38, pageButtonY, false));
        this.updateButton();
    }

    @Override
    public void render(int mouseX, int mouseY, float tick) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(this.getCurrentPage() <= -1 && !writable && !this.signing)
            renderBookCover();
        else if(this.signing)
            renderSigning();
        else
            renderBook();

        super.render(mouseX, mouseY, tick);
    }

    @Override
    public void tick() {
        super.tick();
        if(++this.tick % 5 == 0)
            this.cursorFlag = !this.cursorFlag;
    }

    @Override
    public boolean shouldPause() {
        return MainUtil.shouldPause();
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        super.keyPressed(character, keyCode);
        if(this.writable) {
            if(this.signing)
                this.typeInSigning(character, keyCode);
            else if(focus != PageFocus.UNFOCUS)
                this.typeInBook(character, keyCode);
        } else if(hasSelection() && character == '\u0003')
            copyFromSelected();
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.visible && button.active) {
            if(button.id == 0) {
                this.minecraft.setScreen(null);
                this.updateBookData(false);
            } else if(button.id == 3 && this.writable) {
                this.signing = true;
                this.focus = PageFocus.UNFOCUS;
                this.unSelected();
            } else if(button.id == 1) {
                int nextPage = this.getCurrentPage() + pageDisplayInScreen();

                if(this.writable && nextPage >= this.totalPages)
                    this.newPage();

                if (nextPage < this.totalPages)
                    this.pageForward();
            } else if(button.id == 2) {
                if (this.getCurrentPage() > -1) {
                    this.pageBack();
                    if (this.getCurrentPage() < -1) {
                        this.setCurrentPage(-1);
                    }
                }
            } else if(button.id == 6) {
                this.minecraft.setScreen(null);
            } else if(button.id == 5 && this.signing) {
                this.updateBookData(true);
                this.minecraft.setScreen(null);
            } else if(button.id == 4 && this.signing) {
                this.signing = false;
            }

            this.updateButton();
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int b) {
        PageFocus prevFocus = focus;
        if(this.signing)
            this.focus = PageFocus.UNFOCUS;
        else
            this.handlePageClick(x, y);

        if(prevFocus != this.focus)
            this.unSelected();

        if (b == 0 && this.focus != PageFocus.UNFOCUS) {
            this.selectionStartX = x;
            this.selectionStartY = y;
            this.selectionEndX = x;
            this.selectionEndY = y;
            this.selecting = true;
            this.cursorPos = getIndexAt(x, y);
        }

        super.mouseClicked(x, y, b);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        if (button == 0) {
            selecting = false;
        }

        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onMouseEvent() {
        super.onMouseEvent();

        if (this.focus == PageFocus.UNFOCUS || !this.selecting)
            return;

        if (!Mouse.isButtonDown(0))
            return;

        if (Mouse.getEventDX() == 0 && Mouse.getEventDY() == 0)
            return;

        int mouseX = Mouse.getEventX() * this.width / this.minecraft.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.minecraft.displayHeight - 1;

        this.updateSelection(mouseX, mouseY);
    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }
}