package com.telepathicgrunt.the_bumblezone.modinit;


import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.entities.subpredicates.HoneySlimePredicate;
import com.telepathicgrunt.the_bumblezone.worldgen.features.decorators.ConditionBasedPlacement;
import com.telepathicgrunt.the_bumblezone.worldgen.features.decorators.FixedOffset;
import com.telepathicgrunt.the_bumblezone.worldgen.features.decorators.HoneycombHolePlacer;
import com.telepathicgrunt.the_bumblezone.worldgen.features.decorators.Random3DClusterPlacement;
import com.telepathicgrunt.the_bumblezone.worldgen.features.decorators.Random3DUndergroundChunkPlacement;
import com.telepathicgrunt.the_bumblezone.worldgen.features.decorators.RoofedDimensionCeilingPlacement;
import com.telepathicgrunt.the_bumblezone.worldgen.features.decorators.RoofedDimensionSurfacePlacement;
import com.telepathicgrunt.the_bumblezone.worldgen.features.decorators.StructureDisallowByTag;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class BzSubPredicates {
    public static final ResourcefulRegistry<MapCodec<? extends EntitySubPredicate>> SUB_PREDICATES = ResourcefulRegistries.create(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, Bumblezone.MODID);

    public static final RegistryEntry<MapCodec<HoneySlimePredicate>> HONEY_SLIME = SUB_PREDICATES.register("honey_slime", () -> HoneySlimePredicate.CODEC);
}