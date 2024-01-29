package org.example

@JvmInline
value class C(private val value: Double) : Comparable<C> {
    fun get() = this.value

    operator fun minus(other: C) = C(this.value - other.value)

    override operator fun compareTo(other: C) = this.value.compareTo(other.value)
}

@JvmInline
value class B(private val value: Double) : Comparable<B> {

    fun get() = this.value

    override operator fun compareTo(other: B) = this.value.compareTo(other.value)
}

@JvmInline
value class A(private val value: Double) : Comparable<A> {

    fun get() = this.value

    operator fun div(other: B): C {
        val valueB = other.get()
        val valueC = if (valueB > 0.0) this.value / valueB else 0.0
        return C(valueC)
    }

    override operator fun compareTo(other: A) = this.value.compareTo(other.value)
}

fun main() {
    val a = A(200.0)
    val b = B(100.0)
    val data: HashMap<String, C> = hashMapOf("C" to C(0.0))
    data.merge("C", a / b, C::minus)
    println(data)
}