package md.absa.makeup.topupmama.data.api.resource

data class NetworkResource<out T>(
    val status: Status,
    val data: T?,
    val message: String?
) {
    companion object {
        fun <T> success(data: T): NetworkResource<T> =
            NetworkResource(
                status = Status.SUCCESS,
                data = data,
                message = "Success"
            )

        fun <T> error(message: String?): NetworkResource<T> =
            NetworkResource(
                status = Status.ERROR,
                data = null,
                message = message
            )

        fun <T> loading(message: String): NetworkResource<T> =
            NetworkResource(
                status = Status.LOADING,
                data = null,
                message = message
            )
    }
}

enum class Status {
    SUCCESS,
    LOADING,
    ERROR
}
