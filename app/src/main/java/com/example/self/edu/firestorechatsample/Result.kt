package com.example.self.edu.firestorechatsample

/**
 *
 */
sealed class Result<T>() {

    companion object {
        fun<T> success(data: T): Success<T> = Success(data)
        fun<T> failure(error: Throwable) : Failure<T> = Failure(error)
    }

    class Success<T>(val data: T) : Result<T>()
    class Failure<T>(val error: Throwable) : Result<T>()

}