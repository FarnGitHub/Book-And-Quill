package farn.bookandquil.gui;

import farn.bookandquil.BookAndQuill;
import farn.bookandquil.packet.BookContentC2SPacket;
import farn.bookandquil.packet.SigningBookC2SPacket;
import farn.bookandquil.util.MainUtil;
import farn.bookandquil.util.PageFocus;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.CharacterUtils;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class BookScreen extends Screen {
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
    private PageButton nextButton;
    private PageButton previousButton;
    protected ButtonWidget doneButton;
    protected ButtonWidget signButton;
    protected ButtonWidget finalizeButton;
    protected ButtonWidget cancelButton;
    protected PageFocus pageFocus = PageFocus.FIRST;

    public BookScreen(PlayerEntity player, ItemStack book, boolean writable) {
        this.player = player;
        this.book = book;
        this.writable = writable;
        if(!writable) {
            currentPage = -1;
        }

        this.pages = book.getStationNbt().getList("pages").copy();
        int needToAdd = increment(0) - this.pages.size();
        if(needToAdd > 0)
            for(int i = 0; i < needToAdd; ++i)
                this.pages.add(new NbtString(""));
        this.totalPages = this.pages.size();
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

        int xOffset1 = (this.width - this.titleImageWidth) / 2;
        int xOffset2 = (this.width - this.bookImageWidth) / 2;
        this.buttons.add(this.nextButton = new PageButton(1, xOffset1 + 120, 156, true, xOffset2 + 272));
        this.buttons.add(this.previousButton = new PageButton(2, xOffset1 + 38, 156, false, xOffset2 + 48));
        this.buttonUpdate();
    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void buttonUpdate() {
        this.nextButton.visible = !this.signing && (this.currentPage < this.totalPages - 1 || this.writable);
        this.nextButton.updatePosition(this.currentPage <= -1 || MainUtil.classicBook());
        this.previousButton.visible =  !this.signing && (this.currentPage > 0 || this.currentPage > -1 && !writable);
        this.previousButton.updatePosition(this.currentPage <= -1 || MainUtil.classicBook());
        this.doneButton.visible = !this.writable || !this.signing;
        if(this.writable) {
            this.signButton.visible = !this.signing;
            this.cancelButton.visible = this.signing;
            this.finalizeButton.visible = this.signing;
            this.finalizeButton.active = !this.title.trim().isEmpty();
        }

    }

    protected void mouseClicked(int x, int y, int b) {
        if(MainUtil.classicBook()) {
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

    private void updateBookData(boolean signingBook) {
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

                this.book.getStationNbt().put("pages", this.pages);
                if(signingBook) {
                    this.book.getStationNbt().putString("author", this.player.name);
                    this.book.getStationNbt().putString("title", this.title.trim());
                    this.book.itemId = BookAndQuill.WRITTEN_BOOK.id;
                    PacketHelper.send(new SigningBookC2SPacket(player.inventory.selectedSlot, book));
                } else
                    PacketHelper.send(new BookContentC2SPacket(player.inventory.selectedSlot, book));
            }

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
                int currentPage = this.currentPage + (MainUtil.classicBook() ? 0 : 1);
                if (currentPage < this.totalPages - 1) {
                    this.currentPage += increment(this.currentPage);
                } else if(this.writable) {
                    this.newPage(MainUtil.classicBook());
                    if(currentPage < this.totalPages - 1)
                        this.currentPage += increment(this.currentPage);
                }
            } else if(button.id == 2) {
                if(this.currentPage > -1) {
                    this.currentPage -= increment(this.currentPage);
                    if(this.currentPage < -1) {
                        this.currentPage = -1;
                    }
                }
            } else if(button.id == 5 && this.signing) {
                this.updateBookData(true);
                this.minecraft.setScreen(null);
            } else if(button.id == 4 && this.signing) {
                this.signing = false;
            }

            this.buttonUpdate();
        }
    }

    private void newPage(boolean incrementByOne) {
        if(this.pages != null && this.pages.size() < 50) {
            int increment = incrementByOne ? 1 : increment(this.totalPages);
            for(int i = 0; i < increment; ++i)
                this.pages.add(new NbtString(""));
            this.totalPages += increment;
            this.modified = true;
        }
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

    private void typeInBook(PageFocus pager, char character, int keycode) {
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

    private String getContent(PageFocus focus) {
        int currentPage = this.currentPage + focus.focusVal;
        if(this.pages.size() >= 0 && currentPage < this.pages.size() && focus != PageFocus.UNFOCUS) {
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

    @Override
    public void render(int mouseX, int mouseY, float tick) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if(currentPage <= -1 && !writable && !this.signing) {
            int textColor = MainUtil.classicBook() ? 0 : -1;
            int bookX = (this.width - this.titleImageWidth) / 2;
            bindTitleBookTexture(bookX);
            String title = this.book.getStationNbt().getString("title");
            if(title.isEmpty()) {
                title = MainUtil.translate("book.no.title");
            }
            String author = this.book.getStationNbt().getString("author");
            if(author.isEmpty()) {
                author = MainUtil.translate("book.unknown.author");
            }

            int titleWidth = this.textRenderer.getWidth(title);
            this.textRenderer.draw(title, bookX + 36 + (116 - titleWidth) / 2, 50, textColor);
            String authorFormat = MainUtil.translate("book.byAuthor", author);
            int authorWidth = this.textRenderer.getWidth(authorFormat);
            this.textRenderer.draw(authorFormat, bookX + 36 + (116 - authorWidth) / 2, 60, textColor);
        } else if(this.signing) {
            int textColor = MainUtil.classicBook() ? 0 : -1;
            int bookX = (this.width - this.titleImageWidth) / 2;
            bindTitleBookTexture(bookX);
            this.drawTexture(bookX, 2, 0, 0, this.titleImageWidth, this.imageHeight);
            String title = this.title;

            if(this.writable) {
                if(this.tick / 6 % 2 == 0) {
                    title = title + "§0" + "_";
                } else {
                    title = title + "§7" + "_";
                }
            }
            String editTitle = MainUtil.translate("book.editTitle");
            int newTitleWidth = this.textRenderer.getWidth(editTitle);
            this.textRenderer.draw(editTitle, bookX + 36 + (116 - newTitleWidth) / 2, 34, textColor);
            int titleWidth = this.textRenderer.getWidth(title);
            this.textRenderer.draw(title, bookX + 36 + (116 - titleWidth) / 2, 50, textColor);
            String author = String.format(MainUtil.translate("book.byAuthor"), this.player.name);
            int authorWidth = this.textRenderer.getWidth(author);
            this.textRenderer.draw(author, bookX + 36 + (116 - authorWidth) / 2, 60, textColor);
            String warningFinal = MainUtil.translate("book.finalizeWarning");
            this.textRenderer.drawSplit(warningFinal, bookX + 36, 82, 116, textColor);
        } else {
            if(MainUtil.classicBook()) {
                renderSingleBook();
            } else {
                renderDoubleBook();
            }
        }

        super.render(mouseX, mouseY, tick);
    }

    public void tick() {
        super.tick();
        ++this.tick;
    }

    @Override
    public boolean shouldPause() {
        return MainUtil.shouldPause();
    }

    public void bindTitleBookTexture(int bookX) {
        String tex = MainUtil.classicBook() ? "/assets/bookandquill/gui/legacy_book.png" : "/assets/bookandquill/gui/title_book.png";
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId(tex));
        this.drawTexture(bookX, 2, 0, 0, this.titleImageWidth, this.imageHeight);
    }

    public void renderSingleBook() {
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId("/assets/bookandquill/gui/legacy_book.png"));
        int bookX = (this.width - this.titleImageWidth) / 2;
        this.drawTexture(bookX, 2, 0, 0, this.titleImageWidth, this.imageHeight);
        String pageIndicator = String.format(MainUtil.translate("book.pageIndicator"), this.currentPage + 1, this.totalPages);
        String content = "";
        if(this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            content = getContent(PageFocus.FIRST);
        }

        if(this.writable) {
            if(this.tick / 6 % 2 == 0) {
                content = content + "§0" + "_";
            } else {
                content = content + "§7" + "_";
            }
        }

        int pageIndiWidth = this.textRenderer.getWidth(pageIndicator);
        this.textRenderer.draw(pageIndicator, bookX - pageIndiWidth + this.titleImageWidth - 44, 18, 0);
        this.textRenderer.drawSplit(content, bookX + 36, 34, 116, 0);
    }

    public void renderDoubleBook() {
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId("/assets/bookandquill/gui/double_book.png"));
        int bookX = (this.width - this.bookImageWidth) / 2;
        this.drawTextureAlt(bookX, 2, 0, 0, this.bookImageWidth, this.imageHeight);
        int total = getTotalDisplay();
        String pageIndicator1 = MainUtil.translate("book.pageIndicator", this.currentPage + 1, total);
        String pageIndicator2 = MainUtil.translate("book.pageIndicator", this.currentPage + 2, total);
        String content1 = "";
        if(this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            content1 = getContent(PageFocus.FIRST);
        }

        String content2 = "";
        if(this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            content2 = getContent(PageFocus.SECOND);
        }

        if(this.writable) {
            String underscore = this.tick / 6 % 2 == 0 ?
                    ("§0" + "_") : ("§7" + "_");
            if(pageFocus == PageFocus.SECOND) {
                content2 = content2 + underscore;
            } else if(pageFocus == PageFocus.FIRST) {
                content1 = content1 + underscore;
            }
        }

        int pageIndiWidth2 = this.textRenderer.getWidth(pageIndicator2);
        this.textRenderer.draw(pageIndicator1, bookX + 44, 18, 0);
        this.textRenderer.drawSplit(content1, bookX + 42, 34, 116, 0);
        this.textRenderer.draw(pageIndicator2, bookX - pageIndiWidth2 + this.bookImageWidth - 44, 18, 0);
        this.textRenderer.drawSplit(content2, bookX + this.bookImageWidth - 156, 34, 116, 0);
    }

    public void drawTextureAlt(int x, int y, int u, int v, int width, int height) {
        float var7 = 0.0029296875F;
        float var8 = 0.00520833333F;
        Tessellator tess = Tessellator.INSTANCE;
        tess.startQuads();
        tess.vertex(x, y + height, this.zOffset, (float)(u) * var7, (float)(v + height) * var8);
        tess.vertex(x + width, y + height, this.zOffset, (float)(u + width) * var7, (float)(v + height) * var8);
        tess.vertex(x + width, y, this.zOffset, (float)(u + width) * var7, (float)(v) * var8);
        tess.vertex(x, y, this.zOffset, (float)(u) * var7, (float)(v) * var8);
        tess.draw();
    }

    public static int increment(int integer) {
        return (integer & 1) == 1 || MainUtil.classicBook() ? 1 : 2;
    }

    public int getTotalDisplay() {
         return Math.max(
                 increment(0),
                 (totalPages & 1) == 1 && !MainUtil.classicBook() ?
                 this.totalPages + 1 : totalPages
         );
    }
}