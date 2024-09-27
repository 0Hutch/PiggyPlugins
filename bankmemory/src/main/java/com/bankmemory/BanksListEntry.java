package com.bankmemory;

import com.bankmemory.data.BankWorldType;
import javax.swing.ImageIcon;
import lombok.Value;

@Value
public class BanksListEntry {
    long saveId;
    ImageIcon icon;
    BankWorldType worldType;
    String saveName;
    String accountDisplayName;
    String dateTime;
}
