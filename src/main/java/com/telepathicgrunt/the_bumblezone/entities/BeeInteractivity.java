package com.telepathicgrunt.the_bumblezone.entities;

import com.telepathicgrunt.the_bumblezone.configs.BzBeeAggressionConfigs;
import com.telepathicgrunt.the_bumblezone.effects.WrathOfTheHiveEffect;
import com.telepathicgrunt.the_bumblezone.items.PollenPuff;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzEffects;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.tags.BzItemTags;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BeeInteractivity {

    private static final ResourceLocation PRODUCTIVE_BEES_HONEY_TREAT = new ResourceLocation("productivebees", "honey_treat");

    // heal bees with sugar water bottle or honey bottle or honey bucket
    public static ActionResultType beeFeeding(World world, PlayerEntity playerEntity, Hand hand, BeeEntity beeEntity) {
        ItemStack itemstack = playerEntity.getItemInHand(hand);
        ResourceLocation itemRL = itemstack.getItem().getRegistryName();

        // Disallow all non-tagged items from being fed to bees
        if (itemRL == null || !BzItemTags.BEE_FEEDING_ITEMS.contains(itemstack.getItem()))
            return ActionResultType.PASS;

        boolean removedWrath;
        ItemStack itemstackOriginal = itemstack.copy();

        // Let the honey treat behavior continue on so their gene stuff is not lost.
        if (itemRL.equals(PRODUCTIVE_BEES_HONEY_TREAT)) {
            removedWrath = calmAndSpawnHearts(world, playerEntity, beeEntity, 0.3f, 3);

            if(removedWrath && playerEntity instanceof ServerPlayerEntity ) {
                BzCriterias.FOOD_REMOVED_WRATH_OF_THE_HIVE_TRIGGER.trigger((ServerPlayerEntity) playerEntity, itemstackOriginal);
            }

            playerEntity.swing(hand, true);
            return ActionResultType.PASS;
        }

        if (itemstack.getItem().is(BzItemTags.HONEY_BUCKETS)) {
            beeEntity.heal(beeEntity.getMaxHealth() - beeEntity.getHealth());
            removedWrath = calmAndSpawnHearts(world, playerEntity, beeEntity, 0.8f, 5);
            if (beeEntity.isBaby()) {
                if (world.getRandom().nextBoolean()) {
                    beeEntity.setBaby(false);
                    if(playerEntity instanceof ServerPlayerEntity) {
                        BzCriterias.HONEY_BUCKET_BEE_GROW_TRIGGER.trigger((ServerPlayerEntity) playerEntity);
                    }
                }
            }
            else {
                int nearbyAdultBees = 0;
                for (BeeEntity nearbyBee : world.getEntitiesOfClass(BeeEntity.class, beeEntity.getBoundingBox().inflate(4), beeEntity1 -> true)) {
                    nearbyBee.setInLove(playerEntity);
                    if(!nearbyBee.isBaby()) nearbyAdultBees++;
                }

                if(nearbyAdultBees >= 2 && playerEntity instanceof ServerPlayerEntity) {
                    BzCriterias.HONEY_BUCKET_BEE_LOVE_TRIGGER.trigger((ServerPlayerEntity) playerEntity);
                }
            }
        }
        else if (itemRL.getPath().contains("honey")) {
            beeEntity.addEffect(new EffectInstance(Effects.HEAL, 1, 2, false, false, false));
            removedWrath = calmAndSpawnHearts(world, playerEntity, beeEntity, 0.3f, 3);
        }
        else {
            beeEntity.addEffect(new EffectInstance(Effects.HEAL, 1, 1, false, false, false));
            removedWrath = calmAndSpawnHearts(world, playerEntity, beeEntity, 0.1f, 3);
        }

        if (!playerEntity.isCreative()) {
            // remove current item
            Item item = itemstack.getItem();
            itemstack.shrink(1);
            GeneralUtils.givePlayerItem(playerEntity, hand, new ItemStack(item), true);
        }

        if(removedWrath && playerEntity instanceof ServerPlayerEntity) {
            BzCriterias.FOOD_REMOVED_WRATH_OF_THE_HIVE_TRIGGER.trigger((ServerPlayerEntity) playerEntity, itemstackOriginal);
        }

        playerEntity.swing(hand, true);
        return ActionResultType.SUCCESS;
    }

    public static ActionResultType beeUnpollinating(World world, PlayerEntity playerEntity, Hand hand, BeeEntity beeEntity) {
        if (!world.isClientSide()) {
            ItemStack itemstack = playerEntity.getItemInHand(hand);
            Item item = itemstack.getItem();

            // right clicking on pollinated bee with empty hand or pollen puff with room, gets pollen puff into hand.
            // else, if done with watery items or pollen puff without room, drops pollen puff in world
            if(beeEntity.hasNectar()) {
                if((itemstack.getTag() != null && itemstack.getTag().getString("Potion").contains("water")) ||
                    item == Items.WET_SPONGE ||
                    item == BzItems.SUGAR_WATER_BOTTLE.get() ||
                    (item instanceof BucketItem && ((BucketItem) item).getFluid().is(FluidTags.WATER)))
                {
                    PollenPuff.spawnItemstackEntity(world, beeEntity.blockPosition(), new ItemStack(BzItems.POLLEN_PUFF.get(), 1));
                    playerEntity.swing(hand, true);
                    beeEntity.dropOffNectar();
                    if(playerEntity instanceof ServerPlayerEntity) {
                        BzCriterias.BEE_DROP_POLLEN_PUFF_TRIGGER.trigger((ServerPlayerEntity) playerEntity, itemstack);
                    }
                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }

    public static boolean calmAndSpawnHearts(World world, PlayerEntity playerEntity, LivingEntity beeEntity, float calmChance, int hearts) {
        boolean calmed = world.random.nextFloat() < calmChance;
        boolean removedWrath = false;
        if (calmed) {
            if(playerEntity.hasEffect(BzEffects.WRATH_OF_THE_HIVE.get())){
                playerEntity.removeEffect(BzEffects.WRATH_OF_THE_HIVE.get());
                WrathOfTheHiveEffect.calmTheBees(playerEntity.level, playerEntity);
                removedWrath = true;
            }
            else{
                playerEntity.addEffect(new EffectInstance(
                        BzEffects.PROTECTION_OF_THE_HIVE.get(),
                        BzBeeAggressionConfigs.howLongProtectionOfTheHiveLasts.get(),
                        2,
                        false,
                        false,
                        true));
            }
        }

        if (!world.isClientSide() &&
                (beeEntity instanceof IAngerable ?
                        !((IAngerable)beeEntity).isAngry() || calmed :
                        calmed))
        {
            ((ServerWorld) world).sendParticles(
                    ParticleTypes.HEART,
                    beeEntity.getX(),
                    beeEntity.getY(),
                    beeEntity.getZ(),
                    hearts,
                    world.getRandom().nextFloat() * 0.5 - 0.25f,
                    world.getRandom().nextFloat() * 0.2f + 0.2f,
                    world.getRandom().nextFloat() * 0.5 - 0.25f,
                    world.getRandom().nextFloat() * 0.4 + 0.2f);
        }

        return removedWrath;
    }
}