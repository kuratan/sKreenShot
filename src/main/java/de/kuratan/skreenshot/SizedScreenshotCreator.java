package de.kuratan.skreenshot;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class SizedScreenShotCreator extends ScreenShoter {

    private Stage stage;

    public SizedScreenShotCreator(EntityPlayer player, int width, int height) {
        super(player, width, height);
        this.stage = Stage.INIT;
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
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            nextState();
        }
    }

    private void nextState() {
        this.stage = this.stage.next();
        if (this.stage == Stage.EAST) {
            this.stage = Stage.DONE;
        }
        if (this.stage == Stage.DONE) {
            try {
                this.tearDown();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return;
        }

        this.shotSetUp = true;
        this.shotDone = false;
        this.wait = Config.FRAME_DELAY;
    }
}
