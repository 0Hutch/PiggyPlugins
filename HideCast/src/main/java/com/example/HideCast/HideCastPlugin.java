package com.example.HideCast;

import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.events.ClientTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

@PluginDescriptor(
        name = "<html><font color=\"#ff4d00\">[GS]</font> Hide Cast </html>",
        description = "Elimina las opciones del menú de reparto para los miembros del clan.",
        enabledByDefault = false,
        tags = {"ElGuason"}
)
public class HideCastPlugin extends Plugin {
    @Inject
    private Client client;
    private static final List<MenuAction> PLAYER_MENU_TYPES;

    public HideCastPlugin() {
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        if (this.client.getGameState() == GameState.LOGGED_IN && !this.client.isMenuOpen()) {
            MenuEntry[] menuEntries = this.client.getMenuEntries();
            int idx = 0;
            MenuEntry[] var4 = menuEntries;
            int var5 = menuEntries.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                MenuEntry entry = var4[var6];
                this.findCastAndDelete(menuEntries, idx++, entry);
            }

        }
    }

    private void findCastAndDelete(MenuEntry[] menuEntries, int index, MenuEntry menuEntry) {
        MenuAction menuAction = menuEntry.getType();
        String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
        if (PLAYER_MENU_TYPES.contains(menuAction)) {
            Player player = menuEntry.getPlayer();

            assert player != null;

            boolean isAttack = option.contains("Attack".toLowerCase());
            boolean isCast = option.contains("Cast".toLowerCase());
            if ((isAttack || isCast) && player.isFriendsChatMember()) {
                this.client.setMenuEntries((MenuEntry[])ArrayUtils.remove(menuEntries, index));
            }
        }

    }

    static {
        PLAYER_MENU_TYPES = List.of(MenuAction.WIDGET_TARGET_ON_PLAYER, MenuAction.PLAYER_FIRST_OPTION, MenuAction.PLAYER_SECOND_OPTION, MenuAction.PLAYER_THIRD_OPTION, MenuAction.PLAYER_FOURTH_OPTION, MenuAction.PLAYER_FIFTH_OPTION, MenuAction.PLAYER_SIXTH_OPTION, MenuAction.PLAYER_SEVENTH_OPTION, MenuAction.PLAYER_EIGTH_OPTION);
    }
}
