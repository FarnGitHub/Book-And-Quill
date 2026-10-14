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
    protected final PlayerEntity player;
    protected final ItemStack book;
    protected final boolean writable;
    protected boolean modified;
    protected boolean signing;
    protected NbtList pages;
    protected String title = "";

    protected PageButton nextButton;
    protected PageButton previousButton;
    protected ButtonWidget doneButton;
    protected ButtonWidget signButton;
    protected ButtonWidget finalizeButton;
    protected ButtonWidget cancelButton;

    protected final int titleImageWidth = 192;
    protected final int bookImageWidth = 344;
    protected final int imageHeight = 192;
    protected int bookTitleCenterX = 0;
    protected int bookDoubleCenterX = 0;
    protected int bookCenterY = 0;

    protected String underscore = "§0_";
    protected int tick;

    protected PageFocus pageFocus = PageFocus.FIRST;
    protected int totalPages;
    private int currentPage = 0;
    protected int selectionStartX = -1;
    protected int selectionStartY = -1;
    protected int selectionEndX = -1;
    protected int selectionEndY = -1;
    protected boolean selecting = false;
    public int cursorPos = 0;
    @SuppressWarnings("unchecked")
    public List<TextLine>[] textLines = new List[incrementBy(0)];

    protected BookScreen(PlayerEntity player, ItemStack book, boolean writable) {
        this.player = player;
        this.book = book;
        this.writable = writable;
        if(!writable) {
            this.setCurrentPage(-1);
        }

        this.pages = this.getBookData().getList("pages").copy();
        int needToAdd = incrementBy(0) - this.pages.size();
        if(needToAdd > 0) {
            for (int i = 0; i < needToAdd; ++i)
                this.pages.add(new NbtString(""));
        } else if(!singlePage() && (this.pages.size() & 1) == 1)
            this.pages.add(new NbtString(""));
        this.totalPages = this.pages.size();

        this.updateAllTextLines();
    }

    public static BookScreen get(PlayerEntity player, ItemStack book, boolean writable) {
         return MainUtil.classicBook() ?
                new ClassicBookScreen(player, book, writable):
                new DoubleBookScreen(player, book, writable);
    }

    public abstract boolean singlePage();
    protected abstract void renderBookCover();
    protected abstract void renderSigning();
    protected abstract void renderBook();
    public abstract int getContentX(PageFocus focus);
    public abstract int getContentY(PageFocus focus);
    public abstract int getContentWidth(PageFocus focus);

    public NbtCompound getBookData() {
        return this.book.getStationNbt();
    }

    private void buttonUpdate() {
        this.nextButton.visible = !this.signing && (this.getCurrentPage() < this.totalPages - 1 || this.writable);
        this.nextButton.updatePosition(this.getCurrentPage() <= -1 || singlePage());
        this.previousButton.visible =  !this.signing && (this.getCurrentPage() > 0 || this.getCurrentPage() > -1 && !writable);
        this.previousButton.updatePosition(this.getCurrentPage() <= -1 || singlePage());
        this.doneButton.visible = !this.writable || !this.signing;
        if(this.writable) {
            this.signButton.visible = !this.signing;
            this.cancelButton.visible = this.signing;
            this.finalizeButton.visible = this.signing;
            this.finalizeButton.active = !this.title.trim().isEmpty();
        }

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
        if(this.pages != null && this.pages.size() < 50) {
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
        this.updateAllTextLines();
    }

    protected void pageForward() {
        this.currentPage += this.incrementBy(this.currentPage);
        this.updateAllTextLines();
    }

    protected void pageBack() {
        this.currentPage -= this.incrementBy(this.currentPage);
        this.updateAllTextLines();
    }

    protected void typeInBook(char character, int keycode) {
        if (hasSelection() && character == '\u0003') {
            this.copyFromSelected();
        } else if (character == '\u0016') {
            this.addToContent(Screen.getClipboard(), false);
        } else {
            switch(keycode) {
                case Keyboard.KEY_BACK:
                    this.removeToContent();
                    return;
                case Keyboard.KEY_RETURN:
                    this.addToContent("\n", hasSelection());
                    return;
                default:
                    if(TextUtils.isCharAllowed(character)) {
                        this.addToContent(Character.toString(character), hasSelection());
                    }
            }
        }
    }

    protected void typeInSigning(char character, int keyCode) {
        switch(keyCode) {
            case 14:
                if(!this.title.isEmpty()) {
                    this.title = this.title.substring(0, this.title.length() - 1);
                    this.buttonUpdate();
                }

                return;
            case 28:
                if(!this.title.isEmpty()) {
                    this.updateBookData(true);
                    this.minecraft.setScreen(null);
                }

                return;
            default:
                if(this.title.length() < 16 && TextUtils.isCharAllowed(character)) {
                    this.title = this.title + character;
                    this.buttonUpdate();
                    this.modified = true;
                }
        }
    }

    protected String getContent(PageFocus focus) {
        if(this.pages.size() < 1 || focus == null || focus == PageFocus.UNFOCUS) return "";

        int focusPage = this.getCurrentPage() + focus.increment();

        if(
           focusPage >= 0 &&
           focusPage < this.pages.size() &&
           this.pages.get(focusPage) instanceof NbtString string
        )
            return string.value;
        return "";
    }

    private void setContent(PageFocus focus, String content) {
        int currentPage = this.getCurrentPage() + focus.increment();
        if(this.pages != null && currentPage >= 0 && currentPage < this.pages.size()) {
            ((NbtString)this.pages.get(currentPage)).value = content;
            updateTextLines(focus);
            this.modified = true;
            this.unSelected();
        }
    }

    private void addToContent(String text, boolean selection) {
        String prevContent = this.getContent(pageFocus);
        if(selection)
            deleteSelection(pageFocus);
        this.cursorPos = MainUtil.clamp(this.cursorPos, 0, prevContent.length());
        String newContent = prevContent.substring(0, cursorPos) + text + prevContent.substring(cursorPos);
        this.cursorPos += text.length();
        int contentHeight = TextUtils.getTextLinesCount(newContent, 118);
        if(contentHeight <= 118 && newContent.length() < 256)
            this.setContent(pageFocus, newContent);
    }

    private void removeToContent() {
        if(hasSelection())
            deleteSelection(this.pageFocus);
        else if(this.cursorPos > 0) {
            String prevContent = this.getContent(pageFocus);
            this.cursorPos = MainUtil.clamp(this.cursorPos, 0, prevContent.length());
            String newContent = prevContent.substring(0, cursorPos - 1) + prevContent.substring(cursorPos);
            this.cursorPos -= 1;
            int contentHeight = TextUtils.getTextLinesCount(newContent, 118);
            if(contentHeight <= 118 && newContent.length() < 256)
                this.setContent(pageFocus, newContent);
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
        return (integer & 1) == 1 || singlePage() ? 1 : 2;
    }

    public int getTotalDisplay() {
         return Math.max(
                 incrementBy(0),
                 (totalPages & 1) == 1 && !singlePage() ?
                 this.totalPages + 1 : totalPages
         );
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
        return getCharacterIndexAt(this.selectionStartX, this.selectionStartY);
    }

    protected int getSelectionEnd() {
        return getCharacterIndexAt(this.selectionEndX, this.selectionEndY);
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

        String content = getContent(this.pageFocus);

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
        this.cursorPos = getCharacterIndexAt(mouseX, mouseY);
    }

    protected int getCharacterIndexAt(int mouseX, int mouseY) {
        String content = getContent(this.pageFocus);

        List<TextLine> lines = getTextLines(this.pageFocus);

        if (lines.isEmpty())
            return 0;

        int relativeY = mouseY - getContentY(this.pageFocus);
        int lineY = 0;

        if (relativeY < 0)
            return 0;

        int lineIndex = 0;

        for (; lineIndex < lines.size(); lineIndex++) {
            TextLine line = lines.get(lineIndex);
            int lineHeight = TextUtils.getHeight(line.text());

            if (relativeY < lineY + lineHeight)
                break;

            lineY += lineHeight;
        }

        if (lineIndex >= lines.size())
            return content.length();

        TextLine line = lines.get(lineIndex);

        int relativeX = mouseX - getContentX(this.pageFocus);

        if (relativeX <= 0)
            return line.start();

        boolean isRightToLeft = TextUtils.isRightToLeft(line.text());
        String lineText = line.text();
        int lineWidth = TextUtils.getWidth(lineText);

        if (isRightToLeft) {
            if (relativeX >= lineWidth)
                return line.start();

            int remainingWidth = lineWidth;

            for (int i = 0; i < lineText.length(); i++) {
                int nextWidth = TextUtils.getWidth(lineText.substring(i + 1));

                int characterWidth = remainingWidth - nextWidth;

                if (relativeX < nextWidth + characterWidth / 2)
                    return line.start() + i;

                remainingWidth = nextWidth;
            }
        } else {
            for (int i = 0; i < lineText.length(); i++) {
                int previousWidth = TextUtils.getWidth(lineText.substring(0, i));
                int nextWidth = TextUtils.getWidth(lineText.substring(0, i + 1));

                if (relativeX < (previousWidth + nextWidth) / 2)
                    return line.start() + i;
            }
        }

        return line.end();
    }

    protected void drawPageContent(String content, int x, PageFocus focus) {
        int cursorX = getContentX(focus);
        int cursorY = getContentY(focus);
        List<TextLine> lines = getTextLines(focus);
        for (int i = 0; i < lines.size(); i++) {
            TextLine line = lines.get(i);
            int lineY = getContentY(focus) + i * TextUtils.getHeight(line.text());
            if(this.pageFocus == focus) {
                if (hasSelection() && getSelectionMax() >= line.start() && getSelectionMin() <= line.end()) {
                    int selectedStart = Math.max(getSelectionMin(), line.start());
                    int selectedEnd = Math.min(getSelectionMax(), line.end());
                    int startOffset = Math.max(0, Math.min(selectedStart - line.start(), line.text().length()));
                    int endOffset = Math.max(0, Math.min(selectedEnd - line.start(), line.text().length()));
                    String before = line.text().substring(0, startOffset);
                    String selected = line.text().substring(startOffset, endOffset);
                    int highlightX = getContentX(focus) + TextUtils.getWidth(before);
                    int highlightWidth = TextUtils.getWidth(selected);

                    this.fill(highlightX, lineY, highlightX + highlightWidth, lineY + 8, 0x800000FF);
                }

                int cursor = MainUtil.clamp(this.cursorPos, 0, content.length());
                int cursorPrev = cursor - 1;
                if(cursor >= line.start() && cursor <= line.end()) {
                    int offset = cursor - line.start();
                    String beforeC = line.text().substring(0, offset);
                    cursorX = getContentX(focus) + TextUtils.getWidth(beforeC);
                    cursorY = lineY;
                }

                if(cursorPrev == line.end()) {
                    cursorX = getContentX(focus);
                    cursorY = lineY + TextUtils.getHeight(" ");
                }
            }
            this.textRenderer.draw(line.text(),x,lineY,0);
        }

        if(this.writable && this.pageFocus == focus)
            this.fill(cursorX - 1, cursorY + 8, cursorX, cursorY, this.tick / 6 % 2 == 0 ? 0xFF808080 : 0);
    }

    private void copyFromSelected() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.getSelectedText()), null);
    }

    private List<TextLine> getTextLines(PageFocus focus) {
        int index = focus.increment();
        return index >= 0 && index < textLines.length ?
                textLines[focus.increment()] : Collections.emptyList();
    }

    private void updateTextLines(PageFocus focus) {
        int index = focus.increment();
        if(index >= 0 && index < textLines.length)
            TextUtils.getTextLines(textLines[focus.increment()], getContent(focus), getContentWidth(focus));
    }

    private void updateAllTextLines() {
        int index = 0;
        for(List<TextLine> lines : textLines) {
            ++index;
            TextUtils.getTextLines(lines, getContent(PageFocus.from(index)), getContentWidth(PageFocus.from(index)));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        this.buttons.clear();
        Keyboard.enableRepeatEvents(true);
        this.cursorPos = getContent(pageFocus).length();
        this.bookTitleCenterX = (this.width - this.titleImageWidth) / 2;
        this.bookDoubleCenterX = (this.width - this.bookImageWidth) / 2;
        this.bookCenterY = (this.height - this.imageHeight) / 2 - 10;
        int buttonY = this.bookCenterY + this.imageHeight - 4;
        int pageButtonY = this.bookCenterY + 156;
        if(this.writable) {
            this.buttons.add(this.signButton = new ButtonWidget(3, this.width / 2 - 100, buttonY, 98, 20, MainUtil.translate("book.signButton")));
            this.buttons.add(this.doneButton = new ButtonWidget(0, this.width / 2 + 2, buttonY, 98, 20, TranslationStorage.getInstance().get("gui.done")));
            this.buttons.add(this.finalizeButton = new ButtonWidget(5, this.width / 2 - 100, buttonY, 98, 20, MainUtil.translate("book.finalizeButton")));
            this.buttons.add(this.cancelButton = new ButtonWidget(4, this.width / 2 + 2, buttonY, 98, 20, TranslationStorage.getInstance().get("gui.cancel")));
        } else
            this.buttons.add(this.doneButton = new ButtonWidget(0, this.width / 2 - 100, buttonY, 200, 20, TranslationStorage.getInstance().get("gui.done")));

        this.buttons.add(this.nextButton = new PageButton(1, this.bookTitleCenterX + 120, pageButtonY, true, this.bookDoubleCenterX + 272, this));
        this.buttons.add(this.previousButton = new PageButton(2, this.bookTitleCenterX + 38, pageButtonY, false, this.bookDoubleCenterX + 48, this));
        this.buttonUpdate();
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
        this.underscore =
                ++this.tick / 6 % 2 == 0 ?
                        "§0_" : "§7_";
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
            else if(pageFocus != PageFocus.UNFOCUS)
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
                this.pageFocus = PageFocus.UNFOCUS;
                this.unSelected();
            } else if(button.id == 1) {
                int currentPage = this.getCurrentPage() + (singlePage() ? 0 : 1);
                if (currentPage < this.totalPages - 1) {
                    this.pageForward();
                } else if(this.writable) {
                    this.newPage();
                    if(currentPage < this.totalPages - 1)
                        this.pageForward();
                }
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

            this.buttonUpdate();
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int b) {
        PageFocus prevFocus = pageFocus;
        if(!this.signing) {
            if(this.singlePage()) {
                this.pageFocus = PageFocus.FIRST;
            } else {
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
        } else {
            this.pageFocus = PageFocus.UNFOCUS;
        }

        if(prevFocus != this.pageFocus) {
            this.unSelected();
        }

        if (b == 0 && this.pageFocus != PageFocus.UNFOCUS) {
            this.selectionStartX = x;
            this.selectionStartY = y;
            this.selectionEndX = x;
            this.selectionEndY = y;
            this.selecting = true;
            this.cursorPos = getCharacterIndexAt(x, y);
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

        if (this.pageFocus == PageFocus.UNFOCUS || !this.selecting)
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