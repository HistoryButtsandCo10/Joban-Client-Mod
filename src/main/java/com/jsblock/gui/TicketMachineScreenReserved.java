package com.jsblock.gui;

import minecraftmappings.Utilities;
import minecraftmappings.UtilitiesClient;
import mtr.data.IGui;
import mtr.gui.IDrawing;
import mtr.packet.IPacket;
import mtr.packet.PacketTrainDataGuiClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TicketMachineScreenReserved extends Screen implements IGui, IPacket {

    private final ButtonWidget[] buttons = new ButtonWidget[BAL_BUTTON_COUNT];
    private final ButtonWidget[] ticketButtons = new ButtonWidget[TICKET_BUTTON_COUNT];
    private final Text balanceText;

    private static final int EMERALD_TO_DOLLAR = 10;
    private static final int BAL_BUTTON_COUNT = 10;
    private static final int TICKET_BUTTON_COUNT = 3;
    private static final int BUTTON_WIDTH = 80;
    private static final int SEPARATOR_MARGIN = 2;

    public TicketMachineScreenReserved(int balance) {
        super(new LiteralText(""));

        for (int i = 0; i < BAL_BUTTON_COUNT; i++) {
            final int index = i;
            buttons[i] = new ButtonWidget(0, 0, 0, SQUARE_SIZE, new TranslatableText("gui.mtr.add_value"), button -> {
                PacketTrainDataGuiClient.addBalanceC2S(getAddAmount(index), (int) Math.pow(2, index));
                if (client != null) {
                    UtilitiesClient.setScreen(client, null);
                }
            });
        }

        for (int i = 0; i < TICKET_BUTTON_COUNT; i++) {
            final int index = i;
            ticketButtons[i] = new ButtonWidget(0, 0, 0, SQUARE_SIZE, new TranslatableText("gui.jsblock.buy_ticket"), button -> {
                PacketTrainDataGuiClient.addBalanceC2S(getAddAmount(index), (int) Math.pow(2, index));
                if (client != null) {
                    UtilitiesClient.setScreen(client,null);
                }
            });
        }

        balanceText = new TranslatableText("gui.mtr.balance", balance);
    }

    @Override
    protected void init() {
        super.init();

        for (int i = 0; i < BAL_BUTTON_COUNT; i++) {
            IDrawing.setPositionAndWidth(buttons[i], width - BUTTON_WIDTH, SQUARE_SIZE * (i + 1), BUTTON_WIDTH - TEXT_FIELD_PADDING);
        }

        for (int i = 0; i < TICKET_BUTTON_COUNT; i++) {
            IDrawing.setPositionAndWidth(ticketButtons[i], width - BUTTON_WIDTH, (SQUARE_SIZE * (i + 1)) + (SQUARE_SIZE * BAL_BUTTON_COUNT) + (SQUARE_SIZE * SEPARATOR_MARGIN), BUTTON_WIDTH - TEXT_FIELD_PADDING);
        }

        for (final ButtonWidget button : buttons) {
            addDrawableChild(button);
        }

        for (final ButtonWidget button : ticketButtons) {
            addDrawableChild(button);
        }
    }

    @Override
    public void tick() {
        final int emeraldCount = getEmeraldCount();
        for (int i = 0; i < BAL_BUTTON_COUNT; i++) {
            buttons[i].active = emeraldCount >= Math.pow(2, i);
        }

        for (int i = 0; i < TICKET_BUTTON_COUNT; i++) {
            ticketButtons[i].active = emeraldCount >= Math.pow(2, i);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        try {
            renderBackground(matrices);
            final Text emeraldsText = new TranslatableText("gui.mtr.emeralds", getEmeraldCount());
            final Text ticketsText = new TranslatableText("gui.jsblock.ticket_machine_tickets");
            textRenderer.draw(matrices, balanceText, TEXT_PADDING, TEXT_PADDING, ARGB_WHITE);
            textRenderer.draw(matrices, emeraldsText, width - TEXT_PADDING - textRenderer.getWidth(emeraldsText), TEXT_PADDING, ARGB_WHITE);
            textRenderer.draw(matrices, ticketsText, width / 2, (BAL_BUTTON_COUNT + 2) * SQUARE_SIZE, ARGB_WHITE);

            for (int i = 0; i < BAL_BUTTON_COUNT; i++) {
                textRenderer.draw(matrices, new TranslatableText("gui.mtr.add_balance_for_emeralds", getAddAmount(i), (int) Math.pow(2, i)), TEXT_PADDING, (i + 1) * SQUARE_SIZE + TEXT_PADDING, ARGB_WHITE);
            }

            super.render(matrices, mouseX, mouseY, delta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getEmeraldCount() {
        if (client != null && client.player != null) {
            return Utilities.getInventory(client.player).count(Items.EMERALD);
        } else {
            return 0;
        }
    }

    private static int getAddAmount(int index) {
        return (int) Math.ceil(Math.pow(2, index) * (EMERALD_TO_DOLLAR + index));
    }
}
