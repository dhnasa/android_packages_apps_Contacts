/*
 * Copyright (C) 2015 The Android Open Source Project
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
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;

import com.android.contacts.R;
import com.android.contacts.model.dataitem.DataKind;
import com.android.contactsbind.FeedbackHelper;

import com.google.common.collect.Lists;

import java.util.Locale;

/**
 * A writable account type that can be used to support samsung contacts. This may not perfectly
 * match Samsung's latest intended account schema.
 *
 * This is only used to partially support Samsung accounts. The DataKind labels & fields are
 * setup to support the values used by Samsung. But, not everything in the Samsung account type is
 * supported. The Samsung account type includes a "Message Type" mimetype that we have no intention
 * of showing inside the Contact editor. Similarly, we don't handle the "Ringtone" mimetype here
 * since managing ringtones is handled in a different flow.
 */
public class SamsungAccountType extends BaseAccountType {
    private static final String TAG = "KnownExternalAccount";
    private static final String ACCOUNT_TYPE_SAMSUNG = "com.osp.app.signin";

    public SamsungAccountType(Context context, String authenticatorPackageName, String type) {
        this.accountType = type;
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
            addDataKindGroupMembership(context);
            addDataKindRelation(context);
            addDataKindEvent(context);

            mIsInitialized = true;
        } catch (DefinitionException e) {
            FeedbackHelper.sendFeedback(context, TAG, "Failed to build samsung account type", e);
        }
    }

    /**
     * Returns {@code TRUE} if this is samsung's account type and Samsung hasn't bothered to
     * define a contacts.xml to provide a more accurate definition than ours.
     */
    public static boolean isSamsungAccountType(Context context, String type,
            String packageName) {
        return ACCOUNT_TYPE_SAMSUNG.equals(type)
                && !ExternalAccountType.hasContactsXml(context, packageName);
    }

    @Override
    protected DataKind addDataKindStructuredPostal(Context context) throws DefinitionException {
        final DataKind kind = super.addDataKindStructuredPostal(context);

        final boolean useJapaneseOrder =
                Locale.JAPANESE.getLanguage().equals(Locale.getDefault().getLanguage());
        kind.typeColumn = StructuredPostal.TYPE;
        kind.typeList = Lists.newArrayList();
        kind.typeList.add(buildPostalType(StructuredPostal.TYPE_WORK).setSpecificMax(1));
        kind.typeList.add(buildPostalType(StructuredPostal.TYPE_HOME).setSpecificMax(1));
        kind.typeList.add(buildPostalType(StructuredPostal.TYPE_OTHER).setSpecificMax(1));

        kind.fieldList = Lists.newArrayList();
        if (useJapaneseOrder) {
            kind.fieldList.add(new EditField(StructuredPostal.COUNTRY,
                    R.string.postal_country, FLAGS_POSTAL).setOptional(true));
            kind.fieldList.add(new EditField(StructuredPostal.POSTCODE,
                    R.string.postal_postcode, FLAGS_POSTAL));
            kind.fieldList.add(new EditField(StructuredPostal.REGION,
                    R.string.postal_region, FLAGS_POSTAL));
            kind.fieldList.add(new EditField(StructuredPostal.CITY,
                    R.string.postal_city,FLAGS_POSTAL));
            kind.fieldList.add(new EditField(StructuredPostal.STREET,
                    R.string.postal_street, FLAGS_POSTAL));
        } else {
            kind.fieldList.add(new EditField(StructuredPostal.STREET,
                    R.string.postal_street, FLAGS_POSTAL));
            kind.fieldList.add(new EditField(StructuredPostal.CITY,
                    R.string.postal_city,FLAGS_POSTAL));
            kind.fieldList.add(new EditField(StructuredPostal.REGION,
                    R.string.postal_region, FLAGS_POSTAL));
            kind.fieldList.add(new EditField(StructuredPostal.POSTCODE,
                    R.string.postal_postcode, FLAGS_POSTAL));
            kind.fieldList.add(new EditField(StructuredPostal.COUNTRY,
                    R.string.postal_country, FLAGS_POSTAL).setOptional(true));
        }

        return kind;
    }

    @Override
    protected DataKind addDataKindPhone(Context context) throws DefinitionException {
        final DataKind kind = super.addDataKindPhone(context);

        kind.typeColumn = Phone.TYPE;
        kind.typeList = Lists.newArrayList();
        kind.typeList.add(buildPhoneType(Phone.TYPE_MOBILE));
        kind.typeList.add(buildPhoneType(Phone.TYPE_HOME));
        kind.typeList.add(buildPhoneType(Phone.TYPE_WORK));
        kind.typeList.add(buildPhoneType(Phone.TYPE_MAIN));
        kind.typeList.add(buildPhoneType(Phone.TYPE_FAX_WORK).setSecondary(true));
        kind.typeList.add(buildPhoneType(Phone.TYPE_FAX_HOME).setSecondary(true));
        kind.typeList.add(buildPhoneType(Phone.TYPE_PAGER).setSecondary(true));
        kind.typeList.add(buildPhoneType(Phone.TYPE_RADIO).setSecondary(true));
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
    protected DataKind addDataKindRelation(Context context) throws DefinitionException {
        final DataKind kind = super.addDataKindRelation(context);

        kind.weight = 160;

        return kind;
    }

    @Override
    protected DataKind addDataKindEvent(Context context) throws DefinitionException {
        final DataKind kind = super.addDataKindEvent(context);

        kind.weight = 150;

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
}
