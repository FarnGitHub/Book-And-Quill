package farn.bookandquill.unimined;

import farn.bookandquill.BookAndQuill;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.ItemStack;

@SuppressWarnings("unused")
@CTransformer(CraftingManager.class)
public class CraftingManagerTransformer {

    @CInject(method="findMatchingRecipe(Lnet/minecraft/src/InventoryCrafting;)Lnet/minecraft/src/ItemStack;", target = @CTarget("HEAD"), cancellable = true)
    public void bookandquill_hijackRecipe(InventoryCrafting matrix, InjectionCallback callback) {
        if(!BookAndQuill.canDoBookCopy) return;
        ItemStack stack = BookAndQuill.getCopyableBook(matrix);
        if(stack != null) callback.setReturnValue(stack);
    }
}
