package nc.tile.energy;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import nc.ModCheck;
import nc.energy.EnumStorage.Connection;
import nc.energy.Storage;
import nc.tile.NCTile;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public class TileEnergy extends NCTile implements ITileEnergy, IEnergyReceiver, IEnergyProvider, IEnergyTile, IEnergySink, IEnergySource {

	public Connection connection;
	public final Storage storage;
	
	public TileEnergy(int capacity, Connection connection) {
		this(capacity, capacity, capacity, connection);
	}
	
	public TileEnergy(int capacity, int maxTransfer, Connection connection) {
		this(capacity, maxTransfer, maxTransfer, connection);
	}
	
	public TileEnergy(int capacity, int maxReceive, int maxExtract, Connection connection) {
		super();
		storage = new Storage(capacity, maxReceive, maxExtract);
		this.connection = connection;
	}
	
	public void onAdded() {
		super.onAdded();
		if (!worldObj.isRemote && ModCheck.ic2Loaded()) MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
	}

	public void invalidate() {
		super.invalidate();
		if (!worldObj.isRemote && ModCheck.ic2Loaded()) MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
	}
	
	// Redstone Flux
	
	public int getEnergyStored(EnumFacing from) {
		return storage.getEnergyStored();
	}

	public int getMaxEnergyStored(EnumFacing from) {
		return storage.getMaxEnergyStored();
	}
	
	public int getEnergyStored() {
		return storage.getEnergyStored();
	}

	public int getMaxEnergyStored() {
		return storage.getMaxEnergyStored();
	}

	public boolean canConnectEnergy(EnumFacing from) {
		return connection.canConnect();
	}

	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return connection.canReceive() ? storage.receiveEnergy(maxReceive, simulate) : 0;
	}
	
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return connection.canExtract() ? storage.extractEnergy(maxExtract, simulate) : 0;
	}
	
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return connection.canReceive() ? storage.receiveEnergy(maxReceive, simulate) : 0;
	}

	public int extractEnergy(int maxExtract, boolean simulate) {
		return connection.canExtract() ? storage.extractEnergy(maxExtract, simulate) : 0;
	}
	
	// IC2 Energy

	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
		return connection.canReceive();
	}

	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
		return connection.canExtract();
	}

	public double getOfferedEnergy() {
		return Math.min(Math.pow(2, 2*getSourceTier() + 3), storage.takePower(storage.maxExtract, true) / 4);
	}
	
	public double getDemandedEnergy() {
		return Math.min(Math.pow(2, 2*getSinkTier() + 3), storage.givePower(storage.maxReceive, true) / 4);
	}
	
	public void drawEnergy(double amount) {
		storage.takePower((long) (4*amount), false);
	}

	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		int energyReceived = storage.receiveEnergy((int) (4*amount), true);
		storage.givePower(energyReceived, false);
		return amount - (energyReceived/4);
	}
	
	public int getSourceTier() {
		return 2;
	}

	public int getSinkTier() {
		return 2;
	}
	
	// NBT
	
	public NBTTagCompound writeAll(NBTTagCompound nbt) {
		super.writeAll(nbt);
		nbt.setInteger("energy", storage.getEnergyStored());
		return nbt;
	}
	
	public void readAll(NBTTagCompound nbt) {
		super.readAll(nbt);
		storage.setEnergyStored(nbt.getInteger("energy"));
	}
	
	// Energy Connections
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public void pushEnergy() {
		if (storage.getEnergyStored() <= 0 || !connection.canExtract()) return;
		for (EnumFacing side : EnumFacing.VALUES) {
			TileEntity tile = worldObj.getTileEntity(getPos().offset(side));
			//TileEntity thisTile = world.getTileEntity(getPos());
			
			if (tile instanceof IEnergyReceiver /*&& tile != thisTile*/) {
				storage.extractEnergy(((IEnergyReceiver) tile).receiveEnergy(side.getOpposite(), storage.extractEnergy(storage.getMaxEnergyStored(), true), false), false);
			}
			else if (tile instanceof IEnergySink /*&& tile != thisTile*/) {
				storage.extractEnergy((int) Math.round(((IEnergySink) tile).injectEnergy(side.getOpposite(), storage.extractEnergy(storage.getMaxEnergyStored(), true) / 24, getSourceTier())), false);
			}
		}
	}
	
	// Capability
	
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY == capability) {
			return true;
		}
		if (connection != null && ModCheck.teslaLoaded && connection.canConnect()) {
			if ((capability == TeslaCapabilities.CAPABILITY_CONSUMER && connection.canReceive()) || (capability == TeslaCapabilities.CAPABILITY_PRODUCER && connection.canExtract()) || capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY == capability) {
			return (T) storage;
		}
		if (connection != null && ModCheck.teslaLoaded && connection.canConnect()) {
			if ((capability == TeslaCapabilities.CAPABILITY_CONSUMER && connection.canReceive()) || (capability == TeslaCapabilities.CAPABILITY_PRODUCER && connection.canExtract()) || capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return (T) storage;
		}
		return super.getCapability(capability, facing);
	}
}
