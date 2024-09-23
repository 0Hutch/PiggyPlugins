/*
 * Copyright (c) 2019, Spedwards <https://github.com/Spedwards>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.betterprofiles;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Better Profiles</html>",
        description = "Allow for a allows you to easily switch between multiple OSRS Accounts - Ported by Piggy",
        tags = {"profile", "account", "login", "log in", "pklite"}
)
@Slf4j
public class BetterProfilesPlugin extends Plugin {
    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private BetterProfilesConfig config;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ScheduledExecutorService executorService;

    private BetterProfilesPanel panel;
    private NavigationButton navButton;
    private BufferedImage icon;

    @Provides
    BetterProfilesConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BetterProfilesConfig.class);
    }

    @Override
    protected void startUp() {
        panel = injector.getInstance(BetterProfilesPanel.class);
        panel.init();

        if (icon == null) {
            icon = ImageUtil.loadImageResource(getClass(), "profiles_icon.png");
        }

        if (icon != null) {
            navButton = NavigationButton.builder()
                    .tooltip("Profiles")
                    .icon(icon)
                    .priority(8)
                    .panel(panel)
                    .build();

            clientToolbar.addNavigation(navButton);
        } else {
            log.error("Failed to load profiles_icon.png");
        }
    }

    @Override
    protected void shutDown() {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {

    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("piggyProfiles")) {
            if (event.getKey().equals("rememberPassword")) {
                panel = injector.getInstance(BetterProfilesPanel.class);
                this.shutDown();
                this.startUp();
            }
            if (!event.getKey().equals("rememberPassword")) {
                panel = injector.getInstance(BetterProfilesPanel.class);
                panel.redrawProfiles();
            }
        }
    }

    private void openPanel() {

    }

}
