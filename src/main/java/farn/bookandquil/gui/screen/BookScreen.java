package farn.bookandquil.gui.screen;

import farn.bookandquil.BookAndQuill;
import farn.bookandquil.gui.PageButton;
import farn.bookandquil.packet.BookContentC2SPacket;
import farn.bookandquil.packet.SigningBookC2SPacket;
import farn.bookandquil.util.MainUtil;
import farn.bookandquil.util.PageFocus;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.CharacterUtils;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public abstract class BookScreen extends Screen {
    protected final PlayerEntity player;
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
    protected NbtList pages;
    protected String title = "";
    protected PageButton nextButton;
    protected PageButton previousButton;
    protected ButtonWidget doneButton;
    protected ButtonWidget signButton;
    protected ButtonWidget finalizeButton;
    protected ButtonWidget cancelButton;
    protected PageFocus pageFocus = PageFocus.FIRST;
    protected int bookTitleCenterX = 0;
    protected int bookDoubleCenterX = 0;
    protected String underscore = "§0_";

    protected BookScreen(PlayerEntity player, ItemStack book, boolean writable) {
        this.player = player;
        this.book = book;
        this.writable = writable;
        if(!writable) {
            currentPage = -1;
        }

        this.pages = this.getBookData().getList("pages").copy();
        int needToAdd = increment(0) - this.pages.size();
        if(needToAdd > 0) {
            for (int i = 0; i < needToAdd; ++i)
                this.pages.add(new NbtString(""));
        } else if(!singlePage() && (this.pages.size() & 1) == 1)
            this.pages.add(new NbtString(""));
        this.totalPages = this.pages.size();
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

    public NbtCompound getBookData() {
        return this.book.getStationNbt();
    }

    private void buttonUpdate() {
        this.nextButton.visible = !this.signing && (this.currentPage < this.totalPages - 1 || this.writable);
        this.nextButton.updatePosition(this.currentPage <= -1 || singlePage());
        this.previousButton.visible =  !this.signing && (this.currentPage > 0 || this.currentPage > -1 && !writable);
        this.previousButton.updatePosition(this.currentPage <= -1 || singlePage());
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
            int increment = increment(this.totalPages);
            for(int i = 0; i < increment; ++i)
                this.pages.add(new NbtString(""));
            this.totalPages += increment;
            this.modified = true;
        }
    }

    protected void typeInBook(PageFocus pager, char character, int keycode) {
        if(character == '\u0016') this.addToContent(Screen.getClipboard());
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
                    if(CharacterUtils.VALID_CHARACTERS.indexOf(character) >= 0) {
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
                    this.minecraft.setScreen(null);
                }

                return;
            default:
                if(this.title.length() < 16 && CharacterUtils.VALID_CHARACTERS.indexOf(character) >= 0) {
                    this.title = this.title + character;
                    this.buttonUpdate();
                    this.modified = true;
                }
        }
    }

    protected String getContent(PageFocus focus) {
        int currentPage = this.currentPage + focus.focusVal;
        if(this.pages != null && this.pages.size() >= 0 && currentPage < this.pages.size() && focus != PageFocus.UNFOCUS) {
            if(this.pages.get(currentPage) instanceof NbtString string) {
                return string.value;
            }
        }

        return "";
    }

    private void setContent(PageFocus focus, String content) {
        int currentPage = this.currentPage + focus.focusVal;
        if(this.pages != null && currentPage >= 0 && currentPage < this.pages.size()) {
            ((NbtString)this.pages.get(currentPage)).value = content;
            this.modified = true;
        }
    }

    private void addToContent(String text) {
        String newContent = this.getContent(pageFocus) + text;
        int contentHeight = this.textRenderer.splitAndGetHeight(newContent + "§0" + "_", 118);
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
    public void init() {
        this.buttons.clear();
        Keyboard.enableRepeatEvents(true);
        if(this.writable) {
            this.buttons.add(this.signButton = new ButtonWidget(3, this.width / 2 - 100, 4 + this.imageHeight, 98, 20, MainUtil.translate("book.signButton")));
            this.buttons.add(this.doneButton = new ButtonWidget(0, this.width / 2 + 2, 4 + this.imageHeight, 98, 20, TranslationStorage.getInstance().get("gui.done")));
            this.buttons.add(this.finalizeButton = new ButtonWidget(5, this.width / 2 - 100, 4 + this.imageHeight, 98, 20, MainUtil.translate("book.finalizeButton")));
            this.buttons.add(this.cancelButton = new ButtonWidget(4, this.width / 2 + 2, 4 + this.imageHeight, 98, 20, TranslationStorage.getInstance().get("gui.cancel")));
        } else
            this.buttons.add(this.doneButton = new ButtonWidget(0, this.width / 2 - 100, 4 + this.imageHeight, 200, 20, TranslationStorage.getInstance().get("gui.done")));

        this.bookTitleCenterX = (this.width - this.titleImageWidth) / 2;
        this.bookDoubleCenterX = (this.width - this.bookImageWidth) / 2;
        this.buttons.add(this.nextButton = new PageButton(1, this.bookTitleCenterX + 120, 156, true, this.bookDoubleCenterX + 272, this));
        this.buttons.add(this.previousButton = new PageButton(2, this.bookTitleCenterX + 38, 156, false, this.bookDoubleCenterX + 48, this));
        this.buttonUpdate();
    }

    @Override
    public void render(int mouseX, int mouseY, float tick) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(currentPage <= -1 && !writable && !this.signing)
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
        if(++this.tick / 6 % 2 == 0)
            this.underscore = "§0_";
        else
            this.underscore = "§7_";
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
                this.typeInBook(pageFocus, character, keyCode);
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if(button.visible && button.active) {
            if(button.id == 0) {
                this.minecraft.setScreen(null);
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
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }
}