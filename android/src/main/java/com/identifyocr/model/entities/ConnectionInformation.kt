package com.identifyocr.model.entities

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ConnectionInformation(
        val baseApiUrl: String,
        val baseSocketUrl: String,
        val socketPort: String?,
        val stunUrl: String,
        val stunPort: String,
        val turnUrl: String,
        val turnPort: String,
        val username: String,
        val password: String
) {


    companion object{
        fun getKimlikBasitData() : ConnectionInformation{
            return ConnectionInformation(
                    "https://api.kimlikbasit.com",
                    "wss://ws.kimlikbasit.com",
                    "8888",
                    "stun:stun.l.google.com",
                    "19302",
                    "turn:18.156.205.32",
                    "3478",
                    "test",
                    "test"
            )
        }

        fun getIdentifyTrLiveData() : ConnectionInformation{
            return ConnectionInformation(
                "https://api.identify.com.tr",
                "wss://ws.identify.com.tr",
                "443",
                "stun:stun.l.google.com",
                "19302",
                "turn:185.32.14.165",
                "3478",
                "test",
                "test"
            )
        }

        fun getIdentifyTrV2Data() : ConnectionInformation{
            return ConnectionInformation(
                "https://v2api.identify.com.tr",
                "wss://v2ws.identify.com.tr",
                "443",
                "stun:stun.l.google.com",
                "19302",
                "turn:185.32.14.165",
                "3478",
                "test",
                "test"
            )
        }

        fun getIdentifyTrDevData() : ConnectionInformation{
            return ConnectionInformation(
                "https://apidev.identify.com.tr",
                "wss://wsdev.identify.com.tr",
                "443",
                "stun:stun.l.google.com",
                "19302",
                "turn:185.32.14.165",
                "3478",
                "test",
                "test"
            )
        }

        fun getIdentifyTrTestData() : ConnectionInformation{
            return ConnectionInformation(
                "https://apitest.identify.com.tr",
                "wss://wstest.identify.com.tr",
                "443",
                "stun:stun.l.google.com",
                "19302",
                "turn:185.32.14.165",
                "3478",
                "test",
                "test"
            )
        }

        fun getDogusData() : ConnectionInformation{
            return ConnectionInformation(
                "https://api-identity-test.vdf.com.tr",
                "wss://ws-identity-test.vdf.com.tr",
                "443",
                "stun:stun.l.google.com",
                "19302",
                "turn:10.112.118.59",
                "3478",
                "test",
                "test"
            )
        }

        fun getStableXData() : ConnectionInformation{
            return ConnectionInformation(
                "https://tidapi.stablex.net",
                "wss://tidws.stablex.net",
                "443",
                "stun.l.google.com",
                "19302",
                "turn:3.64.99.127",
                "3478",
                "test",
                "test"
            )
        }

        fun getOzanData() : ConnectionInformation{
            return ConnectionInformation(
                "https://identify-sandbox-api.ozan.com",
                "wss://identify-sandbox-ws.ozan.com",
                null,
                "stun.l.google.com",
                "19302",
                "",
                "",
                "",
                ""

            )
        }
    }

}
