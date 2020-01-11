package com.horuslugo.damagetp.mixin;

import com.horuslugo.damagetp.Util;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerEntity.class)
public class TeleportOnDamagePlayerMixin {

	@Inject(method = "damage", at = @At("RETURN"))
	private void onDamage(DamageSource source, float amount, CallbackInfoReturnable info) {
		PlayerEntity self = (PlayerEntity) (Object) this;
		List<PlayerEntity> players = (List<PlayerEntity>) self.world.getPlayers();
		PlayerEntity otherPlayer;

		if (players.size() <= 1 || self.isInvulnerable()) {
			return;
		}

		do {
			int index = (int) Math.floor(Math.random() * players.size());
			otherPlayer = players.get(index);
		} while(otherPlayer == self);

		BlockPos selfPos = self.getBlockPos();
		ListTag selfInventory = self.inventory.serialize(new ListTag());

		BlockPos otherPlayerPos = otherPlayer.getBlockPos();
		ListTag otherPlayerInventory = otherPlayer.inventory.serialize(new ListTag());

		self.setInvulnerable(true);
		otherPlayer.setInvulnerable(true);
		self.inventory.deserialize(otherPlayerInventory);
		otherPlayer.inventory.deserialize(selfInventory);

		self.teleport(
				Util.center(otherPlayerPos.getX()),
				256,
				Util.center(otherPlayerPos.getZ())
		);

		otherPlayer.teleport(
				Util.center(selfPos.getX()),
				Util.center(selfPos.getY()),
				Util.center(selfPos.getZ())
		);

		self.teleport(
				Util.center(otherPlayerPos.getX()),
				Util.center(otherPlayerPos.getY()),
				Util.center(otherPlayerPos.getZ())
		);



		PlayerEntity finalOtherPlayer = otherPlayer;

		Util.setTimeout(() -> {
			self.setInvulnerable(false);
			finalOtherPlayer.setInvulnerable(false);
		}, 3000);
	}

}
