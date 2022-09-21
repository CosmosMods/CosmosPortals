package com.tcn.cosmosportals.core.blockentity;

import com.tcn.cosmoslibrary.common.enums.EnumAllowedEntities;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.core.teleport.CosmosTeleportCore;
import com.tcn.cosmoslibrary.core.teleport.CosmosTeleporter;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectDestinationInfo;
import com.tcn.cosmosportals.core.management.ConfigurationManagerCommon;
import com.tcn.cosmosportals.core.management.CoreSoundManager;
import com.tcn.cosmosportals.core.management.EventFactory;
import com.tcn.cosmosportals.core.management.ModObjectHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.server.ServerLifecycleHooks;

public class BlockEntityPortal extends BlockEntity {
	
	private BlockEntityType<?> type;
	
	public ObjectDestinationInfo destInfo = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
	public ResourceLocation destDimension = new ResourceLocation("missing_info");
	public int display_colour = ComponentColour.GRAY.dec();
	
	public boolean playSound = true;
	public EnumAllowedEntities allowedEntities = EnumAllowedEntities.ALL;
	public boolean showParticles = true;
	
	public boolean entityInside = false;
	private boolean teleported = false;
	
	private Entity entityToTele = null;

	public BlockEntityPortal(BlockPos posIn, BlockState stateIn) {
		super(ModObjectHolder.tile_portal, posIn, stateIn);
		
		this.type = ModObjectHolder.tile_portal;
	}
	
	public void sendUpdates(boolean update) {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (update) {
				if (!level.isClientSide) {
					level.setBlockAndUpdate(this.getBlockPos(), state.updateShape(Direction.DOWN, state, level, worldPosition, worldPosition));
				}
			}
		}
	}
	
	public void setPlaySound(boolean play) {
		this.playSound = play;
	}
	
	public void setAllowedEntities(EnumAllowedEntities allow) {
		this.allowedEntities = allow;
	}
	
	public void setShowParticles(boolean show) {
		this.showParticles = show;
	}

	public void setDestDimension(ResourceLocation type) {
		this.destDimension = type;
	}
	
	public ResourceKey<Level> getDestDimension() {
		return ResourceKey.create(Registry.DIMENSION_REGISTRY, this.destDimension);
	}

	public BlockPos getDestPos() {
		return destInfo.getPos();
	}

	public void updateDestPos(BlockPos pos) {
		this.destInfo.setPos(pos);
	}

	public void setDestInfo(BlockPos posIn, float yawIn, float pitchIn) {
		this.destInfo = new ObjectDestinationInfo(posIn, yawIn, pitchIn);
	}
	
	public int getDisplayColour() {
		return this.display_colour;
	}
	
	public void setDisplayColour(int colourIn) {
		if (!(this.display_colour == colourIn)) {
			this.display_colour = colourIn;
		}
	}
	
	public void setDisplayColour(ComponentColour colour) {
		this.display_colour = colour.dec();
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);

		compound.putString("namespace", this.destDimension.getNamespace());
		compound.putString("path", this.destDimension.getPath());
		this.destInfo.writeToNBT(compound);
		compound.putInt("colour", this.getDisplayColour());
		
		compound.putBoolean("playSound", this.playSound);
		compound.putInt("allowedEntities", this.allowedEntities.getIndex());
		compound.putBoolean("showParticles", this.showParticles);
		
		compound.putBoolean("inside", this.entityInside);
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
		String namespace = compound.getString("namespace");
		String path = compound.getString("path");
		this.destDimension = new ResourceLocation(namespace, path);
		this.destInfo = ObjectDestinationInfo.readFromNBT(compound);
		this.display_colour = compound.getInt("colour");
		
		this.playSound = compound.getBoolean("playSound");
		this.allowedEntities = EnumAllowedEntities.getStateFromIndex(compound.getInt("allowedEntities"));
		this.showParticles = compound.getBoolean("showParticles");
		
		this.entityInside = compound.getBoolean("inside");
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
		
		this.sendUpdates(true);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag);
		return tag;
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundTag tag_ = pkt.getTag();

		this.handleUpdateTag(tag_);
	}
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityPortal entityIn) {
		if (!levelIn.isClientSide) {
			if (entityIn.entityInside && !entityIn.teleported && entityIn.entityToTele != null) {
				entityIn.teleported = true;
				Entity entityToTeleport = entityIn.entityToTele;
				
				if (!entityIn.destDimension.getNamespace().isEmpty() && !entityIn.destDimension.getPath().isEmpty()) {
					if (entityIn.getDestPos() != null && entityIn.getDestPos() != BlockPos.ZERO) {
						BlockPos targetPos = entityIn.getDestPos();
						float yaw = entityIn.destInfo.getYaw();
						float pitch = entityIn.destInfo.getPitch();

						if (entityToTeleport.level.dimension().equals(entityIn.getDestDimension())) {
							if (entityToTeleport instanceof ServerPlayer) {
								ServerPlayer playerIn = (ServerPlayer) entityToTeleport;
								
								if (!playerIn.isShiftKeyDown()) {
									if (entityIn.allowedEntities.equals(EnumAllowedEntities.ALL) || entityIn.allowedEntities.equals(EnumAllowedEntities.PLAYERS_ONLY)) {
										if (ConfigurationManagerCommon.getInstance().getPlayPortalTravelSounds() && entityIn.playSound) {
											playerIn.connection.send(new ClientboundSoundPacket(CoreSoundManager.PORTAL_TRAVEL.get(), SoundSource.AMBIENT, targetPos.getX(), targetPos.getY(), targetPos.getZ(), 0.1F, 1, 1));
										}
										
										if (EventFactory.onPortalTravel(playerIn, playerIn.blockPosition(), targetPos, entityIn.destDimension)) {
											playerIn.connection.teleport(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, yaw, pitch);
										}
										
										playerIn.setYHeadRot(yaw);
										playerIn.setYBodyRot(pitch);
									}
								}
							} else {
								if (EventFactory.onPortalTravel(entityToTeleport, entityToTeleport.blockPosition(), targetPos, entityIn.destDimension)) {
									if (entityIn.allowedEntities.equals(EnumAllowedEntities.ALL) || entityIn.allowedEntities.equals(EnumAllowedEntities.NON_PLAYERS_ONLY)) {
										
										if (entityToTeleport.getType().equals(EntityType.WARDEN)) {
											if (ConfigurationManagerCommon.getInstance().getAllowWardenTeleport()) {
													entityToTeleport.teleportTo(targetPos.getX(), targetPos.getY(), targetPos.getZ());
											}
										} else {
											entityToTeleport.teleportTo(targetPos.getX(), targetPos.getY(), targetPos.getZ());
										}
									}
								}
							}
						} else {
							if (entityToTeleport instanceof ServerPlayer) {
								ServerPlayer playerIn = (ServerPlayer) entityToTeleport;
								
								if (!playerIn.isShiftKeyDown()) {
									if (entityIn.allowedEntities.equals(EnumAllowedEntities.ALL) || entityIn.allowedEntities.equals(EnumAllowedEntities.PLAYERS_ONLY)) {
										CosmosTeleporter teleporter = CosmosTeleporter.createTeleporter(entityIn.getDestDimension(), targetPos, yaw, pitch, false, false, true);
										
										if (EventFactory.onPortalTravel(playerIn, playerIn.blockPosition(), targetPos, entityIn.destDimension)) {
											CosmosTeleportCore.shiftPlayerToDimension(playerIn, teleporter, ConfigurationManagerCommon.getInstance().getPlayPortalTravelSounds() && entityIn.playSound ? CoreSoundManager.PORTAL_TRAVEL.get() : null, 0.1F);
										}
									}
								}
							} else {
								CosmosTeleporter teleporter = CosmosTeleporter.createTeleporter(entityIn.getDestDimension(), targetPos, yaw, pitch, false, false, true);

								if (EventFactory.onPortalTravel(entityToTeleport, entityToTeleport.blockPosition(), targetPos, entityIn.destDimension)) {
									if (entityIn.allowedEntities.equals(EnumAllowedEntities.ALL) || entityIn.allowedEntities.equals(EnumAllowedEntities.NON_PLAYERS_ONLY)) {

										if (entityToTeleport.getType().equals(EntityType.WARDEN)) {
											if (ConfigurationManagerCommon.getInstance().getAllowWardenTeleport()) {
												entityToTeleport.changeDimension(ServerLifecycleHooks.getCurrentServer().getLevel(entityIn.getDestDimension()), teleporter);
											} 
										} else {
											entityToTeleport.changeDimension(ServerLifecycleHooks.getCurrentServer().getLevel(entityIn.getDestDimension()), teleporter);
										}
									}
								}
							}
						}
					}
				}
				
				entityIn.entityToTele = null;
				entityIn.entityInside = false;
				entityIn.teleported = false;
			}
		}
	}
	
	
	public void entityInside(BlockState stateIn, Level worldIn, BlockPos posIn, Entity entityIn) {
		if (!entityIn.isPassenger() && !entityIn.isVehicle() && entityIn.canChangeDimensions()) {
			if (this.isPortalAtFeet(worldIn, this.getBlockPos(), entityIn)) {
				
				if (!this.entityInside && this.entityToTele == null) {
					this.entityInside = true;
					this.entityToTele = entityIn;
				}
			}
		}
	}
	
	public boolean isPortalAtFeet(Level worldIn, BlockPos pos, Entity entityIn) {
		BlockPos entityPos = entityIn.blockPosition();
		
		return entityPos.equals(pos);
	}
	
	public void setCooldown(Entity entityIn, int cooldown) {
		ObfuscationReflectionHelper.setPrivateValue(Entity.class, entityIn, cooldown, "f_19839_");
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos posIn, RandomSource randIn) {
		if (this.showParticles) {
			for (int i = 0; i < 3; ++i) {
				double d0 = (double) posIn.getX() + randIn.nextDouble();
				double d1 = (double) posIn.getY() + randIn.nextDouble();
				double d2 = (double) posIn.getZ() + randIn.nextDouble();
				double d3 = ((double) randIn.nextFloat() - 0.5D) * 0.5D;
				double d4 = ((double) randIn.nextFloat() - 0.5D) * 0.5D;
				double d5 = ((double) randIn.nextFloat() - 0.5D) * 0.5D;
				int j = randIn.nextInt(2) * 2 - 1;
				
				if (!worldIn.getBlockState(posIn.west()).is(worldIn.getBlockState(posIn).getBlock()) && !worldIn.getBlockState(posIn.east()).is(worldIn.getBlockState(posIn).getBlock())) {
					d0 = (double) posIn.getX() + 0.5D + 0.25D * (double) j;
					d3 = (double) (randIn.nextFloat() * 2.0F * (float) j);
				} else {
					d2 = (double) posIn.getZ() + 0.5D + 0.25D * (double) j;
					d5 = (double) (randIn.nextFloat() * 2.0F * (float) j);
				}
				
				BlockEntity tile = worldIn.getBlockEntity(posIn);
				
				if (tile instanceof BlockEntityPortal) {
					BlockEntityPortal tileEntity = (BlockEntityPortal) tile;
					
					if (tileEntity.destDimension.equals(Level.NETHER.location())) {
						worldIn.addParticle(ParticleTypes.CRIMSON_SPORE, d0, d1, d2, d3, d4, d5);
					} else if (tileEntity.destDimension.equals(Level.END.location())) {
						worldIn.addParticle(ParticleTypes.ASH, d0, d1, d2, d3, d4, d5);
					} else {
						worldIn.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, d0, d1, d2, d3, d4, d5);
					}
				}
			}
		}
	}

	@Override
	public boolean isRemoved() {
		return false;
	}
	
	@Override
	public BlockEntityType<?> getType() {
		return this.type;
	}
}