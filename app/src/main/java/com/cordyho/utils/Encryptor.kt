package com.cordyho.utils

import android.util.Base64
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Encryptor {

    /* private const val key = "gBZ6vvkOWzWotHW2"

     private const val initVector = "6vlk9VCSha9CBMjy"*/

    fun encrypt(content: Any, initVector: String, key: String): String? {   //加密
        try {
            val iv = IvParameterSpec(initVector.toByteArray(StandardCharsets.UTF_8))
            val skeySpec = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "AES")
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            val encrypted: ByteArray? = cipher.doFinal(content.toString().toByteArray())
            return Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    fun decrypt(content: Any?, initVector: String, key: String): String? {  //解密
        try {
            val iv = IvParameterSpec(initVector.toByteArray(StandardCharsets.UTF_8))
            val skeySpec = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "AES")
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
            val original: ByteArray? = cipher.doFinal(Base64.decode(content?.toString(), Base64.NO_WRAP))
            return String(original!!)
        } catch (ex: Exception) {
        }
        return null
    }
}