package dev.fasd.resultWrap

sealed class ResultWrap<out T> {
    class Success<T>(val value: T) : ResultWrap<T>()
    class Error(val error: Throwable) : ResultWrap<Nothing>()
}

/**
 * Метод для обертки кода результата
 * @param wrapBlock блок исполняемого кода, его результат будет обернут в [ResultWrap]
 *
 * @return результат исполнения блока [wrapBlock] в обертке [ResultWrap]
 */
inline fun <T> wrapResult(wrapBlock: () -> T): ResultWrap<T> {
    return try {
        ResultWrap.Success(wrapBlock())
    } catch (ex: Exception) {
        ResultWrap.Error(ex)
    }
}

/**
 * Метод для маппинга значения
 * Для типа [ResultWrap.Error] вернет тот-же экземпляр ошибки,
 * для [ResultWrap.Success] вернет новый экземпляр с новым значением
 *
 * @param mapBlock блок маппинга, его результат будет обертнут в новый экземпляр [ResultWrap]
 *
 * @return результат маппинга
 */
inline fun <T, R> ResultWrap<T>.map(mapBlock: (value: T) -> R): ResultWrap<R> {
    return when (this) {
        is ResultWrap.Success -> ResultWrap.Success(mapBlock(value))
        is ResultWrap.Error -> this
    }
}

/**
 * Метод для маппинга результата
 * Для типа [ResultWrap.Error] вернет тот-же экземпляр ошибки,
 * для [ResultWrap.Success] вернет новый экземпляр из результат исполнения [mapBlock]
 *
 * @param mapBlock блок маппинга, его результат будет передан как реузльтат исполнения
 *
 * @return результат маппинга
 */
inline fun <T, R> ResultWrap<T>.mapResult(mapBlock: (value: T) -> ResultWrap<R>): ResultWrap<R> {
    return when (this) {
        is ResultWrap.Success -> mapBlock(value)
        is ResultWrap.Error -> this
    }
}

/**
 * Метод для получения значения
 *
 * Для типа [ResultWrap.Success] вернет значение из [ResultWrap.Success.value]
 * или null для [ResultWrap.Error]
 *
 * @return значение или null
 */
fun <T> ResultWrap<T>.getValueOrNull(): T? {
    return when (this) {
        is ResultWrap.Success -> value
        is ResultWrap.Error -> null
    }
}

/**
 * Метод для получения значения
 *
 * Для типа [ResultWrap.Success] Вернет значение из [ResultWrap.Success.value],
 * для типа [ResultWrap.Error] произойдет вызов исключениня
 *
 * @return значение
 */
fun <T> ResultWrap<T>.getValueOrThrow(): T {
    return when (this) {
        is ResultWrap.Success -> value
        is ResultWrap.Error -> throw error
    }
}

/**
 * Метод для получения значения
 *
 * Для типа [ResultWrap.Success] Вернет значение из [ResultWrap.Success.value],
 * для типа [ResultWrap.Error] будет вызван блок [handleError] в котором можно обработать ошибку,
 * и в качестве результата вернет null
 *
 * @return значение или null
 */
inline fun <T> ResultWrap<T>.getValueOrHandle(handleError: (error: Throwable) -> Unit): T? {
    if (this is ResultWrap.Error) {
        handleError(error)
    }
    return getValueOrNull()
}