/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ketie.app.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import me.ketie.app.android.bean.UserInfo;

public class UserInfoKeeper {
    private static final String PREFERENCES_NAME = "_userinfo";

    private static final String KEY_NICK = "_nickname";
    private static final String KEY_IMG = "_headimg";

    public static void writeUser(Context context, UserInfo user) {
        if (null == context || null == user) {
            return;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString(KEY_NICK, user.nickname);
        editor.putString(KEY_IMG, user.img);
        editor.commit();
    }

    public static UserInfo readUser(Context context) {
        if (null == context) {
            return null;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        String nickname = pref.getString(KEY_NICK, "");
        String img = pref.getString(KEY_IMG, "");
        return new UserInfo(nickname, img);
    }

    public static void clear(Context context) {
        if (null == context) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
