/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.android.contacts.model.account;

import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.android.contacts.R;
import com.android.contacts.model.dataitem.DataKind;
import com.android.contactsbind.FeedbackHelper;

import com.google.common.collect.Lists;

import java.util.List;

public class GoogleAccountType extends BaseAccountType {
    private static final String TAG = "GoogleAccountType";

    /**
     * The package name that we should load contacts.xml from and rely on to handle
     * G+ account actions. Even though this points to gms, in some cases gms will still hand
     * off responsibility to the G+ app.
     */
    public static final String PLUS_EXTENSION_PACKAGE_NAME = "com.google.android.gms";

    public static final String ACCOUNT_TYPE = "com.google";

    private static final List<String> mExtensionPackages =
            Lists.newArrayList(PLUS_EXTENSION_PACKAGE_NAME);

    public GoogleAccountType(Context context, String authenticatorPackageName) {
        this.accountType = ACCOUNT_TYPE;
        this.resourcePackageName = null;
        this.syncAdapterPackageName = authenticatorPackageName;

        try {
            addDataKindStructuredName(context);
            addDataKindName(context);
            addDataKindPhoneticName(context);
            addDataKindNickname(context);
            addDataKindPhone(context);
            addDataKindEmail(context);
            addDataKindStructuredPostal(context);
            addDataKindIm(context);
            addDataKindOrganization(context);
            addDataKindPhoto(context);
            addDataKindNote(context);
            addDataKindWebsite(context);
            addDataKindSipAddress(context);
            addDataKindGroupMembership(context);
            addDataKindRelation(context);
            addDataKindEvent(context);
            addDataKindCustomField(context);

            mIsInitialized = true;
        } catch (DefinitionException e) {
            FeedbackHelper.sendFeedback(context, TAG, "Failed to build google account type", e);
        }
    }

    @Override
    public List<String> getExtensionPackageNames() {
        return mExtensionPackages;
    }

    @Override
    protected DataKind addDataKindPhone(Context context) throws DefinitionException {
        final DataKind kind = super.addDataKindPhone(context);

        kind.typeColumn = Phone.TYPE;
        kind.typeList = Lists.newArrayList();
        kind.typeList.add(buildPhoneType(Phone.TYPE_MOBILE));
        kind.typeList.add(buildPhoneType(Phone.TYPE_WORK));
        kind.typeList.add(buildPhoneType(Phone.TYPE_HOME));
        kind.typeList.add(buildPhoneType(Phone.TYPE_MAIN));
        kind.typeList.add(buildPhoneType(Phone.TYPE_FAX_WORK).setSecondary(true));
        kind.typeList.add(buildPhoneType(Phone.TYPE_FAX_HOME).setSecondary(true));
        kind.typeList.add(buildPhoneType(Phone.TYPE_PAGER).setSecondary(true));
        kind.typeList.add(buildPhoneType(Phone.TYPE_OTHER));
        kind.typeList.add(buildPhoneType(Phone.TYPE_CUSTOM).setSecondary(true)
                .setCustomColumn(Phone.LABEL));

        kind.fieldList = Lists.newArrayList();
        kind.fieldList.add(new EditField(Phone.NUMBER, R.string.phoneLabelsGroup, FLAGS_PHONE));

        return kind;
    }

    @Override
    protected DataKind addDataKindEmail(Context context) throws DefinitionException {
        final DataKind kind = super.addDataKindEmail(context);

        kind.typeColumn = Email.TYPE;
        kind.typeList = Lists.newArrayList();
        kind.typeList.add(buildEmailType(Email.TYPE_HOME));
        kind.typeList.add(buildEmailType(Email.TYPE_WORK));
        kind.typeList.add(buildEmailType(Email.TYPE_OTHER));
        kind.typeList.add(buildEmailType(Email.TYPE_CUSTOM).setSecondary(true).setCustomColumn(
                Email.LABEL));

        kind.fieldList = Lists.newArrayList();
        kind.fieldList.add(new EditField(Email.DATA, R.string.emailLabelsGroup, FLAGS_EMAIL));

        return kind;
    }

    @Override
    public boolean isGroupMembershipEditable() {
        return true;
    }

    @Override
    public boolean areContactsWritable() {
        return true;
    }

    @Override
    public String getViewContactNotifyServiceClassName() {
        return "com.google.android.syncadapters.contacts." +
                "SyncHighResPhotoIntentService";
    }

    @Override
    public String getViewContactNotifyServicePackageName() {
        return "com.google.android.syncadapters.contacts";
    }
}
