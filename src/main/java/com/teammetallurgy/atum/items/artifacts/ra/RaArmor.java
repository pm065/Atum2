package com.teammetallurgy.atum.items.artifacts.ra;

import com.teammetallurgy.atum.api.God;
import com.teammetallurgy.atum.api.material.AtumArmorMaterials;
import com.teammetallurgy.atum.client.ClientHandler;
import com.teammetallurgy.atum.client.model.armor.RaArmorModel;
import com.teammetallurgy.atum.init.AtumItems;
import com.teammetallurgy.atum.items.artifacts.ArtifactArmor;
import com.teammetallurgy.atum.misc.StackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class RaArmor extends ArtifactArmor {

    public RaArmor(Type type) {
        super(AtumArmorMaterials.NEBU, "ra_armor", type, new Item.Properties().rarity(Rarity.RARE));
        this.setHasRender();
    }

    @Override
    public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return new RaArmorModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientHandler.RA_ARMOR), equipmentSlot, hasFullSet(livingEntity));
            }
        });
    }

    @Override
    public God getGod() {
        return God.RA;
    }

    @Override
    public Item getHelmet() {
        return AtumItems.HALO_OF_RA.get();
    }

    @Override
    public Item getChestplate() {
        return AtumItems.BODY_OF_RA.get();
    }

    @Override
    public Item getLeggings() {
        return AtumItems.LEGS_OF_RA.get();
    }

    @Override
    public Item getBoots() {
        return AtumItems.FEET_OF_RA.get();
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int slot, boolean b) {
        super.inventoryTick(stack, level, entity, slot, b);
        if (entity instanceof LivingEntity livingEntity) {
            if (StackHelper.hasFullArmorSet(livingEntity, AtumItems.HALO_OF_RA.get(), AtumItems.BODY_OF_RA.get(), AtumItems.LEGS_OF_RA.get(), AtumItems.FEET_OF_RA.get())) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0, true, false));
            }
        }
    }
}
