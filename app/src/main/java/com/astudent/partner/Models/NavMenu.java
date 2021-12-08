package com.astudent.partner.Models;

import com.astudent.partner.R;

public enum NavMenu {
    HOME(R.string.menu_home),
    PROFILE(R.string.profile2),
    SETTINGS(R.string.menu_settings),
    LANGUAGE(R.string.language),
    SHARE(R.string.menu_share),
    CONTACT_SHARE(R.string.menu_contact_share),
    LOGOUT(R.string.menu_logout),
    CITIES(R.string.cities),
    REVOLUTION(R.string.revolution),
    LEGAL(R.string.legal),
    HISTORY(R.string.menu_history),
    SUMMARY(R.string.summary),
    HELP(R.string.help),
    EARNINGS(R.string.earnings);

    private int stringId;

    NavMenu(int stringId) {
        this.stringId = stringId;
    }

    public int getStringId() {
        return stringId;
    }

}
