package de.kuratan.skreenshot;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuBackgroundCreator {

    private Minecraft mc;
    private Method resize;
    private int stage;
    private EntityPlayer player;
    private int height;
    private int width;
    private boolean accessable;
    private boolean anaglyph;
    private boolean hideGUI;
    private int wait;
    private Vec3 pos;
    private float yRot;
    private float xRot;
    private boolean shotSetUp;
    private boolean shotDone;
    private int thirdPersonView;
    private File timedDir;
    private float fovSetting;

    public MenuBackgroundCreator(EntityPlayer player) {
        this.player = player;
        this.stage = -1;
        this.mc = Minecraft.getMinecraft();
        this.resize = null;
        timedDir = null;

        for (Method method : Minecraft.getMinecraft().getClass().getDeclaredMethods()) {
            if (method.getParameterCount() == 2) {
                Type[] types = method.getParameterTypes();
                if (types[0] == Integer.TYPE && types[1] == Integer.TYPE) {
                    this.resize = method;
                }
            }
        }
        if (canRun()) {
            accessable = resize.isAccessible();
        }
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        System.out.println("Added Background Creator");
    }

    private void setUp() throws InvocationTargetException, IllegalAccessException {
        File dir = new File(Config.BASE_DIR, "menubackground");
        String time = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        timedDir = new File(dir.getAbsolutePath(), time);
        if (!timedDir.exists()) {
            timedDir.mkdirs();
        }

        resize.setAccessible(true);
        hideGUI = mc.gameSettings.hideGUI;
        anaglyph = mc.gameSettings.anaglyph;
        width = mc.displayWidth;
        height = mc.displayHeight;
        pos = player.getPosition(0);
        yRot = player.rotationYaw;
        xRot = player.rotationPitch;
        thirdPersonView = mc.gameSettings.thirdPersonView;
        fovSetting = mc.gameSettings.fovSetting;

        mc.gameSettings.anaglyph = false;
        mc.gameSettings.hideGUI = true;
        mc.gameSettings.thirdPersonView = 0;
        mc.gameSettings.fovSetting = 90.0f;
        resize.invoke(mc, 256, 256);
        this.shotDone = true;
    }

    public boolean canRun() {
        return this.resize != null;
    }

    private void tearDown() throws InvocationTargetException, IllegalAccessException {
        mc.gameSettings.hideGUI = hideGUI;
        mc.gameSettings.anaglyph = anaglyph;
        mc.gameSettings.thirdPersonView = thirdPersonView;
        mc.gameSettings.fovSetting = fovSetting;
        resize.invoke(mc, width, height);
        resize.setAccessible(accessable);
        player.setPositionAndRotation(pos.xCoord, pos.yCoord, pos.zCoord, yRot, xRot);
        player.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);
        FMLCommonHandler.instance().bus().unregister(this);
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    private float getYRotForStage() {
        switch (this.stage) {
            case 1:
                return -90;
            case 2:
                // South
                return 0.0f;
            case 3:
                return 90.0f;
            default:
                // North
                return -180.0f;
        }
    }

    private float getXRotForStage() {
        switch (this.stage) {
            case 4:
                return -90.0f;
            case 5:
                return 90.0f;
            default:
                return 0.0f;
        }
    }

    private void nextState() {
        this.stage = this.stage + 1;
        if (this.stage > 5) {
            try {
                this.tearDown();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return;
        }

        // Center on current block
        Vec3 pos = player.getPosition(0);
        pos.xCoord = Math.floor(pos.xCoord) + 0.5;
        pos.zCoord = Math.floor(pos.zCoord) + 0.5;

        // Calculate next index
        // North = 0, East, South, West, Up, Down = 5
        player.setPositionAndRotation(pos.xCoord, pos.yCoord, pos.zCoord, getYRotForStage(), getXRotForStage());
        player.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);

        this.shotSetUp = true;
        this.shotDone = false;
        this.wait = Config.FRAME_DELAY;
        System.out.println("Prepared " + this.stage);
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (this.wait > 0) {
            this.wait--;
            return;
        }
        if (this.shotDone) {
            nextState();
        } else {
            if (shotSetUp) {
                takeShot();
            } else {
                try {
                    this.setUp();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void takeShot() {
        System.out.println("TAKE SHOT " + this.stage);
        int width = 256;
        int height = 256;
        int pixelCount = width * height;

        IntBuffer pixelBuffer = BufferUtils.createIntBuffer(pixelCount);
        int[] values = new int[pixelCount];
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        if (OpenGlHelper.isFramebufferEnabled()) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        } else {
            GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        }
        pixelBuffer.get(values);
        TextureUtil.func_147953_a(values, width, height);
        BufferedImage bufferedImage = new BufferedImage(width, height, 1);
        bufferedImage.setRGB(0, 0, width, height, values, 0, width);

        try {
            ImageIO.write(bufferedImage, "png", new File(timedDir.getAbsolutePath(), "panorama_" + this.stage + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.shotDone = true;
    }
}
