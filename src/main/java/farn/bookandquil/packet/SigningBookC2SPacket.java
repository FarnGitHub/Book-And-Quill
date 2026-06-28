package farn.bookandquil.packet;

import farn.bookandquil.BookAndQuill;
import farn.bookandquil.util.MainUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SigningBookC2SPacket extends Packet
        implements ManagedPacket<SigningBookC2SPacket> {

    public static final PacketType<SigningBookC2SPacket> TYPE =
            PacketType.builder(false, true, SigningBookC2SPacket::new).build();

    public int slot;
    public String title;
    public NbtList list;
    private int length;

    public SigningBookC2SPacket() {
    }

    public SigningBookC2SPacket(int slot, ItemStack stack) {
        this.slot = slot;
        this.title = stack.getStationNbt().getString("title");
        this.list = stack.getStationNbt().getList("pages");
    }

    @Override
    public void read(DataInputStream in) {
        try {
            slot = in.readInt();
            title = in.readUTF();
            list = new NbtList();
            list.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream out) {
        try {
            out.writeInt(slot);
            out.writeUTF(title);
            DataOutputStream outputStream = new DataOutputStream(out);
            list.write(outputStream);
            try {
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            length = outputStream.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        return length;
    }

    @Override
    public void apply(NetworkHandler handler) {
        SideUtil.run(
                () -> {},
                () -> handleServer(handler)
        );
    }

    @Environment(EnvType.SERVER)
    public void handleServer(NetworkHandler handler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(handler);
        ItemStack stack = player.inventory.getStack(slot);
        if (stack != null && stack.itemId == BookAndQuill.BOOK_AND_QUILL.id
                && MainUtil.validContent(list)) {
            stack.getStationNbt().putString("author", player.name);
            stack.getStationNbt().putString("title", title);
            stack.getStationNbt().put("pages", list);
            stack.itemId = BookAndQuill.WRITTEN_BOOK.id;
        }
    }

    @NotNull
    @Override
    public PacketType<SigningBookC2SPacket> getType() {
        return TYPE;
    }
}