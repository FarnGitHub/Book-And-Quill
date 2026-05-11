package farn.bookandquil.mixin;

import farn.bookandquil.recipe.BookCloning;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingRecipeManager.class)
public class CraftRecipeManagerMixin {

    @Inject(method="craft", at = @At("HEAD"), cancellable = true)
    public void bookandquill_craftCopy(CraftingInventory inv, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = BookCloning.getCraftingResult(inv);
        if(stack != null)
            cir.setReturnValue(stack);
    }
}
