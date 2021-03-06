package nc.block.fluid;

import java.util.Random;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidSteam extends BlockFluid {
	
	private static final Material GAS = new MaterialLiquid(MapColor.AIR);
	
	public static DamageSource steam_burn = (new DamageSource("steam_burn")).setDamageBypassesArmor();

	public BlockFluidSteam(Fluid fluid, String name) {
		super(fluid, name, GAS);
		setQuantaPerBlock(16);
	}
	
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		entityIn.attackEntityFrom(steam_burn, 3.0F);
		entityIn.setFire(1);
	}
	
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if (isSourceBlock(worldIn, pos)) worldIn.setBlockToAir(pos);
	}
}
