package farn.bookandquil.item.gui;

import farn.bookandquil.BookAndQuil;
import farn.bookandquil.item.writable_book.WritableBookServerPacket;
import farn.bookandquil.item.written_book.WrittenBookServerPacket;
import farn.bookandquil.mixin.NbtListAccessor;
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

public class BookScreen extends Screen {
    private final PlayerEntity editingPlayer;
    private final ItemStack itemstackBook;
    private final boolean bookIsUnsigned;
    private boolean bookModified;
    private boolean editingTitle;
    private int updateCount;
    private int bookImageWidth = 192;
    private int bookImageHeight = 192;
    private int bookTotalPages = 1;
    private int currPage = 0;
    private NbtList bookPages;
    private String bookTitle = "";
    private ButtonWidgetPage buttonNextPage;
    private ButtonWidgetPage buttonPreviousPage;
    private ButtonWidget buttonDone;
    private ButtonWidget buttonSign;
    private ButtonWidget buttonFinalize;
    private ButtonWidget buttonCancel;

    public BookScreen(PlayerEntity var1, ItemStack var2, boolean var3) {
        this.editingPlayer = var1;
        this.itemstackBook = var2;
        this.bookIsUnsigned = var3;
        if(var2.getStationNbt() != null) {
            NbtCompound var4 = var2.getStationNbt();
            this.bookPages = var4.getList("pages");
            if(this.bookPages != null) {
                this.bookPages = (NbtList) this.bookPages.copy();
                this.bookTotalPages = this.bookPages.size();
                if(this.bookTotalPages < 1) {
                    this.bookTotalPages = 1;
                }
            }
        }

        if(this.bookPages == null && var3) {
            this.bookPages = new NbtList();
            this.bookPages.add(new NbtString(""));
            this.bookTotalPages = 1;
        }

    }

    public void tick() {
        super.tick();
        ++this.updateCount;
    }

    public void init() {
        this.buttons.clear();
        Keyboard.enableRepeatEvents(true);
        if(this.bookIsUnsigned) {
            this.buttons.add(this.buttonSign = new ButtonWidget(3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, BookAndQuil.getTranslatedKey("book.signButton")));
            this.buttons.add(this.buttonDone = new ButtonWidget(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, TranslationStorage.getInstance().get("gui.done")));
            this.buttons.add(this.buttonFinalize = new ButtonWidget(5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, BookAndQuil.getTranslatedKey("book.finalizeButton")));
            this.buttons.add(this.buttonCancel = new ButtonWidget(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, TranslationStorage.getInstance().get("gui.cancel")));
        } else {
            this.buttons.add(this.buttonDone = new ButtonWidget(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, TranslationStorage.getInstance().get("gui.done")));
        }

        int var1 = (this.width - this.bookImageWidth) / 2;
        byte var2 = 2;
        this.buttons.add(this.buttonNextPage = new ButtonWidgetPage(1, var1 + 120, var2 + 154, true));
        this.buttons.add(this.buttonPreviousPage = new ButtonWidgetPage(2, var1 + 38, var2 + 154, false));
        this.updateButtons();
    }

    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void updateButtons() {
        this.buttonNextPage.visible = !this.editingTitle && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
        this.buttonPreviousPage.visible = !this.editingTitle && this.currPage > 0;
        this.buttonDone.visible = !this.bookIsUnsigned || !this.editingTitle;
        if(this.bookIsUnsigned) {
            this.buttonSign.visible = !this.editingTitle;
            this.buttonCancel.visible = this.editingTitle;
            this.buttonFinalize.visible = this.editingTitle;
            this.buttonFinalize.active = this.bookTitle.trim().length() > 0;
        }

    }

    private void sendBookToServer(boolean var1) {
        if(this.bookIsUnsigned && this.bookModified) {
            if(this.bookPages != null) {
                while(this.bookPages.size() > 1) {
                    NbtString var2 = (NbtString)this.bookPages.get(this.bookPages.size() - 1);
                    if(var2.value != null && !var2.value.isEmpty()) {
                        break;
                    }

                    ((NbtListAccessor)this.bookPages).getList().remove(this.bookPages.size() - 1);
                }

                if(this.itemstackBook.getStationNbt() != null) {
                    NbtCompound var7 = this.itemstackBook.getStationNbt();
                    var7.put("pages", this.bookPages);
                } else {
                    this.itemstackBook.getStationNbt().put("pages", this.bookPages);
                }

                String var8 = "MC|BEdit";
                if(var1) {
                    var8 = "MC|BSign";
                    this.itemstackBook.getStationNbt().put("author", new NbtString(this.editingPlayer.name));
                    this.itemstackBook.getStationNbt().put("title", new NbtString(this.bookTitle.trim()));
                    this.itemstackBook.itemId = BookAndQuil.WRITTEN_BOOK.id;
                }

                NbtCompound finalItemStack = new NbtCompound();
                finalItemStack = this.itemstackBook.writeNbt(finalItemStack);


                try {
                    if(var8.equals("MC|BEdit")) {
                        PacketHelper.send(new WritableBookServerPacket(editingPlayer.inventory.selectedSlot, finalItemStack));
                    } else if(var8.equals("MC|BSign")) {
                        PacketHelper.send(new WrittenBookServerPacket(editingPlayer.inventory.selectedSlot, finalItemStack));
                    }
                } catch (Exception var6) {
                    var6.printStackTrace();
                }
            }

        }
    }

    protected void buttonClicked(ButtonWidget var1) {
        if(var1.active) {
            if(var1.id == 0) {
                this.minecraft.setScreen((Screen) null);
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
                this.minecraft.setScreen((Screen) null);
            } else if(var1.id == 4 && this.editingTitle) {
                this.editingTitle = false;
            }

            this.updateButtons();
        }
    }

    private void addNewPage() {
        if(this.bookPages != null && this.bookPages.size() < 50) {
            this.bookPages.add(new NbtString(""));
            ++this.bookTotalPages;
            this.bookModified = true;
        }
    }

    protected void keyPressed(char var1, int var2) {
        super.keyPressed(var1, var2);
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
                this.func_74160_b(Screen.getClipboard());
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
                        if(CharacterUtils.VALID_CHARACTERS.indexOf(var1) >= 0) {
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
                    this.minecraft.setScreen((Screen)null);
                }

                return;
            default:
                if(this.bookTitle.length() < 16 && CharacterUtils.VALID_CHARACTERS.indexOf(var1) >= 0) {
                    this.bookTitle = this.bookTitle + Character.toString(var1);
                    this.updateButtons();
                    this.bookModified = true;
                }
        }
    }

    private String func_74158_i() {
        if(this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.size()) {
            NbtString var1 = (NbtString)this.bookPages.get(this.currPage);
            return var1.value;
        } else {
            return "";
        }
    }

    private void func_74159_a(String var1) {
        this.ensureCurrentPageExists();
        if(this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.size()) {
            NbtString var2 = (NbtString)this.bookPages.get(this.currPage);
            var2.value = var1;
            this.bookModified = true;
        }

    }

    private void ensureCurrentPageExists() {
        if (this.bookPages == null) {
            this.bookPages = new NbtList();
        }
        while (this.currPage >= this.bookPages.size()) {
            this.bookPages.add(new NbtString(""));
            ++this.bookTotalPages;
        }
    }

    private void func_74160_b(String var1) {
        String var2 = this.func_74158_i();
        String var3 = var2 + var1;
        int var4 = this.textRenderer.splitAndGetHeight(var3 + "§0" + "_", 118);
        if(var4 <= 118 && var3.length() < 256) {
            this.func_74159_a(var3);
        }

    }

    public void render(int var1, int var2, float var3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bindTexture(this.minecraft.textureManager.getTextureId("/gui/book.png"));
        int var4 = (this.width - this.bookImageWidth) / 2;
        byte var5 = 2;
        this.drawTexture(var4, var5, 0, 0, this.bookImageWidth, this.bookImageHeight);
        String var6;
        String var7;
        int var8;
        if(this.editingTitle) {
            var6 = this.bookTitle;
            if(this.bookIsUnsigned) {
                if(this.updateCount / 6 % 2 == 0) {
                    var6 = var6 + "§0" + "_";
                } else {
                    var6 = var6 + "§7" + "_";
                }
            }

            var7 = BookAndQuil.getTranslatedKey("book.editTitle");
            var8 = this.textRenderer.getWidth(var7);
            this.textRenderer.draw(var7, var4 + 36 + (116 - var8) / 2, var5 + 16 + 16, 0);
            int var9 = this.textRenderer.getWidth(var6);
            this.textRenderer.draw(var6, var4 + 36 + (116 - var9) / 2, var5 + 48, 0);
            String var10 = String.format(BookAndQuil.getTranslatedKey("book.byAuthor"), new Object[]{this.editingPlayer.name});
            int var11 = this.textRenderer.getWidth(var10);
            this.textRenderer.draw("§8" + var10, var4 + 36 + (116 - var11) / 2, var5 + 48 + 10, 0);
            String var12 = BookAndQuil.getTranslatedKey("book.finalizeWarning");
            this.textRenderer.drawSplit(var12, var4 + 36, var5 + 80, 116, 0);
        } else {
            var6 = String.format(BookAndQuil.getTranslatedKey("book.pageIndicator"), new Object[]{Integer.valueOf(this.currPage + 1), Integer.valueOf(this.bookTotalPages)});
            var7 = "";
            if(this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.size()) {
                NbtString var13 = (NbtString)this.bookPages.get(this.currPage);
                var7 = var13.toString();
            }

            var8 = this.textRenderer.getWidth(var6);
            this.textRenderer.draw(var6, var4 - var8 + this.bookImageWidth - 44, var5 + 16, 0);
            this.textRenderer.drawSplit(var7, var4 + 36, var5 + 16 + 16, 116, 0);
        }

        super.render(var1, var2, var3);
    }
}
