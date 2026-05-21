package farn.bookandquill.gui;

import farn.bookandquill.BookAndQuill;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiScreenBook extends GuiScreen {
    /*private final EntityPlayer editingPlayer;
    private final ItemStack itemstackBook;
    private final boolean bookIsUnsigned;
    private boolean bookModified;
    private boolean editingTitle;
    private int updateCount;
    private int bookImageWidth = 192;
    private int bookImageHeight = 192;
    private int bookTotalPages = 1;
    private int currPage;
    private NBTTagList bookPages;
    private String bookTitle = "";
    private GuiButtonNextPage buttonNextPage;
    private GuiButtonNextPage buttonPreviousPage;
    private GuiButton buttonDone;
    private GuiButton buttonSign;
    private GuiButton buttonFinalize;
    private GuiButton buttonCancel;

    public GuiScreenBook(EntityPlayer var1, ItemStack var2, boolean var3) {
        this.editingPlayer = var1;
        this.itemstackBook = var2;
        this.bookIsUnsigned = var3;
        if(var2.getItemData() != null) {
            NBTTagCompound var4 = var2.getItemData();
            this.bookPages = var4.getTagList("pages");
            if(this.bookPages != null) {
                this.bookPages = mod_BookAndQuill.copyNbtList(this.bookPages);
                this.bookTotalPages = this.bookPages.tagCount();
                if(this.bookTotalPages < 1) {
                    this.bookTotalPages = 1;
                }
            }
        }

        if(this.bookPages == null && var3) {
            this.bookPages = new NBTTagList();
            this.bookPages.setTag(new NBTTagString(""));
            this.bookTotalPages = 1;
        }

    }

    public void updateScreen() {
        super.updateScreen();
        ++this.updateCount;
    }

    public void initGui() {
        this.controlList.clear();
        Keyboard.enableRepeatEvents(true);
        if(this.bookIsUnsigned) {
            this.controlList.add(this.buttonSign = new GuiButton(3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, "Sign"));
            this.controlList.add(this.buttonDone = new GuiButton(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, StatCollector.translateToLocal("gui.done")));
            this.controlList.add(this.buttonFinalize = new GuiButton(5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, "Sign and Close"));
            this.controlList.add(this.buttonCancel = new GuiButton(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, StatCollector.translateToLocal("gui.cancel")));
        } else {
            this.controlList.add(this.buttonDone = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, StatCollector.translateToLocal("gui.done")));
        }

        int var1 = (this.width - this.bookImageWidth) / 2;
        byte var2 = 2;
        this.controlList.add(this.buttonNextPage = new GuiButtonNextPage(1, var1 + 120, var2 + 154, true));
        this.controlList.add(this.buttonPreviousPage = new GuiButtonNextPage(2, var1 + 38, var2 + 154, false));
        this.updateButtons();
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void updateButtons() {
        this.buttonNextPage.enabled2 = !this.editingTitle && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
        this.buttonPreviousPage.enabled2 = !this.editingTitle && this.currPage > 0;
        this.buttonDone.enabled2 = !this.bookIsUnsigned || !this.editingTitle;
        if(this.bookIsUnsigned) {
            this.buttonSign.enabled2 = !this.editingTitle;
            this.buttonCancel.enabled2 = this.editingTitle;
            this.buttonFinalize.enabled2 = this.editingTitle;
            this.buttonFinalize.enabled = this.bookTitle.trim().length() > 0;
        }

    }

    private void sendBookToServer(boolean var1) {
        if(this.bookIsUnsigned && this.bookModified) {
            if(this.bookPages != null) {
                while(this.bookPages.tagCount() > 1) {
                    NBTTagString var2 = (NBTTagString)this.bookPages.tagAt(this.bookPages.tagCount() - 1);
                    if(var2.stringValue != null && var2.stringValue.length() != 0) {
                        break;
                    }

                    mod_BookAndQuill.nbtListRemoveTag(this.bookPages, this.bookPages.tagCount() - 1);
                }

                if(this.itemstackBook.getItemData() != null) {
                    NBTTagCompound var7 = this.itemstackBook.getItemData();
                    var7.setTag("pages", this.bookPages);
                } else {
                    this.itemstackBook.getItemData().setTag("pages", this.bookPages);
                }

                String var8 = "MC|BEdit";
                if(var1) {
                    var8 = "MC|BSign";
                    this.itemstackBook.getItemData().setTag("author", new NBTTagString(this.editingPlayer.username));
                    this.itemstackBook.getItemData().setTag("title", new NBTTagString(this.bookTitle.trim()));
                    this.itemstackBook.itemID = mod_BookAndQuill.writtenBook.shiftedIndex;
                }
            }

        }
    }

    protected void actionPerformed(GuiButton var1) {
        if(var1.enabled) {
            if(var1.id == 0) {
                this.mc.displayGuiScreen((GuiScreen)null);
                this.sendBookToServer(false);
            } else if(var1.id == 3 && this.bookIsUnsigned) {
                this.editingTitle = true;
            } else if(var1.id == 1) {
                if(this.currPage < this.bookTotalPages - 1) {
                    ++this.currPage;
                } else if(this.bookIsUnsigned) {
                    this.addNewPage();
                    if(this.currPage < this.bookTotalPages - 1) {
                        ++this.currPage;
                    }
                }
            } else if(var1.id == 2) {
                if(this.currPage > 0) {
                    --this.currPage;
                }
            } else if(var1.id == 5 && this.editingTitle) {
                this.sendBookToServer(true);
                this.mc.displayGuiScreen((GuiScreen)null);
            } else if(var1.id == 4 && this.editingTitle) {
                this.editingTitle = false;
            }

            this.updateButtons();
        }
    }

    private void addNewPage() {
        if(this.bookPages != null && this.bookPages.tagCount() < 50) {
            this.bookPages.setTag(new NBTTagString(""));
            ++this.bookTotalPages;
            this.bookModified = true;
        }
    }

    protected void keyTyped(char var1, int var2) {
        super.keyTyped(var1, var2);
        if(this.bookIsUnsigned) {
            if(this.editingTitle) {
                this.func_74162_c(var1, var2);
            } else {
                this.keyTypedInBook(var1, var2);
            }

        }
    }

    private void keyTypedInBook(char var1, int var2) {
        switch(var1) {
            case '\u0016':
                this.func_74160_b(GuiScreen.getClipboardString());
                return;
            default:
                switch(var2) {
                    case 14:
                        String var3 = this.func_74158_i();
                        if(var3.length() > 0) {
                            this.func_74159_a(var3.substring(0, var3.length() - 1));
                        }

                        return;
                    case 28:
                        this.func_74160_b("\n");
                        return;
                    default:
                        if(ChatAllowedCharacters.allowedCharacters.indexOf(var1) >= 0) {
                            this.func_74160_b(Character.toString(var1));
                        }
                }
        }
    }

    private void func_74162_c(char var1, int var2) {
        switch(var2) {
            case 14:
                if(this.bookTitle.length() > 0) {
                    this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
                    this.updateButtons();
                }

                return;
            case 28:
                if(this.bookTitle.length() > 0) {
                    this.sendBookToServer(true);
                    this.mc.displayGuiScreen((GuiScreen)null);
                }

                return;
            default:
                if(this.bookTitle.length() < 16 && ChatAllowedCharacters.allowedCharacters.indexOf(var1) >= 0) {
                    this.bookTitle = this.bookTitle + Character.toString(var1);
                    this.updateButtons();
                    this.bookModified = true;
                }
        }
    }

    private String func_74158_i() {
        if(this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            NBTTagString var1 = (NBTTagString)this.bookPages.tagAt(this.currPage);
            return var1.toString();
        } else {
            return "";
        }
    }

    private void func_74159_a(String var1) {
        ensureCurrentPageExists();
        if(this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            NBTTagString var2 = (NBTTagString)this.bookPages.tagAt(this.currPage);
            var2.stringValue = var1;
            this.bookModified = true;
        }

    }

    private void ensureCurrentPageExists() {
        if (this.bookPages == null) {
            this.bookPages = new NBTTagList();
        }
        while (this.currPage >= this.bookPages.tagCount()) {
            this.bookPages.setTag(new NBTTagString(""));
            ++this.bookTotalPages;
        }
    }


    private void func_74160_b(String var1) {
        String var2 = this.func_74158_i();
        String var3 = var2 + var1;
        int var4 = this.fontRenderer.func_27277_a(var3 + "§0" + "_", 118);
        if(var4 <= 118 && var3.length() < 256) {
            this.func_74159_a(var3);
        }

    }

    public void drawScreen(int var1, int var2, float var3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/farn/bookandquill/resource/bookGui.png"));
        int var4 = (this.width - this.bookImageWidth) / 2;
        byte var5 = 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.bookImageWidth, this.bookImageHeight);
        String var6;
        String var7;
        int var8;
        if (this.editingTitle) {
            var6 = this.bookTitle;
            if (this.bookIsUnsigned) {
                if (this.updateCount / 6 % 2 == 0) {
                    var6 = var6 + "§0" + "_";
                } else {
                    var6 = var6 + "§7" + "_";
                }
            }

            var7 = "Enter Book Title:";
            var8 = this.fontRenderer.getStringWidth(var7);
            this.fontRenderer.drawString(var7, var4 + 36 + (116 - var8) / 2, var5 + 16 + 16, 0);
            int var9 = this.fontRenderer.getStringWidth(var6);
            this.fontRenderer.drawString(var6, var4 + 36 + (116 - var9) / 2, var5 + 48, 0);
            String var10 = String.format("by %1$s", new Object[]{this.editingPlayer.username});
            int var11 = this.fontRenderer.getStringWidth(var10);
            this.fontRenderer.drawString("§8" + var10, var4 + 36 + (116 - var11) / 2, var5 + 48 + 10, 0);
            String var12 = "Note! When you sign the book, it will no longer be editable.";
            this.fontRenderer.func_27278_a(var12, var4 + 36, var5 + 80, 116, 0);
        } else {
            var6 = String.format("Page %1$s of %2$s", new Object[]{Integer.valueOf(this.currPage + 1), Integer.valueOf(this.bookTotalPages)});
            var7 = "";
            if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
                NBTTagString var13 = (NBTTagString) this.bookPages.tagAt(this.currPage);
                var7 = var13.toString();
            }

            if (this.bookIsUnsigned) {
                if (this.updateCount / 6 % 2 == 0) {
                    var7 = var7 + "_";
                } else {
                    var7 = var7 + "§8" + "_";
                }
            }

            var8 = this.fontRenderer.getStringWidth(var6);
            this.fontRenderer.drawString(var6, var4 - var8 + this.bookImageWidth - 44, var5 + 16, 0);
            this.fontRenderer.func_27278_a(var7, var4 + 36, var5 + 16 + 16, 116, 0);
        }

        super.drawScreen(var1, var2, var3);
    }*/

    private final EntityPlayer player;
    private final ItemStack book;
    private final boolean writable;
    private boolean modified;
    private boolean signing;
    private int tick;
    private final int backgroundWidth = 192;
    private final int backgroundHeight = 192;
    private int totalPages = 1;
    private int currentPage = 0;
    private NBTTagList pages;
    private String title = "";
    private GuiPageButton nextButton;
    private GuiPageButton previousButton;
    private GuiButton doneButton;
    private GuiButton signButton;
    private GuiButton finalizeButton;
    private GuiButton cancelButton;

    public GuiScreenBook(EntityPlayer player, ItemStack book, boolean writable) {
        this.player = player;
        this.book = book;
        this.writable = writable;
        if(!writable) {
            currentPage = -1;
        }

        if(book.getItemData() != null) {
            this.pages = book.getItemData().getTagList("pages");
            if(this.pages != null) {
                if(this.pages.tagCount() <= 0) {
                    this.pages.setTag(new NBTTagString(""));
                    this.totalPages = 1;
                } else
                    this.totalPages = this.pages.tagCount();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.controlList.clear();
        Keyboard.enableRepeatEvents(true);
        if(this.writable) {
            this.controlList.add(this.signButton = new GuiButton(3, this.width / 2 - 100, 4 + this.backgroundHeight, 98, 20, "Sign"));
            this.controlList.add(this.doneButton = new GuiButton(0, this.width / 2 + 2, 4 + this.backgroundHeight, 98, 20, StringTranslate.getInstance().translateKey("gui.done")));
            this.controlList.add(this.finalizeButton = new GuiButton(5, this.width / 2 - 100, 4 + this.backgroundHeight, 98, 20, "Sign and Close"));
            this.controlList.add(this.cancelButton = new GuiButton(4, this.width / 2 + 2, 4 + this.backgroundHeight, 98, 20, StringTranslate.getInstance().translateKey("gui.cancel")));
        } else
            this.controlList.add(this.doneButton = new GuiButton(0, this.width / 2 - 100, 4 + this.backgroundHeight, 200, 20, StringTranslate.getInstance().translateKey("gui.done")));

        int var1 = (this.width - this.backgroundWidth) / 2;
        byte var2 = 2;
        this.controlList.add(this.nextButton = new GuiPageButton(1, var1 + 120, var2 + 154, true));
        this.controlList.add(this.previousButton = new GuiPageButton(2, var1 + 38, var2 + 154, false));
        this.buttonUpdate();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void buttonUpdate() {
        this.nextButton.enabled2 = !this.signing && (this.currentPage < this.totalPages - 1 || this.writable);
        this.previousButton.enabled2 = !this.signing && (this.currentPage > 0 || this.currentPage > -1 && !writable);
        this.doneButton.enabled2 = !this.writable || !this.signing;
        if(this.writable) {
            this.signButton.enabled2 = !this.signing;
            this.cancelButton.enabled2 = this.signing;
            this.finalizeButton.enabled2 = this.signing;
            this.finalizeButton.enabled = !this.title.trim().isEmpty();
        }

    }

    private void updateBookData(boolean signingBook) {
        if(this.writable && this.modified) {
            if(this.pages != null) {
                while(this.pages.tagCount() > 1) {
                    NBTTagString stringNbt = (NBTTagString)this.pages.tagAt(this.pages.tagCount() - 1);
                    if(stringNbt.stringValue != null && !stringNbt.stringValue.isEmpty())
                        break;

                    BookAndQuill.removeFromNbtList(this.pages, this.pages.tagCount() - 1);
                }

                this.book.getItemData().setTag("pages", this.pages);
                if(signingBook) {
                    this.book.getItemData().setString("author", this.player.username);
                    this.book.getItemData().setString("title", this.title.trim());
                    this.book.itemID = BookAndQuill.writtenBook.shiftedIndex;
                }
            }

        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.enabled) {
            if(button.id == 0) {
                this.mc.displayGuiScreen(null);
                this.updateBookData(false);
            } else if(button.id == 3 && this.writable) {
                this.signing = true;
            } else if(button.id == 1) {
                if(this.currentPage < this.totalPages - 1) {
                    ++this.currentPage;
                } else if(this.writable) {
                    this.newPage();
                    if(this.currentPage < this.totalPages - 1) {
                        ++this.currentPage;
                    }
                }
            } else if(button.id == 2) {
                if(this.currentPage > -1) {
                    --this.currentPage;
                }
            } else if(button.id == 5 && this.signing) {
                this.updateBookData(true);
                this.mc.displayGuiScreen(null);
            } else if(button.id == 4 && this.signing) {
                this.signing = false;
            }

            this.buttonUpdate();
        }
    }

    private void newPage() {
        if(this.pages != null && this.pages.tagCount() < 50) {
            this.pages.setTag(new NBTTagString(""));
            ++this.totalPages;
            this.modified = true;
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if(this.writable) {
            if(this.signing)
                this.typeInSigning(character, keyCode);
            else
                this.typeInBook(character, keyCode);
        }
    }

    private void typeInBook(char character, int keycode) {
        if(character == '\u0016') this.addToContent(GuiScreen.getClipboardString());
        else {
            switch(keycode) {
                case 14:
                    String content = this.getContent();
                    if(!content.isEmpty()) {
                        this.setContent(content.substring(0, content.length() - 1));
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

    private void typeInSigning(char character, int keyCode) {
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

    private String getContent() {
        if(this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.tagCount()) {
            NBTTagString var1 = (NBTTagString)this.pages.tagAt(this.currentPage);
            return var1.stringValue;
        }

        return "";
    }

    private void setContent(String content) {
        if(this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.tagCount()) {
            ((NBTTagString)this.pages.tagAt(this.currentPage)).stringValue = content;
            this.modified = true;
        }
    }

    private void addToContent(String text) {
        String newContent = this.getContent() + text;
        int contentHeight = this.fontRenderer.func_27277_a(newContent + "§0" + "_", 118);
        if(contentHeight <= 118 && newContent.length() < 256) {
            this.setContent(newContent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float tick) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/farn/bookandquill/resource/bookGui.png"));
        int bookX = (this.width - this.backgroundWidth) / 2;
        this.drawTexturedModalRect(bookX, 2, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if(currentPage <= -1 && !writable && !this.signing) {
            String title = this.book.getItemData().getString("title");
            if(title.isEmpty()) {
                title = "No Title";
            }
            String author = this.book.getItemData().getString("author");
            if(author.isEmpty()) {
                author = "Unknown Author";
            }

            int titleWidth = this.fontRenderer.getStringWidth(title);
            this.fontRenderer.drawString(title, bookX + 36 + (116 - titleWidth) / 2, 50, 0);
            String authorFormat = String.format("by %1$s", author);
            int authorWidth = this.fontRenderer.getStringWidth(authorFormat);
            this.fontRenderer.drawString("§8" + authorFormat, bookX + 36 + (116 - authorWidth) / 2, 60, 0);
        } else if(this.signing) {
            String title = this.title;

            if(this.writable) {
                if(this.tick / 6 % 2 == 0) {
                    title = title + "§0" + "_";
                } else {
                    title = title + "§7" + "_";
                }
            }
            String editTitle = "Enter Book Title:";
            int newTitleWidth = this.fontRenderer.getStringWidth(editTitle);
            this.fontRenderer.drawString(editTitle, bookX + 36 + (116 - newTitleWidth) / 2, 34, 0);
            int titleWidth = this.fontRenderer.getStringWidth(title);
            this.fontRenderer.drawString(title, bookX + 36 + (116 - titleWidth) / 2, 50, 0);
            String author = String.format("by %1$s", this.player.username);
            int authorWidth = this.fontRenderer.getStringWidth(author);
            this.fontRenderer.drawString("§8" + author, bookX + 36 + (116 - authorWidth) / 2, 60, 0);
            String warningFinal = "Note! When you sign the book, it will no longer be editable.";
            this.fontRenderer.func_27278_a(warningFinal, bookX + 36, 82, 116, 0);
        } else {
            String pageIndicator = String.format("Page %1$s of %2$s", this.currentPage + 1, this.totalPages);
            String content = "";
            if(this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.tagCount()) {
                content = this.pages.tagAt(this.currentPage).toString();
            }

            if(this.writable) {
                if(this.tick / 6 % 2 == 0) {
                    content = content + "§0" + "_";
                } else {
                    content = content + "§7" + "_";
                }
            }

            int pageIndiWidth = this.fontRenderer.getStringWidth(pageIndicator);
            this.fontRenderer.drawString(pageIndicator, bookX - pageIndiWidth + this.backgroundWidth - 44, 18, 0);
            this.fontRenderer.func_27278_a(content, bookX + 36, 34, 116, 0);
        }

        super.drawScreen(mouseX, mouseY, tick);
    }

    public void updateScreen() {
        super.updateScreen();
        ++this.tick;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }


}
