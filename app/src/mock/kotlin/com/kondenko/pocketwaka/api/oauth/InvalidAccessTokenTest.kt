package com.kondenko.pocketwaka.api.oauth

import com.kondenko.pocketwaka.data.auth.model.AccessToken

class InvalidAccessTokenTest(
        override var access_token: String = "sec_EbSZXye0Itq18jDeCeG53xNsPOkxB2ptRCUSZG3BXoqXwtNVa2WLRjcB4jKELykjmzUu5kRUMFPI1fQm",
        override var expires_in: Double = 5184000.0,
        override var refresh_token: String = "ref_Vt2PVpd5IvSZJ2ygJVUKSoEryPPIsZxUSXTdwZV3ESt33jfxdUIl4vdgYBeNrxxBdjQ1kbNRbb6i3dlD",
        override var scope: String = "email,read_stats,read_logged_time,read_teams",
        override var token_type: String = "bearer",
        override var uid: String = "0974f3f9-7f4e-4ccf-91bd-f4176dc3c94e",
        override var created_at: Float = 1419120000f
) : AccessToken(access_token, expires_in, refresh_token, scope, token_type, uid, created_at)