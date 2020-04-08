package com.raoulvdberge.refinedpipes.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raoulvdberge.refinedpipes.RefinedPipes;
import com.raoulvdberge.refinedpipes.container.ExtractorAttachmentContainer;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.BlacklistWhitelist;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.ExtractorAttachment;
import com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor.RedstoneMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class ExtractorAttachmentScreen extends ContainerScreen<ExtractorAttachmentContainer> {
    private static final ResourceLocation RESOURCE = new ResourceLocation(RefinedPipes.ID, "textures/gui/extractor_attachment.png");

    private Button redstoneModeButton;
    private Button blacklistWhitelistButton;

    public ExtractorAttachmentScreen(ExtractorAttachmentContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);

        xSize = 176;
        ySize = 193;
    }

    @Override
    protected void init() {
        super.init();

        redstoneModeButton = addButton(new IconButton(
            this.guiLeft + 32,
            this.guiTop + 76,
            getRedstoneModeX(container.getRedstoneMode()),
            61,
            getRedstoneModeText(container.getRedstoneMode()),
            btn -> setRedstoneMode((IconButton) btn, container.getRedstoneMode().next())
        ));

        redstoneModeButton.active = container.getExtractorAttachmentType().getCanSetRedstoneMode();

        blacklistWhitelistButton = addButton(new IconButton(
            this.guiLeft + 55,
            this.guiTop + 76,
            getBlacklistWhitelistX(container.getBlacklistWhitelist()),
            82,
            getBlacklistWhitelistText(container.getBlacklistWhitelist()),
            btn -> setBlacklistWhitelist((IconButton) btn, container.getBlacklistWhitelist().next())
        ));

        blacklistWhitelistButton.active = container.getExtractorAttachmentType().getCanSetWhitelistBlacklist();
    }

    private void setRedstoneMode(IconButton button, RedstoneMode redstoneMode) {
        button.setMessage(getRedstoneModeText(redstoneMode));
        button.setIconTexX(getRedstoneModeX(redstoneMode));

        container.setRedstoneMode(redstoneMode);
    }

    private void setBlacklistWhitelist(IconButton button, BlacklistWhitelist blacklistWhitelist) {
        button.setMessage(getBlacklistWhitelistText(blacklistWhitelist));
        button.setIconTexX(getBlacklistWhitelistX(blacklistWhitelist));

        container.setBlacklistWhitelist(blacklistWhitelist);
    }

    private int getRedstoneModeX(RedstoneMode redstoneMode) {
        switch (redstoneMode) {
            case IGNORED:
                return 219;
            case HIGH:
                return 177;
            case LOW:
                return 198;
            default:
                return 0;
        }
    }

    private String getRedstoneModeText(RedstoneMode redstoneMode) {
        return I18n.format("misc.refinedpipes.redstone_mode." + redstoneMode.toString().toLowerCase());
    }

    private int getBlacklistWhitelistX(BlacklistWhitelist blacklistWhitelist) {
        switch (blacklistWhitelist) {
            case BLACKLIST:
                return 198;
            case WHITELIST:
                return 177;
            default:
                return 0;
        }
    }

    private String getBlacklistWhitelistText(BlacklistWhitelist blacklistWhitelist) {
        return I18n.format("misc.refinedpipes.mode." + blacklistWhitelist.toString().toLowerCase());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        font.drawString(title.getFormattedText(), 7, 7, 4210752);
        font.drawString(I18n.format("container.inventory"), 7, 103 - 4, 4210752);

        renderHoveredToolTip(mouseX - guiLeft, mouseY - guiTop);

        if (blacklistWhitelistButton.isHovered()) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add(I18n.format("misc.refinedpipes.mode"));
            tooltip.add(TextFormatting.GRAY + getBlacklistWhitelistText(container.getBlacklistWhitelist()));

            GuiUtils.drawHoveringText(tooltip, mouseX - guiLeft, mouseY - guiTop, width, height, -1, Minecraft.getInstance().fontRenderer);
        } else if (redstoneModeButton.isHovered()) {
            List<String> tooltip = new ArrayList<>();
            tooltip.add(I18n.format("misc.refinedpipes.redstone_mode"));
            tooltip.add(TextFormatting.GRAY + getRedstoneModeText(container.getRedstoneMode()));

            GuiUtils.drawHoveringText(tooltip, mouseX - guiLeft, mouseY - guiTop, width, height, -1, Minecraft.getInstance().fontRenderer);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        renderBackground();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(RESOURCE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);

        int x = 43;
        int y = 18;
        for (int filterSlotId = 1; filterSlotId <= ExtractorAttachment.MAX_FILTER_SLOTS; ++filterSlotId) {
            if (filterSlotId > container.getExtractorAttachmentType().getFilterSlots()) {
                this.blit(i + x, j + y, 198, 0, 18, 18);
            }

            if (filterSlotId % 5 == 0) {
                x = 43;
                y += 18;
            } else {
                x += 18;
            }
        }
    }
}
