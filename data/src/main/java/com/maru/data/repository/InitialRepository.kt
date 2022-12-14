package com.maru.data.repository

import com.maru.data.datasource.initial.InitialDataSource
import com.maru.data.datasource.initial.InitialRemoteDataSource
import com.maru.data.model.CoupleInfo
import com.maru.data.model.User
import com.maru.data.network.SignUpRequest
import com.maru.data.network.SignUpResponse
import com.maru.data.network.Token
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InitialRepository @Inject constructor(
    private val userRemoteDataSource: InitialRemoteDataSource
) : InitialDataSource {

    override suspend fun registerNewUser(user: SignUpRequest): Result<SignUpResponse> =
        userRemoteDataSource.registerNewUser(user)

    override suspend fun signInUser(key: String): Result<Token> =
        userRemoteDataSource.signInUser(key)

    override suspend fun registerNewCouple(code: String, startDate: String): Result<Unit> =
        userRemoteDataSource.registerNewCouple(code, startDate)

    override suspend fun getMyCoupleData(): Result<CoupleInfo> =
        userRemoteDataSource.getMyCoupleData()

    override suspend fun getMyUserData(): Result<User> =
        userRemoteDataSource.getMyUserData()
}