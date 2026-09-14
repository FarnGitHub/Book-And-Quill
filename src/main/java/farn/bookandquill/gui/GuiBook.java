package farn.bookandquill.gui;

import farn.bookandquill.BookAndQuill;
import farn.bookandquill.util.PageFocus;
import farn.bookandquill.util.TextLine;
import farn.bookandquill.util.TextUtils;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;

public abstract class GuiBook extends GuiScreen {
    protected final EntityPlayer player;
    protected final ItemStack book;
    protected final boolean writable;
    protected boolean modified;
    protected boolean signing;
    protected int tick;
    protected final int titleImageWidth = 192;
    protected final int bookImageWidth = 344;
    protected final int imageHeight = 192;
    protected int totalPages;
    protected int currentPage = 0;
    protected NBTTagList pages;
    protected String title = "";
    protected GuiPageButton nextButton;
    protected GuiPageButton previousButton;
    protected GuiButton doneButton;
    protected GuiButton signButton;
    protected GuiButton finalizeButton;
    protected GuiButton cancelButton;
    protected PageFocus pageFocus = PageFocus.FIRST;
    protected int bookTitleCenterX = 0;
    protected int bookDoubleCenterX = 0;
    protected String underscore = "§0_";

    protected int selectionStartX = -1;
    protected int selectionStartY = -1;
    protected int selectionEndX = -1;
    protected int selectionEndY = -1;
    protected boolean selecting = false;

    public int cursorPos = 0;

    protected GuiBook(EntityPlayer player, ItemStack book, boolean writable) {
        this.player = player;
        this.book = book;
        this.writable = writable;
        if(!writable) {
            currentPage = -1;
        }

        this.pages = BookAndQuill.copyOf(this.getBookData().getTagList("pages"));
        int needToAdd = increment(0) - this.pages.tagCount();
        if(needToAdd > 0) {
            for (int i = 0; i < needToAdd; ++i)
                this.pages.setTag(new NBTTagString(""));
        } else if(!singlePage() && (this.pages.tagCount() & 1) == 1)
            this.pages.setTag(new NBTTagString(""));
        this.totalPages = this.pages.tagCount();
    }

    public static GuiBook get(EntityPlayer player, ItemStack book, boolean writable) {
        if(BookAndQuill.isClassicScreen())
            return new GuiBookClassic(player, book, writable);
        else
            return new GuiBookDouble(player, book, writable);
    }

    public abstract boolean singlePage();
    protected abstract void renderBookCover();
    protected abstract void renderSigning();
    protected abstract void renderBook();
    public abstract int getContentX(PageFocus focus);
    public abstract int getContentY(PageFocus focus);
    public abstract int getContentWidth(PageFocus focus);

    public NBTTagCompound getBookData() {
        return this.book.getItemData();
    }

    private void buttonUpdate() {
        this.nextButton.enabled2 = !this.signing && (this.currentPage < this.totalPages - 1 || this.writable);
        this.nextButton.updatePosition(this.currentPage <= -1 || singlePage());
        this.previousButton.enabled2 =  !this.signing && (this.currentPage > 0 || this.currentPage > -1 && !writable);
        this.previousButton.updatePosition(this.currentPage <= -1 || singlePage());
        this.doneButton.enabled2 = !this.writable || !this.signing;
        if(this.writable) {
            this.signButton.enabled2 = !this.signing;
            this.cancelButton.enabled2 = this.signing;
            this.finalizeButton.enabled2 = this.signing;
            this.finalizeButton.enabled = !this.title.trim().isEmpty();
        }

    }

    protected void updateBookData(boolean signingBook) {
        if(this.writable && this.modified) {
            if(this.pages != null) {
                while(this.pages.tagCount() > 1) {
                    NBTBase base = this.pages.tagAt(this.pages.tagCount() - 1);
                    if(!(base instanceof NBTTagString))
                        break;

                    if(!((NBTTagString)base).stringValue.isEmpty())
                        break;

                    BookAndQuill.remove(this.pages, this.pages.tagCount() - 1);
                }

                this.getBookData().setTag("pages", this.pages);
                if(signingBook) {
                    this.getBookData().setString("author", this.player.username);
                    this.getBookData().setString("title", this.title.trim());
                    this.book.itemID = BookAndQuill.writtenBook.shiftedIndex;
                }
            }

        }
    }

    protected void newPage() {
        if(this.pages != null && this.pages.tagCount() < 50) {
            int increment = increment(this.totalPages);
            for(int i = 0; i < increment; ++i)
                this.pages.setTag(new NBTTagString(""));
            this.totalPages += increment;
            this.modified = true;
        }
    }

    protected void typeInBook(char character, int keycode) {
        if (hasSelection() && character == '\u0003') {
            this.copyFromSelected();
        } else if (character == '\u0016') {
            this.addToContent(GuiScreen.getClipboardString(), false);
        } else {
            switch(keycode) {
                case Keyboard.KEY_BACK:
                    this.removeToContent();
                    return;
                case Keyboard.KEY_RETURN:
                    this.addToContent("\n", hasSelection());
                    return;
                default:
                    if(ChatAllowedCharacters.allowedCharacters.indexOf(character) >= 0) {
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
                    this.mc.displayGuiScreen(null);
                }

                return;
            default:
                if(this.title.length() < 16 && ChatAllowedCharacters.allowedCharacters.indexOf(character) >= 0) {
                    this.title = this.title + character;
                    this.buttonUpdate();
                    this.modified = true;
                }
        }
    }

    protected String getContent(PageFocus focus) {
        int currentPage = this.currentPage + focus.focusVal;
        if(currentPage >= 0 && this.pages != null && this.pages.tagCount() >= 0 && currentPage < this.pages.tagCount() && focus != PageFocus.UNFOCUS) {
            NBTBase base = this.pages.tagAt(currentPage);
            if(base instanceof NBTTagString) {
                return ((NBTTagString)base).stringValue;
            }
        }

        return "";
    }

    private void setContent(PageFocus focus, String content) {
        int currentPage = this.currentPage + focus.focusVal;
        if(this.pages != null && currentPage >= 0 && currentPage < this.pages.tagCount()) {
            ((NBTTagString)this.pages.tagAt(currentPage)).stringValue = content;
            this.modified = true;
        }
    }

    private void addToContent(String text, boolean selection) {
        String prevContent = this.getContent(pageFocus);
        if(selection)
            deleteSelection(pageFocus);
        this.cursorPos = BookAndQuill.clamp(this.cursorPos, 0, prevContent.length());
        String newContent = prevContent.substring(0, cursorPos) + text + prevContent.substring(cursorPos);
        this.cursorPos += text.length();
        int contentHeight = TextUtils.getTextLinesCount(newContent + "§0" + "_", 118);
        if(contentHeight <= 118 && newContent.length() < 256) {
            this.setContent(pageFocus, newContent);
        }
    }

    private void removeToContent() {
        if(hasSelection())
            deleteSelection(this.pageFocus);
        else if(this.cursorPos > 0) {
            String prevContent = this.getContent(pageFocus);
            this.cursorPos = BookAndQuill.clamp(this.cursorPos, 0, prevContent.length());
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


    public int increment(int integer) {
        return (integer & 1) == 1 || singlePage() ? 1 : 2;
    }

    public int getTotalDisplay() {
         return Math.max(
                 increment(0),
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

        List<TextLine> lines = TextUtils.getTextLines(content, getContentWidth(this.pageFocus));

        if (lines.isEmpty())
            return 0;

        int lineIndex = (mouseY - getContentY(this.pageFocus)) / 8;

        if (lineIndex < 0)
            return 0;

        if (lineIndex >= lines.size())
            return content.length();

        TextLine line = lines.get(lineIndex);

        int relativeX = mouseX - getContentX(this.pageFocus);

        if (relativeX <= 0)
            return line.start;

        for (int i = 0; i < line.text.length(); i++) {
            int previousWidth = i == 0 ? 0 : this.fontRenderer.getStringWidth(line.text.substring(0, i));

            int characterWidth = this.fontRenderer.getStringWidth(line.text.substring(0, i + 1));

            if (relativeX < (previousWidth + characterWidth) / 2) {
                return line.start + i;
            }
        }

        return line.end;
    }

    protected void drawPageContent(String content, int x, PageFocus focus) {
        int cursorX = getContentX(focus);
        int cursorY = getContentY(focus);
        List<TextLine> lines = TextUtils.getTextLines(content, getContentWidth(focus));
        for (int i = 0; i < lines.size(); i++) {
            TextLine line = lines.get(i);
            int lineY = getContentY(focus) + i * 8;
            if(this.pageFocus == focus) {
                if (hasSelection() && getSelectionMax() >= line.start && getSelectionMin() <= line.end) {
                    int selectedStart = Math.max(getSelectionMin(), line.start);
                    int selectedEnd = Math.min(getSelectionMax(), line.end);
                    int startOffset = Math.max(0, Math.min(selectedStart - line.start, line.text.length()));
                    int endOffset = Math.max(0, Math.min(selectedEnd - line.start, line.text.length()));
                    String before = line.text.substring(0, startOffset);
                    String selected = line.text.substring(startOffset, endOffset);
                    int highlightX = getContentX(focus) + this.fontRenderer.getStringWidth(before);
                    int highlightWidth = this.fontRenderer.getStringWidth(selected);

                    this.drawRect(highlightX, lineY, highlightX + highlightWidth, lineY + 8, 0x800000FF);
                }

                int cursor = BookAndQuill.clamp(this.cursorPos, 0, content.length());
                int cursorPrev = cursor - 1;
                if(cursorPrev == line.end) {
                    cursorX = getContentX(focus);
                    cursorY = lineY + 8;
                } else if(cursor >= line.start && cursor <= line.end) {
                    int offset = cursor - line.start;
                    String beforeC = line.text.substring(0, offset);
                    cursorX = getContentX(focus) + this.fontRenderer.getStringWidth(beforeC);
                    cursorY = lineY;
                }
            }
            this.fontRenderer.drawString(line.text,x,lineY,0);
        }

        if(this.writable && this.pageFocus == focus)
            this.drawRect(cursorX - 1, cursorY + 8, cursorX, cursorY, this.tick / 6 % 2 == 0 ? 0xFF808080 : 0);
    }

    private void copyFromSelected() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.getSelectedText()), null);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.controlList.clear();
        Keyboard.enableRepeatEvents(true);
        if(this.writable) {
            this.controlList.add(this.signButton = new GuiButton(3, this.width / 2 - 100, 4 + this.imageHeight, 98, 20, "Sign"));
            this.controlList.add(this.doneButton = new GuiButton(0, this.width / 2 + 2, 4 + this.imageHeight, 98, 20, StringTranslate.getInstance().translateKey("gui.done")));
            this.controlList.add(this.finalizeButton = new GuiButton(5, this.width / 2 - 100, 4 + this.imageHeight, 98, 20, "Sign and Close"));
            this.controlList.add(this.cancelButton = new GuiButton(4, this.width / 2 + 2, 4 + this.imageHeight, 98, 20, StringTranslate.getInstance().translateKey("gui.cancel")));
        } else
            this.controlList.add(this.doneButton = new GuiButton(0, this.width / 2 - 100, 4 + this.imageHeight, 200, 20, StringTranslate.getInstance().translateKey("gui.done")));

        this.bookTitleCenterX = (this.width - this.titleImageWidth) / 2;
        this.bookDoubleCenterX = (this.width - this.bookImageWidth) / 2;
        this.controlList.add(this.nextButton = new GuiPageButton(1, this.bookTitleCenterX + 120, 156, true, this.bookDoubleCenterX + 272, this));
        this.controlList.add(this.previousButton = new GuiPageButton(2, this.bookTitleCenterX + 38, 156, false, this.bookDoubleCenterX + 48, this));
        this.buttonUpdate();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float tick) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(currentPage <= -1 && !writable && !this.signing)
            renderBookCover();
        else if(this.signing)
            renderSigning();
        else
            renderBook();

        super.drawScreen(mouseX, mouseY, tick);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(++this.tick / 6 % 2 == 0)
            this.underscore = "§0_";
        else
            this.underscore = "§7_";
    }

    @Override
    public boolean doesGuiPauseGame() {
        return BookAndQuill.pauseGame();
    }

    @Override
    protected void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if(this.writable) {
            if(this.signing)
                this.typeInSigning(character, keyCode);
            else if(pageFocus != PageFocus.UNFOCUS)
                this.typeInBook(character, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.enabled2 && button.enabled) {
            if(button.id == 0) {
                this.mc.displayGuiScreen(null);
                this.updateBookData(false);
            } else if(button.id == 3 && this.writable) {
                this.signing = true;
            } else if(button.id == 1) {
                int currentPage = this.currentPage + (singlePage() ? 0 : 1);
                if (currentPage < this.totalPages - 1) {
                    this.currentPage += increment(this.currentPage);
                } else if(this.writable) {
                    this.newPage();
                    if(currentPage < this.totalPages - 1)
                        this.currentPage += increment(this.currentPage);
                }
            } else if(button.id == 2) {
                if (this.currentPage > -1) {
                    this.currentPage -= increment(this.currentPage);
                    if (this.currentPage < -1) {
                        this.currentPage = -1;
                    }
                }
            } else if(button.id == 6) {
                this.mc.displayGuiScreen(null);
            } else if(button.id == 5 && this.signing) {
                this.updateBookData(true);
                this.mc.displayGuiScreen(null);
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
                int yMin = 0;
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
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (button == 0) {
            selecting = false;
        }

        super.mouseMovedOrUp(mouseX, mouseY, button);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        if (this.pageFocus == PageFocus.UNFOCUS || !this.selecting)
            return;

        if (!Mouse.isButtonDown(0))
            return;

        if (Mouse.getEventDX() == 0 && Mouse.getEventDY() == 0)
            return;

        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        this.updateSelection(mouseX, mouseY);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}