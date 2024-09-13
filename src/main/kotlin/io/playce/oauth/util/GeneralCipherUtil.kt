package io.playce.oauth.util

import io.playce.oauth.exception.ErrorCode
import io.playce.oauth.exception.PlayceOAuthException
import org.apache.commons.codec.binary.Base64
import org.springframework.http.HttpStatus
import java.io.DataInputStream
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

abstract class GeneralCipherUtil {
    companion object {

        fun encrypt(plainText: String): String {
            val cipher = Cipher.getInstance("RSA").also {
                it.init(Cipher.ENCRYPT_MODE, this.getPublicKey())
            }
            val bytes = cipher.doFinal(plainText.toByteArray())
            return Base64.encodeBase64String(bytes).also {
                it.replace("\\r|\\n", "")
            }
        }

        fun decrypt(cipherText: String): String {
            val cipher = Cipher.getInstance("RSA").also {
                it.init(Cipher.DECRYPT_MODE, this.getPrivateKey())
            }
            try {
                val bytes = cipher.doFinal(Base64.decodeBase64(cipherText))
                return String(bytes)
            } catch (e: Exception) {
                throw PlayceOAuthException(ErrorCode.INVALID_REQUEST_PARAM, HttpStatus.BAD_REQUEST, e)
            }
        }

        private fun getPublicKey(): PublicKey {
            val inputStream = GeneralCipherUtil::class.java.getResourceAsStream("/playce_general.pub")
                ?: throw PlayceOAuthException(ErrorCode.NO_EXIST_PUB, HttpStatus.INTERNAL_SERVER_ERROR)
            val keyBytes = DataInputStream(inputStream).readAllBytes().let {
                Base64.decodeBase64(it)
            }
            val keySpec = X509EncodedKeySpec(keyBytes)
            return KeyFactory.getInstance("RSA").generatePublic(keySpec)
        }

        private fun getPrivateKey(): PrivateKey {
            val inputStream = GeneralCipherUtil::class.java.getResourceAsStream("/playce_general.priv")
                ?: throw PlayceOAuthException(ErrorCode.NO_EXIST_PRIV, HttpStatus.INTERNAL_SERVER_ERROR)
            val keyBytes = DataInputStream(inputStream).readAllBytes().let {
                Base64.decodeBase64(it)
            }
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
        }
    }
}