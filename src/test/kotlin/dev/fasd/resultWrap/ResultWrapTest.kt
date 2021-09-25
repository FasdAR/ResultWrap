package dev.fasd.resultWrap

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.*
import org.junit.Test
import java.lang.IllegalStateException

class ResultWrapTest {
    //#region wrapResult
    /**
     * Если в блоке [wrapResult] не произошла ошибка, то он должен вернуть экземпляр
     * [ResultWrap.Success] с типом выражения, и содержать [ResultWrap.Success.value] необходимого типа
     */
    @Test
    fun wrapResultSuccess() {
        val res = wrapResult { 10 + 5 }

        assertThat(
            res,
            instanceOf(ResultWrap.Success::class.java)
        )

        if (res is ResultWrap.Success) {
            assertEquals(15, res.value)
        }
    }

    /**
     * Если в блоке [wrapResult] происходит ошибка, то он должен вернуть экземпляр
     * [ResultWrap.Error] и содержать [ResultWrap.Error.error] необходимого типа
     */
    @Test
    fun wrapResultError() {
        val res = wrapResult { throw IllegalStateException("Something error") }

        assertThat(
            res,
            instanceOf(ResultWrap.Error::class.java)
        )

        if (res is ResultWrap.Error) {
            assertThat(
                res.error,
                instanceOf(IllegalStateException::class.java)
            )
        }
    }
    //#endregion

    //#region map
    /**
     * Блок [ResultWrap.map] для типа [ResultWrap.Success] должен вернуть новый экземпляр
     * c новым значением в [ResultWrap.Success.value] с указанием необходимого обобщенного типа
     */
    @Test
    fun mapSuccess() {
        val res = ResultWrap.Success<Int>(1)
        val resMap: ResultWrap<Boolean> = res.map { it == 1 }

        assertThat(
            resMap,
            instanceOf(ResultWrap.Success::class.java)
        )

        if (resMap is ResultWrap.Success) {
            assertEquals(true, resMap.value)
        }
    }

    /**
     * Блок [ResultWrap.map] для типа [ResultWrap.Error] должен вернуть тот-же экзепляр с таким-же типом
     */
    @Test
    fun mapError() {
        val res = ResultWrap.Error(IllegalStateException("Somthing error"))
        val resMap: ResultWrap<Boolean> = res.map { true }

        assertThat(
            resMap,
            instanceOf(ResultWrap.Error::class.java)
        )

        if (resMap is ResultWrap.Error) {
            assertThat(
                resMap.error,
                instanceOf(IllegalStateException::class.java)
            )
        }
    }
    //#endregion

    //#region mapResult
    /**
     * Блок [ResultWrap.mapResult] для типа [ResultWrap.Success] должен вернуть новый экземпляр
     */
    @Test
    fun mapResultSuccess() {
        val res = ResultWrap.Success<Int>(1)
        val resMap = res.mapResult { ResultWrap.Success(it == 1) }

        assertThat(
            resMap,
            instanceOf(ResultWrap.Success::class.java)
        )

        if (resMap is ResultWrap.Success) {
            assertEquals(true, resMap.value)
        }
    }

    /**
     * Блок [ResultWrap.mapResult] для типа [ResultWrap.Error] должен вернуть тот-же экзмпляр ошибки
     */
    @Test
    fun mapResultError() {
        val res: ResultWrap<Int> = ResultWrap.Error(IllegalStateException("Something error"))
        val resMap = res.mapResult { ResultWrap.Success(it == 1) }

        assertThat(
            resMap,
            instanceOf(ResultWrap.Error::class.java)
        )

        if (resMap is ResultWrap.Error) {
            assertThat(
                resMap.error,
                instanceOf(IllegalStateException::class.java)
            )
        }
    }
    //#endregion

    //#region getValueOrNull
    /**
     * Метод [ResultWrap.getValueOrNull] для типа [ResultWrap.Success] должен вернуть его значение
     * из [ResultWrap.Success.value]
     */
    @Test
    fun getValueOrNullSuccess() {
        val res = ResultWrap.Success<Int>(10)
        val resValue = res.getValueOrNull()

        assertEquals(10, resValue)
    }

    /**
     * Метод [ResultWrap.getValueOrNull] для типа [ResultWrap.Error] должен вернуть null
     */
    @Test
    fun getValueOrNullError() {
        val res: ResultWrap<Int> = ResultWrap.Error(IllegalStateException("Error"))
        val resValue: Int? = res.getValueOrNull()

        assertEquals(null, resValue)
    }
    //#endregion

    //#region getValueOrThrow
    /**
     * Метод [ResultWrap.getValueOrThrow] для типа [ResultWrap.Success] должен вернуть его значение
     * из [ResultWrap.Success.value]
     */
    @Test
    fun getValueOrThrowSuccess() {
        val res = ResultWrap.Success<Int>(10)
        val resValue = res.getValueOrThrow()

        assertEquals(10, resValue)
    }

    /**
     * Метод [ResultWrap.getValueOrThrow] для типа [ResultWrap.Error] должен вызвать исключение
     */
    @Test(expected = IllegalStateException::class)
    fun getValueOrThrowError() {
        val res: ResultWrap<Int> = ResultWrap.Error(IllegalStateException("Error"))
        val resValue: Int = res.getValueOrThrow()
    }
    //#endregion

    //#region getValueOrHandle
    /**
     * Метод [ResultWrap.getValueOrHandle] для типа [ResultWrap.Success] должен вернуть его значение
     * из [ResultWrap.Success.value]
     */
    @Test
    fun getValueOrHandleSuccess() {
        val res = ResultWrap.Success<Int>(10)
        val resValue = res.getValueOrHandle {
            error("Error handling")
        }

        assertEquals(10, resValue)
    }

    /**
     * Метод [ResultWrap.getValueOrHandle] для типа [ResultWrap.Error] должен вызвать блок
     * для обработки исключения
     */
    @Test
    fun getValueOrHandleError() {
        val res: ResultWrap<Int> = ResultWrap.Error(IllegalStateException("Somthing error"))
        val resValue = res.getValueOrHandle {
            assertThat(
                it,
                instanceOf(IllegalStateException::class.java)
            )
        }
    }
    //#endregion
}