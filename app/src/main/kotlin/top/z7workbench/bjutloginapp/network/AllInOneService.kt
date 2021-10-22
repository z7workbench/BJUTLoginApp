package top.z7workbench.bjutloginapp.network

import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.*
import top.z7workbench.bjutloginapp.Constants

interface AllInOneService {
    @GET(Constants.LGN_URL)
    suspend fun wiredSync(): String

    @FormUrlEncoded
    @POST(Constants.LGN_URL + Constants.LOGIN_TAIL)
    suspend fun wiredLogin(@Body body: RequestBody)

    @GET(Constants.LGN_URL + Constants.QUIT_TAIL)
    suspend fun wiredLogout()

    @GET(Constants.IPV6_URL)
    suspend fun wired6Sync(): String

    @FormUrlEncoded
    @POST(Constants.IPV6_URL + Constants.LOGIN_TAIL)
    suspend fun wired6Login(@Body body: RequestBody)

    @GET(Constants.IPV6_URL + Constants.QUIT_TAIL)
    suspend fun wired6Logout()

    @GET(Constants.WLGN_URL + Constants.WLGN_LOGIN_TAIL)
    suspend fun wirelessLogin(
        @Query("DDDDD") name: String,
        @Query("upass") pass: String,
        @Query("callback") callback: String = "dr1003"
    )

    @GET(Constants.WLGN_URL + Constants.WLGN_LOGOUT_TAIL)
    suspend fun wirelessLogout(@Query("callback") callback: String = "dr1002")

    companion object {
        val service by lazy {
            Retrofit.Builder()
                .client(NetworkGlobalObject.client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create<AllInOneService>()
        }
    }
}