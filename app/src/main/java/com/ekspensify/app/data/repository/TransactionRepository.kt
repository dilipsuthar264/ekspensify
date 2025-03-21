package com.ekspensify.app.data.repository

import android.content.Context
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ekspensify.app.data.ApiResponse
import com.ekspensify.app.data.BaseRepository
import com.ekspensify.app.data.ErrorResponseModel
import com.ekspensify.app.data.model.requestModel.TransactionQueryModel
import com.ekspensify.app.data.model.requestModel.TransactionRequestModel
import com.ekspensify.app.data.model.responseModel.AttachmentResponseModel
import com.ekspensify.app.data.model.responseModel.TransactionResponseModel
import com.ekspensify.app.data.pagingSource.TransactionPagingSource
import com.ekspensify.app.data.services.TransactionApi
import com.ekspensify.app.utils.getFileFromUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionApi: TransactionApi,
    @ApplicationContext private val context: Context
) : BaseRepository {

    suspend fun createTransaction(
        transactionRequestModel: TransactionRequestModel
    ): ApiResponse<TransactionResponseModel> {
        return handleResponse { transactionApi.createTransaction(transactionRequestModel) }
    }

    //user can update note and attachment
    suspend fun updateTransaction(
        transactionId: Int,
        transactionRequestModel: TransactionRequestModel
    ): ApiResponse<TransactionResponseModel> {
        return handleResponse {
            transactionApi.updateTransaction(
                transactionId,
                transactionRequestModel
            )
        }
    }

    suspend fun deleteTransaction(
        transactionId: Int
    ): ApiResponse<TransactionResponseModel> {
        return handleResponse { transactionApi.deleteTransaction(transactionId) }
    }

    suspend fun uploadAttachment(
        imageUri: Uri
    ): ApiResponse<AttachmentResponseModel> {
        val file = getFileFromUri(context, imageUri) ?: return ApiResponse.failure(
            ErrorResponseModel(
                message = "File not found"
            )
        )
        val image = MultipartBody.Part.createFormData(
            name = "attachment",
            filename = file.name,
            body = file.asRequestBody(
                contentType = "image/jpeg".toMediaTypeOrNull(),
            )
        )
        return handleResponse { transactionApi.uploadAttachment(image) }
    }


    fun getTransactions(
        transactionQueryModel: TransactionQueryModel
    ): Flow<PagingData<TransactionResponseModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = transactionQueryModel.limit,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                TransactionPagingSource(
                    transactionApi,
                    transactionQueryModel
                )
            }
        ).flow
            .catch { e ->
                emit(PagingData.empty())
            }
    }

}