package farn.bookandquil.mixin;

import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(NbtList.class)
public interface NbtListAccessor {

    @Accessor("value")
    public List getList();

}
