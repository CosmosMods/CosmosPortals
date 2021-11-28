package com.tcn.cosmosportals.core.tileentity;

import java.util.Random;

import com.tcn.cosmoslibrary.common.comp.CosmosColour;
import com.tcn.cosmoslibrary.core.teleport.CosmosTeleportCore;
import com.tcn.cosmoslibrary.core.teleport.CosmosTeleporter;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectDestinationInfo;
import com.tcn.cosmosportals.core.block.BlockPortal;
import com.tcn.cosmosportals.core.management.CoreConfigurationManager;
import com.tcn.cosmosportals.core.management.CoreEventFactory;
import com.tcn.cosmosportals.core.management.CoreModBusManager;
import com.tcn.cosmosportals.core.management.CoreSoundManager;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class TileEntityPortal extends TileEntity implements ITickableTileEntity {
	
	public ObjectDestinationInfo destInfo = new ObjectDestinationInfo(BlockPos.ZERO, 0, 0);
	public ResourceLocation destDimension = new ResourceLocation("missing_info");
	public int display_colour = CosmosColour.GRAY.dec();
	
	public boolean playSound = true;
	public boolean allowEntities = true;
	public boolean showParticles = true;

	public TileEntityPortal() {
		super(CoreModBusManager.PORTAL_TILE_TYPE);
	}
	
	public void sendUpdates(boolean update) {
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			BlockPortal block = (BlockPortal) state.getBlock();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (update) {
				if (!level.isClientSide) {
					level.setBlockAndUpdate(this.getBlockPos(), block.updateState(state, this.getBlockPos(), this.getLevel()));
				}
			}
		}
	}
	
	public void setPlaySound(boolean play) {
		this.playSound = play;
	}
	
	public void setAllowEntities(boolean allow) {
		this.allowEntities = allow;
	}
	
	public void setShowParticles(boolean show) {
		this.showParticles = show;
	}

	public void setDestDimension(ResourceLocation type) {
		this.destDimension = type;
	}
	
	public RegistryKey<World> getDestDimension() {
		return RegistryKey.create(Registry.DIMENSION_REGISTRY, this.destDimension);
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
	
	public void setDisplayColour(CosmosColour colour) {
		this.display_colour = colour.dec();
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		super.save(compound);

		compound.putString("namespace", this.destDimension.getNamespace());
		compound.putString("path", this.destDimension.getPath());
		
		this.destInfo.writeToNBT(compound);
		
		compound.putInt("colour", this.getDisplayColour());
		
		compound.putBoolean("playSound", this.playSound);
		compound.putBoolean("allowEntities", this.allowEntities);
		compound.putBoolean("showParticles", this.showParticles);
		
		return compound;
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
		
		String namespace = compound.getString("namespace");
		String path = compound.getString("path");
		
		this.destDimension = new ResourceLocation(namespace, path);
		this.destInfo = ObjectDestinationInfo.readFromNBT(compound);
		this.display_colour = compound.getInt("colour");
		
		this.playSound = compound.getBoolean("playSound");
		this.allowEntities = compound.getBoolean("allowEntities");
		this.showParticles = compound.getBoolean("showParticles");
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		this.load(state, tag);
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = new CompoundNBT();
		
		this.save(tag);
		
		return tag;
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.getBlockPos(), 0, this.getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundNBT tag_ = pkt.getTag();

		BlockState state = level.getBlockState(pkt.getPos());
		
		this.handleUpdateTag(state, tag_);
	}

	@Override
	public void onLoad() { }
	
	@Override
	public void tick() { }
	
	public void entityInside(BlockState stateIn, World worldIn, BlockPos posIn, Entity entityIn) {
		if (!entityIn.isPassenger() && !entityIn.isVehicle() && entityIn.canChangeDimensions()) {
			
			if (this.isPortalAtFeet(worldIn, this.getBlockPos(), entityIn)) {
				if (!entityIn.isOnPortalCooldown()) {
					if (!this.destDimension.getNamespace().isEmpty() && !this.destDimension.getPath().isEmpty()) {
						if (this.getDestPos() != null && this.getDestPos() != BlockPos.ZERO) {
							BlockPos targetPos = this.getDestPos();
							float yaw = this.destInfo.getYaw();
							float pitch = this.destInfo.getPitch();
	
							if (entityIn.level.dimension().equals(this.getDestDimension())) {
								if (entityIn instanceof ServerPlayerEntity) {
									ServerPlayerEntity playerIn = (ServerPlayerEntity) entityIn;
									
									if (!playerIn.isShiftKeyDown()) {
										if (CoreConfigurationManager.getInstance().getPlayPortalTravelSounds() && this.playSound) {
											playerIn.connection.send(new SPlaySoundEffectPacket(CoreSoundManager.PORTAL_TRAVEL, SoundCategory.AMBIENT, targetPos.getX(), targetPos.getY(), targetPos.getZ(), 0.1F, 1));
										}
										
										this.setCooldown(entityIn, 4, 50);
	
										if (CoreEventFactory.onPortalTravel(playerIn, playerIn.blockPosition(), targetPos, this.destDimension)) {
											playerIn.connection.teleport(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, yaw, pitch);
										}
										
										playerIn.setYHeadRot(yaw);
										playerIn.setYBodyRot(pitch);
									}
								} else {
									if (CoreEventFactory.onPortalTravel(entityIn, entityIn.blockPosition(), targetPos, this.destDimension)) {
										if (this.allowEntities) {
											entityIn.teleportTo(targetPos.getX(), targetPos.getY(), targetPos.getZ());
										}
									}
								}
							} else {
								if (entityIn instanceof ServerPlayerEntity) {
									ServerPlayerEntity playerIn = (ServerPlayerEntity) entityIn;
									
									if (!playerIn.isShiftKeyDown()) {
										
										CosmosTeleporter teleporter = CosmosTeleporter.createTeleporter(this.getDestDimension(), targetPos, yaw, pitch, false, false, true);
										
										if (CoreEventFactory.onPortalTravel(playerIn, playerIn.blockPosition(), targetPos, this.destDimension)) {
											CosmosTeleportCore.shiftPlayerToDimension(playerIn, teleporter, CoreConfigurationManager.getInstance().getPlayPortalTravelSounds() && this.playSound ? CoreSoundManager.PORTAL_TRAVEL : null, 0.1F);
										}
									}
								} else {
									CosmosTeleporter teleporter = CosmosTeleporter.createTeleporter(this.getDestDimension(), targetPos, yaw, pitch, false, false, true);

									if (CoreEventFactory.onPortalTravel(entityIn, entityIn.blockPosition(), targetPos, this.destDimension)) {
										if (this.allowEntities) {
											entityIn.changeDimension(ServerLifecycleHooks.getCurrentServer().getLevel(this.getDestDimension()), teleporter);
										}
									}
								}
							}
						}
					}
				} else {
					//Object intt = ObfuscationReflectionHelper.getPrivateValue(Entity.class, entityIn, "field_242273_aw");
					//CoreConsole.debug(this.getBlockPos() + " _ " + intt);
				}
			}
		}
	}
	
	public boolean isPortalAtFeet(World worldIn, BlockPos pos, Entity entityIn) {
		BlockPos entityPos = entityIn.blockPosition();
		
		return entityPos.equals(pos);
	}
	
	public void setCooldown(Entity entityIn, int cooldown, int sleep) {
		ObfuscationReflectionHelper.setPrivateValue(Entity.class, entityIn, cooldown, "field_242273_aw");
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos posIn, Random randIn) {
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
				
				TileEntity tile = worldIn.getBlockEntity(posIn);
				
				if (tile instanceof TileEntityPortal) {
					TileEntityPortal tileEntity = (TileEntityPortal) tile;
					
					if (tileEntity.destDimension.equals(World.NETHER.location())) {
						worldIn.addParticle(ParticleTypes.CRIMSON_SPORE, d0, d1, d2, d3, d4, d5);
					} else if (tileEntity.destDimension.equals(World.END.location())) {
						worldIn.addParticle(ParticleTypes.ASH, d0, d1, d2, d3, d4, d5);
					} else {
						worldIn.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, d0, d1, d2, d3, d4, d5);
					}
				}
			}
		}
	}
}