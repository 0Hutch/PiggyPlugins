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

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.annotation.Nullable;
import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
class BetterProfilesPanel extends PluginPanel {
    private static final int iterations = 100000;
    private static final String UNLOCK_PASSWORD = "Encryption Password";
    private static final String ACCOUNT_USERNAME = "Account Username";
    private static final String ACCOUNT_LABEL = "Account Label";
    private static final String PASSWORD_LABEL = "Account Password";
    private static final String PIN_LABEL = "Bank Pin";
    private static final String PIN_TOOLTIP = "This pin is used by plugins for banking.";
    private static final String HELP = "To add and load accounts, first enter a password into the Encryption Password " +
            "field then press %s. <br /><br /> You can now add as many accounts as you would like. <br /><br /> The next time you restart " +
            "RuneLite, enter your encryption password and click on account to auto-fill your data.";

    @Inject
    @Nullable
    private Client client;

    @Inject
    private BetterProfilesConfig betterProfilesConfig;

    private DocumentFilter digitFilter;

    private final JPasswordField txtDecryptPassword = new JPasswordField(UNLOCK_PASSWORD);
    private final JTextField txtAccountLabel = new JTextField(ACCOUNT_LABEL);
    private final JPasswordField txtAccountLogin = new JPasswordField(ACCOUNT_USERNAME);
    private final JPasswordField txtPasswordLogin = new JPasswordField(PASSWORD_LABEL);
    private final JPasswordField pinLogin = new JPasswordField(PIN_LABEL);
    private final JPanel profilesPanel = new JPanel();
    private final JPanel accountPanel = new JPanel();
    private final JPanel loginPanel = new JPanel();

    void init() {
        final String LOAD_ACCOUNTS = betterProfilesConfig.salt().isEmpty() ? "Save" : "Unlock";

        setLayout(new BorderLayout(0, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        final Font smallFont = FontManager.getRunescapeSmallFont();

        JPanel helpPanel = new JPanel();
        helpPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        helpPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        helpPanel.setLayout(new DynamicGridLayout(1, 1));

        JLabel helpLabel = new JLabel(htmlLabel(String.format(HELP, betterProfilesConfig.salt().isEmpty() ? "save" : "unlock")));
        helpLabel.setFont(smallFont);

        helpPanel.add(helpLabel);

        loginPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        loginPanel.setBorder(new EmptyBorder(10, 10, 10, 3));
        loginPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));

        // Setup for txtDecryptPassword
        setupPasswordField(txtDecryptPassword, UNLOCK_PASSWORD);

        // Create and set action for the button
        Action loadAccountsAction = new AbstractAction(LOAD_ACCOUNTS) {
            @Override
            public void actionPerformed(ActionEvent e) {
                decryptAccounts();
            }
        };

        JButton btnLoadAccounts = new JButton(loadAccountsAction);
        btnLoadAccounts.setToolTipText(LOAD_ACCOUNTS);

        // Binding "Enter" key to the action for txtDecryptPassword
        txtDecryptPassword.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "loadAccounts");
        txtDecryptPassword.getActionMap().put("loadAccounts", loadAccountsAction);

        loginPanel.add(txtDecryptPassword);
        loginPanel.add(btnLoadAccounts);

        accountPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        accountPanel.setBorder(new EmptyBorder(10, 10, 10, 3));
        accountPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));

        // Setup for txtAccountLabel
        setupTextField(txtAccountLabel, ACCOUNT_LABEL);

        // Setup for txtAccountLogin
        setupPasswordField(txtAccountLogin, ACCOUNT_USERNAME, betterProfilesConfig.streamerMode());

        // Setup for txtPasswordLogin
        setupPasswordField(txtPasswordLogin, PASSWORD_LABEL);

        // Setup for pinLogin
        setupPasswordField(pinLogin, PIN_LABEL);

        digitFilter = new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);

                if (newText.matches("\\d{0,4}")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + string + currentText.substring(offset);

                if (newText.matches("\\d{0,4}")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + currentText.substring(offset + length);

                if (newText.matches("\\d{0,4}")) {
                    super.remove(fb, offset, length);
                }
            }
        };

        PlainDocument pinDocument = (PlainDocument) pinLogin.getDocument();
        pinDocument.setDocumentFilter(digitFilter);

        JButton btnAddAccount = new JButton("Add Account");
        btnAddAccount.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        btnAddAccount.addActionListener(e -> addAccount());

        txtAccountLogin.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnAddAccount.doClick();
                    btnAddAccount.requestFocus();
                }
            }
        });

        accountPanel.add(txtAccountLabel);
        accountPanel.add(txtAccountLogin);
        if (betterProfilesConfig.rememberPassword()) {
            accountPanel.add(txtPasswordLogin);
        }
        accountPanel.add(pinLogin);
        accountPanel.add(btnAddAccount);

        add(helpPanel, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);

        // addAccounts(config.profilesData());
    }

    private void setupTextField(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                }
            }
        });
    }

    private void setupPasswordField(JPasswordField passwordField, String placeholder) {
        setupPasswordField(passwordField, placeholder, false);
    }

    private void setupPasswordField(JPasswordField passwordField, String placeholder, boolean useEchoChar) {
        passwordField.setText(placeholder);
        passwordField.setEchoChar((char) 0);
        passwordField.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        passwordField.setToolTipText(placeholder);
        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                }
                passwordField.setEchoChar('*');
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setText(placeholder);
                    passwordField.setEchoChar((char) 0);
                }
            }
        });
    }

    private void addAccount() {
        String labelText = txtAccountLabel.getText();
        String loginText = String.valueOf(txtAccountLogin.getPassword());
        String passwordText = String.valueOf(txtPasswordLogin.getPassword());
        String pinText = String.valueOf(pinLogin.getPassword());

        if (labelText.equals(ACCOUNT_LABEL) || loginText.equals(ACCOUNT_USERNAME)) {
            return;
        }
        if (labelText.contains(":") || loginText.contains(":")) {
            JOptionPane.showMessageDialog(null, "You may not use colons in your label or login name", "Account Switcher", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String data;
        if (betterProfilesConfig.rememberPassword() && txtPasswordLogin.getPassword() != null) {
            data = labelText + ":" + loginText + ":" + passwordText + ":" + pinText;
        } else {
            data = labelText + ":" + loginText + ":" + pinText;
        }

        try {
            if (!addProfile(data)) {
                return;
            }
            redrawProfiles();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException |
                 IllegalBlockSizeException | InvalidKeyException | BadPaddingException |
                 NoSuchPaddingException ex) {
            log.error(ex.toString());
        }

        txtAccountLabel.setText(ACCOUNT_LABEL);
        txtAccountLogin.setText(ACCOUNT_USERNAME);
        txtAccountLogin.setEchoChar((char) 0);
        txtPasswordLogin.setText(PASSWORD_LABEL);
        txtPasswordLogin.setEchoChar((char) 0);
        PlainDocument pinDocument = (PlainDocument) pinLogin.getDocument();
        pinDocument.setDocumentFilter(null);
        pinLogin.setText(PIN_LABEL);
        pinLogin.setEchoChar((char) 0);
        pinDocument.setDocumentFilter(digitFilter);
    }

    private void decryptAccounts() {
        if (txtDecryptPassword.getPassword().length == 0 || String.valueOf(txtDecryptPassword.getPassword()).equals(UNLOCK_PASSWORD)) {
            log.debug("Unable to load data -- Please enter a password!");
            return;
        }

        boolean error = false;
        log.debug("Attempting to decrypt accounts with provided password.");
        redrawProfiles();

        if (error) return;

        remove(loginPanel);
        add(accountPanel, BorderLayout.CENTER);

        profilesPanel.setLayout(new DynamicGridLayout(0, 1, 0, 3));
        add(profilesPanel, BorderLayout.SOUTH);
    }

    void redrawProfiles() {
        try {
            profilesPanel.removeAll();
            String profileData = getProfileData();
            addAccounts(profileData);
            revalidate();
            repaint();
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage()); // This basically suppresses the big error block - it says incorrect padding
        }
    }

    private void addAccount(String data) {
        try {
            BetterProfilePanel profile = new BetterProfilePanel(client, data, betterProfilesConfig, this);
            profilesPanel.add(profile);
            revalidate();
            repaint();
        } catch (Exception e) {
            log.warn("Error processing profile data: {}", e.getMessage());
        }
    }

    private void addAccounts(String data) {
        data = data.trim();
        if (!data.contains(":")) {
            return;
        }
        Arrays.stream(data.split("\\n")).forEach(this::addAccount);
    }

    private boolean addProfile(String data) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        log.debug("Adding profile data: {}", data);
        String currentData = getProfileData();
        log.debug("Current profile data: {}", currentData);

        String updatedData = currentData.isEmpty() ? data : currentData + "\n" + data;
        log.debug("Updated profile data: {}", updatedData);

        return setProfileData(updatedData);
    }

    void removeProfile(String data) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        String currentData = getProfileData();

        // Ensure each profile data entry is on a new line and remove the specific profile data
        String[] profiles = currentData.split("\\n");
        StringBuilder updatedData = new StringBuilder();

        for (String profile : profiles) {
            if (!profile.trim().equals(data.trim())) {
                if (updatedData.length() > 0) {
                    updatedData.append("\\n");
                }
                updatedData.append(profile.trim());
            }
        }

        String finalData = updatedData.toString();

        // Set the updated profile data
        if (setProfileData(finalData)) {
            log.debug("Profile data successfully updated.");
        } else {
            log.error("Failed to update profile data.");
        }

        revalidate();
        repaint();
    }

    private void setSalt(byte[] bytes) {
        betterProfilesConfig.salt(base64Encode(bytes));
    }

    private byte[] getSalt() {
        if (betterProfilesConfig.salt().isEmpty()) {
            return new byte[0];
        }
        return base64Decode(betterProfilesConfig.salt());
    }

    private SecretKey getAesKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (getSalt().length == 0) {
            byte[] b = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(b);
            setSalt(b);
        }
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(txtDecryptPassword.getPassword(), getSalt(), iterations, 128);
        return factory.generateSecret(spec);
    }

    private String getProfileData() throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        String tmp = betterProfilesConfig.profilesData();
        if (tmp.startsWith("¬")) {
            tmp = tmp.substring(1);
            byte[] decoded = base64Decode(tmp);

            if (decoded.length % 16 != 0) {
                throw new IllegalStateException("Encrypted data length is not a multiple of 16 bytes");
            }

            return decryptText(decoded, getAesKey());
        }
        return tmp;
    }

    private boolean setProfileData(String data) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        if (txtDecryptPassword.getPassword().length == 0 || String.valueOf(txtDecryptPassword.getPassword()).equals(UNLOCK_PASSWORD)) {
            showErrorMessage("Unable to save data", "Please enter a password!");
            return false;
        }

        if (data == null || data.trim().isEmpty()) {
            log.error("Profile data is null or empty.");
            betterProfilesConfig.profilesData("");  // Set the config to an empty string
            return true;
        }

        byte[] enc = encryptText(data, getAesKey());
        if (enc.length == 0) {
            return false;
        }
        String s = "¬" + base64Encode(enc);
        betterProfilesConfig.profilesData(s);
        return true;
    }

    private byte[] base64Decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    private String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Encrypts login info
     *
     * @param text text to encrypt
     * @return encrypted string
     */
    private static byte[] encryptText(String text, SecretKey aesKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        if (text == null || text.isEmpty()) {
            log.debug("Encrypting empty text.");
            return new byte[0];  // Return an empty byte array for empty text
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec newKey = new SecretKeySpec(aesKey.getEncoded(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, newKey);
        return cipher.doFinal(text.getBytes());
    }

    private static String decryptText(byte[] enc, SecretKey aesKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        if (enc == null || enc.length == 0) {
            log.debug("Decrypting empty data.");
            return "";  // Return an empty string for empty byte array
        }
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec newKey = new SecretKeySpec(aesKey.getEncoded(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, newKey);
        return new String(cipher.doFinal(enc));
    }



    private static void showErrorMessage(String title, String text) {
        //TODO FIX
    }

    private static String htmlLabel(String text) {
        return "<html><body><span style = 'color:white'>" + text + "</span></body></html>";
    }
}
