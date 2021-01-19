package wee.digital.sample.data.local

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import wee.digital.sample.app.app
import java.nio.charset.Charset
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class SecurityHelper {

    private var pass = PASS_ALIAS.toCharArray()

    private val protectParam: KeyStore.ProtectionParameter = KeyStore.PasswordProtection(pass)

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").also {
        it.load(null)
    }

    private var encodeParam: AlgorithmParameters? = null

    private var rsaEncodeParam: AlgorithmParameters? = null

    private var currentSignature: ByteArray? = null

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SecurityHelper()
        }
        const val PASS_ALIAS = "WeeDigital@123"
        const val ALIAS_RSA = "WEESERRSA"
        const val ALIAS_AES = "WEESERAES"
        const val ALIAS_SIGN = "WEESERSIGN"
        const val AES_TRANSFORMATION = "AES/CBC/PKCS7PADDING"
        const val RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
        const val SIGN_ALGORITHM = "SHA512withECDSA"
        const val PASS_KEYSTORE = "WeeDigital@123"
        const val FIRST_NAME = "FACEPAY"
        const val UNIT_NAME = "WEEDIGITAL"

    }

    fun base64Encode(str: ByteArray?): String {
        return Base64.encodeToString(str, Base64.DEFAULT)
    }

    fun base64Decode(strBase64: String): ByteArray {
        return Base64.decode(strBase64, Base64.DEFAULT)
    }

    fun removeKeyData() {
        removeKeyStore(ALIAS_RSA)
        /* removeKeyStore(ALIAS_AES)
         removeKeyStore(ALIAS_SIGN)*/
    }

    fun createNewKeyData() {
        removeKeyData()
        createKeyData()
    }

    fun createKeyData() {
        //createNewKeyStoreAES(ALIAS_AES)
        createNewKeyStoreRSA(ALIAS_RSA)
        //createNewKeyStoreSign(ALIAS_SIGN)
    }

    fun getKeyStore(): KeyStore {
        return keyStore
    }

    fun getRSAPriKey(ali: String): ByteArray? {
        try {
            createNewKeyStoreRSA(ali).apply {
                val priKey = keyStore.getEntry(ali, null) as KeyStore.PrivateKeyEntry
                return priKey.privateKey.encoded
            }
        } catch (e: Exception) {
            Log.d("getRSAPriKey", "${e.message}")
            return null
        }

    }

    fun getRSAPubKey(ali: String): ByteArray? {
        try {
            createNewKeyStoreRSA(ali).apply {
                val priKey = keyStore.getEntry(ali, null) as KeyStore.PrivateKeyEntry
                return priKey.certificate.publicKey.encoded
            }
        } catch (e: Exception) {
            Log.d("getRSAPubKey", "${e.message}")
            return null
        }
    }

    fun getAESSecKey(): SecretKey {
        createNewKeyStoreAES(ALIAS_AES)
        val keyEntry = keyStore.getEntry(ALIAS_AES, protectParam) as KeyStore.SecretKeyEntry
        return keyEntry.secretKey
    }

    private fun removeKeyStore(alias: String) {
        if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias)
        }
    }

    @Throws(Exception::class)
    fun aesEncrypt(plaintext: ByteArray?): ByteArray? {
        plaintext ?: return null
        createNewKeyStoreAES(ALIAS_AES)
        val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
        val keyEntry = keyStore.getEntry(ALIAS_AES, protectParam) as KeyStore.SecretKeyEntry
        val key = keyEntry.secretKey
        val iv = ByteArray(16)
        val random = SecureRandom()
        random.nextBytes(iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, random)
        encodeParam = cipher.parameters
        // App.baseSharedPref!!.saveStringValue("ENCODE_PARAMS", base64Encode(encodeParam!!.encoded))
        return cipher.doFinal(plaintext)
    }

    fun aesDecrypt(cipherText: ByteArray?): ByteArray? {
        try {
            cipherText ?: return null
            createNewKeyStoreAES(ALIAS_AES)
            val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
            val keyEntry = keyStore.getEntry(ALIAS_AES, protectParam) as KeyStore.SecretKeyEntry
            val key = keyEntry.secretKey
            val iv = ByteArray(16)
            val random = SecureRandom()
            random.nextBytes(iv)
            return if (encodeParam == null) {
                encodeParam = AlgorithmParameters.getInstance(KeyProperties.KEY_ALGORITHM_AES)
                /* encodeParam!!.init(
                       *//*  base64Decode(
                              *//**//*  App.baseSharedPref!!.getStringValue(
                                        "ENCODE_PARAMS",
                                        ""
                                )*//**//*
                        )*//*
                )*/
                cipher.init(Cipher.DECRYPT_MODE, key, encodeParam, random)
                cipher.doFinal(cipherText)
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key, encodeParam, random)
                cipher.doFinal(cipherText)
            }

        } catch (e: java.lang.Exception) {
            Log.d("Security", "${e.message}")
            e.printStackTrace()
        }
        return null
    }

    fun rsaDecrypt(base64String: String): ByteArray? {
        return if (base64String.isNotEmpty()) {
            try {
                createNewKeyStoreRSA(ALIAS_AES)
                val privateKeyEntry =
                        keyStore.getEntry(ALIAS_AES, null) as KeyStore.PrivateKeyEntry
                val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
                val iv = ByteArray(16)
                val random = SecureRandom()
                random.nextBytes(iv)
                cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey, random)
                val data = base64Decode(base64String)
                Log.d("rsaDecrypt", "Decrypt data size: ${data.size}")
                cipher.doFinal(data)
            } catch (e: Throwable) {
                Log.d("rsaDecrypt", "${e.message}")
                null
            }
        } else {
            null
        }

    }

    fun rsaEncrypt(alias: String, input: ByteArray): String {
        return try {
            createNewKeyStoreRSA(alias)
            val privateKeyEntry =
                    keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
            val publicKey = privateKeyEntry.certificate.publicKey
            val inCipher = Cipher.getInstance(RSA_TRANSFORMATION)
            val iv = ByteArray(16)
            val random = SecureRandom()
            random.nextBytes(iv)
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey, random)
            val inputEncrypt = inCipher.doFinal(input)
            base64Encode(inputEncrypt)
        } catch (e: Exception) {
            Log.d("rsaEncrypt", "${e.message}")
            "null"
        }
    }

    fun signMessage(message: String): String {
        createNewKeyStoreSign(ALIAS_SIGN)
        val entry: KeyStore.Entry = keyStore.getEntry(ALIAS_SIGN, protectParam)
        if (entry !is KeyStore.PrivateKeyEntry) {
            Log.d("Security", "Not an instance of a PrivateKeyEntry")
            return ""
        }
        currentSignature = Signature.getInstance(SIGN_ALGORITHM).run {
            val iv = ByteArray(16)
            val random = SecureRandom()
            random.nextBytes(iv)
            initSign(entry.privateKey, random)
            update(message.toByteArray(Charset.forName("UTF-8")))
            sign()
        }
        return base64Encode(currentSignature)
    }

    fun verifySignMessage(message: String, signature: String): Boolean {
        createNewKeyStoreSign(ALIAS_SIGN)
        val entry = keyStore.getEntry(ALIAS_SIGN, protectParam) as? KeyStore.PrivateKeyEntry
        if (entry == null) {
            Log.d("Security", "Not an instance of a PrivateKeyEntry")
            return false
        }
        val sign = Signature.getInstance(SIGN_ALGORITHM)
        sign.initVerify(entry.certificate.publicKey)
        sign.update(message.toByteArray(Charset.forName("UTF-8")))
        return sign.verify(base64Decode(signature))
    }

    fun createNewKeyStoreRSA(alias: String) {

        val keyPairGenerator =
                KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
        keyPairGenerator.initialize(
                KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                        .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                        .setKeySize(4096)
                        .build()
        )

        keyPairGenerator.generateKeyPair()

        if (keyStore.aliases().hasMoreElements())
            Log.d("Security", "createNewKeyStoreRSA" + keyStore.aliases().nextElement())
    }

    private fun createNewKeyStoreAES(alias: String) {
        if (!keyStore.containsAlias(alias)) {
            val spec =
                    KeyGenParameterSpec.Builder(
                            alias,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    ).apply {
                        setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        setDigests(KeyProperties.DIGEST_SHA512)
                        /*setUserAuthenticationRequired(true)
                        setUserAuthenticationValidityDurationSeconds(15)*/
                        setRandomizedEncryptionRequired(true)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && hasStrongBox(app)) {
                            Log.d("Security", "addStrongBoxBacked")
                            setIsStrongBoxBacked(true)
                            setUnlockedDeviceRequired(true)
                        }
                        setKeySize(256)

                    }.build()
            val generator: KeyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
            )
            generator.init(spec)
            generator.generateKey()
            Log.d("Security", "createNewKeyStoreAES")
        }
    }

    private fun createNewKeyStoreSign(alias: String) {
        if (!keyStore.containsAlias(alias)) {
            val spec = KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            ).apply {
                setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                setKeySize(521)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && hasStrongBox(app)) {
                    Log.d("Security", "addStrongBoxBacked")
                    setIsStrongBoxBacked(true)
                }
                setRandomizedEncryptionRequired(true)
            }.build()

            val generator: KeyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore"
            )
            generator.initialize(spec)
            generator.generateKeyPair()
            Log.d("Security", "createNewKeyStoreSign")
        }
    }

    private fun isInSecureHardware(key: KeyStore.SecretKeyEntry, alias: String): Boolean {
        try {
            val factory: KeyFactory =
                    KeyFactory.getInstance(key.secretKey.algorithm, alias)
            val keyInfo: KeyInfo = factory.getKeySpec(key.secretKey, KeyInfo::class.java)
            return keyInfo.isInsideSecureHardware
        } catch (e: GeneralSecurityException) {
            Log.d(
                    "Security",
                    "Could not determine if private key is in secure hardware or not"
            )
        }
        return false
    }

    private fun hasStrongBox(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager
                    .hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
        } else {
            false
        }
    }

}