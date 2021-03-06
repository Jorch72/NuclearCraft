package nc.radiation;

import nc.Global;
import nc.capability.radiation.IEntityRads;
import nc.config.NCConfig;
import nc.init.NCItems;
import nc.util.GuiHelper;
import nc.util.RadiationHelper;
import nc.util.TextHelper;
import nc.util.UnitHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/* Originally from coolAlias' 'Tutorial-Demo' - tutorial.client.gui.GuiManaBar */

public class RadiationOverlayHandler extends Gui {
	
	private final Minecraft mc;
	
	private static final ResourceLocation RADS_BAR = new ResourceLocation(Global.MOD_ID + ":textures/hud/" + "rads_bar" + ".png");
	
	public RadiationOverlayHandler(Minecraft mc) {
		this.mc = mc;
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	@SideOnly(Side.CLIENT)
	public void addRadiationInfo(RenderGameOverlayEvent.Post event) {
		if(event.getType() != ElementType.HOTBAR) return;
		final EntityPlayer player = mc.player;
		if (!player.inventory.hasItemStack(new ItemStack(NCItems.geiger_counter)) || !player.hasCapability(IEntityRads.CAPABILITY_ENTITY_RADS, null)) return;
		IEntityRads playerRads = player.getCapability(IEntityRads.CAPABILITY_ENTITY_RADS, null);
		
		ScaledResolution res = new ScaledResolution(mc);
		int barWidth = (int)(100D*playerRads.getTotalRads()/playerRads.getMaxRads());
		String info = playerRads.isRadiationNegligible() ? "0 Rads/t" : UnitHelper.prefix(playerRads.getRadiationLevel(), 3, "Rads/t", 0, -8);
		int infoWidth = mc.fontRenderer.getStringWidth(info);
		int overlayWidth = Math.max(104, infoWidth);
		int overlayHeight = 19;
		
		int xPos = GuiHelper.getRenderPositionXFromAngle(res, NCConfig.radiation_hud_position, overlayWidth, 3);
		int yPos = GuiHelper.getRenderPositionYFromAngle(res, NCConfig.radiation_hud_position, overlayHeight, 3);
		
		mc.getTextureManager().bindTexture(RADS_BAR);
		
		drawTexturedModalRect(xPos, yPos, 0, 0, 104, 10);
		drawTexturedModalRect(xPos + 2 + 100 - barWidth, yPos + 2, 100 - barWidth, 10, barWidth, 6);
		yPos += 12;
		mc.fontRenderer.drawString(info, xPos + (104 - infoWidth)/2, yPos, TextHelper.getFormatColor(RadiationHelper.getRadiationTextColor(playerRads)));
	}
}
