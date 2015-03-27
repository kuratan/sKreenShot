package de.kuratan.skreenshot;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import java.lang.reflect.InvocationTargetException;

public class MenuBackgroundCreator extends ScreenShoter {

    private Stage stage;

    public MenuBackgroundCreator(EntityPlayer player) {
        super(player, 256, 256);
        this.stage = Stage.INIT;
    }

    private float getYRotForStage() {
        switch (this.stage) {
            case EAST:
                return -90;
            case SOUTH:
                return 0.0f;
            case WEST:
                return 90.0f;
            default:
                return -180.0f;
        }
    }

    private float getXRotForStage() {
        switch (this.stage) {
            case UP:
                return -90.0f;
            case DOWN:
                return 90.0f;
            default:
                return 0.0f;
        }
    }


    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (this.wait > 0) {
            this.wait--;
            return;
        }
        if (!this.shotDone) {
            if (shotSetUp && !this.stage.isHelper()) {
                takeShot("panorama_" + this.stage.getValue() + ".png");
            } else {
                try {
                    this.setUp();
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            nextState();
        }
    }

    private void nextState() {
        this.stage = this.stage.next();
        if (this.stage == Stage.DONE) {
            try {
                this.tearDown();
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return;
        }

        // Center on current block
        Vec3 pos = player.getPosition(0);
        pos.xCoord = Math.floor(pos.xCoord) + 0.5;
        pos.zCoord = Math.floor(pos.zCoord) + 0.5;

        // Set values for current stage
        player.setPositionAndRotation(pos.xCoord, pos.yCoord, pos.zCoord, getYRotForStage(), getXRotForStage());
        player.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);

        this.shotSetUp = true;
        this.shotDone = false;
        this.wait = Config.FRAME_DELAY;
        System.out.println("Prepared " + this.stage);
    }
}
