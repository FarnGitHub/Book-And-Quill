package farn.bookandquil.gui;

import farn.bookandquil.BookAndQuil;
import farn.bookandquil.packet.BookContentC2SPacket;
import farn.bookandquil.packet.SigningBookC2SPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
    private final PlayerEntity player;
    private final ItemStack book;
    private final boolean writable;
    private boolean modified;
    private boolean signing;
    private int tick;
    private final int backgroundWidth = 192;
    private final int backgroundHeight = 192;
    private int totalPages = 1;
    private int currentPage = 0;
    private NbtList pages;
    private String title = "";
    private PageButton nextButton;
    private PageButton previousButton;
    private ButtonWidget doneButton;
    private ButtonWidget signButton;
    private ButtonWidget finalizeButton;
    private ButtonWidget cancelButton;

    public BookScreen(PlayerEntity player, ItemStack book, boolean writable) {
        this.player = player;
        this.book = book;
        this.writable = writable;
        if(!writable) {
            currentPage = -1;
        }

        if(book.getStationNbt() != null) {
            this.pages = book.getStationNbt().getList("pages");
            if(this.pages != null) {
                this.pages = this.pages.copy();
                if(this.pages.value.isEmpty()) {
                    this.pages.add(new NbtString(""));
                    this.totalPages = 1;
                } else
                    this.totalPages = this.pages.size();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        this.buttons.clear();
        Keyboard.enableRepeatEvents(true);
        if(this.writable) {
            this.buttons.add(this.signButton = new ButtonWidget(3, this.width / 2 - 100, 4 + this.backgroundHeight, 98, 20, BookAndQuil.translate("book.signButton")));
            this.buttons.add(this.doneButton = new ButtonWidget(0, this.width / 2 + 2, 4 + this.backgroundHeight, 98, 20, TranslationStorage.getInstance().get("gui.done")));
            this.buttons.add(this.finalizeButton = new ButtonWidget(5, this.width / 2 - 100, 4 + this.backgroundHeight, 98, 20, BookAndQuil.translate("book.finalizeButton")));
            this.buttons.add(this.cancelButton = new ButtonWidget(4, this.width / 2 + 2, 4 + this.backgroundHeight, 98, 20, TranslationStorage.getInstance().get("gui.cancel")));
        } else
            this.buttons.add(this.doneButton = new ButtonWidget(0, this.width / 2 - 100, 4 + this.backgroundHeight, 200, 20, TranslationStorage.getInstance().get("gui.done")));

        int var1 = (this.width - this.backgroundWidth) / 2;
        byte var2 = 2;
        this.buttons.add(this.nextButton = new PageButton(1, var1 + 120, var2 + 154, true));
        this.buttons.add(this.previousButton = new PageButton(2, var1 + 38, var2 + 154, false));
        this.buttonUpdate();
    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void buttonUpdate() {
        this.nextButton.visible = !this.signing && (this.currentPage < this.totalPages - 1 || this.writable);
        this.previousButton.visible = !this.signing && (this.currentPage > 0 || this.currentPage > -1 && !writable);
        this.doneButton.visible = !this.writable || !this.signing;
        if(this.writable) {
            this.signButton.visible = !this.signing;
            this.cancelButton.visible = this.signing;
            this.finalizeButton.visible = this.signing;
            this.finalizeButton.active = !this.title.trim().isEmpty();
        }

    }

    private void updateBookData(boolean signingBook) {
        if(this.writable && this.modified) {
            if(this.pages != null) {
                while(this.pages.size() > 1) {
                    NbtString stringNbt = (NbtString)this.pages.get(this.pages.size() - 1);
                    if(stringNbt.value != null && !stringNbt.value.isEmpty())
                        break;

                    this.pages.value.remove(this.pages.size() - 1);
                }

                this.book.getStationNbt().put("pages", this.pages);
                if(signingBook) {
                    this.book.getStationNbt().putString("author", this.player.name);
                    this.book.getStationNbt().putString("title", this.title.trim());
                    this.book.itemId = BookAndQuil.WRITTEN_BOOK.id;
                    PacketHelper.send(new SigningBookC2SPacket(player.inventory.selectedSlot, book));
                } else
                    PacketHelper.send(new BookContentC2SPacket(player.inventory.selectedSlot, book));
            }

        }
    }

    @Override
    protected void buttonClicked(ButtonWidget var1) {
        if(var1.active) {
            if(var1.id == 0) {
                this.minecraft.setScreen(null);
                this.updateBookData(false);
            } else if(var1.id == 3 && this.writable) {
                this.signing = true;
            } else if(var1.id == 1) {
                if(this.currentPage < this.totalPages - 1) {
                    ++this.currentPage;
                } else if(this.writable) {
                    this.newPage();
                    if(this.currentPage < this.totalPages - 1) {
                        ++this.currentPage;
                    }
                }
            } else if(var1.id == 2) {
                if(this.currentPage > -1) {
                    --this.currentPage;
                }
            } else if(var1.id == 5 && this.signing) {
                this.updateBookData(true);
                this.minecraft.setScreen(null);
            } else if(var1.id == 4 && this.signing) {
                this.signing = false;
            }

            this.buttonUpdate();
        }
    }

    private void newPage() {
        if(this.pages != null && this.pages.size() < 50) {
            this.pages.add(new NbtString(""));
            ++this.totalPages;
            this.modified = true;
        }
    }

    @Override
    protected void keyPressed(char var1, int var2) {
        super.keyPressed(var1, var2);
        if(this.writable) {
            if(this.signing)
                this.typeInSigning(var1, var2);
            else
                this.typeInBook(var1, var2);
        }
    }

    private void typeInBook(char character, int keycode) {
        if(character == '\u0016') this.addToContent(Screen.getClipboard());
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

    private String getContent() {
        if(this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            NbtString var1 = (NbtString)this.pages.get(this.currentPage);
            return var1.value;
        }

        return "";
    }

    private void setContent(String content) {
        if(this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            ((NbtString)this.pages.get(this.currentPage)).value = content;
            this.modified = true;
        }
    }

    private void addToContent(String text) {
        String newContent = this.getContent() + text;
        int contentHeight = this.textRenderer.splitAndGetHeight(newContent + "§0" + "_", 118);
        if(contentHeight <= 118 && newContent.length() < 256) {
            this.setContent(newContent);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float tick) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId("/farn/bookandquill/gui/book.png"));
        int bookX = (this.width - this.backgroundWidth) / 2;
        this.drawTexture(bookX, 2, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if(currentPage <= -1 && !writable && !this.signing) {
            String title = this.book.getStationNbt().getString("title");
            if(title.isEmpty()) {
                title = "No Title";
            }
            String author = this.book.getStationNbt().getString("author");
            if(author.isEmpty()) {
                author = "Unknown Author";
            }

            int titleWidth = this.textRenderer.getWidth(title);
            this.textRenderer.draw(title, bookX + 36 + (116 - titleWidth) / 2, 50, 0);
            String authorFormat = String.format(BookAndQuil.translate("book.byAuthor"), author);
            int authorWidth = this.textRenderer.getWidth(authorFormat);
            this.textRenderer.draw("§8" + authorFormat, bookX + 36 + (116 - authorWidth) / 2, 60, 0);
        } else if(this.signing) {
            String title = this.title;

            if(this.writable) {
                if(this.tick / 6 % 2 == 0) {
                    title = title + "§0" + "_";
                } else {
                    title = title + "§7" + "_";
                }
            }
            String editTitle = BookAndQuil.translate("book.editTitle");
            int newTitleWidth = this.textRenderer.getWidth(editTitle);
            this.textRenderer.draw(editTitle, bookX + 36 + (116 - newTitleWidth) / 2, 34, 0);
            int titleWidth = this.textRenderer.getWidth(title);
            this.textRenderer.draw(title, bookX + 36 + (116 - titleWidth) / 2, 50, 0);
            String author = String.format(BookAndQuil.translate("book.byAuthor"), this.player.name);
            int authorWidth = this.textRenderer.getWidth(author);
            this.textRenderer.draw("§8" + author, bookX + 36 + (116 - authorWidth) / 2, 60, 0);
            String warningFinal = BookAndQuil.translate("book.finalizeWarning");
            this.textRenderer.drawSplit(warningFinal, bookX + 36, 82, 116, 0);
        } else {
            String pageIndicator = String.format(BookAndQuil.translate("book.pageIndicator"), this.currentPage + 1, this.totalPages);
            String content = "";
            if(this.pages != null && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
                content = this.pages.get(this.currentPage).toString();
            }

            if(this.writable) {
                if(this.tick / 6 % 2 == 0) {
                    content = content + "§0" + "_";
                } else {
                    content = content + "§7" + "_";
                }
            }

            int pageIndiWidth = this.textRenderer.getWidth(pageIndicator);
            this.textRenderer.draw(pageIndicator, bookX - pageIndiWidth + this.backgroundWidth - 44, 18, 0);
            this.textRenderer.drawSplit(content, bookX + 36, 34, 116, 0);
        }

        super.render(mouseX, mouseY, tick);
    }

    public void tick() {
        super.tick();
        ++this.tick;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}