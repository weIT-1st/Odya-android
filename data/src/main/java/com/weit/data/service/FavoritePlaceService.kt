package com.weit.data.service

import com.weit.data.model.ListResponse
import com.weit.data.model.favoritePlace.FavoritePlaceDTO
import com.weit.data.model.favoritePlace.FavoritePlaceRegistration
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FavoritePlaceService {

    @POST("/api/v1/favorite-places")
    suspend fun register(
        @Body favoritePlaceRegistration: FavoritePlaceRegistration,
    )

    @DELETE("/api/v1/favorite-places/{id}")
    suspend fun delete(
        @Path("id") id: Long,
    ) : Response<Unit>

    @DELETE("/api/v1/favorite-places/places/{placeId}")
    suspend fun deleteByPlaceId(
        @Path("placeId") placeId: String
    ) : Response<Unit>

    @GET("/api/v1/favorite-places/{placeId}")
    suspend fun isFavoritePlace(
        @Path("placeId") placeId: String,
    ): Boolean

    @GET("/api/v1/favorite-places/counts")
    suspend fun getFavoritePlaceCount(): Int

    @GET("/api/v1/favorite-places/counts/{userId}")
    suspend fun getFriendFavoritePlaceCount(
        @Path("userId") userId: Long,
     ): Int

    @GET("/api/v1/favorite-places/list")
    suspend fun getFavoritePlaces(
        @Query("size") size: Int?,
        @Query("sortType") sortType: String?,
        @Query("lastId") lastFavoritePlaceId: Long?,
    ): ListResponse<FavoritePlaceDTO>

    @GET("/api/v1/favorite-places/list/{userId}")
    suspend fun getFriendFavoritePlaces(
        @Path("userId") userId: Long,
        @Query("size") size: Int?,
        @Query("sortType") sortType: String?,
        @Query("lastId") lastFavoritePlaceId: Long?,
    ): ListResponse<FavoritePlaceDTO>
}
