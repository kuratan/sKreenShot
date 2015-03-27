package de.kuratan.skreenshot;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.command.WrongUsageException;
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
import java.util.Arrays;
import java.util.Date;

public abstract class ScreenShoter {

    protected Minecraft mc;
    protected int displayWidth;
    protected int displayHeight;
    protected int height;
    protected int width;
    protected boolean accessable;
    protected boolean anaglyph;
    protected boolean hideGUI;
    protected int wait;
    protected Vec3 pos;
    protected float yRot;
    protected float xRot;
    protected boolean shotSetUp;
    protected boolean shotDone;
    protected int thirdPersonView;
    protected File timedDir;
    protected float fovSetting;
    protected Method resize;
    protected EntityPlayer player;

    public ScreenShoter(EntityPlayer player, int width, int height) {
        this.player = player;
        this.mc = Minecraft.getMinecraft();
        this.resize = null;
        this.width = width;
        this.height = height;
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
            this.accessable = this.resize.isAccessible();
            this.resize.setAccessible(true);
            MinecraftForge.EVENT_BUS.register(this);
            FMLCommonHandler.instance().bus().register(this);
        } else {
            throw new WrongUsageException("Can't resize...");
        }
    }

    protected void setUp() throws InvocationTargetException, IllegalAccessException {
        File dir = new File(Config.BASE_DIR, "skreenshot");
        String time = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        this.timedDir = new File(dir.getAbsolutePath(), time);
        if (!this.timedDir.exists()) {
            this.timedDir.mkdirs();
        }

        this.hideGUI = this.mc.gameSettings.hideGUI;
        this.anaglyph = this.mc.gameSettings.anaglyph;
        this.displayWidth = this.mc.displayWidth;
        this.displayHeight = this.mc.displayHeight;
        this.pos = this.player.getPosition(0);
        this.yRot = this.player.rotationYaw;
        this.xRot = this.player.rotationPitch;
        this.thirdPersonView = mc.gameSettings.thirdPersonView;
        this.fovSetting = mc.gameSettings.fovSetting;

        this.mc.gameSettings.anaglyph = false;
        this.mc.gameSettings.hideGUI = true;
        this.mc.gameSettings.thirdPersonView = 0;
        this.mc.gameSettings.fovSetting = 90.0f;
        this.resize.invoke(this.mc, this.width, this.height);
        this.shotDone = true;
    }

    public boolean canRun() {
        return this.resize != null;
    }

    protected void tearDown() throws InvocationTargetException, IllegalAccessException {
        this.mc.gameSettings.hideGUI = this.hideGUI;
        this.mc.gameSettings.anaglyph = this.anaglyph;
        this.mc.gameSettings.thirdPersonView = this.thirdPersonView;
        this.mc.gameSettings.fovSetting = this.fovSetting;
        this.resize.invoke(this.mc, this.displayWidth, this.displayHeight);
        this.resize.setAccessible(this.accessable);
        this.player.setPositionAndRotation(this.pos.xCoord, this.pos.yCoord, this.pos.zCoord, this.yRot, this.xRot);
        this.player.setPositionAndUpdate(this.pos.xCoord, this.pos.yCoord, this.pos.zCoord);
        FMLCommonHandler.instance().bus().unregister(this);
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    protected void takeShot(String name) {
        if (!canRun()) {
            this.shotDone = true;
            return;
        }
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
            ImageIO.write(bufferedImage, "png", new File(timedDir.getAbsolutePath(), name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.shotDone = true;
    }

    static enum Stage {
        INIT(-1), NORTH(0), EAST(1), SOUTH(2), WEST(3), UP(4), DOWN(5), DONE(-1);

        private int value;

        Stage(int value) {
            this.value = value;
        }

        public boolean isHelper() {
            return this.value < 0;
        }

        public int getValue() {
            return value;
        }

        public Stage next() {
            try {
                return Stage.values()[Arrays.asList(Stage.values()).indexOf(this) + 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                return this;
            }
        }

        public Stage prev() {
            try {
                return Stage.values()[Arrays.asList(Stage.values()).indexOf(this) - 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                return this;
            }
        }
    }
}
