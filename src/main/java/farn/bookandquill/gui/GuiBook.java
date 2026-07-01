package farn.bookandquill.gui;

import farn.bookandquill.BookAndQuill;
import farn.bookandquill.util.PageFocus;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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
         return new GuiBookDouble(player, book, writable);
    }

    public abstract boolean singlePage();

    protected abstract void renderBookCover();

    protected abstract void renderSigning();

    protected abstract void renderBook();

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

    protected void typeInBook(PageFocus pager, char character, int keycode) {
        if(character == '\u0016') this.addToContent(GuiScreen.getClipboardString());
        else {
            switch(keycode) {
                case 14:
                    String content = this.getContent(pager);
                    if(!content.isEmpty()) {
                        this.setContent(pager, content.substring(0, content.length() - 1));
                    }

                    return;
                case 28:
                    this.addToContent("\n");
                    return;
                default:
                    if(ChatAllowedCharacters.allowedCharacters.indexOf(character) >= 0) {
                        this.addToContent(Character.toString(character));
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
        if(this.pages != null && this.pages.tagCount() >= 0 && currentPage < this.pages.tagCount() && focus != PageFocus.UNFOCUS) {
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

    private void addToContent(String text) {
        String newContent = this.getContent(pageFocus) + text;
        int contentHeight = this.fontRenderer.func_27277_a(newContent + "§0" + "_", 118);
        if(contentHeight <= 118 && newContent.length() < 256) {
            this.setContent(pageFocus, newContent);
        }
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
        return false;
    }

    @Override
    protected void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if(this.writable) {
            if(this.signing)
                this.typeInSigning(character, keyCode);
            else if(pageFocus != PageFocus.UNFOCUS)
                this.typeInBook(pageFocus, character, keyCode);
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
        if(singlePage()) {
            this.pageFocus = PageFocus.FIRST;
        } else if(this.writable) {
            int fX = (this.width - this.bookImageWidth) / 2;
            int sX = fX + (this.bookImageWidth / 2);
            int yMin = 0;
            int pageWidth = this.bookImageWidth / 2;
            int yMax = this.imageHeight;
            if(x >= fX && x < fX + pageWidth && y >= yMin && y < yMax) {
                this.pageFocus = PageFocus.FIRST;
            } else if(x >= sX && x < sX + pageWidth && y >= yMin && y < yMax) {
                this.pageFocus = PageFocus.SECOND;
            } else {
                this.pageFocus = PageFocus.UNFOCUS;
            }
        } else {
            this.pageFocus = PageFocus.UNFOCUS;
        }

        super.mouseClicked(x, y, b);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}