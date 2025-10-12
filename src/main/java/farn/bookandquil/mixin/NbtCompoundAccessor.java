package farn.bookandquil.mixin;

import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.DataInput;
import java.io.DataOutput;

@Mixin(NbtCompound.class)
public interface NbtCompoundAccessor {

    @Invoker("read")
    void bookandquill_read(DataInput input);

    @Invoker("write")
    void bookandquill_write(DataOutput output);
}
