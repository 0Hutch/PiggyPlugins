package net.runelite.client.plugins.ChinBreakHandler.ui;

import net.runelite.client.plugins.ChinBreakHandler.ChinBreakHandler;
import net.runelite.client.plugins.ChinBreakHandler.ChinBreakHandlerPlugin;
import net.runelite.client.plugins.ChinBreakHandler.util.DeferredDocumentChangedListener;
import net.runelite.client.plugins.ChinBreakHandler.util.ProfilesData;
import com.google.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.PluginPanel;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static net.runelite.client.ui.PluginPanel.PANEL_WIDTH;

public class ChinBreakHandlerAccountPanel extends JPanel
{
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(PANEL_WIDTH, super.getPreferredSize().height);
    }

    private final ConfigManager configManager;
    private final ChinBreakHandler chinBreakHandler;
    private final JPanel contentPanel = new JPanel(new GridLayout(0, 1));

    @Inject
    ChinBreakHandlerAccountPanel(ChinBreakHandlerPlugin chinBreakHandlerPluginPlugin, ChinBreakHandler chinBreakHandler)
    {
        this.configManager = chinBreakHandlerPluginPlugin.getConfigManager();
        this.chinBreakHandler = chinBreakHandler;

        setupDefaults();

        setLayout(new BorderLayout());
        setBackground(ChinBreakHandlerPanel.PANEL_BACKGROUND_COLOR);

        init();
    }

    private boolean getConfigValue()
    {
        String accountselection = configManager.getConfiguration("chinBreakHandler", "accountselection");

        return Boolean.parseBoolean(accountselection);
    }

    private void init()
    {
        contentPanel.setBorder(new EmptyBorder(10, 10, 0, 10));

        JPanel accountSelection = new JPanel(new GridLayout(0, 2));
        accountSelection.setBorder(new EmptyBorder(5, 0, 0, 0));
        ButtonGroup buttonGroup = new ButtonGroup();

        JCheckBox manualButton = new JCheckBox("Manual");
        JCheckBox profilesButton = new JCheckBox("Profiles plugin");

        String profilesSalt = configManager.getConfiguration("betterProfiles", "salt");
        boolean profilesSavePasswords = Boolean.parseBoolean(configManager.getConfiguration("betterProfiles", "rememberPassword"));

        if (profilesSalt == null || profilesSalt.length() == 0 || !profilesSavePasswords)
        {
            configManager.setConfiguration("chinBreakHandler", "accountselection", true);
            profilesButton.setEnabled(false);
        }

        manualButton.addActionListener(e -> {
            configManager.setConfiguration("chinBreakHandler", "accountselection", manualButton.isSelected());
            contentPanel(manualButton.isSelected());
        });

        profilesButton.addActionListener(e -> {
            configManager.setConfiguration("chinBreakHandler", "accountselection", !profilesButton.isSelected());
            contentPanel(!profilesButton.isSelected());
        });

        buttonGroup.add(manualButton);
        buttonGroup.add(profilesButton);

        boolean config = getConfigValue();

        manualButton.setSelected(config);
        profilesButton.setSelected(!config);

        accountSelection.add(manualButton);
        accountSelection.add(profilesButton);

        add(accountSelection, BorderLayout.NORTH);

        contentPanel(config);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void contentPanel(boolean manual)
    {
        contentPanel.removeAll();

        if (manual)
        {
            contentPanel.add(new JLabel("Username"));

            final JTextField usernameField = new JTextField();
            usernameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            usernameField.setText(configManager.getConfiguration("chinBreakHandler", "accountselection-manual-username"));
            DeferredDocumentChangedListener usernameListener = new DeferredDocumentChangedListener();
            usernameListener.addChangeListener(e ->
                    configManager.setConfiguration("chinBreakHandler", "accountselection-manual-username", usernameField.getText()));
            usernameField.getDocument().addDocumentListener(usernameListener);

            contentPanel.add(usernameField);

            contentPanel.add(new JLabel("Password"));

            final JPasswordField passwordField = new JPasswordField();
            passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            passwordField.setText(configManager.getConfiguration("chinBreakHandler", "accountselection-manual-password"));
            DeferredDocumentChangedListener passwordListener = new DeferredDocumentChangedListener();
            passwordListener.addChangeListener(e ->
                    configManager.setConfiguration("chinBreakHandler", "accountselection-manual-password", String.valueOf(passwordField.getPassword())));
            passwordField.getDocument().addDocumentListener(passwordListener);

            contentPanel.add(passwordField);
        }
        else if (ChinBreakHandlerPlugin.data == null)
        {
            contentPanel.add(new JLabel("Profiles plugin password"));
            final JPasswordField passwordField = new JPasswordField();
            passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel parsingLabel = new JLabel();
            parsingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            parsingLabel.setPreferredSize(new Dimension(PANEL_WIDTH, 15));

            DeferredDocumentChangedListener passwordListener = new DeferredDocumentChangedListener();
            passwordListener.addChangeListener(e ->
            {
                try
                {
                    ChinBreakHandlerPlugin.data = ProfilesData.getProfileData(configManager, passwordField.getPassword());
                    contentPanel(false);
                }
                catch (InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException ignored)
                {
                    parsingLabel.setText("Incorrect password!");
                }
            });
            passwordField.getDocument().addDocumentListener(passwordListener);

            contentPanel.add(passwordField);
            contentPanel.add(parsingLabel);
        }
        else
        {
            ConfigChanged configChanged = new ConfigChanged();
            configChanged.setGroup("mock");
            configChanged.setKey("mock");
            chinBreakHandler.configChanged.onNext(configChanged);

            if (!ChinBreakHandlerPlugin.data.contains(":"))
            {
                contentPanel.add(new JLabel("No accounts found"));
            }
            else
            {
                contentPanel.add(new JLabel("Select account"));

                String[] accounts = Arrays.stream(ChinBreakHandlerPlugin.data.split("\\n"))
                        .map((s) -> s.split(":")[0])
                        .sorted()
                        .toArray(String[]::new);

                JComboBox<String> filterComboBox = new JComboBox<>(accounts);
                filterComboBox.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
                filterComboBox.addActionListener(e -> {
                    if (filterComboBox.getSelectedItem() != null)
                    {
                        configManager.setConfiguration("chinBreakHandler", "accountselection-profiles-account", filterComboBox.getSelectedItem().toString());
                    }
                });

                String config = configManager.getConfiguration("chinBreakHandler", "accountselection-profiles-account");

                if (config != null)
                {
                    int index = Arrays.asList(accounts).indexOf(config);

                    if (index != -1)
                    {
                        filterComboBox.setSelectedIndex(index);
                    }
                    else
                    {
                        filterComboBox.setSelectedIndex(0);
                    }
                }

                contentPanel.add(filterComboBox);
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void setupDefaults()
    {
        if (configManager.getConfiguration("chinBreakHandler", "accountselection") == null)
        {
            configManager.setConfiguration("chinBreakHandler", "accountselection", true);
        }

        if (configManager.getConfiguration("chinBreakHandler", "accountselection-manual-username") == null)
        {
            configManager.setConfiguration("chinBreakHandler", "accountselection-manual-username", "");
        }

        if (configManager.getConfiguration("chinBreakHandler", "accountselection-manual-password") == null)
        {
            configManager.setConfiguration("chinBreakHandler", "accountselection-manual-password", "");
        }

        if (configManager.getConfiguration("chinBreakHandler", "accountselection-profiles-account") == null)
        {
            configManager.setConfiguration("chinBreakHandler", "accountselection-profiles-account", "");
        }
    }
}