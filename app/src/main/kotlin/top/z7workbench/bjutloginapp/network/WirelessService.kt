package top.z7workbench.bjutloginapp.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query
import top.z7workbench.bjutloginapp.Constants

interface WirelessService {
    @GET(Constants.WLGN_LOGIN_TAIL)
    suspend fun login(
        @Query("DDDDD") name: String,
        @Query("upass") pass: String,
        @Query("callback") callback: String = "dr1003"
    )

    @GET(Constants.WLGN_LOGOUT_TAIL)
    suspend fun logout(@Query("callback") callback: String = "dr1002")

    companion object {
        val service by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.WLGN_URL)
                .client(NetworkGlobalObject.client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create<WirelessService>()
        }
    }
}