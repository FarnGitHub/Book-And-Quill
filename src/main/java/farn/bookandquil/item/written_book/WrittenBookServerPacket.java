package farn.bookandquil.item.written_book;

import farn.bookandquil.BookAndQuil;
import farn.bookandquil.item.writable_book.WritableBookItem;
import farn.bookandquil.mixin.NbtCompoundAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WrittenBookServerPacket extends Packet
        implements ManagedPacket<WrittenBookServerPacket> {

    public static final PacketType<WrittenBookServerPacket> TYPE =
            PacketType.builder(false, true, WrittenBookServerPacket::new).build();

    public int slot;
    public NbtCompound itemstackCompound;
    private int length;

    public WrittenBookServerPacket() {
    }

    public WrittenBookServerPacket(int slot, NbtCompound tag) {
        this.slot = slot;
        this.itemstackCompound = tag;
    }

    @Override
    public void read(DataInputStream in) {
        try {
            slot = in.readInt();
            itemstackCompound = new NbtCompound();
            ((NbtCompoundAccessor)itemstackCompound).bookandquill_read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream out) {
        try {
            out.writeInt(slot);
            DataOutputStream outputStream = new DataOutputStream(out);
            ((NbtCompoundAccessor)itemstackCompound).bookandquill_write(outputStream);
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
        return 4 + 2 + length * 2;
    }

    @Override
    public void apply(NetworkHandler handler) {
        SideUtil.run(
                () -> handleClient(handler),
                () -> handleServer(handler)
        );
    }

    @Environment(EnvType.CLIENT)
    public void handleClient(NetworkHandler handler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(handler);
    }

    @Environment(EnvType.SERVER)
    public void handleServer(NetworkHandler handler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(handler);
        ItemStack stack = player.inventory.getStack(slot);
        ItemStack newItem = new ItemStack(itemstackCompound);
        if (stack != null && stack.getItem() instanceof WritableBookItem && newItem.getItem() instanceof WrittenBookItem) {
            if(BookAndQuil.hasSomeWriting(newItem.getStationNbt())) {
                stack.getStationNbt().putString("author", player.name);
                stack.getStationNbt().putString("title", newItem.getStationNbt().getString("title"));
                stack.getStationNbt().put("pages", newItem.getStationNbt().getList("pages"));
                stack.itemId = newItem.itemId;
            }
        }
    }

    @Override
    public PacketType<WrittenBookServerPacket> getType() {
        return TYPE;
    }
}