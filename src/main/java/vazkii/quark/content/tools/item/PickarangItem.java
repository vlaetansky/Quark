package vazkii.quark.content.tools.item;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.tools.entity.Pickarang;
import vazkii.quark.content.tools.module.PickarangModule;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;

public class PickarangItem extends QuarkItem {

	public final boolean isNetherite;

	public PickarangItem(String regname, QuarkModule module, Properties properties, boolean isNetherite) {
		super(regname, module, properties);
		this.isNetherite = isNetherite;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
		stack.hurtAndBreak(2, attacker, (player) -> player.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		return true;
	}

	@Override
	public boolean isCorrectToolForDrops(@Nonnull BlockState blockIn) {
		return switch (isNetherite ? PickarangModule.netheriteHarvestLevel : PickarangModule.harvestLevel) {
			case 0 -> Items.WOODEN_PICKAXE.isCorrectToolForDrops(blockIn) ||
					Items.WOODEN_AXE.isCorrectToolForDrops(blockIn) ||
					Items.WOODEN_SHOVEL.isCorrectToolForDrops(blockIn);
			case 1 -> Items.STONE_PICKAXE.isCorrectToolForDrops(blockIn) ||
					Items.STONE_AXE.isCorrectToolForDrops(blockIn) ||
					Items.STONE_SHOVEL.isCorrectToolForDrops(blockIn);
			case 2 -> Items.IRON_PICKAXE.isCorrectToolForDrops(blockIn) ||
					Items.IRON_AXE.isCorrectToolForDrops(blockIn) ||
					Items.IRON_SHOVEL.isCorrectToolForDrops(blockIn);
			case 3 -> Items.DIAMOND_PICKAXE.isCorrectToolForDrops(blockIn) ||
					Items.DIAMOND_AXE.isCorrectToolForDrops(blockIn) ||
					Items.DIAMOND_SHOVEL.isCorrectToolForDrops(blockIn);
			default -> Items.NETHERITE_PICKAXE.isCorrectToolForDrops(blockIn) ||
					Items.NETHERITE_AXE.isCorrectToolForDrops(blockIn) ||
					Items.NETHERITE_SHOVEL.isCorrectToolForDrops(blockIn);
		};
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return Math.max(isNetherite ? PickarangModule.netheriteDurability : PickarangModule.durability, 0);
	}

	@Override
	public boolean mineBlock(@Nonnull ItemStack stack, @Nonnull Level worldIn, BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity entityLiving) {
		if (state.getDestroySpeed(worldIn, pos) != 0)
			stack.hurtAndBreak(1, entityLiving, (player) -> player.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		return true;
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		playerIn.setItemInHand(handIn, ItemStack.EMPTY);
		int eff = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, itemstack);
		Vec3 pos = playerIn.position();
		worldIn.playSound(null, pos.x, pos.y, pos.z, QuarkSounds.ENTITY_PICKARANG_THROW, SoundSource.NEUTRAL, 0.5F + eff * 0.14F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));

		if(!worldIn.isClientSide)  {
			Inventory inventory = playerIn.getInventory();
			int slot = handIn == InteractionHand.OFF_HAND ? inventory.getContainerSize() - 1 : inventory.selected;
			Pickarang entity = new Pickarang(worldIn, playerIn);
			entity.setThrowData(slot, itemstack, isNetherite);
			entity.shoot(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, 1.5F + eff * 0.325F, 0F);
			worldIn.addFreshEntity(entity);
		}

		if(!playerIn.getAbilities().instabuild && !PickarangModule.noCooldown) {
			int cooldown = 10 - eff;
			if (cooldown > 0)
				playerIn.getCooldowns().addCooldown(this, cooldown);
		}

		playerIn.awardStat(Stats.ITEM_USED.get(this));
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
	}

	@Nonnull
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlot slot, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> multimap = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);

		if (slot == EquipmentSlot.MAINHAND) {
			multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", isNetherite ? 3 : 2, AttributeModifier.Operation.ADDITION));
			multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.8, AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull BlockState state) {
		return 0F;
	}

	@Override
	public boolean isRepairable(@Nonnull ItemStack stack) {
		return true;
	}

	@Override
	public boolean isValidRepairItem(@Nonnull ItemStack toRepair, ItemStack repair) {
		return repair.getItem() == (isNetherite ? Items.NETHERITE_INGOT : Items.DIAMOND);
	}

	@Override
	public int getEnchantmentValue() {
		return isNetherite ? Items.NETHERITE_PICKAXE.getEnchantmentValue() : Items.DIAMOND_PICKAXE.getEnchantmentValue();
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return super.canApplyAtEnchantingTable(stack, enchantment) || ImmutableSet.of(Enchantments.BLOCK_FORTUNE, Enchantments.SILK_TOUCH, Enchantments.BLOCK_EFFICIENCY).contains(enchantment);
	}
}
