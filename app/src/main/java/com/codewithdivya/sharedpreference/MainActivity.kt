package com.codewithdivya.sharedpreference

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys.AES256_GCM_SPEC

class MainActivity : AppCompatActivity() {
    private lateinit var editor : SharedPreferences.Editor
    private lateinit var save : Button
    private lateinit var name : EditText
    val user = "User"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        name = findViewById(R.id.name)
        save = findViewById(R.id.save)
        val masterKeyAlias = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MasterKey.Builder(this)
                .setKeyGenParameterSpec(AES256_GCM_SPEC)
                .build()
        } else {
            MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }
        val sharedPreferences = EncryptedSharedPreferences.create(
            this.applicationContext,
            "ENCRYPTED_PREF_FILE_NAME",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        if(sharedPreferences.contains(user)){
            name.setText(sharedPreferences.getString(user,""))
        }

        save.setOnClickListener {

            var myName = name.getText().toString()
            editor = sharedPreferences.edit()
            editor.putString(user,myName).apply()

        }

        val keyAlias = "AndroidKeyStore"
        val keyGenSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias,
        KeyProperties.PURPOSE_ENCRYPT)
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setKeySize(256)
            .build()


    }
}