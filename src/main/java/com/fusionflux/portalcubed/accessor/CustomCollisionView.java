package com.fusionflux.portalcubed.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public interface CustomCollisionView extends BlockView {

    @Nullable
    BlockView getChunkAsView(int chunkX, int chunkZ);

    default Iterable<VoxelShape> getPortalBlockCollisions(@Nullable Entity entity, Box box, Direction direction) {
        return () -> new CustomBlockCollisionSpliteraror(this, entity, box,direction);
    }
}
